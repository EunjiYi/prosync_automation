insert into ims147068 values(0, 'CLOB ����Ÿ'||''||chr(0)||''||null||'CLOB ����Ÿ' ||''||chr(0)||''||'CLOB ����Ÿ'||'' , 'NCLOB ����Ÿ'||''||chr(0)||''||null||'NCLOB ����Ÿ'||''||chr(0)||''||'NCLOB ����Ÿ'||' ' , utl_raw.cast_to_raw('BLOB ����Ÿ'||''||chr(0)||''||null||'BLOB ����Ÿ' ||''||chr(0)||''||'BLOB ����Ÿ'||' ') );
commit;
 
insert into ims147068 values(1, 'CLOB ����Ÿ'||''||chr(0)||''||null||'CLOB ����Ÿ' ||''||chr(0)||''||'CLOB ����Ÿ'||'' , 'NCLOB ����Ÿ'||''||chr(0)||''||null||'NCLOB ����Ÿ'||''||chr(0)||''||'NCLOB ����Ÿ'||' ' , utl_raw.cast_to_raw('BLOB ����Ÿ'||''||chr(0)||''||null||'BLOB ����Ÿ' ||''||chr(0)||''||'BLOB ����Ÿ'||' ') );
commit;
insert into ims147068 values(2, 'CLOB ����Ÿ'||''||chr(0)||''||null||'CLOB ����Ÿ' ||''||chr(0)||''||'CLOB ����Ÿ'||'' , 'NCLOB ����Ÿ'||''||chr(0)||''||null||'NCLOB ����Ÿ'||''||chr(0)||''||'NCLOB ����Ÿ'||' ' , utl_raw.cast_to_raw('BLOB ����Ÿ'||''||chr(0)||''||null||'BLOB ����Ÿ' ||''||chr(0)||''||'BLOB ����Ÿ'||' ') );
commit;
delete ims147068 where C1=2;
commit;
