#!/usr/bin/env bash
set -eu

mkdir -p build/device-test-results
run_dir=$(mktemp -d "build/device-test-results/$(date -u +%Y%m%dT%H%M%SZ)-XXXXXX")
run_id=${run_dir##*/}
printf 'Device test results: %s\n' "$run_dir"

# Preserve existing device settings if instrumentation exits before FontScaleRule can restore them.
sdk_dir=${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}
adb_bin=${ADB:-$(command -v adb || printf '%s' "$sdk_dir/platform-tools/adb")}
snapshot_count=0

restore_font_scales() {
    for (( index = 0; index < snapshot_count; index++ )); do
        serial=${serials[index]}
        original=${font_scales[index]}
        current=$("$adb_bin" -s "$serial" shell settings get system font_scale) || continue
        current=${current//$'\r'/}
        [[ "$current" != "$original" ]] || continue

        if [[ "$original" == null ]]; then
            "$adb_bin" -s "$serial" shell settings delete system font_scale ||
                printf 'Could not restore the font scale on %s\n' "$serial" >&2
        else
            "$adb_bin" -s "$serial" shell settings put system font_scale "$original" ||
                printf 'Could not restore the font scale on %s\n' "$serial" >&2
        fi
    done
}

if [[ -x "$adb_bin" ]]; then
    while read -r serial state _; do
        [[ "$state" == device ]] || continue
        [[ -z "${ANDROID_SERIAL:-}" || "$serial" == "$ANDROID_SERIAL" ]] || continue
        original=$("$adb_bin" -s "$serial" shell settings get system font_scale) || continue
        original=${original//$'\r'/}
        [[ "$original" == null || "$original" =~ ^[0-9]+([.][0-9]+)?$ ]] || continue
        serials[snapshot_count]=$serial
        font_scales[snapshot_count]=$original
        snapshot_count=$((snapshot_count + 1))
    done < <("$adb_bin" devices)
fi

trap restore_font_scales EXIT
trap 'exit 130' INT
trap 'exit 143' TERM

modules=()
for arg in "$@"; do
    case "$arg" in
        :app:*) modules+=(app) ;;
        :lifecycle-androidTest:*) modules+=(lifecycle-androidTest) ;;
    esac
done
if [[ ${#modules[@]} -eq 0 ]]; then
    modules=(app lifecycle-androidTest)
fi

gradle_status=0
"$@" -Pandroid.enableAdditionalTestOutput=true \
    "-Pandroid.testInstrumentationRunnerArguments.artifactRunId=$run_id" || gradle_status=$?

collection_status=0
for module in "${modules[@]}"; do
    for artifacts in "$module"/build/outputs/connected_android_test_additional_output/debugAndroidTest/connected/*/failures/"$run_id"; do
        [ -d "$artifacts" ] || continue
        device_dir=${artifacts%/failures/*}
        destination=$run_dir/$module/devices/${device_dir##*/}
        if ! mkdir -p "$destination" || ! cp -R "$artifacts/." "$destination"; then
            printf 'Could not archive failure artifacts from %s\n' "$device_dir" >&2
            collection_status=1
        fi
    done

    if [ -d "$module/build/reports/androidTests/connected" ]; then
        mkdir -p "$run_dir/$module/html"
        cp -R "$module/build/reports/androidTests/connected/." "$run_dir/$module/html" || collection_status=1
    fi

    if [ -d "$module/build/outputs/androidTest-results/connected" ]; then
        mkdir -p "$run_dir/$module/xml"
        cp -R "$module/build/outputs/androidTest-results/connected/." "$run_dir/$module/xml" || collection_status=1
    fi
done

if [ "$gradle_status" -ne 0 ]; then
    exit "$gradle_status"
fi
exit "$collection_status"
