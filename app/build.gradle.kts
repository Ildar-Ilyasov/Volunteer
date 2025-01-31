plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.volunteer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.volunteer"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation("com.yandex.android:maps.mobile:4.5.0-full")
    implementation ("com.google.firebase:firebase-auth:21.0.3")
    implementation ("com.google.firebase:firebase-core:21.1.0")
    implementation ("com.google.firebase:firebase-database:20.2.1")
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}