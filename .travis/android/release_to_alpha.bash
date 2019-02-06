#!/usr/bin/env bash

set -eu

die() {
  echo "$*" 1>&2
  exit 1
}

source "$(dirname $0)/../bash.source"

source .release/bash.source

./gradlew publishAlpha --info