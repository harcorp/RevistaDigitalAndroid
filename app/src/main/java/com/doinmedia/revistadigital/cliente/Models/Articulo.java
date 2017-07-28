package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by davidrodriguez on 4/01/17.
 */
@IgnoreExtraProperties
public class Articulo {

    public String category;
    public String descripcion;
    public String parent;
    public String thumbnail;
    public String titulo;
    public String video;

    public Articulo(){

    }

    public Articulo(String category, String descripcion, String parent, String thumbnail, String titulo,
                    String video){
        this.category = category;
        this.descripcion = descripcion;
        this.parent = parent;
        this.thumbnail = thumbnail;
        this.titulo = titulo;
        this.video = video;
    }
}
