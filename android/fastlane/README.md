fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android local

```sh
[bundle exec] fastlane android local
```

Build apk

### android debug_internal

```sh
[bundle exec] fastlane android debug_internal
```

Build apk and install in the simulator

### android internal

```sh
[bundle exec] fastlane android internal
```

Build new bundle and upload to Internal track

### android promote_to_alpha

```sh
[bundle exec] fastlane android promote_to_alpha
```

Promote last version of Internal track to Alpha

### android promote_to_production

```sh
[bundle exec] fastlane android promote_to_production
```

Promote the last version of Alpha track to Production (draft)

### android build_mendix_native

```sh
[bundle exec] fastlane android build_mendix_native
```

Build Mendix Native library

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
