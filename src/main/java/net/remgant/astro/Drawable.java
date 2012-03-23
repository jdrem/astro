package net.remgant.astro;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public interface Drawable {
     public Graphics2D createGraphics();

    public boolean isBW();

    public double getWidth2D();

    public double getHeight2D();

    public double getXOffset2D();

    public double getYOffset2D();

    public Rectangle2D getBounds2D();
}

