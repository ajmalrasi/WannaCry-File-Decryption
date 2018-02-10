package catchytube.com;

/**
 * Created by kpajm on 20-05-2017.
 */

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class AsymmetricCryptoUtils {
    private Cipher cipher;
    private GenerateKey gk;

    public AsymmetricCryptoUtils() throws Exception {
        this.cipher = Cipher.getInstance("RSA");
        gk = new GenerateKey(2048);
        gk.createKeys();
    }

    private PrivateKey getPrivate() throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File("KeyPair/cPrivateDecrypted").toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublicKey() throws Exception {
        String filename= "KeyPair/cPublic";
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }


    //PrivateKey cPrivate
    public void decryptAesKey(PrivateKey cPrivateKey)
            throws IOException, GeneralSecurityException {
        File aesKeyEncrytped = new File("KeyPair/AesKeyEncrypted");
        this.cipher.init(Cipher.DECRYPT_MODE, cPrivateKey);
        File AesKey = new File("KeyPair/AesKey");
        writeToFile(AesKey, this.cipher.doFinal(getFileInBytes(aesKeyEncrytped)));
    }

    //AES key in bytes
    public void encryptAesKey(byte[] input) throws Exception {
        this.cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        File aesKeyEncrytped = new File("KeyPair/AesKeyEncrypted");
        writeToFile(aesKeyEncrytped, this.cipher.doFinal(input));
    }

    private void writeToFile(File output, byte[] toWrite)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }

    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }


    private class GenerateKey{
        private KeyPairGenerator keyGen;
        private KeyPair pair;
        private Cipher cipher;

        GenerateKey(int keyLength) throws Exception {
            this.cipher = Cipher.getInstance("RSA");
            this.keyGen = KeyPairGenerator.getInstance("RSA");
            this.keyGen.initialize(keyLength);
        }

        public void createKeys() throws Exception {
            this.pair = this.keyGen.generateKeyPair();
            this.savePrivate(pair.getPrivate().getEncoded());
            this.savePublic(pair.getPublic().getEncoded());
        }

        private boolean savePrivate(byte[] input) throws Exception {
            File cPrivateFile = new File("KeyPair/cPrivateEncrypted");
            if (!cPrivateFile.exists()&&!cPrivateFile.isDirectory()){
                this.cipher.init(Cipher.ENCRYPT_MODE, getKey());
                writeToFile(cPrivateFile, this.cipher.doFinal(input));
                System.out.println("cPrivateEncrypted created.");
                return true;
            }
            System.out.println("cPrivateEncrypted already exists.");
            return false;
        }

        private boolean savePublic(byte[] input) throws Exception {
            File cPublicFile = new File("KeyPair/cPublic");
            if (!cPublicFile.exists()&&!cPublicFile.isDirectory()){
                writeToFile(cPublicFile, input);
                System.out.println("cPublic created.");
                return true;
            }
            System.out.println("cPublic already exists.");
            return false;
        }


        private PublicKey getKey() throws Exception {
            String filename= "KeyPair/sPublic";
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }

        private void writeToFile(File f, byte[] key) throws IOException {
            f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(key);
            fos.flush();
            fos.close();
        }

    }

}