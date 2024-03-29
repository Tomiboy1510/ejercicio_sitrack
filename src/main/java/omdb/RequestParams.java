package omdb;

/**
 * Objeto que contiene los parámetros para generar una query a la API. No tiene mucha funcionalidad,
 * pero facilita el crear una query donde el número y el orden de los argumentos es variable.
 * No puede tener simultáneamente un año y un rango de años. El setear uno de los dos deshabilita el otro.
 */
public class RequestParams {

    private String title;
    private String type;
    private String startYear;
    private String endYear;
    private String id;

    public RequestParams() {

    }

    public RequestParams(RequestParams p) {
        this.title = p.getTitle();
        this.type = p.getType();
        this.startYear = p.getStartYear();
        this.endYear = p.getEndYear();
        this.id = p.getId();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setYear(String year) {
        this.startYear = year;
        this.endYear = null;
    }

    public void setYearRange(String minYear, String maxYear) {
        this.startYear = minYear;
        this.endYear = maxYear;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setea todos los campos a null
     */
    public void reset() {
        title = null;
        type = null;
        startYear = null;
        endYear = null;
        id = null;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getYear() {
        if (endYear == null)
            return startYear;
        return null;
    }

    public String getStartYear() {
        if (endYear == null)
            return null;
        return startYear;
    }

    public String getEndYear() {
        if (startYear == null)
            return null;
        return endYear;
    }

    public String getId() {
        return id;
    }

}
