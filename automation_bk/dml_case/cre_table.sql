CREATE TABLE TEST_1(C1 number, C2 varchar(4000), constraint pk_test_1 primary key(c1))
partition by range(C1)
(
    partition TEST_1_PART1 values less than (30),
    partition TEST_1_PART2 values less than (70),
    partition TEST_1_PART3 values less than (maxvalue)
);

CREATE TABLESPACE TSTEST_3_1 DATAFILE
'TSTEST_3_1' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLESPACE TSTEST_3_2 DATAFILE
'TSTEST_3_2' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLE TEST_3(C1 number, C2 varchar(4000), constraint pk_TEST_3 primary key(c1))
partition by hash (C1)
(
    partition TEST_1_HASH1 tablespace TSTEST_3_1,
    partition TEST_1_HASH2 tablespace TSTEST_3_2
);

CREATE TABLESPACE TSTEST_5_1 DATAFILE
'TSTEST_5_1' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;

CREATE TABLESPACE TSTEST_5_2 DATAFILE
'TSTEST_5_2' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;

CREATE TABLE TEST_5(C1 number, C2 varchar(4000))
partition by list (C2)
(
    partition PT_TEST_5_1 values ('soft') tablespace TSTEST_5_1,
    partition PT_TSTEST_5_2 values ('data') tablespace TSTEST_5_2
);

CREATE TABLESPACE TSTEST_7_1 DATAFILE
'TSTEST_7' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLESPACE TSTEST_7_2 DATAFILE
'TSTEST_7_2' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLE TEST_7(C1 number, C2 varchar(4000), c3 number)
partition by range (c1)
subpartition by list (c2)
(PARTITION TEST_7_PART1 values less than (26)
 (SUBPARTITION TEST_7_LIST1 values ('soft') tablespace TSTEST_7_1,
 SUBPARTITION TEST_7_LIST2 values ('data') tablespace  TSTEST_7_2),
PARTITION TEST_7_PART2 values less than (maxvalue)
 (SUBPARTITION TEST_7_LIST3 values ('soft') tablespace  TSTEST_7_1,
 SUBPARTITION TEST_7_LIST4 values ('data') tablespace  TSTEST_7_2)
);

CREATE TABLE TEST_10 (C1 number, C2 varchar(4000));
create index idx_c1_test10 on test_10(c1);

CREATE TABLE TEST_12(C1 number, C2 varchar(4000));
create bitmap index idx_test_12_c1_bit on test_12(c1);

CREATE TABLE TEST_14(C1 number, C2 varchar(4000));
create unique index idx_c1_c2_14 ON test_14(c1, c2);

CREATE TABLE TEST_16(C1 number, C2 varchar(4000));
create index reverse_idx ON TEST_16(c1) reverse;

CREATE TABLE TEST_18(C1 number, C2 varchar(4000));
create index func_idx on test_18(UPPER(C2));

CREATE TABLESPACE TSTEST_20_1 DATAFILE
'TEST_20_1' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLESPACE TSTEST_20_2 DATAFILE
'TEST_20_2' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLE TEST_20(C1 number, C2 varchar(4000))
PARTITION BY RANGE (c1)
(PARTITION test_20_part_1 VALUES LESS THAN (50) TABLESPACE tstest_20_1,
PARTITION test_20_part_2 VALUES LESS THAN (maxvalue) TABLESPACE tstest_20_2);
CREATE INDEX test_20_idx ON test_20(c1) LOCAL
(PARTITION idx_sales_p1 TABLESPACE tstest_20_1,
PARTITION idx_sales_p2 TABLESPACE tstest_20_2);

CREATE TABLESPACE TSTEST_21_1 DATAFILE
'TSTEST_21_1' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLESPACE TSTEST_21_2 DATAFILE
'TSTEST_21_2' SIZE 10M AUTOEXTEND ON NEXT 4M MAXSIZE UNLIMITED;
CREATE TABLE TEST_21(C1 number, C2 varchar(4000));
CREATE INDEX test_21_idx ON test_21(c1)
GLOBAL PARTITION BY RANGE (c1)
(PARTITION idx_test_21_c1_p1 VALUES LESS THAN (50) TABLESPACE tstest_21_1,
PARTITION idx_test_21_c1_p2 VALUES LESS THAN (maxvalue) TABLESPACE tstest_21_2);

CREATE TABLE TEST_22 (C1 number, C2 CLOB, constraint pk_test_22 primary key(c1));

CREATE TABLE TEST_23 (C1 number, C2 BLOB, constraint pk_test_23 primary key(c1));

CREATE TABLE TEST_24 (C1 number, C2 NCLOB, constraint pk_test_24 primary key(c1));

CREATE TABLE TEST_25 (C1 number, C2 LONG RAW, constraint pk_test_25 primary key(c1));

CREATE TABLE TEST_27 (C1 number, C2 varchar(4000), constraint pk_test_27 primary key(c1));

CREATE TABLE TEST_28 (C1 number, C2 varchar(4000), constraint uk_test_28 unique(c1));

CREATE TABLE TEST_29 (C1 number NOT NULL, C2 varchar(4000));

CREATE TABLE TEST_30 (C1 number, C2 varchar(4000) default sysdate);

CREATE TABLE TEST_31_1 (C1 number, C2 varchar(4000), constraint pk_test_31_1 primary key(c1));
CREATE TABLE TEST_31_2 (C1 number, C2 varchar(4000), constraint fk_test_31_2 foreign key  (c1) references test_31_1(c1));

CREATE TABLE TEST_32 (C1 number, C2 varchar(4000),constraint check_test_32 check( c2 IN('0','1')));

CREATE TABLE TEST_33 (C1 number, C2 varchar(4000), constraint pk_test_33 primary key(c1));

CREATE TABLE TEST_36_1 (C1 number, C2 varchar(4000));
CREATE TABLE TEST_36_2 (C1 number, C2 varchar(4000));

CREATE TABLE T_DATA_TYPE_CHAR (C1 CHAR(1),C2 CHAR(20),C3 CHAR(100) , C4 CHAR(2000));

CREATE TABLE T_DATA_TYPE_VARCHAR (C1 VARCHAR(1),C2 VARCHAR(20),C3 VARCHAR(1000) , C4 VARCHAR(4000));

CREATE TABLE T_DATA_TYPE_DATE (C1 DATE,C2 DATE,C3 DATE , C4 DATE );

CREATE TABLE T_DATA_TYPE_TIMESTAMP (C1 TIMESTAMP,C2 TIMESTAMP,C3 TIMESTAMP , C4 TIMESTAMP default systimestamp );

CREATE TABLE T_DATA_TYPE_COMPOSIT1 (
  number_co1 number
, char_col1 char(1)
, char_col2 char(2000)
, var_col1 varchar2(1)
, var_col2 varchar2(2000)
, var_col3 varchar2(4000)
, clob_co1l clob
, nclob_col1 nclob
, blob_col1 blob
, long_col1 long
, date_col1 date default sysdate
, timestamp_col1 timestamp default systimestamp
, constraint pk_T_DATA_TYPE_COMPOSIT1 primary key(number_co1)
 );

CREATE TABLE T_CHARACTER (
char_col1 char(1)
, char_col2 char(10)
, char_col3 char(2000)
 );

ALTER TABLE TEST_3 ENABLE ROW MOVEMENT;
ALTER TABLE TEST_5 ENABLE ROW MOVEMENT;
ALTER TABLE TEST_7 ENABLE ROW MOVEMENT;

