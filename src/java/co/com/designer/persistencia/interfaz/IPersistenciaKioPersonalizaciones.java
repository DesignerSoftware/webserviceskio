package co.com.designer.persistencia.interfaz;

import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioPersonalizaciones {
    public List getCorreosComiteConvivencia(String nit, String cadena) throws Exception;
    public String getCorreoContacto(String nitEmpresa, String cadena);
    public List getDatosContactoKioscoNomina (String nit, String cadena, String esquemaP) throws Exception;
    public List getDatosContactoKiosco (String nit, String cadena, String esquemaP) throws Exception;
    public List getCorreosContactosNomina(String nit, String cadena, String esquemaP) throws Exception;
}
