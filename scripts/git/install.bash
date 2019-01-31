#!/usr/bin/env bash

set -eu

hookdir="$(git rev-parse --show-toplevel)/.git/hooks"

while read hookfile; do
    cd "$hookdir" || :
    name="$(basename $(echo $hookfile | sed -e 's/droidkaigi-//' -e 's/\.bash//'))"
    ln -s "../../$hookfile" "$name"
done < <(find $(dirname $0) -type f -name "droidkaigi-*")
