import lesson6.ArrayAfterFour;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AfterLastFourTest {
    private Integer[] newArray;
    private Integer[] array;
    ArrayAfterFour arrayAfterFour;

    public AfterLastFourTest (Integer[] array, Integer[] newArray) {
        this.newArray = newArray;
        this.array = array;
    }

    @Parameterized.Parameters
    public static Collection <Object[]> data() {
            return Arrays.asList(new Object[][] {
                    {new Integer[]{1, 2, 4, 4, 2, 3, 4, 1, 7}, new Integer[]{1, 7}},
                    {new Integer[]{1, 2, 2, 3, 1, 7}, new Integer[]{}},
                    {new Integer[]{5, 4, 1, 2, 3, 5}, new Integer[]{1, 2, 3, 5}},
                    {new Integer[]{1, 4, 4, 3, 8, 7}, new Integer[]{3, 8, 7}},
                    {new Integer[]{4, 4, 4, 6}, new Integer[]{5}}

                    });
    }

    @Before
    public void init() {
        arrayAfterFour = new ArrayAfterFour();
    }

    @Test
    public void testAfterLast () {
        Assert.assertArrayEquals(newArray, ArrayAfterFour.afterLastFour(array));
    }
}
