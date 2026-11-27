plugins {
    id("com.android.library")
}

android {
    namespace = "com.wdiodemoapp.testshared"
    compileSdk = 37
    buildToolsVersion = "36.0.0"

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api("junit:junit:4.13.2")
    api("androidx.test.ext:junit:1.3.0")
    api("androidx.test:runner:1.7.0")
    api("androidx.test:rules:1.7.0")
    api("androidx.test.uiautomator:uiautomator:2.3.0")
}
