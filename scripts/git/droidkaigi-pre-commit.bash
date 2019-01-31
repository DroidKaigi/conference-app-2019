#!/usr/bin/env bash

set -eu

if [[ "${HOOK_TEST:-false}" == "true" ]]; then
    set -x
fi

## Don't verify HEAD because this commit is not an initial commit surely.

exec 1>&2

# prevent commit release google-services.json
if [[ "${HOOK_SKIP_GOOGLE_SERVICE_JSON:-false}" != "true" ]]; then
    if git diff --cached --name-only --diff-filter=M HEAD | grep "google-services.json"; then
        if grep "io.github.droidkaigi.confsched2019" "frontend/android/google-services.json"; then
            echo 'Be careful! google-services.json seems to be a release version.'
        else
            echo 'Be careful! google-services.json seems to be modified.'
        fi

        echo 'You can still commit if HOOK_SKIP_GOOGLE_SERVICE_JSON=true'
        exit 1
    fi
else
    echo 'Ignored to check google-services.json'
fi

if [[ "${HOOK_SKIP_CODE_STYLE_FILE:-false}" != "true" ]]; then
    if git diff --cached --name-only --diff-filter=MD HEAD | grep ".idea/codeStyles/Project.xml"; then
        echo '.idea/codeStyles/Project.xml must be modified or deleted. You can still commit if HOOK_SKIP_CODE_STYLE_FILE=true'
        exit 1
    fi
else
    echo 'Ignored to check .idea/codeStyles/Project.xml'
fi