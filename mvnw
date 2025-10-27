#!/bin/sh
MVNW_JAR="./.mvn/wrapper/maven-wrapper.jar"
if [ ! -f "$MVNW_JAR" ]; then
  echo "Downloading Maven Wrapper jar..."
  mkdir -p .mvn/wrapper
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
  command -v curl >/dev/null 2>&1 && curl -fsSL -o "$MVNW_JAR" "$URL" || wget -q -O "$MVNW_JAR" "$URL"
fi
exec java -Dmaven.multiModuleProjectDirectory="$PWD" -cp "$MVNW_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
