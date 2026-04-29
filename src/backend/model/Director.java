package backend.model;

public class Director {
    private int directorId;
    private String familia;
    private String name;
    private String otchestvo;

    public Director() {}
    public Director(int id, String f, String n, String o) {
        this.directorId = id; this.familia = f; this.name = n; this.otchestvo = o;
    }

    public int getDirectorId()           { return directorId; }
    public void setDirectorId(int v)     { directorId = v; }
    public String getFamilia()           { return familia; }
    public void setFamilia(String v)     { familia = v; }
    public String getName()              { return name; }
    public void setName(String v)        { name = v; }
    public String getOtchestvo()         { return otchestvo; }
    public void setOtchestvo(String v)   { otchestvo = v; }

    @Override public String toString()   { return familia + " " + name + " " + otchestvo; }
}