package net.remgant.astro;

public class Jupiter extends Planet {
    public Jupiter() {
        N0 = 100.4542;
        N1 = 2.76854E-5;
        i0 = 1.3030;
        i1 = -1.557E-7;
        w0 = 273.8777;
        w1 = 1.64505E-5;
        a0 = 5.20256;//  (AU)
        a1 = 0.0;
        e0 = 0.048498;
        e1 = 4.469E-9;
        M0 = 19.8950;
        M1 = 0.0830853001;
        name = "Jupiter";
    }

    private Saturn sat;

    void computePertubations(double d) {
        if (sat == null)
            sat = new Saturn();
        sat.computePos(d);
        double Mj = this.M;
        double Ms = sat.M;

        lonecl = lonecl
                - 0.332 * Trig.sin(2.0 * Mj - 5.0 * Ms - 67.6)
                - 0.056 * Trig.sin(2.0 * Mj - 2.0 * Ms + 21.0)
                + 0.042 * Trig.sin(3.0 * Mj - 5.0 * Ms + 21.0)
                - 0.036 * Trig.sin(Mj - 2.0 * Ms)
                + 0.022 * Trig.cos(Mj - Ms)
                + 0.023 * Trig.sin(2 * Mj - 3 * Ms + 52.0)
                - 0.016 * Trig.sin(Mj - 5 * Ms - 69.0);


        // recompute heliocentric position based on new lonecl
        xh = r * Trig.cos(lonecl) * Trig.cos(latecl);
        yh = r * Trig.sin(lonecl) * Trig.cos(latecl);
        zh = r * Trig.sin(latecl);
    }
}
