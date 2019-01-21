# How to build

Initially, you need to `carthage bootstrap` and `pod install`.

```
cd frontend/ios
carthage bootstrap --platform ios --cache-builds
pod install
```

If it succeeds you can open `DroidKaigi 2019.xcworkspace` in Xcode.
