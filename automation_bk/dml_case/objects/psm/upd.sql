BEGIN
FOR i IN 1..100 LOOP
update test_33 set c1=c1+100 where c1=i;
END LOOP;
commit;
END;
/
