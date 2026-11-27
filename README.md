# WdioNativeDemoApp (`com.wdiodemoapp`)

Native Kotlin Android demo application built with Android Views + ViewBinding to match the controls, accessibility IDs (`contentDescription`), `resource-id` conventions, dialogs, deep links, WebView, biometrics, and gesture interactions required by the Appium/WebdriverIO Android test suite and ready for Android Espresso testing.

---

## App Overview

| Property | Value |
| :--- | :--- |
| **Package / Application ID** | `com.wdiodemoapp` |
| **Launcher Activity** | `com.wdiodemoapp.MainActivity` (`launchMode="singleTop"`, portrait-locked) |
| **Version** | `1.0.0` (`versionCode = 1`) |
| **SDK Levels** | `minSdk = 26`, `targetSdk = 36`, `compileSdk = 37`, `buildToolsVersion = "36.0.0"` |
| **Build System** | Gradle `9.6.0`, Android Gradle Plugin `9.5.2` (built-in Kotlin), Java `17` target |
| **UI Toolkit** | Android Views XML + ViewBinding (`AppCompat`, `MaterialComponents`, `ViewPager2`, `BiometricPrompt`, `WebView`) |
| **Deep Link Scheme** | `wdio://` |

---

## Project Structure

```text
android-espresso-uiautomator-demo/
├── build.gradle.kts
├── settings.gradle.kts                                   # Multi-module build (:app, :test-shared, :lifecycle-androidTest)
├── gradle.properties
├── local.properties
├── gradlew / gradlew.bat
├── test-shared/                                          # Shared UIAutomator & JUnit core module
│   ├── build.gradle.kts
│   └── src/main/java/com/wdiodemoapp/testshared/
│       ├── AccessibilityIds.kt                           # Central locator contract across all modules
│       ├── TestConfig.kt                                 # Timeouts, package ID, deep-link scheme, credentials
│       ├── TestAnnotations.kt                            # @SmokeTest, @RegressionTest, @AccessibilityTest, @LifecycleTest, @WebViewTest
│       ├── SystemActions.kt                              # UIAutomator Home/Recents, deep links, ps/am kill, am force-stop
│       ├── FailureArtifactsRule.kt                       # Screenshot + hierarchy XML + logcat capture on failure
│       └── FontScaleRule.kt                              # System font_scale override + restore rule
├── lifecycle-androidTest/                                # External host APK for OS process kill & force-stop tests
│   ├── build.gradle.kts                                  # Compiles only ProcessLifecycleTests.kt from :app/src/androidTest
│   └── src/main/AndroidManifest.xml                      # Self-hosted package (com.wdiodemoapp.lifecycle.host) with <queries>
└── app/
    ├── build.gradle.kts                                  # Excludes ProcessLifecycleTests.kt from :app androidTest runner
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   └── java/com/wdiodemoapp/
        │       ├── MainActivity.kt                       # Single-activity 6-tab host, window insets, wdio:// deep links
        │       ├── IdlingHooks.kt                        # Espresso CountingIdlingResources (WebView + ViewPager2)
        │       └── ui/
        │           ├── ViewInsetsHelper.kt               # One-time ~1/3 top vertical offset helper
        │           ├── home/HomeFragment.kt
        │           ├── webview/WebViewFragment.kt
        │           ├── login/LoginFragment.kt            # Login / Sign Up form, AlertDialog, BiometricPrompt
        │           ├── forms/FormsFragment.kt            # Text input, switch, ListView dropdown, active/inactive buttons
        │           ├── swipe/SwipeFragment.kt            # ViewPager2 6-card carousel + bottom reveal badge
        │           └── drag/DragFragment.kt              # 3x3 touch-drag puzzle + renew / button-Retry
        └── androidTest/java/com/wdiodemoapp/
            ├── framework/
            │   ├── core/                                 # IdlingResourceRule, EspressoActions, GestureActions, CustomMatchers
            │   ├── rules/AppHybridTestRule.kt            # ActivityScenario + SystemActions + FailureArtifactsRule
            │   └── screens/                              # Screen Robots (Navigation, Alert, Home, Login, Forms, Swipe, Drag, WebView)
            └── tests/                                    # All 11 test suites in one place (25 tests total)
                ├── navigation/ (TabBarNavigationTests, DeepLinkNavigationTests)
                ├── auth/ (LoginAndSignupTests)
                ├── forms/ (FormsInteractionTests)
                ├── swipe/ (SwipeCarouselTests)
                ├── drag/ (DragAndDropTests)
                ├── webview/ (WebViewTests)
                ├── accessibility/ (HomeAccessibilityTests, FormsFontScaleTests)
                └── lifecycle/ (WarmResumeTests, ProcessLifecycleTests [hosted by :lifecycle-androidTest])
```

