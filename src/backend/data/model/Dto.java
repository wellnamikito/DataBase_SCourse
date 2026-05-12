package backend.data.model;

import javafx.beans.property.*;

/**
 * DTO-классы для всех сущностей БД.
 * ID (PK/FK) скрыты в TableView — присутствуют только для внутреннего использования.
 * Публичные поля = бизнес-данные.
 */
public class Dto {

    // ─────────────────────── OWNER ───────────────────────
    public static class Owner {
        private final IntegerProperty ownerId = new SimpleIntegerProperty();
        private final StringProperty familia   = new SimpleStringProperty();
        private final StringProperty name      = new SimpleStringProperty();
        private final StringProperty otchestvo = new SimpleStringProperty();

        public Owner(int id, String f, String n, String o) {
            ownerId.set(id); familia.set(f); name.set(n); otchestvo.set(o);
        }

        public int getOwnerId()           { return ownerId.get(); }
        public String getFamilia()         { return familia.get(); }
        public String getName()            { return name.get(); }
        public String getOtchestvo()       { return otchestvo.get(); }
        public void setFamilia(String v)   { familia.set(v); }
        public void setName(String v)      { name.set(v); }
        public void setOtchestvo(String v) { otchestvo.set(v); }

        public StringProperty familiaProperty()   { return familia; }
        public StringProperty nameProperty()      { return name; }
        public StringProperty otchestvoProperty() { return otchestvo; }
    }

    // ─────────────────────── DISTRICT ───────────────────────
    public static class District {
        private final IntegerProperty districtId   = new SimpleIntegerProperty();
        private final StringProperty districtName  = new SimpleStringProperty();

        public District(int id, String name) {
            districtId.set(id); districtName.set(name);
        }

        public int getDistrictId()            { return districtId.get(); }
        public String getDistrictName()       { return districtName.get(); }
        public void setDistrictName(String v) { districtName.set(v); }
        public StringProperty districtNameProperty() { return districtName; }
    }

    // ─────────────────────── QUALITY ───────────────────────
    public static class Quality {
        private final IntegerProperty qualityId   = new SimpleIntegerProperty();
        private final StringProperty qualityName  = new SimpleStringProperty();

        public Quality(int id, String name) {
            qualityId.set(id); qualityName.set(name);
        }

        public int getQualityId()            { return qualityId.get(); }
        public String getQualityName()       { return qualityName.get(); }
        public void setQualityName(String v) { qualityName.set(v); }
        public StringProperty qualityNameProperty() { return qualityName; }
    }

    // ─────────────────────── SERVICE ───────────────────────
    public static class Service {
        private final IntegerProperty serviceId   = new SimpleIntegerProperty();
        private final StringProperty serviceName  = new SimpleStringProperty();

        public Service(int id, String name) {
            serviceId.set(id); serviceName.set(name);
        }

        public int getServiceId()            { return serviceId.get(); }
        public String getServiceName()       { return serviceName.get(); }
        public void setServiceName(String v) { serviceName.set(v); }
        public StringProperty serviceNameProperty() { return serviceName; }
    }

    // ─────────────────────── DIRECTOR ───────────────────────
    public static class Director {
        private final IntegerProperty directorId = new SimpleIntegerProperty();
        private final StringProperty familia      = new SimpleStringProperty();
        private final StringProperty name         = new SimpleStringProperty();
        private final StringProperty otchestvo    = new SimpleStringProperty();

        public Director(int id, String f, String n, String o) {
            directorId.set(id); familia.set(f); name.set(n); otchestvo.set(o);
        }

        public int getDirectorId()         { return directorId.get(); }
        public String getFamilia()          { return familia.get(); }
        public String getName()             { return name.get(); }
        public String getOtchestvo()        { return otchestvo.get(); }
        public void setFamilia(String v)    { familia.set(v); }
        public void setName(String v)       { name.set(v); }
        public void setOtchestvo(String v)  { otchestvo.set(v); }
        public StringProperty familiaProperty()   { return familia; }
        public StringProperty nameProperty()      { return name; }
        public StringProperty otchestvoProperty() { return otchestvo; }
    }

