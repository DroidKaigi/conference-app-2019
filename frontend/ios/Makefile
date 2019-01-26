.PHONY: init

init:
	carthage bootstrap --platform ios --cache-builds
	pod install

post_checkout:
	carthage bootstrap --platform ios --cache-builds
	pod install
