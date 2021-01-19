package co.com.designer.services;

import co.com.designer.kiosko.correo.EnvioCorreo;
import co.com.designer.kiosko.entidades.ConexionesKioskos;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
//import java.sql.ResultSet;
//import java.sql.SQLDataException;
//import java.math.BigInteger;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
//import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
//import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.json.JSONObject;
import java.io.*;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Persistence;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.sql.Date;
import org.json.JSONException;
import passwordGenerator.GeneradorClave;
//import javax.ws.rs.core.Response;

/**
 *
 * @author Edwin Hastamorir
 */
@Stateless
@Path("conexioneskioskos")
public class ConexionesKioskosFacadeREST extends AbstractFacade<ConexionesKioskos> {

    //@PersistenceContext(unitName = "wsreportePU")
    //private EntityManager em;
    //final String UPLOAD_FILE_SERVER = "C:\\DesignerRHN12\\Basico12\\fotos_empleados\\";
    final String UPLOAD_FILE_SERVER = "E:\\DesignerRHN10\\Basico10\\fotos_empleados\\";

    public ConexionesKioskosFacadeREST() {
        super(ConexionesKioskos.class);
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

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(ConexionesKioskos entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void edit(@PathParam("id") BigDecimal id, ConexionesKioskos entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") BigDecimal id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ConexionesKioskos find(@PathParam("id") BigDecimal id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<ConexionesKioskos> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<ConexionesKioskos> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @GET
    @Path("{usuario}/{pass}@{bd}")
    @Produces(MediaType.TEXT_PLAIN)
//    public String validaUsuario(@DefaultValue("") @QueryParam("usuario") String usuario, @DefaultValue("") @QueryParam("pass") String pass, @DefaultValue("") @QueryParam("bd") String bd) {
//    public Response validaUsuario(@PathParam("usuario") String usuario, @PathParam("pass") String pass, @PathParam("bd") String bd) {
    public String validaUsuario(@PathParam("usuario") String usuario, @PathParam("pass") String pass, @PathParam("bd") String bd) {
        BigDecimal res = null;
        try {
            setearPerfil();
            String sqlQuery = "select count(*) "
                    + "from conexioneskioskos ck, personas per "
                    + "where per.secuencia = ck.persona "
                    + "and per.numerodocumento = ? "
                    + "and generales_pkg.decrypt(ck.pwd) = ? ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, pass);
            res = (BigDecimal) query.getSingleResult();
//            System.out.println("tipo res: "+res.getClass().getName());
//            System.out.println("res: "+res);
        } catch (Exception ex) {
            System.out.println("ex: " + ex);
            res = BigDecimal.ZERO;
        }
//        return String.valueOf(res);
        return res.toString();
        /*        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .entity(res)
                .build();*/
    }

    // Como usar: http://ip:puerto/wsreporte/webresources/conexioneskioskos/updateFechas?usuario=8125176&nitEmpresa=811025446&fechadesde=2020-08-01&fechahasta=2020-08-31&enviocorreo=false
    @POST
    @Path("/updateFechas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFechasConexionesKioskos(@QueryParam("usuario") String usuario, @QueryParam("nitEmpresa") String nitEmpresa, @QueryParam("fechadesde") String fechadesde,
            @QueryParam("fechahasta") String fechahasta, @QueryParam("enviocorreo") boolean enviocorreo, @QueryParam("dirigidoa") String dirigidoa) {
        int conteo = 0;
        System.out.println("Parametros: seudonimo: " + usuario + ", fechadesde: " + fechadesde + ", fechahasta: " + fechahasta+", dirigidoa: "+dirigidoa);
        try {
            setearPerfil();
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS "
                    + " SET FECHADESDE=TO_DATE(?, 'yyyy-mm-dd'), FECHAHASTA=TO_DATE(?, 'yyyy-mm-dd'), ENVIOCORREO=?, DIRIGIDOA=? "
                    + " WHERE SEUDONIMO=? "
                    + " AND NITEMPRESA=?";
            String envioc = (enviocorreo == true ? "S" : "N");
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, fechadesde);
            query.setParameter(2, fechahasta);
            query.setParameter(3, envioc);
            query.setParameter(4, dirigidoa);
            query.setParameter(5, usuario);
            query.setParameter(6, nitEmpresa);

            conteo = query.executeUpdate();
            System.out.println("update conexioneskioskos: " + conteo);
            //if (conteo > 0) {
              //  return Response.status(Response.Status.OK).entity(conteo).build();
            //} else {
                return Response.status(Response.Status.OK).entity(conteo).build();
            //}
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity(conteo).build();
        }
    }

    // como usar: http://localhost:8082/wsreporte/webresources/conexioneskioskos/updateClave?usuario={seudonimo}&nitEmpresa={nitempresa}&clave={nuevaClave}
    @POST
    @Path("/updateClave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFechasConexionesKioskos(@QueryParam("usuario") String usuario, @QueryParam("nitEmpresa") String nitEmpresa, @QueryParam("clave") String clave) {
        int conteo = 0;
        System.out.println("Parametros: seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa + ", clave: " + clave);
        try {
            setearPerfil();
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS "
                    + " SET PWD=GENERALES_PKG.ENCRYPT(?)"
                    + " WHERE SEUDONIMO=? "
                    + " AND NITEMPRESA=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, clave);
            query.setParameter(2, usuario);
            query.setParameter(3, nitEmpresa);

            conteo = query.executeUpdate();
            System.out.println("update conexioneskioskos: " + conteo);
            if (conteo > 0) {
                return Response.status(Response.Status.OK).entity(conteo).build();
            } else {
                return Response.status(Response.Status.OK).entity(conteo).build();
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity(conteo).build();
        }
    }

    // Como usar: http://ip:puerto/wsreporte/webresources/conexioneskioskos/updateFechas?usuario=8125176&nitEmpresa=811025446&fechadesde=2020-08-01&fechahasta=2020-08-31&enviocorreo=false
    @GET
    @Path("/parametros")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParametros(@QueryParam("usuario") String usuario, @QueryParam("nitEmpresa") String nitEmpresa) {
        int conteo = 0;
        List s = null;
        System.out.println("Parametros: seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa);
        try {
            setearPerfil();
            String sqlQuery = "SELECT TO_CHAR(FECHADESDE, 'yyyy-mm-dd'), TO_CHAR(FECHAHASTA, 'yyyy-mm-dd'), ENVIOCORREO, DIRIGIDOA FROM CONEXIONESKIOSKOS "
                    + " WHERE SEUDONIMO=? "
                    + " AND NITEMPRESA=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);

            s = query.getResultList();
            System.out.println("1" + s.get(0));
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity(s).build();
        }
    }

    // Como usar: http://ip:puerto/wsreporte/webresources/conexioneskioskos/updateFechas?usuario=8125176&nitEmpresa=811025446&fechadesde=2020-08-01&fechahasta=2020-08-31&enviocorreo=false
    //@GET
    //@Path("/correo")
    //@Produces(MediaType.APPLICATION_JSON)
    public String getCorreo(@QueryParam("usuario") String usuario, @QueryParam("nitEmpresa") String nitEmpresa) {
        String correo = "";
        System.out.println("Parametros: seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa);
        try {
            setearPerfil();
            String sqlQuery = "SELECT EMAIL FROM CONEXIONESKIOSKOS "
                    + " WHERE SEUDONIMO=? "
                    + " AND NITEMPRESA=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
            correo = (String) query.getSingleResult();
            return correo;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return correo;
        }
    }

    @POST
    @Path("/creaUsuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearUsuario(@QueryParam("seudonimo") String seudonimo, @QueryParam("usuario") String usuario,
            @QueryParam("clave") String clave, @QueryParam("nitEmpresa") String nitEmpresa) {
        String res = "creaUsuario: seudonimo: " + seudonimo + " usuario:" + usuario + " clave: " + clave
                + " nitEmpresa: " + nitEmpresa;
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            setearPerfil();
            String sqlQuery = "SELECT COUNT(*) conteo "
                    + "FROM CONEXIONESKIOSKOS ck, PERSONAS per, EMPLEADOS e, EMPRESAS em "
                    + "WHERE ck.EMPLEADO = e.SECUENCIA "
                    //                    + "WHERE ck.PERSONA = per.SECUENCIA "
                    + "AND e.persona = per.secuencia "
                    + "AND e.empresa = em.secuencia "
                    + "AND e.codigoempleado = ? "
                    + "AND em.nit = ? ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
            retorno = (BigDecimal) query.getSingleResult();
            if (retorno.compareTo(BigDecimal.ZERO) > 0) {
                resultado = false;
                mensaje = "Ya existe el usuario";
                System.out.println("resultado usuarioRegistrado: " + resultado + " Mensaje: " + mensaje);
            } else {
                String sqlQueryInsert = "INSERT INTO CONEXIONESKIOSKOS (SEUDONIMO, EMPLEADO, PERSONA, PWD, "
                        + "NITEMPRESA, ACTIVO) "
                        + "VALUES (?, (SELECT SECUENCIA FROM EMPLEADOS WHERE CODIGOEMPLEADO=?), "
                        + "(SELECT SECUENCIA FROM PERSONAS WHERE NUMERODOCUMENTO=?), "
                        + " GENERALES_PKG.ENCRYPT(?), ?, 'P')";
                Query queryInsert = getEntityManager().createNativeQuery(sqlQueryInsert);
                queryInsert.setParameter(1, seudonimo);
                queryInsert.setParameter(2, usuario);
                queryInsert.setParameter(3, usuario);
                queryInsert.setParameter(4, clave);
                queryInsert.setParameter(5, nitEmpresa);
                conteo = queryInsert.executeUpdate();
                resultado = conteo > 0 ? true : false;
                System.out.println("resultado registrar usuario " + resultado);
                if (resultado == true) {
                    mensaje = "Usuario " + usuario + " creado exitosamente!";

                    // this.getJWTValidCuenta(usuario, clave, nitEmpresa); // si se deja aqui se demora más tiempo en responder
                }
            }
        } catch (Exception e) {
            System.out.println("Error ApiRest.crearUSuario(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        //String[] parametros={usuario, clave, nitEmpresa};
        try {
            resp.put("created", resultado);
            resp.put("Mensaje", mensaje);
            //resp.put("parametros", parametros);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
        // return "hola";
    }

    @POST
    @Path("/cambioEstadoUsuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambioEstadoUsuarioConexionkiosko(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitEmpresa") String nitEmpresa,
            @QueryParam("activo") String activo) {
        String res = "creaUsuario: seudonimo: " + seudonimo + " nitEmpresa: " + nitEmpresa + " activo: " + activo;
        System.out.println(res);
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            setearPerfil();
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS SET ACTIVO=? WHERE SEUDONIMO=? AND NITEMPRESA=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, activo);
            query.setParameter(2, seudonimo);
            query.setParameter(3, nitEmpresa);
            conteo = query.executeUpdate();
            resultado = conteo > 0 ? true : false;
            if (conteo > 0) {
                System.out.println("modificado");
            } else {
                System.out.println("no modificado: " + resultado);
            }
        } catch (Exception e) {
            System.out.println("Error ApiRest.cambioEstadoUsuario(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        //String[] parametros={usuario, clave, nitEmpresa};
        try {
            resp.put("modificado", resultado);
            resp.put("Mensaje", mensaje);
            //resp.put("parametros", parametros);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
        // return "hola";
    }
    
    
    @POST
    @Path("/inactivaToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inactivaToken(
            @QueryParam("jwt") String jwt) {
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            setearPerfil();
            String sqlQuery = "UPDATE CONEXIONESTOKEN SET ACTIVO='N' WHERE TOKEN=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, jwt);
            conteo = query.executeUpdate();
            resultado = conteo > 0 ? true : false;
            if (conteo > 0) {
                System.out.println("modificado");
            } else {
                System.out.println("no modificado: " + resultado);
            }
        } catch (Exception e) {
            System.out.println("Error ApiRest.inactivaToken(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        //String[] parametros={usuario, clave, nitEmpresa};
        try {
            resp.put("modificado", resultado);
            resp.put("Mensaje", mensaje);
            //resp.put("parametros", parametros);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
        // return "hola";
    }
    
    @POST
    @Path("/inactivaTokensTipo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inactivaTokensTipo(
            @QueryParam("tipo") String tipo, 
            @QueryParam("seudonimo") String seudonimo, 
            @QueryParam("nit") String nit) {
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            setearPerfil();
            String sqlQuery = "UPDATE CONEXIONESTOKEN SET ACTIVO='N' WHERE TIPO=? "
                    + "AND CONEXIONKIOSKO=(SELECT SECUENCIA FROM CONEXIONESKIOSKOS WHERE SEUDONIMO=? AND NITEMPRESA=?)";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, tipo);
            query.setParameter(2, seudonimo);
            query.setParameter(3, nit);
            conteo = query.executeUpdate();
            resultado = conteo > 0 ? true : false;
            if (conteo > 0) {
                System.out.println("modificado");
            } else {
                System.out.println("no modificado: " + resultado);
            }
        } catch (Exception e) {
            System.out.println("Error ApiRest.inactivaTokensTipo(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        //String[] parametros={usuario, clave, nitEmpresa};
        try {
            resp.put("modificado", resultado);
            resp.put("Mensaje", mensaje);
            //resp.put("parametros", parametros);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
        // return "hola";
    }


    @Override
    protected void setearPerfil() {
        try {
            System.out.println("setearPerfil");
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil(): " + ex);
        }
    }

    @GET
    @Path("/obtenerFoto/{imagen}")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response obtenerFoto(@PathParam("imagen") String imagen) {
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = getPathFoto();
        try {
            fis = new FileInputStream(new File(RUTAFOTO + imagen));
            file = new File(RUTAFOTO + imagen);
        } catch (FileNotFoundException ex) {
            try {
                fis = new FileInputStream(new File(RUTAFOTO + "sinFoto.jpg"));
                file = new File(RUTAFOTO + "sinFoto.jpg");
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Foto no encontrada: " + imagen, ex1);
            }
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Error cerrando fis " + imagen, ex);
            }
        }
        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + imagen + "\"");
        return responseBuilder.build();
    }

    @GET
    @Path("/obtenerLogo/{imagen}")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response obtenerLogo(@PathParam("imagen") String imagen) {
        System.out.println("obtenerLogo()");
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = getPathFoto();
        try {
            fis = new FileInputStream(new File(RUTAFOTO + imagen));
            file = new File(RUTAFOTO + imagen);
            System.out.println("IMAGEN: "+RUTAFOTO+imagen);
        } catch (FileNotFoundException ex) {
            try {
                fis = new FileInputStream(new File(RUTAFOTO + "logodesigner.png"));
                file = new File(RUTAFOTO + "logodesigner.png");
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Logo no encontrado: " + imagen, ex1);
            }
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Error cerrando fis " + imagen, ex);
            }
        }
        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + imagen + "\"");
        return responseBuilder.build();
    }

    public String getPathFoto() {
        String rutaFoto=UPLOAD_FILE_SERVER;
        try {
            setearPerfil();
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            rutaFoto =  query.getSingleResult().toString();
            System.out.println("rutaFotos: "+rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathFoto: "+e.getMessage());
        }
        return rutaFoto;
    }    
    
    @POST
    @Path("/cargarFoto")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response cargarFoto(
            @FormDataParam("fichero") InputStream fileInputStream,
            @FormDataParam("fichero") FormDataContentDisposition fileFormDataContentDisposition) {
        String fileName = null;
        String uploadFilePath = null;

        try {
            fileName = fileFormDataContentDisposition.getFileName();
            uploadFilePath = writeToFileServer(fileInputStream, fileName);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error: "+ioe.getMessage());
        } finally {
        }
        return Response.ok("Fichero subido a " + uploadFilePath).build();
    }

    private String writeToFileServer(InputStream inputStream, String fileName) throws IOException {

        OutputStream outputStream = null;
        //String qualifiedUploadFilePath = UPLOAD_FILE_SERVER + fileName;
        String qualifiedUploadFilePath = getPathFoto() + fileName;

        try {
            outputStream = new FileOutputStream(new File(qualifiedUploadFilePath));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            outputStream.close();
        }
        return qualifiedUploadFilePath;
    }
    
    @GET
    @Path("/solicitudXEstado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudXEstado(@QueryParam("documento") String documento,
            @QueryParam("empresa") String empresa,
            @QueryParam("estado") String estado) {
        int conteo = 0;
        List s = null;
        System.out.println("Parametros: empleado: " + documento + ", empresa: " + empresa + " estado: " + estado);
        try {
            setearPerfil();
            String sqlQuery = "select to_char(ks.fechageneracion, 'dd/mm/yyyy') fechacreacion, \n"
                    + "to_char(kn.fechainicialdisfrute, 'dd/mm/yyyy') fechainicio, kn.dias dias, \n"
                    + "to_char(e.fechaprocesamiento,'dd/mm/yyyy') fechaprocesamiento, e.estado,\n"
                    + "to_char(kn.ADELANTAPAGOHASTA, 'dd/mm/yyyy') fechafin, \n"
                    + "to_char(kn.FECHASIGUIENTEFINVACA, 'dd/mm/yyyy') fecharegreso,\n"
                    + "TO_CHAR(v.INICIALCAUSACION, 'dd/mm/yyyy')||' a '||TO_CHAR(v.FINALCAUSACION, 'dd/mm/yyyy') periodocausado, \n"
                    + " e.MOTIVOPROCESA motivoprocesa, "
                    + "DECODE(KS.EMPLEADOJEFE, NULL, (select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido from kioautorizadores kioa, personas pei where kioa.persona=pei.secuencia\n"
                    + "and pei.secuencia=ks.autorizador),\n"
                    + "(select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido \n"
                    + "from personas pei, empleados ei where ei.persona=pei.secuencia and ei.secuencia=e.empleadoejecuta)) empleadoejecuta, \n"
                    + "e.secuencia secuencia \n"
                    + "from KioEstadosSolici e,  kiosolicivacas ks, kionovedadessolici kn, VwVacaPendientesEmpleados v\n"
                    + "where \n"
                    + "e.kiosolicivaca = ks.secuencia \n"
                    + "and ks.KIONOVEDADSOLICI = kn.secuencia \n"
                    + "and kn.vacacion=v.RFVACACION\n"
                    + "and ks.empleado = (select ei.secuencia from empleados ei, personas pei, empresas em where ei.persona=pei.secuencia \n"
                    + "                  and ei.empresa=em.secuencia and em.nit=? \n"
                    + "                  and pei.numerodocumento=?)\n"
                    + "and e.estado = ? \n"
                    + "and e.secuencia = (select max(ei.secuencia)\n"
                    + "from KioEstadosSolici ei, kiosolicivacas ksi \n"
                    + "where ei.kioSoliciVaca = ksi.secuencia \n"
                    + "and ksi.secuencia=ks.secuencia) \n"
                    + "order by e.fechaProcesamiento DESC";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, empresa);
            query.setParameter(2, documento);
            query.setParameter(3, estado);

            s = query.getResultList();
            s.forEach(System.out::println);

        if (s.size()>0) {
            for (int i = 0; i < s.size(); i++) {
            JsonObject json=Json.createObjectBuilder()
            .add("1", s.get(0).toString())
            .build();
            }
        }
          return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity("").build();
        }
    }
    
    
    @GET
    @Path("/solicitudesXEmpleadoJefe")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudesXEmpleadoJefe(@QueryParam("documentoJefe") String documentoJefe,
            @QueryParam("empresa") String empresa, @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        System.out.println("Webservice: solicitudesXEmpleadoJefe Parametros: empleado: " + documentoJefe + ", empresa: " + empresa);
        try {
            setearPerfil();
            String sqlQuery = "  SELECT \n" +
            "  t1.CODIGOEMPLEADO, P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE NOMBRECOMPLETO,\n" +
            "  to_char(t2.FECHAGENERACION, 'DD/MM/YYYY HH:mm:ss') solicitud, \n" +
            "  to_char(kn.FECHAINICIALDISFRUTE, 'DD/MM/YYYY HH:mm:ss') FECHAINICIALDISFRUTE,\n" +
            "  to_char(T0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:mm:ss') FECHAPROCESAMIENTO,\n" +
            "  t0.SECUENCIA, NVL(t0.MOTIVOPROCESA, 'N/A'), \n" +
            "  to_char(kn.ADELANTAPAGOHASTA, 'DD/MM/YYYY HH:mm:ss') FECHAFINVACACIONES,\n" +
            "  to_char(kn.fechaSiguienteFinVaca, 'DD/MM/YYYY HH:mm:ss') fecharegresolaborar,\n" +
            "  kn.dias,\n" +
            "  TO_CHAR(v.INICIALCAUSACION, 'DD/MM/YYYY')||' a '||TO_CHAR(v.FINALCAUSACION, 'DD/MM/YYYY') periodo,\n" +
            "  (select pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre from personas pei, empleados ei\n" +
            "  where pei.secuencia=ei.persona and t2.EMPLEADOJEFE=ei.secuencia) empleadojefe,\n" +
            "  TO_CHAR(kn.FECHAPAGO, 'DD/MM/YYYY') FECHAPAGO\n, " +
            "  t0.ESTADO ESTADO "+
            "  FROM KIOESTADOSSOLICI t0, KIOSOLICIVACAS t2, EMPLEADOS t1, PERSONAS P, kionovedadessolici kn, VwVacaPendientesEmpleados v \n" +
            "  WHERE (((((t1.EMPRESA = (select secuencia from empresas where nit=?)) \n" +
            "  AND (t0.ESTADO IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO'))) "+
            " AND (t2.EMPLEADOJEFE = (select secuencia from empleados where codigoempleado=?))) \n" +
            "  AND (t0.SECUENCIA = (SELECT MAX(t3.SECUENCIA) FROM KIOSOLICIVACAS t4, KIOESTADOSSOLICI t3 \n" +
            "  WHERE ((t4.SECUENCIA = t2.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIVACA))))) \n" +
            "  AND ((t2.SECUENCIA = t0.KIOSOLICIVACA) AND (t1.SECUENCIA = t2.EMPLEADO))\n" +
            "  AND t1.PERSONA=P.SECUENCIA\n" +
            "  and t2.KIONOVEDADSOLICI = kn.secuencia\n" +
            "  and kn.vacacion=v.RFVACACION\n" +
            "  ) \n" +
            "  ORDER BY t0.FECHAPROCESAMIENTO";
            //Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, empresa);
            query.setParameter(2, documentoJefe);
            s = query.getResultList();
            s.forEach(System.out::println);

        if (s.size()>0) {
            for (int i = 0; i < s.size(); i++) {
            JsonObject json=Json.createObjectBuilder()
            .add("1", s.get(0).toString())
            .build();
            }
        }
          return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity("").build();
        }
    }
    
    @GET
    @Path("/datosContactoKiosco/{nit}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatosContactoKiosco(@PathParam("nit") String nit) {
        BigDecimal res = null;
        List datos = null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT K.NOMBRECONTACTO NOMBRE, K.EMAILCONTACTO EMAIL, TELEFONOCONTACTO TELEFONO "
                    + "FROM KIOPERSONALIZACIONES K, EMPRESAS EM WHERE K.EMPRESA=EM.SECUENCIA AND EM.NIT=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, nit);
            datos = query.getResultList();
        } catch (Exception ex) {
            System.out.println("ex: " + ex);
            res = BigDecimal.ZERO;
        }
        return Response.status(Response.Status.OK).entity(datos).build();
    }
    
    
    
    @GET
    @Path("/validarIngresoSeudonimoKiosco")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarIngresoSeudonimoKiosco(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave, 
            @QueryParam("nitEmpresa") String nitEmpresa) {
        boolean ingresoExitoso = false;
        boolean primeringreso = false;
        String estadoUsuario = "";
        String mensaje = "Error no controlado, por favor inténtelo nuevamente.";  
        List rs = null;
        System.out.println("Parametro usuario: "+usuario+" clave: "+clave+" nitEmpresa: "+nitEmpresa);
        String documento = null;
        try {
           setearPerfil();
           documento = getDocumentoCorreoODocumento(usuario);
           System.out.println("documento asociado a correo o documento: "+usuario+ " es "+documento);
           if (documento!=null){
                System.out.println("Ingresa ciclo documento!=null");
                if (validarUsuarioyEmpresa(documento, nitEmpresa)) { // valida si existe relacion con la empresa y se encuentra activo
                    if (validarUsuarioRegistrado(documento, nitEmpresa)) { // relacion entre conexioneskioskos, empleados y personas
                        System.out.println("usuario "+usuario+ " esta registrado en conexioneskioskos");
                        if (validarSeudonimoRegistrado(usuario, nitEmpresa)) {
                            System.out.println("El seudonimo  "+usuario+ " existe y esta relacionado con la empresa seleccionada "+nitEmpresa);
                            /// usuario no validado
                            estadoUsuario = validarEstadoUsuarioSeudonimo(usuario, nitEmpresa);
                            System.out.println("validarEstadoUsuarioSeudonimo: " + estadoUsuario);
                            if (estadoUsuario.equals("P")) {
                                System.out.println("El usuario "+usuario+ " no ha validado su cuenta. Estado PENDIENTE");
                                ingresoExitoso = false;
                                primeringreso = false;
                                mensaje = "Cuenta no validada. Por favor valida tu cuenta desde el enlace que se te envió al correo al momento de registrarte.";
                                System.out.println("Mensaje: "+mensaje);
                            } else if (estadoUsuario.equals("S")) {
                                System.out.println("El estado del usuario "+usuario+ " es activo: S");
                                if (validarIngresoUsuarioSeudonimoRegistrado(usuario, clave, nitEmpresa)) {
                                    mensaje = "El usuario que ingresa es: " + usuario;
                                    System.out.println("Mensaje: "+mensaje);
                                    ingresoExitoso = true;
                                } else {
                                    // LA CONTRASEÑA ES INCORRECTA.
                                    ingresoExitoso = false;
                                    mensaje = "La contraseña es inválida.";
                                    System.out.println("La contraseña digita por el usuario "+usuario+ " es incorrecta");
                                }
                            } else {
                                //USUARIO BLOQUEADO
                                mensaje = "El empleado " + usuario + " se encuentra bloqueado";//, por favor comuníquese con el área de recursos humanos de su empresa.";
                                ingresoExitoso = false;
                            }
                        } else {
                            ingresoExitoso = false;
                            primeringreso = true;
                            mensaje = "El usuario no es correcto.";
                            System.out.println("El usuario "+usuario+ " no es correcto");
                        }
                    } else {
                        ingresoExitoso = false;
                        primeringreso = false;
                        mensaje = "El usuario no existe. Si no tiene un usuario utilice la opción de Registrarse.";
                        System.out.println("El usuario "+usuario+ " no existe. No ha creado su cuenta");
                    }
                } else {
                    System.out.println("No se encontró relación conexioneskioskos-empleados-personas");
                    ingresoExitoso = false;
                    primeringreso = false;
                    mensaje = "El empleado no existe o no está activo en la empresa seleccionada.";
                    System.out.println(mensaje);
                }
           } else {
                System.out.println("Ingreso al ciclo de documento == null");
                ingresoExitoso = false;
                primeringreso = false;
                mensaje = "El usuario no existe.";
            }
        } catch (Exception e) {
            System.out.println("Error: validarIngresoSeudonimoKiosco: "+e.getMessage());
        }
        
        try {
                    JsonObject json=Json.createObjectBuilder()
                    .add("primerIngreso", primeringreso)
                    .add("ingresoExitoso", ingresoExitoso)
                    .add("EstadoUsuario", estadoUsuario)
                    .add("mensaje", mensaje).
                                    build();
            /*return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();*/
            return Response.status(Response.Status.CREATED).entity(json)                 
                    .build();
                
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
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
            System.out.println("Error: crearUsuario: "+e.getMessage());
        }
        return documento;
   }
   
    public boolean validarUsuarioyEmpresa(String usuario, String nitEmpresa) {
        boolean resultado = false;
        try {
            setearPerfil();
            String sqlQuery = "SELECT COUNT(*) count FROM EMPLEADOS e, Empresas em "
                    + "WHERE e.empresa = em.secuencia "
                    + "AND e.codigoempleado = ? "
                    + "AND em.nit = ? "
                    + "AND (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'ACTIVO' "
                    + "OR EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'PENSIONADO')";
            System.out.println("Parametros[ usuario: "+usuario+ ", nitEmpresa: "+nitEmpresa+"]");
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);

            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
        }catch (Exception ex) {
            System.out.println("Error: validarUsuarioyEmpresa: "+ex.getMessage());
        }
        return resultado;
    }
    
    public boolean validarCodigoUsuario(String usuario) {
        boolean resultado = false;
        BigInteger numUsuario;
        try {
            numUsuario = new BigInteger(usuario);
            resultado = true;
        } catch (NumberFormatException nfe) {
            resultado = false;
            System.out.println("Error validarCodigoUsuario: "+nfe.getMessage());
        }
        return resultado;
    }
    
     public boolean validarUsuarioRegistrado(String usuario, String nitEmpresa) { //  verifica si se requiere mejora para validar por persona
        boolean resultado = false;
        try {
            setearPerfil();
            String sqlQuery = "SELECT COUNT(*) count "
                    + "FROM CONEXIONESKIOSKOS ck, PERSONAS per, EMPLEADOS e, EMPRESAS em "
                    + "WHERE ck.EMPLEADO = e.SECUENCIA "
                    //                    + "WHERE ck.PERSONA = per.SECUENCIA "
                    + "AND e.persona = per.secuencia "
                    + "AND e.empresa = em.secuencia "
                    + "AND e.codigoempleado = ? "
                    + "AND em.nit = ? ";
            System.out.println("Parametros [ usuario: "+usuario+", nitEmpresa: "+nitEmpresa+" ]");
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
                       BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
        } catch (Exception e) {
            System.out.println("Error : validarUsuarioRegistrado "+e.getMessage());
        }
        return resultado;
     }
     
    public boolean validarSeudonimoRegistrado(String seudonimo, String nitEmpresa) {
        System.out.println("validarSeudonimoRegistrado()");
        boolean resultado = false;
        try {
            setearPerfil();
            String sqlQuery = "SELECT COUNT(*) count "
                    + "FROM CONEXIONESKIOSKOS ck, PERSONAS per, EMPLEADOS e, EMPRESAS em "
                    + "WHERE "
                    + "ck.persona = per.secuencia and " 
                    + "ck.SEUDONIMO = ? " 
                    //                    + "WHERE ck.PERSONA = per.SECUENCIA "
                    + "AND e.persona = per.secuencia "
                    + "AND e.empresa = em.secuencia "
                    + "AND em.nit = ? ";
            System.out.println("Parametros: [ seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa);
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
        } catch (Exception e) {
            System.out.println("Error: validarSeudonimoRegistrado(): "+e.getMessage());
        }
        return resultado;
    }
    
    
    
    public String validarEstadoUsuarioSeudonimo(String usuario, String nitEmpresa) { // retorna true si el usuario esta activo
        System.out.println("validarEstadoUsuarioSeudonimo()");
        String retorno = null;
        boolean resultado = false;
        try {
            setearPerfil();
            String sqlQuery = "SELECT ACTIVO estado "
                    + "FROM CONEXIONESKIOSKOS ck, PERSONAS per, EMPLEADOS e, EMPRESAS em "
                    //                    + "WHERE ck.PERSONA = per.SECUENCIA "
                    + "WHERE ck.EMPLEADO = e.SECUENCIA "
                    + "AND per.secuencia = e.persona "
                    + "AND e.empresa = em.secuencia "
                    + "AND ck.seudonimo = ? "
                    + "AND em.nit = ? ";
            System.out.println("Parametros: [usuario: "+usuario+ ", nitEmpresa: "+nitEmpresa);
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
            retorno = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error validarEstadoUsuarioSeudonimo(): "+e.getMessage());
        }
        return retorno;
    }
    
    
    public boolean validarIngresoUsuarioSeudonimoRegistrado(String seudonimo, String clave, String nitEmpresa) { // retorna true si el usuario esta activo
        boolean resultado = false;
        try {
            setearPerfil();
            String sqlQuery = "SELECT COUNT(*) count "
                    + "FROM CONEXIONESKIOSKOS ck, Personas per, EMPLEADOS e "
                    + "WHERE ck.PERSONA = per.SECUENCIA "
                    + "AND per.secuencia = e.persona "
                    + "AND ck.seudonimo = ? "
                    + "AND ck.PWD = GENERALES_PKG.ENCRYPT(?) "
                    + "AND ck.activo = 'S' "
                    + "AND ck.nitempresa = ? "
                    + "AND (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'ACTIVO' "
                    + "OR EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'PENSIONADO')";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, clave);
            query.setParameter(3, nitEmpresa);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
        } catch(Exception e) {
            System.out.println("Error: validarIngresoUsuarioSeudonimoRegistrado: "+e.getMessage());
        }
        return resultado;
    }
    
    
        // token de inicio de sesion
    // como usar: http://localhost:8080/restKiosco-master/wsKiosco/restapi/restKiosco/jwt?usuario=8125176&clave=Prueba01*&nit=811025446 
    @POST
    @Path("/restKiosco/jwt")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJWT(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave, @QueryParam("nit") String nit) throws UnsupportedEncodingException{
        Boolean r=validarLogin(usuario, clave, nit);
        Boolean registroToken = false;
        //String passwordEncript = "Manager01*"+usuario+nit;
        String passwordEncript = "Manager01";
            long tiempo=System.currentTimeMillis();
            Date fechaCreacion= new Date(tiempo);
            //Date fechaExpiracion= new Date(tiempo+999999999);
            //java.sql.Date fechaExpiracion =  (Date) java.sql.Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
            Calendar fechaExpiracion = Calendar.getInstance();
            fechaExpiracion.setTime(new java.util.Date()); 
            fechaExpiracion.add(Calendar.DAY_OF_YEAR, 30);  // 30 dias a partir de la fecha*/
            String jwt=Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, passwordEncript.getBytes("UTF-8"))
                    .setSubject(usuario)
                    .setIssuedAt(fechaCreacion)
                    //.setExpiration(new Date(tiempo+21600000))
                    .setExpiration(fechaExpiracion.getTime())
                    .setIssuer("https://www.designer.com.co") //200721
                    .claim("empresa", nit)
                    .claim("documento", getDocumentoPorSeudonimo(usuario, nit))
                    .compact();
                    System.out.println("Token generado: "+jwt);
    
                    registroToken = registraToken(usuario, nit, jwt, "LOGIN", fechaCreacion, fechaCreacion);
                        
            JsonObject json=Json.createObjectBuilder()
                    .add("JWT", jwt)
                    .add("registroToken", registroToken)
                    .build();
            /*return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();*/
            return Response.status(Response.Status.CREATED).entity(json)
                    .build();
            // Usuario prueba exitosa: http://localhost:8080/restKiosco-master/wsKiosco/restapi/restKiosco/jwt/8125176/Prueba01*/811025446
    }
    
    
   public boolean validarLogin(String codEmple, String clave, String nit){
        boolean res=false;
        try {
            setearPerfil();
            String sql="select count(*) as total " +
            "from conexioneskioskos ck "+//, empleados e, empresas em " +
            "where " +
            //"ck.empleado=e.secuencia " +
            //"and e.empresa=em.secuencia " +
            // "and e.codigoempleado= ? " +
            "    ck.seudonimo = ? "+
            "and ck.pwd=generales_pkg.encrypt(?) " +
            " and ck.nitempresa=?";
            //"and em.nit= ? and empleadocurrent_pkg.tipotrabajadorcorte(e.secuencia, sysdate)='ACTIVO'";
            System.out.println(sql);
            System.out.println("Nit: "+nit+" clave: "+clave+" usuario: "+codEmple);
            Query query = getEntityManager().createNativeQuery(sql);
            query.setParameter(1, codEmple);
            query.setParameter(2, clave);
            query.setParameter(3, nit);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            res = instancia > 0;
        } catch (Exception e) {
            System.out.println("Error validarLogin() "+e.getMessage()+" "+e.getLocalizedMessage());
            res=false;
        } 
        return res;
    }
   
   
    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa) {
       System.out.println("getDocumentoPorSeudonimo()");
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
    
    
    public boolean registraToken (String usuario, String nit, String token, String tipo, Date fechacreacion, java.sql.Date fechaexpira){
        int resCon = 0;
        boolean resultado = false;
        try {
            setearPerfil();
            String sql= "INSERT INTO CONEXIONESTOKEN (CONEXIONKIOSKO, TOKEN, FECHACREACION, FECHAEXPIRACION, TIPO) VALUES "
                    + "((SELECT SECUENCIA FROM CONEXIONESKIOSKOS WHERE SEUDONIMO =? AND NITEMPRESA=?),"
                    + " ?, ?, ?, ? )";
            Query query = getEntityManager().createNativeQuery(sql);
            System.out.println("Query: "+sql);
            query.setParameter(1, usuario);
            query.setParameter(2, nit);
            query.setParameter(3, token);
            query.setParameter(4, (java.sql.Date) fechacreacion);
            query.setParameter(5, fechaexpira);
            query.setParameter(6, tipo);
            //ps.setDate(5, (java.sql.Date) fechaexpira);
            resCon = query.executeUpdate();
            resultado = resCon>0;
            System.out.println("Resultado registra token: "+resultado);
        } catch (Exception ex) {
            System.out.println("Error registraToken: "+ex.getMessage());
        }
        return resultado;
    }
    
    
    
    @GET
    @Path("/restKiosco/validarJWTActivarCuenta") // verificar nombre
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarJWT(@QueryParam("jwt") String jwt){
        boolean validoToken = false;
        String mensaje="";
        String usuario="";
        String nit="";
        String documento="";
        JsonObject json;
        try {
            setearPerfil();
            if (validarTokenExistente(jwt)) {
                if (getEstadoToken(jwt).equals("S")){
                     System.out.println("Token existe en bd");
                
                     //String passwordEncriptacion = "Manager01*"+usuario+nit;
                     String passwordEncriptacion = "Manager01";
                     Jwts.parser()
                    .setSigningKey(passwordEncriptacion.getBytes("UTF-8"))
                    .parseClaimsJws(
                       jwt
                    );

                //OK, we can trust this JWT
                System.out.println("token válido");
                validoToken = true;
                mensaje="Token válido";
                        
                    try {
                        usuario = (String) Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwt).getBody().getSubject();
                        nit = (String) Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwt).getBody().get("empresa");
                        documento = (String) Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwt).getBody().get("documento");
                    } catch (UnsupportedEncodingException ex) {
                        System.out.println("Error validarJWT: "+ex.getMessage());
                    }
                } else {
                    validoToken = false;
                    // mensaje="El token no es válido o está expirado";
                    mensaje = "El enlace no es válido o se ha expirado.";
                }
            
            } else {
                //mensaje = "El token no es válido";
                mensaje = "El enlace no es válido";
                validoToken = false;
                 json=Json.createObjectBuilder()
                .add("validoToken", validoToken)
                .add("mensaje", mensaje)
                .add("documento", documento)
                .build();
                return Response.status(Response.Status.ACCEPTED .getStatusCode()).entity(json)
                .build();
            }

        } catch (SignatureException e) {

            //don't trust the JWT!
            System.out.println("Error, jwt no válido " + e.getMessage());
            validoToken = false;
            mensaje = "Error, jwt no válido " + e.getMessage();
             json=Json.createObjectBuilder()
            .add("validoToken", validoToken)
            .add("mensaje", mensaje)
            .add("documento", documento)
            .build();
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).entity(json)
            .build();
        } catch (io.jsonwebtoken.ExpiredJwtException exp) {
            System.out.println("Token expirado. " + exp.getMessage());
            validoToken = false;
            //mensaje ="El token se ha expirado";
            mensaje = "El enlace se ha expirado";
            System.out.println(mensaje);
             json=Json.createObjectBuilder()
            .add("validoToken", validoToken)
            .add("mensaje", mensaje)
            .add("documento", documento)
            .build();
            return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
            .build();
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error ");
            validoToken = false;
            //mensaje="El token no es válido";
            mensaje = "El enlace no es válido";
            json=Json.createObjectBuilder()
            .add("validoToken", validoToken)
            .add("mensaje", mensaje)
            .add("documento", documento)
            .build();
            return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
            .build();
        } catch (io.jsonwebtoken.MalformedJwtException emj){
            validoToken = false;
            //mensaje = "El jwt no tiene un formato válido";
            mensaje = "El enlace no es válido";
            System.out.println("El token no tiene un formato válido");
             json=Json.createObjectBuilder()
            .add("validoToken", validoToken)
            .add("mensaje", mensaje)
            .add("documento", documento)
            .build();
             return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity("El jwt no tiene un formato válido")
             .build();
        } catch (Exception e) {
            validoToken = false;
            //mensaje = "El token no es válido "+e.getMessage();
            mensaje = "El enlace no es válido. " +e.getMessage();

        }
        
        json=Json.createObjectBuilder()
            .add("validoToken", validoToken)
            .add("mensaje", mensaje)
            .add("usuario", usuario)
            .add("empresa", nit)
            .add("documento", documento)
            .build();
        
        return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
        .build();
    }
    
    
    // validar si existe token
    public boolean validarTokenExistente(String token){
        System.out.println("validarTokenExistente()");
        boolean resultado=false;
        try {
            setearPerfil();
            String sqlQuery="select count(*) as count from conexionestoken where token=? ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, token);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
            System.out.println("Resultado validarTokenExistente: "+resultado);
        } catch (Exception e) {
            System.out.println("Error DAO.Kiosco.validarTokenExistente() "+e.getMessage());
            resultado=false;
        } 
        return resultado;
    }
    
    
    public String getEstadoToken(String token) { // retorna true si el usuario esta activo
        String estado = "";
        try {
            setearPerfil();
            String sqlQuery = "SELECT ACTIVO FROM CONEXIONESTOKEN WHERE TOKEN = ?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, token);
            estado = query.getSingleResult().toString();           
        } catch (Exception e) {
            System.out.println("Error getEstadoToken(): " + e);
        } 
        return estado;
    }
    
