import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Polynomial implements Serializable {
    public final List<Integer> coefficients;

    public Polynomial(List<Integer> coefficients) {
        this.coefficients = coefficients;
    }

    public Polynomial(int size) {
        this.coefficients = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            coefficients.add(0);
        }
    }

    public void generateCoefficients() {
        Random random = new Random();
        this.coefficients.replaceAll(ignored -> random.nextInt(10));
    }

    public int getDegree() {
        return this.coefficients.size() - 1;
    }

    public List<Integer> getCoefficients() {
        return this.coefficients;
    }

    public int getSize() {
        return this.coefficients.size();
    }

    public static Polynomial add(Polynomial first, Polynomial second) {
        int minDegree = Math.min(first.getDegree(), second.getDegree());
        int maxDegree = Math.max(first.getDegree(), second.getDegree());
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(first.coefficients.get(i) + second.coefficients.get(i));
        }

        if (minDegree != maxDegree) {
            if (maxDegree == first.getDegree()) {
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
        int minDegree = Math.min(first.getDegree(), second.getDegree());
        int maxDegree = Math.max(first.getDegree(), second.getDegree());
        List<Integer> coefficients = new ArrayList<>(maxDegree + 1);

        for (int i = 0; i <= minDegree; i++) {
            coefficients.add(first.coefficients.get(i) - second.coefficients.get(i));
        }

        if (minDegree != maxDegree) {
            if (maxDegree == first.getDegree()) {
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
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = this.coefficients.size() - 1; i >= 0; i--) {
            stringBuilder.append(this.coefficients.get(i)).append("x^").append(i).append(" + ");
        }
        stringBuilder.setLength(stringBuilder.length() - 3);

        return stringBuilder.toString();
    }
}
