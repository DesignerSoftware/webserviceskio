package co.com.designer.persistencia.interfaz;

/**
 *
 * @author Edwin Hastamorir
 * @author Wilmer Uribe
 * @author Mateo Coronado
 */
public interface IPersistenciaKioAusentismo_pkg {

    public String getMensajeValidacionAusent(String fechaInicio, String seudonimo,
            String nitEmpresa, String cadena, String esquemaP);

    public String getFechaFinAusent(String fechaInicio, String dias, String seudonimo, String causa, String nitEmpresa, String cadena, String esquemaP);
    
    public String getCausaFormaLiquidacion(String causa, String nitEmpresa, String cadena, String esquemaP);
    
    public String getCausaPorcentajeLiquidacion(String causa, String nitEmpresa, String cadena, String esquemaP);
}
