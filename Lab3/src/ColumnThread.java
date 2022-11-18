public class ColumnThread extends MatrixThread {
    public ColumnThread(int iStart, int jStart, int sizeOfTask, Matrix matrix1, Matrix matrix2, Matrix result) {
        super(iStart, jStart, sizeOfTask, matrix1, matrix2, result);
    }

    public void generateElements() {
        int i = iStart;
        int j = jStart;
        int size = sizeOfTask;  // count
        while (size > 0 && i < result.nrOfRows && j < result.nrOfColumns) {
            pairs.add(new Pair<>(i, j));
            i++;
            size--;
            if (i == result.nrOfColumns) {
                i = 0;
                j++;
            }
        }
    }
}
