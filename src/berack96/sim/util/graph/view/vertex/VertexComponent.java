package berack96.sim.util.graph.view.vertex;

import berack96.sim.util.graph.Vertex;

import java.awt.*;

public class VertexComponent<V> extends Component {
    private static final long serialVersionUID = 1L;
    
	public final Vertex<V> vertex;

    public VertexComponent(Vertex<V> vertex) {
        this.vertex = vertex;
    }
    
    
    @Override
    public String toString() {
    	return "[" + vertex + " {" + getX() + "," + getY() + "}]";
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
    	try {
    		return obj.getClass().equals(getClass()) && ((VertexComponent<V>)obj).vertex.equals(vertex);	
    	} catch (Exception e) {
    		return false;
    	}
    }
}
