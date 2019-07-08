insert into /*+ parallel(test_3, 8) */ TEST_3 select level, 'HASH' from dual connect by level <=100;
commit;
