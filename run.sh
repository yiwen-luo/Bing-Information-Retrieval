#!/bin/bash

DIR=$(dirname $0)

java -classpath ${DIR}/out/artifacts/InfoRetrieval_jar/InfoRetrieval.jar Main $@
