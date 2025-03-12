package net.berack.upo.graph.view;

import javax.swing.*;

import net.berack.upo.Graph;
import net.berack.upo.graph.VisitStrategy;
import net.berack.upo.graph.view.vertex.VertexComponent;
import net.berack.upo.graph.visit.VisitInfo;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class VisitListener<V> implements GraphListener {

    private final GraphPanel<V> panel;
    private final VisitStrategy<V> strategy;
    private final Set<Timer> timers = new HashSet<>();

    private static int refreshTime = 1000;

    public VisitListener(GraphPanel<V> panel, VisitStrategy<V> strategy) {
        this.panel = panel;
        this.strategy = strategy;
    }

    public static void changeRefresh(int mills) {
        refreshTime = mills;
    }

    @Override
    public void remove() {
    	timers.forEach(Timer::stop);
        timers.clear();
    }

    @Override
    public String getDescription() {
    	return "<html>"
    			+ "Start a visit by pressing<br />"
    			+ "with the mouse SX on the root vertex<br />"
    			+ "</html>";
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        this.remove();

        Graph<V> graph = panel.getGraph();
        graph.unMarkAll();
        panel.repaint();

        if (e.getButton() == MouseEvent.BUTTON1)
            try {
                VertexComponent<V> vertex = panel.getVertexAt(e.getPoint());
                AtomicInteger count = new AtomicInteger(0);
                VisitInfo<V> info = vertex.vertex.visit(strategy, null);

                info.forEach(v -> {
                    final boolean visited = v.timeVisited == count.get();
                    Timer timer = new Timer(count.getAndIncrement() * refreshTime, _ -> {
                        if (visited && v.parent !=null)
                        	graph.mark(v.vertex, v.parent);
                        graph.mark(v.vertex, visited ? "visited" : "discovered");
                        panel.repaint();
                    });

                    timers.add(timer);
                    timer.setRepeats(false);
                    timer.start();
                });

            } catch (Exception ignore) {}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
