BEGIN
FOR i IN 1..100 LOOP
insert into test_33 values(i,SYSDATE);
END LOOP;
commit;
END;
/
