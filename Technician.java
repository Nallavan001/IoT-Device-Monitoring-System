package iot;

import java.util.ArrayList;
import java.util.List;

public class Technician extends Entity {
    private final String name;
    private final String contact;

    public Technician(String name, String contact){
        if(name==null || name.isBlank()) throw new IllegalArgumentException("Technician name required");
        this.name = name.trim();
        this.contact = contact==null ? "" : contact.trim();
    }

    public String getName(){ return name; }
    public String getContact(){ return contact; }

    @Override public String toString(){ return getId()+" | "+name; }
}
