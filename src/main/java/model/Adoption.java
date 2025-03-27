package model;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Adoption implements Serializable {
    private String id;
    private String petId;
    private String adopterId;
    private LocalDateTime adoptionDate;
    private String status; // "Pending", "Approved", "Rejected", "Completed"

    public Adoption() {
        this.id = UUID.randomUUID().toString();
        this.adoptionDate = LocalDateTime.now();
        this.status = "Pending";
    }

    public Adoption(String id, String petId, String adopterId) {
        this.id = id;
        this.petId = petId;
        this.adopterId = adopterId;
        this.adoptionDate = LocalDateTime.now();
        this.status = "Pending";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }

    public String getAdopterId() { return adopterId; }
    public void setAdopterId(String adopterId) { this.adopterId = adopterId; }

    public LocalDateTime getAdoptionDate() { return adoptionDate; }
    public void setAdoptionDate(LocalDateTime adoptionDate) { this.adoptionDate = adoptionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Adoption #" + id.substring(0, 8) + " (" + status + ")";
    }
}
