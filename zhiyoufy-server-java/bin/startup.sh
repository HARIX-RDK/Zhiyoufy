#!/bin/bash

function log_info ()
{
        DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
        LOG_MSG="${DATE_N} $@"
        echo "${LOG_MSG}"
}

log_info "zhiyoufy Container Begin"

if [[ "${ENABLE_DEBUG}" == "true" ]]; then
    touch /tmp/debug
fi

logdir=$MY_POD_NAME
if [[ $logdir == "" ]]; then
    logdir=logs
fi

rm -rf /app/logs
mkdir -p /output/$logdir
ln -s /output/$logdir /app/logs

cd /app

JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*".*/\1\2/p;')

if [ "$JAVA_VER" -le 18 ]; then
  JVM_OPTS=" \
    -verbose:gc \
    -XX:+PrintGCDetails \
    -XX:-PrintGCTimeStamps \
    -XX:+HeapDumpOnOutOfMemoryError \
    -Xms4G \
    -Xmx4G \
    -Dfile.encoding=UTF-8"
else
  JVM_OPTS=" \
    -verbose:gc \
    -Xlog:gc* \
    -XX:+HeapDumpOnOutOfMemoryError \
    -Xms4G \
    -Xmx4G \
    -Dfile.encoding=UTF-8"
fi

java $JVM_OPTS org.springframework.boot.loader.JarLauncher

while true
do
    if [ -f /tmp/debug ]; then
        sleep 5
    else
        break
    fi
done

log_info "zhiyoufy Container End"
