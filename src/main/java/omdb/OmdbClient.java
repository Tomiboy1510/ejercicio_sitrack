package omdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moviedata.MovieData;
import moviedata.MovieDataComposite;
import moviedata.MovieDataLeaf;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Clase que emite peticiones a la API de Omdb. Permite obtener listados de películas buscando
 * por título ({@link #search search()}) u obtener los detalles completos de una película buscando
 * por ID de imdb ({@link #getById getById()}).
 */
public class OmdbClient {

    private final String baseUrl;
    private final String apiKey;

    public OmdbClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Busca una película específica por ID de imdb y devuelve todos sus datos disponibles
     * en un objeto {@link MovieData MovieData}.
     * @param params Parámetros para la query (el ID es obligatorio)
     * @return un objeto {@link MovieData MovieData} con los datos de la película anidados
     * @throws URISyntaxException si la URI está malformada
     * @throws IOException si falla la petición
     * @throws InterruptedException si es interrumpido antes de recibir respuesta
     */
    public MovieData getById(RequestParams params) throws URISyntaxException, IOException, InterruptedException {

        URI requestUri = new URI(buildQueryString(params, false));
        HttpResponse<String> response = makeGetRequest(requestUri);
        return buildMovieData(response.body(), "Detalles");
    }

    /**
     * Busca películas por título y devuelve: año, tipo e ID imdb de cada una,
     * en un array de {@link MovieData MovieData}.
     *
     * Si entre los parámetros se incluye un rango de años, entonces se ejecutan queries para cada
     * año comprendido en ese periodo y se agregan todos esos resultados (Esto produciría duplicados
     * si no se usara un Set ya que hay películas listadas con más de un año)
     * @param params Parámetros para la query (obligatoriamente título, opcionalmente tipo, año o rango de años)
     * @return un array de {@link MovieData MovieData} con año, tipo e ID imdb de cada película que aparezca
     * en el resultado de la query.
     * @throws URISyntaxException si la URI está malformada
     * @throws IOException si falla la petición
     * @throws InterruptedException si es interrumpido antes de recibir respuesta
     */
    public MovieData[] search(RequestParams params) throws URISyntaxException, IOException, InterruptedException {

        // Usar un set evita duplicados
        Set<MovieData> results = new LinkedHashSet<>();

        if (params.getEndYear() != null) {// Hay que buscar metrajes en un rango de tiempo
            int startYear = Integer.parseInt(params.getStartYear());
            int endYear = Integer.parseInt(params.getEndYear());
            RequestParams clone = new RequestParams(params);

            for (int i = startYear; i <= endYear; ++i) {
                clone.setYear(Integer.toString(i));

                URI requestUri = new URI(buildQueryString(clone, true));
                HttpResponse<String> response = makeGetRequest(requestUri);
                results.addAll(parseSearchResults(response.body()));

                // Ordenar por puntaje?

            }
        } else { // Buscar metrajes en un año específico
            URI requestUri = new URI(buildQueryString(params, true));
            HttpResponse<String> response = makeGetRequest(requestUri);
            results.addAll(parseSearchResults(response.body()));
        }

        return results.toArray(new MovieData[0]);
    }

    /**
     * Hace una petición GET Http al Uri pasado por parámetro y devuelve el resultado.
     * @param uri Uri al que enviar la petición
     * @return un objeto {@link HttpResponse HttpResponse} cuyo cuerpo contiene
     * el Json con la respuesta a la query
     * @throws IOException si falla la petición
     * @throws InterruptedException si es interrumpido antes de recibir respuesta
     */
    private static HttpResponse<String> makeGetRequest(URI uri) throws IOException, InterruptedException {

        HttpRequest getReq = HttpRequest.newBuilder().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        return client.send(getReq, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Crea un String para generar el Uri para la query añadiendo los parámetros especificados
     * en el objeto {@link RequestParams RequestParams}.
     * @param params objeto que contiene los parámetros para la query
     * @param searchAll si es true, se buscan todas las películas cuyo título coincida con el título
     * pasado por parámetro. Si es false se busca una película específica para obtener sus datos completos
     * @return un String para generar el Uri para obtener los resultados adecuados
     */
    private String buildQueryString(RequestParams params, boolean searchAll) {

        String queryString = baseUrl + "?apikey=" + apiKey + "&";

        if (params.getTitle() != null) {
            if (searchAll)
                queryString += "s=";
            else
                queryString += "t=";
            String title = params.getTitle().replace('_', '+');
            title = URLEncoder.encode(title, StandardCharsets.UTF_8);
            queryString += title + "&";
        }

        if (params.getId() != null)
            queryString += "i=" + params.getId() + "&";

        if (params.getType() != null)
            queryString += "type=" + params.getType() + "&";

        if (params.getYear() != null)
            queryString += "y=" + params.getYear() + "&";

        //System.out.println(queryString);
        return queryString;
    }

    /**
     * Parsea el Json obtenido en la respuesta y genera una colección de
     * objetos {@link MovieData MovieData} con los datos obtenidos.
     *
     * Se usa {@link LinkedHashSet LinkedHashSet} para eliminar duplicados y mantener el ordenamiento.
     * Véase: {@link #search search()}
     * @param json
     * @return
     */
    private static LinkedHashSet<MovieData> parseSearchResults(String json) {

        LinkedHashSet<MovieData> results = new LinkedHashSet<>();

        JsonArray jsonArr = (JsonArray) JsonParser
                .parseString(json)
                .getAsJsonObject()
                .get("Search");

        if (jsonArr == null) return results;

        for (JsonElement elem : jsonArr) {
            JsonObject obj = elem.getAsJsonObject();

            // De los resultados de la búsqueda sólo se muestran algunos datos, luego el usuario puede pedir más detalles
            MovieDataComposite movie = new MovieDataComposite(obj.get("Title").toString());

            movie.addChild(new MovieDataLeaf("Año", obj.get("Year").toString().replace("\"", "")));
            movie.addChild(new MovieDataLeaf("Tipo", obj.get("Type").toString().replace("\"", "")));
            movie.addChild(new MovieDataLeaf("imdbID", obj.get("imdbID").toString().replace("\"", "")));

            results.add(movie);
        }

        return results;
    }

    /**
     * Construye un objeto {@link MovieData MovieData} con los datos del Json pasado
     * por parámetro.
     * @param json Json obtenido como respuesta de una query con datos de un metraje
     * @param name Nombre para el objeto {@link MovieData MovieData} retornado. El
     * valor o valores correspondiente a este nombre serán obtenidos a partir del Json.
     * @return un objeto {@link MovieData MovieData} que contiene de forma anidada los
     * datos resumidos de metrajes obtenidos a partir del Json
     */
    private static MovieData buildMovieData(String json, String name) {

        MovieDataComposite root = new MovieDataComposite(name);
        JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();

        for (String key : jsonObj.keySet()) {
            JsonElement value = jsonObj.get(key);

            if (value.isJsonArray()) {
                MovieDataComposite m = new MovieDataComposite(key);
                int i = 1;
                for (JsonElement e : value.getAsJsonArray())
                    m.addChild(buildMovieData(e.toString(), Integer.toString(i++)));
                root.addChild(m);
                continue;
            }

            if (value.isJsonObject()) {
                root.addChild(buildMovieData(value.toString(), key));
                continue;
            }

            if (value.isJsonPrimitive()) {
                root.addChild(new MovieDataLeaf(key, value.toString().replace("\"", "")));
            }
        }

        return root;
    }
}
