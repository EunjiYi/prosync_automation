## sql creation
insert.sh 1 1 1 test_1_7 ins_test_1_7_char.sql 2
update.sh 1 1 1 test_1_7 upd_test_1_7_char.sql 2
delete.sh 1 1 1 test_1_7 del_test_1_7_char.sql 2
insert.sh 1 1 1 test_1_7 ins_test_1_7_varchar.sql 3
update.sh 1 1 1 test_1_7 upd_test_1_7_varchar.sql 3
delete.sh 1 1 1 test_1_7 del_test_1_7_varchar.sql 3
insert.sh 1 1 1 test_1_7 ins_test_1_7_nchar.sql 4
update.sh 1 1 1 test_1_7 upd_test_1_7_nchar.sql 4
delete.sh 1 1 1 test_1_7 del_test_1_7_nchar.sql 4
insert.sh 1 1 1 test_1_7 ins_test_1_7_nvarchar.sql 5
update.sh 1 1 1 test_1_7 upd_test_1_7_nvarchar.sql 5
delete.sh 1 1 1 test_1_7 del_test_1_7_nvarchar.sql 5
insert.sh 1 1 1 test_1_7 ins_test_1_7_clob.sql 6
update.sh 1 1 1 test_1_7 upd_test_1_7_clob.sql 6
delete.sh 1 1 1 test_1_7 del_test_1_7_clob.sql 6
insert.sh 1 1 1 test_1_7 ins_test_1_7_nclob.sql 7
update.sh 1 1 1 test_1_7 upd_test_1_7_nclob.sql 7
delete.sh 1 1 1 test_1_7 del_test_1_7_nclob.sql 7
insert.sh 1 1 1 test_1_7 ins_test_1_7_long.sql 8
update.sh 1 1 1 test_1_7 upd_test_1_7_long.sql 8
delete.sh 1 1 1 test_1_7 del_test_1_7_long.sql 8


## test run!!!
tbsql kjccs/tibero@system1 @ins_test_1_7_char.sql; exit;
tbsql kjccs/tibero@system1 @upd_test_1_7_char.sql
##tbsql kjccs/tibero@system1 @del_test_1_7_char.sql

tbsql kjccs/tibero@system1 @ins_test_1_7_varchar.sql
tbsql kjccs/tibero@system1 @upd_test_1_7_varchar.sql
###tbsql kjccs/tibero@system1 @del_test_1_7_varchar.sql

tbsql kjccs/tibero@system1 @ins_test_1_7_nchar.sql
tbsql kjccs/tibero@system1 @upd_test_1_7_nchar.sql
##tbsql kjccs/tibero@system1 @del_test_1_7_nchar.sql

tbsql kjccs/tibero@system1 @ins_test_1_7_nvarchar.sql
tbsql kjccs/tibero@system1 @upd_test_1_7_nvarchar.sql
##tbsql kjccs/tibero@system1 @del_test_1_7_nvarchar.sql

tbsql kjccs/tibero@system1 @ins_test_1_7_clob.sql
tbsql kjccs/tibero@system1 @upd_test_1_7_clob.sql
##tbsql kjccs/tibero@system1 @del_test_1_7_clob.sql

tbsql kjccs/tibero@system1 @ins_test_1_7_nclob.sql
tbsql kjccs/tibero@system1 @upd_test_1_7_nclob.sql
##tbsql kjccs/tibero@system1 @del_test_1_7_nclob.sql

tbsql kjccs/tibero@system1 @ins_test_1_7_long.sql
tbsql kjccs/tibero@system1 @upd_test_1_7_long.sql
##tbsql kjccs/tibero@system1 @del_test_1_7_long.sql

#tbsql kjccs/tibero@system1 @ins_test_1_7_char.sql
#tbsql kjccs/tibero@system1 @upd_test_1_7_char.sql
#tbsql kjccs/tibero@system1 @del_test_1_7_char.sql

#tbsql kjccs/tibero@system1 @ins_test_1_7_varchar.sql
#tbsql kjccs/tibero@system1 @upd_test_1_7_varchar.sql
#tbsql kjccs/tibero@system1 @del_test_1_7_varchar.sql

#tbsql kjccs/tibero@system1 @ins_test_1_7_nchar.sql
#tbsql kjccs/tibero@system1 @upd_test_1_7_nchar.sql
#tbsql kjccs/tibero@system1 @del_test_1_7_nchar.sql

#tbsql kjccs/tibero@system1 @ins_test_1_7_nvarchar.sql
#tbsql kjccs/tibero@system1 @upd_test_1_7_nvarchar.sql
#tbsql kjccs/tibero@system1 @del_test_1_7_nvarchar.sql

#tbsql kjccs/tibero@system1 @ins_test_1_7_clob.sql
#tbsql kjccs/tibero@system1 @upd_test_1_7_clob.sql
#tbsql kjccs/tibero@system1 @del_test_1_7_clob.sql

#tbsql kjccs/tibero@system1 @ins_test_1_7_nclob.sql
#tbsql kjccs/tibero@system1 @upd_test_1_7_nclob.sql
#tbsql kjccs/tibero@system1 @del_test_1_7_nclob.sql

#tbsql kjccs/tibero@system1 @ins_test_1_7_long.sql
#tbsql kjccs/tibero@system1 @upd_test_1_7_long.sql
#tbsql kjccs/tibero@system1 @del_test_1_7_long.sql
