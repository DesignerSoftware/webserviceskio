package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaPersonas;
import java.math.BigDecimal;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaPersonas implements IPersistenciaPersonas {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    
    public PersistenciaPersonas() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persistenciaConexiones = new PersistenciaConexiones();
    }
    
    @Override
    public String consultarCorreoPersonaEmpresa(String documento, String nitEmpresa, String cadena) {
        System.out.println("Parametros consultarCorreoPersonaEmpresa(): documento: " + documento + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String datos = null;
        try {
            String sqlQuery = "SELECT lower(P.EMAIL) email "
                    + "FROM EMPLEADOS e, Empresas em, personas p "
                    + "WHERE e.empresa = em.secuencia "
                    + "and p.secuencia=e.persona "
                    + "AND p.numerodocumento = ? "
                    + "AND em.nit = ? "
                    + "AND (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'ACTIVO' "
                    + "OR EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'PENSIONADO')";
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);
            datos = query.getSingleResult().toString();
        } catch (Exception e) {
        }
        return datos;
    }
}
