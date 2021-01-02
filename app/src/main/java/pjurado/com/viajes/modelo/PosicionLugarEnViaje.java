package pjurado.com.viajes.modelo;

public class PosicionLugarEnViaje {
    private String id;
    private int posicion;

    public PosicionLugarEnViaje(String id, int posicion) {
        this.id = id;
        this.posicion = posicion;
    }

    public PosicionLugarEnViaje() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
