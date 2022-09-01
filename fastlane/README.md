fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
### publish_to_firebase
```
fastlane publish_to_firebase
```
Publish Riwal Build to Firebase
### publish_manlift_to_firebase
```
fastlane publish_manlift_to_firebase
```
Publish Manlift Build to Firebase

----

## Android
### android test
```
fastlane android test
```
Runs all important tests
### android deploy_riwal_rental_to_crashlytics
```
fastlane android deploy_riwal_rental_to_crashlytics
```
Submit a new Beta Build for Riwal to Crashlytics Beta
### android deploy_riwal_rental_to_play_store
```
fastlane android deploy_riwal_rental_to_play_store
```
Deploy a new version of Riwal Rental to the Google Play
### android deploy_manlift_rental_to_crashlytics
```
fastlane android deploy_manlift_rental_to_crashlytics
```
Submit a new Beta Build for Manlift to Crashlytics Beta
### android deploy_manlift_rental_to_play_store
```
fastlane android deploy_manlift_rental_to_play_store
```
Deploy a new version for Manlift to the Google Play

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
