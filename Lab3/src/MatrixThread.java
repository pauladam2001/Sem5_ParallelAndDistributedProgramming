import java.util.ArrayList;
import java.util.List;

public abstract class MatrixThread extends Thread {
    public List<Pair<Integer, Integer>> pairs;
    public final int iStart, jStart, sizeOfTask;
    public final Matrix matrix1, matrix2, result;
    public int k;

    public MatrixThread(int iStart, int jStart, int sizeOfTask, Matrix matrix1, Matrix matrix2, Matrix result) {
        pairs = new ArrayList<>();
        this.iStart = iStart;
        this.jStart = jStart;
        this.sizeOfTask = sizeOfTask;
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
        generateElements();
    }

    public MatrixThread(int iStart, int jStart, int sizeOfTask, Matrix matrix1, Matrix matrix2, Matrix result, int k) {
        pairs = new ArrayList<>();
        this.iStart = iStart;
        this.jStart = jStart;
        this.sizeOfTask = sizeOfTask;
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
        this.k = k;
        generateElements();
    }

    public abstract void generateElements();

    @Override
    public void run() {
        for (Pair<Integer, Integer> p : pairs) {
            try {
                result.setElement(p.first, p.second, ThreadManager.computeElement(matrix1, matrix2, p.first, p.second));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
