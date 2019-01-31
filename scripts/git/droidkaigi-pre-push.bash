#!/usr/bin/env bash

set -eu

if [[ "${HOOK_TEST:-false}" == "true" ]]; then
    set -x
fi

# This script is for all workers

exec 1>&2

if ./gradlew lintDebug; then
    echo 'AndroidLint-ed successfully'
elif [[ "${HOOK_SKIP_ANDROID_LINT:-false}" != "true" ]]; then
    echo 'lintDebug failed. Please fix issues before pushing.'
    echo 'You can still push these changes if HOOK_SKIP_ANDROID_LINT=true'
    exit 1
else
    echo 'Ignored lintDebug issues'
fi

if ./gradlew testDebugUnitTest; then
    echo 'Local tests finished successfully'
elif [[ "${HOOK_SKIP_UNIT_TEST:-false}" != "true" ]]; then
    echo 'testDebugUnitTest failed. Please fix issues before pushing.'
    echo 'You can still push these changes if HOOK_SKIP_UNIT_TEST=true'
    exit 1
else
    echo 'Ignored testDebugUnitTest failures'
fi

if ./gradlew ktlint; then
    echo 'Ktlint-ed successfully'
elif [[ "${HOOK_SKIP_KTLINT:-false}" != "true" ]]; then
    echo 'ktlint failed. Please fix issues before pushing.'
    echo 'You can still push these changes if HOOK_SKIP_KTLINT=true'
    exit 1
else
    echo 'Ignored ktlint issues'
fi