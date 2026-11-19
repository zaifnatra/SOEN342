@echo off
setlocal
cd /d "%~dp0" || exit /b 1

if defined JAVA_HOME (
    set "JAVAC=%JAVA_HOME%\bin\javac.exe"
    set "JAVA=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVAC=javac"
    set "JAVA=java"
)

"%JAVAC%" -version >nul 2>&1
if errorlevel 1 (
    echo A JDK ^(Java 17 or newer^) is required. Install one and set JAVA_HOME, then run: .\run.bat >&2
    exit /b 1
)

if not exist "out" mkdir "out"
if errorlevel 1 exit /b 1
"%JAVAC%" --release 17 -encoding UTF-8 -d out src\*.java
if errorlevel 1 exit /b 1
"%JAVA%" -cp out Console
exit /b %errorlevel%
