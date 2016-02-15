#!/bin/sh
JAVA_HOME=/usr/java/jdk1.5.0_03
CLASSPATH=sans/build
export JAVA_HOME CLASSPATH
cat "$1" |  ${JAVA_HOME}/bin/java edu.bmrb.sans.CifParser
