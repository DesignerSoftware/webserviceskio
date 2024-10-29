package co.com.designer.persistencia.interfaz;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioSoliciVacas {

    public boolean creaKioEstadoSolici(String seudonimo, String nit, String kioSoliciVaca,
            String fechaProcesa, String estado, String motivo, String cadena, String esquema);
    public String getSecPerAutorizadorXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena);
    
    public String getEmplJefeXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena);
    public String getFechaInicioXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena);
    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena, String esquema);
    public boolean creaKioNovedadSolici(String seudonimo, String nitEmpresa, String fechainicial, String fecharegreso, String dias, String RFVACACION, String fechaFin, String cadena, String esquema);
    public boolean creaKioSoliciVacas(String seudonimo, String secEmplJefe, String secPersonaAutorizador, String nit, String secNovedad, String fechaGeneracion, String cadena, String esquema);
    public String getSecuenciaKioNovedadesSolici(String seudonimo, String nitEmpresa,
            String fechainicio, String fecharegreso,
            String dias, String rfVacacion, String cadena, String esquema);
    public String getSecKioSoliciVacas(String secEmpl, String fechaGeneracion,
            String secEmplJefe, String secPerAutorizador, String kioNovedadSolici, String nitEmpresa, String cadena, String esquema);
}
