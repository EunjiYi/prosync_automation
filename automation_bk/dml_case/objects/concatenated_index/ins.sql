insert into test_14 select level, sysdate from dual connect by level <=100;
commit;
