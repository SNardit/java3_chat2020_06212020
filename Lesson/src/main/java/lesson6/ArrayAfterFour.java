package lesson6;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArrayAfterFour {

    private static final Logger logger = Logger.getLogger(ArrayAfterFour.class.getName());

    public static Integer[] afterLastFour (Integer[] arr) throws RuntimeException {

        ArrayList<Integer> newArr = new ArrayList<>();
        int indexFour = -1;

        try {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 4) {
                    indexFour = i;
                    newArr.clear();
                    for (int k = i + 1; k < arr.length; k++) {
                        newArr.add(arr[k]);
                    }
                }
            }
            if (indexFour == - 1) {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "Array does not contain '4'");
        }


        return newArr.toArray(new Integer[0]);
    }


}
