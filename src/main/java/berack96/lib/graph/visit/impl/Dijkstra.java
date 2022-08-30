package berack96.lib.graph.visit.impl;

import berack96.lib.graph.Edge;
import berack96.lib.graph.Graph;
import berack96.lib.graph.visit.VisitDistance;

import java.util.*;
import java.util.function.Consumer;

/**
 * Class that implements the Dijkstra algorithm and uses it for getting all the distance from a source
 *
 * @param <V> vertex
 * @author Berack96
 */
public class Dijkstra<V> implements VisitDistance<V> {

    private Map<V, List<Edge<V>>> distance = null;
    private V source = null;

    @Override
    public Map<V, List<Edge<V>>> getLastDistance() {
        return distance;
    }

    @Override
    public V getLastSource() {
        return source;
    }

    @Override
    public VisitInfo<V> visit(Graph<V> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException {
        VisitInfo<V> info = new VisitInfo<>(source);
        Queue<QueueEntry> queue = new PriorityQueue<>();
        Map<V, Integer> dist = Graph.getDefaultMap();
        Map<V, V> prev = Graph.getDefaultMap();

        this.source = source;
        dist.put(source, 0);                // Initialization
        queue.add(new QueueEntry(source, 0));

        while (!queue.isEmpty()) {                      // The main loop
            QueueEntry u = queue.poll();                    // Remove and return best vertex

            info.setVisited(u.entry);
            if (visit != null)
                visit.accept(u.entry);

            for (V child : graph.getChildren(u.entry)) {
                info.setDiscovered(child);
                int alt = dist.get(u.entry) + graph.getWeight(u.entry, child);
                Integer distCurrent = dist.get(child);

                if (distCurrent == null || alt < distCurrent) {
                    dist.put(child, alt);
                    prev.put(child, u.entry);

                    QueueEntry current = new QueueEntry(child, alt);
                    queue.remove(current);
                    queue.add(current);
                }
            }
        }

        /* Cleaning up the results */
        distance = Graph.getDefaultMap();
        for (V vertex : prev.keySet()) {
            List<Edge<V>> path = new LinkedList<>();
            V child = vertex;
            V father = prev.get(child);
            do {
                Edge<V> edge = new Edge<>(father, child, graph.getWeight(father, child));
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
        final int weight;

        QueueEntry(V entry, int weight) {
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
