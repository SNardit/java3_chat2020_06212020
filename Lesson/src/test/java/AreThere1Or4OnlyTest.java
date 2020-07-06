import lesson6.AreThereOnesOrFours;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AreThere1Or4OnlyTest {
        private Integer[] array;
        private boolean b;

        AreThereOnesOrFours areThereOnesOrFours;

        public AreThere1Or4OnlyTest (Integer[] array, boolean b) {
            this.array = array;
            this.b = b;
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {
                    {new Integer[]{1, 1, 1, 4, 4, 1, 4, 4}, true},
                    {new Integer[]{1, 1, 1, 1, 1, 1}, false},
                    {new Integer[]{4, 4, 4, 4}, false},
                    {new Integer[]{1, 4, 4, 1, 1, 4, 3}, false}

            });
        }

        @Before
        public void init() {
            areThereOnesOrFours = new AreThereOnesOrFours();
        }

        @Test
        public void testAfterLast () {
            Assert.assertEquals(b, AreThereOnesOrFours.areThereOnesOrFours(array));
        }
    }

