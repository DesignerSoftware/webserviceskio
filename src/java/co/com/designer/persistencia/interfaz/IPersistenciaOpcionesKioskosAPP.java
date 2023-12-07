package co.com.designer.persistencia.interfaz;

import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaOpcionesKioskosAPP {
    
    /**
     * Retorna true si el usuario esta activo
     * @param nitEmpresa
     * @param secuencia
     * @param cadena
     * @return 
     */
    public List getOpciones(String nitEmpresa, String secuencia, String cadena);
    
    /**
     * Retorna la lista de opcionesKioskosApp de acuerdo a la empresa y a los roles detectados.
     * @param nitEmpresa
     * @param cadena
     * @param roles
     * @return 
     */
    public List buscarTodos(String nitEmpresa, String cadena, String roles);
}
