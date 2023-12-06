package co.com.designer.services;

import java.util.Set;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
/**
 *
 * @author Thalia Manrique
 */
@ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();        
        this.addRestResourceClasses(resources);
        // verificar que este añadida en resources
        // resources.add(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
        resources.add(MultiPartFeature.class);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(co.com.designer.services.CadenasKioskosFacadeREST.class);
        resources.add(co.com.designer.services.ConexionesKioskosFacadeREST.class);
        resources.add(co.com.designer.services.EmpleadosFacadeREST.class);
        resources.add(co.com.designer.services.GestionPqrsFacadeREST.class);
        resources.add(co.com.designer.services.OpcionesKioskosFacadeREST.class);
        resources.add(co.com.designer.services.ReportesFacadeREST.class);
        resources.add(co.com.designer.services.RhFacadeREST.class);
        resources.add(co.com.designer.services.VwvacaPendientesEmpleadosFacadeREST.class);
        resources.add(co.com.designer.services.filter.CorsFilter.class);
        resources.add(co.com.designer.services.kioCausasAusentismosFacadeREST.class);
    }
    
}
