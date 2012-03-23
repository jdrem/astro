package net.remgant.astro;

public class Trig {
    static double degToRad(double d) {
        d %= 360.0;
        if (d < 0.0)
            d += 360.0;
        return (d / 360.0) * (2.0 * java.lang.Math.PI);
    }

    static double radToDeg(double d) {
        double e = (d / (2.0 * java.lang.Math.PI)) * 360.0;
        //e %= 360.0;
        //if (e < 0.0)
        // e = 360.0 - e;
        return rev(e);
    }

    static double sin(double x) {
        return java.lang.Math.sin(degToRad(x));
    }

    static double cos(double x) {
        return java.lang.Math.cos(degToRad(x));
    }

    static double acos(double x) {
        return radToDeg(java.lang.Math.acos(x));
    }

    static double atan2(double a, double b) {
        return radToDeg(java.lang.Math.atan2(a, b));
    }

    static double asin(double x) {
        return radToDeg(java.lang.Math.asin(x));
    }

    static double sqrt(double x) {
        return java.lang.Math.sqrt(x);
    }

    static double abs(double x) {
        return java.lang.Math.abs(x);
    }

    static double rev(double x) {
        return x - Math.floor(x / 360.0) * 360.0;
    }
}
