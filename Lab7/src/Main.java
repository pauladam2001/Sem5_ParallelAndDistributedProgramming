import mpi.MPI;

import java.util.Objects;

public class Main {
    static final int POLYNOMIAL_SIZE = 5;
    static final String IMPLEMENTATION = "regular";

    public static void masterProcess(Polynomial p1, Polynomial p2, int processes) {
        long startTime = System.nanoTime();
        int length = p1.getSize() / (processes - 1);

        // split the coefficients of the polynomials on processes

        int start;
        int end = 0;

        for (int i = 1; i < processes; i++) {
            start = end;
            end = start + length;
            if (i == processes - 1) {
                end = p1.getSize();         // last coefficient of the polynomial
            }

            // offset = from where we start (what we send)
            // count = how much we send
            // MPI.OBJECT, MPI.INT = parameter type
            // i = id of the process to which we send the information
            MPI.COMM_WORLD.Send(new Object[]{p1}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new Object[]{p2}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{start}, 0, 1, MPI.INT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{end}, 0, 1, MPI.INT, i, 0);
        }

        Object[] results = new Object[processes - 1];
        for (int i = 1; i  < processes; i++){
            MPI.COMM_WORLD.Recv(results, i - 1, 1, MPI.OBJECT, i, 0);
        }

        Polynomial result = Algorithms.getResult(results);

        long endTime = System.nanoTime();
        double time = (endTime - startTime) / 1000000000.0;
        System.out.println(IMPLEMENTATION + ":\n\n");
        System.out.println("Execution time: " + time);
        System.out.println("\n\n");
        System.out.println(result + "\n\n");
    }

    public static void classicWorker(){
        Object[] p1 = new Object[2];
        Object[] p2= new Object[2];
        int[] start = new int[1];
        int[] end = new int[1];

        // source = id of the process to which we send the information (here we send to process 0, the 'master' process)
        MPI.COMM_WORLD.Recv(p1, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(p2, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(start, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(end, 0, 1, MPI.INT, 0, 0);

        Polynomial pol1 = (Polynomial) p1[0];
        Polynomial pol2 = (Polynomial) p2[0];

        Polynomial result = Algorithms.multiplySequence(pol1, pol2, start[0], end[0]);

        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
    }

    public static void karatsubaWorker(){
        Object[] p1 = new Object[2];
        Object[] p2= new Object[2];
        int[] start = new int[1];
        int[] end = new int[1];

        // source = id of the process to which we send the information (here we send to process 0, the 'master' process)
        MPI.COMM_WORLD.Recv(p1, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(p2, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(start, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(end, 0, 1, MPI.INT, 0, 0);

        Polynomial pol1 = (Polynomial) p1[0];
        Polynomial pol2 = (Polynomial) p2[0];

        // we need the following 2 loops so that the process will know for what coefficients (from p1) to do the multiplications (with p2)
        for (int i = 0; i < start[0]; i++) {
            pol1.getCoefficients().set(i, 0);
        }
        for (int j = end[0]; j < pol1.getCoefficients().size(); j++) {
            pol1.getCoefficients().set(j, 0);
        }

        Polynomial result = Algorithms.KaratsubaSequential(pol1, pol2);

        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
    }

    public static void main(String[] args) {
        MPI.Init(args);                     // an MPI program must call MPI.Init in the beginning

        int me = MPI.COMM_WORLD.Rank();     // one's own ID
        int size = MPI.COMM_WORLD.Size();   // the number of launched instances

        if (me == 0){
            System.out.println("Master process: \n");
            Polynomial p1 = new Polynomial(POLYNOMIAL_SIZE);
            p1.generateCoefficients();
            Polynomial p2 = new Polynomial(POLYNOMIAL_SIZE);
            p2.generateCoefficients();

            System.out.println("Polynomial 1:" + p1);
            System.out.println("Polynomial 2:" + p2);
            System.out.println("\n\n");

            masterProcess(p1, p2, size);
        } else {
            if (Objects.equals(IMPLEMENTATION, "regular")){
                classicWorker();
            }
            if (Objects.equals(IMPLEMENTATION, "karatsuba")){
                karatsubaWorker();
            }
        }

        MPI.Finalize();                     // an MPI program must call MPI.Finalize in the end
    }
}
