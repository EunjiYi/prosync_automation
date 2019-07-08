BEGIN
FOR i IN 101..199 LOOP
delete test_33 where C1 = i;
END LOOP;
commit;
END;
/