    // ─────────────────────── COUNTRY ───────────────────────
    public static class Country {
        private final IntegerProperty countryId   = new SimpleIntegerProperty();
        private final StringProperty countryName  = new SimpleStringProperty();

        public Country(int id, String name) {
            countryId.set(id); countryName.set(name);
        }

        public int getCountryId()            { return countryId.get(); }
        public String getCountryName()       { return countryName.get(); }
        public void setCountryName(String v) { countryName.set(v); }
        public StringProperty countryNameProperty() { return countryName; }
    }

    // ─────────────────────── STUDIO ───────────────────────
    public static class Studio {
        private final IntegerProperty studioId   = new SimpleIntegerProperty();
        private final StringProperty studioName  = new SimpleStringProperty();
        private final StringProperty countryName = new SimpleStringProperty();
        private final IntegerProperty countryId  = new SimpleIntegerProperty();

        public Studio(int id, String name, int countryId, String countryName) {
            studioId.set(id); studioName.set(name);
            this.countryId.set(countryId); this.countryName.set(countryName);
        }

        public int getStudioId()             { return studioId.get(); }
        public String getStudioName()        { return studioName.get(); }
        public int getCountryId()            { return countryId.get(); }
        public String getCountryName()       { return countryName.get(); }
        public void setStudioName(String v)  { studioName.set(v); }
        public void setCountryId(int v)      { countryId.set(v); }
        public StringProperty studioNameProperty()  { return studioName; }
        public StringProperty countryNameProperty() { return countryName; }
    }

    // ─────────────────────── FILM ───────────────────────
    public static class Film {
        private final IntegerProperty filmId       = new SimpleIntegerProperty();
        private final StringProperty caption        = new SimpleStringProperty();
        private final StringProperty year           = new SimpleStringProperty();
        private final IntegerProperty duration      = new SimpleIntegerProperty();
        private final StringProperty information    = new SimpleStringProperty();
        private final StringProperty directorName   = new SimpleStringProperty();
        private final StringProperty studioName     = new SimpleStringProperty();
        private final IntegerProperty directorId    = new SimpleIntegerProperty();
        private final IntegerProperty studioId      = new SimpleIntegerProperty();

        public Film(int id, String caption, String year, int duration, String info,
                    int directorId, String directorName, int studioId, String studioName) {
            filmId.set(id); this.caption.set(caption); this.year.set(year);
            this.duration.set(duration); this.information.set(info);
            this.directorId.set(directorId); this.directorName.set(directorName);
            this.studioId.set(studioId); this.studioName.set(studioName);
        }

        public int getFilmId()               { return filmId.get(); }
        public String getCaption()            { return caption.get(); }
        public String getYear()               { return year.get(); }
        public int getDuration()              { return duration.get(); }
        public String getInformation()        { return information.get(); }
        public String getDirectorName()       { return directorName.get(); }
        public String getStudioName()         { return studioName.get(); }
        public int getDirectorId()            { return directorId.get(); }
        public int getStudioId()              { return studioId.get(); }
        public void setCaption(String v)      { caption.set(v); }
        public void setYear(String v)         { year.set(v); }
        public void setDuration(int v)        { duration.set(v); }
        public void setInformation(String v)  { information.set(v); }
        public StringProperty captionProperty()     { return caption; }
        public StringProperty yearProperty()        { return year; }
        public IntegerProperty durationProperty()   { return duration; }
        public StringProperty directorNameProperty(){ return directorName; }
        public StringProperty studioNameProperty()  { return studioName; }
        public StringProperty informationProperty() { return information; }
    }

