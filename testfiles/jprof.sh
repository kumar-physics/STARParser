#!/bin/sh
#JAVA_HOME=/usr/java/jdk1.5.0_03
CLASSPATH=../java/build
export CLASSPATH
rm TIMESJ.csv
for i in `ls /website/ftp/pub/data/nmr-star`
do
    if [ $i = "." -o $i = ".." -o $i = "CSV" -o $i = "offsets" ]
    then
        echo "Skipping $i"
    else
        echo -n "$i," >> TIMESJ.csv
        size=`ls -l /website/ftp/pub/data/nmr-star/$i | awk '{ print $5; }'`
        echo -n "$size,"  >> TIMESJ.csv
        java edu.bmrb.sans.SansParser /website/ftp/pub/data/nmr-star/$i 2>>TIMESJ.csv
    fi
done
