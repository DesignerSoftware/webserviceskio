package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpresas;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaEmpresas implements IPersistenciaEmpresas {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaEmpresas() {
        rolesBD = new PersistenciaPerfiles();
        persistenciaConexiones = new PersistenciaConexiones();
        cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public BigDecimal getSecuenciaPorNitEmpresa(String nitEmpresa, String cadena) {
        BigDecimal secuencia = null;
        String sqlQuery = "SELECT EM.SECUENCIA SECUENCIAEMPRESA FROM EMPRESAS EM WHERE EM.NIT = ? ";
        System.out.println("PersistenciaEmpresas" + ".getSecuenciaPorNitEmpresa(): " + "Query: " + sqlQuery);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            String vcSecuencia = query.getSingleResult().toString();
            System.out.println("PersistenciaEmpresas" + ".getSecuenciaPorNitEmpresa(): " + "secuencia: " + secuencia);
            secuencia = new BigDecimal(vcSecuencia);
        } catch (Exception e) {
            System.out.println("PersistenciaEmpresas" + ".getSecuenciaPorNitEmpresa(): " + "Error: " + e.toString());
        }
        return secuencia;
    }

    @Override
    public String getLogoEmpresa(String nitEmpresa, String cadena) { 
        String logo = "";
        String sqlQuery = "SELECT LOGO FROM EMPRESAS WHERE NIT = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            logo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaEmpresas" + ".getLogoEmpresa(): "+"Error-1: " + e.toString());
        }
        return logo;
    }
}
