#!/bin/sh
#JAVA_HOME=/usr/java/jdk1.5.0_03
CLASSPATH=./build
export CLASSPATH
cat "$1" |  java edu.bmrb.sans.SansParser2
