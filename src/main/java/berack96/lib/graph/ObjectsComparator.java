package berack96.lib.graph;
import java.util.Comparator;

/**
 * Compare two arbitrary objects.<br>
 * It uses the method hashCode that every object has to compare two objects.<br>
 * This is a simple use 
 */
public class ObjectsComparator implements Comparator<Object> {
    static public final ObjectsComparator instance = new ObjectsComparator();
    
    private ObjectsComparator(){};

    @Override
    public int compare(Object o1, Object o2) {
        return o1.hashCode() - o2.hashCode();
    }
}
