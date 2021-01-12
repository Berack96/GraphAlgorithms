package berack96.lib.graph.view;

import berack96.lib.graph.view.edge.EdgeIntListener;
import berack96.lib.graph.view.edge.EdgeView;
import berack96.lib.graph.view.vertex.VertexIntListener;
import berack96.lib.graph.view.vertex.VertexView;

import java.awt.*;

public class Main {

        public static void main(String[] args) {
                GraphPanel<Integer> panel = new GraphPanel<>(new VertexView<>(), new EdgeView<>(), Integer.class);
                GraphWindow<Integer> win = new GraphWindow<>(panel, new VertexIntListener(panel), new EdgeIntListener<>(panel));
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); // full screen
                dim.setSize(dim.width / 2, dim.height / 2);
                win.setSize(dim);
                win.setLocationRelativeTo(null); //centered
                win.visitRefresh(500);

                win.setVisible(true);
        }
}
