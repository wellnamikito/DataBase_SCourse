package backend.model;

/** Сущность: Видеосалон */
public class Video {
    private int videoId;
    private String caption;
    private int districtId;
    private String address;
    private String type;
    private String phone;
    private String licence;
    private int timeStart;
    private int timeEnd;
    private int amount;
    private int ownerId;

    public Video() {}

    public int getVideoId()            { return videoId; }
    public void setVideoId(int v)      { videoId = v; }
    public String getCaption()         { return caption; }
    public void setCaption(String v)   { caption = v; }
    public int getDistrictId()         { return districtId; }
    public void setDistrictId(int v)   { districtId = v; }
    public String getAddress()         { return address; }
    public void setAddress(String v)   { address = v; }
    public String getType()            { return type; }
    public void setType(String v)      { type = v; }
    public String getPhone()           { return phone; }
    public void setPhone(String v)     { phone = v; }
    public String getLicence()         { return licence; }
    public void setLicence(String v)   { licence = v; }
    public int getTimeStart()          { return timeStart; }
    public void setTimeStart(int v)    { timeStart = v; }
    public int getTimeEnd()            { return timeEnd; }
    public void setTimeEnd(int v)      { timeEnd = v; }
    public int getAmount()             { return amount; }
    public void setAmount(int v)       { amount = v; }
    public int getOwnerId()            { return ownerId; }
    public void setOwnerId(int v)      { ownerId = v; }

    @Override public String toString() { return caption; }
}