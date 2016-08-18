package Code;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by Donovan on 16-02-2016.
 */
public class TextEncryption { // AESEncryption
    public static byte[] encrypt(byte[] data,String pass) throws Exception {
        Key key = new SecretKeySpec(pass.getBytes(),"AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE,key);
        return c.doFinal(data);
    }

    public static byte[] decrypt(byte[] data,String pass) throws Exception {
        Key key = new SecretKeySpec(pass.getBytes(),"AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE,key);
        return c.doFinal(data);
    }
}