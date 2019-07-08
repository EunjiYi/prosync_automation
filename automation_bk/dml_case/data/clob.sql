insert into test_22 select level, RPAD('a',999,chr(65)) from dual connect by level <=100;
commit;
update test_22 set c2= rpad('b',999,'2');
commit;
delete test_22 where c1 != 1;
commit;
