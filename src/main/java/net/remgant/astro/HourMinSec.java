package net.remgant.astro;

public class HourMinSec {
    double dVal;
    int hVal;
    int mVal;
    double sVal;

    public HourMinSec(double v) {
        dVal = Trig.rev(v);
        hVal = (int) ((dVal / 360.0) * 24.0);
        mVal = (int) (((dVal - (double) hVal * 15.0) / 60.0) * 60.0);
        sVal = ((dVal - (double) hVal * 15.0 - (double) mVal * 0.5) / 3600.0) * 60.0;
    }

    public HourMinSec(int h, int m, double s) {
        hVal = h;
        mVal = m;
        sVal = s;
        dVal = (((double) h / 24.0) * 3600.0 + ((double) m / 60.0) * 60.0 + s)
                / 3600.0;
    }

    public double toDouble() {
        return dVal;
    }

    public String toString() {
        return hVal + "h " + mVal + "m " + sVal + "s";
    }
}
