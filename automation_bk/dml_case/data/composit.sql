insert into T_DATA_TYPE_COMPOSIT1 (number_co1,char_col1, char_col2 , var_col1 , var_col2, var_col3, clob_co1l , nclob_col1 , blob_col1 , long_col1 ,date_col1 ,timestamp_col1) values (1,'C',Rpad('C',2000,'2'), 'V' ,Rpad('C',2000,'2'),Rpad('C',4000,'2'), 'CLOB 데이타'||''||chr(0)||''||null||'CLOB 데이타' ||''||chr(0)||''||'CLOB 데이타'||' ' ,   'NCLOB 데이타'||''||chr(0)||''||null||'NCLOB 데이타' ||''||chr(0)||''||'NCLOB 데이타'||' ' ,  utl_raw.cast_to_raw('BLOB 데이타'||''||chr(0)||''||null||'BLOB 데이타' ||''||chr(0)||''||'BLOB 데이타'||' '), 'CLONG 데이타'||''||chr(0)||''||null||'CLONG 데이타' ||''||chr(0)||''||'CLONG 데이타'||' ' ,TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'),TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'));
commit;

insert into T_DATA_TYPE_COMPOSIT1 (number_co1,char_col1, char_col2 , var_col1 , var_col2, var_col3, clob_co1l , nclob_col1 , blob_col1 , long_col1 ,date_col1 ,timestamp_col1) values (2,'C',Rpad('C',2000,'2'), 'V' ,Rpad('C',2000,'2'),Rpad('C',4000,'2'), 'CLOB 데이타'||''||chr(0)||''||null||'CLOB 데이타' ||''||chr(0)||''||'CLOB 데이타'||' ' ,   'NCLOB 데이타'||''||chr(0)||''||null||'NCLOB 데이타' ||''||chr(0)||''||'NCLOB 데이타'||' ' ,  utl_raw.cast_to_raw('BLOB 데이타'||''||chr(0)||''||null||'BLOB 데이타' ||''||chr(0)||''||'BLOB 데이타'||' '), 'CLONG 데이타'||''||chr(0)||''||null||'CLONG 데이타' ||''||chr(0)||''||'CLONG 데이타'||' ' ,TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'),TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5'));
commit;

UPDATE  T_DATA_TYPE_COMPOSIT1
SET char_col1 ='c'
, char_col2 =lpad('C',2000,'2')
, var_col1 = 'v'
, var_col2 = lpad('C',2000,'2')
, var_col3 = lpad('C',4000,'2')
, clob_co1l  ='CLOB 데이타'||''||chr(0)||''||null||'CLOB 데이타' ||''||chr(0)||''||'CLOB 데이타'||' '
, nclob_col1 = 'NCLOB 데이타'||''||chr(0)||''||null||'NCLOB 데이타' ||''||chr(0)||''||'NCLOB 데이타'||' '
, blob_col1  = utl_raw.cast_to_raw('BLOB 데이타'||''||chr(0)||''||null||'BLOB 데이타' ||''||chr(0)||''||'BLOB 데이타'||' ')
, long_col1  = 'CLONG 데이타'||''||chr(0)||''||null||'CLONG 데이타' ||''||chr(0)||''||'CLONG 데이타'||' '
, date_col1 = TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5')
, timestamp_col1 =TO_TIMESTAMP('1999/01/01 00:00:01.000001','YYYY/MM/DD HH24:MI:SS.FF5');
commit;

DELETE FROM T_DATA_TYPE_COMPOSIT1 where number_co1 != 1;
commit;
