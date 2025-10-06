plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.recipeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.recipeapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Add your Google Maps API key here
        manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyAngBD2Z_mqn4dB3qmwv-3igqr7mgb1fvY"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lint {
        abortOnError = false
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.common)
    testImplementation(libs.junit)
    // REMOVED: implementation(libs.places) - redundant with the explicit declaration below
    implementation(libs.play.services.maps) // Duplicated in original, kept for safety unless you intended to remove one
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // This is the dependency that failed to resolve, its declaration is fine here.
    implementation("com.google.android.libraries.places:places:3.5.0")

    // Add OkHttp for HTTP requests to new Places API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Add Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Add the Firebase Authentication dependency
    implementation("com.google.firebase:firebase-auth-ktx")

    // Add the Cloud Firestore dependency
    implementation("com.google.firebase:firebase-firestore-ktx")

}