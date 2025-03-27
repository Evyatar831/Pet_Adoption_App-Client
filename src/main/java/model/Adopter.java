package model;

import java.io.Serializable;
import java.util.UUID;


public class Adopter implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;

    private String preferredSpecies;
    private String preferredBreed;
    private int preferredAgeMin;
    private int preferredAgeMax;
    private String preferredGender;

    public Adopter() {
        this.id = UUID.randomUUID().toString();
        this.preferredAgeMin = 0;
        this.preferredAgeMax = 30;
    }

    public Adopter(String id, String name, String email, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.preferredAgeMin = 0;
        this.preferredAgeMax = 30;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPreferredSpecies() { return preferredSpecies; }
    public void setPreferredSpecies(String preferredSpecies) { this.preferredSpecies = preferredSpecies; }

    public String getPreferredBreed() { return preferredBreed; }
    public void setPreferredBreed(String preferredBreed) { this.preferredBreed = preferredBreed; }

    public int getPreferredAgeMin() { return preferredAgeMin; }
    public void setPreferredAgeMin(int preferredAgeMin) { this.preferredAgeMin = preferredAgeMin; }

    public int getPreferredAgeMax() { return preferredAgeMax; }
    public void setPreferredAgeMax(int preferredAgeMax) { this.preferredAgeMax = preferredAgeMax; }

    public String getPreferredGender() { return preferredGender; }
    public void setPreferredGender(String preferredGender) { this.preferredGender = preferredGender; }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}