plugins {
    id("com.android.application")
}

val useTestOrchestrator = providers.gradleProperty("useTestOrchestrator").orNull == "true"

val syncLifecycleTestSources = tasks.register<Sync>("syncLifecycleTestSources") {
    from("../app/src/androidTest/java") {
        include("com/wdiodemoapp/tests/lifecycle/ProcessLifecycleTests.kt")
    }
    into(layout.buildDirectory.dir("generated/lifecycleTestSrc"))
}

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

    sourceSets {
        getByName("androidTest") {
            kotlin.directories.add(
                layout.buildDirectory.dir("generated/lifecycleTestSrc").get().asFile.path,
            )
        }
    }
}

dependencies {
    androidTestImplementation(project(":test-shared"))
    if (useTestOrchestrator) {
        androidTestUtil("androidx.test:orchestrator:1.5.1")
    }
}

tasks.matching { it.name.contains("AndroidTestKotlin") }.configureEach {
    dependsOn(syncLifecycleTestSources)
}

tasks.matching { it.name == "connectedDebugAndroidTest" }.configureEach {
    dependsOn(":app:installDebug")
}
