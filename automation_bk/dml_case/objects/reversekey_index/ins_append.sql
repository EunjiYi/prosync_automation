insert into /*+ append(test_16, 8) */ test_16 select level, sysdate from dual connect by level <=100;
commit;
