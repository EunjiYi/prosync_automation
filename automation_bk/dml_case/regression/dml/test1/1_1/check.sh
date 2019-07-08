query=`tbsql -s kjccs/tibero << EOF
SET HEAD OFF
select count(*) from KJCCS.DML_TEST_1_1
minus
select count(*) from KJCCS.DML_TEST_1_1@dl_sync;
quit
EOF`

cnt=`echo $query | awk '{print $1}'`

if [ ${cnt} -eq 0 ]
then
	echo "dml_test_1_1 succes" >> test.log
else
	echo "dml_test_1_1 fail" >> test.log
fi
