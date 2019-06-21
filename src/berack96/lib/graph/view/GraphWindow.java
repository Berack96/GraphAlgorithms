package berack96.lib.graph.view;

import java.awt.BorderLayout;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JFrame;

import berack96.lib.graph.view.edge.EdgeListener;
import berack96.lib.graph.view.vertex.VertexListener;
import berack96.lib.graph.visit.VisitStrategy;
import berack96.lib.graph.visit.impl.BFS;
import berack96.lib.graph.visit.impl.DFS;
import berack96.lib.graph.visit.impl.Dijkstra;
import berack96.lib.graph.visit.impl.Tarjan;

/**
 * This class is the Window that appear for building the graph and playing around with it
 *
 * @author Berack96
 */
public class GraphWindow<V, W extends Number> extends JFrame {

	private static final long serialVersionUID = 1L;

    private final GraphPanel<V, W> graphPanel;
    private final GraphInfo<V, W> infoPanel;

    public GraphWindow(GraphPanel<V, W> graphPanel, VertexListener<V> vListener, EdgeListener<V, W> eListener) {
        this.setTitle("Grafo");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        Set<VisitStrategy<V, W>> strats = new LinkedHashSet<>();
        strats.add(new DFS<>());
        strats.add(new BFS<>());
        strats.add(new Dijkstra<>());
        strats.add(new Tarjan<>());

        this.infoPanel = new GraphInfo<>(graphPanel, vListener, eListener, strats);
        this.graphPanel = graphPanel;
        this.add(infoPanel, BorderLayout.EAST);
        this.add(graphPanel);
    }

    public void visitRefresh(int millisec) {
        VisitListener.changeRefresh(millisec);
    }

    public GraphPanel<V, W> getGraphPanel() {
        return graphPanel;
    }
}
