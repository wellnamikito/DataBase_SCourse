package backend.model;

/** Сущность: Кассета */
public class Cassette {
    private int cassetteId;
    private int filmId;
    private int videoId;
    private int qualityId;
    private boolean demand;

    public Cassette() {}

    public int getCassetteId()         { return cassetteId; }
    public void setCassetteId(int v)   { cassetteId = v; }
    public int getFilmId()             { return filmId; }
    public void setFilmId(int v)       { filmId = v; }
    public int getVideoId()            { return videoId; }
    public void setVideoId(int v)      { videoId = v; }
    public int getQualityId()          { return qualityId; }
    public void setQualityId(int v)    { qualityId = v; }
    public boolean isDemand()          { return demand; }
    public void setDemand(boolean v)   { demand = v; }
}