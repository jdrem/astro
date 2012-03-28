package net.remgant.astro;

public class Mars extends Planet {
    public Mars() {
        N0 = 49.5574;
        N1 = 2.11081E-5;
        i0 = 1.8497;
        i1 = -1.78E-8;
        w0 = 286.5016;
        w1 = 2.92961E-5;
        a0 = 1.523688;
        a1 = 0.0;
        e0 = 0.093405;
        e1 = 2.516E-9;
        M0 = 18.6021;
        M1 = 0.5240207766;
        name = "Mars";
        symbol = "\u2642";
    }

    void computePertubations(double d) {
        // don't do anything for this object
    }
}
