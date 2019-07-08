insert into TEST_36_1 select level, sysdate from dual connect by level <=100;
insert into TEST_36_2 select * from TEST_36_1;
commit;
update test_36_1 set c1=c1+50;
update test_36_2 t2 set c2= c2 where c1 in (select c1 from test_36_1);
commit;
delete test_36_1 where c1 != 51;
delete test_36_2 where c1 != 51;
commit;
