package iot;

import java.time.LocalDateTime;

public class Reading extends Entity {
    private final String deviceId;
    private final String sensorId;
    private final LocalDateTime timestamp;
    private final double value;

    public Reading(String deviceId, String sensorId, LocalDateTime timestamp, double value){
        if(deviceId==null||deviceId.isBlank()) throw new IllegalArgumentException("deviceId required");
        if(sensorId==null||sensorId.isBlank()) throw new IllegalArgumentException("sensorId required");
        if(timestamp==null) throw new IllegalArgumentException("timestamp required");
        this.deviceId = deviceId;
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getDeviceId(){ return deviceId; }
    public String getSensorId(){ return sensorId; }
    public LocalDateTime getTimestamp(){ return timestamp; }
    public double getValue(){ return value; }

    @Override public String toString(){
        return getId()+" | "+deviceId+"/"+sensorId+" @"+timestamp+" = "+value;
    }
}
