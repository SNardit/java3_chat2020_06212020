package lesson5;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Main {
        public static final int CARS_COUNT = 4;

        public static CountDownLatch cdlStart = new CountDownLatch(4);
        public static CountDownLatch cdlFinish = new CountDownLatch(4);

        public static CyclicBarrier cb = new CyclicBarrier(4);


    public static void main(String[] args) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
            Race race = new Race(new Road(60), new Tunnel(), new Road(40));
            Car[] cars = new Car[CARS_COUNT];
            for (int i = 0; i < cars.length; i++) {
                cars[i] = new Car(race, 20 + (int) (Math.random() * 10));

            }
            for (int i = 0; i < cars.length; i++) {
                new Thread(cars[i]).start();

            }

            try {
                cdlStart.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        try {
            cdlFinish.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        }
    }


