update TEST_3 set C1=C1 + 12, C2='HASH1_update' where C2='HASH1';
update TEST_3 set C1=C1 + 13, C2='HASH2_update' where C2='HASH2';
commit;
