import model.Product;

import java.util.concurrent.locks.ReentrantLock;

public class BillsThread implements Runnable {
    private Supermarket supermarket;
    private final Supermarket supermarketSubset;
    private double profit;
    private final ReentrantLock lock;

    public BillsThread(Supermarket supermarket, Supermarket supermarketSubset) {
        this.supermarket = supermarket;
        this.supermarketSubset = supermarketSubset;
        this.profit = 0.0;
        this.lock = new ReentrantLock();
    }

    private void sellProduct(Product productForSale) {
        this.lock.lock();
        int quantity = this.supermarketSubset.getQuantityOfProduct(productForSale);
        this.supermarket.removeProduct(productForSale, quantity);
        this.profit += productForSale.getPrice() * quantity;
        this.lock.unlock();
    }

    public double getProfit() {
        return this.profit;
    }

    @Override
    public void run() {
        this.supermarketSubset.getProducts().forEach(this::sellProduct);
    }
}
