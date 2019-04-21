package berack96.sim.util.graph.view;

import berack96.sim.util.graph.Graph;
import berack96.sim.util.graph.MapGraph;
import berack96.sim.util.graph.Vertex;
import berack96.sim.util.graph.view.edge.EdgeComponent;
import berack96.sim.util.graph.view.vertex.VertexComponent;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

@SuppressWarnings("unchecked")
public class GraphPanel<V, W extends Number> extends Component {

	private static final long serialVersionUID = 1L;
	private final GraphicalView<VertexComponent<V>> vertexRender;
    private final GraphicalView<EdgeComponent<V, W>> edgeRender;

    private final Container vertices = new Container();
    private final Container edges = new Container();

    private final Graph<V, W> graph = new MapGraph<>();
    private final Set<Observer> observers = new HashSet<>();
    
    private GraphListener old = null;

    public GraphPanel(GraphicalView<VertexComponent<V>> vertexRender, GraphicalView<EdgeComponent<V, W>> edgeRender) {
        this.vertexRender = vertexRender;
        this.edgeRender = edgeRender;
    }

    public Graph<V, W> getGraph() {
        return graph;
    }

    public void setGraphListener(GraphListener listener) {
    	if(old != null)
    		old.remove();
        for (MouseListener l : getMouseListeners())
            removeMouseListener(l);
        for (MouseMotionListener l : getMouseMotionListeners())
            removeMouseMotionListener(l);
        for (KeyListener l : getKeyListeners())
            removeKeyListener(l);

        old = listener;
        addMouseListener(listener);
        addMouseMotionListener(listener);
        addKeyListener(listener);
    }

    public void addVertex(Point center, V vertex) {
        VertexComponent<V> component = getVertexAt(center);

        if (component == null) {
            VertexComponent<V> v = new VertexComponent<>(new Vertex<>(graph, vertex));
            if (!graph.contains(vertex)) {
                v.vertex.addIfAbsent();
                v.setBounds(vertexRender.getBox(v, center));
                vertices.add(v);
            }
        }
    }

    public void removeVertex(Point center) {
        try {
            VertexComponent<V> component = getVertexAt(center);
            component.vertex.remove();
            vertices.remove(component);
        } catch (Exception ignore) {}
    }

    public void moveVertex(VertexComponent<V> vertex, Point destination) {
        Rectangle rectangle = vertexRender.getBox(vertex, destination);
        vertex.setLocation(rectangle.x, rectangle.y);
    }

    public void addEdge(VertexComponent<V> source, VertexComponent<V> dest, W weight) {
        try {
            Point center = new Point(Math.abs(source.getX() - dest.getY()), Math.abs(source.getY() - dest.getY()));
            EdgeComponent<V, W> edgeComponent = new EdgeComponent<>(source, dest, weight);
            edgeComponent.setBounds(edgeRender.getBox(edgeComponent, center));
            edges.add(edgeComponent);
            graph.addEdge(edgeComponent.edge);
        } catch (Exception ignore) {}
    }

    public void removeEdge(VertexComponent<V> source, VertexComponent<V> dest) {
        try {
            graph.removeEdge(source.vertex.getValue(), dest.vertex.getValue());
            EdgeComponent<V, W> toRemove = null;
            for (Component c : edges.getComponents()) {
                EdgeComponent<V, W> edge = (EdgeComponent<V, W>) c;
                if (edge.source.equals(source) && edge.destination.equals(dest))
                    toRemove = edge;
            }
            edges.remove(toRemove);

        } catch (Exception ignore) {}
    }

    public void modEdge(VertexComponent<V> source, VertexComponent<V> dest, W weight) {
        removeEdge(source, dest);
        addEdge(source, dest, weight);
    }

    public VertexComponent<V> getVertexAt(Point point) {
        Component component = vertices.getComponentAt(point);
        return component instanceof VertexComponent ? (VertexComponent<V>) component : null;
    }

    public EdgeComponent<V, W> getEdgeAt(Point point) {
        Component component = edges.getComponentAt(point);
        return component instanceof EdgeComponent ? (EdgeComponent<V, W>) component : null;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }


    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        vertices.setBounds(x, y, width, height);
        edges.setBounds(x, y, width, height);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Collection<EdgeComponent<V, W>> toRemove = new HashSet<>();
        for (Component component : edges.getComponents()) {
            EdgeComponent<V, W> edge = (EdgeComponent<V, W>) component;
            Vertex<V> source = edge.source.vertex;
            Vertex<V> dest = edge.destination.vertex;
            if (source.isStillContained() && dest.isStillContained() && graph.containsEdge(source.getValue(), dest.getValue())) {
                Point center = new Point(edge.getX() + edge.getWidth() / 2, edge.getY() + edge.getHeight() / 2);
                edge.setBounds(edgeRender.getBox(edge, center));
                edgeRender.paint((Graphics2D) g2.create(), edge, center);
            }
            else
                toRemove.add(edge);
        }
        toRemove.forEach(edges::remove);

        for (Component component : vertices.getComponents()) {
            VertexComponent<V> vertex = (VertexComponent<V>) component;
            if (graph.contains(vertex.vertex.getValue())) {
                Point center = new Point(vertex.getX() + vertex.getWidth() / 2, vertex.getY() + vertex.getHeight() / 2);
                vertexRender.paint((Graphics2D) g2.create(), vertex, center);
            }
        }

        updateObservers();
    }

    private void updateObservers() {
        observers.forEach(observer -> observer.update(null, this.graph));
    }

}