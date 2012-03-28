package net.remgant.astro;

public class Venus extends Planet {
    public Venus() {
        N0 = 76.6799;
        N1 = 2.46590E-5;
        i0 = 3.3946;
        i1 = 2.75E-8;
        w0 = 54.8910;
        w1 = 1.38374E-5;
        a0 = 0.723330;
        a1 = 0.0;
        e0 = 0.006773;
        e1 = 1.302E-9;
        M0 = 48.0052;
        M1 = 1.6021302244;
        name = "Venus";
        symbol = "\u2640";
    }

    void computePertubations(double d) {
        // don't do anything for this object
    }
}
