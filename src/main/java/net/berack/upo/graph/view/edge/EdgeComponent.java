package net.berack.upo.graph.view.edge;

import java.awt.*;
import java.io.Serial;

import net.berack.upo.graph.Edge;
import net.berack.upo.graph.view.vertex.VertexComponent;

public class EdgeComponent<V> extends Component {
    @Serial
    private static final long serialVersionUID = 1L;

    public final VertexComponent<V> source;
    public final VertexComponent<V> destination;
    public final int weight;
    public final Edge<V> edge;

    public EdgeComponent(VertexComponent<V> source, VertexComponent<V> destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.edge = new Edge<>(source.vertex.get(), destination.vertex.get(), weight);
    }
}
