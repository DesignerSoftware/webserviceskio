package co.com.designer.services;

import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @author thalia
 */
@Stateless
@Path("empleados")
public class EmpleadosFacadeREST {
    
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
    
    protected void setearPerfil() {
        try {
            System.out.println("setearPerfil()");
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil: " + ex);
        }
    }

    protected void setearPerfil(String cadena) {
        try {
            System.out.println("setearPerfil(cadena)");
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil(String cadena): " + ex);
        }
    }     
    
    @GET
    @Path("/datosEmpleadoNit/{empleado}/{nit}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDatosEmpleadoNit(@PathParam("empleado") String empleado, @PathParam("nit") String nit, @QueryParam("cadena") String cadena) {
        System.out.println("getDatosEmpleadosNit()");
        System.out.println("parametros: empleado: "+empleado+" nit: "+nit+ " cadena "+cadena);
        List s = null;
        try {
        String documento = getDocumentoPorSeudonimo(empleado, nit);
        setearPerfil();
        String sqlQuery="  select \n" +
"          e.codigoempleado usuario,  \n" +
"          p.nombre nombres, \n" +
"          p.primerapellido apellido1, \n" +
"          p.segundoapellido apellido2,  \n" +
"          decode(p.sexo,'M', 'MASCULINO', 'F', 'FEMENINO', '') sexo,  \n" +
"          to_char(p.FECHANACIMIENTO, 'yyyy-mm-dd') fechaNacimiento, \n" +
"          (select nombre from ciudades where secuencia=p.CIUDADNACIMIENTO) ciudadNacimiento, \n" +
"          p.GRUPOSANGUINEO grupoSanguineo, \n" +
"          p.FACTORRH factorRH, \n" +
"          (select nombrelargo from tiposdocumentos where secuencia=p.TIPODOCUMENTO) tipoDocu, \n" +
"          p.NUMERODOCUMENTO documento,  \n" +
"          (select nombre from ciudades where secuencia=p.CIUDADDOCUMENTO) lugarExpediDocu, \n" +
"          p.EMAIL email, \n" +
"          'DIRECCION' direccion,  \n" +
"          ck.ULTIMACONEXION ultimaConexion,\n" +
"          em.codigo codigoEmpresa, \n" +
"          em.nit nitEmpresa,  \n" +
"          em.nombre nombreEmpresa, \n" +
"          empleadocurrent_pkg.descripciontipocontrato(e.secuencia, sysdate) contrato, \n" +
"          empleadocurrent_pkg.ValorBasicoCorte(e.secuencia, sysdate) salario,  \n" +
"          empleadocurrent_pkg.DescripcionCargoCorte(e.secuencia, sysdate) cargo,  \n" +
// "          DIAS360(empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate), sysdate) diasW,  \n" +
"          empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate) inicioContratoActual,\n" +
// "         (select nombre from estructuras where secuencia=empleadocurrent_pkg.estructuracargocorte(e.secuencia, sysdate)) estructura, \n" +
"          em.logo logoEmpresa \n" +
"          from  \n" +
"          empleados e, conexioneskioskos ck, empresas em, personas p \n" +
"          where \n" +
"          e.persona=p.secuencia  \n" +
"          and e.empresa=em.secuencia  \n" +
"          and ck.empleado=e.secuencia\n" +
"          and p.numerodocumento= ? \n" +
"          and em.nit=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nit);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error getDatosEmpleadoNit: " + ex);
            return Response.status(Response.Status.OK).entity("Error").build();
        }
    }

    public String getDocumentoCorreoODocumento(String usuario) {
       String documento=null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P WHERE P.EMAIL=?";
            if (this.validarCodigoUsuario(usuario)) {
                 sqlQuery+=" OR P.NUMERODOCUMENTO=?"; // si el valor es numerico validar por numero de documento
            }
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, usuario);
            if (this.validarCodigoUsuario(usuario)) {
               query.setParameter(2, usuario);
            }
            documento =  query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: getDocumentoCorreoODocumento: "+e.getMessage());
        }
        return documento;
   }
    
    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa) {
       String documento=null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento =  query.getSingleResult().toString();
            System.out.println("documento: "+documento);
        } catch (Exception e) {
            System.out.println("Error: getDocumentoPorSeudonimo: "+e.getMessage());
        }
        return documento;
   }
    
    public boolean validarCodigoUsuario(String usuario) {
        boolean resultado = false;
        BigInteger numUsuario;
        try {
            numUsuario = new BigInteger(usuario);
            resultado = true;
        } catch (NumberFormatException nfe) {
            resultado = false;
        }
        return resultado;
    }
    
    @GET
    @Path("{usuario}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSecuencia(@PathParam("usuario") String usuario, @QueryParam("cadena") String cadena) {
        BigDecimal res = null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT EMPLEADO FROM CONEXIONESKIOSKOS WHERE SEUDONIMO=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            res = (BigDecimal) query.getSingleResult();
        } catch (Exception ex) {
            System.out.println("Error getSecuencia() path /empleados: " + ex);
            res = BigDecimal.ZERO;
        }
        return res.toString();
    }
    
}
