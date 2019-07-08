insert into /*+ append(TEST_5, 8) */  TEST_5  select level, 'soft' from dual connect by level <=500;
insert into /*+ append(TEST_5, 8) */ TEST_5  select level, 'data' from dual connect by level <=500;
commit;
