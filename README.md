# RealmForge Android

Independent Android Bedrock client engineering project. This is not yet a playable or Realm-compatible client.

## Implemented
- Android launcher UI
- Microsoft device-code OAuth, Xbox Live/XSTS, Minecraft Services profile and entitlement check (app ID must be registered)
- Local world metadata and directories (not chunk saves)
- WebRTC PeerConnection/DataChannel transport primitive

## Microsoft application ID
Register a public-client app in Microsoft Entra yourself. Client IDs cannot be invented or registered by this repository. Replace the placeholder in BedrockAccount.kt and enable consumer accounts/device-code flow. No Microsoft-owned app ID is bundled.

## NetherNet / Realms gap
Generic WebRTC is a transport primitive, not Minecraft NetherNet. Realm joining still requires authenticated NetherNet signaling, relay allocation/TURN credentials, SDP/candidate exchange, packet framing, Bedrock RakNet/session encryption and Realm discovery/join APIs. Public STUN is not a substitute for Minecraft relay service. No fake Join Realm success is shown.

## Remaining gameplay
Bedrock protocol/version negotiation; RakNet reliability/encryption; NetherNet signaling/relay; Realms APIs; voxel renderer/Vulkan; touch input; world generator/chunk serialization/resource packs; device testing.

GitHub Actions builds a debug APK artifact.
