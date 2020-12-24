package net.remgant.astro;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class NonStellarObject extends FixedObject {
    private int NGCNumber;
    private int messierNumber;
    protected double magnitude;
    private char type;

    public NonStellarObject(double RA, double decl, int NGCNumber,
                            int messierNumber, double magnitude, char type) {
        this.RA = RA;
        this.decl = decl;
        this.NGCNumber = NGCNumber;
        this.messierNumber = messierNumber;
        this.magnitude = magnitude;
        this.type = type;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
