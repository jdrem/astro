package net.remgant.astro;

public class Saturn extends Planet {
    public Saturn() {
        N0 = 113.6634;
        N1 = 2.38980E-5;
        i0 = 2.4886;
        i1 = -1.081E-7;
        w0 = 339.3939;
        w1 = 2.97661E-5;
        a0 = 9.55475; //  (AU)
        a1 = 0.0;
        e0 = 0.055546;
        e1 = -9.499E-9;
        M0 = 316.9670;
        M1 = 0.0334442282;
        name = "Saturn";
    }

    private Jupiter jup;

    void computePertubations(double d) {
        if (jup == null)
            jup = new Jupiter();
        double Mj = jup.computeMeanAnomaly(d);
        double Ms = this.M;

        lonecl = lonecl
                + 0.812 * Trig.sin(2.0 * Mj - 5.0 * Ms - 67.6)
                - 0.229 * Trig.cos(2.0 * Mj - 4.0 * Ms - 2.0)
                + 0.119 * Trig.sin(Mj - 2.0 * Ms - 3.0)
                + 0.046 * Trig.sin(2.0 * Mj - 6.0 * Ms - 69.0)
                + 0.014 * Trig.sin(Mj - 3.0 * Ms + 32.0);

        latecl = latecl
                - 0.020 * Trig.cos(2.0 * Mj - 4.0 * Ms - 2.0)
                + 0.018 * Trig.sin(2.0 * Mj - 6.0 * Ms - 49.0);

        // recompute heliocentric position based on new lonecl
        xh = r * Trig.cos(lonecl) * Trig.cos(latecl);
        yh = r * Trig.sin(lonecl) * Trig.cos(latecl);
        zh = r * Trig.sin(latecl);
    }
}
