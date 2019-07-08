delete /*+ append(test_3, 8) */ TEST_3 where c1 != 1;
commit;
