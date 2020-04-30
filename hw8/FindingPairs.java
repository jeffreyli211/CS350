package hw8;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

class FindingPairs {
    public static int N = 8;
    public static DecThread[] threads = new DecThread[N];
    public static HashMap<String,String> f2HashMap = new HashMap<>();
    public static int[] f1Checked;

    public static void findPairs (String f1_path, String f2_path) throws Exception {
        File f1File = new File(f1_path);
        Scanner f1_sc = new Scanner(f1File);
        List<String> f1_lines = new ArrayList<String>();
        while (f1_sc.hasNextLine()) {
            f1_lines.add(f1_sc.nextLine());
        }
        f1Checked = new int[f1_lines.size()];

        File f2File = new File(f2_path);
        Scanner f2_sc = new Scanner(f2File);
        while (f2_sc.hasNextLine()) {
            String ln = f2_sc.nextLine();
            f2HashMap.put(ln, ln);
            System.out.println(f2HashMap.size());
        }

        int size = f1_lines.size() / N;
        int ID = 0;
        int a = 0;
        int b = size-1;
        while(b <= f1_lines.size()-1) {
            threads[ID] = new DecThread(a, b, f1_lines, Integer.toString(ID));
            System.out.println("Checkpoint 1");
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
        public int a;
        public int b;
        DecThread (int start, int end, List<String> f1, String name) {
            super (name);
            this.lines = f1;
            this.a = start;
            this.b = end;
        }

        public void run() {
            for (int i = this.a; i <= this.b; i++) {
                if (FindingPairs.f1Checked[i] == 0) {
                    String line_i = lines.get(i);
                    int num1 = UnHash.unhash(line_i);

                    boolean found = false;
                    int j = 0;
                    while (!found) {
                        System.out.println("Checking line " + i + " and " + j);
                        if (FindingPairs.f1Checked[j] == 0 && j!= i) {
                            String line_j = lines.get(j);
                            int num2 = UnHash.unhash(line_j);
                            String encoded = Hash.find_F2(num1, num2);
                            System.out.println("Checkpoint 2");

                            if (FindingPairs.f2HashMap.containsValue(encoded) == true) {
                                System.out.println("Checkpoint 3: Pair found");
                                FindingPairs.f1Checked[i] = 1;
                                FindingPairs.f1Checked[j] = 1;
                                System.out.println(num1 + "," + num2);
                                found = true;
                            }
                        }
                        j++;
                    }
                } continue;
            }
        }
    }