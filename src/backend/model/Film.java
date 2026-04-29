package backend.model;

/** Сущность: Фильм */
public class Film {
    private int filmId;
    private String caption;
    private String year;
    private int duration;
    private String information;
    private int directorId;
    private int studioId;

    public Film() {}

    public int getFilmId()              { return filmId; }
    public void setFilmId(int v)        { filmId = v; }
    public String getCaption()          { return caption; }
    public void setCaption(String v)    { caption = v; }
    public String getYear()             { return year; }
    public void setYear(String v)       { year = v; }
    public int getDuration()            { return duration; }
    public void setDuration(int v)      { duration = v; }
    public String getInformation()      { return information; }
    public void setInformation(String v){ information = v; }
    public int getDirectorId()          { return directorId; }
    public void setDirectorId(int v)    { directorId = v; }
    public int getStudioId()            { return studioId; }
    public void setStudioId(int v)      { studioId = v; }

    @Override public String toString()  { return caption + " (" + year + ")"; }
}