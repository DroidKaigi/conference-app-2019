#!/usr/bin/env bash

set -eu

if [[ -z "${TRAVIS_TAG:-}" ]]; then
  exit 0
fi

die() {
  echo "$*" 1>&2
  exit 1
}

source "$(dirname $0)/../bash.source"

source .release/bash.source

if [[ ! -d ".transart" ]]; then
  transart -f .travis/android/to_github.transart.yml download
fi

./gradlew publishAlpha