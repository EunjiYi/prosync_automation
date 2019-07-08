#!/bin/bash

source $HOME/templates/prs.cfg

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

cnt=0
while read table_name
do
	fn_check_db $1 0
    src_rst[$cnt]=`$sql_connect << EOF
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
	fn_check_db $1 1
    tar_rst_1[$cnt]=`$sql_connect << EOF
    set head off
    set feedback off
    select count(*) from $table_name;
    exit
EOF`
tar_rst_1[$cnt]=`echo ${tar_rst_1[$cnt]} | tr -d '[ |\t]'`
cnt=`expr $cnt + 1`
done < "table.list"

cnt=0
while read table_name
do
	fn_check_db $1 2
    tar_rst_2[$cnt]=`$sql_connect << EOF
    set head off
    set feedback off
    select count(*) from $table_name;
    exit
EOF`
tar_rst_2[$cnt]=`echo ${tar_rst_2[$cnt]} | tr -d '[ |\t]'`
cnt=`expr $cnt + 1`
done < "table.list"


cnt=0
while read table_name
do

if [ "${src_rst[$cnt]}" = "${tar_rst_1[$cnt]}" -a "${src_rst[$cnt]}" = "${tar_rst_2[$cnt]}" ] 
then
    if [ ${src_rst[$cnt]} -ne 0 ]; then
	echo "PASS - $table_name count : ${src_rst[$cnt]}"
    else
	echo "Except - $table_name"
    fi
else
    echo -n "FAIL - $table_name src_cnt : ${src_rst[$cnt]},"
    echo "tar_tibero_cnt : ${tar_rst_1[$cnt]}, tar_oracle_cnt : ${tar_rst_2[$cnt]}"
fi

cnt=`expr $cnt + 1`
done < "table.list"
