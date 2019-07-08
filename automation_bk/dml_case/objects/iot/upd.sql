update test_26 set name=rpad('ab',30,chr(66)), contents=rpad('ab',4000,chr(66));
commit;
