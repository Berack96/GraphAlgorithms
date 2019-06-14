package berack96.test.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import berack96.sim.util.graph.Edge;
import berack96.sim.util.graph.Graph;
import berack96.sim.util.graph.MapGraph;
import berack96.sim.util.graph.Vertex;
import berack96.sim.util.graph.visit.BFS;
import berack96.sim.util.graph.visit.DFS;
import berack96.sim.util.graph.visit.VisitInfo;

@SuppressWarnings("deprecation")
public class TestGraph {

    private Graph<String, Integer> graph;
    
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    
    private final String encoding = "UTF-8";
    private final Exception nullException = new NullPointerException(Graph.PARAM_NULL);
    private final Exception notException = new IllegalArgumentException(Graph.VERTEX_NOT_CONTAINED);
    private final Exception unsuppException = new UnsupportedOperationException(Vertex.REMOVED);

    
    @Before
    public void before() {
        // Change here the instance for changing all the test for that particular class
        graph = new MapGraph<>();

        PrintStream p = null;
        try {
			p = new PrintStream(bytes, true, encoding);
	        System.setErr(p);
	        System.setOut(p);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    @After
    public void after() {
    	try {
			String printed = bytes.toString(encoding);
			bytes.reset();
			if (!printed.isEmpty())
				fail("Remove the printed string in the methods: " + printed);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }

    @Test
    public void basicVertex() {
        assertEquals(0, graph.numberOfVertices());

        graph.addVertex("1");
        graph.addVertex("2");
        shouldThrow(nullException, () -> graph.addVertex(null));

        assertTrue(graph.contains("1"));
        assertFalse(graph.contains("0"));
        assertTrue(graph.contains("2"));
        assertFalse(graph.contains("3"));
        assertEquals(2, graph.numberOfVertices());

        graph.removeVertex("1");
        assertFalse(graph.contains("1"));
        assertTrue(graph.contains("2"));
        assertEquals(1, graph.numberOfVertices());

        graph.addVertex("3");
        assertTrue(graph.contains("3"));
        shouldThrow(nullException, () -> graph.contains(null));
        shouldThrow(nullException, () -> graph.addVertexIfAbsent(null));

        assertTrue(graph.addVertexIfAbsent("4"));
        assertFalse(graph.addVertexIfAbsent("4"));
        assertFalse(graph.addVertexIfAbsent("2"));

        assertEquals(3, graph.numberOfVertices());
        shouldContain(graph.vertices(), "2", "3", "4");

        graph.removeAllVertex();
        shouldContain(graph.vertices());

        Set<String> vertices = new HashSet<>(Arrays.asList("1", "5", "24", "2", "3"));
        graph.addAllVertices(vertices);
        shouldContain(graph.vertices(), vertices.toArray());
        graph.removeVertex("1");
        graph.removeVertex("24");
        shouldContain(graph.vertices(), "5", "2", "3");
        graph.addAllVertices(vertices);
        shouldContain(graph.vertices(), vertices.toArray());

        shouldThrow(nullException, () -> graph.addAllVertices(null));
    }

    @Test
    public void basicEdge() {
        /*
         * This graph should be like this
         *
         * 1  ->  2
         * |      |
         * v      v
         * 3  <-> 5  ->  4
         */
        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");

        shouldThrow(nullException, () -> graph.addEdge(null, "2", 1));
        shouldThrow(nullException, () -> graph.addEdge(null, null, 1));
        shouldThrow(nullException, () -> graph.addEdge("1", null, 1));
        shouldThrow(new NullPointerException(), () -> graph.addEdge(null));
        shouldThrow(nullException, () -> graph.addEdge(new Edge<>("1", null, 1)));
        shouldThrow(nullException, () -> graph.addEdge(new Edge<>(null, null, 1)));
        shouldThrow(nullException, () -> graph.addEdge(new Edge<>(null, "2", 1)));
        shouldThrow(nullException, () -> graph.containsEdge(null, "2"));
        shouldThrow(nullException, () -> graph.containsEdge(null, null));
        shouldThrow(nullException, () -> graph.containsEdge("1", null));
        shouldThrow(nullException, () -> graph.removeEdge(null, "2"));
        shouldThrow(nullException, () -> graph.removeEdge(null, null));
        shouldThrow(nullException, () -> graph.removeEdge("1", null));
        shouldThrow(nullException, () -> graph.removeAllEdge(null));
        shouldThrow(nullException, () -> graph.removeAllOutEdge(null));
        shouldThrow(nullException, () -> graph.removeAllInEdge(null));

        shouldThrow(notException, () -> graph.addEdge("0", "2", 1));
        shouldThrow(notException, () -> graph.addEdge("2", "8", 1));
        shouldThrow(notException, () -> graph.addEdge("9", "6", 1));
        shouldThrow(notException, () -> graph.removeEdge("012", "2"));
        shouldThrow(notException, () -> graph.removeEdge("2", "28"));
        shouldThrow(notException, () -> graph.removeEdge("4329", "62"));
        shouldThrow(notException, () -> graph.removeAllEdge("0"));
        shouldThrow(notException, () -> graph.removeAllInEdge("011"));
        shouldThrow(notException, () -> graph.removeAllOutEdge("9"));

        assertEquals(0, graph.numberOfEdges());

        assertNull(graph.addEdge("1", "2", 1));
        assertNull(graph.addEdge(new Edge<>("1", "3", 1)));
        assertNull(graph.addEdge("2", "5", 4));
        assertNull(graph.addEdge(new Edge<>("3", "5", 2)));
        assertNull(graph.addEdge(new Edge<>("5", "3", 2)));
        assertNull(graph.addEdge("5", "4", 3));

        assertEquals(6, graph.numberOfEdges());

        assertFalse(graph.containsEdge("01", "4"));
        assertFalse(graph.containsEdge("3", "8132"));
        assertFalse(graph.containsEdge("9423", "516"));

        // All this calls should do nothing
        graph.removeEdge("1", "5");
        graph.removeEdge("1", "4");
        graph.removeEdge("2", "3");
        graph.removeEdge("3", "1");
        graph.removeEdge("4", "5");

        assertEquals(6, graph.numberOfEdges());

        assertEquals(new Integer(1), graph.getWeight("1", "2"));
        assertEquals(new Integer(1), graph.getWeight("1", "3"));
        assertEquals(new Integer(4), graph.getWeight("2", "5"));
        assertEquals(new Integer(2), graph.getWeight("3", "5"));
        assertEquals(new Integer(2), graph.getWeight("5", "3"));
        assertEquals(new Integer(3), graph.getWeight("5", "4"));

        assertNull(graph.getWeight("1", "4"));

        assertEquals(new Integer(1), graph.addEdge("1", "2", 102));
        assertEquals(new Integer(102), graph.addEdge("1", "2", 3));
        assertEquals(new Integer(3), graph.addEdge("1", "2", 1));
        assertEquals(new Integer(1), graph.addEdge(new Edge<>("1", "2", 102)));
        assertEquals(new Integer(102), graph.addEdge(new Edge<>("1", "2", 3)));
        assertEquals(new Integer(3), graph.addEdge(new Edge<>("1", "2", 1)));

        assertEquals(6, graph.numberOfEdges());
        assertTrue(graph.containsEdge("1", "2"));
        assertFalse(graph.containsEdge("4", "3"));
        assertFalse(graph.containsEdge("2", "1"));
        assertFalse(graph.containsEdge("1", "4"));
        assertTrue(graph.containsEdge("1", "3"));
        assertTrue(graph.containsEdge("3", "5"));
        assertTrue(graph.containsEdge("2", "5"));

        graph.removeEdge("2", "5");
        assertFalse(graph.containsEdge("2", "5"));
        assertEquals(5, graph.numberOfEdges());

        graph.removeEdge("1", "2");
        assertFalse(graph.containsEdge("1", "2"));
        assertTrue(graph.containsEdge("1", "3"));
        assertEquals(4, graph.numberOfEdges());
        graph.addEdge("1", "2", 2);

        graph.removeAllOutEdge("1");
        assertFalse(graph.containsEdge("1", "2"));
        assertFalse(graph.containsEdge("1", "3"));
        assertEquals(3, graph.numberOfEdges());
        graph.addEdge("1", "2", 2);
        graph.addEdge("1", "3", 2);
        assertEquals(5, graph.numberOfEdges());

        graph.removeAllInEdge("3");
        assertFalse(graph.containsEdge("5", "3"));
        assertFalse(graph.containsEdge("1", "3"));
        assertTrue(graph.containsEdge("3", "5"));
        assertEquals(3, graph.numberOfEdges());
        graph.addEdge("1", "3", 2);
        graph.addEdge("5", "3", 2);

        graph.removeAllEdge("3");
        assertFalse(graph.containsEdge("5", "3"));
        assertFalse(graph.containsEdge("1", "3"));
        assertFalse(graph.containsEdge("3", "5"));
        assertEquals(2, graph.numberOfEdges());

        graph.removeAllEdge();
        assertFalse(graph.containsEdge("1", "2"));
        assertFalse(graph.containsEdge("1", "3"));
        assertFalse(graph.containsEdge("2", "5"));
        assertFalse(graph.containsEdge("3", "5"));
        assertFalse(graph.containsEdge("5", "3"));
        assertFalse(graph.containsEdge("5", "4"));
        assertEquals(0, graph.numberOfEdges());

        assertFalse(graph.containsEdge("2", "323"));
        assertNull(graph.addEdgeAndVertices("2", "323", 3));
        assertTrue(graph.containsEdge("2", "323"));
        assertFalse(graph.containsEdge("2aa", "323"));
        assertNull(graph.addEdgeAndVertices("2aa", "323", 35));
        assertTrue(graph.containsEdge("2aa", "323"));
        assertFalse(graph.containsEdge("2bbb", "323bbb"));
        assertNull(graph.addEdgeAndVertices("2bbb", "323bbb", 135));
        assertTrue(graph.containsEdge("2bbb", "323bbb"));

        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, "1", 1));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, null, 1));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices("2", null, 1));

        assertEquals(3, graph.addEdgeAndVertices("2", "323", 50).intValue());
        assertEquals(35, graph.addEdgeAndVertices("2aa", "323", 5).intValue());
        assertEquals(50, graph.addEdgeAndVertices("2", "323", 500).intValue());

        graph.removeAllEdge();

        assertFalse(graph.containsEdge("2", "323"));
        assertNull(graph.addEdgeAndVertices(new Edge<>("2", "323", 3)));
        assertTrue(graph.containsEdge("2", "323"));
        assertFalse(graph.containsEdge("2aa", "323"));
        assertNull(graph.addEdgeAndVertices(new Edge<>("2aa", "323", 35)));
        assertTrue(graph.containsEdge("2aa", "323"));
        assertFalse(graph.containsEdge("2bbb", "323bbb"));
        assertNull(graph.addEdgeAndVertices(new Edge<>("2bbb", "323bbb", 135)));
        assertTrue(graph.containsEdge("2bbb", "323bbb"));

        shouldThrow(nullException, () -> graph.addEdgeAndVertices(new Edge<>(null, "1", 1)));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(new Edge<>(null, null, 1)));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(new Edge<>("2", null, 1)));
        shouldThrow(new NullPointerException(), () -> graph.addEdgeAndVertices(null));

        assertEquals(3, graph.addEdgeAndVertices(new Edge<>("2", "323", 50)).intValue());
        assertEquals(35, graph.addEdgeAndVertices(new Edge<>("2aa", "323", 5)).intValue());
        assertEquals(50, graph.addEdgeAndVertices(new Edge<>("2", "323", 500)).intValue());

        graph.removeAllVertex();
        graph.addVertex("aaa");
        graph.addVertex("1");
        graph.addVertex("2");

        shouldContain(graph.vertices(), "1", "2", "aaa");
        shouldContain(graph.edges());

        Set<Edge<String, Integer>> edges = new HashSet<>();
        edges.add(new Edge<>("aaa", "bbb", 3));
        edges.add(new Edge<>("bbb", "ccc", 4));
        edges.add(new Edge<>("ccc", "aaa", 5));
        edges.add(new Edge<>("1", "2", 2));
        graph.addAllEdges(edges);

        shouldContain(graph.vertices(), "1", "2", "aaa", "bbb", "ccc");
        shouldContain(graph.edges(),
                new Edge<>("aaa", "bbb", 3),
                new Edge<>("bbb", "ccc", 4),
                new Edge<>("ccc", "aaa", 5),
                new Edge<>("1", "2", 2));
    }

    @Test
    public void advancedEdge() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  ->  6
         *               ^
         * |      |      |
         * v      v
         * 3  <-> 5  ->  4
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");

        shouldContain(graph.edges());

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("2", "6", 5);
        graph.addEdge("3", "5", 2);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "3", 9);
        graph.addEdge("5", "4", 5);

        shouldContain(graph.getChildren("1"), "2", "3");
        shouldContain(graph.getChildren("2"), "5", "6");
        shouldContain(graph.getChildren("3"), "5");
        shouldContain(graph.getChildren("4"), "6");
        shouldContain(graph.getChildren("5"), "3", "4");
        shouldContain(graph.getChildren("6"));

        shouldContain(graph.getAncestors("1"));
        shouldContain(graph.getAncestors("2"), "1");
        shouldContain(graph.getAncestors("3"), "1", "5");
        shouldContain(graph.getAncestors("4"), "5");
        shouldContain(graph.getAncestors("5"), "2", "3");
        shouldContain(graph.getAncestors("6"), "2", "4");

        shouldContain(graph.getEdgesOut("1"), new Edge<>("1", "2", 1), new Edge<>("1", "3", 1));
        shouldContain(graph.getEdgesOut("2"), new Edge<>("2", "5", 4), new Edge<>("2", "6", 5));
        shouldContain(graph.getEdgesOut("3"), new Edge<>("3", "5", 2));
        shouldContain(graph.getEdgesOut("4"), new Edge<>("4", "6", 6));
        shouldContain(graph.getEdgesOut("5"), new Edge<>("5", "3", 9), new Edge<>("5", "4", 5));
        shouldContain(graph.getEdgesOut("6"));

        shouldContain(graph.getEdgesIn("1"));
        shouldContain(graph.getEdgesIn("2"), new Edge<>("1", "2", 1));
        shouldContain(graph.getEdgesIn("3"), new Edge<>("1", "3", 1), new Edge<>("5", "3", 9));
        shouldContain(graph.getEdgesIn("4"), new Edge<>("5", "4", 5));
        shouldContain(graph.getEdgesIn("5"), new Edge<>("2", "5", 4), new Edge<>("3", "5", 2));
        shouldContain(graph.getEdgesIn("6"), new Edge<>("4", "6", 6), new Edge<>("2", "6", 5));

        assertEquals(0, graph.degreeIn("1"));
        assertEquals(1, graph.degreeIn("2"));
        assertEquals(2, graph.degreeIn("3"));
        assertEquals(1, graph.degreeIn("4"));
        assertEquals(2, graph.degreeIn("5"));
        assertEquals(2, graph.degreeIn("6"));

        assertEquals(2, graph.degreeOut("1"));
        assertEquals(2, graph.degreeOut("2"));
        assertEquals(1, graph.degreeOut("3"));
        assertEquals(1, graph.degreeOut("4"));
        assertEquals(2, graph.degreeOut("5"));
        assertEquals(0, graph.degreeOut("6"));

        assertEquals(2, graph.degree("1"));
        assertEquals(3, graph.degree("2"));
        assertEquals(3, graph.degree("3"));
        assertEquals(2, graph.degree("4"));
        assertEquals(4, graph.degree("5"));
        assertEquals(2, graph.degree("6"));

        shouldContain(graph.edges(),
                new Edge<>("1", "2", 1),
                new Edge<>("1", "3", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("2", "6", 5),
                new Edge<>("3", "5", 2),
                new Edge<>("4", "6", 6),
                new Edge<>("5", "3", 9),
                new Edge<>("5", "4", 5));

        shouldThrow(nullException, () -> graph.edgesOf(null));
        shouldThrow(notException, () -> graph.edgesOf("rew"));
        shouldContain(graph.edgesOf("5"),
                new Edge<>("2", "5", 4),
                new Edge<>("3", "5", 2),
                new Edge<>("5", "3", 9),
                new Edge<>("5", "4", 5));
    }

    @Test
    public void preBasicVisit() {
        VisitInfo<Integer> info = new VisitInfo<>(0);
        assertTrue(info.isDiscovered(0));
        assertFalse(info.isVisited(0));
        assertEquals(0, info.getTimeDiscover(0));
        assertEquals(new Integer(0), info.getSource());
        assertNull(info.getParentOf(0));

        assertFalse(info.isVisited(null));
        assertFalse(info.isDiscovered(null));

        shouldThrow(new IllegalArgumentException(), () -> info.getTimeVisit(0));
        shouldThrow(new IllegalArgumentException(), () -> info.getTimeDiscover(1));
        shouldThrow(new IllegalArgumentException(), () -> info.getParentOf(2));
        shouldThrow(new IllegalArgumentException(), () -> info.getParentOf(null));

        shouldThrow(new NullPointerException(), () -> info.getTimeDiscover(null));
        shouldThrow(new NullPointerException(), () -> info.getTimeVisit(null));
    }

    @Test
    public void basicVisit() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 6);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);
        graph.addEdge("8", "7", 8);

        Exception nullP = new NullPointerException();
        shouldThrow(nullP, () -> graph.visit(null, new DFS<>(), null));
        shouldThrow(nullP, () -> graph.visit(null, null, null));
        shouldThrow(nullP, () -> graph.visit("1", null, null));

        shouldThrow(notException, () -> graph.visit("1010", new DFS<>(), null));

        DFS<String, Integer> dfs = new DFS<>();
        VisitInfo<String> visitDFS = graph.visit("1", dfs, null);
        assertEquals(0, visitDFS.getTimeDiscover("1"));
        assertEquals(1, visitDFS.getTimeDiscover("2"));
        assertEquals(2, visitDFS.getTimeDiscover("5"));
        assertEquals(3, visitDFS.getTimeDiscover("3"));
        assertEquals(4, visitDFS.getTimeVisit("3"));
        assertEquals(5, visitDFS.getTimeDiscover("4"));
        assertEquals(6, visitDFS.getTimeDiscover("6"));
        assertEquals(7, visitDFS.getTimeVisit("6"));
        assertEquals(8, visitDFS.getTimeVisit("4"));
        assertEquals(9, visitDFS.getTimeVisit("5"));
        assertEquals(10, visitDFS.getTimeVisit("2"));
        assertEquals(11, visitDFS.getTimeVisit("1"));
        assertFalse(visitDFS.isDiscovered("7"));
        assertFalse(visitDFS.isDiscovered("8"));

        int[] discoverTime = {0, 1, 2, 3, 5, 6};
        String[] verticesDiscovered = {"1", "2", "5", "3", "4", "6"};
        AtomicInteger integer = new AtomicInteger(0);
        visitDFS.forEachDiscovered(vertexInfo -> {
            assertEquals(discoverTime[integer.get()], vertexInfo.timeDiscovered);
            assertEquals(verticesDiscovered[integer.get()], vertexInfo.vertex);
            integer.incrementAndGet();
        });
        integer.set(0);
        int[] visitTime = {4, 7, 8, 9, 10, 11};
        String[] verticesVisited = {"3", "6", "4", "5", "2", "1"};
        visitDFS.forEachVisited(vertexInfo -> {
            assertEquals(visitTime[integer.get()], vertexInfo.timeVisited);
            assertEquals(verticesVisited[integer.get()], vertexInfo.vertex);
            integer.incrementAndGet();
        });

        BFS<String, Integer> bfs = new BFS<>();
        VisitInfo<String> visitBFS = graph.visit("1", bfs, null);
        assertEquals(0, visitBFS.getTimeDiscover("1"));
        assertEquals(1, visitBFS.getTimeVisit("1"));
        assertEquals(2, visitBFS.getTimeDiscover("2"));
        assertEquals(3, visitBFS.getTimeVisit("2"));
        assertEquals(4, visitBFS.getTimeDiscover("3"));
        assertEquals(5, visitBFS.getTimeVisit("3"));
        assertEquals(6, visitBFS.getTimeDiscover("5"));
        assertEquals(7, visitBFS.getTimeVisit("5"));
        assertEquals(8, visitBFS.getTimeDiscover("4"));
        assertEquals(9, visitBFS.getTimeVisit("4"));
        assertEquals(10, visitBFS.getTimeDiscover("6"));
        assertEquals(11, visitBFS.getTimeVisit("6"));
        assertFalse(visitBFS.isDiscovered("7"));
        assertFalse(visitBFS.isDiscovered("8"));
    }

    @Test
    public void iterable() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 6);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);
        graph.addEdge("8", "7", 8);

        Set<String> vertices = new HashSet<>();
        for (String vertex : graph)
            vertices.add(vertex);
        shouldContain(vertices, "1", "2", "3", "4", "5", "6", "7", "8");

        vertices.clear();
        graph.forEach(vertices::add);
        shouldContain(vertices, "1", "2", "3", "4", "5", "6", "7", "8");

        vertices.clear();
        Iterator<String> iter = graph.iterator();
        while (iter.hasNext())
            vertices.add(iter.next());

        shouldContain(vertices, "1", "2", "3", "4", "5", "6", "7", "8");
    }

    @Test
    public void scc() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  ->  6
         *               ^
         * |      |      |
         * v      v
         * 3  <-> 5  ->  4
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("2", "6", 5);
        graph.addEdge("3", "5", 2);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "3", 9);
        graph.addEdge("5", "4", 5);

        shouldContain(graph.stronglyConnectedComponents(), new HashSet<>(Collections.singletonList("6")), new HashSet<>(Arrays.asList("3", "5")), new HashSet<>(Collections.singletonList("4")), new HashSet<>(Collections.singletonList("1")), new HashSet<>(Collections.singletonList("2")));

        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */
        before();
        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 6);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);
        graph.addEdge("8", "7", 8);

        shouldContain(graph.stronglyConnectedComponents(), new HashSet<>(Arrays.asList("7", "8")), new HashSet<>(Arrays.asList("2", "5", "4", "6")), new HashSet<>(Collections.singletonList("3")), new HashSet<>(Collections.singletonList("1")));
    }

    @Test
    public void cyclic() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  ->  6
         *               ^
         * |      |      |
         * v      v
         * 3  ->  5  ->  4
         */

        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");

        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("2", "6", 5);
        graph.addEdge("3", "5", 2);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "4", 5);

        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());

        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6
         *               ^
         * |      |      |
         * v      v
         * 3  <-  5  ->  4
         */
        before();
        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");

        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());


        graph.addEdge("1", "2", 1);
        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());
        graph.addEdge("1", "3", 1);
        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());
        graph.addEdge("2", "5", 4);
        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());
        graph.addEdge("4", "6", 5);
        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());
        graph.addEdge("5", "3", 6);
        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());
        graph.addEdge("5", "4", 3);
        assertFalse(graph.isCyclic());
        assertTrue(graph.isDAG());
        graph.addEdge("6", "2", 2);
        assertTrue(graph.isCyclic());
        assertFalse(graph.isDAG());
    }

    @Test
    public void transpose() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 6);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);

        Graph<String, Integer> transposed = graph.transpose();

        DFS<String, Integer> dfs = new DFS<>();
        VisitInfo<String> visitDFS = transposed.visit("6", dfs, null);
        assertEquals(0, visitDFS.getTimeDiscover("6"));
        assertEquals(1, visitDFS.getTimeDiscover("4"));
        assertEquals(2, visitDFS.getTimeDiscover("5"));
        assertEquals(3, visitDFS.getTimeDiscover("2"));
        assertEquals(4, visitDFS.getTimeDiscover("1"));
        assertEquals(5, visitDFS.getTimeVisit("1"));
        assertEquals(6, visitDFS.getTimeVisit("2"));
        assertEquals(7, visitDFS.getTimeVisit("5"));
        assertEquals(8, visitDFS.getTimeVisit("4"));
        assertEquals(9, visitDFS.getTimeVisit("6"));
        assertFalse(visitDFS.isDiscovered("3"));
        assertFalse(visitDFS.isDiscovered("7"));
        assertFalse(visitDFS.isDiscovered("8"));

        visitDFS = transposed.visit("8", dfs, null);
        assertEquals(0, visitDFS.getTimeDiscover("8"));
        assertEquals(1, visitDFS.getTimeDiscover("7"));
        assertEquals(2, visitDFS.getTimeVisit("7"));
        assertEquals(3, visitDFS.getTimeVisit("8"));
        assertFalse(visitDFS.isDiscovered("1"));
        assertFalse(visitDFS.isDiscovered("2"));
        assertFalse(visitDFS.isDiscovered("3"));
        assertFalse(visitDFS.isDiscovered("4"));
        assertFalse(visitDFS.isDiscovered("5"));
        assertFalse(visitDFS.isDiscovered("6"));
    }

    @Test
    public void topologicalSort() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  ->  6
         *               ^
         * |      |      |
         * v      v
         * 3  ->  5  ->  4
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("2", "6", 5);
        graph.addEdge("3", "5", 2);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "4", 5);

        shouldContainInOrder(graph.topologicalSort(), "1", "3", "2", "5", "4", "6");

    }

    @Test
    public void distanceVV() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 10);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 3);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);

        List<Edge<String, Integer>> distance = graph.distance("1", "6");
        int sum = distance.stream().mapToInt(Edge::getWeight).sum();
        assertEquals(13, sum);
        shouldContainInOrder(distance,
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 3),
                new Edge<>("4", "6", 5));
        distance = graph.distance("1", "3");
        sum = distance.stream().mapToInt(Edge::getWeight).sum();
        assertEquals(8, sum);
        shouldContainInOrder(distance,
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "3", 3));

        shouldContainInOrder(graph.distance("7", "8"), new Edge<>("7", "8", 8));

        shouldThrow(nullException, () -> graph.distance(null, "1"));
        shouldThrow(nullException, () -> graph.distance(null, null));
        shouldThrow(nullException, () -> graph.distance("1", null));
        shouldThrow(notException, () -> graph.distance("34", "1"));
        shouldThrow(notException, () -> graph.distance("2", "36"));
        shouldThrow(notException, () -> graph.distance("689", "374"));
        shouldThrow(new UnsupportedOperationException(Graph.NOT_CONNECTED), () -> graph.distance("1", "7"));
        shouldThrow(new UnsupportedOperationException(Graph.NOT_CONNECTED), () -> graph.distance("3", "2"));
    }

    @Test
    public void distanceVtoAll() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4  ->  8
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 10);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("4", "8", 2);
        graph.addEdge("5", "3", 3);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);

        Map<String, List<Edge<String, Integer>>> distance = graph.distance("1");
        assertNull(distance.get("1"));
        shouldContainInOrder(distance.get("2"),
                new Edge<>("1", "2", 1));
        shouldContainInOrder(distance.get("3"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "3", 3));
        shouldContain(distance.get("4"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 3));
        shouldContain(distance.get("5"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4));
        shouldContain(distance.get("6"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 3),
                new Edge<>("4", "6", 5));
        assertNull(distance.get("7"));
        shouldContain(distance.get("8"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 3),
                new Edge<>("4", "8", 2));
    }

    @Test
    public void marker() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 6);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);
        graph.addEdge("8", "7", 8);

        shouldThrow(nullException, () -> graph.mark(null, null));
        shouldThrow(nullException, () -> graph.mark("1", null));
        shouldThrow(nullException, () -> graph.mark(null, "yellow"));
        shouldThrow(nullException, () -> graph.unMark(null));
        shouldThrow(nullException, () -> graph.getMarks(null));
        shouldThrow(nullException, () -> graph.unMark(null, null));
        shouldThrow(nullException, () -> graph.unMark("1", null));
        shouldThrow(nullException, () -> graph.unMark(null, "blue"));
        shouldThrow(nullException, () -> graph.unMarkAll(null));
        shouldThrow(nullException, () -> graph.getMarkedWith(null));

        shouldThrow(notException, () -> graph.mark("324", "yellow"));
        shouldThrow(notException, () -> graph.unMark("32423"));
        shouldThrow(notException, () -> graph.getMarks("hw7389"));

        shouldContain(graph.getMarks("1"));
        graph.mark("1", "red");
        shouldContain(graph.getMarks("1"), "red");
        graph.mark("1", "yellow");
        graph.mark("1", "blue");
        shouldContain(graph.getMarks("1"), "red", "yellow", "blue");
        graph.mark("1", "red");
        shouldContain(graph.getMarks("1"), "red", "yellow", "blue");

        shouldContain(graph.getMarks("2"));
        graph.mark("2", "red");
        shouldContain(graph.getMarks("8"));
        graph.mark("8", "blue");
        shouldContain(graph.getMarks("2"), "red");
        shouldContain(graph.getMarks("8"), "blue");

        graph.unMark("2");
        shouldContain(graph.getMarks("2"));
        graph.unMark("1");
        shouldContain(graph.getMarks("1"));

        graph.mark("2", "red");
        graph.mark("2", "blue");
        shouldContain(graph.getMarks("2"), "red", "blue");
        graph.mark("4", "green");
        shouldContain(graph.getMarks("4"), "green");
        graph.mark("5", "green");
        shouldContain(graph.getMarks("5"), "green");

        graph.unMarkAll();
        shouldContain(graph.getMarks("1"));
        shouldContain(graph.getMarks("2"));
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarks("4"));
        shouldContain(graph.getMarks("5"));
        shouldContain(graph.getMarks("6"));
        shouldContain(graph.getMarks("7"));
        shouldContain(graph.getMarks("8"));

        graph.mark("1", "mark");
        graph.mark("2", "mark");
        graph.mark("3", "mark2");
        graph.mark("1", "mark2");
        graph.mark("1", 3);
        shouldContain(graph.getMarks("1"), "mark", "mark2", 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"), "mark2");
        shouldContain(graph.getMarkedWith("mark"), "2", "1");
        shouldContain(graph.getMarkedWith("mark2"), "1", "3");
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMark("1", "mark");
        shouldContain(graph.getMarks("1"), "mark2", 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"), "mark2");
        shouldContain(graph.getMarkedWith("mark"), "2");
        shouldContain(graph.getMarkedWith("mark2"), "1", "3");
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMarkAll("mark2");
        shouldContain(graph.getMarks("1"), 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"), "2");
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3), "1");
        
        graph.unMark("1", "mark");
        graph.unMark("2", "mark2");
        shouldContain(graph.getMarks("1"), 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"), "2");
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMark("2", "mark");
        shouldContain(graph.getMarks("1"), 3);
        shouldContain(graph.getMarks("2"));
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"));
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3), "1");
        
        graph.unMarkAll(3);
        shouldContain(graph.getMarks("1"));
        shouldContain(graph.getMarks("2"));
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"));
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3));
    }

    @Test
    public void subGraph() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v
         * 3  <-  5  ->  4      8
         */

        graph.addVertexIfAbsent("1");
        graph.addVertexIfAbsent("2");
        graph.addVertexIfAbsent("3");
        graph.addVertexIfAbsent("4");
        graph.addVertexIfAbsent("5");
        graph.addVertexIfAbsent("6");
        graph.addVertexIfAbsent("7");
        graph.addVertexIfAbsent("8");

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "3", 2);
        graph.addEdge("5", "4", 5);
        graph.addEdge("6", "2", 2);
        graph.addEdge("8", "7", 9);

        graph.mark("1", "blue");
        graph.mark("3", "blue");
        graph.mark("5", "blue");

        graph.mark("2", "even");
        graph.mark("4", "even");
        graph.mark("6", "even");

        graph.mark("2", "circle");
        graph.mark("4", "circle");
        graph.mark("5", "circle");
        graph.mark("6", "circle");

        graph.mark("1", "z");
        graph.mark("2", "z");
        graph.mark("5", "z");
        graph.mark("4", "z");

        Graph<String, Integer> sub = graph.subGraph("1", -541);
        shouldContain(sub.vertices(), "1");
        shouldContain(sub.edges());

        sub = graph.subGraph("1", 0);
        shouldContain(sub.vertices(), "1");
        shouldContain(sub.edges());

        sub = graph.subGraph("1", 1);
        shouldContain(sub.vertices(), "1", "2", "3");
        shouldContain(sub.edges(),
                new Edge<>("1", "2", 1),
                new Edge<>("1", "3", 1));

        sub = graph.subGraph("1", 3);
        shouldContain(sub.vertices(), "1", "2", "3", "5", "4");
        shouldContain(sub.edges(),
                new Edge<>("1", "2", 1),
                new Edge<>("1", "3", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "3", 2),
                new Edge<>("5", "4", 5));

        sub = graph.subGraph("6", 2);
        shouldContain(sub.vertices(), "6", "2", "5");
        shouldContain(sub.edges(),
                new Edge<>("2", "5", 4),
                new Edge<>("6", "2", 2));

        sub = graph.subGraph("1", 77689);
        shouldContain(sub.vertices(), "1", "2", "3", "5", "4", "6");
        shouldContain(sub.edges(),
                new Edge<>("1", "2", 1),
                new Edge<>("1", "3", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("4", "6", 6),
                new Edge<>("5", "3", 2),
                new Edge<>("5", "4", 5),
                new Edge<>("6", "2", 2));

        /* MARKED */
        sub = graph.subGraph("z");
        shouldContain(sub.vertices(), "1", "2", "4", "5");
        shouldContain(sub.edges(),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 5));

        sub = graph.subGraph("circle");
        shouldContain(sub.vertices(), "2", "4", "5", "6");
        shouldContain(sub.edges(),
                new Edge<>("2", "5", 4),
                new Edge<>("4", "6", 6),
                new Edge<>("5", "4", 5),
                new Edge<>("6", "2", 2));

        sub = graph.subGraph("blue");
        shouldContain(sub.vertices(), "1", "3", "5");
        shouldContain(sub.edges(),
                new Edge<>("1", "3", 1),
                new Edge<>("5", "3", 2));

        sub = graph.subGraph("even");
        shouldContain(sub.vertices(), "2", "4", "6");
        shouldContain(sub.edges(),
                new Edge<>("4", "6", 6),
                new Edge<>("6", "2", 2));
        
        sub = graph.subGraph("blue", "circle");
        shouldContain(sub.vertices(), "1", "2", "3", "4", "5", "6");
        shouldContain(sub.edges(),
                new Edge<>("1", "2", 1),
                new Edge<>("1", "3", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("4", "6", 6),
                new Edge<>("5", "3", 2),
                new Edge<>("5", "4", 5),
                new Edge<>("6", "2", 2));
        
        sub = graph.subGraph();
        shouldContain(sub.vertices(), "7", "8");
        shouldContain(sub.edges(), new Edge<>("8", "7", 9));
        
        sub = graph.subGraph(null);
        shouldContain(sub.vertices(), "7", "8");
        shouldContain(sub.edges(), new Edge<>("8", "7", 9));
    }

    @Test
    public void vertexClass() {
        Vertex<String> vertex = new Vertex<>(graph, "stronzo");

        assertEquals("stronzo", vertex.getValue());
        assertEquals(0, graph.numberOfVertices());

        shouldThrow(unsuppException, () -> vertex.addChild(null, null));
        shouldThrow(unsuppException, () -> vertex.mark(null));
        shouldThrow(unsuppException, () -> vertex.removeChild(null));
        shouldThrow(unsuppException, () -> vertex.visit(null, null));
        shouldThrow(unsuppException, vertex::unMark);
        shouldThrow(unsuppException, vertex::getAncestors);
        shouldThrow(unsuppException, vertex::getChildren);
        shouldThrow(unsuppException, vertex::getEdgesOut);
        shouldThrow(unsuppException, vertex::getEdgesIn);
        shouldThrow(unsuppException, vertex::getChildrenAsVertex);
        shouldThrow(unsuppException, vertex::getAncestorsAsVertex);
        shouldThrow(unsuppException, vertex::getMarks);

        vertex.addIfAbsent();
        assertEquals(1, graph.numberOfVertices());
        vertex.addIfAbsent();
        assertEquals(1, graph.numberOfVertices());
        vertex.addIfAbsent();
        assertEquals(1, graph.numberOfVertices());

        assertEquals(vertex, graph.getVertex("stronzo"));
        shouldThrow(nullException, () -> graph.getVertex(null));
        shouldThrow(notException, () -> graph.getVertex("stronzo1"));

        shouldThrow(nullException, () -> vertex.addChild(null, 3));
        shouldThrow(nullException, () -> vertex.addChild(null, null));
        shouldThrow(nullException, () -> vertex.mark(null));
        shouldThrow(nullException, () -> vertex.unMark(null));
        shouldThrow(nullException, () -> vertex.removeChild(null));
        shouldThrow(new NullPointerException(), () -> vertex.visit(null, null));

        shouldThrow(notException, () -> vertex.addChild("1", null));
        shouldThrow(notException, () -> vertex.addChild("ssdsad", 2));
        shouldThrow(notException, () -> vertex.removeChild("234"));

        shouldContain(vertex.getMarks());
        shouldContain(vertex.getAncestors());
        shouldContain(vertex.getChildren());
        shouldContain(vertex.getChildrenAsVertex());
        shouldContain(vertex.getEdgesIn());
        shouldContain(vertex.getEdgesOut());

        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");

        graph.addEdge("1", "2", 2);
        graph.addEdge("3", "stronzo", 6);
        graph.addEdge("stronzo", "2", 1);
        graph.addEdge("stronzo", "1", 3);

        shouldContain(vertex.getMarks());
        shouldContain(vertex.getAncestors(), "3");
        shouldContain(vertex.getChildren(), "1", "2");
        shouldContain(vertex.getChildrenAsVertex(), new Vertex<>(graph, "1"), new Vertex<>(graph, "2"));
        shouldContain(vertex.getAncestorsAsVertex(), new Vertex<>(graph, "3"));
        shouldContain(vertex.getEdgesIn(),
                new Edge<>("3", "stronzo", 6));
        shouldContain(graph.getEdgesIn(vertex.getValue()),
                new Edge<>("3", "stronzo", 6));
        shouldContain(vertex.getEdgesOut(),
                new Edge<>("stronzo", "1", 3),
                new Edge<>("stronzo", "2", 1));
        shouldContain(graph.getEdgesOut(vertex.getValue()),
                new Edge<>("stronzo", "1", 3),
                new Edge<>("stronzo", "2", 1));

        vertex.mark("ciao");
        vertex.mark("ciao2");
        shouldContain(vertex.getMarks(), "ciao", "ciao2");
        shouldContain(graph.getMarks(vertex.getValue()), "ciao", "ciao2");
        vertex.unMark();
        shouldContain(vertex.getMarks());
        vertex.mark("cio");
        vertex.mark(1);
        shouldContain(vertex.getMarks(), "cio", 1);
        vertex.unMark(1);
        shouldContain(vertex.getMarks(), "cio");
        vertex.unMark("cio");
        shouldContain(vertex.getMarks());

        vertex.removeChild("1");
        shouldContain(vertex.getChildren(), "2");
        vertex.addChild("3", 23);
        shouldContain(vertex.getChildren(), "2", "3");
        shouldContain(vertex.getAncestors(), "3");
        shouldContain(vertex.getEdgesOut(), new Edge<>("stronzo", "3", 23), new Edge<>("stronzo", "2", 1));
        shouldContain(graph.getEdgesOut(vertex.getValue()), new Edge<>("stronzo", "3", 23), new Edge<>("stronzo", "2", 1));
        shouldContain(vertex.getEdgesIn(), new Edge<>("3", "stronzo", 6));
        shouldContain(graph.getEdgesIn(vertex.getValue()), new Edge<>("3", "stronzo", 6));

        assertTrue(vertex.isStillContained());
        vertex.remove();
        assertFalse(vertex.isStillContained());
        assertFalse(graph.contains(vertex.getValue()));
        assertEquals(3, graph.numberOfVertices());

        shouldThrow(unsuppException, () -> vertex.addChild(null, null));
        shouldThrow(unsuppException, () -> vertex.mark(null));
        shouldThrow(unsuppException, () -> vertex.removeChild(null));
        shouldThrow(unsuppException, () -> vertex.visit(null, null));
        shouldThrow(unsuppException, vertex::unMark);
        shouldThrow(unsuppException, vertex::getAncestors);
        shouldThrow(unsuppException, vertex::getChildren);
        shouldThrow(unsuppException, vertex::getEdgesOut);
        shouldThrow(unsuppException, vertex::getEdgesIn);
        shouldThrow(unsuppException, vertex::getChildrenAsVertex);
        shouldThrow(unsuppException, vertex::getAncestorsAsVertex);
        shouldThrow(unsuppException, vertex::getMarks);

        vertex.addIfAbsent();
        assertEquals(4, graph.numberOfVertices());
    }

    @Test
    public void saveLoad() {
    	/*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v
         * 3  <-  5  ->  4      8
         */

    	String fileName = "test/resources/test.json";
    	Set<String> vertices = new HashSet<>();
    	Set<Edge<String, Integer>> edges = new HashSet<>();
    	Map<String, Set<Object>> marks = new HashMap<>();
    	Set<Object> temp = new HashSet<>();
    	
    	vertices.add("1");
    	vertices.add("2");
    	vertices.add("3");
    	vertices.add("4");
    	vertices.add("5");
    	vertices.add("6");
    	vertices.add("7");
    	vertices.add("8");

    	edges.add(new Edge<>("1", "2", 1));
    	edges.add(new Edge<>("1", "3", 1));
    	edges.add(new Edge<>("2", "5", 4));
    	edges.add(new Edge<>("4", "6", 6));
    	edges.add(new Edge<>("5", "3", 2));
    	edges.add(new Edge<>("5", "4", 5));
    	edges.add(new Edge<>("6", "2", 2));
    	edges.add(new Edge<>("8", "7", 9));
    	
    	temp.add(1);
    	marks.put("2", new HashSet<>(temp));
    	temp.add("blue");
    	marks.put("1", new HashSet<>(temp));
    	marks.put("7", new HashSet<>(temp));
    	temp.remove(1);
    	temp.add(4.0);
    	marks.put("4", new HashSet<>(temp));
    	marks.put("8", new HashSet<>(temp));
    	temp.remove(4.0);
    	temp.add("red");
    	marks.put("5", new HashSet<>(temp));
    	temp.remove("blue");
    	marks.put("6", new HashSet<>(temp));
    	temp.clear();
    	
        graph.addAllVertices(vertices);
        graph.addAllEdges(edges);
        marks.forEach((v, m) -> m.forEach(mk -> graph.mark(v, mk)));
        
        try {
			Graph.save(graph, fileName);
			Graph.load(graph, fileName, String.class, Integer.class);
			shouldContain(graph.vertices(), vertices.toArray());
			shouldContain(graph.edges(), edges.toArray());
			//marks.forEach((v, m) -> shouldContain(graph.getMarks(v), m.toArray()));
			
			graph.removeAllVertex();
			Graph.load(graph, fileName, String.class, Integer.class);
			shouldContain(graph.vertices(), vertices.toArray());
			shouldContain(graph.edges(), edges.toArray());
			//marks.forEach((v, m) -> shouldContain(graph.getMarks(v), m.toArray()));
		} catch (Exception e) {
			fail(e.getMessage());
		}
        

        try {
			Graph.load(graph, "sadadafacensi", String.class, Integer.class);
			fail("Should have been thrown IOException");
		} catch (Exception ignore) {
			if (!(ignore instanceof IOException))
				fail("Should have been thrown IOException " + ignore.getMessage());
		}
        
		shouldContain(graph.vertices(), vertices.toArray());
		shouldContain(graph.edges(), edges.toArray());
		//marks.forEach((v, m) -> shouldContain(graph.getMarks(v), m.toArray()));
        
        try {
			Graph.load(graph, fileName + ".fail", String.class, Integer.class);
			fail("Should have been thrown JsonSyntaxException");
		} catch (Exception ignore) {
			if (!(ignore instanceof JsonSyntaxException))
				fail("Should have been thrown JsonSyntaxException " + ignore.getMessage());
		}
        
		graph = null;
		shouldThrow(new NullPointerException(), () -> { try {
			Graph.load(graph, fileName, String.class, Integer.class);
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		} });
    }
    
    

    private void shouldContain(Collection<?> actual, Object... expected) {
        assertEquals("They have not the same number of elements\nActual: " + actual, expected.length, actual.size());

        for (Object obj : expected)
            assertTrue("Not containing: [" + obj + "]\nBut has: " + actual, actual.contains(obj));
    }

    private void shouldContainInOrder(List<?> actual, Object... expected) {
        assertEquals("They have not the same number of elements\nActual: " + actual, expected.length, actual.size());

        for (int i = 0; i < actual.size(); i++)
            assertEquals("Index: " + i, expected[i], actual.get(i));
    }

    private void shouldThrow(Exception expected, Runnable runnable) {
        try {
            runnable.run();
            fail("It has't thrown: " + expected.getClass().getSimpleName());
        } catch (Exception actual) {
            assertEquals(expected.getClass(), actual.getClass());
            assertEquals(expected.getMessage(), actual.getMessage());
        }
    }
}
