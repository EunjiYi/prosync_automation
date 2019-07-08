insert into test_37_1 select level, sysdate from dual connect by level <=100;
commit;

update test_37_1 set c2=sysdate;
commit;

delete test_37_1 where c1 != 1;
delete test_37_2 where c1 != 1;
commit;
