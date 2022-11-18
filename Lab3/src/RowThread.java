public class RowThread extends MatrixThread {
    public RowThread(int iStart, int jStart, int sizeOfTask, Matrix matrix1, Matrix matrix2, Matrix result) {
        super(iStart, jStart, sizeOfTask, matrix1, matrix2, result);
    }

    public void generateElements() {
        int i = iStart;
        int j = jStart;
        int size = sizeOfTask;  // count
        while (size > 0 && i < result.nrOfRows && j < result.nrOfColumns) {
            pairs.add(new Pair<>(i, j));
            j++;
            size--;
            if (j == result.nrOfRows) {
                j = 0;
                i++;
            }
        }
    }
}
