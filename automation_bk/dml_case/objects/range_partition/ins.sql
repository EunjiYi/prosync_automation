insert into test_1 select level, sysdate from dual connect by level <=100;
commit;
