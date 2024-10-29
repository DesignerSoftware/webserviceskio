package co.com.designer.kiosko.excepciones;

/**
 *
 * @author Edwin Hastamorir
 */
public class EMailExcepcion extends Exception {
    private String mensaje;

    public EMailExcepcion() {
    }

    public EMailExcepcion(String mensaje) {
        super(mensaje);
        this.mensaje = mensaje;
    }
    
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
}

