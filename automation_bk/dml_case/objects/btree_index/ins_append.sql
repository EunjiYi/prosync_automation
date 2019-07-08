insert into /*+ append(test_10, 8) */  test_10 select level, sysdate from dual connect by level <=1000;
commit;
insert into /*+ append(test_10, 8) */ test_10 select level, sysdate from dual connect by level <=1000;
commit;
