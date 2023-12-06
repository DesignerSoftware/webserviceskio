package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaEmpleados {
    public BigDecimal getDocumentoXUsuario(String cadena, String usuario);
    public boolean validarCodigoUsuario(String usuario);
}
