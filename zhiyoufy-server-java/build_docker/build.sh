#!/bin/bash
private_register_name=$REGISTRY_HUB

BASEDIR=$(dirname "$0")

docker_image_name=zhiyoufy

docker build -t $private_register_name/$docker_image_name:$1 --build-arg REGISTRY_HUB=$REGISTRY_HUB -f ${BASEDIR}/Dockerfile .

docker push $private_register_name/$docker_image_name:$1
