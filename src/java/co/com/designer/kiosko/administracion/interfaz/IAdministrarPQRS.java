package co.com.designer.kiosko.administracion.interfaz;

import org.json.JSONObject;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IAdministrarPQRS {
    /**
     * Método para crear un registro en PQRS incluyendo el envio por correo.
     * @param seudonimo
     * @param nit
     * @param titulo
     * @param mensaje
     * @param cadena
     * @return Mensaje de confirmación de la creación
     */
    public JSONObject crearPQRS(String seudonimo, String nit, String titulo, String mensaje, String cadena, String url);
}
