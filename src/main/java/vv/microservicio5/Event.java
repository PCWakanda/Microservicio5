package vv.microservicio5;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class Event implements Serializable {
    @Id
    private UUID id;
    private String type;
    private String source;
    private String timestamp;

    // No-argument constructor
    public Event() {
    }

    public Event(UUID id, String type, String source, String timestamp) {
        this.id = id;
        this.type = type;
        this.source = source;
        this.timestamp = timestamp;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getTimestamp() {
        return timestamp;
    }
}