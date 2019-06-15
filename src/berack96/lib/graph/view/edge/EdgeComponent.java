package berack96.lib.graph.view.edge;

import berack96.lib.graph.Edge;
import berack96.lib.graph.view.vertex.VertexComponent;

import java.awt.*;

public class EdgeComponent<V, W extends Number> extends Component {
	private static final long serialVersionUID = 1L;
	
	public final VertexComponent<V> source;
    public final VertexComponent<V> destination;
    public final W weight;
    public final Edge<V, W> edge;

    public EdgeComponent(VertexComponent<V> source, VertexComponent<V> destination, W weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        this.edge = new Edge<>(source.vertex.getValue(), destination.vertex.getValue(), weight);
    }
}
