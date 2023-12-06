package co.com.designer.persistencia.interfaz;

import javax.ejb.Local;

/**
 *
 * @author Edwin Hastamorir
 */
@Local
public interface IPersistenciaCadenasKioskosApp {
    public String getEsquema(String nitEmpresa, String cadena);
}
