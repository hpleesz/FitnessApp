package hu.bme.aut.fitnessapp.Models;

public class Measurement {
    public String date;
    public double value;

    public Measurement() {}

    public Measurement(String date, double value) {
        this.date = date;
        this.value = value;
    }
}
