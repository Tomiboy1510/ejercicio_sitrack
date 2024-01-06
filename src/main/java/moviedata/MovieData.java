package moviedata;

/**
 * MovieData representa un objeto que guarda datos sobre un metraje que la consola muestra al usuario.
 * Los resultados de las queries están en Json, pero no todos los datos retornados tienen por qué
 * mostrarse al usuario. Entonces se construye un objeto MovieData que contiene los necesarios.
 *
 * Seguramente existe alguna clase similar para convertir Json a una representación de árbol, pero
 * como no la conozco seguramente reinventé la rueda.
 *
 * Esta interfaz es implementada por {@link MovieDataComposite MovieDataComposite} y
 * {@link MovieDataLeaf MovieDataLeaf}, constituyendo un patrón Composite.
 * MovieDataComposite tiene hijos de tipo MovieData, y MovieDataLeaf es un nodo hoja. Con estas clases
 * se puede componer un árbol cuyos nodos pueden ser manipulados uniformemente ya que implementan la
 * misma interfaz. (La única funcionalidad que ofrecen es convertirse a String para poder mostrarse al
 * usuario)
 *
 * En resumen, cada objeto MovieData representa un par nombre/valor donde el valor puede ser un String u otro
 * objeto MovieData.
 */
public interface MovieData {

    /**
     * Provee una representación de String del objeto, propagando a sus hijos la misma operación si los tiene
     * Este método sólo llama al método {@link #toStringIndented toStringIndented} con el parámetro 0
     * @return Un string donde se ven los datos de la película, indentados según su profundidad en el árbol
     */
    String toString();

    /**
     * Busca en el árbol recursivamente para encontrar el ID de imdb de la película (útil para hacer una nueva
     * query, obteniendo los detalles completos de una película)
     * @return el ID de imdb de la película cuyos datos están almacenados en el objeto
     */
    String getId();

    /**
     * Utiliza un parámetro de profundidad para indentar correctamente los datos anidados en otros datos
     *
     * Quisiera que Java admitiera métodos protected en interfaces, este método no debería ser usado por nadie
     * salvo las otras clases del paquete moviedata. Pero obligatoriamente debe estar incluido en la interfaz
     * ya que los hijos de MovieDataComposite son de tipo MovieData :(
     * @param depth Profundidad en el árbol, un nodo la aumenta en 1 para invocar el mismo método en sus hijos
     * @return Un string donde se ven los datos de la película, indentados según su profundidad en el árbol
     */
    String toStringIndented(int depth);

    /**
     * Sobreescribe {@link Object#equals equals}, devuelve verdadero si el ID imdb de ambos objetos es el mismo
     * Permite que se eliminen entradas duplicadas al guardarlas en un Set
     * @param o Objeto con el que comparar
     * @return true si ambos objetos son de tipo MovieData y contienen el mismo ID imdb
     */
    boolean equals(Object o);
}
