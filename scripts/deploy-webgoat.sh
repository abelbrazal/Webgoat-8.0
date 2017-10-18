#!/usr/bin/env bash

docker login -u $DOCKER_USER -p $DOCKER_PASS
export REPO=webgoat/webgoat-travis

echo "Travis tag: ${TRAVIS_TAG}"
if [ -z "${TRAVIS_TAG}" ]; then
  echo "Tag build";
else
  echo "Build ${BRANCH}";
fi

#docker login -u $DOCKER_USER -p $DOCKER_PASS
#- export REPO=webgoat/webgoat-travis
#- export TAG=`if [ "$TRAVIS_BRANCH" == "master" ]; then echo "latest"; else echo $TRAVIS_BRANCH ; fi`
#- docker build -f Dockerfile -t $REPO .
#- docker push $REPO