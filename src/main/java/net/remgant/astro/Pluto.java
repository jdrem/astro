package net.remgant.astro;

public class Pluto extends Planet {
    public Pluto() {
        name = "Pluto";
    }

    void computePos(double d) {
        double S = 50.03 + 0.033459652 * d;
        double P = 238.95 + 0.003968789 * d;

        lonecl = 238.9508 + 0.00400703 * d
                - 19.799 * Trig.sin(P) + 19.848 * Trig.cos(P)
                + 0.897 * Trig.sin(2.0 * P) - 4.956 * Trig.cos(2.0 * P)
                + 0.610 * Trig.sin(3.0 * P) + 1.211 * Trig.cos(3.0 * P)
                - 0.341 * Trig.sin(4.0 * P) - 0.190 * Trig.cos(4.0 * P)
                + 0.128 * Trig.sin(5.0 * P) - 0.034 * Trig.cos(5.0 * P)
                - 0.038 * Trig.sin(6.0 * P) + 0.031 * Trig.cos(6.0 * P)
                + 0.020 * Trig.sin(S - P) - 0.010 * Trig.cos(S - P);

        latecl = -3.9082
                - 5.453 * Trig.sin(P) - 14.975 * Trig.cos(P)
                + 3.527 * Trig.sin(2.0 * P) + 1.673 * Trig.cos(2.0 * P)
                - 1.051 * Trig.sin(3.0 * P) + 0.328 * Trig.cos(3.0 * P)
                + 0.179 * Trig.sin(4.0 * P) - 0.292 * Trig.cos(4.0 * P)
                + 0.019 * Trig.sin(5.0 * P) + 0.100 * Trig.cos(5.0 * P)
                - 0.031 * Trig.sin(6.0 * P) - 0.026 * Trig.cos(6.0 * P)
                + 0.011 * Trig.cos(S - P);

        r = 40.72
                + 6.68 * Trig.sin(P) + 6.90 * Trig.cos(P)
                - 1.18 * Trig.sin(2.0 * P) - 0.03 * Trig.cos(2.0 * P)
                + 0.15 * Trig.sin(3.0 * P) - 0.14 * Trig.cos(3.0 * P);

        xh = r * Trig.cos(lonecl) * Trig.cos(latecl);
        yh = r * Trig.sin(lonecl) * Trig.cos(latecl);
        zh = r * Trig.sin(latecl);

        sun.computePos(d);
        double xs = sun.r * Trig.cos(sun.lonsun);
        double ys = sun.r * Trig.sin(sun.lonsun);

        // compute the geocentric position
        xg = xh + xs;
        yg = yh + ys;
        zg = zh;

        // now convert to equatorial coordinates
        double ecl = 23.4393 - 3.563E-7 * d;
        xe = xg;
        ye = yg * Trig.cos(ecl) - zg * Trig.sin(ecl);
        ze = yg * Trig.sin(ecl) + zg * Trig.cos(ecl);

        // compute the right acension and declination
        RA = Trig.atan2(ye, xe);
        decl = Trig.atan2(ze, Math.sqrt(xe * xe + ye * ye));
        while (decl >= 360.0)
            decl -= 360.0;
        // compute the geocentric distance
        rg = Math.sqrt(xe * xe + ye * ye + ze * ze);
    }

    void computePertubations(double d) {

    }
}
