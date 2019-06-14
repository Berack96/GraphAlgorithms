package berack96.sim.util.graph.view.vertex;

import java.util.Arrays;

import berack96.sim.util.graph.Graph;
import berack96.sim.util.graph.view.GraphPanel;

public class VertexIntListener extends VertexListener<Integer> {

    public VertexIntListener(GraphPanel<Integer, ?> panel) {
        super(panel);
    }

    @Override
    public void remove() {}
    
    @Override
    protected Integer buildNewVertex(Graph<Integer, ?> graph) {
    	int counter = 0;
    	Integer[] vertices = graph.vertices().toArray(new Integer[graph.numberOfVertices()]);
    	Arrays.sort(vertices);
    	
    	for(int i = 0; i<vertices.length; i++) {
    		if(!vertices[i].equals(counter))
    			return counter;
    		counter++;
    	}
        return counter;
    }
}
