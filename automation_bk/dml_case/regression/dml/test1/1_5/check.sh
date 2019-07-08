query=`tbsql -s kjccs/tibero << EOF
SET HEAD OFF
select count(*) from KJCCS.DML_TEST_1_5
minus
select count(*) from KJCCS.DML_TEST_1_5@dl_sync;
quit
EOF`

cnt=`echo $query | awk '{print $1}'`

if [ ${cnt} -eq 0 ]
then
	echo "dml_test_1_5 succes" >> test.log
else
	echo "dml_test_1_5 fail" >> test.log
fi
