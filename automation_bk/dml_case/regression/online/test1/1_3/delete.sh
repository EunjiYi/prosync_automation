start_idx=$1
end_idx=$2
end_idx=$((end_idx+1))
commit_cnt=$3
table_name=$4
data_file=$5

rm $data_file

while [ $start_idx -lt $end_idx ];do
 echo "DELETE FROM $table_name WHERE C1=$start_idx;" >> $data_file
 is_commit=`expr $start_idx % $commit_cnt`
 if [ "$is_commit" = "0" ]; then
  echo "COMMIT;" >> $data_file
 fi
 start_idx=$(($start_idx+1))
done


