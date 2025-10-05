package iot;

import java.time.LocalDateTime;
import java.util.*;

public class MaintenanceTicket extends Entity {
    private final String alertId;
    private final String deviceId;
    private final String sensorId;
    private final String description;
    private final LocalDateTime createdAt;
    private TicketStatus status = TicketStatus.OPEN;
    private final List<String> actionLogs = new ArrayList<>(); // simple text logs
    private String assignedTechId;

    public MaintenanceTicket(Alert alert, String description){
        if(alert==null) throw new IllegalArgumentException("alert required");
        this.alertId = alert.getId();
        this.deviceId = alert.getDeviceId();
        this.sensorId = alert.getSensorId();
        this.description = (description==null ? "" : description);
        this.createdAt = LocalDateTime.now();
    }

    public String getAlertId(){ return alertId; }
    public String getDeviceId(){ return deviceId; }
    public String getSensorId(){ return sensorId; }
    public TicketStatus getStatus(){ return status; }
    public List<String> getActionLogs(){ return Collections.unmodifiableList(actionLogs); }
    public void assignTechnician(Technician t){ this.assignedTechId = t==null? null : t.getId(); }

    public void addAction(String action, Technician by){
        if(action==null || action.isBlank()) throw new IllegalArgumentException("action required");
        String who = (by==null) ? "system" : by.getName()+"("+by.getId()+")";
        actionLogs.add(LocalDateTime.now()+" | "+who+" | "+action);
    }

    /**
     * Close the ticket. Rule: requires at least one action log before closure.
     */
    public void close(){
        if(actionLogs.isEmpty()) throw new IllegalStateException("At least one technician action required to close ticket");
        this.status = TicketStatus.CLOSED;
    }

    @Override public String toString(){
        return getId()+" | TICKET for alert="+alertId+" | status="+status+" | actions="+actionLogs.size();
    }
}
