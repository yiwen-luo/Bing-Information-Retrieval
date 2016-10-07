#!/bin/bash

DIR=$(dirname $0)

java -classpath ${DIR}/out/artifacts/Project1_jar/Project1.jar Main $@
