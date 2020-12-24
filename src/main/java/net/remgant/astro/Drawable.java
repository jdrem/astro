package net.remgant.astro;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public interface Drawable {
    Graphics2D createGraphics();

    boolean isBW();

    double getWidth2D();

    double getHeight2D();

    double getXOffset2D();

    double getYOffset2D();

    Rectangle2D getBounds2D();
}

