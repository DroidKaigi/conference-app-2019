#!/usr/bin/env bash

set -eu

# for now
./gradlew :frontend:ios-combined:packForXCode -PXCODE_CONFIGURATION=Debug