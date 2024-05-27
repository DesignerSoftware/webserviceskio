package co.com.designer.persistencia.interfaz;

import co.com.designer.kiosko.entidades.ConfiCorreoKiosko;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaConfiCorreoKiosko {
    public ConfiCorreoKiosko obtenerConfiguracionCorreoNativo(String nit, String cadena);
    public ConfiCorreoKiosko obtenerConfiguracionCorreo(String nit, String cadena);
}
