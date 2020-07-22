package pjurado.com.viajes.modelo;

import java.util.List;

public class Lugares {
    private String nombre;
    private String descripcion;
    private String tiempoVisita;
    private String urlfoto;
    private String latitud;
    private String longitud;
    private String area;
    private String parking;
    private String enlaceInformacion;
    private List<String> viajes;


    public Lugares() {
    }

    public Lugares(String nombre, String descripcion, String tiempoVisita, String urlfoto, String latitud, String longitud, String area, String parking, String enlaceInformacion, List<String> viajes) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tiempoVisita = tiempoVisita;
        this.urlfoto = urlfoto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.area = area;
        this.parking = parking;
        this.enlaceInformacion = enlaceInformacion;
        this.viajes = viajes;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getEnlaceInformacion() {
        return enlaceInformacion;
    }

    public void setEnlaceInformacion(String enlaceInformacion) {
        this.enlaceInformacion = enlaceInformacion;
    }

    public List<String> getViajes() {
        return viajes;
    }

    public void setViajes(List<String> viajes) {
        this.viajes = viajes;
    }
}
