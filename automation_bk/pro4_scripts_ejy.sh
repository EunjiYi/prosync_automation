#!/bin/bash

binary=`ls -rt $HOME | grep  prosync4- | tail -1`
echo "$binary"
date=`date +%Y%m%d`

ims=$1
sync_user=SYNC$1
echo "$sync_user"

src_type=$2

work_dir_set () {
    wdir=$1
}

fn_init() {
    echo "remove old directory"
    rm -rf $HOME/prosync4_${ims}
    #rm -rf $HOME/prosync4

    echo "Binary Uncompress." 
    echo "Binary : $binary"
    echo "tar -zxvf $HOME/$binary"
    tar -zxvf $HOME/$binary
    #gzip -dc $binary | tar -xvf - > /dev/null 2>&1
    mv prosync4 $HOME/prosync4_${ims}
}

fn_sync_user_create() {		
        echo "target TIBERO ${sync_user} user create"
        tbsql sys/tibero@${TAR_DB_NAME} << EOF > /dev/null 2>&1
        create user ${sync_user} identified by tibero;
        create user ${sync_user} identified by tibero;
        grant dba to ${sync_user};
        grant dba to ${sync_user};
EOF

	echo "SRC_DB_TYPE : ${src_type}"
	case "${src_type}" in
	      TIBERO)
              echo "source TIBERO ${sync_user} user create"
              tbsql sys/tibero@${SRC_DB_NAME[0]} << EOF > /dev/null 2>&1
              create user ${sync_user} identified by tibero;
              grant dba to ${sync_user};
EOF
              ;;
              ORACLE)
              echo "source ORACLE ${sync_user} user create "
	      sqlplus sys/oracle@${SRC_DB_NAME[1]} as sysdba << EOF > /dev/null 2>&1
              create user ${sync_user} identified by tibero;
              grant dba to ${sync_user};
EOF

	      ;;
	esac	  
}

# target oracle    
#    echo "target ORACLE ${sync_user} / ${sync_user} user create"
#    sqlplus sys/oracle@${TAR_DB_NAME[1]} as sysdba << EOF > /dev/null 2>&1
#    create user ${sync_user} identified by tibero;
#    create user ${sync_user} identified by tibero;
#    grant dba to ${sync_user};
#    grant dba to ${sync_user};
#EOF

fn_config_set() {
	work_dir_set $HOME/prosync4_${ims}/install
	
	echo "config file setting"
	source $HOME/prosync4_${ims}/prs_env `pwd`
	cd $HOME/prosync4_${ims}/install/
	cp templates/* ./
	rm -f prs_obj.list.template
	mv prs_install.cfg.template prs_install.cfg
	mv prs_obj_group1.list.template prs_obj_group1.list
	echo "${sync_user}.%" >> prs_obj_group1.list
	echo "cat prs_obj_group1.list"
	cat prs_obj_group1.list
	
	echo "set prs_install.cfg (1) - target tibero"
	sed -i "s/TAR_DB_NAME\[0\]=/TAR_DB_NAME\[0\]=${TAR_DB_NAME}/g" $HOME/prosync4_${ims}/install/prs_install.cfg
	sed -i "s/TAR_INSTALL_USER\[0\]=/TAR_INSTALL_USER\[0\]=sys/g" $HOME/prosync4_${ims}/install/prs_install.cfg
	sed -i "s/TAR_INSTALL_PWD\[0\]=/TAR_INSTALL_PWD\[0\]=tibero/g" $HOME/prosync4_${ims}/install/prs_install.cfg
			
	
	case "${src_type}" in
		TIBERO)   	
		echo "set prs_install.cfg (2) - source tibero"
		top_id="t2t"
		echo "top_id = ${top_id}"
    		sed -i "s/TOP_ID=/TOP_ID=${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
    		sed -i "s/PRS_USER=prosync/PRS_USER=prosync_${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/PRS_PWD=/PRS_PWD=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/RULE_DB_TYPE=TIBERO/RULE_DB_TYPE=${SRC_DB_TYPE[0]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_DB_NAME=/RULE_DB_NAME=${SRC_DB_NAME[0]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_USER=/RULE_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_PWD=/RULE_INSTALL_PWD=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/SRC_DB_NAME=TIBERO/SRC_DB_TYPE=${SRC_DB_TYPE[0]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_DB_NAME=/SRC_DB_NAME=${SRC_DB_NAME[0]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_USER=/SRC_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_PWD=/SRC_INSTALL_PWD=tibero/g" $HOME/prosync4_$ims/install/prs_install.cfg	
		;;

		ORACLE)
		echo "set prs_install.cfg (2) - source oracle"
		top_id="o2t"
		echo "top_id = ${top_id}"
		sed -i "s/TOP_ID=/TOP_ID=${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
    		sed -i "s/PRS_USER=prosync/PRS_USER=prosync_${top_id}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/PRS_PWD=/PRS_PWD=oracle/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/RULE_DB_TYPE=TIBERO/RULE_DB_TYPE=${SRC_DB_TYPE[1]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_DB_NAME=/RULE_DB_NAME=${SRC_DB_NAME[1]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_USER=/RULE_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/RULE_INSTALL_PWD=/RULE_INSTALL_PWD=oracle/g" $HOME/prosync4_$ims/install/prs_install.cfg
		
		sed -i "s/SRC_DB_TYPE=TIBERO/SRC_DB_TYPE=${SRC_DB_TYPE[1]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_DB_NAME=/SRC_DB_NAME=${SRC_DB_NAME[1]}/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_USER=/SRC_INSTALL_USER=sys/g" $HOME/prosync4_$ims/install/prs_install.cfg
		sed -i "s/SRC_INSTALL_PWD=/SRC_INSTALL_PWD=oracle/g" $HOME/prosync4_$ims/install/prs_install.cfg	
		;;
	esac
}

fn_install () {
    echo "source .bash_profile"
    source $HOME/.bash_profile
    
    echo "prosync4 install start"
    source $HOME/prosync4_$ims/prs_env $HOME/prosync4_$ims
    cd $HOME/prosync4_$ims/install
    sh prs_install.sh

    echo "previous process kill"
    kill -9 `ps -ef| grep prs_  | grep -v grep  | cut -d ' ' -f 3`

}

fn_admin () {
    source $HOME/prosync4_$ims/prs_env $HOME/prosync4_$ims

    case "$1" in
        start)
        prs_adm << EOF
	start ${top_id}
	exit;
EOF
        ;;
        status)
        prs_adm << EOF
	status ${top_id}
	exit;
EOF
        ;;
        stop)
        prs_adm << EOF
	shutd man abort
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
fn_admin start
fn_admin status
fn_admin stop

