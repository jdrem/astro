package net.remgant.astro;

public class NonStellarObject extends FixedObject {
    protected int NGCNumber;
    protected int messierNumber;
    protected double magnitude;
    protected char type;

    public NonStellarObject(double RA, double decl, int NGCNumber,
                            int messierNumber, double magnitude, char type) {
        this.RA = RA;
        this.decl = decl;
        this.NGCNumber = NGCNumber;
        this.messierNumber = messierNumber;
        this.name = name;
        this.magnitude = magnitude;
        this.type = type;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
