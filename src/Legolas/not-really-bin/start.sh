#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
OLD_PWD="$(pwd)"

cd "$SCRIPT_DIR/.."

. cfg/scripts-cfg.sh

OLD_IFS="$IFS"
IFS=$'\n'

CP='.'
for JAR in $(find lib -name '*.jar' -type f -print)
do
	CP="$CP:$JAR"
done

IFS="$OLD_IFS"

echo "CLASSPATH: $CP"
"$JAVA_HOME/bin/java" -cp "$CP" $MAIN_CLASS > /dev/null 2>&1 &
echo "Process running in background (PID $!)..."

cd "$OLD_PWD"
