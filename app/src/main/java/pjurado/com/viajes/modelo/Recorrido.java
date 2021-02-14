package pjurado.com.viajes.modelo;

public class Recorrido {
    private String Origen;
    private String Destino;
    private String tiempo;
    private int distancia;

    public String getOrigen() {
        return Origen;
    }

    public void setOrigen(String origen) {
        Origen = origen;
    }

    public String getDestino() {
        return Destino;
    }

    public void setDestino(String destino) {
        Destino = destino;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public Recorrido(String origen, String destino, String tiempo, int distancia) {
        Origen = origen;
        Destino = destino;
        this.tiempo = tiempo;
        this.distancia = distancia;
    }
}

