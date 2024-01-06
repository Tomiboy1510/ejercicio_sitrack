package moviedata;

/**
 * Implementa la interfaz {@link MovieData MovieData}, representa un elemento simple (nodo hoja)
 */
public class MovieDataLeaf implements MovieData {

    private final String name;
    private final String value;

    public MovieDataLeaf(String name, String value) {

        this.name = name;
        this.value = value;
    }

    public String toStringIndented(int depth) {
        String res = "";
        for (int i = 0; i < depth; ++i)
            res += "\t";
        res += name + ": " + value + "\n";
        return res;
    }

    public String getId() {

        if (name.equals("imdbID")) return value;
        return null;
    }

    public String toString() {
        return toStringIndented(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof MovieData)) return false;
        if (getId() == null) return false;
        if (((MovieData) obj).getId() == null) return false;
        return getId().equals(((MovieData) obj).getId());
    }
}
