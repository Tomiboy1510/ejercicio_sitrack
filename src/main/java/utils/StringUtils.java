package utils;

/**
 * Clase con utilidades varias relacionadas con String (hay una sola)
 */
public class StringUtils {

    // Me sorprende que no haya ya un método para esto en la clase String

    /**
     * Devuelve true si el String representa un número entero
     * @param s
     * @return true si el String pasado como parámetro puede ser parseado como un int
     */
    public static boolean isInteger(String s) {

        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
