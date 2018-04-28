#!/bin/sh
rm nohup.out
nohup java -Xmx128m -Xms128m -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -Duser.timezone=Europe/Moscow -jar fl-project.jar &