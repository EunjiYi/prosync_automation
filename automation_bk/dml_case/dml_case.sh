#!/bin/bash

### DML SYNC script ###

source $HOME/templates/prs.cfg
rev=`ls -rt $HOME | grep  prosync4- | tail -1 | cut -d '-' -f 4`

fn_sql_connect_set () {
    #fn_echo "1:$1, 2:$2, 3:$3, 4:$4 "
    AS_SYSDBA=""
    if [ "$1" = "TIBERO" ]; then 
        sql_connect="tbsql -i -s $2/$3@$4"
    elif [ "$1" = "UPDB" ]; then 
        sql_connect="upsql -i -s $2/$3@$4"
    elif [ "$1" = "KDB" ]; then 
        sql_connect="kdsql -i -s $2/$3@$4"
    elif [ "$1" = "ORACLE" ]; then 
        sql_connect_user=`echo $2 | tr "[a-z]" "[A-Z]"`
        if [ "$sql_connect_user" = "SYS" ]; then 
            AS_SYSDBA="as sysdba"
        fi   
        sql_connect="sqlplus -s $2/$3@$4 $AS_SYSDBA"
    elif [ "$1" = "MYSQL" ]; then 
    if [ -z "$DB_PORT" ]; then 
        DB_PORT=3306
    fi   
        sql_connect="mysql -u$2 -p$3 -h$4 -P$DB_PORT"
    elif [ "$1" = "MSSQL" ]; then 
        sql_connect="sqlcmd -D -S $4 -U $2 -P $3"
    else 
        echo "NOT SET DB_TYPE"
    fi   
}


fn_check_db () {
    if [ "$1" = "TIBERO" -o "$1" = "tibero" ]; then 
       	DB_USER=${TIBERO_USER}
        if [ $2 -eq 1 ];then
            DB_TYPE=${TAR_DB_TYPE[0]}
            DB_PWD=${TAR_DB_PWD[0]}
            DB_NAME=${TAR_DB_NAME[0]}
        elif [ $2 -eq 2 ]; then
            DB_TYPE=${TAR_DB_TYPE[1]}
            DB_PWD=${TAR_DB_PWD[1]}
            DB_NAME=${TAR_DB_NAME[1]}
		else
            DB_TYPE=${SRC_DB_TYPE[0]}
        	DB_NAME=${SRC_DB_NAME[0]}
		    DB_PWD=${SRC_DB_PWD[0]}
    fi
    else
        DB_USER=${ORACLE_USER}
        if [ $2 -eq 1 ];then
            DB_TYPE=${TAR_DB_TYPE[0]}
            DB_PWD=${TAR_DB_PWD[0]}
            DB_NAME=${TAR_DB_NAME[0]}
        elif [ $2 -eq 2 ]; then
            DB_TYPE=${TAR_DB_TYPE[1]}
            DB_PWD=${TAR_DB_PWD[1]}
            DB_NAME=${TAR_DB_NAME[1]}
		else
            DB_TYPE=${SRC_DB_TYPE[1]}
	        DB_PWD=${SRC_DB_PWD[1]}
	        DB_NAME=${SRC_DB_NAME[1]}
    	fi
	fi
    fn_sql_connect_set $DB_TYPE $DB_USER $DB_PWD $DB_NAME
}

function fn_dmltype_drop_table() 
{
    fn_check_db $1 0
	echo "source $DB_TYPE, $DB_USER user table drop"
    $sql_connect << EOF > /dev/null 2>&1
	set feedback 6
    @drop_table.sql 
    @regression/online/drop_table.sql
    @regression/dml/drop_table.sql
    @issue/drop_table.sql
EOF

    fn_check_db $1 1
	echo "target $DB_TYPE, $DB_USER user table drop"
    $sql_connect << EOF > /dev/null 2>&1
    @drop_table.sql 
    @regression/online/drop_table.sql
    @regression/dml/drop_table.sql
    @issue/drop_table.sql
EOF
    fn_check_db $1 2
	echo "target $DB_TYPE, $DB_USER user table drop"
    $sql_connect << EOF > /dev/null 2>&1
    @drop_table.sql 
    @regression/online/drop_table.sql
    @regression/dml/drop_table.sql
    @issue/drop_table.sql
EOF
}

