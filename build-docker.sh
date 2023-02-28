#!/bin/bash

NAME=sys-golf-etl
VERSION=latest

minikube image rm $NAME:$VERSION
docker build --no-cache . -t $NAME
minikube image load $NAME:$VERSION
# minikube cache reload
