package omdbtests;

import moviedata.MovieData;
import omdb.OmdbClient;
import omdb.RequestParams;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OmdbClientTest {

    @Test
    void testBuildQueryString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method buildQueryString = OmdbClient.class.getDeclaredMethod("buildQueryString", RequestParams.class, boolean.class);
        buildQueryString.setAccessible(true);

        OmdbClient c = new OmdbClient("BASEURL", "APIKEY");
        RequestParams p = new RequestParams();

        p.setTitle("batman");
        assertEquals("BASEURL?apikey=APIKEY&s=batman&", buildQueryString.invoke(c, p, true));

        p.setYear("2004");
        assertEquals("BASEURL?apikey=APIKEY&t=batman&y=2004&", buildQueryString.invoke(c, p, false));

        p.setType("movie");
        assertEquals("BASEURL?apikey=APIKEY&s=batman&type=movie&y=2004&", buildQueryString.invoke(c, p, true));

        p.setTitle(null);
        p.setId("tt1234567");
        assertEquals("BASEURL?apikey=APIKEY&i=tt1234567&type=movie&y=2004&", buildQueryString.invoke(c, p, true));
        assertEquals("BASEURL?apikey=APIKEY&i=tt1234567&type=movie&y=2004&", buildQueryString.invoke(c, p, false));
    }

    @Test
    void testParseSearchResults() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method parseSearchResults = OmdbClient.class.getDeclaredMethod("parseSearchResults", String.class);
        parseSearchResults.setAccessible(true);

        String json = "{\"Search\":[{\"Title\":\"Berserk: The Golden Age Arc I - The Egg of the King\",\"Year\":\"2012\",\"imdbID\":\"tt2210479\",\"Type\":\"movie\"}," +
                "{\"Title\":\"Berserk: The Golden Age Arc II - The Battle for Doldrey\",\"Year\":\"2012\",\"imdbID\":\"tt2358911\",\"Type\":\"movie\"}," +
                "{\"Title\":\"Berserk: The Golden Age Arc III - The Advent\",\"Year\":\"2013\",\"imdbID\":\"tt2358913\",\"Type\":\"movie\"}," +
                "{\"Title\":\"Berserk: The Golden Age Arc - Memorial Edition\",\"Year\":\"2022–\",\"imdbID\":\"tt22445494\",\"Type\":\"series\"}]," +
                "\"totalResults\":\"4\",\"Response\":\"True\"}";

        String movieDataString = "[\"Berserk: The Golden Age Arc I - The Egg of the King\":\n\tAño: 2012\n\tTipo: movie\n\timdbID: tt2210479\n, " +
                "\"Berserk: The Golden Age Arc II - The Battle for Doldrey\":\n\tAño: 2012\n\tTipo: movie\n\timdbID: tt2358911\n, " +
                "\"Berserk: The Golden Age Arc III - The Advent\":\n\tAño: 2013\n\tTipo: movie\n\timdbID: tt2358913\n, " +
                "\"Berserk: The Golden Age Arc - Memorial Edition\":\n\tAño: 2022–\n\tTipo: series\n\timdbID: tt22445494\n]";

        Set<MovieData> movies = (Set<MovieData>) parseSearchResults.invoke(null, json);
        assertEquals(movieDataString, movies.toString());
    }

    @Test
    void testBuildMovieData() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method buildMovieData = OmdbClient.class.getDeclaredMethod("buildMovieData", String.class, String.class);
        buildMovieData.setAccessible(true);

        String json = "{\"Title\":\"Ghost in the Shell\",\"Year\":\"1995\",\"Rated\":\"TV-MA\",\"Released\":\"29 Mar 1996\"," +
                "\"Runtime\":\"83 min\",\"Genre\":\"Animation, Action, Crime\",\"Director\":\"Mamoru Oshii\"," +
                "\"Writer\":\"Shirow Masamune, Kazunori Itô\",\"Actors\":\"Atsuko Tanaka, Iemasa Kayumi, Akio Ôtsuka\"," +
                "\"Plot\":\"A cyborg policewoman and her partner hunt a mysterious and powerful hacker called the Puppet Master.\"," +
                "\"Language\":\"Japanese\",\"Country\":\"Japan, United Kingdom\",\"Awards\":\"5 wins & 7 nominations\"," +
                "\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.9/10\"},{\"Source\":\"Rotten Tomatoes\"," +
                "\"Value\":\"95%\"},{\"Source\":\"Metacritic\",\"Value\":\"76/100\"}],\"Metascore\":\"76\",\"imdbRating\":\"7.9\"," +
                "\"imdbVotes\":\"153,334\",\"imdbID\":\"tt0113568\",\"Type\":\"movie\",\"DVD\":\"15 Dec 2010\"," +
                "\"BoxOffice\":\"$889,074\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}";

        String movieDataString = "Detalles:\n\tTitle: Ghost in the Shell\n\tYear: 1995\n\tRated: TV-MA\n\tReleased: 29 Mar 1996\n\t" +
                "Runtime: 83 min\n\tGenre: Animation, Action, Crime\n\tDirector: Mamoru Oshii\n\tWriter: Shirow Masamune, Kazunori Itô" +
                "\n\tActors: Atsuko Tanaka, Iemasa Kayumi, Akio Ôtsuka\n\tPlot: A cyborg policewoman and her partner hunt a mysterious " +
                "and powerful hacker called the Puppet Master.\n\tLanguage: Japanese\n\tCountry: Japan, United Kingdom\n\tAwards: 5 wins " +
                "& 7 nominations\n\tRatings:\n\t\t1:\n\t\t\tSource: Internet Movie Database\n\t\t\tValue: 7.9/10\n\t\t2:\n\t\t\tSource: " +
                "Rotten Tomatoes\n\t\t\tValue: 95%\n\t\t3:\n\t\t\tSource: Metacritic\n\t\t\tValue: 76/100\n\tMetascore: 76\n\timdbRating: " +
                "7.9\n\timdbVotes: 153,334\n\timdbID: tt0113568\n\tType: movie\n\tDVD: 15 Dec 2010\n\tBoxOffice: $889,074\n\tProduction: " +
                "N/A\n\tWebsite: N/A\n\tResponse: True\n";

        MovieData m = (MovieData) buildMovieData.invoke(null, json, "Detalles");
        assertEquals(movieDataString, m.toString());
    }
}