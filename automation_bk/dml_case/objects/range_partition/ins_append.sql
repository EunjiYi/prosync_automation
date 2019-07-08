insert into /*+ append(test_1, 8) */ test_1 select level, sysdate from dual connect by level <=100;
commit;
