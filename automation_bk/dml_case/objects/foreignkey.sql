insert into test_31_1 select level, 'pk success' from dual connect by level <=50;
insert into test_31_2 select level, 'fk success' from dual connect by level <=50;
insert into TEST_31_2 values(101, 'fk fail');
commit;

update test_31_1 set c2=sysdate;
commit;

delete test_31_2 where c1 != 1;
delete test_31_1 where c1 != 1;
commit;
