import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition sendProduct = lock.newCondition();                                                                                                   // Because access to this shared state information occurs in different threads, it must be protected, so a lock of some form is associated with the condition
    private static final Condition receiveProduct = lock.newCondition();
    private static final ArrayList<Integer> array1 = new ArrayList<>(List.of(2, 4, 6, 8, 10));
    private static final ArrayList<Integer> array2 = new ArrayList<>(List.of(1, 3, 5, 7, 9));
    private static int productResult = 0;
    private static boolean productIsCalculated = false;

    public static void main(String[] args) {
        Thread producer = new Thread(() -> {
            for (int i = 0; i < array1.size(); i++) {
                lock.lock();
                try {
                    while (productIsCalculated) {
                        sendProduct.await();
                    }
                    productResult = array1.get(i) * array2.get(i);
                    System.out.println("Sending the product " + productResult + " to the consumer.");
                    productIsCalculated = true;
                    receiveProduct.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        });

        Thread consumer = new Thread(() -> {
            int sum = 0;
            for (int i = 0; i < array1.size(); i++) {
                lock.lock();
                try {
                    while (!productIsCalculated) {
                        receiveProduct.await();
                    }
                    sum += productResult;
                    System.out.println("The sum is: " + sum + ". The producer can send another product.");
                    productIsCalculated = false;
                    sendProduct.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }

            System.out.println("Sum: " + sum + ".");
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
