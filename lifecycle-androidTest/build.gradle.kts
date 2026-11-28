plugins {
    id("com.android.application")
}

val useTestOrchestrator = providers.gradleProperty("useTestOrchestrator").orNull == "true"

android {
    namespace = "com.wdiodemoapp.lifecycle.host"
    compileSdk = 37
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.wdiodemoapp.lifecycle.host"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        animationsDisabled = true
        if (useTestOrchestrator) {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }
}

dependencies {
    androidTestImplementation(project(":test-shared"))
    if (useTestOrchestrator) {
        androidTestUtil("androidx.test:orchestrator:1.5.1")
    }
}

tasks.matching { it.name == "connectedDebugAndroidTest" }.configureEach {
    dependsOn(":app:installDebug")
}
