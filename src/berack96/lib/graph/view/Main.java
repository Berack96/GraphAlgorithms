package berack96.lib.graph.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import berack96.lib.graph.view.edge.EdgeIntListener;
import berack96.lib.graph.view.edge.EdgeView;
import berack96.lib.graph.view.vertex.VertexIntListener;
import berack96.lib.graph.view.vertex.VertexView;

public class Main {


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
}
