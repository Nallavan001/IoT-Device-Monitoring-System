package iot;

import java.time.LocalDateTime;

public class Alert extends Entity {
    private final String deviceId;
    private final String sensorId;
    private final String sensorType;
    private final Reading reading;
    private final LocalDateTime createdAt;
    private final AlertSeverity severity;
    private AlertStatus status = AlertStatus.NEW;
    private String acknowledgedBy; // operator id/name
    private LocalDateTime acknowledgedAt;
    private String linkedTicketId; // maintenance ticket id if created

    public Alert(Reading reading, String sensorType, AlertSeverity severity){
        this.reading = reading;
        this.deviceId = reading.getDeviceId();
        this.sensorId = reading.getSensorId();
        this.sensorType = sensorType;
        this.createdAt = LocalDateTime.now();
        this.severity = severity==null ? AlertSeverity.MEDIUM : severity;
    }

    public String getDeviceId(){ return deviceId; }
    public String getSensorId(){ return sensorId; }
    public String getSensorType(){ return sensorType; }
    public Reading getReading(){ return reading; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public AlertSeverity getSeverity(){ return severity; }
    public AlertStatus getStatus(){ return status; }

    public void acknowledge(String by){
        if(status!=AlertStatus.NEW) throw new IllegalStateException("Only NEW alerts can be acknowledged");
        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedBy = by;
        this.acknowledgedAt = LocalDateTime.now();
    }

    public void linkTicket(String ticketId){
        this.linkedTicketId = ticketId;
    }

    public void resolve(){
        if(status==AlertStatus.RESOLVED) return;
        this.status = AlertStatus.RESOLVED;
    }

    @Override public String toString(){
        return getId()+" | "+deviceId+"/"+sensorId+" | "+severity+" | "+status+" | val="+reading.getValue();
    }
}
