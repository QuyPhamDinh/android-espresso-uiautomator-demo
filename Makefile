.PHONY: test app smoke regression accessibility webview lifecycle

GRADLE ?= ./gradlew
GRADLE_FLAGS ?= -PuseTestOrchestrator=true
ANDROID_SERIAL ?=
SERIAL_FLAG := $(if $(ANDROID_SERIAL),--serial $(ANDROID_SERIAL),)
RUN_TESTS := bash scripts/run-device-tests.sh $(SERIAL_FLAG)
ANN_PKG := com.wdiodemoapp.testshared

NUM_SHARDS ?= 1
SHARD_INDEX ?= 0
PREBUILT_APKS ?= false

ifeq ($(shell test "$(NUM_SHARDS)" -gt 1 && echo yes),yes)
SHARD_FLAGS := -Pandroid.testInstrumentationRunnerArguments.numShards=$(NUM_SHARDS) -Pandroid.testInstrumentationRunnerArguments.shardIndex=$(SHARD_INDEX)
RUN_LIFECYCLE := $(shell test "$(SHARD_INDEX)" -eq 0 && echo yes || echo no)
else
SHARD_FLAGS :=
RUN_LIFECYCLE := yes
endif

ifeq ($(PREBUILT_APKS),true)
APP_PREBUILT_FLAGS := \
	-x :app:packageDebug -x :app:packageDebugAndroidTest \
	-x :app:createDebugApkListingFileRedirect -x :app:createDebugAndroidTestApkListingFileRedirect \
	-x :app:compileDebugKotlin -x :app:compileDebugAndroidTestKotlin \
	-x :app:compileDebugJavaWithJavac -x :app:compileDebugAndroidTestJavaWithJavac \
	-x :app:processDebugResources -x :app:processDebugAndroidTestResources \
	-x :app:processDebugManifest -x :app:processDebugAndroidTestManifest \
	-x :test-shared:compileDebugKotlin
LIFECYCLE_PREBUILT_FLAGS := \
	-x :app:packageDebug -x :app:createDebugApkListingFileRedirect \
	-x :app:compileDebugKotlin -x :app:compileDebugJavaWithJavac \
	-x :app:processDebugResources -x :app:processDebugManifest \
	-x :lifecycle-androidTest:packageDebug -x :lifecycle-androidTest:packageDebugAndroidTest \
	-x :lifecycle-androidTest:createDebugApkListingFileRedirect -x :lifecycle-androidTest:createDebugAndroidTestApkListingFileRedirect \
	-x :lifecycle-androidTest:compileDebugKotlin -x :lifecycle-androidTest:compileDebugAndroidTestKotlin \
	-x :lifecycle-androidTest:compileDebugJavaWithJavac -x :lifecycle-androidTest:compileDebugAndroidTestJavaWithJavac \
	-x :lifecycle-androidTest:processDebugResources -x :lifecycle-androidTest:processDebugAndroidTestResources \
	-x :lifecycle-androidTest:processDebugManifest -x :lifecycle-androidTest:processDebugAndroidTestManifest \
	-x :lifecycle-androidTest:syncLifecycleTestSources \
	-x :test-shared:compileDebugKotlin
else
APP_PREBUILT_FLAGS :=
LIFECYCLE_PREBUILT_FLAGS :=
endif

APP_RUN := $(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS) $(SHARD_FLAGS) $(APP_PREBUILT_FLAGS)
LIFECYCLE_RUN := $(RUN_TESTS) $(GRADLE) :lifecycle-androidTest:connectedDebugAndroidTest $(GRADLE_FLAGS) $(LIFECYCLE_PREBUILT_FLAGS)

test:
	@app_status=0; lifecycle_status=0; \
	$(APP_RUN) || app_status=$$?; \
	if [ "$(RUN_LIFECYCLE)" = "yes" ]; then \
		$(LIFECYCLE_RUN) || lifecycle_status=$$?; \
	fi; \
	if [ $$app_status -ne 0 ]; then exit $$app_status; fi; \
	exit $$lifecycle_status

app:
	$(APP_RUN)

smoke:
	$(APP_RUN) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).SmokeTest

regression:
	$(APP_RUN) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).RegressionTest

accessibility:
	$(APP_RUN) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).AccessibilityTest

webview:
	$(APP_RUN) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).WebViewTest

lifecycle:
	@app_status=0; lifecycle_status=0; \
	$(APP_RUN) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).LifecycleTest || app_status=$$?; \
	if [ "$(RUN_LIFECYCLE)" = "yes" ]; then \
		$(LIFECYCLE_RUN) || lifecycle_status=$$?; \
	fi; \
	if [ $$app_status -ne 0 ]; then exit $$app_status; fi; \
	exit $$lifecycle_status
