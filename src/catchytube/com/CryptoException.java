package catchytube.com;

/**
 * Created by kpajm on 20-05-2017.
 */
public class CryptoException extends Exception {

    public CryptoException() {
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}