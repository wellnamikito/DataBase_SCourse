package backend.model;

public class Country {
    private int countryId;
    private String countryName;

    public Country() {}
    public Country(int id, String name) { this.countryId = id; this.countryName = name; }

    public int getCountryId()            { return countryId; }
    public void setCountryId(int v)      { countryId = v; }
    public String getCountryName()       { return countryName; }
    public void setCountryName(String v) { countryName = v; }

    @Override public String toString()   { return countryName; }
}