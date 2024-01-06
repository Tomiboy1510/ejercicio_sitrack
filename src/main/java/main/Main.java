package main;

import moviedata.MovieData;
import omdb.OmdbClient;
import omdb.RequestParams;
import utils.StringUtils;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;

/**
 * Clase principal, se encarga de mostrar el menú donde se ve la sintaxis de uso del programa,
 * se parsean los comandos que el usuario ingresa en la consola, se construye un objeto con los
 * parámetros de la petición a la API y se llama al cliente de OMDB para que devuelva la información
 * de las películas encontradas. En resumen, cumple el rol de interfaz con el usuario.
 */
public class Main {

    private static final String baseUrl = "http://www.omdbapi.com/";
    private static final String apiKey = "e4837c5e";
    private static final List<String> tipos = Arrays.asList("series", "movie", "episode");

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String input;
        MovieData[] movies = new MovieData[]{};
        RequestParams params = new RequestParams();
        OmdbClient client = new OmdbClient(baseUrl, apiKey);

        printMenu();

        while (true) {

            // Obtener input
            System.out.print("> ");
            input = sc.nextLine();
            String[] tokens = input.split("\\s+");
            if (! validateInput(tokens)) {
                System.out.println("Entrada inválida");
                continue;
            }

            params.reset();

            // Si se ingresó un número, mostrar detalles de un metraje
            if (StringUtils.isInteger(tokens[0])) {
                try {
                    String id = movies[Integer.parseInt(tokens[0]) - 1].getId();
                    params.setId(id);
                    MovieData detalles = client.getById(params);
                    System.out.println(detalles.toString());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Entrada inválida!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }

            // Setear parámetros de la petición
            setParams(params, tokens);

            // Enviar petición
            try {
                movies = client.search(params);
            } catch (Exception e) {
                e.printStackTrace();
                movies = new MovieData[]{};
            }

            // Mostrar resultados
            System.out.println("Mostrando " + movies.length + " resultados:\n");
            for (int i = 0; i < movies.length; ++i)
                System.out.println((i+1)+ ". " + movies[i].toString());
            if (movies.length != 0)
                System.out.println("(Ingresar número para ver los detalles completos de un metraje)\n");
        }
    }

    /**
     * Muestra el menú principal, que está en un archivo de texto
     */
    private static void printMenu() {

        Scanner sc = null;
        try {
            sc = new Scanner(Main.class.getClassLoader().getResourceAsStream("menu.txt"));
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
            sc.close();
        } catch (NullPointerException e) {
            System.out.println("No se pudo abrir el archivo menu.txt");
        }
    }

    /**
     * Valida una línea (dividida en tokens) ingresada por el usuario para ver si constituye un comando válido
     * @param tokens Palabras que componen la línea ingresada
     * @return true si los tokens constituyen un comando válido, false si:
     *  - no hay tokens
     *  - hay uno solo pero no es un número entero
     *  - si hay más de 1 token pero el primero no es "buscar"
     *  - si luego de -t no viene un tipo válido
     *  - si luego de -y no viene un número entero
     *  - si luego de -r no vienen dos números enteros
     *  - si luego de -r vienen dos números enteros pero el primero es más grande que el segundo
     */
    private static boolean validateInput(String[] tokens) {

        // Hay mejores formas de hacer esto pero seguro que el objetivo del ejercicio no es programar un parser
        if (tokens.length == 0) return false;
        if (tokens.length == 1)
            return (StringUtils.isInteger(tokens[0]));
        if (! tokens[0].equalsIgnoreCase("buscar")) return false;
        int i = 2;
        while (i < tokens.length) {
            switch (tokens[i]) {
                case "-t":
                    if (i+1 >= tokens.length || ! tipos.contains(tokens[i+1])) return false;
                    i += 2;
                    break;
                case "-y":
                    if (i+1 >= tokens.length || !StringUtils.isInteger(tokens[i+1])) return false;
                    i += 2;
                    break;
                case "-r":
                    if (i+1 >= tokens.length || !StringUtils.isInteger(tokens[i+1])) return false;
                    if (i+2 >= tokens.length || !StringUtils.isInteger(tokens[i+2])) return false;
                    if (Integer.parseInt(tokens[i+1]) > Integer.parseInt(tokens[i+2])) return false;
                    i += 3;
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * Setea los campos de un objeto {@link RequestParams RequestParams} con el título y las opciones pasadas
     * por el usuario (año, tipo, rango de años)
     * @param params objeto donde se van a establecer los parámetros para una petición a la API
     * @param tokens Tokens que componen un comando válido
     * (tiene que haber sido validado previamente por {@link #validateInput validateInput}
     */
    private static void setParams(RequestParams params, String[] tokens) {

        params.setTitle(tokens[1]);
        for (int i = 2; i < tokens.length; ++i) {
            switch (tokens[i]) {
                case "-t":
                    params.setType(tokens[i+1]);
                    break;
                case "-y":
                    params.setYear(tokens[i+1]);
                    break;
                case "-r":
                    params.setYearRange(tokens[i+1], tokens[i+2]);
                    break;
                default:
            }
        }
    }
}
