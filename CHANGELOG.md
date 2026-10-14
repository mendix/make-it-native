# Changelog   
All notable changes to the Make It Native mobile app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixes

- We fixed an issue on iOS where a deep link that cold-started the app was not delivered to React Native, causing `Linking.getInitialURL()` to return `null`.
- We have fixed the textinputs hiding behind on-screen keyboard.
- We have fixed the Android splash screen being stretched. The Mendix logo is now centered and displayed with the correct proportions on all screen sizes.

### Changes

-   Migrated from react-native-push-notification to @notifee/react-native for better new architecture compatibility and enhanced push notification features
-   Removed `@react-native-masked-view/masked-view` dependency.
-   We migrated from react-native-file-viewer to react-native-file-viewer-turbo for new architecture compatibility
-   File viewer now uses modal to display content
-   We migrated from react-native-biometrics to @sbaiahmed1/react-native-biometrics for new architecture compatibility
-   We have removed react-native-system-navigation-bar dependency. Navigation bar visibility is now handled by the react-native-video package.
-   We migrated from react-native-fast-image to @d11/react-native-fast-image for new architecture compatibility.
-   Upgrade react-native-reanimated to v3.16.7.
-   We migrated from `react-native-splash-screen` to `react-native-bootsplash@6.3.10` for better splash screen support and autolinking compatibility.
-   Replaced @notifee/react-native with react-native-notify-kit library.
-   We updated react-native-vision-camera from v4.7.3 to v5.2.3 and added react-native-vision-camera-barcode-scanner v5.2.3 and react-native-nitro-image v0.15.2. We also updated react-native-nitro-modules to v0.37.0.
-   We updated react-native-firebase from v20.1.0 to v21.14.0 (Firebase iOS SDK v11.11.0) to resolve a CocoaPods dependency conflict with the barcode scanner's GoogleMLKit v9.0.0 on iOS.
-   We updated the Android Firebase BoM to v33.12.0 and removed the Firebase BoM override, so react-native-firebase uses its own BoM version.

## [3.1.8] Make it Native - 2025-4-02

### Fixes

-   We have fixed the sample apps issues, and enabled them in the showcase section.
