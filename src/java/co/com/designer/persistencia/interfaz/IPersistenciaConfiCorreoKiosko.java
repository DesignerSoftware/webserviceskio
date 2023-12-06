package co.com.designer.persistencia.interfaz;

import co.com.designer.kiosko.entidades.ConfiCorreoKiosko;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaConfiCorreoKiosko {
    public ConfiCorreoKiosko obtenerServidorCorreo(String nit, String cadena);
}
