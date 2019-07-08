tbsql kjccs/tibero << EOF
truncate table kjccs.dml_test_1_6
/
set feedback off
@case1/ins_test_1_6.sql
!sleep 1
@case1/ins_test_1_6_1.sql
@case1/upd_test_1_6_1.sql
@case1/del_test_1_6_1.sql
@case1/del_test_1_6.sql
q
EOF
sleep 10
rm test.log
sh check.sh
cat test.log
