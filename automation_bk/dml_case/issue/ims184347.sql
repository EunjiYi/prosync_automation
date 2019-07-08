insert into ims184347 select dbms_random.value(-999999999,9999999999)  from dual connect by level <=100000;
commit;
