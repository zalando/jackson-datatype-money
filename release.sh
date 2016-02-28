#!/usr/bin/env bash

set -e

mvn scm:check-local-modification

# test
mvn dependency:tree clean test -Pjdk7,-jdk8
mvn dependency:tree clean test -Pjdk8,-jdk7

# release
mvn versions:set
git add pom.xml
git commit
mvn clean deploy -P release
mvn scm:tag

# next development version
mvn versions:set
git add pom.xml
git commit
