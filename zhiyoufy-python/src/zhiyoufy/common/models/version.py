from sqlalchemy import Column, Integer

from zhiyoufy.common.db import Base as _Base


class Version(_Base):
    __tablename__ = 'version_info'

    version = Column(Integer, primary_key=True)
