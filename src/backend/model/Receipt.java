package backend.model;

import java.time.LocalDate;

/** Сущность: Квитанция */
public class Receipt {
    private int receiptId;
    private int cassetteId;
    private int videoId;
    private int serviceId;
    private LocalDate date;
    private int price;

    public Receipt() {}

    public int getReceiptId()           { return receiptId; }
    public void setReceiptId(int v)     { receiptId = v; }
    public int getCassetteId()          { return cassetteId; }
    public void setCassetteId(int v)    { cassetteId = v; }
    public int getVideoId()             { return videoId; }
    public void setVideoId(int v)       { videoId = v; }
    public int getServiceId()           { return serviceId; }
    public void setServiceId(int v)     { serviceId = v; }
    public LocalDate getDate()          { return date; }
    public void setDate(LocalDate v)    { date = v; }
    public int getPrice()               { return price; }
    public void setPrice(int v)         { price = v; }
}