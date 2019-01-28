#!/usr/bin/env bash

set -eu

BUNDLE_FILE="$1"

VERSION='0.8.0'

curl -sL# \
  -o bundletool.jar \
  "https://github.com/google/bundletool/releases/download/$VERSION/bundletool-all-$VERSION.jar"

java -jar bundletool.jar \
  build-apks \
  --bundle="$BUNDLE_FILE" \
  --output=temp.apks \
  --overwrite \
  --mode=universal \
  --ks="$KEY_STORE_FILE" \
  --ks-key-alias="$KEY_ALIAS" \
  --ks-pass="pass:$KEY_STORE_PASSWORD" \
  --key-pass="pass:$KEY_PASSWORD"

unzip temp.apks

if [[ "$PWD/universal.apk" != "$UNIVERSAL_APK_PATH" ]]; then
    cp universal.apk "$UNIVERSAL_APK_PATH"
fi

rm temp.apks