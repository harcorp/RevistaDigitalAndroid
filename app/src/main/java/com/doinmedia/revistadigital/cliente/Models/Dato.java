package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Dato {

    public String videoId;
    public String imagen;
    public String audio;


    public Dato(){

    }

    public Dato(String videoId, String imagen, String audio){
        this.videoId = videoId;
        this.imagen = imagen;
        this.audio = audio;
    }
}
