.PHONY: init

init:
	carthage bootstrap --platform ios --cache-builds
	pod install
	# Change path if android-sdk is in different place.
	echo "sdk.dir = ${HOME}/Library/Android/sdk" > ../../local.properties 

post_checkout:
	carthage bootstrap --platform ios --cache-builds
	pod install
