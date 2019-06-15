package berack96.lib.graph.view.vertex;

import java.awt.*;

import berack96.lib.graph.Vertex;

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
