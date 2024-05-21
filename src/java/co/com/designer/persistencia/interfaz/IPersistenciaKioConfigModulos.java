package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioConfigModulos {
    public BigDecimal consultaAuditoria(String nombreModulo, String codigoOpc, String nitEmpresa, String cadena);
}
