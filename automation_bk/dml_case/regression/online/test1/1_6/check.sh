query=`tbsql -s kjccs/tibero << EOF
SET HEAD OFF
select count(*) from KJCCS.TEST_1_6
minus
select count(*) from KJCCS.TEST_1_6@dl_sync;
quit
EOF`

cnt=`echo $query | awk '{print $1}'`

if [ ${cnt} -eq 0 ]
then
	echo "test_1_6 succes" >> test.log
else
	echo "test_1_6 fail" >> test.log
fi
