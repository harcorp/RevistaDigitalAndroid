package com.doinmedia.revistadigital.cliente.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidrodriguez on 24/01/17.
 */
@IgnoreExtraProperties
public class Comentario {

    public String uid_user;
    public String filename;
    public String texto;
    public Boolean aproved;
    private Integer type;

    public Comentario(){

    }

    public Comentario(String uid_user, String filename, String texto, Boolean aproved, Integer type){
        this.uid_user = uid_user;
        this.filename = filename;
        this.texto = texto;
        this.aproved = aproved;
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid_user", uid_user);
        result.put("filename", filename);
        result.put("texto", texto);
        result.put("aproved", aproved);
        result.put("type", type);

        return result;
    }
}
