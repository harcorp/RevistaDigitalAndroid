package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Dato {

    public String videoId;
    public String imagen;
    public String audio;
    public String texto;
    public String imagen2;
    public String videoText;


    public Dato(){

    }

    public Dato(String videoId, String imagen, String audio, String texto, String imagen2, String videoText){
        this.videoId = videoId;
        this.imagen = imagen;
        this.audio = audio;
        this.texto = texto;
        this.imagen2 = imagen2;
        this.videoText = videoText;
    }
}
