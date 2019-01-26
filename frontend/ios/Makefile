.PHONY: init

init:
	bundle install
	carthage bootstrap --platform ios --cache-builds
	bundle exec pod install --repo-update

post_checkout:
	bundle install
	carthage bootstrap --platform ios --cache-builds
	bundle exec pod install
