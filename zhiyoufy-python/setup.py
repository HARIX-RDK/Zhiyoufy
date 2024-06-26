from setuptools import setup, find_packages
import os.path


here = os.path.abspath(os.path.dirname(__file__))


def read(rel_path, encoding="utf-8"):
    with open(os.path.join(here, rel_path), 'r', encoding=encoding) as fp:
        return fp.read()


def get_version(rel_path):
    for line in read(rel_path).splitlines():
        if line.startswith('__version__'):
            delim = '"' if '"' in line else "'"
            return line.split(delim)[1]
    else:
        raise RuntimeError("Unable to find version string.")


long_description = read("README.md")

requires = [
    "alembic",
    "croniter",
    "flask",
    "jinja2",
    "marshmallow",
    "pandas",
    "psutil",
    "pyhocon",
    "requests",
    "robotframework",
    "sqlalchemy",
    "toolz",
    "websocket_client"
]

setup(
    name="zhiyoufy",
    version=get_version("src/zhiyoufy/__init__.py"),
    description="Zhiyoufy python worker base",
    long_description=long_description,
    long_description_content_type="text/markdown",
    classifiers=[
        "Development Status :: 3 - Alpha",
        "Intended Audience :: Developers",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3 :: Only",
    ],
    package_dir={"": "src"},
    packages=find_packages(where="src"),
    python_requires=">=3.8, <4",
    install_requires=requires,
)
