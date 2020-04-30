package hw8;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.HashMap;

class FindingPairs {
    public static int N = 8;
    public static DecThread[] threads;
    public static HashMap<String,String> f2HashMap = new HashMap<>();
    public static List<Integer> f1Checked;

    public static void findPairs (String f1_path, String f2_path) throws Exception {
        File f1File = new File(f1_path);
        Scanner f1_sc = new Scanner(f1File);
        List<String> f1_lines = new ArrayList<String>();
        while (f1_sc.hasNextLine()) {
            f1_lines.add(f1_sc.nextLine());
        }
        f1Checked = new ArrayList<Integer>(Collections.nCopies(f1_lines.size(), 0));

        File f2File = new File(f2_path);
        Scanner f2_sc = new Scanner(f2File);
        while (f2_sc.hasNextLine()) {
            String ln = f2_sc.nextLine();
            f2HashMap.put(ln, ln);
        }

        int size = f1_lines.size() / N;
        int ID = 0;
        int a = 0;
        int b = size-1;
        while(b <= f1_lines.size()-1) {
            threads[ID] = new DecThread(a, b, f1_lines, Integer.toString(ID));
            threads[ID].start();
            a = b+1;
            b += size;
            ID++;
        }
        if (f1_lines.size() % N != 0) {
            ID--;
            threads[ID] = new DecThread(a, f1_lines.size()-1, f1_lines, Integer.toString(ID));
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
        private List<String> lines;
        private int a;
        private int b;
        DecThread (int start, int end, List<String> f1, String name) {
            super (name);
            this.lines = f1;
            this.a = start;
            this.b = end;
        }

        public void run() {
            for (int i = a; i <= b; i++) {
                if (FindingPairs.f1Checked.get(i) == 0) {
                    String line_i = lines.get(i);
                    int num1 = UnHash.unhash(line_i);
                    for (int j = 0; j <= lines.size()-1; j++) {
                        if (FindingPairs.f1Checked.get(i) == 0) {
                            if (j != i) {
                                String line_j = lines.get(j);
                                int num2 = UnHash.unhash(line_j);
                                String encoded = Hash.find_F2(num1, num2);

                                if (FindingPairs.f2HashMap.containsKey(encoded)) {
                                    FindingPairs.f1Checked.set(i, 1);
                                    FindingPairs.f1Checked.set(j, 1);
                                    System.out.println(num1 + "," + num2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }