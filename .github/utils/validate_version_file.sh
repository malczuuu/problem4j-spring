#!/usr/bin/env bash
set -euo pipefail

# Require file via first argument or FILE env variable
FILE="${1:-${FILE:-}}"

if [ ! -f "$FILE" ]; then
  echo "::error::file $FILE not found"
  exit 1
fi

# Read file and trim whitespaces
VERSION=$(tr -d '[:space:]' < "$FILE")

# Validate version format
if [[ ! "$VERSION" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "::error::validation of $FILE failed: '$VERSION', expected format: vX.Y.Z (e.g. v1.2.3)"
  exit 1
fi

echo "::notice::validation of $FILE passed: '$VERSION'"
