#!/bin/bash

# filepath: /Users/nikola.simsic/Documents/Projects/Make-it-native/maestro/run_maestro_tests.sh

# Get the directory of the current script
SCRIPT_DIR=$(dirname "$0")

# Source the helpers.sh script from the script's directory
source "$SCRIPT_DIR/helpers/helpers.sh" || { echo "Failed to source helpers.sh"; exit 1; }

# Check if run_tests function is available
command -v run_tests >/dev/null 2>&1 || { echo "run_tests function not found"; exit 1; }

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

# Run the single test file
run_single_test() {
  local test_file="maestro/tests/AppStartup.yaml"

  if [ ! -f "$test_file" ]; then
    echo "❌ Test file not found: $test_file"
    exit 1
  fi

  echo "🚀 Running test: $test_file"
  run_tests "$test_file"

  # Check results
  if [ ${#passed_tests[@]} -gt 0 ]; then
    echo "✅ Test passed: $test_file"
  else
    echo "❌ Test failed: $test_file"
    exit 1
  fi
}

# Run the single test
run_single_test

echo "🎉 All tests completed successfully!"
exit 0