    @GET
    @Path("/restKiosco/documentoconexioneskioskos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentoConexioneskioskos(@QueryParam("seudonimo") String usuario, @QueryParam("nit") String nit){
        String r=getDocumentoPorSeudonimo(usuario, nit);
        String[] parametros={usuario, nit};
            return Response.ok(
                response("documentoconexioneskioskos", "Usuario: "+usuario+", nit: "+nit, String.valueOf(r)), MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("/restKiosco/logoEmpresa/{nit}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLogoEmpresa(@PathParam("nit") String nit){
        String r= getLogoEmpresaS(nit);
        return Response.ok(r, MediaType.TEXT_PLAIN)
        .build();
    }
    
    
    public String getLogoEmpresaS(String nitEmpresa) { // retorna true si el usuario esta activo
        ResultSet rs = null;
        String logo = "";
        JSONObject logoE = new JSONObject();
        try {
            setearPerfil();
            String sqlQuery = "SELECT LOGO FROM EMPRESAS WHERE NIT = ?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            logo = query.getSingleResult().toString();
            logoE.put("LOGO", logo.substring(0, logo.length()-4));
        } catch (Exception e) {
            System.out.println("Error getLogoEmpresa(): " + e);
        }
        return logoE.toString();
    }
    
    @GET
    @Path("/restKiosco/correoconexioneskioskos/{usuario}/{nit}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCorreoConexioneskioskoS(@PathParam("usuario") String usuario, @PathParam("nit") String nit){
        String r= getCorreoConexioneskioskos(usuario, nit);
        String[] parametros={usuario, nit};
            return Response.ok(
                response("correoConexioneskioskos", "Usuario: "+usuario+", nit: "+nit, 
                        String.valueOf(r)), MediaType.APPLICATION_JSON)
                .build();
    }
    
    public String getCorreoConexioneskioskos(String seudonimo, String empresa) {
        System.out.println("getCorreoConexioneskioskos()");
        System.out.println("seudonimo: "+seudonimo+" nit empresa: "+empresa);
        String correo = null;
        String sqlQuery;
        try {
            setearPerfil();
            sqlQuery = "SELECT P.EMAIL FROM PERSONAS P, conexioneskioskos ck WHERE p.secuencia=ck.persona and "
                    + " ck.seudonimo=? and ck.nitempresa=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, empresa);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error getCorreoConexioneskioskos(): " + e.getMessage());
        }
        return correo;
    }
    
        // Validar si el usuario esta activo y pertenece a la empresa
    @GET
    @Path("/restKiosco/validarUsuarioyEmpresa/{usuario}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarUsuarioyEmpresaS(@PathParam("usuario") String usuario, @PathParam("nitEmpresa") String nitEmpresa){
        boolean res=validarUsuarioyEmpresa(usuario, nitEmpresa);
        String[] parametros={usuario, nitEmpresa};
        try {
                return Response.ok(
                response("validarUsuarioyEmpresa", "Usuario: "+usuario+", "
                        + "nitEmpresa: "+nitEmpresa, String.valueOf(res)), 
                    MediaType.APPLICATION_JSON)                     
                    .build();
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
        // Validar si el usuario esta registrado y pertenece a la empresa
    @GET
    @Path("/restKiosco/validarSeudonimoyEmpresaRegistrado/{usuario}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarSeudonimoyEmpresaRegistrado(@PathParam("usuario") String usuario, @PathParam("nitEmpresa") String nitEmpresa){
        boolean res=validarSeudonimoRegistrado(usuario, nitEmpresa);
        String[] parametros={usuario, nitEmpresa};
        try {
                return Response.ok(
                response("validarSeudonimoyEmpresaRegistrado", "Usuario: "+usuario+", "
                        + "nitEmpresa: "+nitEmpresa, String.valueOf(res)), 
                    MediaType.APPLICATION_JSON)                    
                    .build();
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    
    @GET
    @Path("/restKiosco/generarClave")
    public Response generadorClaveAleatoria(@QueryParam("usuario") String usuario, @QueryParam("nit") String nit) {
        String nuevaClave = GeneradorClave.generatePassword(); // generador de contraseña aleatoria
        boolean envioCorreo=false;
        boolean updateClave = false;
        updateClave = actualizarClave(usuario, nit,  nuevaClave);
        setearPerfil();
        if (updateClave){
            System.out.println("Contraseña actualizada a: "+nuevaClave);
            EnvioCorreo e= new EnvioCorreo();
            //envioCorreo = e.enviarNuevaClave("smtp.gmail.com", "465", "S", "pruebaskiosco534@gmail.com", "Nomina01", getCorreoConexioneskioskos(usuario, nit), getNombrePersona(usuario), nuevaClave, "");
            envioCorreo = e.enviarNuevaClave("smtp.designer.com.co", "587", "S", "kioskodesigner@designer.com.co", "Nomina01", getCorreoConexioneskioskos(usuario, nit), getNombrePersona(usuario), nuevaClave, "");
        } else {
            System.out.println("Error al actualizar la contraseña");
        }
        JsonObject json=Json.createObjectBuilder()
        .add("envioCorreo", envioCorreo)
        .add("updateClave", updateClave)
        .build();
        return Response.ok(json,
                MediaType.APPLICATION_JSON)
                .build();
    }
    
    public boolean actualizarClave(String seudonimo, String nitEmpresa, String clave){
        System.out.println("actualizarClave");
        String datos = null;
        boolean resultado = false;
        int resCon = 0;
        try {
            setearPerfil();
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS SET PWD=GENERALES_PKG.ENCRYPT(?) where seudonimo=? and nitempresa=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, clave);
            query.setParameter(2, seudonimo);
            query.setParameter(3, nitEmpresa);
            resCon = query.executeUpdate();
            resultado = resCon > 0;
            System.out.println("resultado actualizarClave: "+resultado);
        } catch (Exception e) {
            System.out.println("Error actualizarClave: " + e.getMessage());
        }
        return resultado;
    }
    
    public String getNombrePersona(String codEmpleado) { 
        String nombre = null;
        boolean seudonimo_documento = validarCodigoUsuario(codEmpleado); 
        String sqlQuery;
        try {
            setearPerfil();
            if (seudonimo_documento) {
                sqlQuery = "SELECT InitCap(NOMBRE) NOMBRE FROM PERSONAS WHERE NUMERODOCUMENTO=?";
            } else {
                sqlQuery = "SELECT InitCap(NOMBRE) NOMBRE FROM PERSONAS WHERE EMAIL=?";
            }
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, codEmpleado);
            nombre = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error getNombrePersona(): " + e);
        } 
        return nombre;
    }
    
// Validar validarUsuarioRegistrado    
    @GET
    @Path("/restKiosco/validarUsuarioRegistrado/{usuario}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarUsuarioRegistradoW(@PathParam("usuario") String usuario, @PathParam("nitEmpresa") String nitEmpresa){
        boolean res=validarUsuarioRegistrado(usuario, nitEmpresa);
        String[] parametros = {usuario, nitEmpresa};
        try {
                return Response.ok(
                response("validarUsuarioRegistrado", "Usuario: "+usuario, String.valueOf(res)), 
                    MediaType.APPLICATION_JSON)                     
                    .build();
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    
    // Validar si el usuario esta activo y pertenece a la empresa
    @GET
    @Path("/restKiosco/getCorreoPersonaEmpresa/{documento}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCorreoDocumentoEmpresa(@PathParam("documento") String usuario, @PathParam("nitEmpresa") String nitEmpresa){
        String correo = consultarCorreoPersonaEmpresa(usuario, nitEmpresa);
        return Response.ok(response("getCorreoPersonaEmpresa", "documento: "+usuario+" nitEmpresa: "+nitEmpresa, correo))              
        .build();
    }
    
    public String consultarCorreoPersonaEmpresa(String documento, String nitEmpresa) {
        String datos = null;
        try {
            String sqlQuery = "SELECT P.EMAIL email FROM EMPLEADOS e, Empresas em, personas p "
                    + "WHERE e.empresa = em.secuencia and p.secuencia=e.persona "
                    + "AND p.numerodocumento = ? "
                    + "AND em.nit = ? "
                    + "AND (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'ACTIVO' "
                    + "OR EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'PENSIONADO')";
                        Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);
            datos = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error PersistenciaConexionInicial.consultarCorreoPersonaEmpresa: " + e);
        } 
        return datos;
    }
    
        // envia token para validar cuenta
    @GET
    @Path("/restKiosco/jwtValidCuenta")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJWTValidCuenta(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave, @QueryParam("nit") String nit, @QueryParam("urlKiosco") String urlKiosco) throws UnsupportedEncodingException{
        System.out.println("UrlKiosco recibido: "+urlKiosco);
        Boolean r=validarLogin(usuario, clave, nit);
        //String passwordEncript = "Manager01*"+usuario+nit; // contraseña de encriptado Manager01*+usuario+nit
        String passwordEncript = "Manager01"; 
        boolean creaRegistro = false;
        boolean estadoEnvioCorreo = false;
        /*if (r==true) {*/
            long tiempo=System.currentTimeMillis();
            Date fechaCreacion= new Date(tiempo);
            Date fechaExpiracion= new Date(tiempo+2160000);
            //java.sql.Date fechaExpiracion =  (Date) java.sql.Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());
            String jwt=Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, passwordEncript.getBytes("UTF-8"))
                    .setSubject(usuario)
                    .setIssuedAt(fechaCreacion)
                    //.setExpiration(new Date(tiempo+21600000))
                    .setExpiration(fechaExpiracion) // 1 hora de expiración a partir de la generación del Token
                    .setIssuer("https://www.designer.com.co") //200721
                    .claim("empresa", nit)
                    .claim("documento", getDocumentoPorSeudonimo(usuario, nit))
                    .compact();
                    System.out.println("Token generado: "+jwt);
                        
                        
            EnvioCorreo p=new EnvioCorreo();
            creaRegistro = registraToken(usuario, nit, jwt, "VALIDACUENTA", fechaCreacion, fechaExpiracion);
            if (creaRegistro) {
               System.out.println("Token registrado");
               // estadoEnvioCorreo = p.enviarEnlaceValidacionCuenta("smtp.gmail.com", "465", "S", "pruebaskiosco534@gmail.com", "Nomina01", getCorreoConexioneskioskos(usuario, nit), getNombrePersona(usuario), jwt, urlKiosco);
               estadoEnvioCorreo = p.enviarEnlaceValidacionCuenta("smtp.designer.com.co", "587", "S", "kioskodesigner@designer.com.co", "Nomina01", getCorreoConexioneskioskos(usuario, nit), getNombrePersona(usuario), jwt, urlKiosco);
            }
                        
            JsonObject json=Json.createObjectBuilder()
                    .add("JWT", jwt)
                    .add("tokenRegistrado", creaRegistro)
                    .add("envioCorreo", estadoEnvioCorreo)
                    .build();
            /*new EnvioCorreo().pruebaEnvio2("smtp.gmail.com", "587", 
                "pruebaskiosco534@gmail.com", "Nomina01", "S", "thalia.manrike@gmail.com", 
                "C:\\DesignerRHN10\\Basico10\\reportesKiosko\\pruebaenvioreporte.pdf", 
                "pruebaenvioreporte.pdf", 
                "Módulo Kiosco: Validación de cuenta ", 
                "¡Bienvenido Usuario!, \n "+
                 "\n "+
                "Puedes confirmar tu email a través del siguiente enlace:\n" +
                "<b> \n "+
                "\n"+
                "http://localhost:4200/validarCuenta/"+jwt+"</b>");*/
           
 
            /*return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();*/
            return Response.status(Response.Status.CREATED).entity(json)
                    .build();
            // Usuario prueba exitosa: http://localhost:8080/restKiosco-master/wsKiosco/restapi/restKiosco/jwt/8125176/Prueba01*/811025446
        /*}else{
            ////Error cod 401
            // return Response.status(Response.Status.UNAUTHORIZED).build();
             return Response.status(Response.Status.NOT_FOUND.getStatusCode()).entity("Usuario, contraseña y/o empresa incorrectos").build();

            
        }*/
    }
    
    
    // Validar validarUsuarioSeudonimoRegistrado -> consulta seudonimo, clave y nit correctos    
    @GET
    @Path("/restKiosco/validarUsuarioSeudonimoRegistrado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarUsuarioSeudonimoRegistrado(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave, @QueryParam("nitEmpresa") String nitEmpresa){
        boolean res=validarIngresoUsuarioSeudonimoRegistrado(usuario, clave, nitEmpresa);
        String[] parametros = {usuario, nitEmpresa};
        try {
                return Response.ok(
                response("validarUsuarioSeudonimoRegistrado", "Usuario: "+usuario, String.valueOf(res)), 
                MediaType.APPLICATION_JSON)                   
                .build();
             /*return Response.ok(res, MediaType.APPLICATION_JSON)
             // return Response.status(Response.Status.CREATED).entity(r)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS, HEAD")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                    .header("'Access-Control-Allow-Credentials'", false)
                    .build();*/
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    /**
     * metodo privado para dar formato al JSON de respuesta
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater parametro de entrada
     * @param result resultado de la operacion realizada
     * @return String Respuesta en formato JSON
     */
    private String response(String operation, String parameter, String result) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("operation", operation);
            obj.put("parameter", parameter);
            obj.put("result", result);            
            return obj.toString(4);
        } catch (JSONException ex) {
            System.err.println("JSONException: " + ex.getMessage());
        }
        return "";
    }
    
    
    
    
    
    
}
