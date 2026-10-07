# Changelog   
All notable changes to the Make It Native mobile app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixes

- We fixed an issue on iOS where a deep link that cold-started the app was not delivered to React Native, causing `Linking.getInitialURL()` to return `null`.
- We have fixed the textinputs hiding behind on-screen keyboard.
- We have fixed the Android splash screen being stretched. The Mendix logo is now centered and displayed with the correct proportions on all screen sizes.

### Changes

-   We upgraded React Native to 0.88.0-rc.3. On iOS the app now uses the scene-based lifecycle (`SceneDelegate`).
-   The app now uses Hermes V1 (HBC bytecode version 99). JavaScript bundles compiled with Hermes 0.16.0 (bytecode version 96) can no longer be loaded and must be compiled with hermes-compiler 260318099.0.4.
-   We upgraded @op-engineering/op-sqlite to 18.2.5, react-native-gesture-handler to 2.33.0, react-native-reanimated to 4.7.1, react-native-worklets to 0.13.0, react-native-screens to 4.28.0, react-native-nitro-modules to 0.36.2, react-native-blob-util to 0.24.11, react-native-safe-area-context to 5.8.1, and the @react-native-vector-icons/* family to 13.x (to match appdev/client, which uses @react-native-vector-icons/common 13.0.3).
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
