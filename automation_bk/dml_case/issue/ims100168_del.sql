-- delete & insert
delete from ims100168
/
insert into ims100168(c_num_pk, c_clob) values(11111, empty_clob())
/
insert into ims100168(c_num_pk, c_clob) values(33333, NULL)
/
insert into ims100168(c_num_pk) values(55555)
/
commit
/
