package hu.bme.aut.fitnessapp.Models;

public class UserPublicLocation {
    public int id;
    public String gym_id;

    public UserPublicLocation() {}

    public UserPublicLocation(int id, String gym_id) {
        this.id = id;
        this.gym_id = gym_id;
    }
}