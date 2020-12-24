package net.remgant.astro;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class Time {

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
            return (double) d + UT / 24.0;
        }
        return d;
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
