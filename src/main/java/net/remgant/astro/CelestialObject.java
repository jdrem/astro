package net.remgant.astro;

public abstract class CelestialObject {
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

    protected void computeAzAltData(double d, double lon, double lat) {
        double UT = d - Math.floor(d);
        d = Math.floor(d);
        sun.computePos(d);
        sun.computeSetData(lon, lat);

        if (this instanceof MovingObject)
            ((MovingObject) this).computePos(d);

        sidTime = sun.GMST0 + UT * 360.0 + lon;
        HA = sidTime - RA;
        double x = Trig.cos(HA) * Trig.cos(decl);
        double y = Trig.sin(HA) * Trig.cos(decl);
        double z = Trig.sin(decl);

        xhor = x * Trig.sin(lat) - z * Trig.cos(lat);
        yhor = y;
        zhor = x * Trig.cos(lat) + z * Trig.sin(lat);
    }

    public double getAzimuth(double d, double lon, double lat) {
        computeAzAltData(d, lon, lat);
        return Trig.rev(Trig.atan2(yhor, xhor) + 180.0);
    }

    public double getAltitude(double d, double lon, double lat) {
        computeAzAltData(d, lon, lat);
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
