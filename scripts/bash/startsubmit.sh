#!/bin/sh
echo Starting Submission-API version: $SUBMISSION_API_VER
rm -rf /home/centos/egar-submission-api/scripts/kube/submission-api-deployment.yaml; envsubst < "/home/centos/egar-submission-api/scripts/kube/submission-api-deployment-template.yaml" > "/home/centos/egar-submission-api/scripts/kube/submission-api-deployment.yaml";
kubectl create -f /home/centos/egar-submission-api/scripts/kube/submission-api-deployment.yaml
kubectl create -f /home/centos/egar-submission-api/scripts/kube/submission-api-service.yaml
