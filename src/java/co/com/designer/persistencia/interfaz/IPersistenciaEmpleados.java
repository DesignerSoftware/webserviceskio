package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaEmpleados {
    public BigDecimal getDocumentoXUsuario(String cadena, String usuario);
    public boolean validarCodigoUsuario(String usuario);
    public List getDatosEmpleadoNit(String empleado, String nit, String cadena);
    public String getSecEmplPorCodigo(String codigo, String nitEmpresa, String cadena);
    public String getSecEmplPorDocumentoYEmpresa(String documento, String nitEmpresa, String cadena);
}
