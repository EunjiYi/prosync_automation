tbsql kjccs/tibero@tac0 << EOF
@ins_test_1_10000.sql
q;
EOF
tbsql kjccs/tibero@tac0 << EOF
@upd_test_1_10000.sql
q;
EOF
