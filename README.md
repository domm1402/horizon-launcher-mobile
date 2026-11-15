# RealmForge
Independent Android Bedrock-client research project. No Cinnabar source is reused.

## Build
GitHub Actions → **Build RealmForge APK** → run workflow → download `RealmForge-debug-apk`.

## Status
This is a navigable Android UI shell, **not a Minecraft/Bedrock game client**. It cannot join servers or Realms. No Minecraft credentials are collected.

## Engineering backlog (not implemented)
- Microsoft OAuth device-code flow, secure token storage, Xbox Live/XSTS and Minecraft Services identity chain
- Bedrock entitlement verification before multiplayer
- Bedrock protocol version negotiation, RakNet reliability, packet encryption/compression and session lifecycle
- Realms API discovery/invites/join
- NetherNet peer transport, WebRTC ICE, TURN alternate-server handling and relay fallback
- Voxel renderer, Vulkan capability detection/fallback, chunk meshing/streaming and frame pacing
- Touch controls, inventory/UI, audio
- Local world persistence, resource packs and settings
- Integration tests against owned accounts and test servers

These systems require implementation and validation. This repository does not claim Realm compatibility yet.