package net.berack.upo.graph.view;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import net.berack.upo.Graph;
import net.berack.upo.graph.savemodels.GraphSaveStructure;

public class GraphPointsSave<V> extends GraphSaveStructure<V> {

    final private GraphPanel<V> panel;
    public Point[] points;

    public GraphPointsSave(GraphPanel<V> panel) {
        this.panel = panel;
    }

    @Override
    protected void saveGraph(Graph<V> graph) {
        super.saveGraph(graph);
        List<Point> p = new LinkedList<>();

        for (Component vertex : panel.vertices.getComponents()) {
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
    public void loadGraph(Graph<V> graph, Class<V> classV) {
        super.loadGraph(graph, classV);
        panel.vertices.removeAll();
        panel.edges.removeAll();

        for (int i = 0; i < vertices.length; i++) {
            V v = gson.fromJson(vertices[i], classV);
            Point p = points[i];
            panel.addVertex(p, v);
        }

        for (String v : vertices) {
            V src = gson.fromJson(v, classV);
            graph.getChildren(src).forEach(child -> panel.addEdge(src, child, graph.getWeight(src, child)));
        }
        panel.repaint();
    }
}
