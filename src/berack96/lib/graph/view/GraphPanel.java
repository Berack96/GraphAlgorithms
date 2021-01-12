package berack96.lib.graph.view;

import berack96.lib.graph.Graph;
import berack96.lib.graph.Vertex;
import berack96.lib.graph.impl.MapGraph;
import berack96.lib.graph.view.edge.EdgeComponent;
import berack96.lib.graph.view.vertex.VertexComponent;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public class GraphPanel<V> extends Component {

    @Serial
    private static final long serialVersionUID = 1L;
    private final GraphicalView<VertexComponent<V>> vertexRender;
    private final GraphicalView<EdgeComponent<V>> edgeRender;
    private final Class<V> classV;

    final Container vertices = new Container();
    final Container edges = new Container();

    private final Graph<V> graph = new MapGraph<>();
    private final Set<Observer> observers = new HashSet<>();

    private GraphListener old = null;

    public GraphPanel(GraphicalView<VertexComponent<V>> vertexRender, GraphicalView<EdgeComponent<V>> edgeRender, Class<V> classV) {
        this.vertexRender = vertexRender;
        this.edgeRender = edgeRender;
        this.classV = classV;
    }

    public Graph<V> getGraph() {
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
            v.vertex.addIfAbsent();
            boolean isContained = false;
            
            for(Component comp : vertices.getComponents())
                if (comp.equals(v)) {
                    isContained = true;
                    break;
                }
            
            if (!isContained) {
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
        } catch (Exception ignore) {
        }
    }

    public void moveVertex(VertexComponent<V> vertex, Point destination) {
        Rectangle rectangle = vertexRender.getBox(vertex, destination);
        vertex.setLocation(rectangle.x, rectangle.y);
    }

    public void addEdge(V source, V destination, int weight) {
        VertexComponent<V> vSource = null;
        VertexComponent<V> vDest = null;
        for (Component comp : vertices.getComponents()) {
            VertexComponent<V> temp = (VertexComponent<V>) comp;
            V vTemp = temp.vertex.get();
            if (vSource == null && vTemp.equals(source))
                vSource = temp;
            if (vDest == null && vTemp.equals(destination))
                vDest = temp;
        }
        addEdge(vSource, vDest, weight);
    }

    public void addEdge(VertexComponent<V> source, VertexComponent<V> dest, int weight) {
        try {
            Point center = new Point(Math.abs(source.getX() - dest.getY()), Math.abs(source.getY() - dest.getY()));
            EdgeComponent<V> edgeComponent = new EdgeComponent<>(source, dest, weight);
            edgeComponent.setBounds(edgeRender.getBox(edgeComponent, center));
            edges.add(edgeComponent);
            graph.addEdge(edgeComponent.edge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeEdge(VertexComponent<V> source, VertexComponent<V> dest) {
        try {
            graph.removeEdge(source.vertex.get(), dest.vertex.get());
            EdgeComponent<V> toRemove = null;
            for (Component c : edges.getComponents()) {
                EdgeComponent<V> edge = (EdgeComponent<V>) c;
                if (edge.source.equals(source) && edge.destination.equals(dest))
                    toRemove = edge;
            }
            edges.remove(toRemove);

        } catch (Exception ignore) {}
    }

    public void modEdge(VertexComponent<V> source, VertexComponent<V> dest, int weight) {
        removeEdge(source, dest);
        addEdge(source, dest, weight);
    }

    public VertexComponent<V> getVertexAt(Point point) {
        Component component = vertices.getComponentAt(point);
        return component instanceof VertexComponent ? (VertexComponent<V>) component : null;
    }

    public EdgeComponent<V> getEdgeAt(Point point) {
        Component component = edges.getComponentAt(point);
        return component instanceof EdgeComponent ? (EdgeComponent<V>) component : null;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void save(String fileName) throws IOException {
        new GraphPointsSave<>(this).save(graph, fileName);
    }
    
    public void load(String fileName) throws IOException {
        new GraphPointsSave<>(this).load(graph, fileName, classV);
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

        Collection<EdgeComponent<V>> toRemove = new HashSet<>();
        for (Component component : edges.getComponents()) {
            EdgeComponent<V> edge = (EdgeComponent<V>) component;
            Vertex<V> source = edge.source.vertex;
            Vertex<V> dest = edge.destination.vertex;
            if (source.isStillContained() && dest.isStillContained() && graph.containsEdge(source.get(), dest.get())) {
                Point center = new Point(edge.getX() + edge.getWidth() / 2, edge.getY() + edge.getHeight() / 2);
                edge.setBounds(edgeRender.getBox(edge, center));
                edgeRender.paint((Graphics2D) g2.create(), edge, center);
            } else
                toRemove.add(edge);
        }
        toRemove.forEach(edges::remove);

        for (Component component : vertices.getComponents()) {
            VertexComponent<V> vertex = (VertexComponent<V>) component;
            if (graph.contains(vertex.vertex.get())) {
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
