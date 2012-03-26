package net.remgant.astro;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

public class Time {
    static SimpleTimeZone utcZone;

    static {
        utcZone = new SimpleTimeZone(0, "UTC");
    }

    public static double getDayNumber(java.util.Calendar cal) {
        return getDayNumber(cal.getTime());
    }

    public static double getDayNumber(java.util.Date date) {

        Calendar cal = new GregorianCalendar(utcZone);
        cal.setTime(date);

        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int D = cal.get(Calendar.DAY_OF_MONTH);

        // System.out.println(y+" "+m+" "+D);
        int d = 367 * y - 7 * (y + (m + 9) / 12) / 4 + 275 * m / 9 + D - 730530;

        int h = cal.get(Calendar.HOUR_OF_DAY);
        int M = cal.get(Calendar.MINUTE);
        int s = cal.get(Calendar.SECOND);

        double UT = (double) h + (double) (M * 60 + s) / 3600.0;
        double dd = (double) d + UT / 24.0;

        return dd;
    }

    public static String getDayString(double d) {
        return "";
    }
}
