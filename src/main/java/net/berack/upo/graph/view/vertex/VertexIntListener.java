package net.berack.upo.graph.view.vertex;

import java.util.Arrays;

import net.berack.upo.Graph;
import net.berack.upo.graph.view.GraphPanel;

public class VertexIntListener extends VertexListener<Integer> {

    public VertexIntListener(GraphPanel<Integer> panel) {
        super(panel);
    }

    @Override
    public void remove() {}

    @Override
    protected Integer buildNewVertex(Graph<Integer> graph) {
        int counter = 0;
        Integer[] vertices = graph.vertices().toArray(new Integer[graph.size()]);
        Arrays.sort(vertices);

        for (Integer vertex : vertices) {
            if (!vertex.equals(counter))
                return counter;
            counter++;
        }
        return counter;
    }
}
