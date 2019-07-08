tbsql kjccs/tibero << EOF
truncate table kjccs.test_1_7
/
set feedback off
@ins_test_1_7_char.sql
@upd_test_1_7_char.sql
@del_test_1_7_char.sql
@ins_test_1_7_varchar.sql
@upd_test_1_7_varchar.sql
@del_test_1_7_varchar.sql
@ins_test_1_7_nchar.sql
@upd_test_1_7_nchar.sql
@del_test_1_7_nchar.sql
@ins_test_1_7_nvarchar.sql
@upd_test_1_7_nvarchar.sql
@del_test_1_7_nvarchar.sql
@ins_test_1_7_clob.sql
@upd_test_1_7_clob.sql
@del_test_1_7_clob.sql
@ins_test_1_7_nclob.sql
@upd_test_1_7_nclob.sql
@del_test_1_7_nclob.sql
@ins_test_1_7_long.sql
@upd_test_1_7_long.sql
@del_test_1_7_long.sql
@ins_test_1_7.sql
q
EOF
sleep 10
rm test.log
sh check.sh
cat test.log
