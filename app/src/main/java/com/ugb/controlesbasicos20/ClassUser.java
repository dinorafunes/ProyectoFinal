package com.ugb.controlesbasicos20;

import java.io.Serializable;

public class ClassUser {
    private String foto;
    private String nombre;
    private String email;
    private String tipoCuenta;

    public ClassUser(String foto, String nombre, String email, String tipoCuenta) {
        this.foto = foto;
        this.nombre = nombre;
        this.email = email;
        this.tipoCuenta = tipoCuenta;
    }

    public String getEmail() {
        return email;
    }

    public String getFoto() {
        return foto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }
}
