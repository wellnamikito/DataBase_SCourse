package backend.model;

/**Сущность: Владелец видеосалона */
public class Owner {
    private int ownerID;
    private String familia;
    private String name;
    private String otchestvo;

    public Owner(){}
    public Owner(int ownerID, String familia, String name, String otchestvo){
        this.ownerID=ownerID; this.familia=familia;
        this.name = name; this.otchestvo = otchestvo;
    }

    public int getOwnerID() {return ownerID;}
    public void setOwnerID(int v) {ownerID = v;}
    public String getFamilia() {return familia;}
    public void setFamilia(String v){ familia = v;}
    public String getName() {return name;}
    public void setName(String v){ name = v;}
    public String getOtchestvo() {return otchestvo;}
    public void setOtchestvo(String v){ otchestvo = v;}

    @Override
    public String toString()
    {return familia + " " + name + " " + otchestvo;}
}
