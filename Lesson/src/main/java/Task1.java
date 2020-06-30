public class Task1 {

    static Object lock = new Object();
    static char currentChar = 'A';
    static int quantity = 5;

    public static void main(String[] args) {

        new Thread(() -> {
            try {
                for (int i = 0; i < quantity; i++) {

                    synchronized (lock) {
                        while (currentChar != 'A') {
                            lock.wait();
                        }
                        System.out.print(currentChar);
                        currentChar = 'B';
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }).start();

        new Thread(() -> {
            try {
                for (int i = 0; i < quantity; i++) {

                    synchronized (lock) {
                        while (currentChar != 'B') {
                            lock.wait();
                        }
                        System.out.print(currentChar);
                        currentChar = 'C';
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i = 0; i < quantity; i++) {

                    synchronized (lock) {
                        while (currentChar != 'C') {
                            lock.wait();
                        }
                        System.out.print(currentChar);
                        currentChar = 'A';
                        lock.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
