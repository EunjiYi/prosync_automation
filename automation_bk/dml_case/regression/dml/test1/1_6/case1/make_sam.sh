filename=$1
size=`expr $2 / 800`
clob="_clob_file.txt"

cfile=$filename$clob 

rm -rf $cfile $bfile

STR="kjccstiberotmaxyoyookjccstiberotmaxyoyookjccstiberotmaxyoyookjccstiberotmaxyoyookjccstiberotmaxyoyoo"

for i in `seq 1 $size`; do
        for j in `seq 1 40`; do
                echo -n $STR >> $cfile
        done
done
