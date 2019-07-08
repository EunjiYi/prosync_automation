insert into test_29 select level, 'not null success' from dual connect by level <=100;
insert into test_29 values(NULL, 'not null fail');
commit;

update test_29 set c2=sysdate;
commit;

delete test_29 where c1 != 1;
commit;
