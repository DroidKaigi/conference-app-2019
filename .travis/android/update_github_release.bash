#!/usr/bin/env bash

set -eu

create_release_draft() {
    curl -X POST \
        -H "Authorization: token $GITHUB_ACCESS_TOKEN" \
        -d "$(jq -n \
          --arg tag_name "$RELEASE_TAG_NAME" \
          --arg target_commitish "$(git rev-parse HEAD)" \
          --arg name "$RELEASE_TAG_NAME" \
          --arg body "$(cat .travis/android/release_template)" \
          '{ "draft": true, "name": $name, "tag_name": $tag_name, "target_commitish": $target_commitish, "body": $body }')" \
        "https://api.github.com/repos/$TRAVIS_REPO_SLUG/releases"
}

update_release_draft() {
    local -r release_id="$1"

    curl -X POST \
        -H "Authorization: token $GITHUB_ACCESS_TOKEN" \
        -d "$(jq -n \
          --arg target_commitish "$(git rev-parse --short HEAD)" \
          --arg body "$(cat .travis/android/release_template)" \
          '{ "target_commitish": $target_commitish, "body": $body }')" \
        "https://api.github.com/repos/$TRAVIS_REPO_SLUG/releases/$release_id"
}

release_count() {
    curl -X GET \
        -H "Authorization: token $GITHUB_ACCESS_TOKEN" \
        "https://api.github.com/repos/$TRAVIS_REPO_SLUG/releases" | \
        jq -r '.[] | select(.draft) | length'
}

get_release_id() {
    curl -X GET \
        -H "Authorization: token $GITHUB_ACCESS_TOKEN" \
        "https://api.github.com/repos/$TRAVIS_REPO_SLUG/releases" | \
        jq -r --arg tag_name "$RELEASE_TAG_NAME" '.[] | select(.tag_name == $tag_name and .draft) | .id // -1'
}

ruby -rerb -e 'puts ERB.new(File.read(".travis/android/release_template.travis.erb")).result(binding)' > .travis/android/release_template

release_id="$(get_release_id)"

if let "${release_id:--1} > 0"; then
    update_release_draft "$release_id"
else
    create_release_draft
fi