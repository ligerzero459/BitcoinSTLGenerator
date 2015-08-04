/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.bci;

import com.beust.jcommander.JCommander;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

/**
 *
 * @author kai
 */
public class BitcoinSTLGenerator {

    private static String publicKey;
    private static String privateKey;
    private static String filePrefix;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ParamParser parse = new ParamParser();
        JCommander jcom = new JCommander(parse, args);
        jcom.setProgramName("BitcoinSTLGenerator");

        if (parse.help) {
            jcom.usage();
            System.exit(0);
        }

        if (parse.publicKey == null || parse.privateKey == null) {
            // Once keyfile parsing is implemented, this will be used
//            if (parse.keyFile == null) {
//                System.out.println("No keyfile specified. You must supply both public and private key to create STL file.");
//                System.exit(-1);
//            }

            // Until then, this logic will warn users to input both public and private keys
            System.out.println("You must supply both public and private key to create STL file.");
            System.exit(-2);
        } else {
            publicKey = parse.publicKey;
            privateKey = parse.privateKey;
        }

        if (parse.filePrefix != null) {
            filePrefix = parse.filePrefix + "_";
        } else {
            filePrefix = "";
        }

        checkSaveDir();

//        System.out.println("Public key: "+ parse.publicKey);
//        System.out.println("Private key: " + parse.privateKey);
        // Call generateQRCode(String) for both public and private key
        // Expandable for keys read from a file later
        ByteArrayOutputStream pubKey = generateQRCode(publicKey);
        ByteArrayOutputStream privKey = generateQRCode(privateKey);

