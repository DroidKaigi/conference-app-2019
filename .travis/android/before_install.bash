#!/usr/bin/env bash

set -eu


source "$(dirname $0)/../bash.source"

mkdir "$HOME/.bin"

curl -sL "https://raw.githubusercontent.com/jmatsu/dpg/master/install.bash" | bash
curl -sL "https://raw.githubusercontent.com/jmatsu/transart/master/install.bash" | bash

cp ./dpg "$HOME/.bin/"
cp ./transart "$HOME/.bin/"

unzip release.zip

./gradlew androidDependenciesExtra getDependencies | grep "Dependencies for" --line-buffered