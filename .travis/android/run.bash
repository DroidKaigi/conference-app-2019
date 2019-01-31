#!/usr/bin/env bash

set -eu

source "$(dirname $0)/../bash.source"

source .release/bash.source

cp .release/google-services.json frontend/android/

use_release_keystore
 ./gradlew bundleRelease --offline
create_universal_apk_from_aab.bash $(find frontend/android/build/outputs -name "*.aab" | head -1)

curl -sL "https://raw.githubusercontent.com/jmatsu/dpg/master/install.bash" | bash

./dpg app upload --android \
  --app-owner droidkaigi \
  --app "$UNIVERSAL_APK_PATH" \
  --token "$DEPLOYGATE_API_TOKEN" \
  --message "Release build of $(git rev-parse --short HEAD) at $(date)" \
  --distribution-name "via_travis"
