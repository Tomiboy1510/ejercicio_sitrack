package maintests;

import main.Main;
import omdb.RequestParams;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    // El método main() no hace mucho más que recibir input, parsearlo y hacer peticiones a OmdbClient
    // Me parece más sensato testar sus métodos privados (para lo cual hace falta usar Reflexión)

    @Test
    void testValidateInput() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method validateInput = Main.class.getDeclaredMethod("validateInput", String[].class);
        validateInput.setAccessible(true);

        assertEquals(true, validateInput.invoke(null, new Object[]{new String[]{"BUSCAR", "abc"}}));
        assertEquals(true, validateInput.invoke(null, new Object[]{new String[]{"23"}}));
        assertEquals(true, validateInput.invoke(null, new Object[]{new String[]{"buscar", "0000000000", "-y", "2004"}}));
        assertEquals(true, validateInput.invoke(null, new Object[]{new String[]{"buscar", "abc", "-t", "series"}}));
        assertEquals(true, validateInput.invoke(null, new Object[]{new String[]{"buscar", "abc", "-r", "2004", "2008"}}));
        assertEquals(true, validateInput.invoke(null, new Object[]{new String[]{"buscar", "abc", "-t", "movie", "-r", "2004", "2008"}}));

        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{"buscar"}}));
        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{"busc"}}));
        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{"buscar", "abc", "-t", "seriesss"}}));
        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{"buscar", "a", "-r", "abc", "1990"}}));
        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{"buscar", "a", "-r", "1980", "def"}}));
        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{"buscar", "abc", "-r", "2004", "1990"}}));
        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{"buscar", "a", "-y", "1980", "def"}}));
        assertEquals(false, validateInput.invoke(null, new Object[]{new String[]{}}));
    }
    
    @Test 
    void testSetParams() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        
        Method setParams = Main.class.getDeclaredMethod("setParams", RequestParams.class, String[].class);
        setParams.setAccessible(true);

        RequestParams p = new RequestParams();
        
        setParams.invoke(null, p, new String[]{"BUSCAR", "abc"});

        p.reset();
        setParams.invoke(null, p, new String[]{"buscar", "0000000000", "-y", "2004"});
        assertEquals("0000000000", p.getTitle());
        assertEquals("2004", p.getYear());
        assertNull(p.getStartYear());
        assertNull(p.getEndYear());
        assertNull(p.getId());
        assertNull(p.getType());

        p.reset();
        setParams.invoke(null, p, new String[]{"buscar", "abc", "-t", "series"});
        assertEquals("abc", p.getTitle());
        assertEquals("series", p.getType());
        assertNull(p.getStartYear());
        assertNull(p.getEndYear());
        assertNull(p.getId());
        assertNull(p.getYear());

        p.reset();
        setParams.invoke(null, p, new String[]{"buscar", "abc", "-r", "2004", "2008"});
        assertEquals("abc", p.getTitle());
        assertEquals("2004", p.getStartYear());
        assertEquals("2008", p.getEndYear());
        assertNull(p.getYear());
        assertNull(p.getId());
        assertNull(p.getType());

        p.reset();
        setParams.invoke(null, p, new String[]{"buscar", "abc", "-t", "movie", "-r", "2004", "2008"});
        assertEquals("abc", p.getTitle());
        assertEquals("movie", p.getType());
        assertEquals("2004", p.getStartYear());
        assertEquals("2008", p.getEndYear());
        assertNull(p.getYear());
        assertNull(p.getId());

        p.reset();
        setParams.invoke(null, p, new String[]{"buscar", "abc", "-r", "2004", "2008", "-y", "1998"});
        assertEquals("abc", p.getTitle());
        assertEquals("1998", p.getYear());
        assertNull(p.getStartYear());
        assertNull(p.getEndYear());
        assertNull(p.getId());
        assertNull(p.getType());
    }
}