        saveKeys(pubKey, privKey, 1);
    }

    private static void checkSaveDir() {
        File imageDir = new File("images");
        File stlDir = new File("STLs");

        if (!imageDir.exists()) {
            if (imageDir.mkdir()) {
                System.out.println("Image directory created successfully!");
            }
        }

        if (!stlDir.exists()) {
            if (stlDir.mkdir()) {
                System.out.println("STL directory created successfully!");
            }
        }
    }

    private static ByteArrayOutputStream generateQRCode(String key) {
        ByteArrayOutputStream qr;

        // Generate QR Code as PNG
        qr = QRCode.from(key)
                .to(ImageType.PNG)
                .withSize(500, 500)
                .stream();

        return qr;
    }

    private static void saveKeys(ByteArrayOutputStream pub, ByteArrayOutputStream priv, int index) {
        // Saving public key first
        String pubFileName = getFilePrefix() + "pubKey_" + index;
        String privFileName = getFilePrefix() + "privKey_" + index;

        File pubFile = new File("images/" + pubFileName + ".png");
        File privFile = new File("images/" + privFileName + ".png");

        try {
            FileOutputStream fout = new FileOutputStream(pubFile);

            fout.write(pub.toByteArray());

            fout.flush();
            fout.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Public key file creation failed: " + pubFile.toString());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: Public key file read/write failed: " + pubFile.toString());
            e.printStackTrace();
        } finally {
            System.out.println("Public key file created: " + pubFile.toString());
        }

        try {
            FileOutputStream fout = new FileOutputStream(privFile);

            fout.write(priv.toByteArray());

            fout.flush();
            fout.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Private key file creation failed: " + privFile.toString());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: Private key file read/write failed: " + privFile.toString());
            e.printStackTrace();
        } finally {
            System.out.println("Private key file created: " + privFile.toString());
        }

        drawSTL(pubFile, pubFileName);
        drawSTL(privFile, privFileName);
    }

    private static void drawSTL(File qrFile, String filename) {
        int height = -250;
        int width = -250;
        float PLATFORM_RATIO = 15.0f;
        float PLATFORM_HEIGHT = 1.0f;
        boolean printPlatform = true;
        boolean printQRbottom = true;
        STLDrawer drawer = null;
        float[] pofloat1 = new float[3];
        float[] pofloat2 = new float[3];
        float[] pofloat3 = new float[3];
        float[] pofloat4 = new float[3];
        Color color;
        int count;
        BufferedImage image = null;

        try {
            drawer = new STLDrawer("STLs/" + filename + ".stl");
        } catch (FileNotFoundException e) {
            try {
                System.out.println("ERROR: Unable to create stl file! - " + drawer.getFilename());
            } catch (IOException ex) {
                Logger.getLogger(BitcoinSTLGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            e.printStackTrace();
        }

        try {
            image = javax.imageio.ImageIO.read(qrFile);
            height = image.getHeight();
            width = image.getWidth();
        } catch (IOException e) {
            System.out.println("ERROR: Unable to open picture file! - " + qrFile.toString());
            e.printStackTrace();
        }

        if (printPlatform) {
            //Base background
            pofloat1[0] = 0.0f;
            pofloat1[1] = 0.0f;
            pofloat1[2] = 0;

            pofloat2[0] = 0.0f + (float) width;
            pofloat2[1] = 0.0f + 0.0f;
            pofloat2[2] = 0;

            pofloat3[0] = 0.0f + (float) width;
            pofloat3[1] = 0.0f + (float) height;
            pofloat3[2] = 0;

            pofloat4[0] = 0.0f + 0.0f;
            pofloat4[1] = 0.0f + (float) height;
            pofloat4[2] = 0;
            drawer.drawCube(pofloat1, pofloat2, pofloat3, pofloat4, PLATFORM_HEIGHT);
            //end base background
        }

        int[][] drawArray = new int[width + 2][height + 2];

        for (int jj = 0; jj < height; jj++) {
            for (int ii = 0; ii < width; ii++) {
                color = new Color(image.getRGB(ii, jj));

                if (color.equals(Color.BLACK)) {
                    drawArray[ii + 1][height - jj + 1] = 1; //PNG origin starts at top left of image, my origin is bottom left
                }
            }
        }

        count = 0;
        for (int jj = 1; jj < height + 2; jj++) //drawTop
        {

            for (int ii = 1; ii < width + 2; ii++) {
                if (drawArray[ii][jj] == 1) {
                    while ((ii + count < width + 2) && (drawArray[ii + count][jj] == 1) && (drawArray[ii + count][jj + 1] == 0)) {
                        count++;
                    }

                    pofloat1[0] = (float) ii;
                    pofloat1[1] = (float) jj;
                    pofloat1[2] = PLATFORM_HEIGHT;

                    pofloat4[0] = (float) ii + 0.0f;
                    pofloat4[1] = (float) jj + 1.0f;
                    pofloat4[2] = PLATFORM_HEIGHT;

                    pofloat3[0] = (float) ii + count;//1.0f;
                    pofloat3[1] = (float) jj + 1.0f;
                    pofloat3[2] = PLATFORM_HEIGHT;

                    pofloat2[0] = (float) ii + 1.0f;
                    pofloat2[1] = (float) jj + 0.0f;
                    pofloat2[2] = PLATFORM_HEIGHT;

                    ii = ii + count;

                    if (count != 0) {
                        drawer.drawTop(pofloat1, pofloat2, pofloat3, pofloat4, PLATFORM_RATIO);
                    }
                    count = 0;
                }

            }

        }
        count = 0;
        for (int jj = 1; jj < height + 2; jj++) //bottom
        {

            for (int ii = 1; ii < width + 2; ii++) {
                if (drawArray[ii][jj] == 1) {
                    while ((ii + count < width + 2) && (drawArray[ii + count][jj] == 1)
                            && (drawArray[ii + count][jj - 1] == 0)) {
                        count++;
                    }

                    pofloat1[0] = (float) ii;
                    pofloat1[1] = (float) jj;
                    pofloat1[2] = PLATFORM_HEIGHT;

                    pofloat4[0] = (float) ii + 0.0f;
                    pofloat4[1] = (float) jj + 1.0f;
                    pofloat4[2] = PLATFORM_HEIGHT;

                    pofloat3[0] = (float) ii + 1.0f;
                    pofloat3[1] = (float) jj + 1.0f;
                    pofloat3[2] = PLATFORM_HEIGHT;

                    pofloat2[0] = (float) ii + count;//1.0f;
                    pofloat2[1] = (float) jj + 0.0f;
                    pofloat2[2] = PLATFORM_HEIGHT;

                    if (count != 0) {
                        drawer.drawBottom(pofloat1, pofloat2, pofloat3, pofloat4, PLATFORM_RATIO);
                    }
                    ii = ii + count;
                    count = 0;

                }

            }

        }

        for (int ii = 1; ii < width + 2; ii++) //draw right
        {

            for (int jj = 1; jj < height + 2; jj++) {
                if (drawArray[ii][jj] == 1) {
                    while ((jj + count < height + 2) && (drawArray[ii][jj + count] == 1)
                            && (drawArray[ii + 1][jj + count] == 0)) {
                        count++;
                    }

                    pofloat1[0] = (float) ii;
                    pofloat1[1] = (float) jj;
                    pofloat1[2] = PLATFORM_HEIGHT;

                    pofloat4[0] = (float) ii + 0.0f;
                    pofloat4[1] = (float) jj + 1.0f;
                    pofloat4[2] = PLATFORM_HEIGHT;

                    pofloat3[0] = (float) ii + 1.0f;
                    pofloat3[1] = (float) jj + count;//1.0f;
                    pofloat3[2] = PLATFORM_HEIGHT;

                    pofloat2[0] = (float) ii + 1.0f;
                    pofloat2[1] = (float) jj + 0.0f;
                    pofloat2[2] = PLATFORM_HEIGHT;

                    if (count != 0) {
                        drawer.drawRight(pofloat1, pofloat2, pofloat3, pofloat4, PLATFORM_RATIO);
                    }

                    jj = jj + count;
                    count = 0;
                }

            }

        }

        for (int ii = 1; ii < width + 2; ii++) //draw left
        {

            for (int jj = 1; jj < height + 2; jj++) {
                if (drawArray[ii][jj] == 1) {
                    while ((jj + count < height + 2) && (drawArray[ii][jj + count] == 1)
                            && (drawArray[ii - 1][jj + count] == 0)) {
                        count++;
                    }

                    pofloat1[0] = (float) ii;
                    pofloat1[1] = (float) jj;
                    pofloat1[2] = PLATFORM_HEIGHT;

                    pofloat4[0] = (float) ii + 0.0f;
                    pofloat4[1] = (float) jj + count;//1.0f;
                    pofloat4[2] = PLATFORM_HEIGHT;

                    pofloat3[0] = (float) ii + 1.0f;
                    pofloat3[1] = (float) jj + 1.0f;
                    pofloat3[2] = PLATFORM_HEIGHT;

                    pofloat2[0] = (float) ii + 1.0f;
                    pofloat2[1] = (float) jj + 0.0f;
                    pofloat2[2] = PLATFORM_HEIGHT;

                    if (count != 0) {
                        drawer.drawLeft(pofloat1, pofloat2, pofloat3, pofloat4, PLATFORM_RATIO);
                    }

                    jj = jj + count;
                    count = 0;
                }

            }

        }

        for (int jj = 1; jj < height + 2; jj++) //draw back
        {

            for (int ii = 1; ii < width + 2; ii++) {
                if (drawArray[ii][jj] == 1) {
                    while ((ii + count < width + 2) && (drawArray[ii + count][jj] == 1)) {
                        count++;
                    }

                    pofloat1[0] = (float) ii;
                    pofloat1[1] = (float) jj;
                    pofloat1[2] = PLATFORM_HEIGHT;

                    pofloat4[0] = (float) ii + 0.0f;
                    pofloat4[1] = (float) jj + 1.0f;
                    pofloat4[2] = PLATFORM_HEIGHT;

                    pofloat3[0] = (float) ii + count;//1.0f;
                    pofloat3[1] = (float) jj + 1.0f;
                    pofloat3[2] = PLATFORM_HEIGHT;

                    pofloat2[0] = (float) ii + count;//1.0f;
                    pofloat2[1] = (float) jj + 0.0f;
                    pofloat2[2] = PLATFORM_HEIGHT;

                    if (count != 0) {
                        drawer.drawBack(pofloat1, pofloat2, pofloat3, pofloat4, PLATFORM_RATIO);
                    }
                    ii = ii + count;
                    count = 0;
                }

            }

        }

        if (printQRbottom) {
            for (int jj = 1; jj < height + 2; jj++) //draw front
            {

                for (int ii = 1; ii < width + 2; ii++) {
                    if (drawArray[ii][jj] == 1) {
                        while ((ii + count < width + 2) && (drawArray[ii + count][jj] == 1)) {
                            count++;
                        }

                        pofloat1[0] = (float) ii;
                        pofloat1[1] = (float) jj;
                        pofloat1[2] = PLATFORM_HEIGHT;

                        pofloat4[0] = (float) ii + 0.0f;
                        pofloat4[1] = (float) jj + 1.0f;
                        pofloat4[2] = PLATFORM_HEIGHT;

                        pofloat3[0] = (float) ii + count;//1.0f;
                        pofloat3[1] = (float) jj + 1.0f;
                        pofloat3[2] = PLATFORM_HEIGHT;

                        pofloat2[0] = (float) ii + count;//1.0f;
                        pofloat2[1] = (float) jj + 0.0f;
                        pofloat2[2] = PLATFORM_HEIGHT;

                        if (count != 0) {
                            drawer.drawFront(pofloat1, pofloat2, pofloat3, pofloat4, PLATFORM_RATIO);
                        }
                        ii = ii + count;
                        count = 0;
                    }

                }

            }
        }

        drawer.resizeNumTriangles();
        
        try {
            System.out.println("STL drawn: " + drawer.getFilename());
        } catch (IOException ex) {
            Logger.getLogger(BitcoinSTLGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        drawer.closeFile();

    }

    public static String getPublicKey() {
        return publicKey;
    }

    public static void setPublicKey(String publicKey) {
        BitcoinSTLGenerator.publicKey = publicKey;
    }

    public static String getPrivateKey() {
        return privateKey;
    }

    public static void setPrivateKey(String privateKey) {
        BitcoinSTLGenerator.privateKey = privateKey;
    }

    public static String getFilePrefix() {
        return filePrefix;
    }

    public static void setFilePrefix(String filePrefix) {
        BitcoinSTLGenerator.filePrefix = filePrefix;
    }

}
