package utils;

public class StringUtils {

    // Me sorprende que no haya ya un método para esto en la clase String
    public static boolean isInteger(String s) {

        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
