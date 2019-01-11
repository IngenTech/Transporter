package weatherrisk.com.wrms.transporter.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 05-04-2017.
 */
public class PasswordDecoder  {

    public static String md5(String input) {

        String md5 = null;

        if(null == input) return null;

        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(), 0, input.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }
}
