package net.remgant.astro;

public abstract class MovingObject extends CelestialObject {
    double lastD;
    // These are the orbital elements that are set in the object's
    // constructor.
    double N0, N1;
    double i0, i1;
    double w0, w1;
    double a0, a1;
    double e0, e1;
    double M0, M1;

    // These are computed from the above elements for a given day
    double N;
    double i;
    double w;
    double a;
    double e;
    double M;
    double E;

    // used to compute true anomaly
    double xv;
    double yv;
    // true anomaly
    double v;
    double r;

    // heliocentric coordinates
    double xh;
    double yh;
    double zh;

    // ecliptic longitude and lattitude
    double lonecl;
    double latecl;

    // geocentric coordinates
    double xg;
    double yg;
    double zg;

    // equatorial coordinates
    double xe;
    double ye;
    double ze;

    // geocentic distance
    double rg;

    void computePos(double d) {
        // compute the orbital elements for this day.
        N = N0 + N1 * d;
        i = i0 + i1 * d;
        w = w0 + w1 * d;
        a = a0 + a1 * d;
        e = e0 + e1 * d;
        M = M0 + M1 * d;

        // compute the mean anomaly
        E = M + e * (180 / Math.PI) * Trig.sin(Trig.degToRad(M)) *
                (1.0 + e * Trig.cos(Trig.degToRad(M)));
        // iterate it if it's too big
        if (E > 0.055) {
            double E0 = E;
            double E1;
            for (; ;) {
                E1 = E0 - (E0 - e * (180 / Math.PI) * Trig.sin(E0) - M) /
                        (1 - e * Trig.cos(E0));
                if (Math.abs(E1 - E0) < 0.001) {
                    E = E1;
                    break;
                }
                E0 = E1;
            }
        }


        // needed for next two operations
        xv = a * Trig.cos(E) - e;
        yv = a * Math.sqrt(1.0 - e * e) * Trig.sin(E);

        // compute the true anomaly
        v = Trig.atan2(yv, xv);
        // compute the distance
        r = Math.sqrt(xv * yv + yv * yv);

        // compute the heliocentric position in space
        xh = r * (Trig.cos(N) * Trig.cos(v + w) -
                Trig.sin(N) * Trig.sin(v + w) * Trig.cos(i));
        yh = r * (Trig.sin(N) * Trig.cos(v + w) +
                Trig.cos(N) * Trig.sin(v + w) * Trig.cos(i));
        zh = r * (Trig.sin(v + w) * Trig.sin(i));

        // compute the ecliptic longitude and lattitude
        lonecl = Trig.atan2(yh, xh);
        latecl = Trig.atan2(zh, Math.sqrt(xh * xh + yh * yh));

        // call the pertubation method for this object.  This may do
        // nothing, or it may recompute lonecl, latecl, xh, yh and zh;
        this.computePertubations(d);

        // compute the position of the sun for this time
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

    public double getRA(double t) {
        if (lastD != t) {
            computePos(t);
            lastD = t;
        }
        return RA;
    }

    public double getDecl(double t) {
        if (lastD != t) {
            computePos(t);
            lastD = t;
        }
        return decl;
    }

    abstract void computePertubations(double d);

}
