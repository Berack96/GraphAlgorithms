package berack96.lib.graph.view.edge;

import berack96.lib.graph.Vertex;
import berack96.lib.graph.view.GraphPanel;

public class EdgeIntListener<V> extends EdgeListener<V, Integer> {

    public EdgeIntListener(GraphPanel<V, Integer> graphPanel) {
        super(graphPanel);
    }

    @Override
    public void remove() {}
    
    @Override
    protected Integer buildNewEdge(Vertex<V> vertex, Vertex<V> vertex1) {
        return (int) (Math.random() * 100);
    }

    @Override
    protected Integer buildEdgeFrom(String string) {
        return Integer.parseInt(string.replaceAll("[^0-9]+", ""));
    }
}
