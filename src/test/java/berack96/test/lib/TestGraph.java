package berack96.test.lib;

import berack96.lib.graph.*;
import berack96.lib.graph.impl.ListGraph;
import berack96.lib.graph.impl.MapGraph;
import berack96.lib.graph.impl.MatrixGraph;
import berack96.lib.graph.impl.MatrixUndGraph;
import berack96.lib.graph.models.GraphSaveStructure;
import berack96.lib.graph.struct.QuickFind;
import berack96.lib.graph.struct.UnionFind;
import berack96.lib.graph.visit.impl.BFS;
import berack96.lib.graph.visit.impl.DFS;
import berack96.lib.graph.visit.impl.VisitInfo;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10)
@SuppressWarnings("ConstantConditions,ConfusingArgumentToVarargsMethod")
public class TestGraph {
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    private final String encoding = "UTF-8";
    private final Exception nullException = new NullPointerException();
    private final Exception illegalException = new IllegalArgumentException();
    private final Exception notException = new IllegalArgumentException(Graph.VERTEX_NOT_CONTAINED);
    private final Exception unSuppException = new UnsupportedOperationException(Vertex.REMOVED);
    private final Exception notConnException = new UnsupportedOperationException(Graph.NOT_CONNECTED);

    //TODO tests for GraphUndirected minimum spanning forest
    public static Stream<GraphUndirected<String>> getGraphsUnDir() {
        return Stream.of(new MatrixUndGraph<>());
    }

    public static Stream<GraphDirected<String>> getGraphsDir() {
        return Stream.of(new MapGraph<>(), new MatrixGraph<>(), new ListGraph<>());
    }

    public static Stream<Graph<String>> getGraphs() {
        return Stream.concat(getGraphsDir(), getGraphsUnDir());
    }

    public static Stream<UnionFind<String>> getUnionFind() {
        return Stream.of(new QuickFind<>());
    }

