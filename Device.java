package iot;

import java.util.*;

public class Device extends Entity {
    private String name;
    private String location;
    private final Map<String, Sensor> sensors = new LinkedHashMap<>();

    public Device(String name, String location) {
        setName(name); setLocation(location);
    }

    public String getName(){ return name; }
    public void setName(String name){
        if(name==null || name.isBlank()) throw new IllegalArgumentException("Device name required");
        this.name = name.trim();
    }

    public String getLocation(){ return location; }
    public void setLocation(String location){
        this.location = location==null ? "" : location.trim();
    }

    public Collection<Sensor> getSensors(){ return Collections.unmodifiableCollection(sensors.values()); }

    public void addSensor(Sensor s){
        if(s==null) throw new IllegalArgumentException("Sensor null");
        sensors.put(s.getId(), s);
    }

    public Sensor getSensorById(String id){ return sensors.get(id); }

    @Override public String toString(){ return getId()+" | "+name+" @"+location; }
}
