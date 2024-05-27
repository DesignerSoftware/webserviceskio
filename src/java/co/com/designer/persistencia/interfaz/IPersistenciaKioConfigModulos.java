package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioConfigModulos {
    public BigDecimal consultaAuditoria(String nombreModulo, String codigoOpc, String nitEmpresa, String cadena);
    public List<String> consultarCorreosAuditoria(String nombreModulo, String codigoOpc, String nitEmpresa, String cadena);
}
