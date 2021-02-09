/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.services;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
            String sqlQuery = "SELECT CODIGO, DESCRIPCION, NITEMPRESA, GRUPO, CADENA, EMPLNOMINA, ESQUEMA FROM CADENASKIOSKOSAPP WHERE GRUPO=?";
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
    
}
