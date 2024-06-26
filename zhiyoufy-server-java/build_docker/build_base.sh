#!/bin/bash
private_register_name=$REGISTRY_HUB

BASEDIR=$(dirname "$0")

docker_image_name=zhiyoufy-base

docker build -t $private_register_name/$docker_image_name:$1 -f ${BASEDIR}/Dockerfile.base .

docker push $private_register_name/$docker_image_name:$1
