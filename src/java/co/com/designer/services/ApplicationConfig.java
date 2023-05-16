/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.services;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author usuario
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();        
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    // KIOSKO
    // resources.add(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
    // verificar que este añadida en resources
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(co.com.designer.services.CadenasKioskosFacadeREST.class);
        resources.add(co.com.designer.services.ConexionesKioskosFacadeREST.class);
        resources.add(co.com.designer.services.EmpleadosFacadeREST.class);
        resources.add(co.com.designer.services.OpcionesKioskosFacadeREST.class);
        resources.add(co.com.designer.services.ReportesFacadeREST.class);
        resources.add(co.com.designer.services.RhFacadeREST.class);
        resources.add(co.com.designer.services.VwvacaPendientesEmpleadosFacadeREST.class);
        resources.add(co.com.designer.services.filter.CorsFilter.class);
        resources.add(co.com.designer.services.kioCausasAusentismosFacadeREST.class);
    }
    
}
