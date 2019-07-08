insert into test_28 select level, 'uk success' from dual connect by level <=100;
insert into test_28 values(1, 'uk fail');
commit;

update test_28 set c2=sysdate;
commit;

delete test_28 where c1 != 1;
commit;
