package net.remgant.astro;

public class Uranus extends Planet {
    public Uranus() {
        N0 = 74.0005;
        N1 = 1.3978E-5;
        i0 = 0.7733;
        i1 = 1.9E-8;
        w0 = 96.6612;
        w1 = 3.0565E-5;
        a0 = 19.18171;
        a1 = -1.55E-8;
        e0 = 0.047318;
        e1 = 7.45E-9;
        M0 = 142.5905;
        M1 = 0.011725806;
        name = "Uranus";
        symbol = "\u2645";
    }

    private Jupiter jup;
    private Saturn sat;

    void computePertubations(double d) {
        if (jup == null)
            jup = new Jupiter();
        if (sat == null)
            sat = new Saturn();
        double Mj = jup.computeMeanAnomaly(d);
        double Ms = sat.computeMeanAnomaly(d);
        double Mu = this.M;

        lonecl = lonecl
                + 0.040 * Trig.sin(Ms - 2.0 * Mu + 6.0)
                + 0.035 * Trig.sin(Ms - 3.0 * Mu + 33.0)
                - 0.015 * Trig.sin(Mj - Mu + 20.0);

        // recompute heliocentric position based on new lonecl
        xh = r * Trig.cos(lonecl) * Trig.cos(latecl);
        yh = r * Trig.sin(lonecl) * Trig.cos(latecl);
        zh = r * Trig.sin(latecl);
    }
}
