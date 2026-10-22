#!/usr/bin/env bash
# Compila y ejecuta Civitas sin necesidad de NetBeans.
set -euo pipefail
cd "$(dirname "$0")"

if [ -z "${JAVA_HOME:-}" ] && [ -d /opt/homebrew/opt/openjdk@21 ]; then
  JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
fi
JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/}"

CP="lib/flatlaf-3.7.2.jar"
rm -rf build/classes
mkdir -p build/classes
"${JAVA_BIN}javac" -encoding UTF-8 -d build/classes -cp "$CP" $(find src -name '*.java')
exec "${JAVA_BIN}java" -cp "build/classes:$CP" GUI.CivitasApp "$@"
