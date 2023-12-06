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
        System.out.println("setearPerfil()");
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
    }
    
    @Override
    public void setearPerfil(String cadena) {
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
    }

    @Override
    public void setearPerfil(String esquema, String cadenaPersistencia) {
        System.out.println("setearPerfil(esquema, cadena)");
        try {
            String rol = "ROLKIOSKO";
            if (esquema != null && !esquema.isEmpty()) {
                System.out.println("esquema no es nulo ni vacio");
                rol = rol + esquema.toUpperCase();
            }
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            System.out.println(sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadenaPersistencia).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error " 
                    + "PersistenciaPerfiles" 
                    + ".setearPerfil(esquema, cadenaPersistencia): " 
                    + ex 
            );
        }
    }
}