    @BeforeEach
    public void before() {
        PrintStream p;
        try {
            p = new PrintStream(bytes, true, encoding);
            System.setErr(p);
            System.setOut(p);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    @AfterEach
    public void after() {
        try {
            String printed = bytes.toString(encoding);
            bytes.reset();
            if (!printed.isEmpty())
                fail("!!_____________!!_____________!!\nRemove the printed string:\n" + printed);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void preBasicVisit() {
        VisitInfo<Integer> info = new VisitInfo<>(0);
        assertTrue(info.isDiscovered(0));
        assertFalse(info.isVisited(0));
        assertEquals(0, info.getDepth(0));
        assertEquals(0, info.getTimeDiscover(0));
        assertEquals(Integer.valueOf(0), info.getSource());
        assertNull(info.getParentOf(0));

        assertFalse(info.isVisited(null));
        assertFalse(info.isDiscovered(null));

        shouldThrow(illegalException, () -> info.getTimeVisit(0));
        shouldThrow(illegalException, () -> info.getTimeDiscover(1));
        shouldThrow(illegalException, () -> info.getParentOf(2));
        shouldThrow(illegalException, () -> info.getDepth(2));

        shouldThrow(nullException, () -> info.getTimeDiscover(null));
        shouldThrow(nullException, () -> info.getTimeVisit(null));
        shouldThrow(nullException, () -> info.getParentOf(null));
        shouldThrow(nullException, () -> info.getDepth(null));
    }

    @Test
    public void preEdges() {
        Edge<String> edge = new Edge<>(null, null);
        assertEquals(edge.getWeight(), 1);
        assertNull(edge.getSource());
        assertNull(edge.getDestination());
        shouldContain(edge.getVertices());

        edge = new Edge<>("a", "b");
        assertEquals(edge.getWeight(), 1);
        assertEquals(edge.getSource(), "a");
        assertEquals(edge.getDestination(), "b");
        shouldContain(edge.getVertices(), "a", "b");

        edge = new Edge<>("23", "51", 0);
        assertEquals(edge.getWeight(), 0);
        assertEquals(edge.getSource(), "23");
        assertEquals(edge.getDestination(), "51");
        shouldContain(edge.getVertices(), "23", "51");

        edge = new Edge<>("JHfkOHFbeH", "SdGjhFdSaAv", -894656);
        assertEquals(edge.getWeight(), -894656);
        assertEquals(edge.getSource(), "JHfkOHFbeH");
        assertEquals(edge.getDestination(), "SdGjhFdSaAv");
        shouldContain(edge.getVertices(), "JHfkOHFbeH", "SdGjhFdSaAv");

        edge = new Edge<>("", "1", 546815);
        assertEquals(edge.getWeight(), 546815);
        assertEquals(edge.getSource(), "");
        assertEquals(edge.getDestination(), "1");
        shouldContain(edge.getVertices(), "", "1");
    }

    @ParameterizedTest
    @MethodSource("getUnionFind")
    public void preMSF(UnionFind<String> tree) {
        shouldThrow(nullException, () -> tree.find(null));
        shouldThrow(nullException, () -> tree.union(null, null));
        shouldThrow(nullException, () -> tree.union("2", null));
        shouldThrow(nullException, () -> tree.union(null, "5"));
        shouldThrow(illegalException, () -> tree.union("67", "24"));
        shouldThrow(nullException, () -> tree.makeSet(null));
        shouldThrow(nullException, () -> tree.makeSetAll(null));

        assertEquals(tree.size(), 0);
        assertNull(tree.find(""));
        assertNull(tree.find("0"));
        assertNull(tree.find("1"));
        assertNull(tree.find("2"));
        assertNull(tree.find("3"));

        tree.makeSetAll(List.of("1", "2", "3"));
        assertEquals(tree.size(), 3);
        assertNull(tree.find(""));
        assertNull(tree.find("0"));
        assertEquals(tree.find("1"), "1");
        assertEquals(tree.find("2"), "2");
        assertEquals(tree.find("3"), "3");

        tree.makeSet("0");
        assertEquals(tree.size(), 4);
        assertNull(tree.find(""));
        assertEquals(tree.find("0"), "0");
        assertEquals(tree.find("1"), "1");
        assertEquals(tree.find("2"), "2");
        assertEquals(tree.find("3"), "3");

        assertFalse(tree.union("0", "0"));
        assertFalse(tree.union("1", "1"));
        assertFalse(tree.union("2", "2"));
        assertFalse(tree.union("3", "3"));
        shouldThrow(illegalException, () -> tree.union("", ""));
        shouldThrow(illegalException, () -> tree.union("4", "4"));

        assertTrue(tree.union("0", "1"));
        assertEquals(tree.size(), 3);
        String find0 = tree.find("0");
        assertEquals(tree.find("0"), find0);
        assertEquals(tree.find("1"), find0);
        assertEquals(tree.find("2"), "2");
        assertEquals(tree.find("3"), "3");
        assertFalse(tree.union("0", "1"));
        assertFalse(tree.union("1", "0"));

        assertTrue(tree.union("2", "3"));
        assertEquals(tree.size(), 2);
        String find2 = tree.find("2");
        assertEquals(tree.find("0"), find0);
        assertEquals(tree.find("1"), find0);
        assertEquals(tree.find("2"), find2);
        assertEquals(tree.find("3"), find2);
        assertFalse(tree.union("2", "3"));
        assertFalse(tree.union("3", "2"));

        assertTrue(tree.union("3", "0"));
        assertEquals(tree.size(), 1);
        find2 = tree.find("2");
        assertEquals(tree.find("0"), find2);
        assertEquals(tree.find("1"), find2);
        assertEquals(tree.find("2"), find2);
        assertEquals(tree.find("3"), find2);
        assertFalse(tree.union("0", "3"));
        assertFalse(tree.union("3", "0"));
        assertFalse(tree.union("0", "2"));
        assertFalse(tree.union("2", "0"));
        assertFalse(tree.union("1", "3"));
        assertFalse(tree.union("3", "1"));
        assertFalse(tree.union("1", "2"));
        assertFalse(tree.union("2", "1"));
    }

    @ParameterizedTest
    @MethodSource("getGraphs")
    public void basicVertex(Graph<String> graph) {
        assertEquals(0, graph.size());

        graph.add("1");
        graph.add("2");
        shouldThrow(nullException, () -> graph.add(null));

        assertTrue(graph.contains("1"));
        assertFalse(graph.contains("0"));
        assertTrue(graph.contains("2"));
        assertFalse(graph.contains("3"));
        assertEquals(2, graph.size());

        graph.remove("1");
        assertFalse(graph.contains("1"));
        assertTrue(graph.contains("2"));
        assertEquals(1, graph.size());

        graph.add("3");
        assertTrue(graph.contains("3"));
        shouldThrow(nullException, () -> graph.contains(null));
        shouldThrow(nullException, () -> graph.addIfAbsent(null));

        assertTrue(graph.addIfAbsent("4"));
        assertFalse(graph.addIfAbsent("4"));
        assertFalse(graph.addIfAbsent("2"));

        assertEquals(3, graph.size());
        shouldContain(graph.vertices(), "2", "3", "4");

        graph.removeAll();
        shouldContain(graph.vertices());

        Collection<String> vertices = Set.of("1", "5", "24", "2", "3");
        graph.addAll(vertices);
        shouldContain(graph.vertices(), vertices.toArray());
        graph.remove("1");
        graph.remove("24");
        shouldContain(graph.vertices(), "5", "2", "3");
        graph.addAll(vertices);
        shouldContain(graph.vertices(), vertices.toArray());

        shouldThrow(nullException, () -> graph.addAll(null));
    }

    @ParameterizedTest
    @MethodSource("getGraphs")
    public void marker(Graph<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */

        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

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
        shouldContain(graph.marks());
        graph.mark("1", "red");
        shouldContain(graph.getMarks("1"), "red");
        shouldContain(graph.marks(), "red");
        graph.mark("1", "yellow");
        shouldContain(graph.marks(), "red", "yellow");
        graph.mark("1", "blue");
        shouldContain(graph.getMarks("1"), "red", "yellow", "blue");
        graph.mark("1", "red");
        shouldContain(graph.getMarks("1"), "red", "yellow", "blue");
        shouldContain(graph.marks(), "red", "yellow", "blue");

        shouldContain(graph.getMarks("2"));
        graph.mark("2", "red");
        shouldContain(graph.getMarks("8"));
        graph.mark("8", "blue");
        shouldContain(graph.getMarks("2"), "red");
        shouldContain(graph.getMarks("8"), "blue");
        shouldContain(graph.marks(), "red", "yellow", "blue");

        graph.unMark("1");
        shouldContain(graph.getMarks("1"));
        shouldContain(graph.marks(), "red", "blue");
        graph.unMark("2");
        shouldContain(graph.getMarks("2"));
        shouldContain(graph.marks(), "blue");

        graph.mark("2", "red");
        graph.mark("2", "blue");
        shouldContain(graph.getMarks("2"), "red", "blue");
        graph.mark("4", "green");
        shouldContain(graph.getMarks("4"), "green");
        graph.mark("5", "green");
        shouldContain(graph.getMarks("5"), "green");
        shouldContain(graph.marks(), "red", "blue", "green");

        graph.unMarkAll();
        shouldContain(graph.marks());
        shouldContain(graph.getMarks("1"));
        shouldContain(graph.getMarks("2"));
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarks("4"));
        shouldContain(graph.getMarks("5"));
        shouldContain(graph.getMarks("6"));
        shouldContain(graph.getMarks("7"));
        shouldContain(graph.getMarks("8"));

        graph.mark("1", "mark");
        shouldContain(graph.marks(), "mark");
        graph.mark("2", "mark");
        shouldContain(graph.marks(), "mark");
        graph.mark("3", "mark2");
        shouldContain(graph.marks(), "mark", "mark2");
        graph.mark("1", "mark2");
        shouldContain(graph.marks(), "mark", "mark2");
        graph.mark("1", 3);
        shouldContain(graph.marks(), "mark", "mark2", 3);
        shouldContain(graph.getMarks("1"), "mark", "mark2", 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"), "mark2");
        shouldContain(graph.getMarkedWith("mark"), "2", "1");
        shouldContain(graph.getMarkedWith("mark2"), "1", "3");
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMark("1", "mark");
        shouldContain(graph.marks(), "mark", "mark2", 3);
        shouldContain(graph.getMarks("1"), "mark2", 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"), "mark2");
        shouldContain(graph.getMarkedWith("mark"), "2");
        shouldContain(graph.getMarkedWith("mark2"), "1", "3");
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMarkAll("mark2");
        shouldContain(graph.marks(), "mark", 3);
        shouldContain(graph.getMarks("1"), 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"), "2");
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMark("1", "mark");
        graph.unMark("2", "mark2");
        shouldContain(graph.marks(), "mark", 3);
        shouldContain(graph.getMarks("1"), 3);
        shouldContain(graph.getMarks("2"), "mark");
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"), "2");
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMark("2", "mark");
        shouldContain(graph.marks(), 3);
        shouldContain(graph.getMarks("1"), 3);
        shouldContain(graph.getMarks("2"));
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"));
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3), "1");

        graph.unMarkAll(3);
        shouldContain(graph.marks());
        shouldContain(graph.getMarks("1"));
        shouldContain(graph.getMarks("2"));
        shouldContain(graph.getMarks("3"));
        shouldContain(graph.getMarkedWith("mark"));
        shouldContain(graph.getMarkedWith("mark2"));
        shouldContain(graph.getMarkedWith(3));
    }

    @ParameterizedTest
    @MethodSource("getGraphs")
    public void basicEdge(Graph<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2
         * |      |
         * v      v
         * 3  <-> 5  ->  4
         */
        graph.addAll(List.of("1", "2", "3", "4", "5"));

        shouldThrow(nullException, () -> graph.addEdge(null, "2", 1));
        shouldThrow(nullException, () -> graph.addEdge(null, null, 1));
        shouldThrow(nullException, () -> graph.addEdge("1", null, 1));
        shouldThrow(nullException, () -> graph.addEdge(null));
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

        shouldThrow(notException, () -> graph.addEdge("0", "2", 1));
        shouldThrow(notException, () -> graph.addEdge("2", "8", 1));
        shouldThrow(notException, () -> graph.addEdge("9", "6", 1));
        shouldThrow(notException, () -> graph.removeEdge("012", "2"));
        shouldThrow(notException, () -> graph.removeEdge("2", "28"));
        shouldThrow(notException, () -> graph.removeEdge("4329", "62"));
        shouldThrow(notException, () -> graph.removeAllEdge("0"));

        assertEquals(0, graph.numberOfEdges());

        assertEquals(0, graph.addEdge("1", "2", 1));
        assertEquals(0, graph.addEdge(new Edge<>("1", "3", 1)));
        assertEquals(0, graph.addEdge("2", "5", 4));
        assertEquals(0, graph.addEdge(new Edge<>("3", "5", 2)));
        assertEquals(0, graph.addEdge("5", "4", 3));

        assertEquals(5, graph.numberOfEdges());

        assertFalse(graph.containsEdge("01", "4"));
        assertFalse(graph.containsEdge("3", "8132"));
        assertFalse(graph.containsEdge("9423", "516"));

        // All this calls should do nothing
        graph.removeEdge("1", "5");
        graph.removeEdge("1", "4");
        graph.removeEdge("2", "3");
        graph.removeEdge("4", "3");
        graph.removeEdge("5", "1");
        graph.removeEdge("4", "1");

        assertEquals(5, graph.numberOfEdges());

        assertEquals(1, graph.getWeight("1", "2"));
        assertEquals(1, graph.getWeight("1", "3"));
        assertEquals(4, graph.getWeight("2", "5"));
        assertEquals(2, graph.getWeight("3", "5"));
        assertEquals(3, graph.getWeight("5", "4"));

        assertEquals(0, graph.getWeight("1", "4"));

        assertEquals(1, graph.addEdge("1", "2", 102));
        assertEquals(102, graph.addEdge("1", "2", 3));
        assertEquals(3, graph.addEdge("1", "2", 1));
        assertEquals(1, graph.addEdge(new Edge<>("1", "2", 102)));
        assertEquals(102, graph.addEdge(new Edge<>("1", "2", 3)));
        assertEquals(3, graph.addEdge(new Edge<>("1", "2", 1)));

        assertTrue(graph.containsEdge("1", "2"));
        assertEquals(1, graph.addEdge("1", "2", 0));
        assertFalse(graph.containsEdge("1", "2"));
        graph.addEdge("1", "2", 1);
        assertTrue(graph.containsEdge("5", "4"));
        assertEquals(3, graph.addEdge("5", "4", 0));
        assertFalse(graph.containsEdge("5", "4"));
        graph.addEdge("5", "4", 1);

        assertEquals(5, graph.numberOfEdges());
        assertTrue(graph.containsEdge("1", "2"));
        assertFalse(graph.containsEdge("4", "3"));
        assertFalse(graph.containsEdge("1", "4"));
        assertTrue(graph.containsEdge("1", "3"));
        assertTrue(graph.containsEdge("3", "5"));
        assertTrue(graph.containsEdge("2", "5"));

        graph.removeEdge("2", "5");
        assertFalse(graph.containsEdge("2", "5"));
        assertEquals(4, graph.numberOfEdges());

        graph.removeEdge("1", "2");
        assertFalse(graph.containsEdge("1", "2"));
        assertTrue(graph.containsEdge("1", "3"));
        assertEquals(3, graph.numberOfEdges());
        graph.addEdge("1", "2", 2);

        graph.removeAllEdge("3");
        assertFalse(graph.containsEdge("1", "3"));
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
        assertEquals(0, graph.addEdgeAndVertices("2", "323", 3));
        assertTrue(graph.containsEdge("2", "323"));
        assertFalse(graph.containsEdge("2aa", "323"));
        assertEquals(0, graph.addEdgeAndVertices("2aa", "323", 35));
        assertTrue(graph.containsEdge("2aa", "323"));
        assertFalse(graph.containsEdge("2bbb", "323bbb"));
        assertEquals(0, graph.addEdgeAndVertices("2bbb", "323bbb", 135));
        assertTrue(graph.containsEdge("2bbb", "323bbb"));

        assertFalse(graph.containsEdge("aff5", "444"));
        assertEquals(0, graph.addEdgeAndVertices("aff5", "444"));
        assertTrue(graph.containsEdge("aff5", "444"));
        assertFalse(graph.containsEdge("444", "455"));
        assertEquals(0, graph.addEdgeAndVertices("444", "455"));
        assertTrue(graph.containsEdge("444", "455"));
        assertFalse(graph.containsEdge("333", "455"));
        assertEquals(0, graph.addEdgeAndVertices("333", "455"));
        assertTrue(graph.containsEdge("333", "455"));

        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, "1", 1));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, null, 1));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices("2", null, 1));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, "1"));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null, null));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices("3", null));

        assertEquals(3, graph.addEdgeAndVertices("2", "323", 50));
        assertEquals(35, graph.addEdgeAndVertices("2aa", "323", 5));
        assertEquals(50, graph.addEdgeAndVertices("2", "323", 500));
        assertEquals(500, graph.addEdgeAndVertices("2", "323"));
        assertEquals(5, graph.addEdgeAndVertices("2aa", "323"));

        graph.removeAllEdge();

        assertFalse(graph.containsEdge("2", "323"));
        assertEquals(0, graph.addEdgeAndVertices(new Edge<>("2", "323", 3)));
        assertTrue(graph.containsEdge("2", "323"));
        assertFalse(graph.containsEdge("2aa", "323"));
        assertEquals(0, graph.addEdgeAndVertices(new Edge<>("2aa", "323", 35)));
        assertTrue(graph.containsEdge("2aa", "323"));
        assertFalse(graph.containsEdge("2bbb", "323bbb"));
        assertEquals(0, graph.addEdgeAndVertices(new Edge<>("2bbb", "323bbb", 135)));
        assertTrue(graph.containsEdge("2bbb", "323bbb"));

        shouldThrow(nullException, () -> graph.addEdgeAndVertices(new Edge<>(null, "1", 1)));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(new Edge<>(null, null, 1)));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(new Edge<>("2", null, 1)));
        shouldThrow(nullException, () -> graph.addEdgeAndVertices(null));

        assertEquals(3, graph.addEdgeAndVertices(new Edge<>("2", "323", 50)));
        assertEquals(35, graph.addEdgeAndVertices(new Edge<>("2aa", "323", 5)));
        assertEquals(50, graph.addEdgeAndVertices(new Edge<>("2", "323", 500)));

        graph.removeAll();
        graph.add("aaa");
        graph.add("1");
        graph.add("2");

        shouldContain(graph.vertices(), "1", "2", "aaa");
        shouldContain(graph.edges());
    }

    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void basicEdgeDir01(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2
         * |      |
         * v      v
         * 3  <-> 5  ->  4
         */
        graph.addAll(List.of("1", "2", "3", "4", "5"));

        shouldThrow(nullException, () -> graph.removeAllOutEdge(null));
        shouldThrow(nullException, () -> graph.removeAllInEdge(null));
        shouldThrow(notException, () -> graph.removeAllInEdge("011"));
        shouldThrow(notException, () -> graph.removeAllOutEdge("9"));

        graph.addEdge("1", "2");
        graph.addEdge("1", "3");
        graph.addEdge("2", "5");
        graph.addEdge("3", "5");
        graph.addEdge("5", "3");
        graph.addEdge("5", "4");

        graph.removeAllOutEdge("1");
        assertFalse(graph.containsEdge("1", "2"));
        assertFalse(graph.containsEdge("1", "3"));
        assertEquals(4, graph.numberOfEdges());
        graph.addEdge("1", "2");
        graph.addEdge("1", "3");
        assertEquals(6, graph.numberOfEdges());

        graph.removeAllInEdge("3");
        assertFalse(graph.containsEdge("5", "3"));
        assertFalse(graph.containsEdge("1", "3"));
        assertTrue(graph.containsEdge("3", "5"));
        assertEquals(4, graph.numberOfEdges());
        graph.addEdge("1", "3");
        graph.addEdge("5", "3");
        assertEquals(6, graph.numberOfEdges());

        graph.removeAllInEdge("5");
        assertFalse(graph.containsEdge("3", "5"));
        assertFalse(graph.containsEdge("2", "5"));
        assertTrue(graph.containsEdge("5", "3"));
        assertTrue(graph.containsEdge("5", "4"));
        assertEquals(4, graph.numberOfEdges());

        graph.removeAll();
        Set<Edge<String>> edges = new HashSet<>();
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

    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void basicEdgeDir02(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  ->  6
         *               ^
         * |      |      |
         * v      v
         * 3  <-> 5  ->  4
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6"));
        shouldContain(graph.edges());

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("2", "6", 5);
        graph.addEdge("3", "5", 2);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "3", 9);
        graph.addEdge("5", "4", 5);

        assertEquals(8, graph.numberOfEdges());

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

        /* Weird case in the add */
        graph.addIfAbsent("2");
        shouldContain(graph.edges(),
                new Edge<>("1", "2", 1),
                new Edge<>("1", "3", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("2", "6", 5),
                new Edge<>("3", "5", 2),
                new Edge<>("4", "6", 6),
                new Edge<>("5", "3", 9),
                new Edge<>("5", "4", 5));
        graph.add("2");
        shouldContain(graph.edges(),
                new Edge<>("1", "3", 1),
                new Edge<>("3", "5", 2),
                new Edge<>("4", "6", 6),
                new Edge<>("5", "3", 9),
                new Edge<>("5", "4", 5));
    }

    @ParameterizedTest
    @MethodSource("getGraphsUnDir")
    public void basicEdgeUnDir01(GraphUndirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1 - 2
         * |   |
         * 3 - 5 - 4
         */
        graph.addAll(List.of("1", "2", "3", "4", "5"));

        graph.addEdge("1", "2");
        graph.addEdge("1", "3");
        graph.addEdge("2", "5");
        graph.addEdge("3", "5");
        graph.addEdge("5", "4");
        assertEquals(5, graph.numberOfEdges());
        assertEquals(5, graph.size());

        assertTrue(graph.containsEdge("1", "2"));
        assertTrue(graph.containsEdge("1", "3"));
        assertTrue(graph.containsEdge("2", "1"));
        assertTrue(graph.containsEdge("3", "1"));
        graph.removeAllEdge("1");
        assertEquals(5, graph.size());
        assertEquals(3, graph.numberOfEdges());
        assertFalse(graph.containsEdge("1", "2"));
        assertFalse(graph.containsEdge("1", "3"));
        assertFalse(graph.containsEdge("2", "1"));
        assertFalse(graph.containsEdge("3", "1"));

        graph.addEdge("1", "2");
        graph.addEdge("1", "3");
        assertEquals(5, graph.size());
        assertEquals(5, graph.numberOfEdges());

        assertTrue(graph.containsEdge("3", "5"));
        assertTrue(graph.containsEdge("5", "3"));
        assertTrue(graph.containsEdge("1", "3"));
        assertTrue(graph.containsEdge("3", "1"));
        graph.removeAllEdge("3");
        assertEquals(5, graph.size());
        assertEquals(3, graph.numberOfEdges());
        assertFalse(graph.containsEdge("5", "3"));
        assertFalse(graph.containsEdge("3", "5"));
        assertFalse(graph.containsEdge("1", "3"));
        assertFalse(graph.containsEdge("3", "1"));
        graph.addEdge("1", "3");
        graph.addEdge("5", "3");
        assertEquals(5, graph.size());
        assertEquals(5, graph.numberOfEdges());

        assertTrue(graph.containsEdge("3", "5"));
        assertTrue(graph.containsEdge("5", "3"));
        assertTrue(graph.containsEdge("2", "5"));
        assertTrue(graph.containsEdge("5", "2"));
        assertTrue(graph.containsEdge("4", "5"));
        assertTrue(graph.containsEdge("5", "4"));
        graph.removeAllEdge("5");
        assertEquals(5, graph.size());
        assertEquals(2, graph.numberOfEdges());
        assertFalse(graph.containsEdge("3", "5"));
        assertFalse(graph.containsEdge("5", "3"));
        assertFalse(graph.containsEdge("2", "5"));
        assertFalse(graph.containsEdge("5", "2"));
        assertFalse(graph.containsEdge("4", "5"));
        assertFalse(graph.containsEdge("5", "4"));

        graph.removeAll();
        assertEquals(0, graph.size());
        assertEquals(0, graph.numberOfEdges());

        Set<Edge<String>> edges = new HashSet<>();
        edges.add(new Edge<>("aaa", "bbb", 3));
        edges.add(new Edge<>("bbb", "ccc", 4));
        edges.add(new Edge<>("ccc", "aaa", 5));
        edges.add(new Edge<>("1", "2", 2));
        graph.addAllEdges(edges);

        shouldContain(graph.vertices(), "1", "2", "aaa", "bbb", "ccc");
        shouldContainUnDir(graph.edges(),
                new Edge<>("aaa", "bbb", 3),
                new Edge<>("bbb", "ccc", 4),
                new Edge<>("ccc", "aaa", 5),
                new Edge<>("1", "2", 2));
    }

    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void basicVisitDir(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 6);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);
        graph.addEdge("8", "7", 8);

        shouldThrow(nullException, () -> graph.visit(null, new DFS<>(), null));
        shouldThrow(nullException, () -> graph.visit(null, null, null));
        shouldThrow(nullException, () -> graph.visit("1", null, null));

        shouldThrow(notException, () -> graph.visit("1010", new DFS<>(), null));

        DFS<String> dfs = new DFS<>();
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

        String[] vertices = {"1", "2", "5", "3", "3", "4", "6", "6", "4", "5", "2", "1"};
        boolean[] found = new boolean[graph.size()];
        integer.set(0);
        visitDFS.forEach(vertexInfo -> {
            int i = integer.get();
            assertEquals(vertices[i], vertexInfo.vertex, "Iter " + i);
            int vert = Integer.parseInt(vertexInfo.vertex);

            if (found[vert])
                assertEquals(i, vertexInfo.timeVisited, "Iter " + i);
            else {
                assertEquals(i, vertexInfo.timeDiscovered, "Iter " + i);
                found[vert] = true;
            }

            integer.incrementAndGet();
        });

        BFS<String> bfs = new BFS<>();
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

    @ParameterizedTest
    @MethodSource("getGraphsUnDir")
    public void basicVisitUnDir(GraphUndirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1 - 2   6   7
         * |   |   |   |
         * 3 - 5 - 4   8
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

        graph.addEdge("1", "2");
        graph.addEdge("1", "3");
        graph.addEdge("2", "5");
        graph.addEdge("3", "5");
        graph.addEdge("4", "6");
        graph.addEdge("5", "4");
        graph.addEdge("7", "8");

        shouldThrow(nullException, () -> graph.visit(null, new DFS<>(), null));
        shouldThrow(nullException, () -> graph.visit(null, null, null));
        shouldThrow(nullException, () -> graph.visit("1", null, null));
        shouldThrow(notException, () -> graph.visit("1010", new DFS<>(), null));

        DFS<String> dfs = new DFS<>();
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

        String[] vertices = {"1", "2", "5", "3", "3", "4", "6", "6", "4", "5", "2", "1"};
        boolean[] found = new boolean[graph.size()];
        integer.set(0);
        visitDFS.forEach(vertexInfo -> {
            int i = integer.get();
            assertEquals(vertices[i], vertexInfo.vertex, "Iter " + i);
            int vert = Integer.parseInt(vertexInfo.vertex);

            if (found[vert])
                assertEquals(i, vertexInfo.timeVisited, "Iter " + i);
            else {
                assertEquals(i, vertexInfo.timeDiscovered, "Iter " + i);
                found[vert] = true;
            }

            integer.incrementAndGet();
        });

        BFS<String> bfs = new BFS<>();
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

    @ParameterizedTest
    @MethodSource("getGraphs")
    public void iterable(Graph<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

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
        
        Iterator<String> iter = graph.iterator();
        assertNotNull(iter, "This should not be null!");
        while (iter.hasNext())
            vertices.add(iter.next());
        shouldContain(vertices, "1", "2", "3", "4", "5", "6", "7", "8");
        vertices.clear();

        for (String vertex : graph)
            vertices.add(vertex);
        shouldContain(vertices, "1", "2", "3", "4", "5", "6", "7", "8");

        vertices.clear();
        graph.forEach(vertices::add);
        shouldContain(vertices, "1", "2", "3", "4", "5", "6", "7", "8");
    }

    //TODO tests for GraphUndirected cc
    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void scc(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  ->  6
         *               ^
         * |      |      |
         * v      v
         * 3  <-> 5  ->  4
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6"));

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("2", "6", 5);
        graph.addEdge("3", "5", 2);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "3", 9);
        graph.addEdge("5", "4", 5);

        shouldContain(graph.stronglyConnectedComponents(), new HashSet<>(Collections.singletonList("6")), new HashSet<>(Arrays.asList("3", "5")), new HashSet<>(Collections.singletonList("4")), new HashSet<>(Collections.singletonList("1")), new HashSet<>(Collections.singletonList("2")));
        graph.removeAll();

        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */
        graph.addIfAbsent("1");
        graph.addIfAbsent("2");
        graph.addIfAbsent("3");
        graph.addIfAbsent("4");
        graph.addIfAbsent("5");
        graph.addIfAbsent("6");
        graph.addIfAbsent("7");
        graph.addIfAbsent("8");

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

    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void cyclic(GraphDirected<String> graph) {
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

        graph.addAll(List.of("1", "2", "3", "4", "5", "6"));

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
        graph.removeAll();

        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6
         *               ^
         * |      |      |
         * v      v
         * 3  <-  5  ->  4
         */
        graph.addIfAbsent("1");
        graph.addIfAbsent("2");
        graph.addIfAbsent("3");
        graph.addIfAbsent("4");
        graph.addIfAbsent("5");
        graph.addIfAbsent("6");

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

    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void transpose(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 6);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);

        Graph<String> transposed = graph.transpose();
        assertNotNull(transposed, "This should not be null!");

        DFS<String> dfs = new DFS<>();
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

    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void topologicalSort(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  ->  6
         *               ^
         * |      |      |
         * v      v
         * 3  ->  5  ->  4
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6"));

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 1);
        graph.addEdge("2", "5", 4);
        graph.addEdge("2", "6", 5);
        graph.addEdge("3", "5", 2);
        graph.addEdge("4", "6", 6);
        graph.addEdge("5", "4", 5);

        shouldContainInOneOrder(graph.topologicalSort(),
                new String[]{"1", "2", "3", "5", "4", "6"},
                new String[]{"1", "3", "2", "5", "4", "6"});
    }

    //TODO tests for GraphUndirected distanceVV
    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void distanceVV(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4      8
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 10);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("5", "3", 3);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);

        List<Edge<String>> distance = graph.distance("1", "6");
        assertNotNull(distance, "This should not be null!");
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
        shouldThrow(notConnException, () -> graph.distance("1", "7"));
        shouldThrow(notConnException, () -> graph.distance("3", "2"));
    }

    //TODO tests for GraphUndirected distanceVtoAll
    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void distanceVtoAll(GraphDirected<String> graph) {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^
         * |      |      |      |
         * v      v             v
         * 3  <-  5  ->  4  ->  8
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

        graph.addEdge("1", "2", 1);
        graph.addEdge("1", "3", 10);
        graph.addEdge("2", "5", 4);
        graph.addEdge("4", "6", 5);
        graph.addEdge("4", "8", 2);
        graph.addEdge("5", "3", 3);
        graph.addEdge("5", "4", 3);
        graph.addEdge("6", "2", 2);
        graph.addEdge("7", "8", 8);

        Map<String, List<Edge<String>>> distance = graph.distance("1");
        assertNotNull(distance, "This should not be null!");
        assertNull(distance.get("1"));
        shouldContainInOrder(distance.get("2"),
                new Edge<>("1", "2", 1));
        shouldContainInOrder(distance.get("3"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "3", 3));
        shouldContainInOrder(distance.get("4"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 3));
        shouldContainInOrder(distance.get("5"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4));
        shouldContainInOrder(distance.get("6"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 3),
                new Edge<>("4", "6", 5));
        assertNull(distance.get("7"));
        shouldContainInOrder(distance.get("8"),
                new Edge<>("1", "2", 1),
                new Edge<>("2", "5", 4),
                new Edge<>("5", "4", 3),
                new Edge<>("4", "8", 2));
    }

    //TODO tests for GraphUndirected subgraph
    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void subGraphDir(GraphDirected<String> graph) {
        /*
         * This graph should look like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v
         * 3  <-  5  ->  4      8
         */
        graph.addAll(List.of("1", "2", "3", "4", "5", "6", "7", "8"));

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

        Graph<String> sub = graph.subGraph("1", -541);
        assertNotNull(sub, "This should not be null!");
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

        sub = graph.subGraph("even", "even");
        shouldContain(sub.vertices(), "2", "4", "6");
        shouldContain(sub.edges(),
                new Edge<>("4", "6", 6),
                new Edge<>("6", "2", 2));
        
        sub = graph.subGraph("even", null, "even");
        shouldContain(sub.vertices(), "2", "4", "6");
        shouldContain(sub.edges(),
                new Edge<>("4", "6", 6),
                new Edge<>("6", "2", 2));

        sub = graph.subGraph((Object) null);
        shouldContain(sub.vertices(), "7", "8");
        shouldContain(sub.edges(), new Edge<>("8", "7", 9));
        
        sub = graph.subGraph();
        shouldContain(sub.vertices(), "7", "8");
        shouldContain(sub.edges(), new Edge<>("8", "7", 9));

        sub = graph.subGraph((Object[]) null);
        shouldContain(sub.vertices(), "7", "8");
        shouldContain(sub.edges(), new Edge<>("8", "7", 9));
    }

    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void vertexClass(GraphDirected<String> graph) {
        Vertex<String> vertex = new Vertex<>(graph, "stronzo");

        assertEquals("stronzo", vertex.get());
        assertEquals(0, graph.size());

        shouldThrow(unSuppException, () -> vertex.addChild(null));
        shouldThrow(unSuppException, () -> vertex.addChild(null, 3));
        shouldThrow(unSuppException, () -> vertex.mark(null));
        shouldThrow(unSuppException, () -> vertex.removeChild(null));
        shouldThrow(unSuppException, () -> vertex.getChildWeight(null));
        shouldThrow(unSuppException, () -> vertex.visit(null, null));
        shouldThrow(unSuppException, vertex::unMark);
        shouldThrow(unSuppException, vertex::getAncestors);
        shouldThrow(unSuppException, vertex::getChildren);
        shouldThrow(unSuppException, vertex::getChildrenAsVertex);
        shouldThrow(unSuppException, vertex::getAncestorsAsVertex);
        shouldThrow(unSuppException, vertex::getMarks);

        vertex.addIfAbsent();
        assertEquals(1, graph.size());
        vertex.addIfAbsent();
        assertEquals(1, graph.size());
        vertex.addIfAbsent();
        assertEquals(1, graph.size());

        assertEquals(vertex, graph.get("stronzo"));
        shouldThrow(nullException, () -> graph.get(null));
        shouldThrow(notException, () -> graph.get("stronzo1"));

        shouldThrow(nullException, () -> vertex.addChild(null));
        shouldThrow(nullException, () -> vertex.addChild(null, 3));
        shouldThrow(nullException, () -> vertex.mark(null));
        shouldThrow(nullException, () -> vertex.unMark(null));
        shouldThrow(nullException, () -> vertex.removeChild(null));
        shouldThrow(nullException, () -> vertex.getChildWeight(null));
        shouldThrow(nullException, () -> vertex.visit(null, null));

        shouldThrow(notException, () -> vertex.addChild("1"));
        shouldThrow(notException, () -> vertex.addChild("1", 3));
        shouldThrow(notException, () -> vertex.addChild("ssdsad", 2));
        shouldThrow(notException, () -> vertex.removeChild("234"));
        shouldThrow(notException, () -> vertex.getChildWeight("73"));

        shouldContain(vertex.getMarks());
        shouldContain(vertex.getAncestors());
        shouldContain(vertex.getChildren());
        shouldContain(vertex.getChildrenAsVertex());

        graph.addAll(List.of("1", "2", "3"));

        assertEquals(0, vertex.getChildWeight("1"));
        assertEquals(0, vertex.getChildWeight("2"));
        assertEquals(0, vertex.getChildWeight("3"));
        assertEquals(0, vertex.getChildWeight("stronzo"));

        graph.addEdge("1", "2", 2);
        graph.addEdge("3", "stronzo", 6);
        graph.addEdge("stronzo", "2", 1);
        graph.addEdge("stronzo", "1", 3);

        assertEquals(3, vertex.getChildWeight("1"));
        assertEquals(1, vertex.getChildWeight("2"));
        assertEquals(0, vertex.getChildWeight("3"));
        assertEquals(0, vertex.getChildWeight("stronzo"));

        shouldContain(vertex.getMarks());
        shouldContain(vertex.getAncestors(), "3");
        shouldContain(vertex.getChildren(), "1", "2");
        shouldContain(vertex.getChildrenAsVertex(), new Vertex<>(graph, "1"), new Vertex<>(graph, "2"));
        shouldContain(vertex.getAncestorsAsVertex(), new Vertex<>(graph, "3"));

        vertex.mark("ciao");
        vertex.mark("ciao2");
        shouldContain(vertex.getMarks(), "ciao", "ciao2");
        shouldContain(graph.getMarks(vertex.get()), "ciao", "ciao2");
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

        assertTrue(vertex.isStillContained());
        vertex.remove();
        assertFalse(vertex.isStillContained());
        assertFalse(graph.contains(vertex.get()));
        assertEquals(3, graph.size());

        shouldThrow(unSuppException, () -> vertex.addChild(null));
        shouldThrow(unSuppException, () -> vertex.addChild(null, 3));
        shouldThrow(unSuppException, () -> vertex.mark(null));
        shouldThrow(unSuppException, () -> vertex.removeChild(null));
        shouldThrow(unSuppException, () -> vertex.getChildWeight(null));
        shouldThrow(unSuppException, () -> vertex.visit(null, null));
        shouldThrow(unSuppException, vertex::unMark);
        shouldThrow(unSuppException, vertex::getAncestors);
        shouldThrow(unSuppException, vertex::getChildren);
        shouldThrow(unSuppException, vertex::getChildrenAsVertex);
        shouldThrow(unSuppException, vertex::getAncestorsAsVertex);
        shouldThrow(unSuppException, vertex::getMarks);

        vertex.addIfAbsent();
        assertEquals(4, graph.size());
    }

    //TODO tests for GraphUndirected save/load
    @ParameterizedTest
    @MethodSource("getGraphsDir")
    public void saveLoadDir(final GraphDirected<String> graph) throws URISyntaxException {
        /*
         * This graph should be like this
         *
         * 1  ->  2  <-  6      7
         *               ^      ^
         * |      |      |      |
         * v      v
         * 3  <-  5  ->  4      8
         */
        String fileName = ClassLoader.getSystemResource("").getPath() + "resources/test.json";
        Set<String> vertices = Set.of("1", "2", "3", "4", "5", "6", "7", "8");
        Set<Edge<String>> edges = new HashSet<>();
        Map<String, Set<Object>> marks = new HashMap<>();
        Set<Object> temp = new HashSet<>();

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

        graph.addAll(vertices);
        graph.addAllEdges(edges);
        marks.forEach((v, m) -> m.forEach(mk -> graph.mark(v, mk)));
        GraphSaveStructure<String> struct = new GraphSaveStructure<>();

        try {
            struct.save(graph, fileName);
            struct.load(graph, fileName, String.class);
            shouldContain(graph.vertices(), vertices.toArray());
            shouldContain(graph.edges(), edges.toArray());
            //marks.forEach((v, m) -> shouldContain(graph.getMarks(v), m.toArray()));

            graph.removeAll();
            struct.load(graph, fileName, String.class);
            shouldContain(graph.vertices(), vertices.toArray());
            shouldContain(graph.edges(), edges.toArray());
            //marks.forEach((v, m) -> shouldContain(graph.getMarks(v), m.toArray()));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.getMessage());
        }


        try {
            struct.load(graph, "sadadafacensi", String.class);
            fail("Should have been thrown IOException");
        } catch (Exception e) {
            if (!(e instanceof IOException))
                fail("Should have been thrown IOException " + e.getMessage());
        }

        shouldContain(graph.vertices(), vertices.toArray());
        shouldContain(graph.edges(), edges.toArray());
        //marks.forEach((v, m) -> shouldContain(graph.getMarks(v), m.toArray()));

        try {
            struct.load(graph, fileName + ".fail", String.class);
            fail("Should have been thrown JsonSyntaxException");
        } catch (Exception e) {
            if (!(e instanceof JsonSyntaxException))
                fail("Should have been thrown JsonSyntaxException " + e.getMessage());
        }

        shouldThrow(nullException, () -> {
            try {
                struct.load(null, fileName, String.class);
            } catch (IOException e) {
                fail();
                e.printStackTrace();
            }
        });
    }


    @SafeVarargs
    private <V> void shouldContainUnDir(Collection<Edge<V>> actual, Edge<V>... expected) {
        assertNotNull(actual, "You should pass me a collection!");
        assertEquals(expected.length, actual.size(), "They have not the same number of elements\nActual: " + actual);

        for (Edge<V> edge : expected) {
            Edge<V> found = null;
            for (Edge<V> edgeAc : actual) {
                Collection<V> vert = edgeAc.getVertices();
                if (vert.contains(edge.getSource()) && vert.contains(edge.getDestination()))
                    found = edgeAc;
            }

            if (found == null)
                fail("The undirected edge " + edge + " couldn't be found in " + actual);
            assertEquals(edge.getWeight(), found.getWeight());
        }
    }

    private void shouldContain(Collection<?> actual, Object... expected) {
        assertNotNull(actual, "You should pass me a collection!");
        assertEquals(expected.length, actual.size(), "They have not the same number of elements\nActual: " + actual);

        for (Object obj : expected)
            assertTrue(actual.contains(obj), "Not containing: [" + obj + "]\nBut has: " + actual);
    }

    private void shouldContainInOrder(List<?> actual, Object... expected) {
        shouldContainInOneOrder(actual, expected);
    }

    private void shouldContainInOneOrder(List<?> actual, Object[]... expected) {
        assertNotNull(actual, "You should pass me a list!");

        boolean ok = false;
        for (int j = 0; j < expected.length && !ok; j++) {
            Object[] probable = expected[j];
            assertEquals(probable.length, actual.size(), "They have not the same number of elements\nActual: " + actual);

            boolean check = false;
            for (int i = 0; i < actual.size() && !check; i++)
                check = Objects.equals(probable[i], actual.get(i));
            ok = check;
        }
        assertTrue(ok, "The list passed doesn't match any expected arrays\nList: " + actual);
    }

    private void shouldThrow(Exception expected, Runnable runnable) {
        try {
            runnable.run();
            fail("It hasn't thrown: " + expected.getClass().getSimpleName());
        } catch (Exception actual) {
            assertEquals(expected.getClass(), actual.getClass());
            if(expected.getMessage()!=null)
                assertEquals(expected.getMessage(), actual.getMessage());
        }
    }
}
