/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.services;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
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
 * @author thali
 */
@Stateless
@Path("cadenaskioskos")
public class CadenasKioskosFacadeREST {

    protected EntityManager getEntityManager() {
        String unidadPersistencia="wscadenaskioskosPU";
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }
    
    protected EntityManager getEntityManager(String persistence) {
        String unidadPersistencia=persistence;
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }
    
    protected void setearPerfil() {
        try {
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("ex: " + ex);
        }
    }
    
    @GET
    @Path("{grupo}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCadenas(@PathParam("grupo") String grupo) {
        List s = null;
        System.out.println("Parametros: Grupo: " + grupo);
        try {
            String sqlQuery = "SELECT CODIGO, DESCRIPCION, NITEMPRESA, GRUPO, CADENA, EMPLNOMINA, ESQUEMA, ESTADO, OBSERVACION FROM CADENASKIOSKOSAPP WHERE GRUPO=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, grupo);
            s = query.getResultList();
            System.out.println("1" + s.get(0));
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: "+this.getClass().getName()+" getCadenas():" + ex);
            return Response.status(Response.Status.OK).entity(s).build();
        }
    }
    
    /**
     * Devuelve todos los campos de la tabla cadenaskioskosapp
     * @param grupo nombre del grupo empresarial asignado para la URL
     * @param nitEmpresa nit de la empresa a la que pertenece el empleado conectado
     * @return List con los campos de la tabla cadenaskioskosapp
     */    
    @GET
    @Path("{grupo}/{empresa}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCadenasXGrupoNit(@PathParam("grupo") String grupo, @PathParam("empresa") String nitEmpresa) {
        List s = null;
        System.out.println("Parametros: Grupo: " + grupo+", nit: "+nitEmpresa);
        try {
            String sqlQuery = "SELECT CODIGO, DESCRIPCION, NITEMPRESA, GRUPO, CADENA, EMPLNOMINA, ESQUEMA, ESTADO, OBSERVACION FROM CADENASKIOSKOSAPP WHERE GRUPO=? AND NITEMPRESA=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, grupo);
            query.setParameter(2, nitEmpresa);
            s = query.getResultList();
            System.out.println("1" + s.get(0));
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: "+this.getClass().getName()+" getCadenas():" + ex);
            return Response.status(Response.Status.OK).entity(s).build();
        }
    }    
    
    @GET
    @Path("validaGrupoInactivo/{grupo}")
    @Produces(MediaType.TEXT_PLAIN)
    public Long getValidaEstadoInactivoXGrupo(@PathParam("grupo") String grupo) {
        Long res = null;
        System.out.println("Parametros: Grupo: " + grupo);
        try {
            String sqlQuery = "SELECT COUNT(*) FROM CADENASKIOSKOSAPP WHERE GRUPO=? AND ESTADO='INACTIVO' ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, grupo);
            res = (Long) query.getSingleResult();
            System.out.println("count(*) inactivo: "+res);
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
            res = Long.valueOf(0);
        }
        return res;
    }    
    
}
