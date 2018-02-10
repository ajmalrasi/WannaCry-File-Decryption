package catchytube.com;

/**
 * Created by kpajm on 20-05-2017.
 */

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricCryptoUtils {


    private static SecretKey secretKey = null;
    private static String algorithm = "AES";
    private String encryptedFileName = "Enc_File2.txt";

    private static SecretKey generateKey(char[] passphraseOrPin, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations,
                outputKeyLength);
        secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }

    private static SecretKey generateSalt() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        return keyGenerator.generateKey();
    }

    private static byte[] encodeFile(SecretKey secretKey, byte[] fileData)
            throws Exception {
        byte[] data = secretKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(fileData);
    }

    private static byte[] decodeFile(SecretKey secretKey, byte[] fileData)
            throws Exception {
        byte[] data = secretKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(fileData);
    }

    public void encodeFile(File file) {
        try {
            if (FileUtils.fileExists(file) && !FileUtils.isEncrypted(file)) {
                String filename = FileUtils.stripExtension(file.getName());
                //fix this, only works for mp4
                File encodedFile = new File(file.getParent() + File.separator +
                        filename + ".cry");
                File keyFile = new File(file.getParent() + File.separator +
                        FileUtils.encrypt(filename) + ".key");
                char[] p = {'r', 'a', 's', 'i'};
                SecretKey secretKey = generateKey(p, generateSalt().toString().getBytes());
                writeFile(secretKey.getEncoded(), keyFile);

                byte[] filesBytes = encodeFile(secretKey, readFile(file));
                writeFile(filesBytes, encodedFile);
                if (file.delete())
                    System.out.println("File encrypted.");
                else
                    System.out.println("Error.");
            } else {
                System.out.println("File error.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decodeFile(File file) {
        try {
            if (FileUtils.fileExists(file) && FileUtils.isEncrypted(file)) {
                String fileName = FileUtils.stripExtension(file.getName());
                File keyFile =new File(file.getParent()+File.separator+
                        FileUtils.encrypt(fileName)+".key");
                byte[] fileBytes = decodeFile(readKey(keyFile), readFile(file));
                //fix this, only works for mp4
                File decodedFile = new File(file.getParent() +
                        File.separator + fileName + ".mp4");
                writeFile(fileBytes, decodedFile);
                if (file.delete()&&keyFile.delete()){
                    System.out.println("File decrypted.");
                }else
                    System.out.println("Error");
            } else
                System.out.println("File error.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SecretKey readKey(File file) throws Exception {
        byte[] key = readFile(file);
        return new SecretKeySpec(key,"AES");
    }

    private void writeFile(byte[] content, File file) {
        try {
            BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(file));
            buf.write(content);
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readFile(File file) {
        byte[] contents = null;
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

}
