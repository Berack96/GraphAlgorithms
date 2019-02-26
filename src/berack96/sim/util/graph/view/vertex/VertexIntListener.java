package berack96.sim.util.graph.view.vertex;

import berack96.sim.util.graph.Graph;
import berack96.sim.util.graph.view.GraphPanel;

public class VertexIntListener extends VertexListener<Integer> {

    private Integer counter = 0;

    public VertexIntListener(GraphPanel<Integer, ?> panel) {
        super(panel);
    }

    @Override
    public void remove() {}
    
    @Override
    protected Integer buildNewVertex(Graph<Integer, ?> graph) {
        if(graph.numberOfVertices() == 0)
            counter = 0;
        counter++;
        return counter - 1;
    }
}
