insert into ims147068 values(0, 'CLOB 데이타'||''||chr(0)||''||null||'CLOB 데이타' ||''||chr(0)||''||'CLOB 데이타'||'' , 'NCLOB 데이타'||''||chr(0)||''||null||'NCLOB 데이타'||''||chr(0)||''||'NCLOB 데이타'||' ' , utl_raw.cast_to_raw('BLOB 데이타'||''||chr(0)||''||null||'BLOB 데이타' ||''||chr(0)||''||'BLOB 데이타'||' ') );
commit;
 
insert into ims147068 values(1, 'CLOB 데이타'||''||chr(0)||''||null||'CLOB 데이타' ||''||chr(0)||''||'CLOB 데이타'||'' , 'NCLOB 데이타'||''||chr(0)||''||null||'NCLOB 데이타'||''||chr(0)||''||'NCLOB 데이타'||' ' , utl_raw.cast_to_raw('BLOB 데이타'||''||chr(0)||''||null||'BLOB 데이타' ||''||chr(0)||''||'BLOB 데이타'||' ') );
commit;
insert into ims147068 values(2, 'CLOB 데이타'||''||chr(0)||''||null||'CLOB 데이타' ||''||chr(0)||''||'CLOB 데이타'||'' , 'NCLOB 데이타'||''||chr(0)||''||null||'NCLOB 데이타'||''||chr(0)||''||'NCLOB 데이타'||' ' , utl_raw.cast_to_raw('BLOB 데이타'||''||chr(0)||''||null||'BLOB 데이타' ||''||chr(0)||''||'BLOB 데이타'||' ') );
commit;
delete ims147068 where C1=2;
commit;
