public class Task implements Runnable {
    private final int start;
    private final int end;
    private final Polynomial first;
    private final Polynomial second;
    private final Polynomial result;

    public Task(int start, int end, Polynomial first, Polynomial second, Polynomial result) {
        this.start = start;
        this.end = end;
        this.first = first;
        this.second = second;
        this.result = result;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            if (i > result.coefficients.size()) {
                return;
            }
            for (int j = 0; j <= i; j++) {
                if (j < first.coefficients.size() && (i - j) < second.coefficients.size()) {
                    int value = first.coefficients.get(j) * second.coefficients.get(i - j);
                    result.coefficients.set(i, result.coefficients.get(i) + value);
                }
            }
        }
    }
}
