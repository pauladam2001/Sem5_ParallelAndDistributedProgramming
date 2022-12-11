import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Graph {
    private final List<Integer> nodes;
    private final List<List<Integer>> edges;

    public Graph(List<Integer> nodes, List<List<Integer>> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public int size() {
        return nodes.size();
    }

    public List<Integer> neighboursOf(int node) {
        return edges.get(node);
    }

    @Override
    public String toString() {
        return "Graph {\n" +" nodes = " + nodes + ",\n edges = " + edges + "\n}\n";
    }
}
