package co.com.designer.persistencia.interfaz;

import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaVacaPendientes {
    public List<VwVacaPendientesEmpleados> getPeriodosPendientesEmpleado(String seudonimo, String nitEmpresa, String cadena);
    public List<VwVacaPendientesEmpleados> getPeriodoMasAntiguo(String seudonimo, String nitEmpresa, String cadena);
    public BigDecimal getDiasPendPeriodoMasAntiguo(String seudonimo, String nitEmpresa, String cadena);
    public BigDecimal getDiasVacacionesPeriodosCumplidos(String seudonimo, String nitEmpresa, String cadena);
    public BigDecimal getDiasVacacionesSolicitados(BigDecimal documento, String nitEmpresa, String estado, String cadena);
    
    
    public String getPeriodoVacas(String secEmpleado, String refVacas, String cadena, String nitEmpresa);
}
