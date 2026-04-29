package backend.model;

public class Quality {
    private int qualityId;
    private String qualityName;

    public Quality() {}
    public Quality(int id, String name) { this.qualityId = id; this.qualityName = name; }

    public int getQualityId()            { return qualityId; }
    public void setQualityId(int v)      { qualityId = v; }
    public String getQualityName()       { return qualityName; }
    public void setQualityName(String v) { qualityName = v; }

    @Override public String toString()   { return qualityName; }
}