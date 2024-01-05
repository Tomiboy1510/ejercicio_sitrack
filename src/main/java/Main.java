import moviedata.MovieData;
import omdb.OmdbClient;
import omdb.RequestParams;
import utils.StringUtils;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;

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

            // Obtener input del usuario
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
            System.out.println("Mostrando " + movies.length + " resultados:\n\n");
            for (int i = 0; i < movies.length; ++i)
                System.out.println((i+1)+ ". " + movies[i].toString());
            if (movies.length != 0)
                System.out.println("(Ingresar número para ver los detalles completos de un metraje)\n");
        }
    }

    private static void printMenu() {

        try (Scanner file = new Scanner(new File("src/main/resources/menu.txt"))) {
            while (file.hasNextLine()) {
                System.out.println(file.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo menu.txt");
        }
    }

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

    public static void setParams(RequestParams params, String[] tokens) {

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
                    ;
            }
        }
    }
}
