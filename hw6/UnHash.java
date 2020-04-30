package hw6;

import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class UnHash {
    public static int unchecked_i = 0;
    public static List<String> found = new ArrayList<String>();
    public static void addHash(int i, String hash) {
        found.add(i, hash);
    }
    public static boolean exists(String hash) {
        return found.contains(hash);
    }
    public static int indexOf(String hash) {
        return found.indexOf(hash);
    }

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
    
    public static int unhash(String to_unhash) {
        boolean exists = exists(to_unhash);
        if (exists) {
            return indexOf(to_unhash);
        }
        else {
            while (true) {
                String potential = find_hash(unchecked_i);
                addHash(unchecked_i, potential);
                if (potential.equals(to_unhash)) {
                    break;
                }
                unchecked_i++;
            }
            return unchecked_i;
        }
    }

    public static void unhashFromFile(String input_filepath) throws Exception{
        File file = new File(input_filepath);
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            System.out.println(unhash(sc.nextLine()));
        }
        sc.close();
    }

    public static void main (String[] args) throws Exception {
        unhashFromFile(args[0]);
    }
}