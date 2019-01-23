# How to build

※ You can `make init` in **frontend/ios** to run following actions automatically.

Initially, you need to `carthage bootstrap` and `pod install`.

```
cd frontend/ios
carthage bootstrap --platform ios --cache-builds
pod install
```

If it succeeds you can open `DroidKaigi 2019.xcworkspace` in Xcode.

※ If you have an error with following log when building, 
  you need to create **local.properties** in repository root and set `sdk.dir`.
  `sdk.dir` is a path where **Android SDK** contains.

```
> Configure project :model
Unpack Kotlin/Native compiler (version 1.0.3)...

FAILURE: Build failed with an exception.

* What went wrong:
A problem occurred configuring project ':model'.
> SDK location not found. Define location with sdk.dir in the local.properties file or with an ANDROID_HOME environment variable.

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org
```
