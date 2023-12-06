package co.com.designer.persistencia.interfaz;

import javax.persistence.EntityManager;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaConexiones {
    public EntityManager getEntityManager();
    public EntityManager getEntityManager(String persistence);
}
