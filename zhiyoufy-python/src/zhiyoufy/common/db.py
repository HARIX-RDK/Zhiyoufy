import logging
from contextlib import contextmanager

import sqlalchemy as sa
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

Base = declarative_base()


class DbMgr:
    def __init__(self):
        self.tag = type(self).__name__
        self.log_prefix = self.tag

        self.engine = None
        self.session_maker = None

    def setup_engine(self, db_config):
        log_prefix = "%s setup_engine:" % self.tag

        db_path = db_config["db_path"]
        echo = db_config.get("echo", False)

        logging.info("%s Enter with db_path %s, echo %s" % (log_prefix, db_path, echo))

        engine = create_engine(db_path, echo=echo)
        self.engine = engine
        self.session_maker = sessionmaker(bind=self.engine)

        logging.info("%s Leave" % (log_prefix,))

    @contextmanager
    def session_scope(self, auto_commit=True):
        session = self.session_maker()
        try:
            yield session
            if auto_commit:
                session.commit()
            else:
                session.rollback()
        except:
            session.rollback()
            raise
        finally:
            session.close()

    def metadata_create_all(self):
        Base.metadata.create_all(self.engine)

    def is_version_match(self, version_num):
        from .models import Version
        try:
            with self.session_scope() as session:
                db_version = session.query(Version).first()
                if db_version and db_version.version == version_num:
                    return True
                return False
        except Exception:
            return False


def write_version_info(conn, version_table, version_value):
    """
    Inserts the version value in to the version table.

    Parameters
    ----------
    conn : sa.Connection
        The connection to use to execute the insert.
    version_table : sa.Table
        The version table of the asset database
    version_value : int
        The version to write in to the database

    """
    conn.execute(sa.insert(version_table, values={'version': version_value}))
