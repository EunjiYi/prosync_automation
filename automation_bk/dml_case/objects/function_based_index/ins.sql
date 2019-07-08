insert into test_18 select level, 'a' from dual connect by level <=100;
commit;
