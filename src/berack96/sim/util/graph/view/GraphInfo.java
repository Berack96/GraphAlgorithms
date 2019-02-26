package berack96.sim.util.graph.view;

import berack96.sim.util.graph.Graph;
import berack96.sim.util.graph.view.edge.EdgeListener;
import berack96.sim.util.graph.view.vertex.VertexListener;
import berack96.sim.util.graph.visit.VisitStrategy;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;

public class GraphInfo<V, W extends Number> extends JPanel {

	private static final long serialVersionUID = 1L;
	
    private final Map<String, VisitListener<V>> visits;

    public GraphInfo(GraphPanel<V, W> graphPanel, VertexListener<V> vListener, EdgeListener<V, W> eListener, Set<VisitStrategy<V, W>> visits) {
        this.visits = new HashMap<>();

        /* ZERO (DESCRIPTION) */
        JLabel listenerDescription = new JLabel();
        
        JPanel panelDescription = new JPanel();
        panelDescription.setOpaque(false);
        panelDescription.add(listenerDescription);
        
        /* FIRST (GRAPH INFO) */

        JLabel vNumber = new JLabel(String.valueOf(graphPanel.getGraph().numberOfVertices()));
        JLabel eNumber = new JLabel(String.valueOf(graphPanel.getGraph().numberOfEdges()));
        JLabel gCyclic = new JLabel(String.valueOf(graphPanel.getGraph().isCyclic()));

        List<Component> components = new LinkedList<>();
        JLabel selected = new JLabel();
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                try {
                    String clazz = (String) e.getItem();
                    VisitListener<V> listener = this.visits.get(clazz);
                    
                    selected.setText(listener != null? "visit":"nothing");
                	listenerDescription.setText(listener != null? listener.getDescription():"");
                	
	                graphPanel.getGraph().unMarkAll();
	                graphPanel.repaint();
	                graphPanel.setGraphListener(listener);
                    
                } catch (Exception ignore) {}
            }

        });

        comboBox.addItem("None");
        for(VisitStrategy<V, W> strategy: visits) {
            String clazz = strategy.getClass().getSimpleName();
            VisitListener<V> visit = new VisitListener<>(graphPanel, strategy);
            comboBox.addItem(clazz);
            this.visits.put(clazz, visit);
        }

        components.add(new JLabel("Visit Strategy: "));
        components.add(comboBox);
        components.add(new JLabel("Selected modality: "));
        components.add(selected);
        components.add(new JLabel("Vertex Number: "));
        components.add(vNumber);
        components.add(new JLabel("Edge Number: "));
        components.add(eNumber);
        components.add(new JLabel("Is Cyclic: "));
        components.add(gCyclic);

        JPanel panelInfo = new JPanel();
        panelInfo.setOpaque(false);
        panelInfo.setBorder(BorderFactory.createLineBorder(Color.RED));
        panelInfo.setLayout(new GridLayout(components.size()/2, 2, 2*2, 2*2));
        components.forEach(panelInfo::add);
        components.clear();

        /* SECOND (VERTEX) */
        JLabel vVertex = new JLabel();
        JLabel vEdgesNumber = new JLabel();
        JLabel vEdgesInNumber = new JLabel();
        JLabel vEdgesOutNumber = new JLabel();

        JButton modVertex = new JButton("Modify Vertices");
        modVertex.addActionListener(a -> {
            comboBox.setSelectedIndex(0);
    		listenerDescription.setText(vListener.getDescription());
            graphPanel.setGraphListener(vListener);
            graphPanel.getGraph().unMarkAll();
            graphPanel.repaint();
            selected.setText("vertices");
        });
        
        JButton modEdge = new JButton("Modify Edges");
        modEdge.addActionListener(a -> {
            comboBox.setSelectedIndex(0);
    		listenerDescription.setText(eListener.getDescription());
            graphPanel.setGraphListener(eListener);
            graphPanel.getGraph().unMarkAll();
            graphPanel.repaint();
            selected.setText("edges");
        });

        components.add(modVertex);
        components.add(modEdge);
        components.add(new JLabel("Vertex name: "));
        components.add(vVertex);
        components.add(new JLabel("Edges: "));
        components.add(vEdgesNumber);
        components.add(new JLabel("Edges IN: "));
        components.add(vEdgesInNumber);
        components.add(new JLabel("Edges OUT: "));
        components.add(vEdgesOutNumber);
        
        JPanel panelVertex = new JPanel();
        panelVertex.setOpaque(false);
        panelVertex.setLayout(new GridLayout(components.size()/2, 2, 2*2, 2*2));
        components.forEach(panelVertex::add);

        /* Save/Load 
        JPanel panelSave = new JPanel(); */


        /* ADDING COMPONENTS */
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(true);
        this.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED, Color.GRAY, Color.DARK_GRAY));
        this.add(panelDescription);
        this.add(panelInfo);
        this.add(panelVertex);
        this.add(Box.createVerticalGlue());

        modVertex.doClick();

        graphPanel.addObserver((o, arg) -> {
            Graph<V, W> graph = graphPanel.getGraph();
            if(arg.equals(graph)) {
                vNumber.setText(String.valueOf(graph.numberOfVertices()));
                eNumber.setText(String.valueOf(graph.numberOfEdges()));
                gCyclic.setText(String.valueOf(graph.isCyclic()));
                
                /* There should be only one */
                for(V v : graph.getMarkedWith("selected")) {
                	int inE = graph.getEdgesIn(v).size();
                	int outE = graph.getEdgesOut(v).size();
                	
                	vEdgesInNumber.setText(String.valueOf(inE));
                	vEdgesOutNumber.setText(String.valueOf(outE));
                	vEdgesNumber.setText(String.valueOf(inE + outE));
                	vVertex.setText(v.toString());
                }
            }
        });
    }
}
