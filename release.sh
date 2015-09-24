#!/usr/bin/env bash

set -e

mvn scm:check-local-modification

# test
mvn clean test -P jdk7
mvn clean test -P jdk8

# release
mvn versions:set
git add pom.xml
git commit
mvn clean deploy -P release,jdk8
mvn scm:tag

# next development version
mvn versions:set
git add pom.xml
git commit
