#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
OLD_PWD="$(pwd)"

cd "$SCRIPT_DIR/.."

. cfg/scripts-cfg.sh

STATUS=0
if [ -z $(pgrep -f "$MAIN_CLASS") ]
then
    echo 'Not running.'
    STATUS=1
else
    echo 'Running.'
fi

cd "$OLD_PWD"
exit "$STATUS"
