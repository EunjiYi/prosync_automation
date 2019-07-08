## sql creation
insert.sh 1 1 1 test_1_6 ins_test_1_6.sql
insert_sel.sh 2 100 50 test_1_6 ins_test_1_6_1.sql 1
update_sel.sh 2 100 50 test_1_6 upd_test_1_6_1.sql 1
delete.sh 2 100 50 test_1_6 del_test_1_6_1.sql

insert_sel.sh 2 100 50 test_1_6 ins_test_1_6_2.sql 2
update_sel.sh 2 100 50 test_1_6 upd_test_1_6_2.sql 2
delete.sh 2 100 50 test_1_6 del_test_1_6_2.sql

insert_sel.sh 2 100 50 test_1_6 ins_test_1_6_10.sql 10
update_sel.sh 2 100 50 test_1_6 upd_test_1_6_10.sql 10
delete.sh 2 100 50 test_1_6 del_test_1_6_10.sql

insert_sel.sh 2 100 50 test_1_6 ins_test_1_6_100.sql 100
update_sel.sh 2 100 50 test_1_6 upd_test_1_6_100.sql 100
delete.sh 2 100 50 test_1_6 del_test_1_6_100.sql

## 만중 선임님. 테스트 전에 반드시 이걸 실행하세요.
#tbsql kjccs/tibero@tac_src_sync0 @ins_test_1_6.sql

## test run!!!
#tbsql kjccs/tibero@tac_src_sync0 @ins_test_1_6_100.sql
#tbsql kjccs/tibero@tac_src_sync0 @upd_test_1_6_100.sql
#tbsql kjccs/tibero@tac_src_sync0 @del_test_1_6_100.sql

#tbsql kjccs/tibero@system1 @ins_test_1_6_1.sql
#tbsql kjccs/tibero@system1 @upd_test_1_6_1.sql
#tbsql kjccs/tibero@system1 @del_test_1_6_1.sql

#tbsql kjccs/tibero@system1 @ins_test_1_6_2.sql
#tbsql kjccs/tibero@system1 @upd_test_1_6_2.sql
#tbsql kjccs/tibero@system1 @del_test_1_6_2.sql

#tbsql kjccs/tibero@system1 @ins_test_1_6_10.sql
#tbsql kjccs/tibero@system1 @upd_test_1_6_10.sql
#tbsql kjccs/tibero@system1 @del_test_1_6_10.sql

#tbsql kjccs/tibero@system1 @ins_test_1_6_100.sql
#tbsql kjccs/tibero@system1 @upd_test_1_6_100.sql
#tbsql kjccs/tibero@system1 @del_test_1_6_100.sql
