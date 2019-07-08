insert into /*+ parallel(test_12, 8) */  test_12 select level, sysdate from dual connect by level <=100;
commit;
insert into /*+ parallel(test_12, 8) */ test_12 select level, sysdate from dual connect by level <=100;
commit;
