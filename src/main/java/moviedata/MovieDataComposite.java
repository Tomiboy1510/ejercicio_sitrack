package moviedata;

import java.util.ArrayList;

/**
 * Implementa la interfaz {@link MovieData MovieData}, representa un elemento que tiene otros elementos anidados
 */
public class MovieDataComposite implements MovieData {

    private final String name;
    private final ArrayList<MovieData> children;

    public MovieDataComposite(String name) {
        children = new ArrayList<>();
        this.name = name;
    }

    /**
     * Añade un nodo a los niños de este
     * @param c
     */
    public void addChild(MovieData c) {
        children.add(c);
    }

    public String toStringIndented(int depth) {
        String res = "";
        for (int i = 0; i < depth; ++i)
            res += "\t";
        res += name + ":\n";
        for (MovieData v : children)
            res += v.toStringIndented(depth + 1);
        return res;
    }

    public String getId() {

        for (MovieData v : children) {
            String id = v.getId();
            if (id != null) return id;
        }
        return null;
    }

    @Override
    public String toString() {
        return toStringIndented(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof MovieData)) return false;
        return getId() == ((MovieData) obj).getId();
    }
}
