package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
import javax.persistence.Query;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;

/**
 *
 * @author Edwin Hastamorir
 */
//@Stateless
public class PersistenciaCadenasKioskosApp implements IPersistenciaCadenasKioskosApp {

//    @EJB
    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;

    public PersistenciaCadenasKioskosApp() {
        rolesBD = new PersistenciaPerfiles();
        persistenciaConexiones = new PersistenciaConexiones();
    }

    @Override
    public String getEsquema(String nitEmpresa, String cadena) {
        System.out.println("Parametros getEsquema(): nitempresa: " + nitEmpresa + ", cadena: " + cadena);
        String esquema = "";
        String sqlQuery;
        try {
            sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=?";
            Query query = this.persistenciaConexiones.getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("Esquema: " + esquema);
        } catch (Exception e) {
            System.out.println("Error " + "PersistenciaCadenasKioskosApp" + ".getEsquema(nitEmpresa, cadena): " + e);
        }
        return esquema;
    }

}
