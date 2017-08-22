package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Banner {

    public String link;
    public String image;
    public String type;

    public Banner(){

    }

    public Banner(String link, String image, String type){
        this.link = link;
        this.image = image;
        this.type = type;
    }
}
