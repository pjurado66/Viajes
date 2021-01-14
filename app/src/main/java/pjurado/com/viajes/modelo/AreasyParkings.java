package pjurado.com.viajes.modelo;

public class AreasyParkings {
    String url;
    Boolean esExtraible;
    String titulo;
    String latitud;
    String longitud;

    public AreasyParkings(String url, Boolean esExtraible) {
        this.url = url;
        this.esExtraible = esExtraible;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getEsExtraible() {
        return esExtraible;
    }

    public void setEsExtraible(Boolean esExtraible) {
        this.esExtraible = esExtraible;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public AreasyParkings() {
    }

    public AreasyParkings(String url, Boolean esExtraible, String titulo, String latitud, String longitud) {
        this.url = url;
        this.esExtraible = esExtraible;
        this.titulo = titulo;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
