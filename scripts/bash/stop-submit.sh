#!/bin/sh
kubectl delete -f /home/centos/egar-submission-api/scripts/kube/submission-api-deployment.yaml
kubectl delete -f /home/centos/egar-submission-api/scripts/kube/submission-api-service.yaml
