#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
OLD_PWD="$(pwd)"

cd "$SCRIPT_DIR/.."

bin/stop.sh
bin/start.sh

cd "$OLD_PWD"