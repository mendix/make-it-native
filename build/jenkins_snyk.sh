#!/bin/bash
set -e
cd "$(dirname "$0")/.."

npx snyk auth ${SNYK_AUTH}
npm run snyk
