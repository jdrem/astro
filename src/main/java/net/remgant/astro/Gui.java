package net.remgant.astro;

import com.github.lgooddatepicker.components.DatePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.remgant.gui.DecimalField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        @SuppressWarnings({"UnusedDeclaration"})
        Gui frame = new Gui();
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
    enum DisplayMode{FULL_SKY,LOCAL_SKY,SUN_PATH,RISE_SET,PLANET_PATH}
    DisplayMode displayMode;
    Font font;

    Preferences preferences;
    double longitude;
    double latitude;
    String locationName;
    String timeZoneName;
    //Calendar displayDate;
    Instant displayDate;
    Map<String,Location> locationMap;
    Location currentLocation;

    Gui() {
        super("Remgant Sky Watcher");

        preferences = Preferences.userNodeForPackage(Gui.class);
        screenSizeX = preferences.getInt("ScreenSizeX", 750);
        screenSizeY = preferences.getInt("ScreenSizeY", 400);
        displayMode = DisplayMode.valueOf(preferences.get("DisplayMode",DisplayMode.FULL_SKY.name()));
        longitude = preferences.getDouble("location.longitude",-71.1);
        latitude = preferences.getDouble("location.latitude",42.3);
        locationName = preferences.get("location.name","Boston, MA, USA");
        timeZoneName = preferences.get("location.timezone","America/New_York");
        currentTimeMode = preferences.getBoolean("date.useCurrentTime",true);
        LocalDate localDate = LocalDate.now();
        int displayDateYear = preferences.getInt("date.year", localDate.getYear());
        int displayDateMonth = preferences.getInt("date.month",localDate.getMonthValue());
        int displayDateDay = preferences.getInt("date.day_of_month",localDate.getDayOfMonth());
        displayDate = ZonedDateTime.of(LocalDate.of(displayDateYear,displayDateMonth,displayDateDay),
                LocalTime.of(0,0,0), ZoneId.of(timeZoneName)).toInstant();

        locationMap = loadLocations();
        currentLocation = locationMap.get("Boston, MA, USA");
        if (currentLocation == null)
            currentLocation = new Location("Boston, MA, USA",-71.1,42.3);

        font = new Font(Font.SANS_SERIF,Font.PLAIN,12);
        char symbol = (new Jupiter()).getSymbol().charAt(0);
        if (!font.canDisplay(symbol))
        {
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font fonts[] = graphicsEnvironment.getAllFonts();
            for (Font f : fonts)
            {
                if (f.canDisplay(symbol) && f.getStyle() == Font.PLAIN)
                {
                    font = new Font(f.getFontName(),Font.PLAIN,12);
                    break;
                }
            }
        }
        System.out.println("using font "+font);

        setBounds(0, 0, screenSizeX, screenSizeY);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    preferences.sync();
                } catch (BackingStoreException e1) {
                    e1.printStackTrace();
                }
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
                try {
                    preferences.sync();
                } catch (BackingStoreException e1) {
                    e1.printStackTrace();
                }
                System.exit(1);
            }
        });
        FileMenu.add(ExitMenuItem);

        JMenu EditMenu = new JMenu("Edit");
        myMenuBar.add(EditMenu);

        JMenuItem propertiesItem = new JMenuItem("Properties");
        propertiesItem.addActionListener(e -> editProperties());
        EditMenu.add(propertiesItem);

        JMenuItem dateMenuItem = new JMenuItem("Date");
        dateMenuItem.addActionListener(e -> editDate());
        EditMenu.add(dateMenuItem);

        JMenuItem locationMenuItem = new JMenuItem("Location");
        locationMenuItem.addActionListener(e -> editLocation());
        EditMenu.add(locationMenuItem);

        JMenu ModeMenu = new JMenu("Mode");
        myMenuBar.add(ModeMenu);
        JRadioButtonMenuItem fullSkyModeMenuItem =
                new JRadioButtonMenuItem("Full Sky", displayMode == DisplayMode.FULL_SKY);
        fullSkyModeMenuItem.addActionListener(this);
        JRadioButtonMenuItem localSkyModeMenuItem =
                new JRadioButtonMenuItem("Local Sky", displayMode == DisplayMode.LOCAL_SKY);
        localSkyModeMenuItem.addActionListener(this);
        JRadioButtonMenuItem sunPathModeMenuItem =
                new JRadioButtonMenuItem("Sun Path", displayMode == DisplayMode.SUN_PATH);
        sunPathModeMenuItem.addActionListener(this);
        JRadioButtonMenuItem riseSetModeMenuItem =
                new JRadioButtonMenuItem("Rise/Set",displayMode == DisplayMode.RISE_SET);
        riseSetModeMenuItem.addActionListener(this);
        JRadioButtonMenuItem planetPathModeMenuItem =
                new JRadioButtonMenuItem("Planet Path",displayMode == DisplayMode.PLANET_PATH);
        planetPathModeMenuItem.addActionListener(this);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(fullSkyModeMenuItem);
        modeGroup.add(localSkyModeMenuItem);
        modeGroup.add(sunPathModeMenuItem);
        modeGroup.add(riseSetModeMenuItem);
        modeGroup.add(planetPathModeMenuItem);
        ModeMenu.add(fullSkyModeMenuItem);
        ModeMenu.add(localSkyModeMenuItem);
        ModeMenu.add(sunPathModeMenuItem);
        ModeMenu.add(riseSetModeMenuItem);
        ModeMenu.add(planetPathModeMenuItem);

        JMenu HelpMenu = new JMenu("Help");
        myMenuBar.add(HelpMenu);

        JMenuItem AboutMenuItem = new JMenuItem("About");
        AboutMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(Gui.this, "Remgant Sky Mapper"));
        HelpMenu.add(AboutMenuItem);


        Dimension size = new Dimension(screenSizeX, screenSizeY);
        panel = new Panel(size);
        getContentPane().add("Center", panel);
        panel.setPreferredSize(size);
        panel.addComponentListener(this);
        panel.addMouseMotionListener(this);

        stars = loadStars();

        showConBounds = preferences.getBoolean("display.showConBounds",true);
        showGrid = preferences.getBoolean("display.showGrid",true);
        showEcliptic = preferences.getBoolean("display.showEcliptic",true);
        rectDisplayMode = preferences.getBoolean("display.rectDisplayMode",true);
        //currentTimeMode = preferences.getBoolean("display.currentTimeMode",true);
        maxMagnitude = preferences.getDouble("display.maxMagnitude",4.0);

        drawScreen(panel, displayMode);
        // panel.setToolTipText("Tool Tip Text\nSecond Line of Text");

        URL iconURL;
        if (System.getProperties().getProperty("os.name").equals("SunOS"))
            iconURL = getClass().getResource("largeIcon.png");
        else
            iconURL = getClass().getResource("smallIcon.png");
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        setIconImage(toolkit.createImage(iconURL));

        pack();
        setVisible(true);
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
        okButton.addActionListener(e -> {
            {
                showConBounds = showConBoundsCheckBox.isSelected();
                showGrid = showGridCheckBox.isSelected();
                showEcliptic = showEclipticCheckBox.isSelected();
                maxMagnitude = Double.parseDouble(maxMagnitudeField.getText());
                drawScreen(panel,displayMode);
                d.dispose();
                preferences.putBoolean("display.showConBounds",showConBounds);
                preferences.putBoolean("display.showGrid",showGrid);
                preferences.putBoolean("display.showEcliptic",showEcliptic);
                preferences.putDouble("display.maxMagnitude",maxMagnitude);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 2;
        c.gridy = 0;
        panels[p].add(cancelButton, BorderLayout.EAST);
        cancelButton.addActionListener(e -> d.dispose());

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
        okButton.addActionListener(e -> {
            {
                showConBounds = showConBoundsCheckBox.isSelected();
                showGrid = showGridCheckBox.isSelected();
                rectDisplayMode = rectDisplayButton.isSelected();
                currentTimeMode = currentTimeButton.isSelected();
                drawScreen(panel,displayMode);
                d.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 2;
        c.gridy = 0;
        panels[4].add(cancelButton, BorderLayout.EAST);
        cancelButton.addActionListener(e -> d.dispose());

        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });
        d.setBounds(0, 0, 300, 175);
        d.setVisible(true);
    }

    private void editDate()
    {
        final JDialog d = new JDialog(Gui.this, "Date", true);
        Container cp = d.getContentPane();
        cp.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        JPanel panels[] = new JPanel[3];
        for (int i = 0; i < panels.length; i++) {
            panels[i] = new JPanel();
            panels[i].setLayout(new BorderLayout());
            c.gridx = 0;
            c.gridy = i;
            cp.add(panels[i], c);
        }
        c.gridwidth = 1;
        c.gridheight = 1;

        final JRadioButton currentTimeButton =
                new JRadioButton("Use Current Time", currentTimeMode);
        final JRadioButton setTimeButton =
                new JRadioButton("Set Time", !currentTimeMode);
        ButtonGroup group2 = new ButtonGroup();
        group2.add(currentTimeButton);
        group2.add(setTimeButton);
        panels[0].add(currentTimeButton, BorderLayout.WEST);
        panels[0].add(setTimeButton, BorderLayout.EAST);

        DatePicker datePicker = new DatePicker();
        datePicker.setDate(displayDate.atZone(ZoneId.of(timeZoneName)).toLocalDate());
        panels[1].add(datePicker);

        currentTimeButton.addChangeListener(e -> {
            System.out.println("currentTimeButton: "+currentTimeButton.isSelected());
            if (currentTimeButton.isSelected())
            {
                Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                 now.setTime(new java.util.Date());
            }
        });


        JButton okButton = new JButton("OK");
        c.gridx = 0;
        c.gridy = 0;
        panels[2].add(okButton, BorderLayout.WEST);
        okButton.addActionListener(e -> {
            {
                int newYear = datePicker.getDate().getYear();
                int newMonth = datePicker.getDate().getMonthValue();
                int newDay = datePicker.getDate().getDayOfMonth();
                displayDate = ZonedDateTime.of(LocalDate.of(newYear, newMonth, newDay),
                        LocalTime.of(0,0,0),ZoneId.of(timeZoneName)).toInstant();
                currentTimeMode = currentTimeButton.isSelected();
                preferences.putBoolean("date.useCurrentTime", currentTimeMode);
                preferences.putInt("date.year", newYear);
                preferences.putInt("date.month", newMonth);
                preferences.putInt("date.day_of_month", newDay);
                d.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 2;
        c.gridy = 0;
        panels[2].add(cancelButton, BorderLayout.EAST);
        cancelButton.addActionListener(e -> d.dispose());
        d.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });
        d.setBounds(0, 0, 300, 175);
        d.setVisible(true);
    }

    private void editLocation()
    {
        final JDialog d = new JDialog(Gui.this, "Date", true);
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

        DefaultComboBoxModel model = new DefaultComboBoxModel(locationMap.values().toArray());
        model.setSelectedItem(currentLocation);
        JComboBox locationList = new JComboBox(model);
        locationList.setEditable(true);
        panels[0].add(locationList,BorderLayout.CENTER);


        JButton okButton = new JButton("OK");
        c.gridx = 0;
        c.gridy = 0;
        panels[4].add(okButton, BorderLayout.WEST);
        okButton.addActionListener(e -> d.dispose());

        JButton cancelButton = new JButton("Cancel");
        c.gridx = 2;
        c.gridy = 0;
        panels[4].add(cancelButton, BorderLayout.EAST);
        cancelButton.addActionListener(e -> d.dispose());


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
                drawLocalScreen(drawable);
                break;
            case SUN_PATH:
                drawSunPathScreen();
                break;
            case RISE_SET:
                drawRiseSetScreen(drawable);
                break;
            case PLANET_PATH:
                drawPlanetPathScreen(drawable);
                break;
        }
        panel.repaint();
    }

    private void clearScreen(Drawable drawable,Rectangle2D bounds)
    {
        fillScreen(drawable,bounds,drawable.isBW()?Color.WHITE:Color.BLACK);
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
            drawBoundaryLines(drawable);
        drawStars(drawable);
        if (showEcliptic)
            drawEcliptic(drawable);
    }

    private void drawPlanetPathScreen(Drawable drawable) {
        Graphics2D g = drawable.createGraphics();
        double width = drawable.getWidth2D();
        double height = drawable.getHeight2D();
        System.out.printf("w = %f, h = %f%n",width,height);
        MovingObject planets[] = new MovingObject[]{new Moon(),new Sun(),new Venus(),new Mars(),new Jupiter(),new Saturn(),new Mercury()};
        Color colors[] = new Color[]{Color.WHITE,Color.YELLOW,Color.WHITE,Color.RED,Color.MAGENTA,Color.ORANGE,Color.GRAY};
        g.setColor(drawable.isBW()?Color.BLACK:Color.YELLOW);
        Sun sun = new Sun();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2012, Calendar.MARCH, 21, 0, 0);
        for (int j=0; j<planets.length; j++)
        {
            if (j != 2 && j != 3 && j != 6)
                continue;
            double d = net.remgant.astro.Time.getDayNumber(cal);
            g.setColor(drawable.isBW()?Color.BLACK:colors[j]);
//            for (int i = 0; i < 3650; i++) {
//                d = d + (double) i / 10.0;
             for (int i = 0; i < 365; i++) {
                d = d + 1.0;
                double ra = planets[j].getRA(d);
                double decl = planets[j].getDecl(d);
                if (decl >= 180.0)
                    decl = decl - 360.0;
                double x = width - (ra / 360.0 *  width);
                double y =  (((90.0 - decl) / 180.0) *  height);
                g.fill(new Ellipse2D.Double(x,y,2.0,2.0));
            }
        }

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

    private void drawBoundaryLines(Drawable drawable)
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

    private void drawLocalScreen(Drawable drawable)
    {
        System.out.println("Cal: "+displayDate.toString());
        System.out.println("Now: "+new Date());
        double d = net.remgant.astro.Time.getDayNumber(displayDate.atZone(ZoneId.of(timeZoneName)).toLocalDate());
        System.out.println(d);
        double lon = -71.4750;
        double lat = 42.4750;
        Point2D p;

        Graphics2D g = drawable.createGraphics();
        Rectangle2D bounds = drawable.getBounds2D();
        if (!rectDisplayMode) {
            fillScreen(drawable,bounds,Color.GRAY);
            double r = bounds.getWidth();
            if (r > bounds.getHeight())
                r = bounds.getHeight();
            double x = bounds.getWidth()/2.0 - r / 2.0;
            double y = bounds.getHeight()/2.0 - r / 2.0;
            Ellipse2D e = new Ellipse2D.Double(x,y,r,r);
            g.setColor(Color.BLACK);
            g.fill(e);
        }

        for (Star o : stars) {
            double az = o.getAzimuth(d, lon, lat);
            double alt = o.getAltitude(d, lon, lat);
            double magnitude = o.getMagnitude();
            if (magnitude > maxMagnitude)
                  continue;
            if (rectDisplayMode)
                p = getRectCoordinates2D(az, alt,bounds);
            else
                p = getCircCoordinates(az, alt,bounds);
            if (p == null)
                continue;

            double x = p.getX();
            double y = p.getY();
            Color c = drawable.isBW() ? Color.BLACK : Color.WHITE;
            g.setColor(c);
            g.fill(new Ellipse2D.Double(x,y,2.0,2.0));
        }
        Moon moon = new Moon();
        double az = moon.getAzimuth(d, lon, lat);
        double alt = moon.getAltitude(d, lon, lat);
        if (rectDisplayMode)
            p = getRectCoordinates2D(az, alt, bounds);
        else
            p = getCircCoordinates(az, alt,bounds);
        System.out.println("moon: " + az + " " + alt + " " + p);
        if (p != null) {
            double size = (moon.getSize(d) / 360.0 * bounds.getWidth());
            if (size < 6.0)
                size = 6.0;
            g.setColor(Color.BLUE);
            g.fill(new Ellipse2D.Double(p.getX(),p.getY(),size,size));
        }

        Sun sun = new Sun();
        az = sun.getAzimuth(d, lon, lat);
        alt = sun.getAltitude(d, lon, lat);
        if (rectDisplayMode)
            p = getRectCoordinates2D(az, alt, bounds);
        else
            p = getCircCoordinates(az, alt,bounds);
        System.out.println("sun: " + az + " " + alt + " " + p);
        if (p == null)
            return;
        double size = (moon.getSize(d) / 360.0 * bounds.getWidth());
        if (size < 6.0)
            size = 6.0;
        g.setColor(Color.YELLOW);
        g.fill(new Ellipse2D.Double(p.getX(),p.getY(),size,size));
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
        for (int j = 0; j < 3; j++) {
            double d = net.remgant.astro.Time.getDayNumber(cal[j]);
            for (int i = 0; i < 1024; i++) {
                double az = sun.getAzimuth(d, lon, lat);
                double alt = sun.getAltitude(d, lon, lat);
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

    private void drawRiseSetScreen(Drawable drawable) {
        fillScreen(drawable,drawable.getBounds2D(),Color.GRAY);
        double w = drawable.getWidth2D();
        double h = drawable.getHeight2D() * 0.1;
        Graphics2D g = drawable.createGraphics();
        double x = 0.0;
        double y = drawable.getHeight2D() - h;
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(x,y,w,h));
    }


    private Point getRectCoordinates(double az, double alt) {
        int x = (int) (az / 360.0 * (double) screenSizeX);
        if (alt < 0.0)
            return null;
        int y = (int) (((90.0 - alt) / 90.0) * (double) screenSizeY);
        return new Point(x, y);
    }

    private Point2D getRectCoordinates2D(double az, double alt,Rectangle2D bounds) {
        if (alt < 0.0)
            return null;
        double x = az / 360.0 * bounds.getWidth();
        double y = ((90.0 - alt)  / 90.0) * bounds.getHeight();
        return new Point2D.Double(x,y);
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

    private Point2D getCircCoordinates(double az, double alt, Rectangle2D bounds) {
         if (alt < 0.0)
            return null;
        // normalize the altitude
        alt = (90.0 - alt) / 90.0;
        // adjust the azimuth
        az = Trig.rev(az + 90.0);
        double x = Trig.cos(az) * alt;
        double y = Trig.sin(az) * alt;
        double m;
        double xa;
        double ya;
        if (bounds.getWidth() > bounds.getHeight()) {
            m = bounds.getHeight();
            ya = 0.0;
            xa = (bounds.getWidth() - bounds.getHeight()) / 2.0;
        } else {
            m = bounds.getWidth();
            ya = (bounds.getHeight() - bounds.getWidth()) / 2.0;
            xa = 0.0;
        }
        return new Point2D.Double((x * (m / 2.0) + m / 2.0) + xa,((m / 2.0 - y *  m / 2.0)) + ya);
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

    private Set<Star> loadStars() {
        try (InputStream in = getClass().getResource("bsc.json").openStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[4096];
            while (in.available() > 0) {
                int length = in.read(buffer);
                if (length == -1)
                    break;
                baos.write(buffer, 0, length);
            }
            String s = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            Gson gson = new Gson();
            java.lang.reflect.Type listType = new TypeToken<List<Star>>() {
            }.getType();
            List<Star> list = gson.fromJson(s, listType);
            return new HashSet<>(list);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
            return new Rectangle2D.Double(0.0,0.0,getWidth2D(),getHeight2D());
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
        preferences.putInt("ScreenSizeX", screenSizeX);
        preferences.putInt("ScreenSizeY", screenSizeY);
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
        } else if (e.getActionCommand().equals("Rise/Set")) {
            displayMode =DisplayMode.RISE_SET;
        } else if (e.getActionCommand().equals("Planet Path")) {
            displayMode =DisplayMode.PLANET_PATH;
        }
        drawScreen(panel,displayMode);
        preferences.put("DisplayMode",displayMode.name());
    }

    // Next two are required for MouseMotionListener
    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        if (displayMode == DisplayMode.RISE_SET)
        {
            positionCursor(panel,e.getLocationOnScreen());
            drawPlanets(panel,e.getLocationOnScreen());
            panel.repaint();
        }
    }

    private void drawPlanets(Drawable drawable, Point point) {
        double d = Math.floor(net.remgant.astro.Time.getDayNumber(displayDate.atZone(ZoneId.of(timeZoneName)).toLocalDate()))
            + point.getX() / getBounds().getWidth();
        Rectangle2D bounds = drawable.getBounds2D();

        Graphics2D g = drawable.createGraphics();
        FontRenderContext fontRenderContext = g.getFontRenderContext();
        double w = drawable.getWidth2D();
        double h = drawable.getHeight2D() * 0.9;
        double x = 0.0;
        double y = 0.0;
        g.setColor(Color.GRAY);
        g.fill(new Rectangle2D.Double(x,y,w,h));

        Point2D p;
        MovingObject mo[] = new MovingObject[]{new Moon(),new Sun(),new Venus(),new Mars(),new Jupiter(),new Saturn(),new Mercury()};
        Color colors[] = new Color[]{Color.WHITE,Color.YELLOW,Color.WHITE,Color.RED,Color.MAGENTA,Color.ORANGE,Color.BLACK};
        double minSize[] = new double[]{6.0,6.0,2.0,2.0,2.0,2.0,2.0};

        for (int i=0; i<mo.length; i++)
        {
            double az = mo[i].getAzimuth(d, longitude, latitude);
            double alt = mo[i].getAltitude(d, longitude, latitude);
            p = getRectCoordinates2D(az, alt, bounds);
            if (p == null)
                continue;
            GlyphVector gv = font.createGlyphVector(fontRenderContext,mo[i].getSymbol());
            Area a = new Area(gv.getOutline());
            a.transform(AffineTransform.getTranslateInstance(p.getX(),p.getY()));
            g.setColor(colors[i]);
            g.fill(new Ellipse2D.Double(p.getX(),p.getY(),minSize[i],minSize[i]));
            g.setColor(Color.BLACK);
            g.fill(a);

        }
        System.out.println();
    }

    private void positionCursor(Drawable drawable, Point point) {
        Rectangle2D bounds = drawable.getBounds2D();
        if (point.getX() > bounds.getWidth())
            return;
        double w = drawable.getWidth2D();
        double h = drawable.getHeight2D() * 0.1;
        Graphics2D g = drawable.createGraphics();
        double x = 0.0;
        double y = drawable.getHeight2D() - h;
        if (point.getY() < y)
            return;
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(x,y,w,h));

        Rectangle2D cursor = new Rectangle2D.Double(point.getX(),y,1.0,h);
        g.setColor(Color.BLACK);
        g.fill(cursor);
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

    private Map<String,Location> loadLocations()
    {
        Map<String,Location> map = new TreeMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResource("locations.dat").openStream()));
            String line = in.readLine();
            while (line != null)
            {
                Location location = Location.parse(line);
                if (location != null)
                    map.put(location.name,location);
                line = in.readLine();
            }
        } catch (EOFException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    static class Location
    {
        String name;
        double lattitude;
        double longitude;
        String timeZone;

        Location() {
        }

        Location(String name, double lattitude, double longitude) {
            this.name = name;
            this.lattitude = lattitude;
            this.longitude = longitude;
        }

        private static Pattern p = Pattern.compile("^\\s*\"([^\"]*)\"\\s*,\\s*(\\-?\\d+(?:\\.\\d+)?)\\s*,\\s*(\\-?\\d+(?:\\.\\d+)?),\\s*([\\w/]+)\\s*?");
        static Location parse(String s)
        {
            Location location = new Location();
            Matcher m = p.matcher(s);
            if (m.matches())
            {
                location.name = m.group(1);
                location.lattitude = Double.parseDouble(m.group(2));
                location.longitude = Double.parseDouble(m.group(3));
                location.timeZone = m.group(4);
            }
            return location;
        }

        @Override
        public boolean equals(Object obj) {
            Location l = (Location)obj;
            if (l.name == null && this.name == null)
                return true;
            if (l.name == null || this.name == null)
                return false;
            return l.name.equals(this.name);
        }

        @Override
        public int hashCode() {
            if (name == null)
                return "".hashCode();
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }
    }
}