package net.remgant.astro;

import net.remgant.gui.DecimalField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.*;

public class Gui extends JFrame implements ComponentListener, ActionListener,
        MouseMotionListener, Printable {
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel
                    ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception ignore) {
        }

        System.out.println(System.getProperties().getProperty("os.name"));
        System.out.println(System.getProperties().getProperty("os.arch"));
        System.out.println(System.getProperties().getProperty("os.version"));

        Gui frame = new Gui();
        frame.pack();
        frame.setVisible(true);
    }

    int screenSizeX;
    int screenSizeY;
    Panel panel;
    Set<Star> stars;
    boolean showConBounds;
    boolean showGrid;
    boolean showEcliptic;
    boolean rectDisplayMode;
    boolean currentTimeMode;
    double maxMagnitude;
    enum DisplayMode{FULL_SKY,LOCAL_SKY,SUN_PATH}
    DisplayMode displayMode;

    Gui() {
        super("Remgant Sky Watcher");
        screenSizeX = 750;
        screenSizeY = 400;

        setBounds(0, 0, screenSizeX, screenSizeY);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JMenuBar myMenuBar = new JMenuBar();
        setJMenuBar(myMenuBar);
        JMenu FileMenu = new JMenu("File");
        myMenuBar.add(FileMenu);

        JMenuItem PrintMenuItem = new JMenuItem("Print");
        PrintMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PrinterJob pj = PrinterJob.getPrinterJob();
                pj.setPrintable(Gui.this);
                if (!pj.printDialog())
                    return;
                try {
                    pj.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Gui.this, ex.getMessage(),
                            "Print Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        FileMenu.add(PrintMenuItem);

        JMenuItem ExitMenuItem = new JMenuItem("Exit");
        ExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        FileMenu.add(ExitMenuItem);

        JMenu EditMenu = new JMenu("Edit");
        myMenuBar.add(EditMenu);

        JMenuItem propertiesItem = new JMenuItem("Properties");
        propertiesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editProperties();
            }
        });
        EditMenu.add(propertiesItem);


        JMenu ModeMenu = new JMenu("Mode");
        myMenuBar.add(ModeMenu);
        JRadioButtonMenuItem fullSkyModeMenuItem =
                new JRadioButtonMenuItem("Full Sky", true);
        fullSkyModeMenuItem.addActionListener(this);
        JRadioButtonMenuItem localSkyModeMenuItem =
                new JRadioButtonMenuItem("Local Sky", false);
        localSkyModeMenuItem.addActionListener(this);
        JRadioButtonMenuItem sunPathModeMenuItem =
                new JRadioButtonMenuItem("Sun Path", false);
        sunPathModeMenuItem.addActionListener(this);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(fullSkyModeMenuItem);
        modeGroup.add(localSkyModeMenuItem);
        modeGroup.add(sunPathModeMenuItem);
        ModeMenu.add(fullSkyModeMenuItem);
        ModeMenu.add(localSkyModeMenuItem);
        ModeMenu.add(sunPathModeMenuItem);

        JMenu HelpMenu = new JMenu("Help");
        myMenuBar.add(HelpMenu);

        JMenuItem AboutMenuItem = new JMenuItem("About");
        AboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Gui.this, "Remgant Sky Mapper");
            }
        });
        HelpMenu.add(AboutMenuItem);


        Dimension size = new Dimension(screenSizeX, screenSizeY);
        panel = new Panel(size);
        getContentPane().add("Center", panel);
        panel.setPreferredSize(size);
        panel.addComponentListener(this);
        // panel.addMouseMotionListener(this);

        stars = new HashSet<Star>();

        loadObjects(stars);

        showConBounds = true;
        showGrid = true;
        showEcliptic = true;
        displayMode = DisplayMode.FULL_SKY;
        rectDisplayMode = true;
        currentTimeMode = true;
        maxMagnitude = 4.0;
        drawScreen(panel,displayMode);
        // panel.setToolTipText("Tool Tip Text\nSecond Line of Text");

        URL iconURL;
        if (System.getProperties().getProperty("os.name").equals("SunOS"))
            iconURL = getClass().getResource("largeIcon.png");
        else
            iconURL = getClass().getResource("smallIcon.png");
        Toolkit tk = Toolkit.getDefaultToolkit();
        setIconImage(tk.createImage(iconURL));
    }

    private void editProperties() {
        if (displayMode == DisplayMode.FULL_SKY)
            editFullSkyProperties();
        else
            editLocalSkyProperties();
    }

    private void editFullSkyProperties() {
        final JDialog d = new JDialog(Gui.this, "Properties", true);
        Container cp = d.getContentPane();
        cp.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        int numPanels = 6;
        JPanel panels[] = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++) {
            panels[i] = new JPanel();
            // panels[i].setLayout(new GridBagLayout());
            panels[i].setLayout(new BorderLayout());
            c.gridx = 0;
            c.gridy = i;
            cp.add(panels[i], c);
        }
        c.gridwidth = 1;
        c.gridheight = 1;
        int p = 0;

        final JCheckBox showConBoundsCheckBox =
                new JCheckBox("Show Constellation Boundaries", showConBounds);
        c.gridx = 0;
        c.gridy = 0;
        panels[p++].add(showConBoundsCheckBox, BorderLayout.WEST);

        final JCheckBox showGridCheckBox =
                new JCheckBox("Show Grid", showGrid);
        c.gridx = 0;
        c.gridy = 0;
        panels[p++].add(showGridCheckBox, BorderLayout.WEST);

        final JCheckBox showEclipticCheckBox =
                new JCheckBox("Show Ecliptic", showEcliptic);
        c.gridx = 0;
        c.gridy = 0;
        panels[p++].add(showEclipticCheckBox, BorderLayout.WEST);


        JLabel maxMagnitudeLabel = new JLabel("Max Manitude");
        panels[p].add(maxMagnitudeLabel, BorderLayout.WEST);

        final DecimalField maxMagnitudeField =
                new DecimalField(Double.toString(maxMagnitude), 5);
        panels[p++].add(maxMagnitudeField, BorderLayout.EAST);

        JButton okButton = new JButton("OK");
        c.gridx = 0;
        c.gridy = 0;
        panels[p].add(okButton, BorderLayout.WEST);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                {
                    showConBounds = showConBoundsCheckBox.isSelected();
                    showGrid = showGridCheckBox.isSelected();
                    showEcliptic = showEclipticCheckBox.isSelected();
                    maxMagnitude = Double.parseDouble(maxMagnitudeField.getText());
                    drawScreen(panel,displayMode);
                    d.dispose();
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 2;
        c.gridy = 0;
        panels[p].add(cancelButton, BorderLayout.EAST);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                d.dispose();
            }
        });

        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });
        d.setBounds(0, 0, 225, 175);
        d.setVisible(true);
    }

    private void editLocalSkyProperties() {
        final JDialog d = new JDialog(Gui.this, "Properties", true);
        Container cp = d.getContentPane();
        cp.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        JPanel panels[] = new JPanel[5];
        for (int i = 0; i < 5; i++) {
            panels[i] = new JPanel();
            panels[i].setLayout(new BorderLayout());
            c.gridx = 0;
            c.gridy = i;
            cp.add(panels[i], c);
        }
        c.gridwidth = 1;
        c.gridheight = 1;

        final JRadioButton rectDisplayButton =
                new JRadioButton("Rectangle Display", rectDisplayMode);
        final JRadioButton circDisplayButton =
                new JRadioButton("Circular Display", !rectDisplayMode);
        ButtonGroup group1 = new ButtonGroup();
        group1.add(rectDisplayButton);
        group1.add(circDisplayButton);
        panels[0].add(rectDisplayButton, BorderLayout.WEST);
        panels[0].add(circDisplayButton, BorderLayout.EAST);

        final JRadioButton currentTimeButton =
                new JRadioButton("Use Current Time", currentTimeMode);
        final JRadioButton setTimeButton =
                new JRadioButton("Set Time", !currentTimeMode);
        ButtonGroup group2 = new ButtonGroup();
        group2.add(currentTimeButton);
        group2.add(setTimeButton);
        panels[1].add(currentTimeButton, BorderLayout.WEST);
        panels[1].add(setTimeButton, BorderLayout.EAST);

        final JCheckBox showConBoundsCheckBox =
                new JCheckBox("Show Constellation Boundaries", showConBounds);
        c.gridx = 0;
        c.gridy = 0;
        panels[2].add(showConBoundsCheckBox, BorderLayout.WEST);

        final JCheckBox showGridCheckBox =
                new JCheckBox("Show Grid", showGrid);
        c.gridx = 0;
        c.gridy = 0;
        panels[3].add(showGridCheckBox, BorderLayout.WEST);

        JButton okButton = new JButton("OK");
        c.gridx = 0;
        c.gridy = 0;
        panels[4].add(okButton, BorderLayout.WEST);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                {
                    showConBounds = showConBoundsCheckBox.isSelected();
                    showGrid = showGridCheckBox.isSelected();
                    rectDisplayMode = rectDisplayButton.isSelected();
                    currentTimeMode = currentTimeButton.isSelected();
                    drawScreen(panel,displayMode);
                    d.dispose();
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 2;
        c.gridy = 0;
        panels[4].add(cancelButton, BorderLayout.EAST);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                d.dispose();
            }
        });

        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });
        d.setBounds(0, 0, 300, 175);
        d.setVisible(true);
    }

    private void drawScreen(Drawable drawable,DisplayMode displayMode)
    {
        clearScreen(drawable, drawable.getBounds2D());
        switch(displayMode)
        {
            case FULL_SKY:
                drawFullSkyScreen(drawable);
                break;
            case LOCAL_SKY:
                drawLocalScreen();
                break;
            case SUN_PATH:
                drawSunPathScreen();
                break;
        }
        panel.repaint();
    }

    private void clearScreen(Drawable drawable,Rectangle2D bounds)
    {
        Graphics2D g = drawable.createGraphics();
        g.setColor(drawable.isBW()?Color.WHITE:Color.BLACK);
        g.fill(bounds);
    }

    private void fillScreen(Drawable drawable,Rectangle2D bounds,Color color)
    {
        Graphics2D g = drawable.createGraphics();
        g.setColor(color);
        g.fill(bounds);
    }

    private void drawFullSkyScreen(Drawable drawable) {
        if (showGrid)
            drawIndexLines(drawable);
        if (showConBounds)
            drawBoundaryLines2D(drawable);
        drawStars(drawable);
        if (showEcliptic)
            drawEcliptic(drawable);
    }

    private void drawStars(Drawable d)
    {
        Graphics2D g = d.createGraphics();
          for (Star o : stars) {
               double magnitude = o.getMagnitude();
              if (magnitude > maxMagnitude)
                  continue;
              double ra = o.getRA(0.0);
              double decl = o.getDecl(0.0);
              double x = d.getXOffset2D() + d.getWidth2D()
                       - (ra / 360.0 * (double) screenSizeX);
              double y = d.getYOffset2D() +  (((90.0 - decl) / 180.0) *
                     d.getHeight2D());

              Color c = d.isBW() ? Color.BLACK : Color.WHITE;
              g.setColor(c);
              g.fill(new Ellipse2D.Double(x,y,2.0,2.0));
          }
    }

    private void drawBoundaryLines2D(Drawable drawable)
    {
        Graphics2D g = drawable.createGraphics();
        g.setColor(drawable.isBW() ? Color.BLACK : Color.WHITE);
        double width = drawable.getWidth2D();
        double height = drawable.getHeight2D();
        try
        {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(
                            getClass().getResource("boundaries.dat").openStream()));
            String line = in.readLine();
            while (line != null && line.length() > 0) {
                double d[] = tokenizeDoubles(line);
                double x1 = width -  (d[0] / 360.0 * width);
                double y1 = (((90.0 - d[1]) / 180.0) *  height);
                double x2 = width - (d[2] / 360.0 * width);
                double y2 = (((90.0 - d[3]) / 180.0) *  height);
                if (Math.abs(x1 - x2) > width / 2.0) {
                    if (x1 > x2) {
                        g.draw(new Line2D.Double(x1,y1,width,y2));
                        g.draw(new Line2D.Double(0.0,y1,x2,y2));
                    } else {
                        g.draw(new Line2D.Double(x1,y1,0.0,y2));
                        g.draw(new Line2D.Double(width,y1,x2,y2));
                    }
                } else {
                    g.draw(new Line2D.Double(x1,y1,x2,y2));
                }
                line = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawIndexLines(Drawable d)
    {
        Graphics2D g = d.createGraphics();
        g.setColor(d.isBW()?Color.BLACK:Color.RED);
         for (int i = 1; i < 12; i++) {
            g.draw(new Line2D.Double(d.getWidth2D()/12.0*(double)i,d.getYOffset2D(),
                    d.getWidth2D() / 12.0 * (double)i,d.getHeight2D()));
        }
        for (int i = 1; i <= 5; i++) {
            g.draw(new Line2D.Double(d.getXOffset2D(),d.getHeight2D() / 6.0 * (double)i,
                    d.getWidth2D(), d.getHeight2D() / 6.0 * (double)i));
        }
    }

    private void drawEcliptic(Drawable drawable)
    {
        Graphics2D g = drawable.createGraphics();
        double width = drawable.getWidth2D();
        double height = drawable.getHeight2D();
        g.setColor(drawable.isBW()?Color.BLACK:Color.YELLOW);
        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2000, Calendar.MARCH, 21, 0, 0);
        double d = net.remgant.astro.Time.getDayNumber(cal);
        for (int i = 0; i < 3650; i++) {
            d = d + (double) i / 10.0;
            double ra = sun.getRA(d);
            double decl = sun.getDecl(d);
            double x = width - (ra / 360.0 *  width);
            double y =  (((90.0 - decl) / 180.0) *  height);
            g.fill(new Ellipse2D.Double(x,y,1.0,1.0));
        }
    }

    private void drawLocalScreen() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        //cal.set(2002, Calendar.FEBRUARY, 16, 0, 0, 0);
        cal.setTime(new Date());
        double d = net.remgant.astro.Time.getDayNumber(cal);
        double lon = -71.4750;
        double lat = 42.4750;
        Point p;

        if (!rectDisplayMode) {
            panel.clear(Color.gray);
            int r = screenSizeX;
            if (r > screenSizeY)
                r = screenSizeY;
            panel.drawFilledCircle(screenSizeX / 2, screenSizeY / 2, r, Color.black);
        }

        for (Star o : stars) {
            double az = o.getAzimuth(d, 0.0, lon, lat);
            double alt = o.getAltitude(d, 0.0, lon, lat);
            double magnitude = o.getMagnitude();
            if (rectDisplayMode)
                p = getRectCoordinates(az, alt);
            else
                p = getCircCoordinates(az, alt);
            if (p == null)
                continue;
            int x = p.x;
            int y = p.y;
            if (magnitude <= 1.0) {
                panel.drawLine(x - 1, y - 1, x - 1, y + 1, Color.white);
                panel.drawLine(x, y - 1, x, y + 1, Color.white);
                panel.drawLine(x + 1, y - 1, x + 1, y + 1, Color.white);
            } else if (magnitude <= 3.0) {
                panel.drawLine(x, y - 1, x, y + 1, Color.white);
                panel.drawLine(x - 1, y, x + 1, y, Color.white);
            } else if (magnitude <= 4.0) {
                panel.drawPoint(x, y, Color.white);
            }
        }
        Moon moon = new Moon();
        double az = moon.getAzimuth(d, 0.0, lon, lat);
        double alt = moon.getAltitude(d, 0.0, lon, lat);
        if (rectDisplayMode)
            p = getRectCoordinates(az, alt);
        else
            p = getCircCoordinates(az, alt);
        System.out.println("moon: " + az + " " + alt + " " + p);
        if (p == null)
            return;
        int size = (int) (moon.getSize(d) / 360.0 * screenSizeX);
        if (size < 6)
            size = 6;
        panel.drawFilledCircle(p.x, p.y, size, Color.yellow);
    }

    private void drawSunPathScreen() {
        Calendar cal[] = new Calendar[3];
        cal[0] = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal[0].set(2002, Calendar.DECEMBER, 21, 19, 0, 0);
        cal[1] = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal[1].set(2003, Calendar.MARCH, 21, 19, 0, 0);
        cal[2] = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal[2].set(2003, Calendar.JUNE, 21, 19, 0, 0);
        double lon = -71.4750;
        double lat = 42.4750;
        Point p;

        if (!rectDisplayMode) {
            panel.clear(Color.gray);
            int r = screenSizeX;
            if (r > screenSizeY)
                r = screenSizeY;
            panel.drawFilledCircle(screenSizeX / 2, screenSizeY / 2, r, Color.black);
        }

        Sun sun = new Sun();
        double h = 0.0;
        for (int j = 0; j < 3; j++) {
            double d = net.remgant.astro.Time.getDayNumber(cal[j]);
            for (int i = 0; i < 1024; i++) {
                h += (1.0 / 1024.0) * (double) i;
                double az = sun.getAzimuth(d, h, lon, lat);
                double alt = sun.getAltitude(d, h, lon, lat);
                if (rectDisplayMode)
                    p = getRectCoordinates(az, alt);
                else
                    p = getCircCoordinates(az, alt);
                if (p == null)
                    continue;
                panel.drawPoint(p.x, p.y, Color.yellow);
            }
        }
    }

    private Point getRectCoordinates(double az, double alt) {
        int x = (int) (az / 360.0 * (double) screenSizeX);
        if (alt < 0.0)
            return null;
        int y = (int) (((90.0 - alt) / 90.0) * (double) screenSizeY);
        return new Point(x, y);
    }

    private Point getCircCoordinates(double az, double alt) {
        if (alt < 0.0)
            return null;
        // normalize the altitude
        alt = (90.0 - alt) / 90.0;
        // adjust the azimuth
        az = Trig.rev(az + 90.0);
        double x = Trig.cos(az) * alt;
        double y = Trig.sin(az) * alt;
        int m;
        int xa;
        int ya;
        if (screenSizeX > screenSizeY) {
            m = screenSizeY;
            ya = 0;
            xa = (screenSizeX - screenSizeY) / 2;
        } else {
            m = screenSizeX;
            ya = (screenSizeY - screenSizeX) / 2;
            xa = 0;
        }
        Point p = new Point();
        p.x = (int) (x * ((double) m / 2.0) + (double) m / 2.0) + xa;
        p.y = (int) ((double) m / 2.0 - y * ((double) m / 2.0)) + ya;
        return p;
    }

    private static double[] tokenizeDoubles(String line) {
        StringTokenizer st = new StringTokenizer(line, ",");
        int l = st.countTokens();
        double r[] = new double[l];
        for (int i = 0; st.hasMoreTokens(); i++) {
            r[i] = Double.parseDouble(st.nextToken());
        }
        return r;
    }

    private void loadObjects(Set<Star> set) {

        try {
            ObjectInputStream in = new ObjectInputStream(getClass().getResource("bsc.obj").openStream());
            while (true) {
                Object o = in.readObject();
                if (o == null)
                    break;
                set.add((Star) o);
            }
        } catch (EOFException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Panel extends JPanel implements Drawable {
        private Dimension dim;
        private BufferedImage image;

        public Panel(Dimension d) {
            super();
            dim = d;
            image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
        }

        public void resizeImage(Dimension d) {
            dim = d;
            image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
        }

        public void clear(Color c) {
            Graphics g = image.getGraphics();
            g.clearRect(0, 0, dim.width, dim.height);
            g.setColor(c);
            g.fillRect(0, 0, dim.width, dim.height);
        }

        public void drawPoint(int x, int y, Color c) {

             Graphics2D g = image.createGraphics();
            g.setColor(c);
            g.fill(new Ellipse2D.Double((double)x,(double)y,1.0,1.0));

        }

        public void drawLine(int xa, int ya, int xb, int yb, Color c) {
            Graphics2D g = image.createGraphics();
            g.setColor(c);
            g.fill(new Line2D.Double((double)xa,(double)ya,(double)xb,(double)yb));
        }

        public void drawFilledCircle(int x, int y, int r, Color c) {
            Graphics2D g = image.createGraphics();
            g.setColor(c);
            g.fill(new Ellipse2D.Double((double)x/2.0,(double)y/2.0,(double)r,(double)r));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, Color.gray, this);
        }

        @Override
        public Graphics2D createGraphics() {
            return image.createGraphics();
        }

        @Override
        public boolean isBW() {
            return false;
        }

        @Override
        public double getWidth2D() {
            return (double)getWidth();
        }

        @Override
        public double getHeight2D() {
            return (double)getHeight();
        }

        @Override
        public double getXOffset2D() {
            return 0;
        }

        @Override
        public double getYOffset2D() {
            return 0;
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(0.0,0.0,(double)getHeight(),(double)getWidth());
        }
    }

    class PrintPage implements Drawable {
        Graphics g;
        int width;
        int height;
        int xOff;
        int yOff;

        public PrintPage(Graphics g, int width, int height, int xOff, int yOff) {
            this.g = g;
            this.width = width;
            this.height = height;
            this.xOff = xOff;
            this.yOff = yOff;
        }

        @SuppressWarnings({"UnusedDeclaration"})
        public void clear() {
            clear(Color.white);
        }

        public void clear(Color c) {
            if (c.equals(Color.black))
                c = Color.white;
            g.clearRect(xOff, yOff, width, height);
            g.setColor(c);
            g.fillRect(xOff, yOff, width, height);
        }

        @Override
        public Graphics2D createGraphics() {
            return (Graphics2D)g;
        }

        @Override
        public boolean isBW() {
            return true;
        }

        @Override
        public double getWidth2D() {
            return (double)width;
        }

        @Override
        public double getHeight2D() {
            return (double)height;
        }

        @Override
        public double getXOffset2D() {
            return (double)xOff;
        }

        @Override
        public double getYOffset2D() {
            return (double)yOff;
        }

        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(0.0,0.0,width,height);
        }
    }


    // these next four methods are required when implementing
