package omdb;

import com.google.gson.*;
import moviedata.MovieData;
import moviedata.MovieDataComposite;
import moviedata.MovieDataLeaf;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashSet;
import java.util.Set;

public class OmdbClient {

    private final String baseUrl;
    private final String apiKey;

    public OmdbClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public MovieData getById(RequestParams params) throws URISyntaxException, IOException, InterruptedException {

        URI requestUri = new URI(buildQueryString(params, false));
        HttpResponse<String> response = makeGetRequest(requestUri);
        return buildMovieData(response.body(), "Detalles");
    }

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

    private HttpResponse<String> makeGetRequest(URI uri) throws IOException, InterruptedException {

        HttpRequest getReq = HttpRequest.newBuilder().uri(uri).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse getResponse = client.send(getReq, HttpResponse.BodyHandlers.ofString());
        return getResponse;
    }

    private String buildQueryString(RequestParams params, boolean searchAll) {

        String queryString = baseUrl + "?apikey=" + apiKey + "&";

        if (params.getTitle() != null) {
            if (searchAll)
                queryString += "s=";
            else
                queryString += "t=";
            queryString += params.getTitle().replace('_', '+') + "&";
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

    private static MovieData buildMovieData(String json, String name) {

        MovieDataComposite root = new MovieDataComposite(name);
        JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();

        for (String key : jsonObj.keySet()) {
            JsonElement value = jsonObj.get(key);

            if (value.isJsonArray()) {
                MovieDataComposite m = new MovieDataComposite(key);
                for (JsonElement e : value.getAsJsonArray()) {
                    m.addChild(buildMovieData(e.toString(), ""));
                }
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
