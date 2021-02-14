package pjurado.com.viajes.modelo;

import java.util.ArrayList;
import java.util.List;

public class Lugares {
    private String nombre;
    private String descripcion;
    private String tiempoVisita;
    private String urlfoto;
    private String latitud;
    private String longitud;
    private ArrayList<AreasyParkings> areas;

    public ArrayList<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<String> usuarios) {
        this.usuarios = usuarios;
    }

    private ArrayList<AreasyParkings> parking;
    private ArrayList<AreasyParkings> informacion;
    private String viaje;
    private ArrayList<String> usuarios;


    public Lugares() {
    }

    public ArrayList<AreasyParkings> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<AreasyParkings> areas) {
        this.areas = areas;
    }

    public ArrayList<AreasyParkings> getParking() {
        return parking;
    }

    public void setParking(ArrayList<AreasyParkings> parking) {
        this.parking = parking;
    }

    public ArrayList<AreasyParkings> getInformacion() {
        return informacion;
    }

    public void setInformacion(ArrayList<AreasyParkings> informacion) {
        this.informacion = informacion;
    }

    public Lugares(String nombre, String descripcion, String tiempoVisita, String urlfoto, String latitud, String longitud, ArrayList<AreasyParkings> areas, ArrayList<AreasyParkings> parking, ArrayList<AreasyParkings> informacion, String viaje) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tiempoVisita = tiempoVisita;
        this.urlfoto = urlfoto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.areas = areas;
        this.parking = parking;
        this.informacion = informacion;
        this.viaje = viaje;
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

    public String getTiempoVisita() {
        return tiempoVisita;
    }

    public void setTiempoVisita(String tiempoVisita) {
        this.tiempoVisita = tiempoVisita;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setUrlfoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getViaje() {
        return viaje;
    }

    public void setViaje(String viaje) {
        this.viaje = viaje;
    }
}
