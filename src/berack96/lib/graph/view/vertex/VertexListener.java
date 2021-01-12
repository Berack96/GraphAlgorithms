package berack96.lib.graph.view.vertex;

import berack96.lib.graph.Graph;
import berack96.lib.graph.view.GraphListener;
import berack96.lib.graph.view.GraphPanel;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

public abstract class VertexListener<V> implements GraphListener {

    protected final GraphPanel<V> panel;
    private final AtomicReference<VertexComponent<V>> componentPressed = new AtomicReference<>();

    public VertexListener(GraphPanel<V> panel) {
        this.panel = panel;
    }

    protected abstract V buildNewVertex(Graph<V> graph);

    @Override
    public String getDescription() {
    	return "<html>"
    			+ "Modify vertex with:<br />"
    			+ "mouse SX ==> add<br />"
    			+ "mouse SX on vertex   ==> move<br />"
    			+ "mouse DX ==> remove<br />"
    			+ "</html>";
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        try {
            if (e.getButton() == MouseEvent.BUTTON1)
                componentPressed.set(panel.getVertexAt(e.getPoint()));
        } catch (Exception ignore) {}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && componentPressed.get() == null)
            panel.addVertex(e.getPoint(), buildNewVertex(panel.getGraph()));
        else if (e.getButton() == MouseEvent.BUTTON3)
            panel.removeVertex(e.getPoint());

        panel.getGraph().unMarkAll("selected");

        if(componentPressed.get() != null)
            componentPressed.get().vertex.mark("selected");

        panel.repaint();
        componentPressed.set(null);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (componentPressed.get() != null) {
            panel.moveVertex(componentPressed.get(), e.getPoint());
            panel.repaint();
        }
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
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
