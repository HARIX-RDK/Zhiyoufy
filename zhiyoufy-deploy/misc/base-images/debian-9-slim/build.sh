#!/bin/bash
private_register_name=$REGISTRY_HUB
if [[ $private_register_name == "" ]]; then
    private_register_name="zhiyoufy"
fi

docker_image_name=debian-9-slim

docker build -t $private_register_name/$docker_image_name:$1 -f Dockerfile .

docker push $private_register_name/$docker_image_name:$1
