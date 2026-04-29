package backend.model;

public class Service {
    private int serviceId;
    private String serviceName;

    public Service() {}
    public Service(int id, String name) { this.serviceId = id; this.serviceName = name; }

    public int getServiceId()            { return serviceId; }
    public void setServiceId(int v)      { serviceId = v; }
    public String getServiceName()       { return serviceName; }
    public void setServiceName(String v) { serviceName = v; }

    @Override public String toString()   { return serviceName; }
}