package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by davidrodriguez on 27/01/17.
 */
@IgnoreExtraProperties
public class Articulos {

    public String file;
    public String nombre;
    public String descripcion;
    public Long fecha;
    public String type;
    public String thumbnail;

    public Articulos(){

    }

    public Articulos(String file, String nombre, String descripcion, Long fecha, String type, String thumbnail){
        this.file = file;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.type = type;
        this.thumbnail = thumbnail;
    }
}
