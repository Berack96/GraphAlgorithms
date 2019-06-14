package berack96.sim.util.graph.view;

import berack96.sim.util.graph.view.edge.EdgeIntListener;
import berack96.sim.util.graph.view.edge.EdgeListener;
import berack96.sim.util.graph.view.edge.EdgeView;
import berack96.sim.util.graph.view.vertex.VertexIntListener;
import berack96.sim.util.graph.view.vertex.VertexListener;
import berack96.sim.util.graph.view.vertex.VertexView;
import berack96.sim.util.graph.visit.*;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class is the Window that appear for building the graph and playing around with it
 *
 * @author Berack96
 */
public class GraphWindow<V, W extends Number> extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
        GraphPanel<Integer, Integer> panel = new GraphPanel<>(new VertexView<>(), new EdgeView<>(), Integer.class, Integer.class);
        GraphWindow<Integer, Integer> win = new GraphWindow<>(panel, new VertexIntListener(panel), new EdgeIntListener<>(panel));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); // full screen
        dim.setSize(dim.width / 2, dim.height / 2);
        win.setSize(dim);
        win.setLocationRelativeTo(null); //centered
        win.visitRefresh(500);

        win.setVisible(true);
    }

    private final GraphPanel<V, W> graphPanel;
    private final GraphInfo<V, W> infoPanel;

    private GraphWindow(GraphPanel<V, W> graphPanel, VertexListener<V> vListener, EdgeListener<V, W> eListener) {
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
