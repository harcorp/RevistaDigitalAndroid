package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Publicacion {

    public String titulo;
    public Long timestamp;
    public String thumbnail;
    public String descripcion;
    public String category;


    public Publicacion(){

    }

    public Publicacion(String titulo, Long timestamp, String thumbnail, String descripcion, String category){
        this.titulo = titulo;
        this.timestamp = timestamp;
        this.thumbnail = thumbnail;
        this.descripcion = descripcion;
        this.category = category;
    }

}
