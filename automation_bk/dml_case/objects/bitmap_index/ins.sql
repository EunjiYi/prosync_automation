insert into test_12 select level, sysdate from dual connect by level <=100;
commit;
