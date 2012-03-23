package net.remgant.astro;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Date extends java.util.Date {
    public double getDayNumber() {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.setTime(this);

        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int D = cal.get(Calendar.DAY_OF_MONTH);

        double d = (double) (367 * y + -7 * (y + (m + 9) / 12) / 4 +
                275 * m / 9 + D - 730530);

        double h = (double) cal.get(Calendar.HOUR_OF_DAY);
        double M = (double) cal.get(Calendar.MINUTE);
        double s = (double) cal.get(Calendar.SECOND);

        d += h + (60 * M + s) / 3600;
        return d;
    }
}
