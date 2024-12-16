package co.com.designer.persistencia.interfaz;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioAutorizaSoliciVacas {
    /**
     * Creado 02/06/2021
     * 
     * @param secEmpleado
     * @param nitEmpresa
     * @param cadena
     * @param esquema
     * @return
     * @throws Exception 
     */
    public String consultarSecuenciaPorAutorizadorVaca(String secEmpleado, String nitEmpresa, String cadena, String esquema) throws Exception;
    public String consultarSecuenciaPorAutorizador(String secEmpleado, String nitEmpresa, String cadena, String esquemaP, int kioModulo) throws Exception;
    public String consultarSecuenciaPorAutorizadorAus(String secEmpleado, String nitEmpresa, String cadena, String esquemaP, int kioModulo) throws Exception;
}
