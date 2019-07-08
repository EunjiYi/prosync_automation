insert into T_DATA_TYPE_DATE (select sysdate, sysdate, sysdate, sysdate  FROM DUAL CONNECT BY LEVEL <=100);
COMMIT;
UPDATE  T_DATA_TYPE_DATE 
SET C1= SYSDATE
, C2= SYSDATE 
, C3= SYSDATE
, C4= SYSDATE;
commit;
DELETE FROM T_DATA_TYPE_DATE;
commit;
insert into T_DATA_TYPE_DATE (select sysdate, sysdate, sysdate, sysdate  FROM DUAL);
commit;
