package hw6;

import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
/*
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
*/

public class UnHashN {
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

    public static void unhashNFromFile(String input_filepath, int N) throws Exception{
        int count = 0;
        File file = new File(input_filepath);
        Scanner sc = new Scanner(file);
        List<String> lines = new ArrayList<String>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        
        int chunkSize = lines.size() / N;
        int a = 0;
        int b = chunkSize-1;
        while (b <= lines.size()-1) {
            for (int i = a; i <= b; i++) {
                System.out.println(unhash(lines.get(i)));
                count++;
            }
            a = b+1;
            b += chunkSize;
        }
        if (lines.size() % N != 0) {
            for (int i = a; i <= lines.size()-1; i++) {
                System.out.println(unhash(lines.get(i)));
                count++;
            }
        }
        System.out.println("Lines: " + lines.size());
        System.out.println("Calculated: " + count);
        sc.close();
    }

    public static void main (String[] args) throws Exception {
        String in = args[0];
        int N = Integer.parseInt(args[1]);
        unhashNFromFile(in, N);
    }
}