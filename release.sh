#!/usr/bin/env bash

set -e

mvn scm:check-local-modification

# test
mvn clean test -P ri
mvn clean test -P bp

# release
mvn versions:set
git add pom.xml
git commit
mvn clean deploy -P release,ri
mvn scm:tag

# next development version
mvn versions:set
git add pom.xml
git commit
