#!/bin/bash
private_register_name=$REGISTRY_HUB

docker_image_name=gradle-7-jdk11-maven

docker build -t $private_register_name/$docker_image_name:$1 -f Dockerfile .

docker push $private_register_name/$docker_image_name:$1
