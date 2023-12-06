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
public class PersistenciaEmpresas implements IPersistenciaEmpresas{
    
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
        String esquema = null;
        try {
            esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("crearPqrs: Error: No se pudo consultar esquema. " + e.getMessage());
            e.printStackTrace();
        }
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EM.SECUENCIA SECUENCIAEMPRESA FROM EMPRESAS EM WHERE EM.NIT = ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            String vcSecuencia = query.getSingleResult().toString();
            System.out.println("secuencia: " + vcSecuencia);
            secuencia = new BigDecimal(vcSecuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecuenciaPorNitEmpresa: " + e.getMessage());
        }
        return secuencia;
    }
}
