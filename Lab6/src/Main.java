import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws Exception {
        // Hamiltonian Cycle: 0 -> 1 -> 2 -> 4 -> 3 -> 0
        Graph graphWithHamiltonianCycle = new Graph(
                new ArrayList<>(List.of(0, 1, 2, 3, 4)),
                new ArrayList<>(List.of(
                        new ArrayList<>(List.of(1)),
                        new ArrayList<>(List.of(2, 3)),
                        new ArrayList<>(List.of(4)),
                        new ArrayList<>(List.of(0)),
                        new ArrayList<>(List.of(1, 3))
                )));
        System.out.println(graphWithHamiltonianCycle);

        // Hamiltonian Cycle: 0 -> 1 -> 2 -> 3 -> 4 -> 0
        Graph graphWithHamiltonianCycle2 = new Graph(
                new ArrayList<>(List.of(0, 1, 2, 3, 4)),
                new ArrayList<>(List.of(
                        new ArrayList<>(List.of(1)),
                        new ArrayList<>(List.of(2)),
                        new ArrayList<>(List.of(3)),
                        new ArrayList<>(List.of(4)),
                        new ArrayList<>(List.of(0))
                )));
//        System.out.println(graphWithHamiltonianCycle2);

        //edge 4 -> 3 is removed
        Graph graphWithoutHamiltonianCycle = new Graph(
                new ArrayList<>(List.of(0, 1, 2, 3, 4)),
                new ArrayList<>(List.of(
                        new ArrayList<>(List.of(1)),
                        new ArrayList<>(List.of(2, 3)),
                        new ArrayList<>(List.of(4)),
                        new ArrayList<>(List.of(0)),
                        new ArrayList<>(List.of(1))
                )));
//        System.out.println(graphWithoutHamiltonianCycle);

        long startTime = System.nanoTime();

        HamiltonianCycleManager graphWithHamiltonianCycleManager = new HamiltonianCycleManager(graphWithHamiltonianCycle);
        graphWithHamiltonianCycleManager.startHamiltonianCycleFinder();

//        HamiltonianCycleManager graphWithoutHamiltonianCycleManager = new HamiltonianCycleManager(graphWithoutHamiltonianCycle);
//        graphWithoutHamiltonianCycleManager.startHamiltonianCycleFinder();

        long duration = (System.nanoTime() - startTime) / 1000000;
        System.out.println("Duration: " + duration + " ms");
    }
}
