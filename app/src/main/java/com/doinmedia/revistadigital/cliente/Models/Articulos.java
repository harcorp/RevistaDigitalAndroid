package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by davidrodriguez on 27/01/17.
 */
@IgnoreExtraProperties
public class Articulos {

    private String file;
    private String nombre;
    private String descripcion;
    private Long fecha;
    private String type;

    public Articulos(){

    }

    public Articulos(String file, String nombre, String descripcion, Long fecha, String type){
        this.file = file;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.type = type;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Long getFecha() {
        return fecha;
    }

    public String getFile() {
        return file;
    }

    public String getNombre() {
        return nombre;
    }

    public String getType(){
        return type;
    }
}
