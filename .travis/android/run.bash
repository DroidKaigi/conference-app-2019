#!/usr/bin/env bash

set -eu

if [[ -z "$REPOSITORY_ROOT" ]]; then
    export REPOSITORY_ROOT="$(git rev-parse --show-toplevel)"
fi

source "$REPOSITORY_ROOT/scripts/bash.source"

./gradlew assembleRelease --offline