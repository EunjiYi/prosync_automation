query=`tbsql -s kjccs/tibero << EOF
SET HEAD OFF
select count(*) from KJCCS.TEST_1_3
minus
select count(*) from KJCCS.TEST_1_3@dl_sync;
quit
EOF`

cnt=`echo $query | awk '{print $1}'`

if [ ${cnt} -eq 0 ]
then
	echo "test_1_3 succes" >> test.log
else
	echo "test_1_3 fail" >> test.log
fi
