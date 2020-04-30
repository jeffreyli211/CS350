package hw7;

import java.math.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class SynchedUnHashing {
    public static ChunkThread[] threads;

    public static void synchedUnhash (String input_filepath, int N) throws Exception {
        //int count = 0;
        File file = new File(input_filepath);
        Scanner sc = new Scanner(file);
        List<String> lines = new ArrayList<String>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        
        int chunkSize = lines.size() / N;
        int chunkID = 0;
        int a = 0;
        int b = chunkSize-1;
        while (b <= lines.size()-1) {
            /*
             * a and b variables local to synchedUnhash(...). There are updated correctly.
             */
            //System.out.println("Local variables a and b: " + a + ", " + b);   // DELETE
            threads[chunkID] = new ChunkThread(a, b, lines, Integer.toString(chunkID));
            a = b+1;
            b += chunkSize;
            chunkID++;
        }
        if (lines.size() % N != 0) {
            /* Makes sure last chunkID is correct for the last chunk; this should print N-1.*/
            chunkID--;
            //System.out.println("last chunkID is " + chunkID);     // DELETE
            threads[chunkID] = new ChunkThread(a, lines.size()-1, lines, Integer.toString(chunkID));
        }

        for (int i = 0; i <= N-1; i++) {
            /* Print once thread #i has started. */
            //System.out.println("Thread " + i + " started.");
            threads[i].start();
        }
        sc.close();
    }

    public static void main (String[] args) throws Exception {
        String in = args[0];
        int N = Integer.parseInt(args[1]);
        threads = new ChunkThread[N];
        synchedUnhash(in, N);
    }
}

class ChunkThread extends Thread {
    private List<String> lines;
    public int a;
    public int b;
    ChunkThread (int start, int end, List<String> hashes, String name) {
        super (name);
        this.a = start;
        this.b = end;
        this.lines = hashes;
    }

    public void run() {
        //System.out.println("What run() thinks chunk " + getName() + " has: a: " + this.a + "; b: " + this.b);
        //List<String> partition = lines.subList(this.a, this.b + 1);
        try
        {
            Thread.sleep ((int) (Integer.parseInt(getName()) * 4000));
        }
        catch (InterruptedException e)
        {
        }
        for (int i = this.a; i <= this.b; i++) {
            /* Prints every line in this chunk */
            //System.out.println("i = " + i);
            //System.out.println(lines.get(i));

            /* Actual unhash function call. */
            System.out.println(UnHash.unhash(lines.get(i))); //+ " FROM CHUNK " + getName());
        }
    }
}
