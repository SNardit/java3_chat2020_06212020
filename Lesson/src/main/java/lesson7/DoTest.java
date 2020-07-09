package lesson7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;

public class DoTest {

    public static void main(String[] args) {
        start(Tests.class);
    }

    public static void start (Class c) {
        Method[] methods = c.getDeclaredMethods();
        List<PriorityMethods> priorityMethodsList = new ArrayList<>();
        Comparator<PriorityMethods> comparator = comparing(obj -> obj.priority);

        Method beforeSuite = null;
        Method afterSuite = null;

        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class)) {
                if (beforeSuite == null) {
                    beforeSuite = method;
                } else throw new RuntimeException("BeforeSuite more than one");
            }
            if (method.isAnnotationPresent(AfterSuite.class)) {
                if (afterSuite == null) {
                    afterSuite = method;
                } else throw new RuntimeException("AfterSuite more than one");
            }
        }

        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                priorityMethodsList.add(new PriorityMethods(method, method.getAnnotation(Test.class).priority()));
            }
        }

        try {
            if (beforeSuite != null)  {
                beforeSuite.invoke(null);
            }

            priorityMethodsList.sort(comparator);
            for (PriorityMethods priorityMethods : priorityMethodsList) {
                if (priorityMethods.method.isAnnotationPresent(Test.class)) {
                    priorityMethods.method.invoke(null);
                }
            }

            if (afterSuite != null) {
                afterSuite.invoke(null);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private static class PriorityMethods {
        public Method method;
        public int priority;

        public PriorityMethods(Method method, int priority) {
            this.method = method;
            this.priority = priority;
        }

    }
}