    // ─────────────────────── VIDEO ───────────────────────
    public static class Video {
        private final IntegerProperty videoId      = new SimpleIntegerProperty();
        private final StringProperty caption        = new SimpleStringProperty();
        private final StringProperty districtName   = new SimpleStringProperty();
        private final StringProperty address        = new SimpleStringProperty();
        private final StringProperty type           = new SimpleStringProperty();
        private final StringProperty phone          = new SimpleStringProperty();
        private final StringProperty licence        = new SimpleStringProperty();
        private final IntegerProperty timeStart     = new SimpleIntegerProperty();
        private final IntegerProperty timeEnd       = new SimpleIntegerProperty();
        private final IntegerProperty amount        = new SimpleIntegerProperty();
        private final StringProperty ownerName      = new SimpleStringProperty();
        private final IntegerProperty districtId    = new SimpleIntegerProperty();
        private final IntegerProperty ownerId       = new SimpleIntegerProperty();

        public Video(int id, String caption, int districtId, String districtName,
                     String address, String type, String phone, String licence,
                     int timeStart, int timeEnd, int amount, int ownerId, String ownerName) {
            videoId.set(id); this.caption.set(caption);
            this.districtId.set(districtId); this.districtName.set(districtName);
            this.address.set(address); this.type.set(type);
            this.phone.set(phone); this.licence.set(licence);
            this.timeStart.set(timeStart); this.timeEnd.set(timeEnd);
            this.amount.set(amount);
            this.ownerId.set(ownerId); this.ownerName.set(ownerName);
        }

        public int getVideoId()              { return videoId.get(); }
        public String getCaption()            { return caption.get(); }
        public String getDistrictName()       { return districtName.get(); }
        public String getAddress()            { return address.get(); }
        public String getType()               { return type.get(); }
        public String getPhone()              { return phone.get(); }
        public String getLicence()            { return licence.get(); }
        public int getTimeStart()             { return timeStart.get(); }
        public int getTimeEnd()               { return timeEnd.get(); }
        public int getAmount()                { return amount.get(); }
        public String getOwnerName()          { return ownerName.get(); }
        public int getDistrictId()            { return districtId.get(); }
        public int getOwnerId()               { return ownerId.get(); }
        public void setCaption(String v)      { caption.set(v); }
        public void setAddress(String v)      { address.set(v); }
        public StringProperty captionProperty()     { return caption; }
        public StringProperty districtNameProperty(){ return districtName; }
        public StringProperty addressProperty()     { return address; }
        public StringProperty typeProperty()        { return type; }
        public StringProperty phoneProperty()       { return phone; }
        public StringProperty licenceProperty()     { return licence; }
        public IntegerProperty timeStartProperty()  { return timeStart; }
        public IntegerProperty timeEndProperty()    { return timeEnd; }
        public IntegerProperty amountProperty()     { return amount; }
        public StringProperty ownerNameProperty()   { return ownerName; }
    }

    // ─────────────────────── CASSETTE ───────────────────────
    public static class Cassette {
        private final IntegerProperty cassetteId  = new SimpleIntegerProperty();
        private final StringProperty filmCaption   = new SimpleStringProperty();
        private final StringProperty videoCaption  = new SimpleStringProperty();
        private final StringProperty qualityName   = new SimpleStringProperty();
        private final BooleanProperty demand       = new SimpleBooleanProperty();
        private byte[] photo;
        private final IntegerProperty filmId       = new SimpleIntegerProperty();
        private final IntegerProperty videoId      = new SimpleIntegerProperty();
        private final IntegerProperty qualityId    = new SimpleIntegerProperty();

        public Cassette(int id, int filmId, String filmCaption, int videoId, String videoCaption,
                        int qualityId, String qualityName, byte[] photo, boolean demand) {
            cassetteId.set(id);
            this.filmId.set(filmId); this.filmCaption.set(filmCaption);
            this.videoId.set(videoId); this.videoCaption.set(videoCaption);
            this.qualityId.set(qualityId); this.qualityName.set(qualityName);
            this.photo = photo; this.demand.set(demand);
        }

