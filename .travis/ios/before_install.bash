#!/usr/bin/env bash

set -eu

# prepare android sdk

if [[ ! -d "$ANDROID_HOME" ]]; then
    pushd "$ANDROID_HOME"
    curl -s# 'https://dl.google.com/android/repository/sdk-tools-darwin-4333796.zip' > android-sdk.zip
    unzip android-sdk.zip
    rm android-sdk.zip
    popd
fi

brew tap caskroom/versions
brew cask install java8

# switch jdk to 1.8 anyway
cp ./gradlew gradlew.origin

cat<<'EOF' > ./gradlew
#!/usr/bin/env bash

export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
./gradlew.origin "$@"
EOF

chmod +x ./gradlew

pushd frontend/ios

curl https://cocoapods-specs.circleci.com/fetch-cocoapods-repo-from-s3.sh | bash -s cf

bundle install
carthage bootstrap --platform ios --cache-builds
bundle exec pod install

popd