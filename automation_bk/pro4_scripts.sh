#!/bin/bash

# �׽�Ʈ ������ ���ν�ũ ���丮 ����
binary=`ls -rt | grep  prosync4- | tail -1`
rev=`ls $binary | cut -d '-' -f 4`
date=`date +%Y%m%d`
tibero_sync_user=TIBERO_$rev
oracle_sync_user=ORACLE_$rev


work_dir_set () {
    wdir=$1
}

fn_init() {
#echo "���� ���� ���� : ${tibero_sync_user}, ${oracle_sync_user}"
    rm -rf $HOME/prosync4_$rev
    rm -rf $HOME/prosync4

    echo "$binary Uncompress." 
    tar -zxvf $HOME/$binary
    #gzip -dc $binary | tar -xvf - > /dev/null 2>&1
    mv $HOME/prosync4 $HOME/prosync4_$rev
	
	sed -i "s/tibero_sync_user/${tibero_sync_user}/g" $HOME/templates/prs.cfg
	sed -i "s/oracle_sync_user/${oracle_sync_user}/g" $HOME/templates/prs.cfg
}

fn_sync_user_create () {
    echo "source TIBERO ${tibero_sync_user} user create "
    tbsql sys/tibero@${SRC_DB_NAME[0]} << EOF > /dev/null 2>&1
    create user ${tibero_sync_user} identified by tibero;
    grant dba to ${tibero_sync_user};
EOF
    echo "source ORACLE ${oracle_sync_user} user create"
    sqlplus sys/oracle@${SRC_DB_NAME[1]} as sysdba << EOF > /dev/null 2>&1
    create user ${oracle_sync_user} identified by tibero;
    grant dba to ${oracle_sync_user};
EOF
    echo "target TIBERO ${tibero_sync_user} / ${oracle_sync_user} user create "
    tbsql sys/tibero@${TAR_DB_NAME[0]} << EOF > /dev/null 2>&1
    create user ${tibero_sync_user} identified by tibero;
    create user ${oracle_sync_user} identified by tibero;
    grant dba to ${tibero_sync_user};
    grant dba to ${oracle_sync_user};
EOF
    echo "target ORACLE ${tibero_sync_user} / ${oracle_sync_user} user create"
    sqlplus sys/oracle@${TAR_DB_NAME[1]} as sysdba << EOF > /dev/null 2>&1
    create user ${tibero_sync_user} identified by tibero;
    create user ${oracle_sync_user} identified by tibero;
    grant dba to ${tibero_sync_user};
    grant dba to ${oracle_sync_user};
EOF
}

fn_sync_table_create () {
    cd $HOME/dml_case
    sh dml_case.sh 0 TIBERO
    sh dml_case.sh 0 ORACLE
    sh dml_case.sh 1 TIBERO
    sh dml_case.sh 1 ORACLE
}

