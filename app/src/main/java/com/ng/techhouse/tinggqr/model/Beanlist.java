package com.ng.techhouse.tinggqr.model;

/**
 * Created by apple on 15/03/16.
 */
public class Beanlist {

    private int image;
    private String title;



    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Beanlist(int image, String title) {


        this.image = image;
        this.title = title;

    }
}

