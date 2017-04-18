

package net.semanticmetadata.lire.sampleapp;


import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import net.semanticmetadata.lire.imageanalysis.features.global.JCD;
import net.semanticmetadata.lire.imageanalysis.features.global.mpeg7.ColorLayoutImpl;
import net.semanticmetadata.lire.utils.FileUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * User: Mathias Lux, mathias@juggle.at
 * Date: 06.02. 2014
 */
public class CreateARFFFile {


    private static String pathFolder;

    public static void main(String[] args) throws IOException {
        double[][] matrixF1 = null;
        double[][] matrixF2 = null;
        double[][] matrixF3 = null;

        boolean passed = false;
        String a = "/Users/Cadarso/Desktop/image/tiger";
        pathFolder = a;
        File f = new File(a);
        System.out.println("% Indexing images in " + a);
        if (f.exists() && f.isDirectory()) passed = true;
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"CreateARFFFile <directory>\" to index files of a directory.");
            System.exit(1);
        }
        // Getting all images from a directory and its sub directories.
        ArrayList<String> images = FileUtils.getAllImages(new File(a), true);
        // Features
        GlobalFeature feature = new CEDD();
        GlobalFeature feature1 = new FCTH();
        GlobalFeature feature2 = new JCD();
        GlobalFeature feature3 = new EdgeHistogram();

        if (images.size() > 0) {
            // getting the number of dimensions:
            feature.extract(ImageIO.read(new FileInputStream(images.get(0))));
            feature1.extract(ImageIO.read(new FileInputStream(images.get(0))));
            feature2.extract(ImageIO.read(new FileInputStream(images.get(0))));

            // Iterating through images building the low level features
            matrixF1 = new double[images.size()][feature.getFeatureVector().length];
            matrixF2 = new double[images.size()][feature1.getFeatureVector().length];
            matrixF3 = new double[images.size()][feature2.getFeatureVector().length];
            ArrayList<Integer>[][] matrix;
            int j = 0;
            for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
                String imageFilePath = it.next();
                try {
                    BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                    System.out.print(imageFilePath);
                    feature.extract(img);
                    feature1.extract(img);
                    feature2.extract(img);
                    for (int i = 0; i < feature.getFeatureVector().length; i++) {
                        double v = feature.getFeatureVector()[i];
                        matrixF1[j][i] = v;
                    }
                    for (int i = 0; i < feature1.getFeatureVector().length; i++) {
                        double v1 = feature1.getFeatureVector()[i];
                        matrixF2[j][i] = v1;
                    }
                    for (int i = 0; i < feature2.getFeatureVector().length; i++) {
                        double v2 = feature2.getFeatureVector()[i];
                        matrixF3[j][i] = v2;
                    }
                    System.out.println();
                    j++;
                } catch (Exception e) {
                    System.err.println("Error reading image or indexing it.");
                    e.printStackTrace();
                }
            }
            System.out.println("% Finished indexing.");
        } else {
            System.err.println("No images found in kgiven directory: " + a);
        }

        double[] folderPl = new double[images.size()];
        Map weight = new HashMap();
        int numF = 0;
        boolean empty = false;
        ArrayList<String> imagesAux = new ArrayList<String>();
        int picture = 0;

        while (images.size() > 0) {
            for (int u = 0; u < images.size(); u++) {
                for (int i = 0; i < feature.getFeatureVector().length; i++) {
                    if (0 != u) {
                        double v = feature.getFeatureVector()[i];
                        if (matrixF1[u][i] == matrixF1[0][i]) {
                            empty = true;
                            numF++;
                        }
                    }
                }
                if (empty) {
                    System.out.println();
                    float result = ((float) numF / (float) feature.getFeatureVector().length);
                    System.out.println(u + " CEDD (%): " + result);
                    if (result > 0.7) {
                        String key = "" + u;
                        System.out.println(key);
                        weight.put((key), result);
                    } else {
                        imagesAux.add(images.get(u));
                    }
                    // }
                    numF = 0;
                    empty = false;
                }
            }
            System.out.println("tamaño" + images.size());
            images.removeAll(imagesAux);
            System.out.println("tamaño dos" + images.size());
            for(int i=0;i<images.size();i++)
                System.out.println(images.get(i));
            for (int u = 0; u < images.size(); u++) {
                for (int i = 0; i < feature2.getFeatureVector().length; i++) {
                    if (0 != u) {
                        double v = feature2.getFeatureVector()[i];
                        if (matrixF2[u][i] == matrixF2[0][i]) {
                            empty = true;
                            numF++;
                        }
                    }
                }
                if (empty) {
                    System.out.println();
                    float result = ((float) numF / (float) feature2.getFeatureVector().length);
                    System.out.println("CEDD (%): " + result);
                    if (result > 0.7) {
                        String key = "" + u;
                        System.out.println(key);
                        weight.put((key), result);
                    } else {
                        imagesAux.add(images.get(u));
                    }
                    numF = 0;
                    empty = false;

                }
            }
            System.out.println("tamaño" + images.size());
            images.removeAll(imagesAux);
            System.out.println("tamaño dos" + images.size());
            for(int i=0;i<images.size();i++)
                System.out.println(images.get(i));
            for (int u = 0; u < images.size(); u++) {
                for (int i = 0; i < feature3.getFeatureVector().length; i++) {
                    if (0 != u) {
                        double v = feature3.getFeatureVector()[i];
                        if (matrixF3[u][i] == matrixF3[0][i]) {
                            empty = true;
                            numF++;
                        }
                    }
                }
                if (empty) {
                    System.out.println();
                    float result = ((float) numF / (float) feature3.getFeatureVector().length);
                    System.out.println("CEDD (%): " + result);
                    if (result > 0.65) {
                        String key = "" + u;
                        System.out.println(key);
                        weight.put((key), result);
                    } else {
                        imagesAux.add(images.get(u));
                    }
                    // }
                    numF = 0;
                    empty = false;

                }
            }
            System.out.println("tamaño" + images.size());
            images.removeAll(imagesAux);
            System.out.println("tamaño dos" + images.size());
            for(int i=0;i<images.size();i++)
                System.out.println(images.get(i));

            picture++;
            File file = new File(pathFolder + "/picture" + picture);
            if (!file.exists()) {
                if (file.mkdir()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }

            for (int i = 0; i < images.size(); i++) {
                copiar(images.get(i), file.getAbsolutePath() + "/pic" + i + ".jpg");
                System.out.println("copy..."+i);
            }
            images.clear();
            images.addAll(imagesAux);
            imagesAux.clear();
        }
    }


    public static boolean copiar(String origen, String destino) {
        File archivoOrigen;
        File archivoDestino;
        FileInputStream in = null;
        FileOutputStream out = null;
        boolean b;
        try {
            archivoOrigen = new File(origen);
            archivoDestino = new File(destino);

            /**
             * Validamos que el archivo de origen exista. En caso de que no
             * exista saldremos del método
             */
            if (b = archivoOrigen.exists()) {
                /**
                 * Validamos que el archivo de origen se pueda leer
                 */
                if (b = archivoOrigen.canRead()) {
                    /**
                     * Creamos el lector y el escritor
                     */
                    in = new FileInputStream(archivoOrigen);
                    out = new FileOutputStream(archivoDestino);

                    /**
                     * Mientras se lee de un lado por otro lado se escribe
                     */
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





