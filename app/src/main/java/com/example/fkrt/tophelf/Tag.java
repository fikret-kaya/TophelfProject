package com.example.fkrt.tophelf;

/**
 * Created by FKRT on 27.04.2016.
 */
public class Tag {
    private String name, id, time;

    public Tag(String name, String id, String time) {
        this.name = name;
        this.id = id;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
