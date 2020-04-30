package hw8;

import java.util.List;
import java.util.ArrayList;

public class UnHash {
    public static int unchecked_i = 0;
    public static List<String> found = new ArrayList<String>();
    public static boolean exists(String hash) {
        return found.contains(hash);
    }
    public static int indexOf(String hash) {
        return found.indexOf(hash);
    }

    public static int unhash(String to_unhash) {
        boolean exists = exists(to_unhash);
        if (exists) {
            return indexOf(to_unhash);
        }
        else {
            while (true) {
                String potential = Hash.find_hash(unchecked_i);
                found.add(potential);
                if (potential.equals(to_unhash)) {
                    unchecked_i++;
                    return unchecked_i-1;
                }
                unchecked_i++;
            }
        }
    }
}