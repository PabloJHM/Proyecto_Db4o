package com.example.pablo.Proyecto_Db4o.mapa;

import java.util.Date;

public class Posicion {
    private float latitud;
    private float longitud;
    private Date fecha;

    public Posicion() {
    }

    public Posicion(float latitud, float longitud, Date fecha) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
