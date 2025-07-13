#!/bin/bash
. /etc/profile
pid=$(ps x | grep "Chapter13-1.0-SNAPSHOT.jar" grep | -v grep | awk '{print $1}')
if [ -n "$pid" ]; then
kill -9 $pid
fi

cd Chapter13
mvn clean package
cd target
BUILD ID=dontKillMe
nohup java -jar Chapter13-1.0-SNAPSHOT.jar &