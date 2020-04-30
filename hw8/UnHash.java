package hw8;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class UnHash {
    private static int unchecked_i = 0;
    private static HashMap<String,Integer> found = new HashMap<>();     // Changed to HashMap to speedup time complexity.
    public static boolean exists(String hash) {
        return found.containsKey(hash);         // Now takes O(1)
    }
    public static int indexOf(String hash) {
        return found.get(hash);                 // Now takes O(1)
    }

    public static int unhash(String to_unhash) {
        boolean exists = exists(to_unhash);
        if (exists) {
            return indexOf(to_unhash);
        }
        else {
            while (true) {
                String potential = Hash.find_hash(unchecked_i);
                found.put(potential, unchecked_i);
                if (potential.equals(to_unhash)) {
                    unchecked_i++;
                    return unchecked_i-1;
                }
                unchecked_i++;
            }
        }
    }
}

class UnHashThread extends Thread {
    
}