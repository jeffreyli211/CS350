package hw8;

import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String pad_hash(String short_hash) {
        while (short_hash.length() < 32) {
            short_hash = "0" + short_hash;
        }
        return short_hash;
    }

    public static String find_hash(int to_hash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String str_in = Integer.toString(to_hash);

            byte[] mdbytes = md.digest(str_in.getBytes());
            BigInteger num = new BigInteger(1, mdbytes);
            
            String hash_it = num.toString(16);
            
            if (hash_it.length() < 32) {
                hash_it = pad_hash(hash_it);
            }
            return hash_it;
            
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String find_F2(int num1, int num2) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String str1 = Integer.toString(num1);
            String str2 = Integer.toString(num2);
            String full = str1 + "," + str2;
            
            byte[] mdbytes = md.digest(full.getBytes());
            BigInteger num = new BigInteger(1, mdbytes);
            
            String hash_it = num.toString(16);
            
            if (hash_it.length() < 32) {
                hash_it = pad_hash(hash_it);
            }
            return hash_it;
            
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}