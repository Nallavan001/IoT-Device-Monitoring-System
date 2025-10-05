package iot;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Simple menu-driven console app implementing the rules.
 * Note: uses in-memory maps; not persistent.
 */
public class IoTMonitoringApp {
    private final Map<String, Device> devices = new LinkedHashMap<>();
    private final Map<String, Sensor> sensors = new LinkedHashMap<>();
    private final Map<String, Threshold> thresholds = new LinkedHashMap<>();
    private final Map<String, Alert> alerts = new LinkedHashMap<>();
    private final Map<String, MaintenanceTicket> tickets = new LinkedHashMap<>();
    private final Map<String, Technician> techs = new LinkedHashMap<>();

    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args){
        new IoTMonitoringApp().run();
    }

    private void run(){
        seed();
        while(true){
            System.out.println("\n=== IoT Monitoring Menu ===");
            System.out.println("1) Add Device");
            System.out.println("2) Add Sensor to Device");
            System.out.println("3) Set Threshold");
            System.out.println("4) Ingest Reading");
            System.out.println("5) Generate Alerts (scan recent readings)");
            System.out.println("6) Acknowledge Alert");
            System.out.println("7) Create Maintenance Ticket for Alert");
            System.out.println("8) Add Tech Action to Ticket");
            System.out.println("9) Close Ticket");
            System.out.println("10) View Dashboards / Alerts");
            System.out.println("11) Exit");
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            try {
                switch(ch){
                    case "1": addDevice(); break;
                    case "2": addSensor(); break;
                    case "3": setThreshold(); break;
                    case "4": ingestReading(); break;
                    case "5": generateAlerts(); break;
                    case "6": acknowledgeAlert(); break;
                    case "7": createTicket(); break;
                    case "8": addTechAction(); break;
                    case "9": closeTicket(); break;
                    case "10": viewDashboards(); break;
                    case "11": System.out.println("Bye."); return;
                    default: System.out.println("Invalid.");
                }
            } catch(Exception ex){
                System.out.println("Error: "+ex.getMessage());
            }
        }
    }

    private void seed(){
        Device d = new Device("Boiler-01", "Plant-A"); devices.put(d.getId(), d);
        Sensor s = new Sensor("TEMPERATURE", "°C"); d.addSensor(s); sensors.put(s.getId(), s);
        Threshold t = new Threshold(d.getId(), "TEMPERATURE", null, 80.0, AlertSeverity.HIGH); thresholds.put(t.getId(), t);

        Technician tech = new Technician("Ravi", "99999"); techs.put(tech.getId(), tech);
    }

    private void addDevice(){
        System.out.print("Device name: "); String name = sc.nextLine().trim();
        System.out.print("Location: "); String loc = sc.nextLine().trim();
        Device d = new Device(name, loc); devices.put(d.getId(), d);
        System.out.println("Added: "+d);
    }

    private void addSensor(){
        Device d = pickDevice(); if(d==null) return;
        System.out.print("Sensor type (e.g. TEMPERATURE): "); String type = sc.nextLine().trim();
        System.out.print("Unit (e.g. °C): "); String unit = sc.nextLine().trim();
        Sensor s = new Sensor(type, unit);
        d.addSensor(s); sensors.put(s.getId(), s);
        System.out.println("Added sensor: "+s+" to device "+d.getName());
    }

    private void setThreshold(){
        Device d = pickDevice(); if(d==null) return;
        System.out.print("Sensor type for threshold: "); String stype = sc.nextLine().trim();
        System.out.print("Min (blank for none): "); String minu = sc.nextLine().trim();
        System.out.print("Max (blank for none): "); String maxu = sc.nextLine().trim();
        System.out.print("Severity (LOW/MEDIUM/HIGH/CRITICAL): "); String sev = sc.nextLine().trim();
        Double min = minu.isEmpty()? null: Double.parseDouble(minu);
        Double max = maxu.isEmpty()? null: Double.parseDouble(maxu);
        AlertSeverity severity = sev.isEmpty()? AlertSeverity.MEDIUM : AlertSeverity.valueOf(sev.toUpperCase());
        Threshold t = new Threshold(d.getId(), stype, min, max, severity); thresholds.put(t.getId(), t);
        System.out.println("Threshold set: "+t);
    }

    private void ingestReading(){
        Device d = pickDevice(); if(d==null) return;
        Sensor s = pickSensorForDevice(d); if(s==null) return;
        System.out.print("Value: "); double v = Double.parseDouble(sc.nextLine().trim());
        LocalDateTime now = LocalDateTime.now();
        Reading r = new Reading(d.getId(), s.getId(), now, v);
        s.setLastReading(r); s.touch(now);
        System.out.println("Ingested: "+r);
        // immediate check against thresholds for that device/sensor type
        thresholds.values().stream()
            .filter(t -> t.getDeviceId().equals(d.getId()) && t.getSensorType().equalsIgnoreCase(s.getType()))
            .forEach(t -> {
                if(t.isBreached(v)){
                    Alert a = new Alert(r, s.getType(), t.getSeverity());
                    alerts.put(a.getId(), a);
                    System.out.println("ALERT GENERATED: "+a);
                }
            });
    }

    private void generateAlerts(){
        // simple scan: any lastReading against thresholds
        for(Threshold t : thresholds.values()){
            Device d = devices.get(t.getDeviceId());
            if(d==null) continue;
            for(Sensor s : d.getSensors()){
                if(!s.getType().equalsIgnoreCase(t.getSensorType())) continue;
                Reading lr = s.getLastReading();
                if(lr==null) continue;
                if(t.isBreached(lr.getValue())){
                    Alert a = new Alert(lr, s.getType(), t.getSeverity());
                    alerts.put(a.getId(), a);
                    System.out.println("ALERT: "+a);
                }
            }
        }
    }

    private void acknowledgeAlert(){
        Alert a = pickAlert(AlertStatus.NEW); if(a==null) return;
        System.out.print("Acknowledger name: "); String who = sc.nextLine().trim();
        a.acknowledge(who);
        System.out.println("Acknowledged: "+a);
    }

    private void createTicket(){
        Alert a = pickAlert(AlertStatus.ACKNOWLEDGED); if(a==null) return;
        System.out.print("Ticket description: "); String desc = sc.nextLine().trim();
        MaintenanceTicket t = new MaintenanceTicket(a, desc);
        tickets.put(t.getId(), t);
        a.linkTicket(t.getId());
        System.out.println("Ticket created: "+t);
    }

    private void addTechAction(){
        MaintenanceTicket t = pickTicket(); if(t==null) return;
        System.out.print("Action description: "); String action = sc.nextLine().trim();
        System.out.print("Technician ID (leave blank to pick): "); String tid = sc.nextLine().trim();
        Technician tech = null;
        if(tid.isEmpty()){
            tech = pickTechnician();
        } else {
            tech = techs.get(tid);
            if(tech==null) System.out.println("Tech id not found; using none");
        }
        t.addAction(action, tech);
        System.out.println("Action added.");
    }

    private void closeTicket(){
        MaintenanceTicket t = pickTicket(); if(t==null) return;
        try {
            t.close();
            // resolve linked alert if present
            Alert a = alerts.get(t.getAlertId());
            if(a!=null) a.resolve();
            System.out.println("Ticket closed and alert resolved (if linked).");
        } catch(Exception ex){
            System.out.println("Cannot close: "+ex.getMessage());
        }
    }

    private void viewDashboards(){
        System.out.println("\n--- Devices ---");
        devices.values().forEach(DashboardPrinter::printDeviceSummary);
        DashboardPrinter.printAlerts(alerts.values());
        System.out.println("\n--- Tickets ---");
        tickets.values().forEach(System.out::println);
    }

    /* ===== Helpers / Pickers ===== */
    private Device pickDevice(){
        if(devices.isEmpty()){ System.out.println("No devices"); return null; }
        devices.values().forEach(d -> System.out.println(d.getId()+" : "+d.getName()+" @"+d.getLocation()));
        System.out.print("Device ID: "); String id = sc.nextLine().trim(); Device d = devices.get(id);
        if(d==null) System.out.println("Not found");
        return d;
    }

    private Sensor pickSensorForDevice(Device d){
        if(d.getSensors().isEmpty()){ System.out.println("Device has no sensors"); return null; }
        d.getSensors().forEach(s -> System.out.println(s.getId()+" : "+s.getType()+" ("+s.getUnit()+")"));
        System.out.print("Sensor ID: "); String id = sc.nextLine().trim(); Sensor s = sensors.get(id);
        if(s==null) System.out.println("Not found");
        return s;
    }

    private Alert pickAlert(AlertStatus required){
        if(alerts.isEmpty()){ System.out.println("No alerts"); return null; }
        alerts.values().stream().forEach(a -> System.out.println(a.getId()+" : "+a));
        System.out.print("Alert ID: "); String id = sc.nextLine().trim(); Alert a = alerts.get(id);
        if(a==null) { System.out.println("Not found"); return null; }
        if(required!=null && a.getStatus()!=required){ System.out.println("Alert status mismatch: required="+required); return null; }
        return a;
    }

    private MaintenanceTicket pickTicket(){
        if(tickets.isEmpty()){ System.out.println("No tickets"); return null; }
        tickets.values().forEach(t -> System.out.println(t.getId()+" : "+t));
        System.out.print("Ticket ID: "); String id = sc.nextLine().trim(); MaintenanceTicket t = tickets.get(id);
        if(t==null) System.out.println("Not found");
        return t;
    }

    private Technician pickTechnician(){
        if(techs.isEmpty()){
            System.out.print("No technicians exist. Enter name to create: ");
            String name = sc.nextLine().trim();
            Technician t = new Technician(name, "");
            techs.put(t.getId(), t);
            return t;
        }
        techs.values().forEach(t -> System.out.println(t.getId()+" : "+t.getName()));
        System.out.print("Technician ID: "); String id = sc.nextLine().trim(); Technician t = techs.get(id);
        if(t==null) System.out.println("Not found");
        return t;
    }
}
