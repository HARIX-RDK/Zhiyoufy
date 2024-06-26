#!/bin/bash
num=$(grep "errors similar to this. Turn up verbosity to see them."  /var/log/syslog |tail -1 | awk '{print $12}' |sed 's/"//g')
echo $num

while [ $num ]
do
   [ -d "/var/lib/kubelet/pods/${num}" ] && rm -rf /var/lib/kubelet/pods/${num}

  sleep 2s
  num=$(grep "errors similar to this. Turn up verbosity to see them."  /var/log/syslog |tail -1 | awk '{print $12}' |sed 's/"//g')
  [ -d "/var/lib/kubelet/pods/${num}" ] || num=

  echo "$num remaining"

done
