package hw7;

import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

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
            String str_input = Integer.toString(to_hash);
            
            byte[] mdbytes = md.digest(str_input.getBytes());
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