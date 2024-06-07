package com.ugb.controlesbasicos20;

import java.io.Serializable;

public class ClassVenta implements Serializable {
    private String ID;
    private String user;
    private String codigo;
    private String nombre;
    private String marca;
    private Double precio;
    private String foto;
    private String fotoUrl;
    private String fecha;
    private Double cantidad;
    private String cliente;
    private Double latitud;
    private Double longitud;
    private Double totalVent;
    private Double ganancia;

    public ClassVenta(String ID, String user, String codigo, String nombre, String marca, Double precio, String foto, String fotoUrl, String fecha, Double cantidad, String cliente, Double totalVent, Double ganancia, double latitud, double longitud) {
        this.ID = ID;
        this.user = user;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.foto = foto;
        this.fotoUrl = fotoUrl;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.cliente = cliente;
        this.totalVent = totalVent;
        this.ganancia = ganancia;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getID() {
        return ID;
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

    public Double getPrecio() {
        return precio;
    }

    public String getFoto() {
        return foto;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getFecha() {
        return fecha;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public String getCliente() {
        return cliente;
    }

    public Double getTotalVent() {
        return totalVent;
    }

    public Double getGanancia() {
        return ganancia;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

}