function fn_dmltype_create_table()
{
	fn_check_db $1 0
    echo "source $DB_TYPE, $DB_USER user table create"
    $sql_connect << EOF > /dev/null 2>&1
    @cre_table.sql 
    @regression/online/cre_table.sql
    @regression/dml/cre_table.sql
    @issue/cre_table.sql
EOF

    fn_check_db $1 1
    echo "target $DB_TYPE, $DB_USER user table create"
    $sql_connect << EOF > /dev/null 2>&1
    @cre_table.sql 
    @regression/online/cre_table.sql
    @regression/dml/cre_table.sql
    @issue/cre_table.sql
EOF
    fn_check_db $1 2
    echo "target $DB_TYPE, $DB_USER user table create"
    $sql_connect << EOF > /dev/null 2>&1
    @cre_table.sql 
    @regression/online/cre_table.sql
    @regression/dml/cre_table.sql
    @issue/cre_table.sql
EOF
}

function fn_dmltype_objects_type()
{
    echo "fn_dmltype_objects_type START!!"
    fn_check_db $1 0
    $sql_connect << EOF > /dev/null 2>&1
    -- range_partition
    @objects/range_partition/ins.sql
    @objects/range_partition/upd.sql
    @objects/range_partition/del.sql
    @objects/range_partition/ins_parallel.sql
    @objects/range_partition/upd_parallel.sql
    @objects/range_partition/del_parallel.sql
    @objects/range_partition/ins_append.sql
    @objects/range_partition/upd_append.sql
    @objects/range_partition/del_append.sql
    -- hash_partition
    @objects/hash_partition/ins.sql
    @objects/hash_partition/upd.sql
    @objects/hash_partition/del.sql
    @objects/hash_partition/ins_parallel.sql
    @objects/hash_partition/upd_parallel.sql
    @objects/hash_partition/del_parallel.sql
    @objects/hash_partition/ins_append.sql
    @objects/hash_partition/upd_append.sql
    @objects/hash_partition/del_append.sql
    -- list_partition
    @objects/list_partition/ins.sql
    @objects/list_partition/upd.sql
    @objects/list_partition/del.sql
    @objects/list_partition/ins_parallel.sql
    @objects/list_partition/upd_parallel.sql
    @objects/list_partition/del_parallel.sql
    @objects/list_partition/ins_append.sql
    @objects/list_partition/upd_append.sql
    @objects/list_partition/del_append.sql
    -- composite_partition
    @objects/composite_partition/ins.sql
    @objects/composite_partition/upd.sql
    @objects/composite_partition/del.sql
    @objects/composite_partition/ins_parallel.sql
    @objects/composite_partition/upd_parallel.sql
    @objects/composite_partition/del_parallel.sql
    @objects/composite_partition/ins_append.sql
    @objects/composite_partition/upd_append.sql
    @objects/composite_partition/del_append.sql
    -- btree_index
    @objects/btree_index/ins.sql
    @objects/btree_index/upd.sql
    @objects/btree_index/del.sql
    @objects/btree_index/ins_parallel.sql
    @objects/btree_index/upd_parallel.sql
    @objects/btree_index/del_parallel.sql
    @objects/btree_index/ins_append.sql
    @objects/btree_index/upd_append.sql
    @objects/btree_index/del_append.sql
    -- bitmap_index
    @objects/bitmap_index/ins.sql
    @objects/bitmap_index/upd.sql
    @objects/bitmap_index/del.sql
    @objects/bitmap_index/ins_parallel.sql
    @objects/bitmap_index/upd_parallel.sql
    @objects/bitmap_index/del_parallel.sql
    @objects/bitmap_index/ins_append.sql
    @objects/bitmap_index/upd_append.sql
    @objects/bitmap_index/del_append.sql
    -- concatenated_index
    @objects/concatenated_index/ins.sql
    @objects/concatenated_index/upd.sql
    @objects/concatenated_index/del.sql
    @objects/concatenated_index/ins_parallel.sql
    @objects/concatenated_index/upd_parallel.sql
    @objects/concatenated_index/del_parallel.sql
    @objects/concatenated_index/ins_append.sql
    @objects/concatenated_index/upd_append.sql
    @objects/concatenated_index/del_append.sql
    -- reversekey_index
    @objects/reversekey_index/ins.sql
    @objects/reversekey_index/upd.sql
    @objects/reversekey_index/del.sql
    @objects/reversekey_index/ins_parallel.sql
    @objects/reversekey_index/upd_parallel.sql
    @objects/reversekey_index/del_parallel.sql
    @objects/reversekey_index/ins_append.sql
    @objects/reversekey_index/upd_append.sql
    @objects/reversekey_index/del_append.sql
    -- function_based_index
    @objects/function_based_index/ins.sql
    @objects/function_based_index/upd.sql
    @objects/function_based_index/del.sql
    @objects/function_based_index/ins_parallel.sql
    @objects/function_based_index/upd_parallel.sql
    @objects/function_based_index/del_parallel.sql
    @objects/function_based_index/ins_append.sql
    @objects/function_based_index/upd_append.sql
    @objects/function_based_index/del_append.sql
    -- local_index
    @objects/local_index/ins.sql
    @objects/local_index/upd.sql
    @objects/local_index/del.sql
    -- global_index
    @objects/global_index/ins.sql
    @objects/global_index/upd.sql
    @objects/global_index/del.sql
    -- psm
    @objects/psm/ins.sql
    @objects/psm/upd.sql
    @objects/psm/del.sql
    @objects/primarykey.sql
    -- etc
    @objects/unique.sql
    @objects/notnull.sql
    @objects/default.sql
    @objects/foreignkey.sql
    @objects/check.sql
    @objects/select_insert.sql

EOF
    echo "fn_dmltype_objects_type END!!"

}

