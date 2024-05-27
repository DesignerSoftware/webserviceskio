package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaKioAusentismo_pkg;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 * @author Wilmer Uribe
 * @author Mateo Coronado
 */
public class PersistenciaKioAusentismo_pkg implements IPersistenciaKioAusentismo_pkg {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaKioAusentismo_pkg() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public String getMensajeValidacionAusent(String fechaInicio, String seudonimo,
            String nitEmpresa, String cadena, String esquemaP) {
        String resultado = null;
        String consulta = "SELECT "
                + "kioausentismo_pkg.MENSAJEVALIDACIONAUSENT"
                + "( ? , TO_DATE(?, 'DD/MM/YYYY'), ?) "
                + "from dual";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, seudonimo);
            query.setParameter(2, fechaInicio);
            query.setParameter(3, nitEmpresa);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getMensajeValidacionAusent(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public String getFechaFinAusent(String fechaInicio, String dias, String seudonimo, String causa, String nitEmpresa, String cadena, String esquemaP) {
        String resultado = null;
        String consulta = "select "
                + "TO_CHAR(TO_DATE(kioausentismo_pkg.CALCULAFECHAFINAUSENT"
                + "( ?, ?, ?, ? , ?)), 'DD/MM/YYYY') "
                + "from dual";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaInicio);
            query.setParameter(2, dias);
            query.setParameter(3, seudonimo);
            query.setParameter(4, causa);
            query.setParameter(5, nitEmpresa);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getFechaFinAusent(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }
    
    @Override
    public String getCausaFormaLiquidacion(String causa, String nitEmpresa, String cadena, String esquemaP) {
        String resultado = null;
        String consulta = "SELECT "
                + "kioausentismo_pkg.CAUSAFORMALIQUIDACION( ? ) "
                + "from dual";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, causa);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getCausaFormaLiquidacion(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }
    
    @Override
    public String getCausaPorcentajeLiquidacion(String causa, String nitEmpresa, String cadena, String esquemaP) {
        String resultado = null;
        String consulta = "SELECT "
                + "kioausentismo_pkg.CAUSAPORCENTAJELIQUIDACION( ? ) "
                + "from dual";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, causa);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getCausaPorcentajeLiquidacion(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }
}
