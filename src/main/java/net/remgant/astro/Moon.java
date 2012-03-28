package net.remgant.astro;

public class Moon extends Planet {
    public Moon() {
        N0 = 125.1228;
        N1 = 0.0529538083;
        i0 = 5.1454;
        w0 = 318.0634;
        w1 = 0.1643573223;
        a0 = 60.2666;
        a1 = 0.0;
        e0 = 0.054900;
        e1 = 0.0;
        M0 = 115.3654;
        M1 = 13.0649929509;
        name = "Moon";
        symbol = "\u263e";
    }


    void computePertubations(double d) {
        sun.computePos(d);
        double Ms = sun.M;
        double Mm = this.M;
        double Ls = sun.M + sun.w;
        double Lm = this.M + this.w + this.N;
        double D = Lm - Ls;
        double F = Lm - this.N;

        lonecl = lonecl
                - 1.274 * Trig.sin(Mm - 2.0 * D)        // (the Evection)
                + 0.658 * Trig.sin(2.0 * D)             // (the Variation)
                - 0.186 * Trig.sin(Ms)                // (the Yearly Equation)
                - 0.059 * Trig.sin(2.0 * Mm - 2.0 * D)
                - 0.057 * Trig.sin(Mm - 2.0 * D + Ms)
                + 0.053 * Trig.sin(Mm + 2.0 * D)
                + 0.046 * Trig.sin(2.0 * D - Ms)
                + 0.041 * Trig.sin(Mm - Ms)
                - 0.035 * Trig.sin(D)                // (the Parallactic Equation)
                - 0.031 * Trig.sin(Mm + Ms)
                - 0.015 * Trig.sin(2.0 * F - 2.0 * D)
                + 0.011 * Trig.sin(Mm - 4.0 * D);

        latecl = latecl
                - 0.173 * Trig.sin(F - 2.0 * D)
                - 0.055 * Trig.sin(Mm - F - 2.0 * D)
                - 0.046 * Trig.sin(Mm + F - 2.0 * D)
                + 0.033 * Trig.sin(F + 2.0 * D)
                + 0.017 * Trig.sin(2.0 * Mm + F);

        a = a
                - 0.58 * Trig.cos(Mm - 2.0 * D)
                - 0.46 * Trig.cos(2.0 * D);

        // recompute heliocentric position based on new lonecl
        xh = r * Trig.cos(lonecl) * Trig.cos(latecl);
        yh = r * Trig.sin(lonecl) * Trig.cos(latecl);
        zh = r * Trig.sin(latecl);
    }

    // need to override this to get a topocentric result
    public double getAltitude(double d, double UT, double lon, double lat) {
        computeAzAltData(d, UT, lon, lat);
        double alt = Trig.rev(Trig.asin(zhor));
        if (alt > 180.0)
            alt -= 360.0;
        // compute parallex
        double par = Trig.asin(1.0 / r);
        alt = alt - par * Trig.cos(alt);
        return alt;
    }

    double getSize(double d) {
        this.computePos(d);
        // return size in degrees
        return (1873.7 * 60.0 / r) / 3600.0;
    }

}

