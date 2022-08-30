package berack96.lib.graph.view.vertex;

import berack96.lib.graph.Graph;
import berack96.lib.graph.view.GraphPanel;

import java.util.Arrays;

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
