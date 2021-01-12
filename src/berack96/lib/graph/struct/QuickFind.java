package berack96.lib.graph.struct;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Simple implementation of the {@link UnionFind} interface with priority to the find function.
 *
 * @param <X> the elements to search and merge
 */
public class QuickFind<X> implements UnionFind<X> {
    Map<X, Collection<X>> struct = new HashMap<>();

    @Override
    public int size() {
        return struct.size();
    }

    @Override
    public void makeSetAll(Collection<X> elements) throws NullPointerException {
        Map<X, Collection<X>> temp = new HashMap<>(elements.size() + 1, 1);
        for (X elem : elements)
            temp.computeIfAbsent(elem, new AddElement());
        struct.putAll(temp);
    }

    @Override
    public void makeSet(X element) throws NullPointerException {
        if (element == null)
            throw new NullPointerException();
        struct.computeIfAbsent(element, new AddElement());
    }

    @Override
    public boolean union(X element1, X element2) throws NullPointerException, IllegalArgumentException {
        element1 = find(element1);
        element2 = find(element2);
        if (element1 == null || element2 == null)
            throw new IllegalArgumentException();
        if (element1 == element2)
            return false;

        return struct.get(element1).addAll(struct.remove(element2));
    }

    @Override
    public X find(X element) throws NullPointerException {
        if (element == null)
            throw new NullPointerException();
        if (struct.containsKey(element))
            return element;

        AtomicReference<X> toReturn = new AtomicReference<>(null);
        struct.forEach((key, collection) -> {
            if (collection.contains(element))
                toReturn.set(key);
        });
        return toReturn.get();
    }

    /**
     * Stupid class for implementing the adding of a new element
     */
    private class AddElement implements Function<X, Collection<X>> {
        @Override
        public Collection<X> apply(X x) {
            Collection<X> coll = new HashSet<>();
            coll.add(x);
            return coll;
        }
    }
}
