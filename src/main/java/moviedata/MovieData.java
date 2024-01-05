package moviedata;

public interface MovieData {

    String toString();
    String getId();

    String toStringIndented(int depth);
    boolean equals(Object o); // Para remover duplicados al hacer una búsqueda
}
