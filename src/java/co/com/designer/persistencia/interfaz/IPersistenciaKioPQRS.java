package co.com.designer.persistencia.interfaz;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioPQRS {
    public int crearPQRS(String seudonimo, String nit, String titulo, String mensaje, String cadena) throws Exception;
}
