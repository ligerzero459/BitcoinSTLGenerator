/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.bci;

import com.beust.jcommander.Parameter;

/**
 *
 * @author kai
 */
public class ParamParser {
    
    @Parameter(names = { "--output", "-o" }, description = "Output file prefix")
    private String filePrefix;
    
    @Parameter(names = { "--input", "-i" }, description = "Input file name. Format is json array "
            + "with pubkey and privkey as the object keys (In progress)", hidden = true)
    private String keyFile;
    
    @Parameter(names = { "--pubkey", "-pb" }, description = "Specify public key to encode")
    private String publicKey;
    
    @Parameter(names = { "--privateKey", "-pv" }, description = "Specify private key to encode")
    private String privateKey;
    
    @Parameter(names = { "--help", "-h" }, help = true, description = "Prints this help message")
    private boolean help;
    
    @Parameter(names = { "--nostl", "-n" }, description = "Print only QR images with no STL files")
    private boolean noStl;
    
    @Parameter(names = { "--size", "-s" }, description = "Modify size of both QR code and STL. Size in pixels (1 in = 96 px)")
    private int pixelSize;
    
    @Parameter(names = { "--inch" }, description = "Modify size of both QR code and STL. Size in inches")
    private double inchSize;

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public boolean isNoStl() {
        return noStl;
    }

    public void setNoStl(boolean noStl) {
        this.noStl = noStl;
    }

    public int getPixelSize() {
        return pixelSize;
    }

    public void setPixelSize(int pixelSize) {
        this.pixelSize = pixelSize;
    }

    public double getInchSize() {
        return inchSize;
    }

    public void setInchSize(double inchSize) {
        this.inchSize = inchSize;
    }
    
}
