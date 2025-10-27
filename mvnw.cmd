@ECHO OFF
setlocal
set MVNW_JAR=.mvn\wrapper\maven-wrapper.jar
IF NOT EXIST %MVNW_JAR% (
  ECHO Downloading Maven Wrapper jar...
  mkdir .mvn\wrapper 2>NUL
  powershell -Command "Invoke-WebRequest -Uri https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar -OutFile %MVNW_JAR%"
)
set MAVEN_PROJECTBASEDIR=%CD%
java -Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR% -cp %MVNW_JAR% org.apache.maven.wrapper.MavenWrapperMain %*
