import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition sendProduct = lock.newCondition();
    private static Condition receiveProduct = lock.newCondition();
    private static ArrayList<Integer> array1 = new ArrayList<>(List.of(2, 4, 6, 8, 10));        // array1 and array 2 have the same size
    private static ArrayList<Integer> array2 = new ArrayList<>(List.of(1, 3, 5, 7, 9));
    private static int productResult = -1;      // the producer will start first

    public static void main(String[] args) {
        Thread producer = new Thread(() -> {
            for (int i = 0; i < array1.size(); i++) {
                lock.lock();
                try {
                    while (productResult != -1) {   // wait until the previous product is processed on the consumer side
                        sendProduct.await();        // the thread waits until it is signaled
                    }
                    productResult = array1.get(i) * array2.get(i);
                    System.out.println("Sending the product " + productResult + " to the consumer.");
                    receiveProduct.signal();        // tell the consumer to receive a new product
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
                    while (productResult == -1) {   // wait until the product is computed on the producer side
                        receiveProduct.await();     // the thread waits until it is signaled
                    }
                    sum += productResult;
                    productResult = -1;
                    System.out.println("The sum is: " + sum + ". The producer can send another product.");
                    sendProduct.signal();           // tell the producer to send a new product
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }

            System.out.println("Sum: " + sum + ".");
        });

        producer.start();      // the producer will start first
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
