delete /*+ append(test_18, 8) */  test_18 where c1 != 1;
commit;
