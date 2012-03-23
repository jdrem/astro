package net.remgant.astro;

public class Neptune extends Planet {
    public Neptune() {
        N0 = 131.7806;
        N1 = 3.0173E-5;
        i0 = 1.7700;
        i0 = -2.55E-7;
        w0 = 272.8461;
        w1 = -6.027E-6;
        a0 = 30.05826;
        a1 = 3.313E-8;
        e0 = 0.008606;
        e1 = 2.15E-9;
        M0 = 260.2471;
        M1 = 0.005995147;
        name = "Neptune";
    }


    void computePertubations(double d) {
        // don't do anything for this planet
    }
}
