package berack96.sim.util.graph.visit;

import berack96.sim.util.graph.Graph;

import java.util.*;
import java.util.function.Consumer;

public class Dijkstra<V, W extends Number> implements VisitStrategy<V, W> {

    private Map<V, List<Graph.Edge<V, W>>> distance;

    /**
     * Get the last calculated distance to all the possible destinations<br>
     * The map contains all the possible vertices that are reachable from the source set in the visit<br>
     * If there is no path between the destination and the source, then null is returned as accordingly to the map interface<br>
     * If the visit is not already been done, then the map is null.
     *
     * @return the last distance
     */
    public Map<V, List<Graph.Edge<V, W>>> getLastDistance() {
        return distance;
    }

    @Override
    public void visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        Queue<QueueEntry<V, Integer>> queue = new PriorityQueue<>();
        Map<V, Integer> dist = new HashMap<>();
        Map<V, V> prev = new HashMap<>();

        dist.put(source, 0);                // Initialization
        queue.add(new QueueEntry<>(source, 0));

        while (!queue.isEmpty()) {                      // The main loop
            QueueEntry<V, Integer> u = queue.poll();                    // Remove and return best vertex
            graph.getChildrenAndWeight(u.entry).forEach((vertex, weight) -> {
                int alt = dist.get(u.entry) + weight.intValue();
                Integer distCurrent = dist.get(vertex);
                if (distCurrent == null || alt < distCurrent) {
                    dist.put(vertex, alt);
                    prev.put(vertex, u.entry);

                    QueueEntry<V, Integer> current = new QueueEntry<>(vertex, alt);
                    queue.remove(current);
                    queue.add(current);
                }
            });
        }

        /* Cleaning up the results */
        distance = new HashMap<>();
        for (V vertex : prev.keySet()) {
            List<Graph.Edge<V, W>> path = new LinkedList<>();
            V child = vertex;
            V father = prev.get(child);
            do {
                Graph.Edge<V, W> edge = new Graph.Edge<>(father, child, graph.getWeight(father, child));
                path.add(0, edge);
                child = father;
                father = prev.get(child);
            } while (father != null);

            distance.put(vertex, new ArrayList<>(path));
        }
    }

    private class QueueEntry<V, W extends Number> implements Comparable<QueueEntry> {
        final V entry;
        final W weight;

        QueueEntry(V entry, W weight) {
            this.entry = entry;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object obj) {
            try {
                return ((QueueEntry) obj).entry.equals(entry);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public int compareTo(QueueEntry queueEntry) {
            return this.weight.intValue() - queueEntry.weight.intValue();
        }
    }
}
