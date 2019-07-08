tbsql kjccs/tibero << EOF
truncate table kjccs.DML_TEST_1_4
/
set feedback off
@ins_test_1_4_s1.sql
@ins_test_1_4_s1_one.sql
@upd_test_1_4_s1.sql
@del_test_1_4_s1.sql
q
EOF
sleep 10
rm test.log
sh check.sh
cat test.log
