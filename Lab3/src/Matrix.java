import java.util.Random;

public class Matrix {
    public final int nrOfRows, nrOfColumns;
    public int[][] elements;

    public Matrix(int nrOfRows, int nrOfColumns) {
        this.nrOfRows = nrOfRows;
        this.nrOfColumns = nrOfColumns;
        elements = new int[nrOfRows][nrOfColumns];
        generateMatrix();
    }

    private void generateMatrix() {
        Random rand = new Random();
        for (int i = 0; i < nrOfRows; i++) {
            for (int j = 0; j < nrOfColumns; j++) {
                elements[i][j] = rand.nextInt(100) + 1;
            }
        }
    }

    public int getElement(int i, int j) {
        return elements[i][j];
    }

    public void setElement(int i, int j, int value) {
        elements[i][j] = value;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < nrOfRows; i++) {
            for (int j = 0; j < nrOfColumns; j++) {
                stringBuilder.append(elements[i][j]).append(" ");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