function fn_dmltype_data_type()
{
    echo "fn_dmltype_data_type START!!"
    fn_check_db $1 0
    $sql_connect << EOF > /dev/null 2>&1
    @data/char.sql
    @data/varchar.sql
    @data/date.sql
    @data/timestamp.sql
    @data/composit.sql
    @data/clob.sql      -- TEST_22
    @data/blob.sql      -- TEST_23
    @data/nclob.sql     -- TEST_24
    @data/longraw.sql   -- TEST_25
EOF
    echo "fn_dmltype_data_type END!!"
}
function fn_dmltype_charcter_type()
{
    echo "fn_dmltype_charcter_type START!!"
    fn_check_db $1 0
    $sql_connect << EOF > /dev/null 2>&1
    @character/character.sql
EOF
    echo "fn_dmltype_charcter_type END!!"
}

function fn_dmltype_regression()
{
    echo "fn_dmltype_regression START!!"
    fn_check_db $1 0
    $sql_connect << EOF > /dev/null 2>&1
    @regression/online/test1/1_1/ins_test_1_1.sql
    @regression/online/test1/1_1/upd_test_1_1.sql
    @regression/online/test1/1_1/del_test_1_1.sql
    @regression/online/test1/1_2/ins_test_1_2.sql
    @regression/online/test1/1_2/upd_test_1_2.sql
    @regression/online/test1/1_2/del_test_1_2.sql
    @regression/online/test1/1_3/ins_test_1_3_s1.sql
    @regression/online/test1/1_3/upd_test_1_3_s1.sql
    @regression/online/test1/1_3/del_test_1_3_s1.sql
    @regression/online/test1/1_4/ins_test_1_4_s1.sql
    @regression/online/test1/1_4/upd_test_1_4_s1.sql
    @regression/online/test1/1_4/del_test_1_4_s1.sql
    @regression/online/test1/1_5/ins_test_1_5_c1.sql
    @regression/online/test1/1_5/upd_test_1_5_c1.sql
    @regression/online/test1/1_5/del_test_1_5_c1.sql
    @regression/online/test1/1_6/case1/ins_test_1_6_100.sql
    @regression/online/test1/1_6/case1/upd_test_1_6_100.sql
    @regression/online/test1/1_6/case1/del_test_1_6_100.sql
    @regression/online/test1/1_7/ins_test_1_7_char.sql
    @regression/online/test1/1_7/upd_test_1_7_char.sql
    @regression/online/test1/1_7/del_test_1_7_char.sql
    @regression/online/test1/1_7/ins_test_1_7_varchar.sql
    @regression/online/test1/1_7/upd_test_1_7_varchar.sql
    @regression/online/test1/1_7/del_test_1_7_varchar.sql
    @regression/online/test1/1_7/ins_test_1_7_nchar.sql
    @regression/online/test1/1_7/upd_test_1_7_nchar.sql
    @regression/online/test1/1_7/del_test_1_7_nchar.sql
    @regression/online/test1/1_7/ins_test_1_7_nvarchar.sql
    @regression/online/test1/1_7/upd_test_1_7_nvarchar.sql
    @regression/online/test1/1_7/del_test_1_7_nvarchar.sql
    @regression/online/test1/1_7/ins_test_1_7_clob.sql
    @regression/online/test1/1_7/upd_test_1_7_clob.sql
    @regression/online/test1/1_7/del_test_1_7_clob.sql
    @regression/online/test1/1_7/ins_test_1_7_nclob.sql
    @regression/online/test1/1_7/upd_test_1_7_nclob.sql
    @regression/online/test1/1_7/del_test_1_7_nclob.sql
    @regression/online/test1/1_7/ins_test_1_7_long.sql
    @regression/online/test1/1_7/upd_test_1_7_long.sql
    @regression/online/test1/1_7/del_test_1_7_long.sql
    exit
EOF

    $sql_connect << EOF > /dev/null 2>&1
    @regression/dml/test1/1_1/ins_test_1_1.sql
    @regression/dml/test1/1_1/upd_test_1_1.sql
    @regression/dml/test1/1_1/del_test_1_1.sql
    @regression/dml/test1/1_2/ins_test_1_2.sql
    @regression/dml/test1/1_2/upd_test_1_2.sql
    @regression/dml/test1/1_2/del_test_1_2.sql
    @regression/dml/test1/1_3/ins_test_1_3_s1.sql
    @regression/dml/test1/1_3/upd_test_1_3_s1.sql
    @regression/dml/test1/1_3/del_test_1_3_s1.sql
    @regression/dml/test1/1_4/ins_test_1_4_s1.sql
    @regression/dml/test1/1_4/upd_test_1_4_s1.sql
    @regression/dml/test1/1_4/del_test_1_4_s1.sql
    @regression/dml/test1/1_5/ins_test_1_5_c1.sql
    @regression/dml/test1/1_5/upd_test_1_5_c1.sql
    @regression/dml/test1/1_5/del_test_1_5_c1.sql
    @regression/dml/test1/1_6/ins_test_1_6_1.sql
    @regression/dml/test1/1_6/upd_test_1_6_1.sql
    @regression/dml/test1/1_6/del_test_1_6_1.sql
    @regression/dml/test1/1_7/ins_test_1_7_char.sql
    @regression/dml/test1/1_7/upd_test_1_7_char.sql
    @regression/dml/test1/1_7/del_test_1_7_char.sql
    @regression/dml/test1/1_7/ins_test_1_7_varchar.sql
    @regression/dml/test1/1_7/upd_test_1_7_varchar.sql
    @regression/dml/test1/1_7/del_test_1_7_varchar.sql
    @regression/dml/test1/1_7/ins_test_1_7_nchar.sql
    @regression/dml/test1/1_7/upd_test_1_7_nchar.sql
    @regression/dml/test1/1_7/del_test_1_7_nchar.sql
    @regression/dml/test1/1_7/ins_test_1_7_nvarchar.sql
    @regression/dml/test1/1_7/upd_test_1_7_nvarchar.sql
    @regression/dml/test1/1_7/del_test_1_7_nvarchar.sql
    @regression/dml/test1/1_7/ins_test_1_7_clob.sql
    @regression/dml/test1/1_7/upd_test_1_7_clob.sql
    @regression/dml/test1/1_7/del_test_1_7_clob.sql
    @regression/dml/test1/1_7/ins_test_1_7_nclob.sql
    @regression/dml/test1/1_7/upd_test_1_7_nclob.sql
    @regression/dml/test1/1_7/del_test_1_7_nclob.sql
    @regression/dml/test1/1_7/ins_test_1_7_long.sql
    @regression/dml/test1/1_7/upd_test_1_7_long.sql
    @regression/dml/test1/1_7/del_test_1_7_long.sql
    exit
EOF

    echo "fn_dmltype_regression END!!"

}

