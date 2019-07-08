insert into /*+ parallel(test_7, 8) */ test_7 select level, 'soft', level from dual connect by level <=500;
insert into /*+ parallel(test_7, 8) */ test_7 select level, 'data', level from dual connect by level <=500;
commit;
