tbsql kjccs/tibero << EOF
truncate table kjccs.test_1_1
/
set feedback off
@ins_test_1_1.sql
@ins_test_1_1_one.sql
@upd_test_1_1.sql
@del_test_1_1.sql
q
EOF
sleep 10
rm test.log
sh check.sh
cat test.log
