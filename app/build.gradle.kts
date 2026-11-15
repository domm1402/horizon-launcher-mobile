plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "gg.realmforge.client"
    compileSdk = 35
    defaultConfig { applicationId = "gg.realmforge.client"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.1.0" }
}
