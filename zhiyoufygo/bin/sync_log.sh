#!/bin/bash

while true
do
  rsync -avh --delete /app/logs/ /app/shared_logs/
  chmod 666 /app/shared_logs/*

  sleep 15
done
