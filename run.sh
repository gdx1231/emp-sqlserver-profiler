#!/bin/sh

sh_dir=`dirname $0`
echo $sh_dir
java -cp $sh_dir/target/emp-sqlserver-profiler-1.0.0.jar:$sh_dir/target/lib/* com.gdxsoft.sqlProfiler.ProfilerControl $1 $2 $3 $4 $5 $6 $7 $8 $9 $10 

