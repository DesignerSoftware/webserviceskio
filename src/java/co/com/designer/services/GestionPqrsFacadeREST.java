package co.com.designer.services;

import co.com.designer.kiosko.administracion.implementacion.AdministrarPQRS;
import co.com.designer.kiosko.administracion.interfaz.IAdministrarPQRS;
import javax.ejb.Stateless;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Edwin Hastamorir
 */
@Stateless
@Path("pqrs")
public class GestionPqrsFacadeREST {

    private IAdministrarPQRS adminPQRS;

    public GestionPqrsFacadeREST() {
        adminPQRS = new AdministrarPQRS();
    }

    @POST
    @Path("/crearPqrs")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response crearPqrs(
            @HeaderParam("authorization") String token,
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nit,
            @QueryParam("titulo") String titulo,
            @QueryParam("mensaje") String mensaje,
            @QueryParam("cadena") String cadena, 
            @QueryParam("url") String url) {
        System.out.println("crearPqrs: Token recibido: " + token);
        System.out.println("crearPqrs: parametros: crearMensajeRh{ seudonimo: "
                + seudonimo
                + ", nitempresa: " + nit
                + ", titulo: " + titulo
                + ", mensaje: " + mensaje
                + ", cadena: " + cadena
                + ", url: " + url
        );
        
        JSONObject res = this.adminPQRS.crearPQRS(seudonimo, nit, titulo, mensaje, cadena, url);
        boolean creada = false;
        boolean enviada = false; 
        try {
            creada = (boolean) (res.get("PQRS_Creada"));
            enviada = (boolean) ( res.get("correoEnviado") );
        } catch (JSONException je) {
            je.printStackTrace();
        }
        if (creada && enviada) {
            return Response.status(Response.Status.CREATED).entity(res.toString()).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(res.toString()).build();
        }
    }
    
    
}
