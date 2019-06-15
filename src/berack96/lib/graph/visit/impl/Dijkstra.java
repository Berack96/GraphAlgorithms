package berack96.lib.graph.visit.impl;

import java.util.*;
import java.util.function.Consumer;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import berack96.lib.graph.visit.VisitDistance;

/**
 * Class that implements the Dijkstra algorithm and uses it for getting all the distance from a source
 *
 * @param <V> vertex
 * @param <W> weight
 * @author Berack96
 */
public class Dijkstra<V, W extends Number> implements VisitDistance<V, W> {

    private Map<V, List<Edge<V, W>>> distance = null;
    private V source = null;

    @Override
    public Map<V, List<Edge<V, W>>> getLastDistance() {
        return distance;
    }

    @Override
    public V getLastSource() {
        return source;
    }

    @Override
    public VisitInfo<V> visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        VisitInfo<V> info = new VisitInfo<>(source);
        Queue<QueueEntry> queue = new PriorityQueue<>();
        Map<V, Double> dist = new HashMap<>();
        Map<V, V> prev = new HashMap<>();

        this.source = source;
        dist.put(source, 0.0);                // Initialization
        queue.add(new QueueEntry(source, 0.0));

        while (!queue.isEmpty()) {                      // The main loop
            QueueEntry u = queue.poll();                    // Remove and return best vertex

            info.setVisited(u.entry);
            if (visit != null)
                visit.accept(u.entry);

            graph.getEdgesOut(u.entry).forEach((edge) -> {
                V child = edge.getDestination();
                info.setDiscovered(child);
                double alt = dist.get(u.entry) + edge.getWeight().doubleValue();
                Double distCurrent = dist.get(child);
                if (distCurrent == null || alt < distCurrent) {
                    dist.put(child, alt);
                    prev.put(child, u.entry);

                    QueueEntry current = new QueueEntry(child, alt);
                    queue.remove(current);
                    queue.add(current);
                }
            });
        }

        /* Cleaning up the results */
        distance = new HashMap<>();
        for (V vertex : prev.keySet()) {
            List<Edge<V, W>> path = new LinkedList<>();
            V child = vertex;
            V father = prev.get(child);
            do {
                Edge<V, W> edge = new Edge<>(father, child, graph.getWeight(father, child));
                path.add(0, edge);
                info.setParent(father, child);
                child = father;
                father = prev.get(child);
            } while (father != null);

            distance.put(vertex, new ArrayList<>(path));
        }
        return info;
    }

    private class QueueEntry implements Comparable<QueueEntry> {
        final V entry;
        final Double weight;

        QueueEntry(V entry, Double weight) {
            this.entry = entry;
            this.weight = weight;
        }

        @SuppressWarnings("unchecked")
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
        	double ret = this.weight - queueEntry.weight;
            return ret==0? 0: ret<0? -1:1;
        }
    }
}
