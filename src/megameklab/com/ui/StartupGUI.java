package megameklab.com.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import megamek.client.ui.swing.widget.MegamekButton;
import megamek.client.ui.swing.widget.SkinSpecification;
import megamek.client.ui.swing.widget.SkinXMLHandler;
import megamek.common.Configuration;
import megamek.common.util.EncodeControl;
import megamek.common.util.ImageUtil;
import megamek.common.util.MegaMekFile;
import megameklab.com.MegaMekLab;
import megameklab.com.ui.dialog.LoadingDialog;

/**
 *
 * @author Taharqa
 */
public class StartupGUI extends javax.swing.JPanel {

    
    private static final long serialVersionUID = 8376874926997734492L;
    JFrame frame;
    Image imgSplash;
    BufferedImage backgroundIcon;
    
    private MegamekButton btnNewMek;
    private MegamekButton btnNewProto;
    private MegamekButton btnNewVee;
    private MegamekButton btnNewSupportVee;
    private MegamekButton btnNewAero;
    private MegamekButton btnNewDropper;
    private MegamekButton btnNewLargeCraft;
    private MegamekButton btnNewBA;
    private MegamekButton btnNewPbi;
    private MegamekButton btnQuit;
    
    /** A map of resolution widths to file names for the startup screen */
    private final TreeMap<Integer, String> startupScreenImages = new TreeMap<>();
    {
        startupScreenImages.put(0, "data/images/misc/mml_start_spooky_hd.jpg");
        startupScreenImages.put(1441, "data/images/misc/mml_start_spooky_fhd.jpg");
        startupScreenImages.put(1921, "data/images/misc/mml_start_spooky_uhd.jpg");
    }
    
    /** booleans for types */
    public final static int T_MEK    = 0;
    public final static int T_VEE    = 1;
    public final static int T_SVEE   = 2;
    public final static int T_PROTO  = 3;
    public final static int T_BA     = 4;
    public final static int T_PBI    = 5;
    public final static int T_AERO   = 6;
    public final static int T_DROP   = 7;
    public final static int T_LCRAFT = 8;
    
    public StartupGUI() {
       
        initComponents();
    }

    private void initComponents() {

        SkinSpecification skinSpec = SkinXMLHandler.getSkin(SkinSpecification.UIComponents.MainMenuBorder.getComp(),
                true);
        
        frame = new JFrame("MegaMekLab");

        setBackground(Color.DARK_GRAY);

        ResourceBundle resourceMap = ResourceBundle.getBundle("megameklab.resources.Splash", new EncodeControl()); //$NON-NLS-1$
        
        imgSplash = getToolkit().getImage(startupScreenImages.floorEntry((int)MegaMekLab.calculateMaxScreenWidth()).getValue());
      
        
        // wait for splash image to load completely
        MediaTracker tracker = new MediaTracker(frame);
        tracker.addImage(imgSplash, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            // really should never come here
        }
        
        // make splash image panel
        ImageIcon icon = new ImageIcon(imgSplash);
        JLabel panTitle = new JLabel(icon);
        add(panTitle, BorderLayout.CENTER);
        
        if (skinSpec.hasBackgrounds()) {
            if (skinSpec.backgrounds.size() > 1) {
                File file = new MegaMekFile(Configuration.widgetsDir(),
                        skinSpec.backgrounds.get(1)).getFile();
                if (!file.exists()){
                    System.err.println("MainMenu Error: background icon doesn't exist: "
                            + file.getAbsolutePath());
                } else {
                    backgroundIcon = (BufferedImage) ImageUtil.loadImageFromFile(file.toString());
                }
            }
        } else {
            backgroundIcon = null;
        }
        
        JLabel labVersion = new JLabel(resourceMap.getString("version.text") + MegaMekLab.VERSION, JLabel.CENTER); //$NON-NLS-1$
        labVersion.setPreferredSize(new Dimension(250,15));
        if (skinSpec.fontColors.size() > 0) {
            labVersion.setForeground(skinSpec.fontColors.get(0));
        }
        
        btnNewMek = new MegamekButton(resourceMap.getString("btnNewMek.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewMek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_MEK);
            }
        });
        
