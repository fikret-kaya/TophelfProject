package com.tophelf;

import java.security.Timestamp;

/**
 * Created by FKRT on 15.04.2016.
 */
public class Relation {

    private String username, u_id, p_id, t_id, c_id, rating, relationTime, email, r_id;

    public Relation() {
        username = "";
        u_id = "";
        p_id = "";
        t_id  = "";
        c_id  = "";
        rating  = "";
        relationTime = "";
        email = "";
        r_id = "";
    }

    public Relation(String username, String u_id, String p_id, String t_id, String c_id, String rating,
                                                    String relationTime, String email, String r_id) {
        this.username = username;
        this.u_id = u_id;
        this.p_id = p_id;
        this.t_id  = t_id;
        this.c_id  = c_id;
        this.rating  = rating;
        this.relationTime = relationTime;
        this.email = email;
        this.r_id = r_id;
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

    public String getU_id() {
        return u_id;
    }

    public String getEmail() {
        return email;
    }

    public String getR_id() {
        return r_id;
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

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }
}
