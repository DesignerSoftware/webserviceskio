package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioVacaciones_pkg {

    /**
     * Método que valida si la fecha de disfrute que recibe como parametro ya
     * tiene una solicitud asociada
     *
     * @param seudonimo
     * @param nitEmpresa
     * @param fechaIniVaca
     * @param cadena
     * @return
     * @throws java.lang.Exception
     */
    public BigDecimal verificaExistenciaSolicitud(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca, String cadena) throws Exception;

    public BigDecimal consultaTraslapamientos(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca,
            String fechaFinVaca, String cadena) throws PersistenceException, NullPointerException, Exception;
    
    public List getFechaRegreso(
            String fechainicio, 
            int dias, 
            String seudonimo, 
            String nitEmpresa, 
            String cadena, 
            String esquema);
    
    public Timestamp getFechaFinVaca(String fechainicio, String fechafin, int dias, String seudonimo, String nitEmpresa, String cadena, String esquema);
}
