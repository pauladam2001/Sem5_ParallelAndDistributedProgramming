public class KthThread extends MatrixThread {
    public KthThread(int iStart, int jStart, int sizeOfTask, Matrix matrix1, Matrix matrix2, Matrix result, int k) {
        super(iStart, jStart, sizeOfTask, matrix1, matrix2, result, k);
    }

    public void generateElements() {
        int i = iStart;
        int j = jStart;
        int size = sizeOfTask;  // count
        while (size > 0 && i < result.nrOfRows) {
            pairs.add(new Pair<>(i, j));
            size--;
            i += (j + k) / result.nrOfColumns;  // k = number of threads
            j = (j + k) % result.nrOfRows;
        }
    }
}
