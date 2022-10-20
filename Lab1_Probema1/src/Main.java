import model.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    private static Supermarket loadSupermarket() throws FileNotFoundException {
        Supermarket supermarket = new Supermarket();
        File file = new File("products.txt");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            supermarket.addProduct(new Product(scanner.next(), scanner.nextDouble()), scanner.nextInt() * 10000);
        }
        return supermarket;
    }

    private static Supermarket generateSupermarketSubset(Supermarket supermarket) {
        Random random = new Random();
        int productCount = random.nextInt(99) + 1;
        ArrayList<Product> productsAsArray = new ArrayList<>(supermarket.getProducts());
        Supermarket supermarketSubset = new Supermarket();
        for (int i = 0; i < productCount; i++) {
            boolean foundNewProduct = false;
            do {
                Product selectedProduct = productsAsArray.get(random.nextInt(productsAsArray.size() - 1));
                if (! supermarketSubset.containsProduct(selectedProduct)) {
                    foundNewProduct = true;
                    int quantity = random.nextInt(99) + 1;
                    supermarketSubset.addProduct(selectedProduct, quantity);
                }
            }
            while (!foundNewProduct);
        }
        return supermarketSubset;
    }

    public static void main(String[] args ) throws FileNotFoundException, InterruptedException {
        int THREAD_COUNT = 1000;
        Supermarket supermarket = loadSupermarket();
        double totalInitialValue = supermarket.computeValue();
        ArrayList<BillsThread> bills = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            bills.add(new BillsThread(supermarket, generateSupermarketSubset(supermarket)));
        }
        SupermarketCheckThread supermarketChecker = new SupermarketCheckThread(totalInitialValue, supermarket, bills);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                supermarketChecker.checkSupermarket();
            }
        }, 0, 1);
        ArrayList<Thread> threads = new ArrayList<>();
        bills.forEach(sale -> threads.add(new Thread(sale)));
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
        timer.cancel();
        Thread.sleep(1000);     // wait for all threads to finish
        System.out.println("All bills have finished. Result of the final check: ");
        supermarketChecker.checkSupermarket();
    }
}
