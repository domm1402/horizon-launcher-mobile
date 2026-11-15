# Cinnabar for Android

This repository builds the upstream Cinnabar Android client as a single installable APK.
The build does not rename or patch the upstream application: it keeps Cinnabar's own
application ID, product name, and Android packaging implementation.

## Build

The **Cinnabar Android APK** workflow checks out the pinned upstream source revision
`08446aa3aec50c42497cf5b37cdaffbfcb29a185`, installs the Android/Rust/Go toolchains,
and runs Cinnabar's own `packaging/android/build-apk.sh` script.

When the workflow completes successfully, download the
`Cinnabar-Android-APK` artifact from the GitHub Actions run. The APK targets
`arm64-v8a`, Android API 26 or later, and requires Vulkan support.

## Important limitations

Cinnabar's Android support is experimental. This repository has not independently
verified compatibility, gameplay, multiplayer, or performance on physical devices.
The generated APK is test-signed unless a maintainer configures a private signing
keystore. It is not a Play Store release.

Cinnabar is licensed under PolyForm Noncommercial 1.0.0. Use and redistribution must
comply with that licence and the upstream third-party notices. Minecraft is a
Microsoft/Mojang trademark; this project is unofficial and is not endorsed by them.

Upstream: https://github.com/cinnabar-mc/cinnabar
