package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import javax.persistence.Query;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaCadenasKioskosApp implements IPersistenciaCadenasKioskosApp {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;

    public PersistenciaCadenasKioskosApp() {
        rolesBD = new PersistenciaPerfiles();
        persistenciaConexiones = new PersistenciaConexiones();
    }

    @Override
    public String getEsquema(String nitEmpresa, String cadena) {
        System.out.println("PersistenciaCadenasKioskosApp" + ".getEsquema(): " + "Parametros: "
                +"nitempresa: " + nitEmpresa 
                + ", cadena: " + cadena);
        String esquema = "";
        String sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=? ";
        try {
            Query query = this.persistenciaConexiones.getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("PersistenciaCadenasKioskosApp" + ".getEsquema(): " + "Esquema consultado.");
        } catch (Exception e) {
            System.out.println("Error: "+"PersistenciaCadenasKioskosApp" + ".getEsquema(): " + e.toString());
            throw e;
        }
//        System.out.println("PersistenciaCadenasKioskosApp" + ".getEsquema(): " + "Esquema: " + esquema);
        return esquema;
    }

}
