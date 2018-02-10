package catchytube.com;


import java.io.File;


public class Main {

    public static void main(String[] args) throws Exception {
        File inputFile1 = new File("Crypt/Material Design Motion.mp4");
        File inputFile2 = new File("Crypt/Enrique-Iglesias---SUBEME-LA-RADIO-Official-Video-ft-Descemer-Bueno-Zion--Lennox.mp4");
        File inputFile3 = new File("Crypt/Justin Bieber - Sorry (PURPOSE   The Movement).mp4");
        File encFile1 = new File("Crypt/Material Design Motion.cry");
        File encFile2 = new File("Crypt/Enrique-Iglesias---SUBEME-LA-RADIO-Official-Video-ft-Descemer-Bueno-Zion--Lennox.cry");
        File encFile3 = new File("Crypt/Justin Bieber - Sorry (PURPOSE   The Movement).cry");
        SymmetricCryptoUtils as = new SymmetricCryptoUtils();
        as.encodeFile(inputFile1);
//        as.encodeFile(inputFile2);
//        as.encodeFile(inputFile3);
//        as.decodeFile(encFile1);
//        as.decodeFile(encFile2);
//        as.decodeFile(encFile3);
        System.out.println(FileUtils.encrypt(inputFile1.getName()));
    }
}
