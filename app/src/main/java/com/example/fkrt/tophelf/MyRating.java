package com.example.fkrt.tophelf;

/**
 * Created by FKRT on 15.04.2016.
 */
public class MyRating {

    private String p_id, t_id, c_id, rating;

    public MyRating() {
        p_id = "";
        t_id  = "";
        c_id  = "";
        rating  = "";
    }

    public MyRating(String p_id, String t_id, String c_id, String rating) {
        this.p_id = p_id;
        this.t_id  = t_id;
        this.c_id  = c_id;
        this.rating  = rating;
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
}