---

## Navigation & Deep Links

### Bottom Tab Bar
[MainActivity.kt](app/src/main/java/com/wdiodemoapp/MainActivity.kt) hosts all 6 tab fragments inside `@id/fragment_container` using `show`/`hide` transactions (preserving in-memory tab state such as loaded WebView pages and active form drafts across tab switches). The custom 6-tab bar in [activity_main.xml](app/src/main/res/layout/activity_main.xml) avoids Material `BottomNavigationView`'s 5-item cap and exposes exact `contentDescription` attributes:

| Tab | View ID | `contentDescription` / Appium `~` | Espresso Matcher |
| :--- | :--- | :--- | :--- |
| Home | `R.id.nav_home` | `Home` | `withContentDescription("Home")` |
| Webview | `R.id.nav_webview` | `Webview` | `withContentDescription("Webview")` |
| Login | `R.id.nav_login` | `Login` | `withContentDescription("Login")` |
| Forms | `R.id.nav_forms` | `Forms` | `withContentDescription("Forms")` |
| Swipe | `R.id.nav_swipe` | `Swipe` | `withContentDescription("Swipe")` |
| Drag | `R.id.nav_drag` | `Drag` | `withContentDescription("Drag")` |

### Deep Links (`wdio://`)
`MainActivity` handles `ACTION_VIEW` intents in both `onCreate` and `onNewIntent`:

| URI | Destination Tab |
| :--- | :--- |
| `wdio://` or `wdio://home` | `Home` |
| `wdio://webview` | `Webview` |
| `wdio://login` | `Login` |
| `wdio://forms` | `Forms` |
| `wdio://swipe` | `Swipe` |
| `wdio://drag` | `Drag` |

---

## Window Insets, Top Offset & Branding

- **System Bars & Camera Cutout Protection:** `MainActivity` registers `ViewCompat.setOnApplyWindowInsetsListener` for `WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()`, applying padding to the root layout.
- **~1/3 Top Offset for Top Controls:**
  - [fragment_webview.xml](app/src/main/res/layout/fragment_webview.xml) uses a horizontal `Guideline` (`layout_constraintGuide_percent="0.33"`) below a branding header so the WebView search bar stays ~1/3 down from the top and clear of the camera area.
  - [ViewInsetsHelper.kt](app/src/main/java/com/wdiodemoapp/ui/ViewInsetsHelper.kt) (`applyTopThirdOffset`) applies a one-time `height / 3` top padding on initial layout (preventing jumps when the soft keyboard resizes the window) in `FormsFragment`, `LoginFragment`, `SwipeFragment`, and `DragFragment` (capped via `reservedContentHeightDp = 580` on Drag so all 9 slots and 9 pieces remain visible without scrolling).
- **Branding:** Visual badges on Home, Swipe carousel cards, WebView header, and the Swipe reveal card display **"Quy Pham"** ([bg_brand_badge.xml](app/src/main/res/drawable/bg_brand_badge.xml)). The Swipe bottom badge retains `android:contentDescription="WebdriverIO logo"` so automated locators remain intact.

