# Changelog   
All notable changes to the Make It Native mobile app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Breaking

-   We updated react-native-vision-camera from v4.7.3 to v5.2.3 and added react-native-vision-camera-barcode-scanner v5.2.3 and react-native-nitro-image v0.15.2. We also updated react-native-nitro-modules to v0.37.0. react-native-vision-camera-barcode-scanner is only used on Android; on iOS the Barcode Scanner widget uses Apple's built-in scanner from react-native-vision-camera.
-   Because of this update, the Barcode Scanner widget requires version 4.6.0 or above (Native Mobile Resources version 12.6.0 or above). Older versions of the widget will not work in this version of Make It Native.

### Fixes

- We have fixed the textinputs hiding behind on-screen keyboard.
- We have fixed the Android splash screen being stretched. The Mendix logo is now centered and displayed with the correct proportions on all screen sizes.
- We migrated iOS to the UIKit scene delegate lifecycle.
- We have fixed the Android back button closing the app instead of navigating back within the Mendix app.

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

## [3.1.8] Make it Native - 2025-4-02

### Fixes

-   We have fixed the sample apps issues, and enabled them in the showcase section.
