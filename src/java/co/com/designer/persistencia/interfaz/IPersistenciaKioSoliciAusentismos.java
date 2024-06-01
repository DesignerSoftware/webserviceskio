package co.com.designer.persistencia.interfaz;

import co.com.designer.kiosko.entidades.IntervinientesSolAusent;
import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaKioSoliciAusentismos {

    public List getSolicitudesPorEstado(String nitEmpresa, String cadena, String secEmpl, String estado);

    public List getSolicitudesSinProcesarPorJefe(String nitEmpresa, String cadena, String estado, String secuenciaJefe);

    public List getSolicitudesPorJefe(String nitEmpresa, String cadena, String secuenciaJefe);
    
    public List getSolicitudesPorAutorizador(String nitEmpresa, String cadena, String secuenciaAutorizador);
    
    public List getSolicitudesSinProcesarPorAutorizador(String nitEmpresa, String cadena, String estado, String secAutorizador);

    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena);
    
    public IntervinientesSolAusent getIntervinientesPorEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena );

    public String getSecuenciaSolicitudAusentismo(String secEmpleado, String fechaGeneracion
            , String secEmplJefe , String secAutorizador 
            , String nitEmpresa, String cadena, String esquemaP);

    //public int creaSolicitudAusentismo(String seudonimo, String secEmplJefe, String nitEmpresa, String nombreAnexo, String fechaGeneracion, String fechainicial, String fechaFin, String dias, String observacion, String secCausaAusent, String cadena, String esquemaP);
    public int creaSolicitudAusentismo(String seudonimo, String secEmplJefe, String secAutorizador, String nitEmpresa, String nombreAnexo, String fechaGeneracion, String fechainicial, String fechaFin, String dias, String observacion, String secCausaAusent, String cadena, String esquemaP);

    public int creaEstadoSolicitudAusent(String seudonimo, String nitEmpresa, String kioSoliciAusentismo,
            String fechaProcesa, String estado, String motivo, String cadena, String esquemaP);
    
    public int creaEstadoSolicitudAusent(String nitEmpresa, String cadena, String fechaGeneracion, String estado, String secEmplEjecuta,
            String secPerAutoriza, String secKioEstadoSolici, String motivo, String esquemaP);

    public int creaEstadoSolicitudAusent(String nitEmpresa, String cadena, String fechaGeneracion, String estado, String secEmplEjecuta,
            String secPerAutoriza, String secKioEstadoSolici);

    public int creaNovedadSoliciAusent(String seudonimo, String nitEmpresa, String fechainicial,
            String secTipo, String secClase, String secCausa, String secCodDiagnostico,
            int dias, String fechaFin, String kioSoliciAusentismo, String secKioNovedadSoliciAusent, String formaLiq, String porcentajeLiq,
            String cadena, String esquemaP);
            
    public String getEmplJefeXsecKioSoliciAusentismo(String secKioSoliciAusentismo, String nitEmpresa, String cadena);

    public List getDetalleAusentismo(String secKioSoliciAusentismo, String nitEmpresa, String cadena);

    public int updateAnexoKioSoliciAusentismo(String seudonimo, String nitEmpresa,
            String nombreAnexo, String secKioSoliciAusentismo, String cadena);

    public String getSecuenciaJefeEstadoSolici(String secKioEstadoSoliciAus, String nitEmpresa, String cadena);

    public List getProrroga(String empleado, String causa, String fechaInicial, String nitEmpresa, String cadena, String esquemaP);
}
