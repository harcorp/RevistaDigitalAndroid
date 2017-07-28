package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by davidrodriguez on 8/01/17.
 */
@IgnoreExtraProperties
public class Categoria {

    public Integer id;
    public String nombre;

    public Categoria(){

    }

    public Categoria(Integer id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }


}
