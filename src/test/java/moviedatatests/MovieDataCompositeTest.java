package moviedatatests;

import moviedata.MovieDataComposite;
import moviedata.MovieDataLeaf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieDataCompositeTest {

    @Test
    void testGetId() {

        MovieDataComposite o = new MovieDataComposite("0");

        MovieDataLeaf c1 = new MovieDataLeaf("c1", "1");
        MovieDataComposite c2 = new MovieDataComposite("c2");
        MovieDataLeaf c3 = new MovieDataLeaf("c3", "3");
        MovieDataLeaf c4 = new MovieDataLeaf("imdbID", "999");

        o.addChild(c1);
        o.addChild(c2);
        c2.addChild(c3);
        c2.addChild(c4);

        assertEquals("999", o.getId());
        assertNull(c1.getId());
    }

    @Test
    void testToString() {

        MovieDataComposite o = new MovieDataComposite("0");

        MovieDataLeaf c1 = new MovieDataLeaf("c1", "1");
        MovieDataComposite c2 = new MovieDataComposite("c2");
        MovieDataLeaf c3 = new MovieDataLeaf("c3", "3");
        MovieDataLeaf c4 = new MovieDataLeaf("imdbID", "999");

        o.addChild(c1);
        o.addChild(c2);
        c2.addChild(c3);
        c2.addChild(c4);

        String text = "0:\n\tc1: 1\n\tc2:\n\t\tc3: 3\n\t\timdbID: 999\n";
        assertEquals(text, o.toString());
    }
}