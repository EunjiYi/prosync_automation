insert into test_7 select level, 'soft', level from dual connect by level <=500;
insert into  test_7 select level, 'data', level from dual connect by level <=500;
commit;
