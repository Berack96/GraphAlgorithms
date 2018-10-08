package berack96.test.sim;

import berack96.sim.util.graph.Graph;
import berack96.sim.util.graph.visit.BFS;
import berack96.sim.util.graph.visit.DFS;
import berack96.sim.util.graph.visit.VisitStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestGraph {

    private Graph<String, Integer> graph;

    private final Exception nullException = new NullPointerException(Graph.PARAM_NULL);
    private final Exception notException = new IllegalArgumentException(Graph.VERTEX_NOT_CONTAINED);

    @Before
    public void before() {
        // Change here the instance for changing all the test for that particular class
        graph = null;
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
        shouldThrow(notException, () -> graph.containsEdge("01", "4"));
        shouldThrow(notException, () -> graph.containsEdge("3", "8132"));
        shouldThrow(notException, () -> graph.containsEdge("9423", "516"));
        shouldThrow(notException, () -> graph.removeEdge("012", "2"));
        shouldThrow(notException, () -> graph.removeEdge("2", "28"));
        shouldThrow(notException, () -> graph.removeEdge("4329", "62"));
        shouldThrow(notException, () -> graph.removeAllEdge("0"));
        shouldThrow(notException, () -> graph.removeAllInEdge("011"));
        shouldThrow(notException, () -> graph.removeAllOutEdge("9"));

        assertEquals(0, graph.numberOfEdges());

        assertNull(graph.addEdge("1", "2", 1));
        assertNull(graph.addEdge("1", "3", 1));
        assertNull(graph.addEdge("2", "5", 4));
        assertNull(graph.addEdge("3", "5", 2));
        assertNull(graph.addEdge("5", "3", 2));
        assertNull(graph.addEdge("5", "4", 3));

        assertEquals(6, graph.numberOfEdges());

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

        shouldThrow(notException, () -> graph.containsEdge("2", "323"));
        graph.addEdgeAndVertices("2", "323", 3);
        assertTrue(graph.containsEdge("2", "323"));
        shouldThrow(notException, () -> graph.containsEdge("2aa", "323"));
        graph.addEdgeAndVertices("2aa", "323", 3);
        assertTrue(graph.containsEdge("2aa", "323"));
        shouldThrow(notException, () -> graph.containsEdge("2bbb", "323bbb"));
        graph.addEdgeAndVertices("2bbb", "323bbb", 3);
        assertTrue(graph.containsEdge("2bbb", "323bbb"));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, "1", 1));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, null, 1));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices("2", null, 1));

        graph.removeAllVertex();
        graph.addVertex("aaa");
        graph.addVertex("1");
        graph.addVertex("2");

        shouldContain(graph.vertices(), "1", "2", "aaa");
        shouldContain(graph.edges());

        Set<Graph.Edge<String, Integer>> edges = new HashSet<>();
        edges.add(new Graph.Edge<>("aaa", "bbb", 3));
        edges.add(new Graph.Edge<>("bbb", "ccc", 4));
        edges.add(new Graph.Edge<>("ccc", "aaa", 5));
        edges.add(new Graph.Edge<>("1", "2", 2));
        graph.addAllEdges(edges);

        shouldContain(graph.vertices(), "1", "2", "aaa", "bbb", "ccc");
        shouldContain(graph.edges(),
                new Graph.Edge<>("aaa", "bbb", 3),
                new Graph.Edge<>("bbb", "ccc", 4),
                new Graph.Edge<>("ccc", "aaa", 5),
                new Graph.Edge<>("1", "2", 2));
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

        shouldContain(graph.getChildrenAndWeight("1").entrySet(), new AbstractMap.SimpleEntry<>("2", 1), new AbstractMap.SimpleEntry<>("3", 1));
        shouldContain(graph.getChildrenAndWeight("2").entrySet(), new AbstractMap.SimpleEntry<>("5", 4), new AbstractMap.SimpleEntry<>("6", 5));
        shouldContain(graph.getChildrenAndWeight("3").entrySet(), new AbstractMap.SimpleEntry<>("5", 2));
        shouldContain(graph.getChildrenAndWeight("4").entrySet(), new AbstractMap.SimpleEntry<>("6", 6));
        shouldContain(graph.getChildrenAndWeight("5").entrySet(), new AbstractMap.SimpleEntry<>("3", 9), new AbstractMap.SimpleEntry<>("4", 5));
        shouldContain(graph.getChildrenAndWeight("6").entrySet());

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
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("1", "3", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("2", "6", 5),
                new Graph.Edge<>("3", "5", 2),
                new Graph.Edge<>("4", "6", 6),
                new Graph.Edge<>("5", "3", 9),
                new Graph.Edge<>("5", "4", 5));
    }

    @Test
    public void preBasicVisit() {
        VisitStrategy.VisitInfo<Integer> info = new VisitStrategy.VisitInfo<>(0);
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
        graph.visit("1", dfs, null);
        VisitStrategy.VisitInfo<String> visitDFS = dfs.getLastVisit();
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

        BFS<String, Integer> bfs = new BFS<>();
        graph.visit("1", bfs, null);
        VisitStrategy.VisitInfo<String> visitBFS = bfs.getLastVisit();
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
        transposed.visit("6", dfs, null);
        VisitStrategy.VisitInfo<String> visitDFS = dfs.getLastVisit();
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

        transposed.visit("8", dfs, null);
        visitDFS = dfs.getLastVisit();
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

        List<Graph.Edge<String, Integer>> distance = graph.distance("1", "6");
        int sum = distance.stream().mapToInt(Graph.Edge::getWeight).sum();
        assertEquals(13, sum);
        shouldContainInOrder(distance,
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("5", "4", 3),
                new Graph.Edge<>("4", "6", 5));
        distance = graph.distance("1", "3");
        sum = distance.stream().mapToInt(Graph.Edge::getWeight).sum();
        assertEquals(8, sum);
        shouldContainInOrder(distance,
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("5", "3", 3));

        shouldContainInOrder(graph.distance("7", "8"), new Graph.Edge<>("7", "8", 8));

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

        Map<String, List<Graph.Edge<String, Integer>>> distance = graph.distance("1");
        assertNull(distance.get("1"));
        shouldContainInOrder(distance.get("2"),
                new Graph.Edge<>("1", "2", 1));
        shouldContainInOrder(distance.get("3"),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("5", "3", 3));
        shouldContain(distance.get("4"),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("5", "4", 3));
        shouldContain(distance.get("5"),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("2", "5", 4));
        shouldContain(distance.get("6"),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("5", "4", 3),
                new Graph.Edge<>("4", "6", 5));
        assertNull(distance.get("7"));
        shouldContain(distance.get("8"),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("5", "4", 3),
                new Graph.Edge<>("4", "8", 2));
    }

    @Test
    public void subGraph() {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6
         *               ^
         * |      |      |
         * v      v
         * 3  <-  5  ->  4
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
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "3", 2);
        graph.addEdge("5", "4", 5);
        graph.addEdge("6", "2", 2);

        Graph<String, Integer> sub = graph.subGraph("1", -541);
        shouldContain(sub.vertices(), "1");
        shouldContain(sub.edges());

        sub = graph.subGraph("1", 0);
        shouldContain(sub.vertices(), "1");
        shouldContain(sub.edges());

        sub = graph.subGraph("1", 1);
        shouldContain(sub.vertices(), "1", "2", "3");
        shouldContain(sub.edges(),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("1", "3", 1));

        sub = graph.subGraph("1", 3);
        shouldContain(sub.vertices(), "1", "2", "3", "5", "4");
        shouldContain(sub.edges(),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("1", "3", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("5", "3", 2),
                new Graph.Edge<>("5", "4", 5));

        sub = graph.subGraph("6", 2);
        shouldContain(sub.vertices(), "6", "2", "5");
        shouldContain(sub.edges(),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("6", "2", 2));

        sub = graph.subGraph("1", 77689);
        shouldContain(sub.vertices(), "1", "2", "3", "5", "4", "6");
        shouldContain(sub.edges(),
                new Graph.Edge<>("1", "2", 1),
                new Graph.Edge<>("1", "3", 1),
                new Graph.Edge<>("2", "5", 4),
                new Graph.Edge<>("4", "6", 6),
                new Graph.Edge<>("5", "3", 2),
                new Graph.Edge<>("5", "4", 5),
                new Graph.Edge<>("6", "2", 2));
    }

    // TODO test saveFile

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
