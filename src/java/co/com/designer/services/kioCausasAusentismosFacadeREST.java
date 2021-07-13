/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.services;

import co.com.designer.kiosko.entidades.KioCausasAusentismos;
import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author UPC006
 */
@Stateless
@Path("ausentismos")
public class kioCausasAusentismosFacadeREST extends AbstractFacade<KioCausasAusentismos>  {

    public kioCausasAusentismosFacadeREST() {
        super(KioCausasAusentismos.class);
    }          
            
    @Override
    protected EntityManager getEntityManager() {
        String unidadPersistencia="wsreportePU";
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }   
    
    protected EntityManager getEntityManager(String persistence) {
        String unidadPersistencia=persistence;
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }
    
    @Override
    protected void setearPerfil() {
        try {
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil: " + ex);
        }    }    
    
    protected void setearPerfil(String cadenaPersistencia) {
        try {
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager(cadenaPersistencia).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil: " + ex);
        }
    }
    
    protected void setearPerfil(String esquema, String cadenaPersistencia) {
        try {
            String rol = "ROLKIOSKO";
            if (esquema != null && !esquema.isEmpty()) {
                rol = rol + esquema.toUpperCase();
            }
            System.out.println("setearPerfil(cadena)");
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager(cadenaPersistencia).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil(cadenaPersistencia): " + ex);
        }
    }     
    
    public String getEsquema( String nitempresa, String cadena) {
        System.out.println("Parametros getEsquema(): nitempresa: "+nitempresa+", cadena: "+cadena);
        String esquema = null;
        String sqlQuery;
        try {
            sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=?";
            Query query = getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitempresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("Esquema: "+esquema);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getEsquema(): " + e);
        } 
        return esquema;
    }      
    
    @GET
    //@Path("/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List findAlls() {
         setearPerfil();
         return super.findAll();
    }
    
   /*@GET
    @Path("/causas")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List findAlls(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
           String esquema = getEsquema(nitEmpresa, cadena);
           setearPerfil(esquema, cadena);
           String sqlQuery = "SELECT ka "
                   + " FROM KioCausasAusentismos ka "
                   + " WHERE "
                   + " ka.empresa.nit=:nitempresa ";
                       Query query = getEntityManager(cadena).createQuery(sqlQuery);
            query.setParameter("nitempresa", Long.parseLong(nitEmpresa));
            List<OpcionesKioskosApp>  lista = query.getResultList();
            return lista;
    }*/
    
   @GET
    @Path("/causas")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List findAlls(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
           String esquema = getEsquema(nitEmpresa, cadena);
           setearPerfil(esquema, cadena);
           String sqlQuery = "SELECT ka "
                   + " FROM KioCausasAusentismos ka "
                   + " WHERE "
                   + " ka.empresa.nit=:nitempresa ";
                       Query query = getEntityManager(cadena).createQuery(sqlQuery);
            query.setParameter("nitempresa", Long.parseLong(nitEmpresa));
            List<OpcionesKioskosApp>  lista = query.getResultList();
            return lista;
    }    
    
}
