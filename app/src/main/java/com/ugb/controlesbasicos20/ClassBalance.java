package com.ugb.controlesbasicos20;

import java.io.Serializable;

public class ClassBalance {
    private String user;
    private String stock;
    private String venta;

    public ClassBalance(String user, String stock, String venta) {
        this.user = user;
        this.stock = stock;
        this.venta = venta;
    }

    public String getUser() {
        return user;
    }

    public String getStock() {
        return stock;
    }

    public String getVenta() {
        return venta;
    }
}
