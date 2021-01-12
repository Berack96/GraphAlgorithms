package berack96.lib.graph.view;

import berack96.lib.graph.view.edge.EdgeListener;
import berack96.lib.graph.view.vertex.VertexListener;
import berack96.lib.graph.visit.VisitStrategy;
import berack96.lib.graph.visit.impl.BFS;
import berack96.lib.graph.visit.impl.DFS;
import berack96.lib.graph.visit.impl.Dijkstra;
import berack96.lib.graph.visit.impl.Tarjan;

import javax.swing.*;
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