function fn_dmltype_issue()
{
    echo "fn_dmltype_issue START!!"
    fn_check_db $1 0
    $sql_connect << EOF > /dev/null 2>&1
    @issue/ims135693.sql
    @issue/ims100168_ins.sql
    @issue/ims100168_del.sql
    @issue/ims100168_upd.sql
    @issue/ims147068.sql
    @issue/ims184465/ims184465.sql
    @issue/ims184347.sql
EOF
    echo "fn_dmltype_issue END!!"
}


function fn_dmltype_all()
{
    fn_dmltype_objects_type $1
    fn_dmltype_data_type $1 
    fn_dmltype_charcter_type $1
    fn_dmltype_regression $1
    fn_dmltype_issue $1
	if [ $1 = TIBERO -o $1 = tibero ]; then
		current=current_tsn
	else
		current=current_scn
	fi

	sync_tsn=`$sql_connect << EOF
    set head off
    set feedback off
    select $current from v\\$database;
    exit
EOF`
	sync_tsn=`echo ${sync_tsn} | tr -d '[ |\t]'`

	if [ $1 = ORACLE -o $1 = oracle ]; then
        # 오라클 일 때는 tsn 포맷 형식이 달라서 256^2
        sync_tsn=`expr $sync_tsn '*' 256 '*' 256`
    else
        #약간의 갭으로 인해서, 7정도 감소
        sync_tsn=`expr $sync_tsn - 7`
    fi

	# 3min
	kill_cnt=18
	while [ $kill_cnt -gt 0 ] 
	do
		fn_sync_check $1
		sleep 10
		kill_cnt=`expr $kill_cnt - 1`
	done
	sh check.sh $1 > $1_$rev
}
function fn_sync_check() {
	echo -n "Sync user :  $DB_USER, Finishied TSN : $sync_tsn"
	fn_check_db $1 1
	fn_sql_connect_set $DB_TYPE PROSYNC_${DB_USER} $DB_PWD $DB_NAME
	last_tsn1=`$sql_connect << EOF
set head off
set feedback off
SELECT MAX(TO_CHAR(TSN)) FROM PRS_LCT;
exit
EOF`
	last_tsn1=`echo ${last_tsn1} | tr -d '[ |\t]'`
	fn_check_db $1 2
	fn_sql_connect_set $DB_TYPE PROSYNC_${DB_USER} $DB_PWD $DB_NAME
	last_tsn2=`$sql_connect << EOF
set head off
set feedback off
SELECT MAX(TO_CHAR(TSN)) FROM PRS_LCT;
exit
EOF`
	last_tsn2=`echo ${last_tsn2} | tr -d '[ |\t]'`
	gap_tsn=`expr $sync_tsn - $last_tsn2`
	echo ", Last TSN : $last_tsn2 - gap is ${gap_tsn}."
	if [ "$last_tsn1" = "$last_tsn2" ]; then
		if [ $last_tsn2 -ge $sync_tsn ]; then
			sh check.sh $1 > $1_$rev
			exit;
		fi
	else 
		echo "The values of the first node($last_tsn1) and the second node($last_tsn2) are different."
	fi

}
    
### DDL SYNC TYPE ###


dml_type=("Table_Drop" "Table_Create" "Objects_Type_Test" "Data_Type_Test" "Character_Type_Test" "Regression_Test" "Issue" "ALL Test")

if [ -z $1 ] ; 
then
    for ((iq0; i<8; i++)); do
        echo "$i) ${dml_type[$i]}"
    done
    read "dmltype_num"
else
    dmltype_num=$1
fi

for ((i=0; i<8; i++)); do
    if [ $dmltype_num -eq $i ]
    then
        #echo "$dmltype_num ${dml_type[$dmltype_num]}"
        case $dmltype_num in
            0) fn_dmltype_drop_table $2 ;;
            1) fn_dmltype_create_table $2 ;;
            2) fn_dmltype_objects_type $2 ;;
            3) fn_dmltype_data_type ${dml_type[$dmltype_num]} ;;
            4) fn_dmltype_charcter_type ${dml_type[$dmltype_num]} ;;
            5) fn_dmltype_regression ${dml_type[$dmltype_num]} ;;
            6) fn_dmltype_issue ${dml_type[$dmltype_num]} ;;
            7) fn_dmltype_all $2 ;;
        esac
    fi
done

exit

echo "DML Query exit!!"

################
