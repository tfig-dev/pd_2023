package pt.isec.pd.eventsManager.api.models;

import java.io.Serial;
import java.io.Serializable;

public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int ID;
    private final String name;
    private final String location;
    private final String date;
    private final String startTime;
    private final String endTime;

    public Event(int ID, String name, String location, String date, String startTime, String endTime) {
        this.ID = ID;
        this.name = name;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Event(String name, String location, String date, String startTime, String endTime) {
        this.ID = -1;
        this.name = name;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "{" +
                "\"ID\": " + ID +
                ", \"name\": \"" + name + "\"" +
                ", \"location\": \"" + location + "\"" +
                ", \"date\": \"" + date + "\"" +
                ", \"startTime\": \"" + startTime + "\"" +
                ", \"endTime\": \"" + endTime + "\"}";
    }

    public int getId() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}