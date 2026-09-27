#!/usr/bin/env bash
# Publish an hChat release to Modrinth.
#
# Usage:
#   .modrinth/publish.sh <version> <jar-path>
#   e.g. .modrinth/publish.sh 1.4.0 plugin-dist/target/hChat-1.4.0.jar
#
# Requires:
#   - MODRINTH_TOKEN env var: personal access token from
#     https://modrinth.com/settings/pats (scope: Create versions)
#   - curl and python on PATH
set -euo pipefail

VERSION="${1:?usage: publish.sh <version> <jar-path>}"
JAR="${2:?usage: publish.sh <version> <jar-path>}"
DIR="$(cd "$(dirname "$0")" && pwd)"

# pick the first python interpreter that actually runs (Windows ships a
# broken python3 alias stub when Python is not installed)
PYTHON=""
for candidate in python3 python; do
  if command -v "$candidate" >/dev/null 2>&1 && "$candidate" -c "" 2>/dev/null; then
    PYTHON="$(command -v "$candidate")"
    break
  fi
done
if [[ -z "$PYTHON" ]]; then
  echo "error: python is required (python3 or python on PATH)" >&2
  exit 1
fi

: "${MODRINTH_TOKEN:?MODRINTH_TOKEN env var is required (https://modrinth.com/settings/pats)}"

CHANGELOG_FILE="$DIR/changelogs/${VERSION}.md"
if [[ ! -f "$CHANGELOG_FILE" ]]; then
  echo "error: $CHANGELOG_FILE not found - create it before publishing" >&2
  exit 1
fi
if [[ ! -f "$JAR" ]]; then
  echo "error: jar not found: $JAR" >&2
  exit 1
fi

# merge the version template with the changelog text into the multipart data
# NOTE: the JSON is uploaded from a FILE. Passing it inline in
# "-F data=${JSON};type=..." makes curl cut the value at the first
# semicolon inside the changelog (e.g. "transitions...;"), sending a
# truncated payload that Modrinth rejects with a JSON parse error.
DATA_FILE="$(mktemp "${TMPDIR:-/tmp}/modrinth-data-XXXXXX.json")"
RESPONSE_FILE="$(mktemp "${TMPDIR:-/tmp}/modrinth-response-XXXXXX.json")"
trap 'rm -f "$DATA_FILE" "$RESPONSE_FILE"' EXIT

"$PYTHON" - "$DIR/version-template.json" "$CHANGELOG_FILE" "$VERSION" > "$DATA_FILE" <<'PY'
import json, sys
with open(sys.argv[1], encoding="utf-8") as f:
    data = json.load(f)
with open(sys.argv[2], encoding="utf-8") as f:
    data["changelog"] = f.read()
for key in ("name", "version_number"):
    data[key] = data[key].replace("{version}", sys.argv[3])
print(json.dumps(data))
PY

echo "Publishing hChat ${VERSION} to Modrinth (project P0DczmuD)..."
HTTP_CODE="$(curl -sS -o "$RESPONSE_FILE" -w '%{http_code}' \
  -X POST "https://api.modrinth.com/v2/version" \
  -H "Authorization: ${MODRINTH_TOKEN}" \
  -F "data=<${DATA_FILE};type=application/json" \
  -F "file=@${JAR}")"

if [[ "$HTTP_CODE" != "200" && "$HTTP_CODE" != "201" ]]; then
  echo "error: Modrinth API returned HTTP ${HTTP_CODE}:" >&2
  cat "$RESPONSE_FILE" >&2
  exit 1
fi

VERSION_ID="$("$PYTHON" -c 'import json,sys;print(json.load(open(sys.argv[1], encoding="utf-8"))["id"])' "$RESPONSE_FILE")"
echo "Done: version ${VERSION_ID} published."
echo "https://modrinth.com/plugin/hchat"
