insert into test_32 select level, '0' from dual connect by level <=50;
insert into test_32 select level, '1' from dual connect by level <=50;
insert into TEST_32 values(101, 'check fail');
commit;

update test_32 set c1=c1+100;
commit;

delete test_32 where c1 != 101;
commit;
