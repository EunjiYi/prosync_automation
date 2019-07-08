insert into T_DATA_TYPE_VARCHAR (SELECT 'C', Lpad('C',20,'2'),Lpad('C',1000,'3'),Lpad('C',4000,'4') FROM DUAL CONNECT BY LEVEL <=100);
COMMIT;
UPDATE  T_DATA_TYPE_VARCHAR 
SET C1= 'v'
, C2= rpad('C',20,'2') 
, C3= rpad('C',1000,'2')    
, C4= rpad('C',4000,'2');
commit;
DELETE FROM T_DATA_TYPE_VARCHAR;
commit;
insert into T_DATA_TYPE_VARCHAR (SELECT 'C', Lpad('C',20,'2'),Lpad('C',1000,'3'),Lpad('C',4000,'4') FROM DUAL);
commit;
