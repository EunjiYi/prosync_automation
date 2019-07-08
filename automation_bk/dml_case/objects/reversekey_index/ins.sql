insert into test_16 select level, sysdate from dual connect by level <=100;
commit;
