package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaKioConfigModulos;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioConfigModulos implements IPersistenciaKioConfigModulos {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaKioConfigModulos() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public BigDecimal consultaAuditoria(String nombreModulo, String codigoOpc, String nitEmpresa, String cadena) {
        BigDecimal retorno = null;
        String query1 = "SELECT COUNT(*) "
                + "FROM KioConfigModulos "
                + "WHERE nombreModulo = ? "
                + "AND codigoOpcion = ? "
                + "AND nitEmpresa = ? ";
        System.out.println("PersistenciaKioConfigModulos" + ".consultaAuditoria(): query1: " + query1);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(query1);
            query.setParameter(1, nombreModulo);
            query.setParameter(2, codigoOpc);
            query.setParameter(3, nitEmpresa);
            retorno = (BigDecimal) query.getSingleResult();
            System.out.println("PersistenciaKioConfigModulos" + ".consultaAuditoria(): retorno: " + retorno);
        } catch (Exception e) {
            System.out.println("PersistenciaKioConfigModulos" + ".consultaAuditoria(): Error: " + e.toString());
        }
        return retorno;
    }

    @Override
    public List<String> consultarCorreosAuditoria(String nombreModulo, String codigoOpc, String nitEmpresa, String cadena) {
        List<String> retorno = null;
        String query1 = "SELECT email "
                + "FROM KioConfigModulos "
                + "WHERE nombreModulo = ? "
                + "AND codigoOpcion = ? "
                + "AND nitEmpresa = ? ";
        System.out.println("PersistenciaKioConfigModulos" + ".consultaAuditoria(): query1: " + query1);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(query1);
            query.setParameter(1, nombreModulo);
            query.setParameter(2, codigoOpc);
            query.setParameter(3, nitEmpresa);
            retorno = query.getResultList();
        } catch (Exception e) {
            System.out.println("PersistenciaKioConfigModulos" + ".consultarCorreoAuditoria(): Error: " + e.toString());
        }
        return retorno;
    }
}
