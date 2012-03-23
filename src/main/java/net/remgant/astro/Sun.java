package net.remgant.astro;

public class Sun extends MovingObject {
    public Sun() {
        name = "Sun";
        N0 = N1 = 0.0;
        i0 = i1 = 0.0;
        w0 = 282.9404;
        w1 = 4.70935E-5;
        a = 1.0;
        e0 = 0.016709;
        e1 = -1.151E-9;
        M0 = 356.0470;
        M1 = 0.9856002585;
    }

    double lonsun;

    /* The sun has a slightly simpler computation for getting the
    position, so it overrides this method.
    */
    void computePos(double d) {
        N = 0.0;
        i = 0.0;
        w = w0 + w1 * d;
        // System.out.println("ws = "+ws);
        e = e0 + e1 * d;
        // System.out.println("es = "+es);
        M = Trig.rev(M0 + M1 * d);
        // System.out.println("Ms = "+Ms);

        E = M + e * (180 / Math.PI) * Trig.sin(M) *
                (1.0 + e * Trig.cos(M));
        // System.out.println("Es = "+Es);

        xv = Trig.cos(E) - e;
        // System.out.println("xvs = "+xvs);
        yv = Math.sqrt(1.0 - e * e) * Trig.sin(E);
        // System.out.println("yvs = "+yvs);
        v = Trig.atan2(yv, xv);
        // System.out.println("vs = "+vs);
        r = Math.sqrt(xv * xv + yv * yv);
        // System.out.println("rs = "+rs);
        lonsun = v + w;
        // System.out.println("lonsun = "+lonsun);
        xh = r * Trig.cos(lonsun);
        // System.out.println("xs = "+xs);
        yh = r * Trig.sin(lonsun);
        // System.out.println("ys = "+ys);
        zh = 0.0;

        double ecl = 23.4394 - 3.563E-7 * d;
        // System.out.println("ecl = "+ecl);
        xe = xh;
        // System.out.println("xes = "+xes);
        ye = yh * Trig.cos(ecl);
        // System.out.println("yes = "+yes);
        ze = yh * Trig.sin(ecl);
        // System.out.println("zes = "+zes);
        RA = Trig.atan2(ye, xe);
        // System.out.println("RAs = "+RAs);
        decl = Trig.atan2(ze, Math.sqrt(xe * xe + ye * ye));
        //while (decl >= 360.0)
        // decl -= 360.0;
        decl = Trig.rev(decl);
        if (decl > 180.0)
            decl = decl - 360.0;
        // System.out.println("decls = "+decls);
    }

    public double getRA(double d) {
        computePos(d);
        return RA;
    }

    public double getDecl(double d) {
        computePos(d);
        return decl;
    }

    double L;
    double GMST0;
    double h;
    double preLHA;
    double LHA;
    double UT_Sun_in_south;

    void computeSetData(double lon, double lat, double d,
                        double tzOff) {
        // System.out.println("decls = "+decls);
        // System.out.println("Ms = "+Ms);
        // System.out.println("ws = "+ws);
        L = M + w;
        if (L > 360.0)
            L %= 360.0;
        // System.out.println("Ls = "+Ls);
        // GMST0 = L + 180.0;
        GMST0 = Trig.rev(L + 180.0);
        // System.out.println("GMST0 = "+GMST0);
        // System.out.println("RA = "+RA);
        UT_Sun_in_south = (RA - GMST0 - lon) / 15.0;
        if (UT_Sun_in_south > 24.0)
            UT_Sun_in_south %= 24.0;
        else if (UT_Sun_in_south < 0.0)
            UT_Sun_in_south += 24.0;
        // System.out.println("UT_Sun_in_south = "+UT_Sun_in_south);
        h = -0.833;
        // System.out.println("h = "+h);
        preLHA = (Trig.sin(h) - Trig.sin(lat) * Trig.sin(decl)) / (Trig.cos(lat) * Trig.cos(decl));
        // System.out.println("preLHA = "+preLHA);
        LHA = Trig.acos(preLHA) / 15.0;
        // System.out.println("LHA = "+LHA);
    }

    public double computeTransitTime(double lon, double lat, double d,
                                     double tzOff) {
        d = d - d % 1.0 + 0.5 + tzOff / 24.0;
        computePos(d);
        computeSetData(lon, lat, d, tzOff);
        return UT_Sun_in_south;
    }

    public double computeRiseTime(double lon, double lat, double d,
                                  double tzOff) {
        // adjsut d to be noon local time
        d = d - d % 1.0 + 0.5 + tzOff / 24.0;
        computePos(d);
        computeSetData(lon, lat, d, tzOff);
        return UT_Sun_in_south - LHA;
    }

    public double computeSetTime(double lon, double lat, double d,
                                 double tzOff) {
        // adjsut d to be noon local time
        d = d - d % 1.0 + 0.5 + tzOff / 24.0;
        computePos(d);
        computeSetData(lon, lat, d, tzOff);
        return UT_Sun_in_south + LHA;
    }

    void computePertubations(double d) {
        // don't do anything for this object
    }

}
