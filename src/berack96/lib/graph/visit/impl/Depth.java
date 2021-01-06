package berack96.lib.graph.visit.impl;

import berack96.lib.graph.Graph;
import berack96.lib.graph.visit.VisitStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Depth<V, W extends Number> implements VisitStrategy<V, W> {

	private long finalDepth;
	
	public Depth(long depth) {
		this.finalDepth = depth;
	}

	public void setDepth(long depth) {
		this.finalDepth = depth;
	}

	@Override
	public VisitInfo<V> visit(Graph<V, W> graph, V source, Consumer<V> visit) throws NullPointerException, IllegalArgumentException, UnsupportedOperationException {
		VisitInfo<V> info = new VisitInfo<>(source);
		long currentDepth = info.getDepth(source);

        if(visit != null)
        	visit.accept(source);
        info.setVisited(source);
		
		List<V> toVisit = new LinkedList<>();
        toVisit.add(source);

        while (!toVisit.isEmpty() && currentDepth < finalDepth) {
        	V current = toVisit.remove(0);
            currentDepth = info.getDepth(current) + 1;
            
            for (V child : graph.getChildrens(current))
                if (!info.isDiscovered(child)) {
                    if(visit != null)
                    	visit.accept(child);
                    
                	info.setVisited(child);
                	info.setParent(current, child);
                	toVisit.add(child);

                }
        }
        
        return info;
	}

}
