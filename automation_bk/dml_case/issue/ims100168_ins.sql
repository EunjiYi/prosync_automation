-- single row + empty_clob + success
insert into ims100168(c_num_pk, c_clob) values(11111, empty_clob())
/
commit
/
-- single row + empty_clob + rollback
insert into ims100168(c_num_pk, c_clob) values(22222, empty_clob())
/
rollback
/
commit
/
-- single row + empty_clob + constraint violation
insert into ims100168(c_num_pk, c_clob) values(11111, empty_clob())
/
commit
/
-- single row + NULL + success
insert into ims100168(c_num_pk, c_clob) values(33333, NULL)
/
commit
/
-- single row + NULL + rollback
insert into ims100168(c_num_pk, c_clob) values(44444, NULL)
/
rollback
/
commit
/
-- single row + NULL + constraint violation
insert into ims100168(c_num_pk, c_clob) values(33333, NULL)
/
commit
/

-- single row + success
insert into ims100168(c_num_pk) values(55555)
/
commit
/
-- single row + rollback
insert into ims100168(c_num_pk) values(66666)
/
rollback
/
commit
/
-- single row + constraint violation
insert into ims100168(c_num_pk) values(55555)
/
commit
/

