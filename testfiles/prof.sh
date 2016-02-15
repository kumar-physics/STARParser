#!/bin/sh
rm TIMESC.csv
for i in `ls /website/ftp/pub/data/nmr-star`
do
    if [ $i = "." -o $i = ".." -o $i = "CVS" -o $i = "offsets" ]
    then
        echo "Skipping $i"
    else
        echo -n "$i," >> TIMESC.csv
        size=`ls -l  /website/ftp/pub/data/nmr-star/$i | awk '{ print $5; }'`
        echo -n "$size,"  >> TIMESC.csv
        ../c++/lex  /website/ftp/pub/data/nmr-star/$i 2>> TIMESC.csv
    fi
done
