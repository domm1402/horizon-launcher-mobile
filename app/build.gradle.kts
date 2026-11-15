plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
 namespace = "gg.realmforge.client"
 compileSdk = 35
 defaultConfig { applicationId = "gg.realmforge.client"; minSdk = 26; targetSdk = 35; versionCode = 2; versionName = "0.2.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
dependencies { implementation("io.github.webrtc-sdk:android:125.6422.07") }
