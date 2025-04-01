#!/bin/bash

# filepath: /Users/nikola.simsic/Documents/Projects/Make-it-native/maestro/run_maestro_tests.sh

# Get the directory of the current script
SCRIPT_DIR=$(dirname "$0")

# Source the helpers.sh script from the script's directory
source "$SCRIPT_DIR/helpers/helpers.sh" || { echo "Failed to source helpers.sh"; exit 1; }

# Check if run_test_with_retries function is available
command -v run_test_with_retries >/dev/null 2>&1 || { echo "run_test_with_retries function not found"; exit 1; }

if [ "$1" == "android" ]; then
  APP_ID="com.mendix.developerapp.mx10"
  PLATFORM="android"
elif [ "$1" == "ios" ]; then
  APP_ID="com.mendix.developerapp.mx10"
  PLATFORM="ios"
  IOS_DEVICE="iPhone 16"
else
  echo "Usage: $0 [android|ios]"
  exit 1
fi

passed_tests=()
failed_tests=()
final_failed_tests=()

# Define the test file
TEST_FILE="maestro/tests/AppStartup.yaml"

# Check if the test file exists
if [ ! -f "$TEST_FILE" ]; then
  echo "❌ Test file not found: $TEST_FILE"
  exit 1
fi

# Run the test with retries
run_test_with_retries "$TEST_FILE"

# Check the result
if [ $? -eq 0 ]; then
  echo "🎉 Test completed successfully!"
  exit 0
else
  echo "❌ Test failed after retries."
  exit 1
fi