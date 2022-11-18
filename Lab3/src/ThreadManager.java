public class ThreadManager {
    public static int computeElement(Matrix matrix1, Matrix matrix2, int i, int j) throws Exception {
        if (i < matrix1.nrOfRows && j < matrix2.nrOfColumns) {
            int element = 0;
            for (int l = 0; l < matrix1.nrOfColumns; l++) {
                element += matrix1.getElement(i, l) * matrix2.getElement(l, j);
            }
            return element;
        } else {
            throw new Exception("Index out of bounds!");
        }
    }

    public static MatrixThread initRowThread(int index, Matrix matrix1, Matrix matrix2, Matrix result, int nrOfThreads) {
        int resultSize = result.nrOfRows * result.nrOfColumns;
        int count = resultSize / nrOfThreads;
        int iStart = count * index / result.nrOfColumns;
        int jStart = count * index % result.nrOfColumns;

        if (index == nrOfThreads - 1)
            count += resultSize % nrOfThreads;

        return new RowThread(iStart, jStart, count, matrix1, matrix2, result);
    }

    public static MatrixThread initColumnThread(int index, Matrix matrix1, Matrix matrix2, Matrix result, int nrOfThreads) {
        int resultSize = result.nrOfRows * result.nrOfColumns;
        int count = resultSize / nrOfThreads;
        int iStart = count * index % result.nrOfRows;
        int jStart = count * index / result.nrOfRows;

        if (index == nrOfThreads - 1)
            count += resultSize % nrOfThreads;

        return new ColumnThread(iStart, jStart, count, matrix1, matrix2, result);
    }

    public static MatrixThread initKthThread(int index, Matrix matrix1, Matrix matrix2, Matrix result, int nrOfThreads) {
        int resultSize = result.nrOfRows * result.nrOfColumns;
        int count = resultSize / nrOfThreads;
        int iStart = index / result.nrOfColumns;
        int jStart = index % result.nrOfColumns;

        if (index < resultSize % nrOfThreads)
            count++;

        return new KthThread(iStart, jStart, count, matrix1, matrix2, result, nrOfThreads);
    }
}
