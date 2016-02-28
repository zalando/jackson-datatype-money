#!/usr/bin/env bash

set -e

mvn scm:check-local-modification

# test using current jdk
# for realistic tests the jdk has to be switched in addition to the profile
# to avoid dependencies on rt.jar
mvn clean test

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
