#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ -n "${JAVA_HOME:-}" ]]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JAVA="$JAVA_HOME/bin/java"
else
    JAVAC=javac
    JAVA=java
fi
if ! "$JAVAC" -version >/dev/null 2>&1; then
    printf '%s\n' 'A JDK (Java 17 or newer) is required. Install one and set JAVA_HOME, then run: bash run.sh' >&2
    exit 1
fi
mkdir -p out
"$JAVAC" --release 17 -encoding UTF-8 -d out src/*.java
exec "$JAVA" -cp out Console
