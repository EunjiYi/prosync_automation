insert into /*+ append(test_12, 8) */  test_12 select level, sysdate from dual connect by level <=100;
commit;
insert into /*+ append(test_12, 8) */ test_12 select level, sysdate from dual connect by level <=100;
commit;
