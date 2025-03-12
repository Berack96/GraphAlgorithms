package net.berack.upo.graph.view;

import java.awt.*;

import net.berack.upo.graph.view.edge.EdgeIntListener;
import net.berack.upo.graph.view.edge.EdgeView;
import net.berack.upo.graph.view.vertex.VertexIntListener;
import net.berack.upo.graph.view.vertex.VertexView;

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
