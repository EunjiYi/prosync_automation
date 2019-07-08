tbsql kjccs/tibero << EOF
truncate table kjccs.test_1_2
/
set feedback off
@ins_test_1_2.sql
@ins_test_1_2_one.sql
@upd_test_1_2.sql
@del_test_1_2.sql
q
EOF
sleep 10
rm test.log
sh check.sh
cat test.log
