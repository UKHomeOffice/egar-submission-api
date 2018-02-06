#!/bin/sh
NAME="${1}"
version=$(./scripts/bash/get-site-version.sh)

docker build -t $NAME:$version .
