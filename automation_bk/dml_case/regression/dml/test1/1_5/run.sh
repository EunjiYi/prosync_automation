tbsql kjccs/tibero << EOF
truncate table kjccs.DML_TEST_1_5
/
set feedback off
@ins_test_1_5_c1.sql
@upd_test_1_5_c1.sql
@del_test_1_5_c1.sql
q
EOF
sleep 20
rm test.log
sh check.sh
cat test.log
