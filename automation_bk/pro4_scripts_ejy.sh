#!/bin/bash

# 테스트 진행할 프로싱크 디렉토리 생성
binary=`ls -rt $HOME | grep  prosync4- | tail -1`
echo "$binary"
date=`date +%Y%m%d`

#ims=등록한ims번호
ims=$1
sync_user=SYNC$1

echo "$sync_user"

work_dir_set () {
    wdir=$1
}

fn_init() {
#echo "동일 폴더 삭제 : ${sync_user}"
    rm -rf $HOME/prosync4_${ims}
    rm -rf $HOME/prosync4

    echo "$binary Uncompress." 
    echo "tar -zxvf $HOME/$binary"
    tar -zxvf $HOME/$binary
    #gzip -dc $binary | tar -xvf - > /dev/null 2>&1
    mv prosync4 $HOME/prosync4_{$ims}
}

fn_sync_user_create () {
echo "a"
     #prosync4_o2t 일 때 수행
     if [ ${top_id} -eq 0 ];then
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
     fi   
     
     #prosync4_t2t 일 때 수행
     if [ ${top_id} -eq 1 ];then
              echo "source TIBERO ${tibero_sync_user} user create "
              tbsql sys/tibero@${SRC_DB_NAME[0]} << EOF > /dev/null 2>&1
              create user ${tibero_sync_user} identified by tibero;
              grant dba to ${tibero_sync_user};
          EOF
   
              echo "target TIBERO ${tibero_sync_user} / ${oracle_sync_user} user create "
              tbsql sys/tibero@${TAR_DB_NAME[0]} << EOF > /dev/null 2>&1
              create user ${tibero_sync_user} identified by tibero;
              create user ${oracle_sync_user} identified by tibero;
              grant dba to ${tibero_sync_user};
              grant dba to ${oracle_sync_user};
          EOF
     fi   
#타겟 오라클은 일단 주석처리       
#    echo "target ORACLE ${tibero_sync_user} / ${oracle_sync_user} user create"
#    sqlplus sys/oracle@${TAR_DB_NAME[1]} as sysdba << EOF > /dev/null 2>&1
#    create user ${tibero_sync_user} identified by tibero;
#    create user ${oracle_sync_user} identified by tibero;
#    grant dba to ${tibero_sync_user};
#    grant dba to ${oracle_sync_user};
#EOF
}
}

fn_config_set() {
	work_dir_set $HOME/prosync4_{$ims}/install
	
	source $HOME/prosync4_{$im}s/prs_env `pwd`
	cd $HOME/prosync4_$ims/install/
	cp templates/* ./
	rm -f prs_obj.list.template
	mv prs_install.cfg.template prs_install.cfg
	mv prs_obj_group1.list.template prs_obj_group1.list
	
	#vi prs_obj_group1.list 편집
	
	
	#prs_install.cfg 수정 - 공통: 타겟 tibero 설정
				
sed -i "s/TAR_DB_NAME[0]=/TAR_DB_NAME[0]=${TAR_DB_NAME[0]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
echo "d -i s/TAR_DB_NAME[0]=/TAR_DB_NAME[0]=${TAR_DB_NAME[0]}/g $HOME/prosync4_$ims/install/prs_install.cfg"
sed -i "s/TAR_INSTALL_USER[0]=/TAR_INSTALL_USER[0]=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
sed -i "s/TAR_INSTALL_PWD[0]=/TAR_INSTALL_PWD[0]=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
			
	
	#prs_install.cfg 수정 - 소스 tibero 일 때
    if [ "${SRC_DB_TYPE}" == "tibero" ];then
    
    	sed -i "s/TOP_ID=/TOP_ID=${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
    	sed -i "s/PRS_USER=prosync/PRS_USER=prosync_${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/PRS_PWD=/PRS_PWD=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/RULE_DB_NAME=/RULE_DB_NAME=orcl182/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_USER=/RULE_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_PWD=/RULE_INSTALL_PWD=oracle/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/SRC_DB_NAME=/SRC_DB_NAME=orcl/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_USER=/SRC_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_PWD=/SRC_INSTALL_PWD=oracle/g" $HOME/prosync4_$ims/install/prs_install.cfg	
	fi
	
	
	#prs_install.cfg 수정 - 소스 oracle 일 때
	if [ "${src_type}" == "oracle" ];then
		
		sed -i "s/TOP_ID=/TOP_ID=${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
    	sed -i "s/PRS_USER=prosync/PRS_USER=prosync_${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/PRS_PWD=/PRS_PWD=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/RULE_DB_TYPE=TIBERO/RULE_DB_TYPE=ORACLE/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_DB_NAME=/RULE_DB_NAME=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_USER=/RULE_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_PWD=/RULE_INSTALL_PWD=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/SRC_DB_TYPE=TIBERO/SRC_DB_TYPE=ORACLE/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_DB_NAME=/SRC_DB_NAME=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_USER=/SRC_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_PWD=/SRC_INSTALL_PWD=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg	
		
	fi
}

fn_install () {
    source $HOME/prosync4_$ims/prs_env $HOME/prosync4_$ims
    cd $HOME/prosync4_$ims/install
    sh prs_install.sh
}

fn_admin () {
    source $HOME/prosync4_$ims/prs_env $HOME/prosync4_$ims

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
source prs_ejy.cfg
fn_sync_user_create
fn_config_set
#java test runpre
fn_install
#java test runaction
