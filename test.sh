#!/bin/sh

set -e

rollback() {
    jenv local 1.7
}

trap rollback EXIT INT TERM   

# test
jenv local 1.7
./mvnw dependency:tree clean test

jenv local 1.8
./mvnw dependency:tree clean test

jenv local 1.8
./mvnw dependency:tree clean test -P jdk7,-jdk8
