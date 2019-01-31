#!/usr/bin/env bash

set -eu

# for now
./gradlew :frontend:ioscombined:packForXCode -PXCODE_CONFIGURATION=Debug