fn_install_cfg () {
    ## �ҽ� ��ġ ���� �۾�
    work_dir_set $PWD/prosync4_$rev/install
    cp templates/prs_install.cfg_tibero $wdir/
    cp templates/prs_install.cfg_oracle $wdir/
    sed -i "s/<<sync_user>>/${tibero_sync_user}/g" $wdir/prs_install.cfg_tibero
    sed -i "s/<<sync_user>>/${oracle_sync_user}/g" $wdir/prs_install.cfg_oracle
    cp templates/prs_obj_group1.list $wdir/prs_obj_group1.list
    cp templates/prs_obj_group2.list $wdir/prs_obj_group2.list
    
    ## �ҽ� TIBERO cfg ���� �۾�
    work_dir_set $PWD/prosync4_$rev/config
    cp $PWD/templates/prs_top.map $wdir/prs_top.map
    sed -i  "s/<<tibero_sync_user>>/${tibero_sync_user}/g"  $wdir/prs_top.map
    sed -i  "s/<<oracle_sync_user>>/${oracle_sync_user}/g"  $wdir/prs_top.map
    sed -i  "s/PROGRAM=<<tibero_ext>>/PROGRAM=prs_ext/g" $wdir/prs_top.map
    sed -i  "s/PROGRAM=<<oracle_ext>>/PROGRAM=prs_oext/g" $wdir/prs_top.map

    cp $PWD/templates/prs_ext.cfg.template $wdir/${tibero_sync_user}_ext1.cfg
    cp $PWD/templates/prs_ext.cfg.template $wdir/${tibero_sync_user}_ext2.cfg
    sed -i  "s/<<sync_user>>/${tibero_sync_user}/g"  $wdir/${tibero_sync_user}_ext1.cfg
    sed -i  "s/<<sync_user>>/${tibero_sync_user}/g"  $wdir/${tibero_sync_user}_ext2.cfg
    sed -i  "s/EXTRACT_NUM=1/EXTRACT_NUM=2/g"  $wdir/${tibero_sync_user}_ext2.cfg
    sed -i  "s/LISTENER_PORT=7610/LISTENER_PORT=17610/g"  $wdir/${tibero_sync_user}_ext1.cfg
    sed -i  "s/LISTENER_PORT=7610/LISTENER_PORT=17611/g"  $wdir/${tibero_sync_user}_ext2.cfg
    sed -i  "s/<<db_type>>/TIBERO/g"  $wdir/${tibero_sync_user}_ext*.cfg
    sed -i  "s/<<db_name>>/tibero/g"  $wdir/${tibero_sync_user}_ext*.cfg
    sed -i  "s/<<archive_log_dir>>/\/data\/tibero\/tbdata\/archive\//g"  $wdir/${tibero_sync_user}_ext*.cfg
    sed -i  "s/<<archive_log_format>>/log-t\%t-r\%r-s\%s.arc/g"  $wdir/${tibero_sync_user}_ext*.cfg

    sed -i  "s/<<client_driver>>/\/data\/tibero\/tibero6\/client\/lib\/libtbodbc\.so/g"  $wdir/${tibero_sync_user}_ext*.cfg

    cp $PWD/templates/prs_apply.cfg.template $wdir/${tibero_sync_user}_apply1.cfg
    sed -i  "s/<<apply_num>>/1/g"  $wdir/${tibero_sync_user}_apply1.cfg
    sed -i  "s/<<src_db_cnt>>/2/g"  $wdir/${tibero_sync_user}_apply1.cfg
    sed -i  "s/<<sync_user>>/${tibero_sync_user}/g"  $wdir/${tibero_sync_user}_apply1.cfg
    sed -i  "s/<<tar_db_type>>/${TAR_DB_TYPE[0]}/g"  $wdir/${tibero_sync_user}_apply1.cfg
    sed -i  "s/<<tar_db_name>>/${TAR_DB_NAME[0]}/g"  $wdir/${tibero_sync_user}_apply1.cfg
    sed -i  "s/<<client_driver>>/\/data\/tibero\/tibero6\/client\/lib\/libtbodbc\.so/g"  $wdir/${tibero_sync_user}_apply1.cfg

    cp $PWD/templates/prs_apply.cfg.template $wdir/${tibero_sync_user}_apply2.cfg
    sed -i  "s/<<apply_num>>/2/g"  $wdir/${tibero_sync_user}_apply2.cfg
    sed -i  "s/<<src_db_cnt>>/2/g"  $wdir/${tibero_sync_user}_apply2.cfg
    sed -i  "s/<<sync_user>>/${tibero_sync_user}/g"  $wdir/${tibero_sync_user}_apply2.cfg
    sed -i  "s/<<tar_db_type>>/${TAR_DB_TYPE[1]}/g"  $wdir/${tibero_sync_user}_apply2.cfg
    sed -i  "s/<<tar_db_name>>/${TAR_DB_NAME[1]}/g"  $wdir/${tibero_sync_user}_apply2.cfg
    sed -i  "s/<<client_driver>>/\/home\/oracle\/app\/product\/11.2.0\/dbhome_2\/lib\/libclntsh.so/g"  $wdir/${tibero_sync_user}_apply2.cfg

    sed -i  "s/LISTENER_PORT=7620/LISTENER_PORT=17620/g"  $wdir/${tibero_sync_user}_apply1.cfg
    sed -i  "s/LISTENER_PORT=7620/LISTENER_PORT=17621/g"  $wdir/${tibero_sync_user}_apply2.cfg

    cp $PWD/templates/prs_vf.cfg.template $wdir/${tibero_sync_user}_vf.cfg
    sed -i  "s/<<sync_user>>/${tibero_sync_user}/g"  $wdir/${tibero_sync_user}_vf.cfg
    sed -i  "s/<<tar_db_type>>/${TAR_DB_TYPE[0]}/g"  $wdir/${tibero_sync_user}_vf.cfg
    sed -i  "s/<<tar_db_name>>/${TAR_DB_NAME[0]}/g"  $wdir/${tibero_sync_user}_vf.cfg

    cp $PWD/templates/prs_llob.cfg.template $wdir/${tibero_sync_user}_llob.cfg
    sed -i  "s/<<sync_user>>/${tibero_sync_user}/g"  $wdir/${tibero_sync_user}_llob.cfg
    sed -i  "s/<<src_db_type>>/TIBERO/g"  $wdir/${tibero_sync_user}_llob.cfg
    sed -i  "s/<<src_db_name>>/tibero/g"  $wdir/${tibero_sync_user}_llob.cfg
    sed -i  "s/<<client_driver>>/\/home\/oracle\/app\/product\/11.2.0\/dbhome_2\/lib\/libclntsh.so/g"  $wdir/${tibero_sync_user}_llob.cfg
    cp $PWD/templates/prs_rule.cfg.template $wdir/${tibero_sync_user}_rule.cfg
    sed -i  "s/<<sync_user>>/${tibero_sync_user}/g"  $wdir/${tibero_sync_user}_rule.cfg

    ## �ҽ� ORACLE cfg ���� �۾�
    work_dir_set $PWD/prosync4_$rev/config
    cp $PWD/templates/prs_ext.cfg.template $wdir/${oracle_sync_user}_ext1.cfg
    sed -i  "s/<<sync_user>>/${oracle_sync_user}/g"  $wdir/${oracle_sync_user}_ext1.cfg
    sed -i  "s/LISTENER_PORT=7610/LISTENER_PORT=27610/g"  $wdir/${oracle_sync_user}_ext1.cfg
    sed -i  "s/<<db_type>>/ORACLE/g"  $wdir/${oracle_sync_user}_ext1.cfg
    sed -i  "s/<<db_name>>/orcl/g"  $wdir/${oracle_sync_user}_ext1.cfg
    sed -i  "s/<<archive_log_dir>>/\/home\/oracle\/ora_arch\//g"  $wdir/${oracle_sync_user}_ext1.cfg
    sed -i  "s/<<archive_log_format>>/log-t\%t-r\%r-s\%s.arc/g"  $wdir/${oracle_sync_user}_ext1.cfg
    sed -i  "s/<<client_driver>>/\/home\/oracle\/app\/product\/11.2.0\/dbhome_2\/lib\/libclntsh.so/g"  $wdir/${oracle_sync_user}_ext1.cfg

    cp $PWD/templates/prs_apply.cfg.template $wdir/${oracle_sync_user}_apply1.cfg
    sed -i  "s/<<apply_num>>/1/g"  $wdir/${oracle_sync_user}_apply1.cfg
    sed -i  "s/<<src_db_cnt>>/1/g"  $wdir/${oracle_sync_user}_apply1.cfg
    sed -i  "s/<<sync_user>>/${oracle_sync_user}/g"  $wdir/${oracle_sync_user}_apply1.cfg
    sed -i  "s/<<tar_db_type>>/${TAR_DB_TYPE[0]}/g"  $wdir/${oracle_sync_user}_apply1.cfg
    sed -i  "s/<<tar_db_name>>/${TAR_DB_NAME[0]}/g"  $wdir/${oracle_sync_user}_apply1.cfg

    cp $PWD/templates/prs_apply.cfg.template $wdir/${oracle_sync_user}_apply2.cfg
    sed -i  "s/<<apply_num>>/2/g"  $wdir/${oracle_sync_user}_apply2.cfg
    sed -i  "s/<<src_db_cnt>>/1/g"  $wdir/${oracle_sync_user}_apply2.cfg
    sed -i  "s/<<sync_user>>/${oracle_sync_user}/g"  $wdir/${oracle_sync_user}_apply2.cfg
    sed -i  "s/<<tar_db_type>>/${TAR_DB_TYPE[1]}/g"  $wdir/${oracle_sync_user}_apply2.cfg
    sed -i  "s/<<tar_db_name>>/${TAR_DB_NAME[1]}/g"  $wdir/${oracle_sync_user}_apply2.cfg
    sed -i  "s/<<client_driver>>/\/data\/tibero\/tibero6\/client\/lib\/libtbodbc\.so/g"  $wdir/${oracle_sync_user}_apply1.cfg
    sed -i  "s/<<client_driver>>/\/home\/oracle\/app\/product\/11.2.0\/dbhome_2\/lib\/libclntsh.so/g"  $wdir/${oracle_sync_user}_apply2.cfg
    sed -i  "s/LISTENER_PORT=7620/LISTENER_PORT=27620/g"  $wdir/${oracle_sync_user}_apply1.cfg
    sed -i  "s/LISTENER_PORT=7620/LISTENER_PORT=27621/g"  $wdir/${oracle_sync_user}_apply2.cfg

    cp $PWD/templates/prs_vf.cfg.template $wdir/${oracle_sync_user}_vf.cfg
    sed -i  "s/<<sync_user>>/${oracle_sync_user}/g"  $wdir/${oracle_sync_user}_vf.cfg
    sed -i  "s/<<tar_db_type>>/${TAR_DB_TYPE[0]}/g"  $wdir/${oracle_sync_user}_vf.cfg
    sed -i  "s/<<tar_db_name>>/${TAR_DB_NAME[0]}/g"  $wdir/${oracle_sync_user}_vf.cfg

    cp $PWD/templates/prs_llob.cfg.template $wdir/${oracle_sync_user}_llob.cfg
    sed -i  "s/<<sync_user>>/${oracle_sync_user}/g"  $wdir/${oracle_sync_user}_llob.cfg
    sed -i  "s/<<src_db_type>>/ORACLE/g"  $wdir/${oracle_sync_user}_llob.cfg
    sed -i  "s/<<src_db_name>>/orcl/g"  $wdir/${oracle_sync_user}_llob.cfg
    sed -i  "s/<<client_driver>>/\/home\/oracle\/app\/product\/11.2.0\/dbhome_2\/lib\/libclntsh.so/g"  $wdir/${oracle_sync_user}_llob.cfg
    sed -i  "s/LISTENER_PORT=17630/LISTENER_PORT=27630/g"  $wdir/${oracle_sync_user}_llob.cfg
    cp $PWD/templates/prs_rule.cfg.template $wdir/${oracle_sync_user}_rule.cfg
    sed -i  "s/<<sync_user>>/${oracle_sync_user}/g"  $wdir/${oracle_sync_user}_rule.cfg
    
    cp $PWD/templates/prs_mgr.cfg.template $wdir/prs_mgr.cfg
    cp $PWD/templates/prs_adm.cfg.template $wdir/prs_adm.cfg
    sed -i  "s/<<revision>>/${rev}/g"  $wdir/*.cfg
}

fn_install () {
    source $HOME/prosync4_$rev/prs_env $HOME/prosync4_$rev
    cd $HOME/prosync4_$rev/install
    sed -i "s/<<sync_user>>/${tibero_sync_user}/g" prs_obj_group*.list
    sh prs_install.sh -c prs_install.cfg_tibero
    sed -i "s/${tibero_sync_user}/${oracle_sync_user}/g" prs_obj_group*.list
    sh prs_install.sh -c prs_install.cfg_oracle
}

fn_admin () {
    source $HOME/prosync4_$rev/prs_env $HOME/prosync4_$rev

    case "$1" in
        start)
        prs_adm << EOF
        start $2
        exit;
EOF
        ;;
        status)
        prs_adm << EOF
        status $2
        exit;
EOF
        ;;
        stop)
        prs_adm << EOF
        shutd $2 abort
        exit;
EOF
        ;;
    esac
}



######################## main ########################
fn_init
source templates/prs.cfg
fn_install_cfg
fn_sync_user_create
fn_sync_table_create
fn_install

fn_admin start ${tibero_sync_user} &
sleep 1
fn_admin status ${tibero_sync_user} &
sleep 1

#all case test
cd $HOME/dml_case
echo "TIBERO regression test"
sh dml_case.sh 7 TIBERO 

fn_admin stop ${tibero_sync_user} &
sleep 1

fn_admin start ${oracle_sync_user} &
sleep 1
fn_admin status ${oracle_sync_user} &
sleep 1

#all case test
cd $HOME/dml_case
echo "ORACLE regression test"
sh dml_case.sh 7 ORACLE 

fn_admin stop ${oracle_sync_user} &
sleep 5

echo "Test finished"

fn_admin stop man &
sleep 2

cd $HOME
tar -czvf ${rev}_log.tgz prosync4_${rev}/var
exit