package net.remgant.astro;

public class Mercury extends Planet {
    public Mercury() {
        N0 = 48.3313;
        N1 = 3.24587E-5;
        i0 = 7.0047;
        i1 = 5.00E-8;
        w0 = 29.1241;
        w1 = 1.01444E-5;
        a0 = 0.387098;
        a1 = 0.0;
        e0 = 0.205635;
        e1 = 5.59E-10;
        M0 = 168.6562;
        M1 = 4.0923344368;
    }

    void computePertubations(double d) {
        // don't do anything for this object
    }
}
