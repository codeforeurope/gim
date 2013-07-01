#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
OLD_PWD="$(pwd)"

cd "$SCRIPT_DIR/.."

. cfg/scripts-cfg.sh

echo 'Stopping process...'
PID=$(pgrep -f "$MAIN_CLASS")
if [ -z "$PID" ]
then
    echo 'Not running.'
else
    echo "PID is $PID"
    #pkill -f "$MAIN_CLASS" && echo 'Killed.' || echo 'Failed.'
    touch ctl/stop
fi

cd "$OLD_PWD"
