start_idx=$1
end_idx=$2
end_idx=$((end_idx+1))
commit_cnt=$3
table_name=$4
data_file=$5
varchar_idx=1
varchar_cnt=$6
varchar_cnt=$(($varchar_cnt+1))

rm $data_file

while [ $start_idx -lt $end_idx ];do
 varchar_idx=2
 echo -n "UPDATE $table_name SET " >> $data_file
 while [ $varchar_idx -le $varchar_cnt ];do
  echo -n "C$varchar_idx=(select c2 from dml_test_1_6 where c1=1)" >> $data_file
  varchar_idx=$(($varchar_idx+1))
  if [ $varchar_idx -le $varchar_cnt ];then
   echo -n ", " >> $data_file
  fi
  done
 echo ", C888=SYSTIMESTAMP WHERE C1 = $start_idx;" >> $data_file

 is_commit=`expr $start_idx % $commit_cnt`
 if [ "$is_commit" = "0" ]; then
  echo "COMMIT;" >> $data_file
 fi
 start_idx=$(($start_idx+1))
done



