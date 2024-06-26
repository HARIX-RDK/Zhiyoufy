#!/bin/bash

function log_info ()
{
        DATE_N=`date "+%Y-%m-%d %H:%M:%S"`
        LOG_MSG="${DATE_N} $@"
        echo "${LOG_MSG}"
}

log_info "zhiyoufygo Container Begin"

if [[ "${ENABLE_DEBUG}" == "true" ]]; then
    touch /tmp/debug
fi

logdir=$MY_POD_NAME
if [[ $logdir == "" ]]; then
    logdir=logs
fi

rm -rf /app/logs
mkdir /app/logs
mkdir -p /output/logs/$logdir
ln -s /output/logs/$logdir /app/shared_logs

cd /output/logs
ls -t | sed -e '1,3d' | xargs -d '\n' rm -rf

if [ -d "/app/configs_override" ]; then
    cp -L /app/configs_override/* /app/configs/
fi

cd /app

/app/bin/sync_log.sh &

/app/zhiyoufygo worker

while true
do
    if [ -f /tmp/debug ]; then
        sleep 5
    else
        break
    fi
done

log_info "zhiyoufygo Container End"
