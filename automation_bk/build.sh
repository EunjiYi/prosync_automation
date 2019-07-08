#!/bin/bash

tbsql -s tibero/tmax@dbqa2 << EOF
INSERT INTO DBQA2.BUILD (SEQ, STATUS, PRIORITY, PLATFORM, PRODUCT, VERSION, HOTFIX,
REQ_DATE, REGIST_ID, REGIST_DATE, MODIFY_ID, MODIFY_DATE, COMPILE_MODE,
IS_SRC_DEL, IS_TBSYNC, IS_CONFLICT, PROJECT_NAME, SITE_NAME, IMS_NO, SAMPLER_RUN_YN)
values (DBQA2.SEQ_BUILD.nextval, 'G1301', 'G0403003', 'G0214', 'G0101002', 'G0103005',
'', sysdate, 'sangbok_heo', sysdate, 'sangbok_heo', sysdate,
'opt',1, 0, 0, '테스트 자동화', 'DQA1', 0, 'N');
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
    echo "빌드 완료";
    exit;
elif [ "$build_status" = "G1303" ]
then
    echo "빌드 실패";
    exit;
fi

sleep 300
done
