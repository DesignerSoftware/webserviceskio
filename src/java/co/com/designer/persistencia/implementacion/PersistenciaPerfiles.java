package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import javax.persistence.Query;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaPerfiles implements IPersistenciaPerfiles {

    private IPersistenciaConexiones persistenciaConexiones;

    public PersistenciaPerfiles() {
        this.persistenciaConexiones = new PersistenciaConexiones();
    }

    @Override
    public void setearPerfil(String esquema, String cadena) {

        String rol = "ROLKIOSKO";
        if (esquema != null && !esquema.isEmpty()) {
            rol = rol + esquema.toUpperCase();
        }
        String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
        try {
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("PersistenciaPerfiles" + ".setearPerfil(): " + "Error " + ex.toString());
            throw ex;
        }
    }
}
