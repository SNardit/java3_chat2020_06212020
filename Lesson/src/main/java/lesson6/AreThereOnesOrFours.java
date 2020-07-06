package lesson6;

import java.util.*;

public class AreThereOnesOrFours {
    public static boolean areThereOnesOrFours(Integer[] arr) {

        Set<Integer> set = new HashSet<>(Arrays.asList(arr));
        return set.size() == 2 && set.contains(1) && set.contains(4);
    }


}
