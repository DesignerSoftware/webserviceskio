package co.com.designer.kiosko.generales;

/**
 *
 * @author Edwin Hastamorir
 */
public class ArchivoCorreo {
    private String ruta;
    private String nombre;
    private String tipo;
    private String tipoMimeContenido;

    public ArchivoCorreo() {
    }
    
    public ArchivoCorreo(String ruta, String nombre, String tipo, String tipoMimeContenido) {
        this.ruta = ruta;
        this.nombre = nombre;
        this.tipo = tipo;
        this.tipoMimeContenido = tipoMimeContenido;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    

    public String getTipoMimeContenido() {
        return tipoMimeContenido;
    }

    public void setTipoMimeContenido(String tipoMimeContenido) {
        this.tipoMimeContenido = tipoMimeContenido;
    }
    
    
}

