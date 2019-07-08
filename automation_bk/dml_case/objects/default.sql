insert into test_30(c1) select level from dual connect by level <=100;
insert into TEST_30 values(101, 'default test');
commit;

update test_30 set c2=sysdate;
commit;

delete test_30 where c1 != 1;
commit;
