package net.remgant.astro;

public abstract class CelestialObject
        implements java.io.Serializable {
    static final long serialVersionUID = -4526987729909101128L;
    protected double RA;
    protected double decl;
    protected String name;
    protected String symbol = "";

    abstract public double getRA(double t);

    abstract public double getDecl(double t);

    // need to have a sun object to do certain calculations
    static Sun sun = new Sun();

    transient double sidTime;
    transient double HA;
    transient double xhor;
    transient double yhor;
    transient double zhor;

    void computeAzAltData(double d, double UT, double lon, double lat) {
        sun.computePos(d);
        sun.computeSetData(lon, lat, d, UT);

        // System.out.println(this.name);
        if (this instanceof MovingObject)
            ((MovingObject) this).computePos(d);

        // System.out.println("d = "+d);
        // System.out.println("lon = "+lon);
        // System.out.println("lat = "+lat);

        // System.out.println("RA = "+RA);
        // System.out.println("decl = "+decl);

        // System.out.println("UT = "+UT);
        // System.out.println("M = "+sun.M);
        // System.out.println("w = "+sun.w);
        // System.out.println("L = "+sun.L);

        // System.out.println("GMSTO = "+sun.GMST0);
        sidTime = sun.GMST0 + UT * 360.0 + lon;
        // System.out.println("sidTime = "+sidTime);
        HA = sidTime - RA;
        // System.out.println("HA = "+HA);
        double x = Trig.cos(HA) * Trig.cos(decl);
        double y = Trig.sin(HA) * Trig.cos(decl);
        double z = Trig.sin(decl);
        // System.out.println("("+x+","+y+","+z+")");

        xhor = x * Trig.sin(lat) - z * Trig.cos(lat);
        yhor = y;
        zhor = x * Trig.cos(lat) + z * Trig.sin(lat);
        // System.out.println("("+xhor+","+yhor+","+zhor+")");
    }

    public double getAzimuth(double d, double UT, double lon, double lat) {
        computeAzAltData(d, UT, lon, lat);
        return Trig.rev(Trig.atan2(yhor, xhor) + 180.0);
    }

    public double getAltitude(double d, double UT, double lon, double lat) {
        computeAzAltData(d, UT, lon, lat);
        double alt = Trig.rev(Trig.asin(zhor));
        if (alt > 180.0)
            return alt - 360.0;
        else
            return alt;
    }

    public String getSymbol()
    {
        return symbol;
    }

    @Override
    public String toString() {
        if (name != null)
            return name;
        return super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
