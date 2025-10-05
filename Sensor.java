package iot;

import java.time.LocalDateTime;

public class Sensor extends Entity {
    private final String type;           // e.g., TEMPERATURE, HUMIDITY
    private final String unit;           // e.g., °C, %
    private LocalDateTime lastSeen;
    private Reading lastReading;

    public Sensor(String type, String unit){
        if(type==null || type.isBlank()) throw new IllegalArgumentException("Sensor type required");
        this.type = type.trim();
        this.unit = (unit==null) ? "" : unit.trim();
    }

    public String getType(){ return type; }
    public String getUnit(){ return unit; }

    public LocalDateTime getLastSeen(){ return lastSeen; }
    void touch(LocalDateTime time){ this.lastSeen = time; }

    public Reading getLastReading(){ return lastReading; }
    void setLastReading(Reading r){ this.lastReading = r; this.lastSeen = r.getTimestamp(); }

    @Override public String toString(){ return getId()+" | "+type+" ("+unit+")"; }
}
