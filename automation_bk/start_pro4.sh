#!/bin/sh

file_path=/home/dbqa/automation
source ${file_path}/templates/prs.cfg
source ${file_path}/tibero.profile

if [ -z $1 ] 
then
	continue
elif [ $1 == "build" ]
then
tbsql -s tibero/tmax@dbqa2 << EOF
INSERT INTO DBQA2.BUILD (SEQ, STATUS, PRIORITY, PLATFORM, PRODUCT, VERSION, HOTFIX,
REQ_DATE, REGIST_ID, REGIST_DATE, MODIFY_ID, MODIFY_DATE, COMPILE_MODE,
IS_SRC_DEL, IS_TBSYNC, IS_CONFLICT, PROJECT_NAME, SITE_NAME, IMS_NO, SAMPLER_RUN_YN)
values (DBQA2.SEQ_BUILD.nextval, 'G1301', 'G0403003', 'G0209', 'G0101002', 'G0103005',
'', sysdate, 'sangbok_heo', sysdate, 'sangbok_heo', sysdate,
'opt',1, 0, 0, '프로싱크 자동화', 'DQA2', 0, 'N');
INSERT INTO DBQA2.BUILD_HISTORY (SEQ, STATUS, REGIST_DATE ) values (DBQA2.SEQ_BUILD.currval, 'G1301', sysdate );
commit;
exit
EOF

build_seq=`tbsql -s tibero/tmax@dbqa2 << EOF
set pages 0
set head off
set feedback 6
select max(seq) from DBQA2.BUILD_HISTORY;
exit
EOF`

while [ true ]
do

build_status=`tbsql -s tibero/tmax@dbqa2 << EOF
set pages 0
set head off
set feedback 6
select status from DBQA2.BUILD_HISTORY where SEQ=\$build_seq and REGIST_DATE=(select max(REGIST_DATE) from DBQA2.BUILD_HISTORY where SEQ=\$build_seq);
exit
EOF`

    build_seq=`echo $build_seq | tr -d '[ |\t]'`
    echo "SEQ : $build_seq, STATUS : $build_status, `date`"
    if [ "$build_status" = "G1304" ]
    then
        echo "build success";
	break
    fi
    sleep 200
done
fi



BINARY_PATH=prosync4/linux64
NEWEST_BINARY=`ssh binary@192.168.105.34 ls -lrt $BINARY_PATH | grep -v 'md5' | tail -1 | awk '{print $9}'`
# 최신 바이너리가 없으면 최신 바이너리 다운로드
if [ -f $NEWEST_BINARY ];
then
    echo "최신 바이너리 존재 : $NEWEST_BINARY"
else
    sftp binary@192.168.105.34 <<EOF
    get $BINARY_PATH/$NEWEST_BINARY
    quit
EOF
fi


cp -Rf $file_path/templates $file_path/dml_case $file_path/pro4_scripts.sh ./
tar -czvf pro4_automation.tgz $NEWEST_BINARY templates/ dml_case/ pro4_scripts.sh

scp pro4_automation.tgz $SRC_USER@$SRC_IP:$SRC_USER_PATH 

ssh -t -t $SRC_USER@$SRC_IP << EOF
rm -rf templates/ dml_case/ pro4_scripts.sh
tar -zxvf pro4_automation.tgz
sh pro4_scripts.sh $1
exit

EOF

exit