        btnNewVee = new MegamekButton(resourceMap.getString("btnNewVee.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewVee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_VEE);
            }
        });
        
        btnNewSupportVee = new MegamekButton(resourceMap.getString("btnNewSupportVee.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewSupportVee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_SVEE);
            }
        });
        
        btnNewBA = new MegamekButton(resourceMap.getString("btnNewBA.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewBA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_BA);
            }
        });
        
        btnNewAero = new MegamekButton(resourceMap.getString("btnNewAero.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewAero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_AERO);
            }
        });
        btnNewDropper = new MegamekButton(resourceMap.getString("btnNewDropper.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewDropper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_DROP);
            }
        });
        
        btnNewLargeCraft = new MegamekButton(resourceMap.getString("btnNewLargeCraft.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewLargeCraft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_LCRAFT);
            }
        });
        
        btnNewProto = new MegamekButton(resourceMap.getString("btnNewProto.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewProto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_PROTO);
            }
        });
        
        btnNewPbi = new MegamekButton(resourceMap.getString("btnNewPbi.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnNewPbi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newUnit(T_PBI);
            }
        });
        
        btnQuit = new MegamekButton(resourceMap.getString("btnQuit.text"), //$NON-NLS-1$
                SkinSpecification.UIComponents.MainMenuButton.getComp(), true);
        btnQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.exit(0);
            }
        });
        
        // Use the current monitor so we don't "overflow" computers whose primary
        // displays aren't as large as their secondary displays.
        DisplayMode currentMonitor = frame.getGraphicsConfiguration().getDevice().getDisplayMode();        
        FontMetrics metrics = btnNewDropper.getFontMetrics(btnNewDropper.getFont());
        int width = metrics.stringWidth(btnNewDropper.getText());
        int height = metrics.getHeight();
        Dimension textDim =  new Dimension(width+50, height+10);

        // Strive for no more than ~90% of the screen and use golden ratio to make
        // the button width "look" reasonable.
        int imageWidth = imgSplash.getWidth(frame);
        int maximumWidth = (int)(0.9 * currentMonitor.getWidth()) - imageWidth;
        //no more than 50% of image width
        if(maximumWidth > (int) (0.5 * imageWidth)) {
            maximumWidth = (int) (0.5 * imageWidth);
        }
        Dimension minButtonDim = new Dimension((int)(maximumWidth / 1.618), 25);
        if (textDim.getWidth() > minButtonDim.getWidth()) {
            minButtonDim = textDim;
        }
        
        btnNewMek.setMinimumSize(minButtonDim);
        btnNewMek.setPreferredSize(minButtonDim);
        btnNewVee.setMinimumSize(minButtonDim);
        btnNewVee.setPreferredSize(minButtonDim);
        btnNewSupportVee.setMinimumSize(minButtonDim);
        btnNewSupportVee.setPreferredSize(minButtonDim);
        btnNewBA.setMinimumSize(minButtonDim);
        btnNewBA.setPreferredSize(minButtonDim);
        btnNewAero.setMinimumSize(minButtonDim);
        btnNewAero.setPreferredSize(minButtonDim);
        btnNewDropper.setMinimumSize(minButtonDim);
        btnNewDropper.setPreferredSize(minButtonDim);
        btnNewLargeCraft.setMinimumSize(minButtonDim);
        btnNewLargeCraft.setPreferredSize(minButtonDim);
        btnNewPbi.setMinimumSize(minButtonDim);
        btnNewPbi.setPreferredSize(minButtonDim);
        btnNewProto.setMinimumSize(minButtonDim);
        btnNewProto.setPreferredSize(minButtonDim);
        btnQuit.setMinimumSize(minButtonDim);
        btnQuit.setPreferredSize(minButtonDim);
        
        // layout
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        // Left Column
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 5, 10, 10);
        c.ipadx = 10; c.ipady = 5;
        c.gridx = 0;  c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0; c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 11;
        add(panTitle, c);
        // Right Column
        c.insets = new Insets(2, 2, 2, 10);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0; c.weighty = 1.0;
        c.ipadx = 0; c.ipady = 0;
        c.gridheight = 1;
        c.gridx = 1; c.gridy = 0;
        add(labVersion, c);
        c.gridy++;
        add(btnNewMek, c);
        c.gridy++;
        add(btnNewVee, c);
        c.gridy++;
        add(btnNewSupportVee, c);
        c.gridy++;
        add(btnNewProto, c);
        c.gridy++;
        add(btnNewBA, c);
        c.gridy++;
        add(btnNewPbi, c);
        c.gridy++;
        add(btnNewAero, c);
        c.gridy++;
        add(btnNewDropper, c);
        c.gridy++;
        add(btnNewLargeCraft, c);
        c.gridy++;
        add(btnQuit, c);
        
        frame.setResizable(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
                System.exit(0);
            }
        });
        frame.validate();
        frame.pack();
        // Determine the location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (currentMonitor.getWidth()-w)/2;
        int y = (currentMonitor.getHeight()-h)/2;
        frame.setLocation(x, y);
        
        frame.setVisible(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundIcon == null){
            return;
        }
        int w = getWidth();
        int h = getHeight();
        int iW = backgroundIcon.getWidth();
        int iH = backgroundIcon.getHeight();
        // If the image isn't loaded, prevent an infinite loop
        if ((iW < 1) || (iH < 1)) {
            return;
        }
        for (int x = 0; x < w; x+=iW){
            for (int y = 0; y < h; y+=iH){
                g.drawImage(backgroundIcon, x, y,null);
            }
        }
     }

    private void newUnit(int type) {
        frame.setVisible(false);
        LoadingDialog ld = new LoadingDialog(frame, type);
        ld.setVisible(true);
    }
}