---

## Screen & Control Reference (Espresso & Appium Locators)

### 1. Home (`HomeFragment`)
- **Screen container:** `R.id.home_screen` / `contentDescription="Home-screen"`
- Displays the **Quy Pham** badge, `WEBDRIVERIO` heading, and summary text centered vertically.

### 2. WebView (`WebViewFragment`)
- **Screen container:** `R.id.webview_screen`
- **WebView widget:** `android.webkit.WebView` (`R.id.webview`) loading `https://webdriver.io/` with `javaScriptEnabled = true`, `domStorageEnabled = true`, `WebView.setWebContentsDebuggingEnabled(true)`, and an in-app `WebViewClient` so navigation stays inside the app.

### 3. Login / Sign Up (`LoginFragment`)
- **Screen container (`ScrollView`):** `R.id.login_screen` / `contentDescription="Login-screen"`
- **Controls:**
  - Header `TextView` (`R.id.login_header`): `android:text="Login / Sign up Form"` — tapping clears focus and dismisses the soft keyboard.
  - Mode tabs: `button-login-container` (`R.id.button_login_container`), `button-sign-up-container` (`R.id.button_sign_up_container`).
  - Inputs: `input-email` (`R.id.input_email`), `input-password` (`R.id.input_password`), `input-repeat-password` (`R.id.input_repeat_password`, visible only in Sign Up mode).
  - Submit buttons: `button-LOGIN` (`R.id.button_login`), `button-SIGN UP` (`R.id.button_sign_up`).
  - Biometric button: `button-biometric` (`R.id.button_biometric`), visible only in Login mode when `BiometricManager.canAuthenticate(...) == BIOMETRIC_SUCCESS`.
- **Dialogs & Biometrics:**
  - Uses framework `android.app.AlertDialog` (`android:id/alertTitle`, `android:id/message`) with uppercase button rendering (`isAllCaps = true`):
    - Login alert: title `"Success"`, message `"You are logged in!"`, button `"OK"`.
    - Sign Up alert: title `"Signed Up!"`, message `"You successfully signed up!"`, button `"OK"`.
  - `BiometricPrompt` title `"Login with Fingerprint"`, negative button `"Cancel"`; keeps the prompt open on non-matching fingerprints and shows the `"Success"` alert on match.

### 4. Forms (`FormsFragment`)
- **Screen container (`ScrollView`):** `R.id.forms_screen` / `contentDescription="Forms-screen"`
- **Controls:**
  - `text-input` (`R.id.text_input`, `EditText`, `hint="Type something"`, `saveEnabled="false"`) mirrored live into `input-text-result` (`R.id.input_text_result`).
  - `switch` (`R.id.form_switch`, `SwitchCompat`, `saveEnabled="false"`) paired with status text `switch-text` (`R.id.switch_text`).
  - `Dropdown` (`R.id.dropdown_container`, containing non-focusable `EditText` `R.id.dropdown_value` and `dropdown-chevron` `R.id.dropdown_chevron`) opening a framework `AlertDialog` `ListView` with options: `"This app is awesome"`, `"webdriver.io is awesome"`, `"Appium is awesome"`.
  - `button-Active` (`R.id.button_active`): opens framework `AlertDialog` (`"This button is"` / `"This button is active"`) with 3 buttons: `"Ask me later"`, `"Cancel"`, `"OK"` (`isAllCaps = true`).
  - `button-Inactive` (`R.id.button_inactive`): disabled (`android:enabled="false"`) and takes no action when tapped.
- **Lifecycle behavior:** Temporary `text-input`, `form_switch`, and `dropdown_value` state survives warm backgrounding/Recents in memory and is explicitly cleared on Activity/process recreation (`savedInstanceState != null`).

