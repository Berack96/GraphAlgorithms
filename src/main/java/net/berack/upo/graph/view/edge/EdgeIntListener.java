package net.berack.upo.graph.view.edge;

import net.berack.upo.graph.Vertex;
import net.berack.upo.graph.view.GraphPanel;

public class EdgeIntListener<V> extends EdgeListener<V> {

    public EdgeIntListener(GraphPanel<V> graphPanel) {
        super(graphPanel);
    }

    @Override
    public void remove() {
    }

    @Override
    protected int buildNewEdge(Vertex<V> vertex, Vertex<V> vertex1) {
        return (int) (Math.random() * 100);
    }

    @Override
    protected int buildEdgeFrom(String string) {
        return Integer.parseInt(string.replaceAll("[^0-9]+", ""));
    }
}
