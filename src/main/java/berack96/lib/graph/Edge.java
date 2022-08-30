package berack96.lib.graph;

import java.util.Collection;
import java.util.List;

/**
 * Class used for retrieving the edges of the graph.
 *
 * @param <V> the vertices
 * @author Berack96
 */
public class Edge<V> implements Comparable<Edge<V>> {

    /**
     * The source vertex
     */
    private final V source;
    /**
     * The destination vertex
     */
    private final V destination;
    /**
     * The weight of this edge
     */
    private final int weight;

    /**
     * Create a final version of this object with weight 1
     *
     * @param source      the source of the edge
     * @param destination the destination of the edge
     */
    public Edge(V source, V destination) {
        this(source, destination, 1);
    }

    /**
     * Create a final version of this object
     *
     * @param source      the source of the edge
     * @param destination the destination of the edge
     * @param weight      the weight of the edge
     */
    public Edge(V source, V destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    /**
     *
     */
    public Collection<V> getVertices() {
        if (source == null && destination == null)
            return List.of();
        if (source == null)
            return List.of(destination);
        if (destination == null)
            return List.of(source);
        return List.of(source, destination);
    }

    /**
     * The vertex where the edge goes
     *
     * @return the vertex
     */
    public V getDestination() {
        return destination;
    }

    /**
     * The vertex where the edge starts from
     *
     * @return the vertex
     */
    public V getSource() {
        return source;
    }

    /**
     * The weight of the edge
     *
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "[" + source + " -> " + destination + ", " + weight + "]";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return obj.getClass().equals(getClass()) && obj.toString().equals(toString());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int compareTo(Edge<V> edge) {
        return weight - edge.weight;
    }
}
