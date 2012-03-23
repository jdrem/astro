package net.remgant.astro;

import java.awt.*;

public interface Drawable {
    public void clear(Color c);

    public void drawPoint(int x, int y, Color c);

    public void drawPoint(Point p, Color c);

    public void drawLine(int xa, int ya, int xb, int yb, Color c);

    public void drawCircle(int x, int y, int r, Color c);

    public void drawFilledCircle(int x, int y, int r, Color c);

    public int getWidth();

    public int getHeight();

    public int getXOffset();

    public int getYOffset();
}

