from functools import wraps

from alembic.migration import MigrationContext
from alembic.operations import Operations
import sqlalchemy as sa
from toolz.curried import do, operator

from .db import write_version_info
from .models import Version


def alter_columns(op, name, *columns, **kwargs):
    """Alter columns from a table.

    Parameters
    ----------
    name : str
        The name of the table.
    *columns
        The new columns to have.
    selection_string : str, optional
        The string to use in the selection. If not provided, it will select all
        of the new columns from the old table.

    Notes
    -----
    The columns are passed explicitly because this should only be used in a
    downgrade where ``db_schema`` could change.
    """
    selection_string = kwargs.pop('selection_string', None)
    if kwargs:
        raise TypeError(
            'alter_columns received extra arguments: %r' % sorted(kwargs),
        )
    if selection_string is None:
        selection_string = ', '.join(column.name for column in columns)

    tmp_name = '_alter_columns_' + name
    op.rename_table(name, tmp_name)

    for column in columns:
        # Clear any indices that already exist on this table, otherwise we will
        # fail to create the table because the indices will already be present.
        # When we create the table below, the indices that we want to preserve
        # will just get recreated.
        for table in name, tmp_name:
            try:
                op.drop_index('ix_%s_%s' % (table, column.name))
            except sa.exc.OperationalError:
                pass

    op.create_table(name, *columns)
    op.execute(
        'insert into %s select %s from %s' % (
            name,
            selection_string,
            tmp_name,
        ),
    )
    op.drop_table(tmp_name)


def upgrade(engine, desired_version):
    """Upgrades the db at the given engine to the desired version.

    Parameters
    ----------
    engine : Engine
        An SQLAlchemy engine to the database.
    desired_version : int
        The desired resulting version for the database.
    """

    # Check the version of the db at the engine
    with engine.begin() as conn:
        metadata = sa.MetaData(conn)
        metadata.reflect()
        version_info_table = metadata.tables[Version.__tablename__]
        starting_version = sa.select((version_info_table.c.version,)).scalar()

        # Check for accidental downgrade
        if starting_version > desired_version:
            raise Exception("starting_version %s is greater than %s" % (
                starting_version, desired_version
            ))

        if starting_version == desired_version:
            return

        # Create alembic context
        ctx = MigrationContext.configure(conn)
        op = Operations(ctx)

        # Integer keys of upgrades to run
        # E.g.: [3, 4, 5, 6] would upgrade v2 to v6
        upgrade_keys = range(starting_version+1, desired_version+1)

        # Disable foreign keys until all upgrades are complete
        _pragma_foreign_keys(conn, False)

        # Execute the upgrades in order
        for upgrade_key in upgrade_keys:
            _upgrade_methods[upgrade_key](op, conn, version_info_table)

        # Re-enable foreign keys
        _pragma_foreign_keys(conn, True)


def _pragma_foreign_keys(connection, on):
    """Sets the PRAGMA foreign_keys state of the SQLite database. Disabling
    the pragma allows for batch modification of tables with foreign keys.

    Parameters
    ----------
    connection : Connection
        A SQLAlchemy connection to the db
    on : bool
        If true, PRAGMA foreign_keys will be set to ON. Otherwise, the PRAGMA
        foreign_keys will be set to OFF.
    """
    connection.execute("PRAGMA foreign_keys=%s" % ("ON" if on else "OFF"))


# This dict contains references to upgrade methods that can be applied to an
# db. The resulting db's version is the key.
# e.g. The method at key '3' is the upgrade method from v2 to v3
_upgrade_methods = {}


def upgrades(src):
    """Decorator for marking that a method is a upgrade to a version to the
    next version.

    Parameters
    ----------
    src : int
        The version this upgrades from.

    Returns
    -------
    decorator : callable[(callable) -> callable]
        The decorator to apply.
    """
    def _(f):
        destination = src + 1

        @do(operator.setitem(_upgrade_methods, destination))
        @wraps(f)
        def wrapper(op, conn, version_info_table):
            conn.execute(version_info_table.delete())  # clear the version
            f(op)
            write_version_info(conn, version_info_table, destination)

        return wrapper
    return _


# @upgrades(1)
# def _upgrade_v1(op):
#     op.drop_index('ix_%s_name_email' % User.__tablename__)
#
#     # Execute batch op to allow column modification in SQLite
#     with op.batch_alter_table(User.__tablename__) as batch_op:
#
#         # Rename 'displayname'
#         batch_op.alter_column(column_name='displayname',
#                               new_column_name='display_name')
#
#         # Delete 'hobby'
#         batch_op.drop_column('hobby')
#
#         batch_op.add_column(sa.Column("registered_on", sa.DateTime))
#
#     # Recreate indices after batch
#     op.create_index('ix_%s_name_email' % User.__tablename__,
#                     table_name=User.__tablename__,
#                     columns=['name', 'email'],
#                     unique=True)
