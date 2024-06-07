package com.ugb.controlesbasicos20;

import java.io.Serializable;

public class ClassProductos implements Serializable {
    private String user;
    private String codigo;
    private String nombre;
    private String marca;
    private String descripcion;
    private Double precio;
    private Double costo;
    private int stock;
    private String foto;
    private String fotoUrl;

    public ClassProductos(String user, String codigo, String nombre, String marca, String descripcion, Double precio, Double costo, int stock, String foto, String fotoUrl) {
        this.user = user;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.descripcion = descripcion;
        this.precio = precio;
        this.costo = costo;
        this.stock = stock;
        this.foto = foto;
        this.fotoUrl = fotoUrl;
    }

    public String getUser() {
        return user;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMarca() {
        return marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public Double getCosto() {
        return costo;
    }

    public int getStock() {
        return stock;
    }

    public String getFoto() {
        return foto;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }
}