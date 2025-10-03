#!/usr/bin/env bash
set -euo pipefail

# scripts/setup-local-properties.sh
# Detect common Android SDK locations and write local.properties at project root

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOCAL_PROPERTIES_PATH="$REPO_ROOT/local.properties"

echo "Issuetrax: creating local.properties if Android SDK is found..."

candidates=()

if [ -n "${ANDROID_HOME-}" ]; then
  candidates+=("$ANDROID_HOME")
fi
if [ -n "${ANDROID_SDK_ROOT-}" ]; then
  candidates+=("$ANDROID_SDK_ROOT")
fi

# Common SDK install locations
candidates+=("$HOME/Android/Sdk" "$HOME/Android/sdk" "$HOME/.android-sdk" "/opt/android-sdk" "/opt/android/sdk")

found=""
for p in "${candidates[@]}"; do
  if [ -n "$p" ] && [ -d "$p" ]; then
    # Prefer platforms; basic sanity check
    if [ -d "$p/platforms" ] || [ -d "$p/build-tools" ]; then
      found="$p"
      break
    fi
  fi
done

if [ -n "$found" ]; then
  # If path contains tilde or spaces, ensure proper escaping is handled by Gradle (absolute path)
  echo "sdk.dir=$found" > "$LOCAL_PROPERTIES_PATH"
  echo "Wrote local.properties with sdk.dir=$found"
  echo "Note: local.properties is gitignored by default. If you need a different SDK location, set ANDROID_HOME or ANDROID_SDK_ROOT, or edit local.properties manually."
  exit 0
fi

cat <<EOF
Could not find Android SDK in common locations.

Please install the Android SDK (via Android Studio) or set one of the following environment variables to the SDK path:

  ANDROID_HOME
  ANDROID_SDK_ROOT

Common SDK locations tried:
  - $HOME/Android/Sdk
  - $HOME/Android/sdk
  - $HOME/.android-sdk
  - /opt/android-sdk
  - /opt/android/sdk

Run this script again after setting the environment variable or installing the SDK.

Example (bash/zsh):
  export ANDROID_SDK_ROOT="/path/to/Android/Sdk"
  ./scripts/setup-local-properties.sh

If you prefer to create the file manually, add a file at the project root named local.properties with a single line like:
  sdk.dir=/absolute/path/to/Android/Sdk

EOF

exit 2
