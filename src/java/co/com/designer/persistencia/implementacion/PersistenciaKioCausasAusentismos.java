package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaKioCausasAusentismos;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioCausasAusentismos implements IPersistenciaKioCausasAusentismos {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaKioCausasAusentismos() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public List getKioCausasAusentismos(String nitEmpresa, String cadena) {
        List resultado = null;
        String consulta = "SELECT ka "
                + " FROM KioCausasAusentismos ka "
                + " WHERE "
                + " ka.empresa.nit= :nitempresa ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createQuery(consulta);
            query.setParameter("nitempresa", Long.valueOf(nitEmpresa));
            resultado = query.getResultList();
            return resultado;
        } catch (NumberFormatException nfe) {
            System.out.println("PersistenciaVacaPendientes" + ".getPeriodosPendientesEmpleado(): " + "Error-1: " + nfe.toString());
            return null;
        } catch (Exception e) {
            System.out.println("PersistenciaVacaPendientes" + ".getPeriodosPendientesEmpleado(): " + "Error-2: " + e.toString());
            return null;
        }
    }

}
