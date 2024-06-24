sh_dir=`dirname $0`
echo $sh_dir
java -cp $sh_dir:$sh_dir/../classes:"$sh_dir/../lib/*" com.gdxsoft.sqlProfiler.AppTest $1 $2 $3 $4 $5 $6 $7 $8 $9 $10 

