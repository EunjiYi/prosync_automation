insert into test_20 select level, sysdate from dual connect by level <=100;
commit;