// component listener
    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        screenSizeX = panel.getWidth();
        screenSizeY = panel.getHeight();
        Dimension size = new Dimension(screenSizeX, screenSizeY);
        panel.resizeImage(size);
        drawScreen(panel,displayMode);
    }

    public void componentShown(ComponentEvent e) {
    }

    // this is required when implementing ActionListener
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Full Sky")) {
            displayMode = DisplayMode.FULL_SKY;
        } else if (e.getActionCommand().equals("Local Sky")) {
            displayMode = DisplayMode.LOCAL_SKY;
        } else if (e.getActionCommand().equals("Sun Path")) {
            displayMode = DisplayMode.SUN_PATH;
        }
        drawScreen(panel,displayMode);
    }

    // Next two are required for MouseMotionListener
    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        System.out.println(e);
    }

    // Required for Printable interface
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex > 0)
            return Printable.NO_SUCH_PAGE;

        pf.setOrientation(PageFormat.LANDSCAPE);
        PrintPage page = new PrintPage(g, (int) pf.getImageableWidth(),
                (int) pf.getImageableHeight(),
                (int) pf.getImageableX(),
                (int) pf.getImageableY());
        drawScreen(page,DisplayMode.FULL_SKY);
        return Printable.PAGE_EXISTS;
    }
}