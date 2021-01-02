package pjurado.com.viajes.modelo;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Viajes implements Serializable {
    private String nombre;
    private String descripcion;
    private String urlfoto;
    private ArrayList<PosicionLugarEnViaje> idLugares;


    public ArrayList<PosicionLugarEnViaje> getIdLugares() {
        return idLugares;
    }

    public void setIdLugares(ArrayList<PosicionLugarEnViaje> idLugares) {
        this.idLugares = idLugares;
    }

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
