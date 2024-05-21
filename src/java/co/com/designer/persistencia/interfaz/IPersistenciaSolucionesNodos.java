package co.com.designer.persistencia.interfaz;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaSolucionesNodos {
    public List getSaldoProvisiones(String empleado, String nit, String cadena);
    public Date getFechaUltimoPago(String seudonimo, String nitEmpresa, String cadena, String esquema) throws Exception;
}
