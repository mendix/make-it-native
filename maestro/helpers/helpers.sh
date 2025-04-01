#!/bin/bash

MAX_RETRIES=3
RETRY_DELAY=10

# Function to restart the iOS simulator
restart_simulator() {
    echo "🔄 Restarting iOS Simulator..."
    xcrun simctl shutdown "$IOS_DEVICE"
    sleep 10
    bash ./maestro/helpers/prepare_ios.sh
}

# Function to set the status bar on the Android emulator
set_status_bar() {
    echo "Setting status bar on Android Emulator..."
    adb root
    adb shell "date -u 11010000" # Set time to 11:01 - due to some bug it always sets to 12:00
    adb shell svc wifi enable # Enable Wi-Fi
    adb shell svc data enable # Enable mobile data
    adb shell dumpsys battery set level 100 # Set battery level to 100%
    adb shell dumpsys battery set status 2 # Set battery status to charging
    adb reverse tcp:8080 tcp:8080 # Reverse port 8080

    # Verify the status bar settings
    retries=0
    max_retries=5
    while [ $retries -lt $max_retries ]; do
        current_time=$(adb shell "date +%H:%M")
        if [ "$current_time" == "00:00" ]; then
            echo "Status bar set successfully."
            break
        else
            echo "Retrying status bar settings..."
            adb shell "date -u 11010000"
            sleep 2
            retries=$((retries + 1))
        fi
    done

    if [ $retries -eq $max_retries ]; then
        echo "Failed to set status bar after $max_retries attempts."
    fi
}

# Function to ensure the emulator is ready
ensure_emulator_ready() {
    boot_completed=false
    while [ "$boot_completed" == "false" ]; do
        boot_completed=$(adb -s emulator-5554 shell getprop sys.boot_completed 2>/dev/null)
        if [ "$boot_completed" == "1" ]; then
            echo "Emulator is ready."
            break
        else
            echo "Waiting for emulator to be ready..."
            sleep 5
        fi
    done
}

# Function to run tests
run_test_with_retries() {
    local test_file="$1"
    local attempt=0

    while [ $attempt -lt $MAX_RETRIES ]; do
        echo "🚀 Running test: $test_file (Attempt $((attempt + 1))/$MAX_RETRIES)"
        
        # Ensure emulator is ready before each attempt
        ensure_emulator_ready

        # Set the status bar for Android
        if [ "$PLATFORM" == "android" ]; then
            set_status_bar
        fi

        if $HOME/.local/bin/maestro/bin/maestro test --env APP_ID=$APP_ID --env PLATFORM=$PLATFORM --env MAESTRO_DRIVER_STARTUP_TIMEOUT=300000 "$test_file"; then
            echo "✅ Test passed: $test_file"
            return 0
        else
            echo "❌ Test failed: $test_file (Attempt $((attempt + 1))/$MAX_RETRIES)"
            attempt=$((attempt + 1))
            if [ $attempt -lt $MAX_RETRIES ]; then
                echo "Retrying in $RETRY_DELAY seconds..."
                sleep $RETRY_DELAY
            fi
        fi
    done

    echo "❌ Test failed after $MAX_RETRIES attempts: $test_file"
    return 1
}