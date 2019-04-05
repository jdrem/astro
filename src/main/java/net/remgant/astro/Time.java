package net.remgant.astro;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {
    static SimpleTimeZone utcZone;

    static {
        utcZone = new SimpleTimeZone(0, "UTC");
    }

    public static double getDayNumber(TemporalAccessor temporalAccessor) {
        int y = temporalAccessor.get(ChronoField.YEAR);
        int m = temporalAccessor.get(ChronoField.MONTH_OF_YEAR);
        int D = temporalAccessor.get(ChronoField.DAY_OF_MONTH);

        int d = 367 * y - 7 * (y + (m + 9) / 12) / 4 + 275 * m / 9 + D - 730530;

        if (temporalAccessor.isSupported(ChronoField.HOUR_OF_DAY)) {
            int h = temporalAccessor.get(ChronoField.HOUR_OF_DAY);
            int M = temporalAccessor.get(ChronoField.MINUTE_OF_HOUR);
            int s = temporalAccessor.get(ChronoField.SECOND_OF_MINUTE);

            double UT = (double) h + (double) (M * 60 + s) / 3600.0;
            double dd = (double) d + UT / 24.0;
            return dd;
        }
        return d;
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

    private final static Pattern htdPattern = Pattern.compile("(\\d+)[Hh]\\s*(\\d+)[Mm]\\s*(\\d+)[Ss]");

    public static double hoursToDegrees(String in) {
        Matcher matcher = htdPattern.matcher(in);
        if (!matcher.matches())
            throw new RuntimeException("can't parse: " + in);
        double h = Double.parseDouble(matcher.group(1));
        double m = Double.parseDouble(matcher.group(2));
        double s = Double.parseDouble(matcher.group(3));

        return ((h + (m / 60.0) + (s / 3600.0)) / 24.0) * 360.0;
    }

    private final static Pattern dmsPattern = Pattern.compile("(-?\\d+)[o\\u00B0]\\s*(?:(\\d+)'\\s*(?:(\\d+)\"))");

    public static double degMinSecToDegDec(String in) {
        Matcher matcher = dmsPattern.matcher(in);
        if (!matcher.matches())
            throw new RuntimeException(("can't parse: " + in));
        double d = Double.parseDouble(matcher.group(1));
        double m = matcher.group(2) != null ? Double.parseDouble(matcher.group(2)) : 0.0;
        double s = matcher.group(3) != null ? Double.parseDouble(matcher.group(3)) : 0.0;
        return d + m / 60.0 + s / 3600.0;

    }
}
