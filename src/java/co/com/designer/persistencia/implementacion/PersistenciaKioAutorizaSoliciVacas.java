package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaKioAutorizaSoliciVacas;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioAutorizaSoliciVacas implements IPersistenciaKioAutorizaSoliciVacas {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaKioAutorizaSoliciVacas() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    
    @Override
    public String consultarSecuenciaPorAutorizadorVaca(String secEmpleado, String nitEmpresa, String cadena, String esquemaP) throws Exception {
        String secAutorizador = null;
        String sqlQuery = "select per.secuencia "
                + "from empleados empl, kioautorizadores ka, kioautorizasolicivacas kasv, personas per, KioModulos m "
                + "where empl.secuencia = kasv.empleado "
                + "and kasv.kioautorizador = ka.secuencia "
                + "and per.secuencia = ka.persona "
                + "and m.secuencia = ka.kiomodulo "
                + "and m.codigo = 1 "
                + "and empl.secuencia = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            secAutorizador = query.getSingleResult().toString();
            System.out.println("secAutorizador: " + secAutorizador);
        } catch (Exception e) {
            System.out.println("PersistenciaKioAutorizaSoliciVacas" + ".consultarSecuenciaPorAutorizadorVaca: " + "Error: " + e.getMessage());
        }
        return secAutorizador;
    }
    
    @Override
    public String consultarSecuenciaPorAutorizador(String secEmpleado, String nitEmpresa, String cadena, String esquemaP, int kioModulo) throws Exception {
        String secAutorizador = null;
        String sqlQuery = "select per.secuencia "
                + "from empleados empl, kioautorizadores ka, kioautorizasolicivacas kasv, personas per, KioModulos m "
                + "where empl.secuencia = kasv.empleado "
                + "and kasv.kioautorizador = ka.secuencia "
                + "and per.secuencia = ka.persona "
                + "and m.secuencia = ka.kiomodulo "
                + "and m.codigo = ? "
                + "and empl.secuencia = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioModulo);
            query.setParameter(2, secEmpleado);
            secAutorizador = query.getSingleResult().toString();
            System.out.println("PersistenciaKioAutorizaSoliciVacas" + ".consultarSecuenciaPorAutorizador: " + "secAutorizador: " + secAutorizador);
        } catch (Exception e) {
            System.out.println("PersistenciaKioAutorizaSoliciVacas" + ".consultarSecuenciaPorAutorizador: " + "Error: " + e.toString());
        }
        return secAutorizador;
    }
}
