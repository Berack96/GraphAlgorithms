package berack96.lib.graph.view;

import berack96.lib.graph.Graph;
import berack96.lib.graph.models.GraphSaveStructure;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class GraphPointsSave<V, W extends Number> extends GraphSaveStructure<V, W> {

    final private GraphPanel<V,W> panel;
    public Point[] points;

    public GraphPointsSave(GraphPanel<V,W> panel) {
        this.panel = panel;
    }

    @Override
    protected void saveGraph(Graph<V, W> graph) {
        super.saveGraph(graph);
        List<Point> p = new LinkedList<>();

        for(Component vertex : panel.vertices.getComponents()) {
            Point temp = new Point(vertex.getX(), vertex.getY());
            temp.x += vertex.getWidth() / 2;
            temp.y += vertex.getHeight() / 2;
            p.add(temp);
        }

        int i = 0;
        this.points = new Point[p.size()];
        for(Point pt : p)
            this.points[i++] = pt;
    }

    @Override
    public void loadGraph(Graph<V, W> graph, Class<V> classV, Class<W> classW) {
        super.loadGraph(graph, classV, classW);

        panel.vertices.removeAll();
        panel.edges.removeAll();

        for(int i = 0; i<vertices.length; i++) {
            V v = gson.fromJson(vertices[i], classV);
            Point p = points[i];
            panel.addVertex(p, v);
        }

        for(String v : vertices)
            graph.getEdgesOut(gson.fromJson(v, classV)).forEach(panel::addEdge);
        panel.repaint();
    }
}