        public int getCassetteId()           { return cassetteId.get(); }
        public String getFilmCaption()        { return filmCaption.get(); }
        public String getVideoCaption()       { return videoCaption.get(); }
        public String getQualityName()        { return qualityName.get(); }
        public boolean isDemand()             { return demand.get(); }
        public byte[] getPhoto()              { return photo; }
        public int getFilmId()               { return filmId.get(); }
        public int getVideoId()              { return videoId.get(); }
        public int getQualityId()            { return qualityId.get(); }
        public void setPhoto(byte[] p)       { this.photo = p; }
        public void setDemand(boolean v)     { demand.set(v); }
        public StringProperty filmCaptionProperty()  { return filmCaption; }
        public StringProperty videoCaptionProperty() { return videoCaption; }
        public StringProperty qualityNameProperty()  { return qualityName; }
        public BooleanProperty demandProperty()      { return demand; }
    }

    // ─────────────────────── RECEIPT ───────────────────────
    public static class Receipt {
        private final IntegerProperty receiptId    = new SimpleIntegerProperty();
        private final StringProperty videoCaption   = new SimpleStringProperty();
        private final StringProperty serviceName    = new SimpleStringProperty();
        private final ObjectProperty<java.time.LocalDate> date =
                new SimpleObjectProperty<>();
        private final IntegerProperty price         = new SimpleIntegerProperty();
        private final IntegerProperty cassetteId    = new SimpleIntegerProperty();
        private final IntegerProperty videoId       = new SimpleIntegerProperty();
        private final IntegerProperty serviceId     = new SimpleIntegerProperty();

        public Receipt(int id, int cassetteId, int videoId, String videoCaption,
                       int serviceId, String serviceName,
                       java.time.LocalDate date, int price) {
            receiptId.set(id);
            this.cassetteId.set(cassetteId); this.videoId.set(videoId);
            this.videoCaption.set(videoCaption);
            this.serviceId.set(serviceId); this.serviceName.set(serviceName);
            this.date.set(date); this.price.set(price);
        }

        public int getReceiptId()            { return receiptId.get(); }
        public String getVideoCaption()       { return videoCaption.get(); }
        public String getServiceName()        { return serviceName.get(); }
        public java.time.LocalDate getDate()  { return date.get(); }
        public int getPrice()                { return price.get(); }
        public int getCassetteId()           { return cassetteId.get(); }
        public int getVideoId()              { return videoId.get(); }
        public int getServiceId()            { return serviceId.get(); }
        public void setPrice(int v)          { price.set(v); }
        public StringProperty videoCaptionProperty() { return videoCaption; }
        public StringProperty serviceNameProperty()  { return serviceName; }
        public ObjectProperty<java.time.LocalDate> dateProperty() { return date; }
        public IntegerProperty priceProperty()       { return price; }
    }

    // ─────────────────────── AUDIT LOG ───────────────────────
    public static class AuditLog {
        private final IntegerProperty logId           = new SimpleIntegerProperty();
        private final StringProperty tableName         = new SimpleStringProperty();
        private final StringProperty operationType     = new SimpleStringProperty();
        private final ObjectProperty<java.time.LocalDateTime> operationDate =
                new SimpleObjectProperty<>();

        public AuditLog(int id, String table, String op, java.time.LocalDateTime dt) {
            logId.set(id); tableName.set(table);
            operationType.set(op); operationDate.set(dt);
        }

        public int getLogId()                  { return logId.get(); }
        public String getTableName()            { return tableName.get(); }
        public String getOperationType()        { return operationType.get(); }
        public java.time.LocalDateTime getOperationDate() { return operationDate.get(); }
        public StringProperty tableNameProperty()     { return tableName; }
        public StringProperty operationTypeProperty() { return operationType; }
        public ObjectProperty<java.time.LocalDateTime> operationDateProperty() { return operationDate; }
    }
}