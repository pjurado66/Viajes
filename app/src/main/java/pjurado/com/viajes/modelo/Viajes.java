package pjurado.com.viajes.modelo;

import android.net.Uri;

import java.util.List;

public class Viajes {
    private String nombre;
    private String descripcion;
    private String urlfoto;
    private int orden;




    public Viajes() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setUrlfoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }

    public Viajes(String nombre, String descripcion, String urlfoto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.urlfoto = urlfoto;
    }
}
