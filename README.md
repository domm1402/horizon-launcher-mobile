# RealmForge Android
Android Kotlin prototype for an independent Bedrock client.

Microsoft device-code sign-in uses the publicly documented Minecraft Bedrock Android relying-party client ID 0000000048183522. It is a platform client identifier, not a RealmForge-owned Entra app. Microsoft/Mojang can still reject third-party API access; this is not an approval bypass.

RelayPeer implements NetherNet's standard reliable/unreliable data channel labels and one-byte fragmentation/reassembly. Realms still requires authenticated Xbox signaling, Realm APIs/invite resolution, signed multiplayer identity assertions, Bedrock Login/session crypto and an actual game engine. This transport foundation alone cannot join Realms.
