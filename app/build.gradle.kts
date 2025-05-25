plugins {
    alias(libs.plugins.android.application)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.questionnairebuilder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.questionnairebuilder"
        minSdk = 26
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.scenecore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.hdodenhof.circleimageview)
    implementation (libs.gson)

    // Firebase:
    implementation(platform(libs.firebase.bom)) // Import the Firebase BoM
    // When using the BoM, don't specify versions in Firebase dependencies
    // TODO: Add the dependencies for Firebase products you want to use
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.storage)
    implementation(libs.google.firebase.firestore)

    // Vertex AI:
    implementation(libs.firebase.vertexai)
    implementation(libs.guava)
    implementation(libs.reactive.streams)

    // analysis:
    implementation(libs.mpandroidchart)

}