package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 *
 * @author UPC044
 */
public class PersistenciaConexiones implements IPersistenciaConexiones {
    
    @Override
    public EntityManager getEntityManager() {
        String unidadPersistencia = "wsreportePU";
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }

    @Override
    public EntityManager getEntityManager(String persistence) {
        String unidadPersistencia = persistence;
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }
}
