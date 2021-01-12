package berack96.lib.graph.models;

/**
 * Support class used for saving a Graph in a file.
 *
 * @author Berack96
 */
public class EdgeSaveStructure {
    protected EdgeSaveStructure(String s, String d, int w) {
        this.src = s;
        this.dest = d;
        this.weight = w;
    }

    public String src;
    public String dest;
    public int weight;
}