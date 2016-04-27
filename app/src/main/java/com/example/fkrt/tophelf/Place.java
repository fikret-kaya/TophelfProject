package com.example.fkrt.tophelf;

/**
 * Created by FKRT on 27.04.2016.
 */
public class Place {

    private String name, id, info, loc;

    public Place(String name, String id, String info, String loc) {
        this.name = name;
        this.id = id;
        this.info = info;
        this.loc = loc;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public String getLoc() {
        return loc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
