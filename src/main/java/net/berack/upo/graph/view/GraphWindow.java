package net.berack.upo.graph.view;

import javax.swing.*;

import net.berack.upo.graph.VisitStrategy;
import net.berack.upo.graph.view.edge.EdgeListener;
import net.berack.upo.graph.view.vertex.VertexListener;
import net.berack.upo.graph.visit.BFS;
import net.berack.upo.graph.visit.DFS;
import net.berack.upo.graph.visit.Dijkstra;
import net.berack.upo.graph.visit.Tarjan;

import java.awt.*;
import java.io.Serial;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class is the Window that appear for building the graph and playing around with it
 *
 * @author Berack96
 */
public class GraphWindow<V> extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private final GraphPanel<V> graphPanel;

    public GraphWindow(GraphPanel<V> graphPanel, VertexListener<V> vListener, EdgeListener<V> eListener) {
        this.setTitle("Grafo");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        Set<VisitStrategy<V>> strats = new LinkedHashSet<>();
        strats.add(new DFS<>());
        strats.add(new BFS<>());
        strats.add(new Dijkstra<>());
        strats.add(new Tarjan<>());

        GraphInfo<V> infoPanel = new GraphInfo<>(graphPanel, vListener, eListener, strats);
        this.graphPanel = graphPanel;
        this.add(infoPanel, BorderLayout.EAST);
        this.add(graphPanel);
    }

    public void visitRefresh(int millis) {
        VisitListener.changeRefresh(millis);
    }

    public GraphPanel<V> getGraphPanel() {
        return graphPanel;
    }
}
