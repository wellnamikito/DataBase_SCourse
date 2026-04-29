package backend.model;

public class Studio {
    private int studioId;
    private String studioName;
    private int countryId;

    public Studio() {}
    public Studio(int id, String name, int countryId) {
        this.studioId = id; this.studioName = name; this.countryId = countryId;
    }

    public int getStudioId()            { return studioId; }
    public void setStudioId(int v)      { studioId = v; }
    public String getStudioName()       { return studioName; }
    public void setStudioName(String v) { studioName = v; }
    public int getCountryId()           { return countryId; }
    public void setCountryId(int v)     { countryId = v; }

    @Override public String toString()  { return studioName; }
}