#!/bin/bash
private_register_name=$REGISTRY_HUB
if [[ $private_register_name == "" ]]; then
    private_register_name="example.com/zhiyoufy"
fi

BASEDIR=$(dirname "$0")

docker_image_name=zhiyoufygo-base

docker build -t $private_register_name/$docker_image_name:$1 -f ${BASEDIR}/Dockerfile.base .

echo docker push $private_register_name/$docker_image_name:$1
#docker push $private_register_name/$docker_image_name:$1
