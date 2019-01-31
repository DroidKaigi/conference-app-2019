#!/usr/bin/env bash

set -eu

source "$(dirname $0)/bash.source"

./gradlew assembleRelease --offline