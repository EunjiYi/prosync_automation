#!/bin/bash

. prs_dml.cfg

fn_check_db_type () {
if [ "$1" = "TIBERO" ]; then 
    sql_exec="tbsql -s"
else
    sql_exec="sqlplus -s"
fi
}

cnt=0
while read table_name
do
    fn_check_db_type $SRC_DB_TYPE
    src_rst[$cnt]=`$sql_exec $USER/$PWD@$SRC_DB_NAME << EOF
    set head off
    set feedback off
    select count(*) from $table_name;
    exit
EOF`
src_rst[$cnt]=`echo ${src_rst[$cnt]} | tr -d '[ |\t]'`
cnt=`expr $cnt + 1`
done < "table.list"

cnt=0
while read table_name
do
    fn_check_db_type $TAR_DB_TYPE
    tar_rst[$cnt]=`$sql_exec $USER/$PWD@$TAR_DB_NAME << EOF
    set head off
    set feedback off
    select count(*) from $table_name;
    exit
EOF`
tar_rst[$cnt]=`echo ${tar_rst[$cnt]} | tr -d '[ |\t]'`
cnt=`expr $cnt + 1`
done < "table.list"

cnt=0
while read table_name
do


if [ "${src_rst[$cnt]}" = "${tar_rst[$cnt]}" ] 
then
    echo "pass - $table_name src_cnt : ${src_rst[$cnt]}, tar_cnt : ${tar_rst[$cnt]}"
else
    echo "fails - $table_name src_cnt : ${src_rst[$cnt]}, tar_cnt : ${tar_rst[$cnt]}"
fi

cnt=`expr $cnt + 1`
done < "table.list"
