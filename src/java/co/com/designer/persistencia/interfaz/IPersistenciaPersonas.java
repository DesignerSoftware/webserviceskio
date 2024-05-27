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
    public String getCorreoConexioneskioskos(String seudonimo, String nitEmpresa, String cadena);
    /**
     * Método que proporciona el correo electrónico de un empleado que está registrado en nómina, 
     * haciendo uso de la secuencia del empleado y el NIT de la empresa.
     * @param secEmpleado Secuencia del empleado
     * @param nitEmpresa NIT de la empresa
     * @param cadena Cadena de conexión configurada para la empresa 
     * @return correo electrónico del empleado.
     */
    public String getCorreoPorEmpleado(String secEmpleado, String nitEmpresa, String cadena);
    
    /**
     * Método que proporciona el correo electrónico de una persona que está registrado en nómina, 
     * haciendo uso de la secuencia de la persona.
     * El NIT de la empresa y la cadena de conexión se utiliza para obtener el esquema de confiración.
     * 
     * @param secPersona
     * @param nitEmpresa
     * @param cadena
     * @return 
     */
    public String getCorreoPorPersona(String secPersona, String nitEmpresa, String cadena);
}
