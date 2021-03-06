package net.remgant.astro;

public abstract class CelestialObject {
    protected double RA;
    protected double decl;
    protected String name;
    protected String symbol = "";

    abstract public double getRA(double t);

    abstract public double getDecl(double t);

    // need to have a sun object to do certain calculations
    // static Sun sun = new Sun();
    @SuppressWarnings("AnonymousHasLambdaAlternative")
    final static ThreadLocal<Sun> sun = new ThreadLocal<Sun>(){
        @Override
        protected Sun initialValue() {
            return new Sun();
        }
    };

    private transient double xhor;
    private transient double yhor;
    @SuppressWarnings("WeakerAccess")
    protected transient double zhor;

    @SuppressWarnings("WeakerAccess")
    protected void computeAzAltData(double d, double lon, double lat) {
        double UT = d - Math.floor(d);
        d = Math.floor(d);
        sun.get().computePos(d);
        sun.get().computeSetData(lon, lat);

        if (this instanceof MovingObject)
            ((MovingObject) this).computePos(d);

        double sidTime = sun.get().GMST0 + UT * 360.0 + lon;
        double HA = sidTime - RA;
        double x = Trig.cos(HA) * Trig.cos(decl);
        double y = Trig.sin(HA) * Trig.cos(decl);
        double z = Trig.sin(decl);

        xhor = x * Trig.sin(lat) - z * Trig.cos(lat);
        yhor = y;
        zhor = x * Trig.cos(lat) + z * Trig.sin(lat);
    }

    @SuppressWarnings("WeakerAccess")
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
