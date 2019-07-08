insert into test_10 select level, sysdate from dual connect by level <=1000;
commit;
