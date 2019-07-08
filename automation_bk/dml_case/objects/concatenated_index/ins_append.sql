insert into /*+ append(test_14, 8) */  test_14 select level, sysdate from dual connect by level <=100;
commit;
insert into /*+ append(test_14, 8) */ test_14 select level, sysdate from dual connect by level <=100;
commit;
