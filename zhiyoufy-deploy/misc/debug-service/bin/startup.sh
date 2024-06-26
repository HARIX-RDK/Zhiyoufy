#!/bin/bash
logdir=$MY_POD_NAME
if [[ $logdir == "" ]]; then
    logdir=logs
fi

mkdir -p /opt/logs/debug-service/$logdir
ln -s /opt/logs/debug-service/$logdir /app/logs

touch /tmp/debug

while true
do
    if [ -f /tmp/debug ]; then
        sleep 30
    else
        break
    fi
done

print "debug-service Container End"