### 5. Swipe (`SwipeFragment`)
- **Screen container (`ScrollView`):** `R.id.swipe_screen` / `contentDescription="Swipe-screen"`
- **Carousel (`ViewPager2`):** `R.id.Carousel`, `contentDescription="Carousel"`, plus `AccessibilityDelegateCompat` setting `info.viewIdResourceName = "Carousel"`.
- **6 Cards:** `R.id.__CAROUSEL_ITEM_0__` .. `R.id.__CAROUSEL_ITEM_5__` (`contentDescription` and `viewIdResourceName` set to `__CAROUSEL_ITEM_0__` .. `__CAROUSEL_ITEM_5__`), full-width horizontally aligned at `x = 0` when active.
- **Below-fold reveal badge:** `R.id.wdio_logo` with `contentDescription="WebdriverIO logo"` displaying **Quy Pham** and `"You found me!!!"` after scrolling down.

### 6. Drag and Drop (`DragFragment`)
- **Screen container:** `R.id.drag_drop_screen` / `contentDescription="Drag-drop-screen"`
- **Puzzle controls:**
  - 9 drop targets: `drop-l1`, `drop-c1`, `drop-r1`, `drop-l2`, `drop-c2`, `drop-r2`, `drop-l3`, `drop-c3`, `drop-r3`.
  - 9 draggable tiles: `drag-l1`, `drag-c1`, `drag-r1`, `drag-l2`, `drag-c2`, `drag-r2`, `drag-l3`, `drag-c3`, `drag-r3` driven by `OnTouchListener` translation + `elevation`/`translationZ` lift and screen-bounds hit-testing (compatible with Espresso/UIAutomator `GestureActions.dragAndDrop` and Appium `dragAndDrop`).
  - Reset icon: `renew` (`R.id.renew_button`).
  - Completion view: shown once all 9 pieces match their slots, displaying `button-Retry` (`R.id.button_retry`) to reset the puzzle.

---

## Build & Run Tests

All Gradle commands run from `android-espresso-uiautomator-demo/`:

```bash
# Build app APK and both instrumentation test APKs
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :lifecycle-androidTest:assembleDebugAndroidTest

# Run all 23 Espresso + hybrid UIAutomator tests with AndroidX Test Orchestrator (recommended)
./gradlew :app:connectedDebugAndroidTest -PuseTestOrchestrator=true

# Run external process-kill & force-stop lifecycle tests (auto-installs :app:installDebug)
./gradlew :lifecycle-androidTest:connectedDebugAndroidTest -PuseTestOrchestrator=true

# Filter by JUnit annotation (e.g. @SmokeTest, @AccessibilityTest, @LifecycleTest, @WebViewTest)
./gradlew :app:connectedDebugAndroidTest -PuseTestOrchestrator=true \
  -Pandroid.testInstrumentationRunnerArguments.annotation=com.wdiodemoapp.testshared.SmokeTest
```

> **Note:** Run `:app:connectedDebugAndroidTest` and `:lifecycle-androidTest:connectedDebugAndroidTest` as separate Gradle commands because AGP uninstalls `:app` at teardown of `:app:connectedDebugAndroidTest`.

Generated APKs:
- Debug app APK: `android-espresso-uiautomator-demo/app/build/outputs/apk/debug/app-debug.apk`
- App Espresso test APK: `android-espresso-uiautomator-demo/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk`
- Lifecycle host test APK: `android-espresso-uiautomator-demo/lifecycle-androidTest/build/outputs/apk/androidTest/debug/lifecycle-androidTest-debug-androidTest.apk`

From the sibling Appium repo (`/Users/phamdinhquy/Documents/mobile-automation/appium-wdio`), you can also run the Appium/WebdriverIO Android suite against this APK via [wdio.android.native.app.conf.ts](../appium-wdio/config/wdio.android.native.app.conf.ts):

```bash
# Boot emulator (if needed) and run all 10 Android Appium specs against app-debug.apk
ANDROID_NPM_SCRIPT=android.native.app ./scripts/run-android-app.sh

# Or run directly against an already-running emulator/device
npm run android.native.app
```
