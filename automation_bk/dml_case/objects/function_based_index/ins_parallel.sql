insert into /*+ parallel(test_18, 8) */ test_18 select level, 'a' from dual connect by level <=100;
commit;
