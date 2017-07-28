package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Publicacion {

    public String titulo;
    public Integer count;
    public String fecha;
    public Integer uid;
    public String thumbnail;
    public String descripcion;

    public Publicacion(){

    }

    public Publicacion(String titulo, Integer count, String fecha, Integer uid, String thumbnail, String descripcion){
        this.titulo = titulo;
        this.count = count;
        this.fecha = fecha;
        this.uid = uid;
        this.thumbnail = thumbnail;
        this.descripcion = descripcion;
    }

}
