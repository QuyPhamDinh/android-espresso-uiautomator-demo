.PHONY: test app smoke regression accessibility webview lifecycle

GRADLE ?= ./gradlew
GRADLE_FLAGS ?= -PuseTestOrchestrator=true
RUN_TESTS := bash scripts/run-device-tests.sh
ANN_PKG := com.wdiodemoapp.testshared

test:
	@app_status=0; lifecycle_status=0; \
	$(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS) || app_status=$$?; \
	$(RUN_TESTS) $(GRADLE) :lifecycle-androidTest:connectedDebugAndroidTest $(GRADLE_FLAGS) || lifecycle_status=$$?; \
	if [ $$app_status -ne 0 ]; then exit $$app_status; fi; \
	exit $$lifecycle_status

app:
	$(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS)

smoke:
	$(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).SmokeTest

regression:
	$(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).RegressionTest

accessibility:
	$(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).AccessibilityTest

webview:
	$(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).WebViewTest

lifecycle:
	@app_status=0; lifecycle_status=0; \
	$(RUN_TESTS) $(GRADLE) :app:connectedDebugAndroidTest $(GRADLE_FLAGS) -Pandroid.testInstrumentationRunnerArguments.annotation=$(ANN_PKG).LifecycleTest || app_status=$$?; \
	$(RUN_TESTS) $(GRADLE) :lifecycle-androidTest:connectedDebugAndroidTest $(GRADLE_FLAGS) || lifecycle_status=$$?; \
	if [ $$app_status -ne 0 ]; then exit $$app_status; fi; \
	exit $$lifecycle_status
