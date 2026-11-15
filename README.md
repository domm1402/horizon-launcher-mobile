# RealmForge Android

This repository has been reset to an independent Android project (no Cinnabar source).

## Build APK
Push to `main` or use Actions → **Build RealmForge APK** → Run workflow. Download artifact `RealmForge-debug-apk`.

## Current state
The APK is an installable Android shell only. It is **not a playable Minecraft client**. Rendering, Bedrock protocol, Microsoft/Xbox authentication, entitlement checks, Realms APIs, and NetherNet/WebRTC relay are not implemented. This repository does not claim Realm compatibility yet.

## Next technical milestones
1. Define clean-room client architecture and legal protocol implementation scope.
2. Add Microsoft device-code authentication and ownership verification.
3. Implement Bedrock packet/session layer and reliable encryption.
4. Implement Realms discovery and NetherNet/WebRTC TURN relaying.
5. Add Vulkan renderer, chunk pipeline, touch controls, local world storage.
6. Test with owned Bedrock accounts and Realms.
