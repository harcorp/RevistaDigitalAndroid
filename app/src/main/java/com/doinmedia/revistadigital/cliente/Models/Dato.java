package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Dato {

    public String videoId;
    public String imagen;
    public String audio;
    public String texto;


    public Dato(){

    }

    public Dato(String videoId, String imagen, String audio, String texto){
        this.videoId = videoId;
        this.imagen = imagen;
        this.audio = audio;
        this.texto = texto;
    }
}
