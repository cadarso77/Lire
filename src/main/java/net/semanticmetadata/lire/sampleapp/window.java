package net.semanticmetadata.lire.sampleapp;

import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import net.semanticmetadata.lire.imageanalysis.features.global.JCD;
import net.semanticmetadata.lire.imageanalysis.features.global.ScalableColor;
import net.semanticmetadata.lire.imageanalysis.features.global.ColorLayout;

import net.semanticmetadata.lire.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Cadarso on 17/4/17.
 */
//ChangeListener,
public class window extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Dimension dim;
    private static JLabel mode, tFolder,tFeatures;
    protected static JButton btnSelect;
    private JRadioButton radio1, radio2;
    private ButtonGroup bg;
    private JPanel radioPanel;
    private static JPanel mainPanel;
    private JButton btnTextfield;
    public static window mainWindow;
    public static JTextField textField;
    private static  JCheckBox checkMPEG,checkColorLayout,checkedgeHistogram,checkCEDD ,
            checkJCD ,checkFCTH ;

    public window() throws Exception {
        startComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tool");
        dim = super.getToolkit().getScreenSize();
        setSize(dim);
        setLocationRelativeTo(null);
        setMainWindow(mainWindow);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void setMainWindow(window window) {
        mainWindow = window;
    }

    public window getVentanaPrincipal() {
        return mainWindow;
    }

    private void startComponents() throws ParseException {
        mainPanel = new JPanel();
        mainPanel.setBounds((int) super.getToolkit().getScreenSize().getWidth() / 7,
                (int) super.getToolkit().getScreenSize().getHeight() / 63, 680, 140);
        mainPanel.setLayout(null);
        mainPanel.setBorder(new TitledBorder("App"));
        add(mainPanel);
        mode = new JLabel();
        mode.setBounds(10, 20, 400, 23);
        mode.setText("Select a mode: ");
        bg = new ButtonGroup();
        radio1 = new JRadioButton("1:Features");
        radio1.setSelected(true);
        radio2 = new JRadioButton("2:");
        textField = new JTextField();
        textField.setToolTipText("Insert the path of the folder");
        textField.setBounds(120, 60, 250, 23);
        textField.setVisible(true);
        btnTextfield = new JButton("Select...");
        btnTextfield.setBounds(370, 60, 109, 23);
        btnTextfield.setVisible(true);
        btnTextfield.addActionListener(this);
        mainPanel.add(btnTextfield);
        btnSelect = new JButton("Start");
        btnSelect.setBounds(500, 40, 150, 23);
        btnSelect.addActionListener(this);
        mainPanel.add(btnSelect);
        setLayout(null);
        mainPanel.add(mode);
        tFolder = new JLabel();
        tFolder.setText("Select the folder:");
        tFolder.setBounds(10, 60, 400, 23);
        mainPanel.add(tFolder);
        radioPanel = new JPanel(new GridLayout(1, 0));
        radioPanel.setBounds(100, 20, 200, 23);
        radioPanel.add(radio1);
        radioPanel.add(radio2);
        mainPanel.add(radioPanel);
        mainPanel.add(textField);
        bg.add(radio2);
        bg.add(radio1);
        tFeatures = new JLabel();
        tFeatures.setText("Features:");
        tFeatures.setBounds(10, 100, 400, 23);
        checkMPEG = new JCheckBox("MPEG-7");
        checkColorLayout = new JCheckBox("Color Layout");
        checkedgeHistogram = new JCheckBox("Edge Histogram");
        checkCEDD = new JCheckBox("CEDD");
        checkJCD = new JCheckBox("JCD");
        checkFCTH = new JCheckBox("FCTH");
        checkMPEG.setBounds(80, 100, 83, 20);
        checkColorLayout .setBounds(160, 100, 120, 20);
        checkedgeHistogram .setBounds(270, 100, 140, 20);
        checkCEDD .setBounds(400, 100, 70, 20);
        checkJCD .setBounds(460, 100, 60, 20);
        checkFCTH .setBounds(510, 100, 80, 20);
        checkMPEG.setName("MPEG");
        checkColorLayout.setName("ColorLayout");
        checkedgeHistogram.setName("edgeHistogram");
        checkCEDD.setName("CEDD");
        checkJCD.setName("JCD");
        checkFCTH.setName("FCTH");
        mainPanel.add(tFeatures);
        mainPanel.add(checkMPEG);
        mainPanel.add(checkColorLayout);
        mainPanel.add(checkedgeHistogram);
        mainPanel.add(checkCEDD);
        mainPanel.add(checkJCD);
        mainPanel.add(checkFCTH);
        repaint();
    }

    public static void main(String[] args) throws Exception {
        window view = new window();
    }

    public void actionPerformed(ActionEvent event)  {
        if (event.getSource() == btnTextfield) {
            selectFolder();
        }
        else if( event.getSource()== btnSelect){
            try {
                classify(textField.getText().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void selectFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choosertitle");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
            textField.setText(chooser.getSelectedFile().toString());
        } else {
            System.out.println("No Selection ");
        }
    }
    public static void classify(String path) throws IOException {
        double[][] imgFeatures = new double[0][];
        ArrayList<double[]> picFeatures;
        boolean passed = false;
        File f = new File(path);
        System.out.println("% Indexing images in " + path);
        if (f.exists() && f.isDirectory()) passed = true;
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"CreateARFFFile <directory>\" to index files of a directory.");
            System.exit(1);
        }
        // Getting all images from a directory and its sub directories.
        ArrayList<String> images = FileUtils.getAllImages(new File(path), false);

        // Features

        ArrayList<GlobalFeature> featuresUsed = new ArrayList<GlobalFeature>();

        Component[] components = mainPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JCheckBox) {
                JCheckBox checkAux = (JCheckBox) components[i];
                if (checkAux.isSelected() && checkAux.getName() == "MPEG") {
                    featuresUsed.add(new ScalableColor());
                } else if (checkAux.isSelected() && checkAux.getName() == "ColorLayout") {
                    featuresUsed.add(new ColorLayout());
                } else if (checkAux.isSelected() && checkAux.getName() == "edgeHistogram") {
                    featuresUsed.add(new EdgeHistogram());
                } else if (checkAux.isSelected() && checkAux.getName() == "CEDD") {
                    featuresUsed.add(new CEDD());
                } else if (checkAux.isSelected() && checkAux.getName() == "JCD") {
                    featuresUsed.add(new JCD());
                } else if (checkAux.isSelected() && checkAux.getName() == "FCTH") {
                    featuresUsed.add(new FCTH());
                }

            }

        }
        for (int j = 0; j < featuresUsed.size(); j++)
            System.out.println(featuresUsed.get(j).getFeatureName());

        if (images.size() > 0) {
            // getting the number of dimensions:
            int sumFeatures = 0;
            for (int j = 0; j < featuresUsed.size(); j++) {
                featuresUsed.get(j).extract(ImageIO.read(new FileInputStream(images.get(0))));
                sumFeatures = sumFeatures + featuresUsed.get(j).getFeatureVector().length;
            }
            System.out.println(featuresUsed.size());
            System.out.println(sumFeatures);
            imgFeatures = new double[images.size()][sumFeatures];
            // Iterating through images building the low level features

            int j = 0;
            for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
                String imageFilePath = it.next();
                int localSum = 0;
                for (int d = 0; d < featuresUsed.size(); d++) {
                    BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                    featuresUsed.get(d).extract(img);
                    System.out.print(imageFilePath);
                    for (int e = 0; e < featuresUsed.get(d).getFeatureVector().length; e++) {
                        try {
                            double v = featuresUsed.get(d).getFeatureVector()[e];
                            imgFeatures[j][localSum] = v;
                            localSum++;
                        } catch (Exception i) {
                            System.err.println("Error reading image or indexing it.");
                            i.printStackTrace();
                        }
                    }
                    System.out.println(localSum);
                }
                j++;
            }
            System.out.println("% Finished indexing.");
        } else {
            System.err.println("No images found in kgiven directory: " + path);
        }

        double[] folderPl = new double[images.size()];
        Map weight = new HashMap();
        int numF = 0;
        int start=0;
        boolean empty = false;
        ArrayList<String> imagesAux = new ArrayList<String>();
        int picture = 0;
       picFeatures = new ArrayList<double[]>();
        while (images.size() > 0) {
            System.out.println("numero de iamges" + images.size());
            for (int u = 0; u < images.size(); u++) {
                System.out.println("imagen" + u);
                boolean higher=true;
                boolean once=false;
                for (int e = 0; e < featuresUsed.size(); e++) {
                    for (int i = 0; i < featuresUsed.get(e).getFeatureVector().length; i++) {
                        if (imgFeatures[u][i] == imgFeatures[0][i]) {
                            numF++;
                        }
                    }
                    System.out.println(imgFeatures[0][0]+"||"+numF);
                    float result = ((float) numF / (float) featuresUsed.get(e).getFeatureVector().length);
                    System.out.println(u + "  " + featuresUsed.get(e).getFeatureName() + result);

                    if (result > 0.6 && higher) {
                        String key = "" + u;
                        if(e==featuresUsed.size()-1)
                            weight.put((key), result);
                    }
                    else {
                        if(!once){
                            start=u;
                            // System.out.println("Tamaño antes de borrar" + images.size());
                            imagesAux.add(images.get(u));
                            picFeatures.add(imgFeatures[u]);
                            // System.out.println("Tamaño despues de borrar" + images.size());
                            higher=false;
                            once=true;
                        }
                    }
                    numF = 0;
                }
            }
            images.removeAll(imagesAux);

            for (int i = 0; i < images.size(); i++)
                System.out.println(images.get(i));
            picture++;

            File file = new File(path + "/picture" + picture);
            if (!file.exists()) {
                if (file.mkdir()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }

            for (int i = 0; i < images.size(); i++) {
                copy(images.get(i), file.getAbsolutePath() + "/pic" + i + ".jpg");
                System.out.println("copy..."+i);
            }




            images.clear();
            images.addAll(imagesAux);
            imagesAux.clear();
        }

    }


    public static boolean copy(String originFile, String detinyFile) {
        File origin;
        File destiny;
        FileInputStream in = null;
        FileOutputStream out = null;
        boolean b;
        try {
            origin = new File(originFile);
            destiny = new File(detinyFile);

            if (b = origin.exists()) {
                if (b = origin.canRead()) {
                    in = new FileInputStream(origin);
                    out = new FileOutputStream(destiny);
                    int c;
                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
            b = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
                b = false;
            }
        }
        return b;
    }

}