import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Solution {
    public static void main(String[] args) throws IOException {
        // Set up input reader and output writer
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        // Read the number of nodes and edges in the graph
        String[] gNodesEdges = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
        int gNodes = Integer.parseInt(gNodesEdges[0]); // Number of nodes in the graph
        int gEdges = Integer.parseInt(gNodesEdges[1]); // Number of edges in the graph

        // Lists to store graph data: gFrom - start nodes, gTo - end nodes, gWeight - weights
        List<Integer> gFrom = new ArrayList<>();
        List<Integer> gTo = new ArrayList<>();
        List<Integer> gWeight = new ArrayList<>();

        // Read the edges of the graph
        IntStream.range(0, gEdges).forEach(i -> {
            try {
                String[] gFromToWeight = bufferedReader.readLine().replaceAll("\\s+$", "").split(" ");
                gFrom.add(Integer.parseInt(gFromToWeight[0])); // Add start node of the edge
                gTo.add(Integer.parseInt(gFromToWeight[1])); // Add end node of the edge
                gWeight.add(Integer.parseInt(gFromToWeight[2])); // Add weight of the edge
            } catch (IOException ex) {
                throw new RuntimeException(ex); // Handle unexpected I/O error
            }
        });

        // Compute the minimum spanning tree weight using Kruskal's algorithm
        int res = Result.kruskals(gNodes, gFrom, gTo, gWeight);

        // Write the result to output
        bufferedWriter.write(String.valueOf(res));
        bufferedWriter.newLine();

        // Close buffered reader and writer
        bufferedReader.close();
        bufferedWriter.close();
    }
}

class Result {
    public static int kruskals(int gNodes, List<Integer> gFrom, List<Integer> gTo, List<Integer> gWeight) {
        // Initialize parent array for union-find and an array to store rank of nodes
        int[] parent = new int[gNodes + 1];
        int[] rank = new int[gNodes + 1];

        // Initialize each node as its own parent and rank as 0
        for (int i = 1; i <= gNodes; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        // Create a list of all edges with their weights
        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < gFrom.size(); i++) {
            edges.add(new int[]{gFrom.get(i), gTo.get(i), gWeight.get(i)});
        }

        // Sort edges by weight, breaking ties by summing node indices
        edges.sort((a, b) -> {
            if (a[2] != b[2]) {
                return Integer.compare(a[2], b[2]);
            } else {
                int sumA = a[0] + a[1];
                int sumB = b[0] + b[1];
                return Integer.compare(sumA, sumB);
            }
        });

        int result = 0;

        // Iterate through each edge
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            int weight = edge[2];

            // Find the root parent of both nodes
            int rootFrom = find(parent, from);
            int rootTo = find(parent, to);

            // If roots are different, include this edge in the MST
            if (rootFrom != rootTo) {
                result += weight; // Add weight of edge to the result
                union(parent, rank, rootFrom, rootTo); // Union the two sets
            }
        }
        return result; // Return the total weight of MST
    }

    private static int find(int[] parent, int node) {
        // Path compression: make every node in the path point directly to the root
        if (parent[node] != node) {
            parent[node] = find(parent, parent[node]);
        }
        return parent[node];
    }

    private static void union(int[] parent, int[] rank, int rootX, int rootY) {
        // Union by rank: attach the tree with lower rank to the tree with higher rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            // If ranks are equal, choose one as root and increment its rank
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }
}