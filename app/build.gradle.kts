plugins {
    id("com.android.application")
}

val useTestOrchestrator = providers.gradleProperty("useTestOrchestrator").orNull == "true"

android {
    namespace = "com.wdiodemoapp"
    compileSdk = 37
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.wdiodemoapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        if (useTestOrchestrator) {
            testInstrumentationRunnerArguments["clearPackageData"] = "true"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        animationsDisabled = true
        if (useTestOrchestrator) {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.test.espresso:espresso-idling-resource:3.7.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(project(":test-shared"))
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-web:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.7.0")

    if (useTestOrchestrator) {
        androidTestUtil("androidx.test:orchestrator:1.5.1")
    }
}
