insert into test_21 select level, sysdate from dual connect by level <=100;
commit;
