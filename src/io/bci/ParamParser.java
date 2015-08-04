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
    public String filePrefix;
    
    @Parameter(names = { "--input", "-i" }, description = "Input file name. Format is json array "
            + "with pubkey and privkey as the object keys (In progress)")
    public String keyFile;
    
    @Parameter(names = { "--pubkey", "-pb" }, description = "Specify public key to encode")
    public String publicKey;
    
    @Parameter(names = { "--privateKey", "-pv" }, description = "Specify private key to encode")
    public String privateKey;
    
    @Parameter(names = { "--help", "-h" }, help = true, description = "Prints this help message")
    public boolean help;
}
