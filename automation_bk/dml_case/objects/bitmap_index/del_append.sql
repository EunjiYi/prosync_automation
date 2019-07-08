delete /*+ append(test_12, 8) */ test_12 where c1 != 101;
commit;
