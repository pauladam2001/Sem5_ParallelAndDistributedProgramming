import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HamiltonianCycleManager {
    public Graph graph;

    public HamiltonianCycleManager(Graph graph) {
        this.graph = graph;
    }

    public void startHamiltonianCycleFinder() throws Exception {
        ArrayList<Integer> path = new ArrayList<>();

        path.add(0);    // always start from 0 in order to return a single Hamiltonian Cycle
        findHamiltonianCycle(0, path);
    }

    public void findHamiltonianCycle(int currentNode, ArrayList<Integer> path) throws Exception {
        // base case
        // we can reach the first node from the current node and the path size is equal to the number of nodes
        if (graph.neighboursOf(currentNode).contains(0) && path.size() == graph.size()) {
            System.out.println("Hamiltonian Cycle found:" + path + "\n");
            return;
        }

        // if we visited all nodes and the above condition is not met, then there is no Hamiltonian Cycle
        if (path.size() == graph.size()) {
            return;
        }

        // start checking all possible edges
        for (int i = 0; i < graph.size(); i++) {
            // if there is an edge to the node, and we haven't already visited it,  we add it to the path and mark it as visited (temporarily remove the edge)
            if (graph.neighboursOf(currentNode).contains(i) && !(path.contains(i))) {
                path.add(i);
                graph.neighboursOf(currentNode).remove(Integer.valueOf(i));

                // call findHamiltonianCycle for this node recursively in a new thread
                ExecutorService executorService = Executors.newFixedThreadPool(4);  // an object that executes submitted Runnable tasks and provides methods to manage termination and methods that can produce a Future for tracking progress of one or more asynchronous tasks
                final int node = i;     // error otherwise, it needs to be final in lambda expression
                ArrayList<Integer> copiedPath = new ArrayList<>(path);

                // the Runnable interface should be implemented by any class whose instances are intended to be executed by a thread. The class must define a method of no arguments called run.
                final Runnable task = () -> {   // https://www.tutorialspoint.com/how-to-implement-the-runnable-interface-using-lambda-expression-in-java
                    try {
                        findHamiltonianCycle(node, copiedPath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };

                executorService.submit(task);   // submits a value-returning task for execution and returns a Future representing the pending results of the task

                executorService.shutdown();     // initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted
                executorService.awaitTermination(50000, TimeUnit.MILLISECONDS);     // blocks until all tasks have completed execution after a shutdown request

                // add the removed edge back so the paths can be correctly used
                graph.neighboursOf(currentNode).add(i);

                // delete the path after it was checked
                path.remove(path.size() - 1);
            }
        }
    }
}
