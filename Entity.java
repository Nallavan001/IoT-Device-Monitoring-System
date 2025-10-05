package iot;

import java.util.UUID;

/**
 * Base entity providing a UUID id.
 */
public abstract class Entity {
    private final String id = UUID.randomUUID().toString();
    public String getId() { return id; }
}
