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
    public String file;
    public String texto;
    public Boolean aproved;
    private Integer type;
    public String parent;

    public Comentario(){

    }

    public Comentario(String uid_user, String file, String texto, Boolean aproved, Integer type, String parent){
        this.uid_user = uid_user;
        this.file = file;
        this.texto = texto;
        this.aproved = aproved;
        this.type = type;
        this.parent = parent;
    }

    public Integer getType() {
        return type;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("uid_user", uid_user);
        result.put("file", file);
        result.put("texto", texto);
        result.put("parent", parent);
        result.put("aproved", aproved);
        result.put("type", type);
        result.put("timestamp",  -0.001 * System.currentTimeMillis());
        return result;
    }
}
