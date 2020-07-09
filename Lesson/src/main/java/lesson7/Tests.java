package lesson7;

public class Tests {

    @AfterSuite
    public static void test1() {
        System.out.println("test1: " + "AfterSuite");
    }

    @Test(priority = 1)
    public static void test2() {
        System.out.println("test2: " + "priority = 1");
    }

    @BeforeSuite
    public static void test3() {
        System.out.println("test3: " + "BeforeSuite");
    }

    @Test
    public static void test4() {
        System.out.println("test4: " + "priority = default(5)");
    }

    @Test(priority = 8)
    public static void test5() {
        System.out.println("test5: " + "priority = 8");
    }

    @Test(priority = 4)
    public static void test6() {
        System.out.println("test6: " + "priority = 4");
    }

    /*@BeforeSuite
    public static void test7() {
        System.out.println("AfterSuite");
    }*/
}
