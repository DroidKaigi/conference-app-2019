#!/usr/bin/env bash

set -eu

mkdir -p "$ANDROID_HOME/licenses"

echo '8933bad161af4178b1185d1a37fbf41ea5269c55' > "$ANDROID_HOME/licenses/android-sdk-license"
echo 'd56f5187479451eabf01fb78af6dfcb131a6481e' >> "$ANDROID_HOME/licenses/android-sdk-license"
echo '24333f8a63b6825ea9c5514f83c2829b004d1fee' >> "$ANDROID_HOME/licenses/android-sdk-license"
echo '84831b9409646a918e30573bab4c9c91346d8abd' > "$ANDROID_HOME/licenses/android-sdk-preview-license"
echo '601085b94cd77f0b54ff86406957099ebe79c4d6' > "$ANDROID_HOME/licenses/android-googletv-license"
echo '33b6a2b64607f11b759f320ef9dff4ae5c47d97a' > "$ANDROID_HOME/licenses/google-gdk-license"
echo 'd975f751698a77b662f1254ddbeed3901e976f5a' > "$ANDROID_HOME/licenses/intel-android-extra-license"
echo 'e9acab5b5fbb560a72cfaecce8946896ff6aab9d' > "$ANDROID_HOME/licenses/mips-android-sysimage-license"