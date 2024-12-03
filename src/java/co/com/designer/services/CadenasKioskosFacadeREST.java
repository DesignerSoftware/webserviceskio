package co.com.designer.services;

import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConexiones;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Thalia Manrique
 * @author Edwin Hastamorir
 */
@Stateless
@Path("cadenaskioskos")
public class CadenasKioskosFacadeREST {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public CadenasKioskosFacadeREST() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persisConexiones = new PersistenciaConexiones();
    }
    
    @GET
    @Path("{grupo}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCadenas(@PathParam("grupo") String grupo, @QueryParam("dominio") String dominio) {
        List s = null;
        System.out.println("Parametros: Grupo: " + grupo + ", Dominio: " + dominio);
        try {
            String sqlQuery = "SELECT CODIGO, DESCRIPCION, NITEMPRESA, GRUPO, CADENA, EMPLNOMINA, ESQUEMA, ESTADO, OBSERVACION "
                    + "FROM CADENASKIOSKOSAPP "
                    + "WHERE GRUPO=? and ? LIKE '%'||DOMINIO||'%'";
            Query query = this.persisConexiones.getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, grupo);
            query.setParameter(2, dominio);
            s = query.getResultList();
            System.out.println("1" + s.get(0));
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + this.getClass().getName() + " getCadenas():" + ex);
            return Response.status(Response.Status.OK).entity(s).build();
        }
    }

    /**
     * Devuelve todos los campos de la tabla cadenaskioskosapp
     *
     * @param grupo nombre del grupo empresarial asignado para la URL
     * @param nitEmpresa nit de la empresa a la que pertenece el empleado
     * conectado
     * @return List con los campos de la tabla cadenaskioskosapp
     */
    @GET
    @Path("{grupo}/{empresa}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCadenasXGrupoNit(@PathParam("grupo") String grupo, @PathParam("empresa") String nitEmpresa) {
        List s = null;
        System.out.println("Parametros: Grupo: " + grupo + ", nit: " + nitEmpresa);
        if ( grupo == null || nitEmpresa == null 
                || "".equalsIgnoreCase(grupo) || "".equalsIgnoreCase(nitEmpresa) ) {
            return Response.status(Response.Status.OK).entity(s).build();
        } else {
            try {
                String sqlQuery = "SELECT CODIGO, DESCRIPCION, NITEMPRESA, GRUPO, CADENA, EMPLNOMINA, ESQUEMA, ESTADO, OBSERVACION "
                        + "FROM CADENASKIOSKOSAPP "
                        + "WHERE GRUPO=? AND NITEMPRESA=?";
                Query query = this.persisConexiones.getEntityManager().createNativeQuery(sqlQuery);
                query.setParameter(1, grupo);
                query.setParameter(2, nitEmpresa);
                s = query.getResultList();
                System.out.println("1" + s.get(0));
                s.forEach(System.out::println);
                return Response.status(Response.Status.OK).entity(s).build();
            } catch (Exception ex) {
                System.out.println("Error: " + this.getClass().getName() + " getCadenas():" + ex);
                return Response.status(Response.Status.OK).entity(s).build();
            }
        }
    }

    @GET
    @Path("validaGrupoInactivo/{grupo}")
    @Produces(MediaType.TEXT_PLAIN)
    public Long getValidaEstadoInactivoXGrupo(@PathParam("grupo") String grupo) {
        Long res = null;
        System.out.println("Parametros: Grupo: " + grupo);
        try {
            String sqlQuery = "SELECT COUNT(*) "
                    + "FROM CADENASKIOSKOSAPP "
                    + "WHERE GRUPO=? "
                    + "AND ESTADO='INACTIVO' ";
            Query query = this.persisConexiones.getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, grupo);
            res = (Long) query.getSingleResult();
            System.out.println("count(*) inactivo: " + res);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            res = Long.valueOf(0);
        }
        return res;
    }

}
