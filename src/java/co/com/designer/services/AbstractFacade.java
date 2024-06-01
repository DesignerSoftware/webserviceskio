package co.com.designer.services;

import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.List;

/**
 *
 * @author Thalia Manrique
 * @param <T>
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;
    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    public String esquema;
    public String cadena;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.rolesBD = new PersistenciaPerfiles();
    }

    public void create(T entity) {
        this.rolesBD.setearPerfil(esquema, cadena);
        this.persistenciaConexiones.getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        this.rolesBD.setearPerfil(esquema, cadena);
        this.persistenciaConexiones.getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        this.rolesBD.setearPerfil(esquema, cadena);
        this.persistenciaConexiones.getEntityManager().remove(this.persistenciaConexiones.getEntityManager().merge(entity));
    }

    public T find(Object id) {
        this.rolesBD.setearPerfil(esquema, cadena);
        return this.persistenciaConexiones.getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        this.rolesBD.setearPerfil(esquema, cadena);
        javax.persistence.criteria.CriteriaQuery cq = this.persistenciaConexiones.getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return this.persistenciaConexiones.getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        this.rolesBD.setearPerfil(esquema, cadena);
        javax.persistence.criteria.CriteriaQuery cq = this.persistenciaConexiones.getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = this.persistenciaConexiones.getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        this.rolesBD.setearPerfil(esquema, cadena);
        javax.persistence.criteria.CriteriaQuery cq = this.persistenciaConexiones.getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(this.persistenciaConexiones.getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = this.persistenciaConexiones.getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
}
