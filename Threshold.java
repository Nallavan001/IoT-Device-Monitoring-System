package iot;

/**
 * Threshold for a sensor type on a device.
 * Supports simple upper/lower bounds. If a bound is null, it's not applied.
 */
public class Threshold extends Entity {
    private final String deviceId;
    private final String sensorType;
    private final Double minValue; // nullable
    private final Double maxValue; // nullable
    private final AlertSeverity severity;

    public Threshold(String deviceId, String sensorType, Double minValue, Double maxValue, AlertSeverity severity){
        if(deviceId==null||deviceId.isBlank()) throw new IllegalArgumentException("deviceId required");
        if(sensorType==null||sensorType.isBlank()) throw new IllegalArgumentException("sensorType required");
        if(minValue!=null && maxValue!=null && minValue>maxValue) throw new IllegalArgumentException("min>max");
        this.deviceId=deviceId; this.sensorType=sensorType; this.minValue=minValue; this.maxValue=maxValue;
        this.severity = severity==null? AlertSeverity.MEDIUM: severity;
    }

    public String getDeviceId(){ return deviceId; }
    public String getSensorType(){ return sensorType; }
    public Double getMinValue(){ return minValue; }
    public Double getMaxValue(){ return maxValue; }
    public AlertSeverity getSeverity(){ return severity; }

    public boolean isBreached(double value){
        if(minValue!=null && value < minValue) return true;
        if(maxValue!=null && value > maxValue) return true;
        return false;
    }

    @Override public String toString(){
        return getId()+" | "+deviceId+" / "+sensorType+" ["+
               (minValue==null? "-":minValue)+","+(maxValue==null? "-":maxValue)+"] -> "+severity;
    }
}
