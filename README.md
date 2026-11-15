# Horizon Bedrock for Android

Horizon Bedrock is the Android game client build for the Horizon project. The Android APK
contains the Cinnabar Bedrock client and its native runtime in one installable package.
You do not need to install a separate Cinnabar app.

## Android build

The `Android APK` GitHub Actions workflow checks out a pinned Cinnabar source revision,
applies Horizon's Android application ID and product branding, and runs Cinnabar's own
Android packaging script. The result is a single arm64-v8a APK containing the Rust/Bevy
client, Go Bedrock networking core, and Android native libraries.

To build it, open **Actions → Android APK → Run workflow**. Download the
`Horizon-Bedrock-Android-APK` artifact from the completed run. The build currently targets
64-bit ARM Android 8+ devices with Vulkan support.

The engine source is pinned to
[`cinnabar-mc/cinnabar` revision `08446aa3aec50c42497cf5b37cdaffbfcb29a185`](https://github.com/cinnabar-mc/cinnabar/tree/08446aa3aec50c42497cf5b37cdaffbfcb29a185).
This keeps builds reproducible and avoids downloading a second APK at runtime.

## Current scope and limitations

This first integration builds the Cinnabar Android client as the Horizon Bedrock app. It does
not yet merge the old Kotlin Horizon launcher screens into the in-game client. Microsoft/Xbox
sign-in and Bedrock server connectivity are provided by Cinnabar's existing implementation.
A separate, verified Minecraft ownership entitlement gate that limits unowned accounts to
singleplayer has **not** been implemented yet; do not treat the current APK as enforcing that
policy. Android support is experimental upstream, and compatibility/performance must be
tested on physical devices.

## Licensing

Cinnabar is distributed under the
[PolyForm Noncommercial 1.0.0 licence](https://polyformproject.org/licenses/noncommercial/1.0.0).
This build is intended for permitted noncommercial use only. Read Cinnabar's
[licence](https://github.com/cinnabar-mc/cinnabar/blob/08446aa3aec50c42497cf5b37cdaffbfcb29a185/LICENSE)
and [third-party notices](https://github.com/cinnabar-mc/cinnabar/blob/08446aa3aec50c42497cf5b37cdaffbfcb29a185/THIRD_PARTY_NOTICES.md)
before using or redistributing the APK. Minecraft is a trademark of Microsoft; Horizon and
Cinnabar are independent, unofficial projects and are not endorsed by Microsoft or Mojang.
