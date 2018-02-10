package catchytube.com;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.Key;

/**
 * Created by kpajm on 20-05-2017.
 */
public class FileUtils {

    static String stripExtension(String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }

    static String getExtension(String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return null;
        return str.substring(pos + 1);
    }

    static boolean fileExists(File file) {
        if (file.exists() && !file.isDirectory())
            return true;
        else
            return false;
    }

    static boolean isEncrypted(File file) {
        String filename = file.getName();
        String extension = getExtension(filename);
        if (extension.equals("cry"))
            return true;
        else
            return false;
    }

    static String encrypt(String file) throws Exception {
        String secret = "12345678abcdefgh";
        Key key = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(file.getBytes());
        return new String(encryptedData,"UTF-16");
    }

    static String decrypt(String file) throws Exception{
        String secret = "12345678abcdefgh";
        Key key = new SecretKeySpec(secret.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(file.getBytes());
        return new String(decryptedData,"UTF-8");
    }

}
