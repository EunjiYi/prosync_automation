insert into T_DATA_TYPE_COMPOSIT1 (number_co1,char_col1, char_col2 , var_col1 , var_col2, var_col3, clob_co1l , nclob_col1 , blob_col1 , long_col1 ,date_col1 ,timestamp_col1) values (1,'C',Rpad('C',2000,'2'), 'V' ,Rpad('C',2000,'2'),Rpad('C',4000,'2'), 'CLOB ����Ÿ'||''||chr(0)||''||null||'CLOB ����Ÿ' ||''||chr(0)||''||'CLOB ����Ÿ'||' ' ,   'NCLOB ����Ÿ'||''||chr(0)||''||null||'NCLOB ����Ÿ' ||''||chr(0)||''||'NCLOB ����Ÿ'||' ' ,  utl_raw.cast_to_raw('BLOB ����Ÿ'||''||chr(0)||''||null||'BLOB ����Ÿ' ||''||chr(0)||''||'BLOB ����Ÿ'||' '), 'CLONG ����Ÿ'||''||chr(0)||''||null||'CLONG ����Ÿ' ||''||chr(0)||''||'CLONG ����Ÿ'||' ' ,TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'),TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'));
commit;

insert into T_DATA_TYPE_COMPOSIT1 (number_co1,char_col1, char_col2 , var_col1 , var_col2, var_col3, clob_co1l , nclob_col1 , blob_col1 , long_col1 ,date_col1 ,timestamp_col1) values (2,'C',Rpad('C',2000,'2'), 'V' ,Rpad('C',2000,'2'),Rpad('C',4000,'2'), 'CLOB ����Ÿ'||''||chr(0)||''||null||'CLOB ����Ÿ' ||''||chr(0)||''||'CLOB ����Ÿ'||' ' ,   'NCLOB ����Ÿ'||''||chr(0)||''||null||'NCLOB ����Ÿ' ||''||chr(0)||''||'NCLOB ����Ÿ'||' ' ,  utl_raw.cast_to_raw('BLOB ����Ÿ'||''||chr(0)||''||null||'BLOB ����Ÿ' ||''||chr(0)||''||'BLOB ����Ÿ'||' '), 'CLONG ����Ÿ'||''||chr(0)||''||null||'CLONG ����Ÿ' ||''||chr(0)||''||'CLONG ����Ÿ'||' ' ,TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'),TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'));
commit;

UPDATE  T_DATA_TYPE_COMPOSIT1
SET char_col1 ='c'
, char_col2 =lpad('C',2000,'2')
, var_col1 = 'v'
, var_col2 = lpad('C',2000,'2')
, var_col3 = lpad('C',4000,'2')
, clob_co1l  ='CLOB ����Ÿ'||''||chr(0)||''||null||'CLOB ����Ÿ' ||''||chr(0)||''||'CLOB ����Ÿ'||' '
, nclob_col1 = 'NCLOB ����Ÿ'||''||chr(0)||''||null||'NCLOB ����Ÿ' ||''||chr(0)||''||'NCLOB ����Ÿ'||' '
, blob_col1  = utl_raw.cast_to_raw('BLOB ����Ÿ'||''||chr(0)||''||null||'BLOB ����Ÿ' ||''||chr(0)||''||'BLOB ����Ÿ'||' ')
, long_col1  = 'CLONG ����Ÿ'||''||chr(0)||''||null||'CLONG ����Ÿ' ||''||chr(0)||''||'CLONG ����Ÿ'||' '
, date_col1 = TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5')
, timestamp_col1 =TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5');
commit;

DELETE FROM T_DATA_TYPE_COMPOSIT1 where number_co1 != 1;
commit;
