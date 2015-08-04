# BitcoinSTLGenerator

Using single private/public key pair, or JSON file containing an array of public/private key pairs, generate both .png and .stl QR code versions

- - -

## Importing into Netbeans

Navigate to folder in Netbeans and open project like normal. All dependancies will be linked and project will be immediately buildable.

## Compiling on command-line

mkdir bin
cp -r lib bin/lib
javac -d bin -sourcepath src -cp "lib/*" src/io/bci/BitcoinSTLGenerator.java
cd bin
jar cmf ../manifest.mf BitcoinSTLGenerator.jar io
