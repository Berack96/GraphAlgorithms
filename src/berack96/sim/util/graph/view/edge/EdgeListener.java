package berack96.sim.util.graph.view.edge;

import berack96.sim.util.graph.Vertex;
import berack96.sim.util.graph.view.GraphListener;
import berack96.sim.util.graph.view.GraphPanel;
import berack96.sim.util.graph.view.vertex.VertexComponent;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

public abstract class EdgeListener<V, W extends Number> implements GraphListener {

    private final GraphPanel<V, W> graphPanel;
    private final AtomicReference<VertexComponent<V>> componentPressed = new AtomicReference<>();
    private final AtomicReference<Integer> buttonPressed = new AtomicReference<>();
    private final AtomicReference<EdgeComponent<V, W>> edge = new AtomicReference<>();
    private final StringBuilder string = new StringBuilder();

    public EdgeListener(GraphPanel<V, W> graphPanel) {
        this.graphPanel = graphPanel;
    }

    protected abstract W buildNewEdge(Vertex<V> vertex, Vertex<V> vertex1);

    protected abstract W buildEdgeFrom(String string);

    @Override
    public String getDescription() {
    	return "<html>"
    			+ "Modify edges with:<br />"
    			+ "mouse SX on vertex to another ==> add<br />"
    			+ "mouse DX on edge ==> change weigth<br />"
    			+ "(only numbers allowed)"
    			+ "</html>";
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        buttonPressed.set(e.getButton());
        componentPressed.set(graphPanel.getVertexAt(e.getPoint()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    	try {
            edge.get().source.vertex.unMark("modS");
    		edge.get().destination.vertex.unMark("modD");
            edge.set(null);
    	} catch (Exception ignored) {}
    	
        if (buttonPressed.get() == MouseEvent.BUTTON1) {
            try {
                VertexComponent<V> source = componentPressed.get();
                VertexComponent<V> destination = graphPanel.getVertexAt(e.getPoint());

                if (!graphPanel.getGraph().containsEdge(source.vertex.getValue(), destination.vertex.getValue())
                	&& !source.vertex.equals(destination.vertex))
                    graphPanel.addEdge(source, destination, buildNewEdge(source.vertex, destination.vertex));
            } catch (Exception ignore) {
            }
        } else {
            edge.set(graphPanel.getEdgeAt(e.getPoint()));
            
            try {
	            edge.get().source.vertex.mark("modS");
	            edge.get().destination.vertex.mark("modD");
	            graphPanel.setFocusTraversalKeysEnabled(false);
	            graphPanel.requestFocusInWindow();
        	} catch (Exception ignored) {}
        }

        string.delete(0, string.length());
        componentPressed.set(null);
        graphPanel.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (edge.get() != null && Character.isDigit(e.getKeyChar())) {
            string.append(e.getKeyChar());
            try {
                graphPanel.modEdge(edge.get().source, edge.get().destination, buildEdgeFrom(string.toString()));
                graphPanel.repaint();
            } catch (Exception ignored) {
            }
        } else {
        	
        	try {
	            edge.get().source.vertex.unMark("modS");
	            edge.get().destination.vertex.unMark("modD");
        	} catch (Exception ignored) {}
        	
            edge.set(null);
            string.delete(0, string.length());
        }

        graphPanel.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
