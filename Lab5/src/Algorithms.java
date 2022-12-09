import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Algorithms {
    public static Polynomial ClassicSequential(Polynomial first, Polynomial second) {
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i <= first.degree + second.degree; i++) {
            coefficients.add(0);
        }

        for (int i = 0; i < first.coefficients.size(); i++) {
            for (int j = 0; j < second.coefficients.size(); j++) {
                int index = i + j;
                int value = first.coefficients.get(i) * second.coefficients.get(j);
                coefficients.set(index, coefficients.get(index) + value);
            }
        }

        return new Polynomial(coefficients);
    }

    public static Polynomial ClassicParallelized(Polynomial first, Polynomial second) throws InterruptedException {
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i <= first.degree + second.degree; i++) {       // first.degree + second.degree + 1 = size of result coefficient list
            coefficients.add(0);
        }

        Polynomial resultPolynomial = new Polynomial(coefficients);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);

        int step = (first.degree + second.degree + 1) / 4;                      // distribute nr of computations between threads
        for (int i = 0; i <= first.degree + second.degree; i = i + step) {
            Task task = new Task(i, i + step, first, second, resultPolynomial);
            threadPoolExecutor.execute(task);
        }

        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        return resultPolynomial;
    }

    public static Polynomial KaratsubaSequential(Polynomial first, Polynomial second) {
        if (first.degree < 2 || second.degree < 2) {
            return ClassicSequential(first, second);
        }

        // the point of the Karatsuba algorithm is to break large numbers down into smaller numbers so that any multiplications that occur happen on smaller numbers

        // calculate the middle of the polynomials
        int length = Math.max(first.degree, second.degree) / 2;

        // split the polynomials in the middle
        Polynomial lowFirst = new Polynomial(first.coefficients.subList(0, length));
        Polynomial highFirst = new Polynomial(first.coefficients.subList(length, first.coefficients.size()));
        Polynomial lowSecond = new Polynomial(second.coefficients.subList(0, length));
        Polynomial highSecond = new Polynomial(second.coefficients.subList(length, first.coefficients.size()));

        // 3 recursive calls made to number approximately half the size
        Polynomial z1 = KaratsubaSequential(lowFirst, lowSecond);
        Polynomial z2 = KaratsubaSequential(Polynomial.add(lowFirst, highFirst), Polynomial.add(lowSecond, highSecond));    // https://en.wikipedia.org/wiki/Karatsuba_algorithm
        Polynomial z3 = KaratsubaSequential(highFirst, highSecond);

        // return (z3 × 10 ^ (length × 2)) + ((z2 - z3 - z1) × 10 ^ length) + z1

        Polynomial r1 = Polynomial.addZeroes(z3, 2 * length);
        Polynomial r2 = Polynomial.addZeroes(Polynomial.subtract(Polynomial.subtract(z2, z3), z1), length);

        return Polynomial.add(Polynomial.add(r1, r2), z1);
    }

    public static Polynomial KaratsubaParallelized(Polynomial first, Polynomial second, int currentDepth) throws ExecutionException, InterruptedException {
        if (currentDepth > 4) {
            return KaratsubaSequential(first, second);
        }
        if (first.degree < 2 || second.degree < 2) {
            return KaratsubaSequential(first, second);
        }

        // the point of the Karatsuba algorithm is to break large numbers down into smaller numbers so that any multiplications that occur happen on smaller numbers

        // calculate the middle of the polynomials
        int length = Math.max(first.degree, second.degree) / 2;

        // split the polynomials in the middle
        Polynomial lowFirst = new Polynomial(first.coefficients.subList(0, length));
        Polynomial highFirst = new Polynomial(first.coefficients.subList(length, first.coefficients.size()));
        Polynomial lowSecond = new Polynomial(second.coefficients.subList(0, length));
        Polynomial highSecond = new Polynomial(second.coefficients.subList(length, first.coefficients.size()));

        // a Future represents the result of an asynchronous computation
        // submits a value-returning task for execution and returns a Future representing the pending results of the task. The Future's get method will return the task's result upon successful completion.

        // 3 recursive calls made to number approximately half the size
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Future<Polynomial> f1 = executor.submit(() -> KaratsubaParallelized(lowFirst, lowSecond, currentDepth + 1));
        Future<Polynomial> f2 = executor.submit(() -> KaratsubaParallelized(Polynomial.add(lowFirst, highFirst), Polynomial
                .add(lowSecond, highSecond), currentDepth + 1));
        Future<Polynomial> f3 = executor.submit(() -> KaratsubaParallelized(highFirst, highSecond, currentDepth + 1));

        executor.shutdown();

        Polynomial z1 = f1.get();       // Future.get() - waits if necessary for the computation to complete, and then retrieves its result
        Polynomial z2 = f2.get();       // https://en.wikipedia.org/wiki/Karatsuba_algorithm
        Polynomial z3 = f3.get();

        executor.awaitTermination(5000, TimeUnit.MILLISECONDS);

        // return (z3 × 10 ^ (length × 2)) + ((z2 - z3 - z1) × 10 ^ length) + z1

        Polynomial r1 = Polynomial.addZeroes(z3, 2 * length);
        Polynomial r2 = Polynomial.addZeroes(Polynomial.subtract(Polynomial.subtract(z2, z3), z1), length);

        return Polynomial.add(Polynomial.add(r1, r2), z1);
    }
}
