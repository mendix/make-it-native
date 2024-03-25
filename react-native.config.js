module.exports = {
  dependencies: {
    'react-native-code-push': {
      platforms: {
        android: null, // disable Android platform, other platforms will still autolink if provided
        ios: null, // disable ios platform, CodePush is not necessary for the MendixNative library
      },
    },
    'react-native-video': {
      platforms: {
        android: {
          sourceDir: '../node_modules/react-native-video/android-exoplayer',
        },
      },
    },
  },
};
