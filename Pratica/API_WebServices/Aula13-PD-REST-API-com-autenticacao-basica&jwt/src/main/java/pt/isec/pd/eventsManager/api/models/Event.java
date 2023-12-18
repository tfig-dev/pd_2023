package pt.isec.pd.eventsManager.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int ID;
    private String name;
    private String location;
    private String date;
    private String startTime;
    private String endTime;

    public Event(int ID, String name, String location, String date, String startTime, String endTime) {
        this.ID = ID;
        this.name = name;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @JsonCreator
    public Event(@JsonProperty("name") String name,
                 @JsonProperty("location") String location,
                 @JsonProperty("date") String date,
                 @JsonProperty("startTime") String startTime,
                 @JsonProperty("endTime") String endTime) {
        this.ID = -1;
        this.name = name;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        if (ID == -1)
            return "{" +
                    "\"name\": \"" + name + "\"" +
                    ", \"location\": \"" + location + "\"" +
                    ", \"date\": \"" + date + "\"" +
                    ", \"startTime\": \"" + startTime + "\"" +
                    ", \"endTime\": \"" + endTime + "\"}";
        else return "{" +
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