# $name$

$project-description$

---
## Contents
 - [About](#about)
 - [Running Locally](#running-locally)
 - [Running Directly From Github](#running-from-github)
 - [Contributing Guide](/docs/CONTRIBUTING.md)

## About

## Running Locally

#### Building a local docker image

[sbt-native-packager](https://sbt-native-packager.readthedocs.io/en/latest/) plugin is available for creating docker builds. To create a
build locally run `sbt docker:publishLocal`. The build uses the `adoptopenjdk:11` version to minimize size, N.B. this is a minimal linux container
and does not include bash, instead using [ash](https://en.wikipedia.org/wiki/Almquist_shell).

#### Installing the application on local kubernetes cluster

[Helm](https://helm.sh/) is configured in this repo under `/helm` directory. To install onto your local kubernetes cluster (e.g. minikube), create 
the namespace for your project and run the below command, specifying your namespace and a port for the LoadBalancer service that is not used 
on your machine.

:warning: Please ensure you have created the namespace first

```
helm upgrade --install $repo-name$ .\helm\\$repo-name$\ --set service.port=<port> --set service.type=LoadBalancer -n <namespace> -f .\helm\\$repo-name$\dev-values.yaml
```
## Running From Github

This repo contains the helm chart repository that is updated via the `Create Release` Github action. To install the chart
without building locally.

:warning: Please ensure you have created the namespace first

1. Create an image pull secret so that the pod can pull the image container from Github  
   `kubectl create secret docker-registry regcred --docker-server=ghcr.io/flutter-global --docker-username=\${GITHUB_USERNAME} --docker-password=\$
   {GITHUB_TOKEN}  --docker-email=<email> -n <namespace> `
2. Add the repo to your helm repos  
   `helm repo add --username \${GITHUB_USERNAME} --password \${GITHUB_TOKEN} <repo name> https://raw.githubusercontent.
   com/Flutter-Global/$repo-name$/main/charts/`
3. Update the repo  
   `helm repo update`
4. Install with the command  
   `helm -n <namespace> upgrade --install <release name> <repo name>/$repo-name$ --set service.port=<port> --set image.pullPolicy=IfNotPresent --set service.type=LoadBalancer --set "imagePullSecrets[0].name=regcred"`

