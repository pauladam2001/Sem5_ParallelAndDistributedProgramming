import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Polynomial {
    public final List<Integer> coefficients;
    public final int degree;

    public Polynomial(int degree) {
        this.degree = degree;
        coefficients = new ArrayList<>(degree + 1);     // polynomial of degree 4 = 2x^4 + 2x^3 + 2x^2 + 2x + 2 (=> 5 coefficients on positions 0, 1, 2, 3, 4)

        Random random = new Random();
        for (int i = 0; i < degree; i++) {
            coefficients.add(random.nextInt(10));
        }
        coefficients.add(random.nextInt(10) + 1);
    }

    public Polynomial(List<Integer> coefficients) {
        this.coefficients = coefficients;
        degree = coefficients.size() - 1;
    }

    public static Polynomial add(Polynomial first, Polynomial second) {
        int minDegree = Math.min(first.degree, second.degree);
        int maxDegree = Math.max(first.degree, second.degree);
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(first.coefficients.get(i) + second.coefficients.get(i));
        }

        if (minDegree != maxDegree) {
            if (maxDegree == first.degree) {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(first.coefficients.get(i));
                }
            } else {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(second.coefficients.get(i));
                }
            }
        }

        return new Polynomial(coefficients);
    }

    public static Polynomial subtract(Polynomial first, Polynomial second) {
        int minDegree = Math.min(first.degree, second.degree);
        int maxDegree = Math.max(first.degree, second.degree);
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(first.coefficients.get(i) - second.coefficients.get(i));
        }

        if (minDegree != maxDegree) {
            if (maxDegree == first.degree) {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(first.coefficients.get(i));
                }
            } else {
                for (int i = minDegree + 1; i <= maxDegree; i++) {
                    coefficients.add(second.coefficients.get(i));
                }
            }
        }

        int i = coefficients.size() - 1;
        while (coefficients.get(i) == 0 && i > 0) {
            coefficients.remove(i);
            i--;
        }

        return new Polynomial(coefficients);
    }

    public static Polynomial addZeroes(Polynomial polynomial, int offset) {
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i < offset; i++) {
            coefficients.add(0);
        }
        coefficients.addAll(polynomial.coefficients);

        return new Polynomial(coefficients);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(coefficients.get(degree)).append("x^").append(degree);
        for (var i = degree - 1; i > 0; --i) {
            if (coefficients.get(i) != 0)
                builder.append(" + ").append(coefficients.get(i)).append("x^").append(i);
        }
        if (coefficients.get(0) != 0)
            builder.append(" + ").append(coefficients.get(0));
        return builder.toString();
    }
}
