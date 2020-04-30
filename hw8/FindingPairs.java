package hw8;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashSet;

class FindingPairs {
    public static int N = 4;
    public static DecThread[] threads = new DecThread[N];
    public static HashSet<String> f2HashSet = new HashSet<>();
    public static int[] f1Checked;

    public static void findPairs (String f1_path, String f2_path) throws Exception {
        File f1File = new File(f1_path);
        Scanner f1_sc = new Scanner(f1File);
        List<Integer> f1_decoded = new ArrayList<Integer>();      // Unhashed F1 lines.
        while (f1_sc.hasNextLine()) {
            f1_decoded.add(UnHash.unhash(f1_sc.nextLine()));
        }
        f1Checked = new int[f1_decoded.size()];                   // List that checks when the integer at line i has been correctly paired.

        File f2File = new File(f2_path);
        Scanner f2_sc = new Scanner(f2File);
        while (f2_sc.hasNextLine()) {
            String ln = f2_sc.nextLine();
            f2HashSet.add(ln);
        }

        int size = f1_decoded.size() / N;
        int ID = 0;
        int a = 0;
        int b = size-1;
        while(b <= f1_decoded.size()-1) {
            List<Integer> chunk = f1_decoded.subList(a, b+1);
            threads[ID] = new DecThread(f1_decoded, chunk, f2HashSet, Integer.toString(ID));
            //System.out.println("Checkpoint 1");
            threads[ID].start();
            a = b+1;
            b += size;
            ID++;
        }
        if (f1_decoded.size() % N != 0) {
            ID--;
            List<Integer> lastChunk = f1_decoded.subList(a, f1_decoded.size());
            threads[ID] = new DecThread(f1_decoded, lastChunk, f2HashSet, Integer.toString(ID));
            threads[ID].start();
        }

        f1_sc.close();
        f2_sc.close();
    }
    public static void main (String[] args) throws Exception {
        String f1 = args[0];
        String f2 = args[1];
        findPairs(f1,f2);
    }
}

class DecThread extends Thread {
        private List<Integer> lines;
        private List<Integer> chunk;
        private HashSet<String> f2;
        DecThread (List<Integer> whole, List<Integer> piece, HashSet<String> f2, String name) {
            super (name);
            this.lines = whole;
            this.chunk = piece;
            this.f2 = f2;
        }

        /*
        public List<Integer> getChunk() {
            return chunk;
        }
        */

        public void run() {
            for (int i = 0; i <= this.chunk.size()-1; i++) {
                int num1 = chunk.get(i);
                int index_f1 = lines.indexOf(num1);
                if (FindingPairs.f1Checked[index_f1] == 1) {        // This number has already be paired.
                    continue;
                }
                else {
                    for (int j = 0; j <= this.lines.size()-1; j++) {
                        if (FindingPairs.f1Checked[j] == 1 || index_f1 == j) {      // This jth number was already paired OR it is the same number as i, ignore.
                            continue;
                        }
                        else {
                            int num2 = lines.get(j);
                            String concat = Hash.find_F2(num1, num2);
                            if (f2.contains(concat)) {
                                FindingPairs.f1Checked[index_f1] = 1;
                                FindingPairs.f1Checked[j] = 1;
                                System.out.println(num1 + "," + num2);
                                break;      // Found the pair, no need to continue inner for loop.
                            }
                        }
                    }
                }
            }
        }
    }
