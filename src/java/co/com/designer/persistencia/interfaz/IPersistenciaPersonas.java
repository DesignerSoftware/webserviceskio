package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaPersonas {
    public String consultarCorreoPersonaEmpresa(String documento, String nitEmpresa, String cadena);
    /*
     * Método que devuelve el nombre de la persona de acuerdo a la secuencia de la tabla personas
     * @param secPersona secuencia de la tabla personas
     * @return el apellido||' '||nombre de la persona
     */
    public String getApellidoNombreXSecPer(String secPersona, String nitEmpresa, String cadena, String esquema);
    public String getApellidoNombreXsecEmpl(String secEmpl, String nitEmpresa, String cadena, String esquema);
}
