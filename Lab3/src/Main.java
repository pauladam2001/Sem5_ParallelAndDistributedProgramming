import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int matrix1Rows = 1000;
    private static final int matrix1Columns = 1000;
    private static final int matrix2Rows = 1000;
    private static final int matrix2Columns = 1000;
    private static final String tasksWork = "Row";  // "Column", "Kth"
    private static final int nrOfThreads = 40;
    private static final boolean manualThreadsApproach = true;  // false

    public static void runManualThreads(Matrix matrix1, Matrix matrix2, Matrix result) throws Exception {
        List<Thread> threadList = new ArrayList<>();
        switch (tasksWork) {
            case "Row":
                for (int i = 0; i < nrOfThreads; i++) {
                    threadList.add(ThreadManager.initRowThread(i, matrix1, matrix2, result, nrOfThreads));
                }
                break;
            case "Column":
                for (int i = 0; i < nrOfThreads; i++) {
                    threadList.add(ThreadManager.initColumnThread(i, matrix1, matrix2, result, nrOfThreads));
                }
                break;
            case "Kth":
                for (int i = 0; i < nrOfThreads; i++) {
                    threadList.add(ThreadManager.initKthThread(i, matrix1, matrix2, result, nrOfThreads));
                }
                break;
            default:
                throw new Exception("Invalid task work!");
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        System.out.println("Final matrix:\n" + result);
    }

    public static void runThreadPool(Matrix matrix1, Matrix matrix2, Matrix result) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(nrOfThreads);
        switch (tasksWork) {
            case "Row":
                for (int i = 0; i < nrOfThreads; i++) {
                    executorService.submit(ThreadManager.initRowThread(i, matrix1, matrix2, result, nrOfThreads));
                }
                break;
            case "Column":
                for (int i = 0; i < nrOfThreads; i++) {
                    executorService.submit(ThreadManager.initColumnThread(i, matrix1, matrix2, result, nrOfThreads));
                }
                break;
            case "Kth":
                for (int i = 0; i < nrOfThreads; i++) {
                    executorService.submit(ThreadManager.initKthThread(i, matrix1, matrix2, result, nrOfThreads));
                }
                break;
            default:
                throw new Exception("Invalid task work!");
        }

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(180, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
//            System.out.println("Final matrix:\n" + result);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Matrix matrix1 = new Matrix(matrix1Rows, matrix1Columns);
        Matrix matrix2 = new Matrix(matrix2Rows, matrix2Columns);

//        System.out.println(matrix1);
//        System.out.println("----------------------------\n");
//        System.out.println(matrix2);
//        System.out.println("----------------------------\n");

        Matrix result = new Matrix(matrix1.nrOfRows, matrix2.nrOfColumns);

        float startTime = System.nanoTime();
        if (manualThreadsApproach) {
            try {
                runManualThreads(matrix1, matrix2, result);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            try {
                runThreadPool(matrix1, matrix2, result);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("Time elapsed: " + (System.nanoTime() - startTime)/1_000_000_000.0 + " seconds.");
    }
}
