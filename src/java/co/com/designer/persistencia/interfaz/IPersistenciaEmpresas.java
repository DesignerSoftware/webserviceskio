package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaEmpresas {
    public BigDecimal getSecuenciaPorNitEmpresa(String nitEmpresa, String cadena);
}
