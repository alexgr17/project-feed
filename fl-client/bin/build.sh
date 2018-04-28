#!/bin/sh
CURRENT_DIR=`dirname "$0"`
sh $CURRENT_DIR/shutdown.sh
rm $CURRENT_DIR/fl-client.jar
mvn -f $CURRENT_DIR/../pom.xml clean package -DskipTests
cp $CURRENT_DIR/../target/fl-client.jar $CURRENT_DIR
sh $CURRENT_DIR/startup.sh