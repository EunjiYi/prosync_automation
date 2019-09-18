public class DumpConfirm {
	public static void main(String[] args) {
		int i;
		double val = 0.0;

        if (Integer.valueOf(args[0], 16) < 100 && args.length > 2 ) {
			int pow, argCnt = Integer.parseInt(args[0]);
			if (argCnt == 1) {
				val += (Integer.valueOf(args[1], 16) - Integer.valueOf("80", 16));
			} else if (argCnt >= 2) {
				if (Integer.valueOf(args[1], 16) <= 62) {
					pow = Integer.valueOf("40", 16) - Integer.valueOf(args[1], 16);
				} else
					pow = Integer.valueOf(args[1], 16) - Integer.valueOf("BF", 16);
				for (i = 2; i <= argCnt; i++) {
					if (args[i].equals("f0"))
						continue;
					val += ((Integer.valueOf(args[i], 16) - Integer.valueOf("80", 16)) * Math.pow(100, pow - i));
				}
			} else {
				System.out.println("Not Supported");
			}

			if (val == (long) val)
				System.out.println(String.format("%d", (long) val));
			else
				System.out.println(String.format("%s", val));
		} else if (Integer.valueOf(args[0], 16) >= 100 && Integer.valueOf(args[0], 16) < 200) {
			int year = (Integer.valueOf(args[0], 16) - Integer.valueOf("64", 16)) * 100
					+ (Integer.valueOf(args[1], 16) - Integer.valueOf("64", 16));
			int month = Integer.valueOf(args[2], 16);
			int day = Integer.valueOf(args[3], 16);
			String datetime = String.format("%02d", Integer.valueOf(args[4], 16)) + ":"
					+ String.format("%02d", Integer.valueOf(args[5], 16)) + ":"
					+ String.format("%02d", Integer.valueOf(args[6], 16));
			String time_tmp = "0";
			for (i = 7; i < args.length; i++) {
				time_tmp += args[i];
			}
			int time = Integer.valueOf(time_tmp, 16) / 1000;
			System.out.println(String.format("%04d", year) + "/" + String.format("%02d", month) + "/"
					+ String.format("%02d", day) + " " + datetime + "." + String.format("%06d", time));

		} else {
			System.out.println("ex) java "+ Thread.currentThread().getStackTrace()[1].getClassName() + " LogDump");
            System.out.println("Number Type -> java " + Thread.currentThread().getStackTrace()[1].getClassName() + " 07 c3 8c a2 b8 8c a2 b8 1f a0");
System.out.println("Time Type -> java " + Thread.currentThread().getStackTrace()[1].getClassName() + " c7 c7 0c 1f 17 3b 3b 00 3b 9a c6 18");
		}
	}
}
