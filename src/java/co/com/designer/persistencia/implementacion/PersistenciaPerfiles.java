package co.com.designer.persistencia.implementacion;

//import javax.ejb.Stateless;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import javax.persistence.Query;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;

/**
 *
 * @author Edwin Hastamorir
 */
//@Stateless
public class PersistenciaPerfiles implements IPersistenciaPerfiles {

    private IPersistenciaConexiones persistenciaConexiones;

    public PersistenciaPerfiles() {
        this.persistenciaConexiones = new PersistenciaConexiones();
    }

    @Override
    public void setearPerfil() {
        System.out.println("PersistenciaPerfiles" + ".setearPerfil()-1: " );
//        System.out.println("setearPerfil()");
        /*
        try {
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = this.persistenciaConexiones.getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error "
                    + this.getClass().getName()
                    + ".setearPerfil(): "
                    + ex
            );
        }
        */
        this.setearPerfil("", "");
    }

    @Override
    public void setearPerfil(String cadena) {
        /*System.out.println("PersistenciaPerfiles" + ".setearPerfil()-2: " + "Parametros: "
                + "esquema: " + esquema
                + " , cadenaPersistencia: " + cadenaPersistencia
        );*/
        /*
        System.out.println("setearPerfil(cadena)");
        try {
            System.out.println("setearPerfil(cadena)");
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error "
                    + this.getClass().getName()
                    + ".setearPerfil(cadena): "
                    + ex
            );
        }
        */
        this.setearPerfil("", cadena);
    }

    @Override
    public void setearPerfil(String esquema, String cadena) {
        /*System.out.println("PersistenciaPerfiles" + ".setearPerfil()-3: " + "Parametros: "
                + "esquema: " + esquema
                + " , cadenaPersistencia: " + cadenaPersistencia
        );*/
        String rol = "ROLKIOSKO";
        if (esquema != null && !esquema.isEmpty()) {
//                System.out.println("esquema no es nulo ni vacio");
            rol = rol + esquema.toUpperCase();
        }
        String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
//        System.out.println(sqlQuery);
//        System.out.println("PersistenciaPerfiles" + ".setearPerfil(): " + "sqlQuery " + sqlQuery);
        try {
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("PersistenciaPerfiles" + ".setearPerfil(): " + "Error " + ex.toString());
        }
    }
}
