package com.example.fkrt.tophelf;

import java.security.Timestamp;

/**
 * Created by FKRT on 15.04.2016.
 */
public class Relation {

    private String username, p_id, t_id, c_id, rating, relationTime;

    public Relation() {
        username = "";
        p_id = "";
        t_id  = "";
        c_id  = "";
        rating  = "";
        relationTime = "";
    }

    public Relation(String username, String p_id, String t_id, String c_id, String rating, String relationTime) {
        this.username = username;
        this.p_id = p_id;
        this.t_id  = t_id;
        this.c_id  = c_id;
        this.rating  = rating;
        this.relationTime = relationTime;
    }

    public String getP_id() {
        return p_id;
    }

    public String getT_id() {
        return t_id;
    }

    public String getC_id() {
        return c_id;
    }

    public String getRating() {
        return rating;
    }

    public String getRelationTime() {
        return relationTime;
    }

    public String getUsername() {
        return username;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setRelationTime(String relationTime) {
        this.relationTime = relationTime;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
