package net.remgant.astro;

public abstract class FixedObject extends CelestialObject {
    public double getRA(double t) {
        return RA;
    }

    public double getDecl(double t) {
        return decl;
    }
}
