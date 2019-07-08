insert into test_27 select level, 'pk success' from dual connect by level <=100;
insert into test_27 values(1, 'pk fail');
commit;

update test_27 set c2=sysdate;
commit;

delete test_27 where c1 != 1;
commit;
