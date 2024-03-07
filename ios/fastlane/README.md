fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## iOS

### ios install_dev_certificate

```sh
[bundle exec] fastlane ios install_dev_certificate
```



### ios internal

```sh
[bundle exec] fastlane ios internal
```

Build an internal version for automation

It always uses v.0.0.0 and build number 0 by design

### ios debug_internal

```sh
[bundle exec] fastlane ios debug_internal
```

Builds and install DeveloperApp into the simulator

This will use iPhone 13 Pro simulator with OS 15.5

### ios beta

```sh
[bundle exec] fastlane ios beta
```

Submit a new Beta Build to Apple TestFlight

This will also make sure the profile is up to date

### ios beta_manually

```sh
[bundle exec] fastlane ios beta_manually
```



### ios promote_to_production

```sh
[bundle exec] fastlane ios promote_to_production
```

Send appstore version to review

### ios build_mendixnative

```sh
[bundle exec] fastlane ios build_mendixnative
```

Build a new version of MendixNative lib

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
