package berack96.lib.graph.struct;

import java.util.Collection;

/**
 * Basic interface for the UnionFind tree sets
 *
 * @param <X> the object
 * @author Berack96
 */
public interface UnionFind<X> {

    /**
     * Indicate how many different sets there are.
     *
     * @return the number of sets
     */
    int size();

    /**
     * It creates the single element set for every element in the collection
     *
     * @param elements the collection of the elements
     * @throws NullPointerException in the case of the set being null
     */
    void makeSetAll(Collection<X> elements) throws NullPointerException;

    /**
     * Creates the single element set for the element
     *
     * @param element the element to insert
     * @throws NullPointerException in the case of a null element
     */
    void makeSet(X element) throws NullPointerException;

    /**
     * Merge the tho elements into a single set.<br>
     * In the case that the two elements are in the same set it returns false.
     *
     * @param element1 an element of a set
     * @param element2 an element of another set
     * @return true in the case of a successful merge, false otherwise
     * @throws NullPointerException     in the case of a null element
     * @throws IllegalArgumentException in the case of an element not in the sets
     */
    boolean union(X element1, X element2) throws NullPointerException, IllegalArgumentException;

    /**
     * Returns the element representing the set in which the element passed resides.<br>
     * In case of an element not found then it's returned null
     *
     * @param element the element in the set
     * @return the representing element of the set found
     * @throws NullPointerException in the case of a null element
     */
    X find(X element) throws NullPointerException;
}
