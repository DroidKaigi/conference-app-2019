#!/usr/bin/env bash

set -eu


source "$(dirname $0)/../bash.source"

unzip release.zip

./gradlew androidDependenciesExtra getDependencies | grep "Dependencies for" --line-buffered