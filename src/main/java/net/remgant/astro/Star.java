package net.remgant.astro;

public class Star extends FixedObject {
    static final long serialVersionUID = -3952665899111645394L;
    protected double magnitude;
    protected String constellation;
    protected String id1;
    protected String id2;
    protected char spectralClass;

    public Star(double RA, double decl, String constellation,
                String id1, String id2, String name, double magnitude,
                char spectralClass) {
        this.RA = RA;
        this.decl = decl;
        this.constellation = constellation;
        this.id1 = id1;
        this.id2 = id2;
        this.name = name;
        this.magnitude = magnitude;
        this.spectralClass = spectralClass;

    }

    public Star(String RA, String decl, String constellation,
                String id1, String id2, String name, String magnitude,
                String spectralClass) {
        this.RA = Double.parseDouble(RA);
        this.decl = Double.parseDouble(decl);
        this.constellation = constellation;
        this.id1 = id1;
        this.id2 = id2;
        this.name = name;
        this.magnitude = Double.parseDouble(magnitude);
        this.spectralClass = spectralClass.charAt(0);
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getName() {
        if (name != null && name.trim().length() != 0)
            return name;

        if (constellation == null || constellation.trim().length() == 0)
            return "";
        if (id2 != null || id2.trim().length() != 0)
            return constellation + id2.trim();
        return constellation + id1.trim();
    }

    @Override
    public String toString() {
        return getName();
    }
}
