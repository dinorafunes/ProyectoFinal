package com.ugb.controlesbasicos20;

public class ClassMensaje {
    private String contenido;
    private String emisor;

    public ClassMensaje(String contenido, String emisor) {
        this.contenido = contenido;
        this.emisor = emisor;
    }

    public String getContenido() {
        return contenido;
    }

    public String getEmisor() {
        return emisor;
    }
}
