import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class SupermarketCheckThread {
    public final double totalInitialValue;
    public final Supermarket supermarket;
    public final ArrayList<BillsThread> bills;
    private static final ReentrantLock lock = new ReentrantLock();

    public SupermarketCheckThread(double totalInitialValue, Supermarket supermarket, ArrayList<BillsThread> bills) {
        this.totalInitialValue = totalInitialValue;
        this.supermarket = supermarket;
        this.bills = bills;
    }

    public void checkSupermarket() {
        lock.lock();
        double difference = this.totalInitialValue - bills.stream().mapToDouble(BillsThread::getProfit).sum() - this.supermarket.computeValue();
        lock.unlock();
        if (Math.abs(difference) > 0.01) {
            System.out.println("Supermarket is not ok!");
        }
        else {
            System.out.println("Supermarket is ok!");
        }
    }
}
