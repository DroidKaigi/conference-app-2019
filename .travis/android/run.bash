#!/usr/bin/env bash

set -eu

die() {
  echo "$*" 1>&2
  exit 1
}

source "$(dirname $0)/../bash.source"

source .release/bash.source

cp .release/google-services.json frontend/android/

use_release_keystore
 ./gradlew bundleRelease --offline
create_universal_apk_from_aab.bash $(find frontend/android/build/outputs -name "*.aab" | head -1)

dpg app upload --android \
  --app-owner droidkaigi \
  --app "$UNIVERSAL_APK_PATH" \
  --token "$DEPLOYGATE_API_TOKEN" \
  --message "Release build of $(git rev-parse --short HEAD) at $(date)" \
  --distribution-name "Production Build" > .dpg_response

export APP_VERSION_CODE=$(cat .dpg_response | jq -r ".results.version_code")
export APP_VERSION_NAME=$(cat .dpg_response | jq -r ".results.version_name")

if [[ -z "${TRAVIS_TAG:-}" ]] && [[ "${TRAVIS_BRANCH:-}" != "release" ]]; then
  echo "do not upload to github releases"
  exit 0
fi

if [[ -n "${TRAVIS_TAG:-}" ]]; then
  if [[ "$TRAVIS_TAG" != "v$APP_VERSION_NAME" ]]; then
    die "tag and version name are different! ($TRAVIS_TAG and $APP_VERSION_NAME)"
  fi

  export RELEASE_TAG_NAME="$TRAVIS_TAG"
else
  export RELEASE_TAG_NAME="release-draft"
fi

.travis/android/update_github_release.bash
transart -f .travis/android/to_github.transart.yml transfer