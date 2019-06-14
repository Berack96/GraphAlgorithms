package berack96.sim.util.graph;

/**
 * Class used for retrieving the edges of the graph.
 *
 * @param <V> the vertices
 * @param <W> the weight of the edge
 * @author Berack96
 */
public class Edge<V, W extends Number> {

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
    private final W weight;

    /**
     * Create an final version of this object
     *
     * @param source      the source of the edge
     * @param destination the destination of the edge
     * @param weight      the weight of the edge
     */
    public Edge(V source, V destination, W weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
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
    public W getWeight() {
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
}
