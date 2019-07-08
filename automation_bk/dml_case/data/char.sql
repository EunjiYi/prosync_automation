insert into T_DATA_TYPE_CHAR (SELECT 'C', Lpad('C',20,'2'),Lpad('C',100,'3'),Lpad('C',2000,'4') FROM DUAL CONNECT BY LEVEL <=1000);
COMMIT;
UPDATE  T_DATA_TYPE_CHAR 
SET C1= 'c'
, C2= rpad('C',20,'2') 
, C3= rpad('C',100,'2')    
, C4= rpad('C',2000,'2');
commit;
DELETE FROM T_DATA_TYPE_CHAR;
commit;

insert into T_DATA_TYPE_CHAR (SELECT 'C', Lpad('C',20,'2'),Lpad('C',100,'3'),Lpad('C',2000,'4')  from dual);
commit;
