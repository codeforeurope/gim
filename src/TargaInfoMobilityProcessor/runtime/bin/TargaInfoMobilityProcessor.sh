#!/bin/bash

# Application main class
APP_NAME=sistematica.infomobprocessor.InfoMobilityProcessor

# Absolute path to this script. /home/user/bin/foo.sh
SCRIPT=$0
#$(readlink -f $0)

# Absolute path this script is in. /home/user/bin
SCRIPTPATH=`dirname $SCRIPT`

cd $SCRIPTPATH

for file in ../lib/*
do
    CLASSPATH=$CLASSPATH:$file
done

nohup java -Djava.library.path=../lib -Dmain.class.name=$APP_NAME -classpath $CLASSPATH sistematica.apptemplate.Main > /dev/null 2>&1 &

#java -Djava.library.path=../lib -Dmain.class.name=$APP_NAME -classpath $CLASSPATH sistematica.apptemplate.Main

