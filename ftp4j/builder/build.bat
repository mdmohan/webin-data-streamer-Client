@echo off

rem IF YOU NEED, OVERWRITE HERE YOUR JAVA_HOME AND ANT_HOME ENVIRONMENT VARIABLES
rem set JAVA_HOME=
rem set ANT_HOME=

if '"%JAVA_HOME%"' == '""' goto NO_JAVA_HOME
if '"%ANT_HOME%"' == '""' goto NO_ANT_HOME

if '%1' == '?' goto USAGE
if '%1' == '' goto USAGE
if '%2' == '' goto USAGE

goto BUILD

:NO_JAVA_HOME
echo.
echo JAVA_HOME environment variable is not defined.
echo.
goto EXIT

:NO_ANT_HOME
echo.
echo ANT_HOME environment variable is not defined.
echo.
goto EXIT

:USAGE
echo.
echo Usage:
echo.
echo build [svn.user] [svn.password] [svn.tag]
echo.
echo svn.tag is optional. If not supplied the script will build the trunk.
echo.
goto EXIT

:BUILD
"%ANT_HOME%/bin/ant" "-Dsvn.user=%1" "-Dsvn.pass=%2" "-Dsvn.tag=%3"
goto EXIT

:EXIT
