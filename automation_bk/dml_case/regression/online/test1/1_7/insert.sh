start_idx=$1
end_idx=$2
end_idx=$((end_idx+1))
commit_cnt=$3
table_name=$4
data_file=$5
column_number=$6

start_char=1
end_char=2000

rm $data_file

while [ $start_idx -lt $end_idx ];do
 echo -n "INSERT INTO $table_name (C1, C$column_number) VALUES($start_idx, " >> $data_file
 echo -n "'" >> $data_file
 while [ $start_char -lt $end_char ];do
  echo -n "C" >> $data_file
  start_char=$(($start_char+1))
 done
 echo "');" >> $data_file 

 is_commit=`expr $start_idx % $commit_cnt`
 if [ "$is_commit" = "0" ]; then
  echo "COMMIT;" >> $data_file
 fi
 start_idx=$(($start_idx+1))
done



