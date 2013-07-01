@echo off

set SCRIPT_DIR=%~dp0
set OLD_PWD=%CD%

cd %SCRIPT_DIR%\..

call cfg\scripts-cfg.cmd

set CP=.
for %%i in (lib\*.jar) do call :CONCAT %%i

echo CLASSPATH: %CP%
echo MAIN CLASS: %MAIN_CLASS%

java -cp %CP% %MAIN_CLASS%
cd %OLD_PWD%

:CONCAT
set CP=%CP%;%1
