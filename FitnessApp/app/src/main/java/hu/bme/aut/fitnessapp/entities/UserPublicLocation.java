package hu.bme.aut.fitnessapp.entities;

public class UserPublicLocation {

    private int id;
    private String gymId;

    public UserPublicLocation() {}

    public UserPublicLocation(int id, String gymId) {
        this.id = id;
        this.gymId = gymId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGymId() {
        return gymId;
    }

    public void setGymId(String gymId) {
        this.gymId = gymId;
    }
}
