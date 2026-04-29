package backend.model;

public class District {
    private int districtId;
    private String districtName;

    public District() {}
    public District(int id, String name) { this.districtId = id; this.districtName = name; }

    public int getDistrictId()            { return districtId; }
    public void setDistrictId(int v)      { districtId = v; }
    public String getDistrictName()       { return districtName; }
    public void setDistrictName(String v) { districtName = v; }

    @Override public String toString()    { return districtName; }
}