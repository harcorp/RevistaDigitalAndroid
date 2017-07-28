package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by davidrodriguez on 27/01/17.
 */
@IgnoreExtraProperties
public class Articulos {

    private String filename;
    private String nombre;
    private String descripcion;
    private String fecha;

    public Articulos(){

    }

    public Articulos(String filename, String nombre, String descripcion, String fecha){
        this.filename = filename;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getFilename() {
        return filename;
    }

    public String getNombre() {
        return nombre;
    }
}
