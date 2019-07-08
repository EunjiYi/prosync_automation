-- single row + empty_clob + success
update ims100168 set c_var = 'update_SES', c_clob = empty_clob()
where c_num_pk = 11111
/
commit
/
-- single row + empty_clob + rollback
update ims100168 set c_var = 'update_SER', c_clob = empty_clob()
where c_num_pk = 11111
/
rollback
/
commit
/
-- single row + empty_clob + constraint violation
update ims100168 set c_var = 'update_SEC', c_clob = empty_clob(), c_num_pk = 33333 
where c_num_pk = 11111
/
commit
/
-- single row + NULL + success
update ims100168 set c_var = 'update_SNS', c_clob = NULL
where c_num_pk = 11111
/
commit
/
-- single row + NULL + rollback
update ims100168 set c_var = 'update_SNR', c_clob = NULL
where c_num_pk = 11111
/
rollback
/
commit
/
-- single row + NULL + constraint violation
update ims100168 set c_var = 'update_SNC', c_num_pk = 33333, c_clob = NULL
where c_num_pk = 11111
/
commit
/

-- single row + success
update ims100168 set c_var = 'update_SS'
where c_num_pk = 11111
/
commit
/
-- single row + rollback
update ims100168 set c_var = 'update_SR'
where c_num_pk = 11111
/
rollback
/
commit
/
-- single row + constraint violation
update ims100168 set c_var = 'update_SC', c_num_pk = 55555 
where c_num_pk in (11111)
/
commit
/
-- range row + empty_clob + success
update ims100168 set c_var = 'update_RES', c_clob = empty_clob()
/
commit
/
-- range row + empty_clob + rollback
update ims100168 set c_var = 'update_RER', c_clob = empty_clob()
/
rollback
/
commit
/
-- range row + empty_clob + constraint violation
update ims100168 set c_var = 'update_REC', c_clob = empty_clob(), c_num_pk = 55555
where c_num_pk in (11111, 33333)
/
commit
/
-- range row + NULL + success
update ims100168 set c_var = 'update_RNS', c_clob = NULL
/
commit
/
-- range row + NULL + rollback
update ims100168 set c_var = 'update_RNR', c_clob = NULL
/
rollback
/
commit
/
-- range row + NULL + constraint violation
update ims100168 set c_var = 'update_RNC', c_num_pk = 55555, c_clob = NULL
where c_num_pk in (11111, 33333)
/
commit
/

-- range row + success
update ims100168 set c_var = 'update_RNS'
/
commit
/
-- range row + rollback
update ims100168 set c_var = 'update_RNR'
/
rollback
/
commit
/
-- range row + constraint violation
update ims100168 set c_var = 'update_RNC', c_num_pk = 55555
where c_num_pk in (11111, 33333)
/
commit
/
