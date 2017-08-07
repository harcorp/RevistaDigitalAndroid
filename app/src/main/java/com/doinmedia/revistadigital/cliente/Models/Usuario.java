package com.doinmedia.revistadigital.cliente.Models;

/**
 * Created by davidrodriguez on 5/08/17.
 */

public class Usuario {

    public String nombre;
    public String profilePicture;

    public Usuario(){

    }

    public Usuario(String nombre, String profilePicture){
        this.nombre = nombre;
        this.profilePicture = profilePicture;
    }
}
