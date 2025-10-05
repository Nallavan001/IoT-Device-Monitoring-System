package iot;

import java.util.Collection;

public class DashboardPrinter {
    public static void printDeviceSummary(Device d){
        System.out.println("DEVICE: "+d.getName()+" ("+d.getId()+") @"+d.getLocation());
        System.out.println(" Sensors: "+d.getSensors().size());
        d.getSensors().forEach(s -> {
            System.out.println("  - "+s.getId()+" | "+s.getType()+" last:"+ (s.getLastReading()==null? "n/a": s.getLastReading().getValue()+"@"+s.getLastReading().getTimestamp()));
        });
    }

    public static void printAlerts(Collection<Alert> alerts){
        System.out.println("\n---- LIVE ALERTS ----");
        alerts.forEach(a -> System.out.println(a));
    }
}
