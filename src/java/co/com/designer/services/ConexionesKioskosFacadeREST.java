package co.com.designer.services;

import co.com.designer.kiosko.generales.*;
import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConexiones;
import co.com.designer.persistencia.implementacion.PersistenciaConexionesKioskos;
import co.com.designer.persistencia.implementacion.PersistenciaEmpleados;
import co.com.designer.persistencia.implementacion.PersistenciaEmpresas;
import co.com.designer.persistencia.implementacion.PersistenciaGeneralesKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaPersonas;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpleados;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpresas;
import co.com.designer.persistencia.interfaz.IPersistenciaGeneralesKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaPersonas;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import java.io.*;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.sql.Date;
import javax.json.JsonValue;
import org.json.JSONException;
import passwordGenerator.GeneradorClave;

/**
 *
 * @author Edwin Hastamorir
 */
@Stateless
@Path("conexioneskioskos")
public class ConexionesKioskosFacadeREST { //extends AbstractFacade<ConexionesKioskos> {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaConexionesKioskos persisConKio;
    private IPersistenciaGeneralesKiosko persisGenKio;
    private IPersistenciaEmpleados perisEmpleados;
    private IPersistenciaEmpleados persisEmple;
    private IPersistenciaPersonas persisPersonas;
    private IPersistenciaEmpresas persisEmpresas;

    public ConexionesKioskosFacadeREST() {
//        super(ConexionesKioskos.class);
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.persisGenKio = new PersistenciaGeneralesKiosko();
        this.perisEmpleados = new PersistenciaEmpleados();
        this.persisEmple = new PersistenciaEmpleados();
        this.persisPersonas = new PersistenciaPersonas();
        this.persisConKio = new PersistenciaConexionesKioskos();
        this.persisEmpresas = new PersistenciaEmpresas();
    }

    /*@POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(ConexionesKioskos entity) {
        super.create(entity);
    }*/

    /*@PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void edit(@PathParam("id") BigDecimal id, ConexionesKioskos entity) {
        super.edit(entity);
    }
    */

    /*@DELETE
    @Path("{id}")
    public void remove(@PathParam("id") BigDecimal id) {
        super.remove(super.find(id));
    }
    */

    /*@GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ConexionesKioskos find(@PathParam("id") BigDecimal id) {
        return super.find(id);
    }
    */

    /*
    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<ConexionesKioskos> findAll() {
        return super.findAll();
    }
    */

    /*@GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<ConexionesKioskos> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }
    */

    /*@GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    */

    @GET
    @Path("{usuario}/{pass}@{bd}")
    @Produces(MediaType.TEXT_PLAIN)
    public String validaUsuario(@PathParam("usuario") String usuario, @PathParam("pass") String pass, @PathParam("bd") String bd) {
        System.out.println("ConexionesKioskosFacadeREST.validaUsuario(): Parametros: "
                + "usuario: " + usuario
                + " , pass: " + pass
                + " , bd" + bd
        );
        return persisConKio.validaUsuario(usuario, pass, bd).toString();
    }

    // Como usar: http://ip:puerto/wsreporte/webresources/conexioneskioskos/updateFechas?usuario=8125176&nitEmpresa=811025446&fechadesde=2020-08-01&fechahasta=2020-08-31&enviocorreo=false
    @POST
    @Path("/updateFechas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFechasConexionesKioskos(@QueryParam("usuario") String usuario, @QueryParam("nitEmpresa") String nitEmpresa, @QueryParam("fechadesde") String fechadesde,
            @QueryParam("fechahasta") String fechahasta, @QueryParam("enviocorreo") boolean enviocorreo, @QueryParam("dirigidoa") String dirigidoa, @QueryParam("cadena") String cadena) {
        System.out.println("updateFechasConexionesKioskos() Parametros: seudonimo: " + usuario + ", fechadesde: " + fechadesde + ", fechahasta: " + fechahasta + ", dirigidoa: " + dirigidoa + ", cadena: " + cadena);
        int conteo = this.persisConKio.updateFechasConexionesKioskos(usuario, nitEmpresa, fechadesde,
                fechahasta, enviocorreo, dirigidoa, cadena);
        if (conteo > 0) {
            return Response.status(Response.Status.OK).entity(conteo).build();
        } else {
            return Response.status(Response.Status.GONE).entity(conteo).build();
        }
    }

    // como usar: http://localhost:8082/wsreporte/webresources/conexioneskioskos/updateClave?usuario={seudonimo}&nitEmpresa={nitempresa}&clave={nuevaClave}
    @POST
    @Path("/updateClave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClaveConexionesKioskos(@QueryParam("usuario") String usuario, @QueryParam("nitEmpresa") String nitEmpresa, @QueryParam("clave") String clave, @QueryParam("cadena") String cadena) {
        System.out.println("updateClaveConexionesKioskos() Parametros: seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa + ", clave: " + clave);
        int conteo = this.persisConKio.updateClaveConexionesKioskos(usuario, nitEmpresa, clave, cadena);
        if (conteo > 0) {
            return Response.status(Response.Status.OK).entity(conteo).build();
        } else {
            return Response.status(Response.Status.GONE).entity(conteo).build();
        }
    }

    // Como usar: http://ip:puerto/wsreporte/webresources/conexioneskioskos/updateFechas?usuario=8125176&nitEmpresa=811025446&fechadesde=2020-08-01&fechahasta=2020-08-31&enviocorreo=false
    @GET
    @Path("/parametros")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParametros(@QueryParam("usuario") String usuario, @QueryParam("nitEmpresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        System.out.println("Parametros getParametros(): seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT TO_CHAR(FECHADESDE, 'yyyy-mm-dd'), TO_CHAR(FECHAHASTA, 'yyyy-mm-dd'), ENVIOCORREO, nvl(DIRIGIDOA, ' ') "
                    + " FROM CONEXIONESKIOSKOS "
                    + " WHERE SEUDONIMO=? "
                    + " AND NITEMPRESA=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);

            s = query.getResultList();
            System.out.println("1" + s.get(0));
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + this.getClass().getName() + ".getParametros" + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity(s).build();
        }
    }

    /**
     * Como usar:
     * http://ip:puerto/wsreporte/webresources/conexioneskioskos/updateFechas?usuario=8125176&nitEmpresa=811025446&fechadesde=2020-08-01&fechahasta=2020-08-31&enviocorreo=false
     *
     * @param seudonimo
     * @param documento
     * @param clave
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    @POST
    @Path("/creaUsuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearUsuario(@QueryParam("seudonimo") String seudonimo, @QueryParam("usuario") String documento,
            @QueryParam("clave") String clave, @QueryParam("nitEmpresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros crearUsuario(): seudonimo: " + seudonimo + ", documento: " + documento + ", clave: " + clave + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String res = "creaUsuario: seudonimo: " + seudonimo + " usuario:" + documento + " clave: " + clave
                + " nitEmpresa: " + nitEmpresa;
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT COUNT(*) conteo "
                    + "FROM CONEXIONESKIOSKOS ck, PERSONAS p, EMPLEADOS e, EMPRESAS em "
                    + "WHERE ck.PERSONA = p.SECUENCIA "
                    + "AND e.persona = p.secuencia "
                    + "AND e.empresa = em.secuencia "
                    + "AND p.numerodocumento = ? "
                    + "AND em.nit = ? "
                    + "and empleadocurrent_pkg.tipotrabajadorcorte(e.secuencia, sysdate)='ACTIVO' "
                    + "AND ck.nitempresa = ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);
            query.setParameter(3, nitEmpresa);
            retorno = (BigDecimal) query.getSingleResult();
            if (retorno.compareTo(BigDecimal.ZERO) > 0) {
                resultado = false;
                mensaje = "Ya existe el usuario, por favor intente iniciar sesión.";
                System.out.println("resultado usuarioRegistrado: " + resultado + " Mensaje: " + mensaje);
            } else {
                String secEmpl = this.perisEmpleados.getSecEmplPorDocumentoYEmpresa(documento, nitEmpresa, cadena);
                String sqlQueryInsert = "INSERT INTO CONEXIONESKIOSKOS (SEUDONIMO, EMPLEADO, PERSONA, PWD, "
                        + "NITEMPRESA, ACTIVO, ULTIMACONEXION, FECHADESDE, FECHAHASTA) "
                        + "VALUES (lower(?), ?, "
                        + "(SELECT SECUENCIA FROM PERSONAS WHERE NUMERODOCUMENTO=?), "
                        + " GENERALES_PKG.ENCRYPT(?), ?, 'P', SYSDATE, TRUNC(SYSDATE, 'MM'), LAST_DAY(SYSDATE))";
                Query queryInsert = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQueryInsert);
                queryInsert.setParameter(1, seudonimo);
                queryInsert.setParameter(2, secEmpl);
                queryInsert.setParameter(3, documento);
                queryInsert.setParameter(4, clave);
                queryInsert.setParameter(5, nitEmpresa);
                conteo = queryInsert.executeUpdate();
                resultado = conteo > 0 ? true : false;
                System.out.println("resultado registrar usuario " + resultado);
                if (resultado == true) {
                    mensaje = "Usuario " + documento + " creado exitosamente!";
                }
            }
        } catch (Exception e) {
            System.out.println("Error ApiRest.crearUSuario(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        try {
            resp.put("created", resultado);
            resp.put("Mensaje", mensaje);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/cambioEstadoUsuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambioEstadoUsuarioConexionkiosko(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitEmpresa") String nitEmpresa,
            @QueryParam("activo") String activo,
            @QueryParam("cadena") String cadena) {
        System.out.println("Parametros cambioEstadoUsuarioConexionkiosko(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", activo: " + activo + ", cadena: " + cadena);
        String res = "creaUsuario: seudonimo: " + seudonimo + " nitEmpresa: " + nitEmpresa + " activo: " + activo;
        System.out.println(res);
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS SET ACTIVO=? WHERE SEUDONIMO=? AND NITEMPRESA=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
            System.out.println("Error " + ConexionesKioskos.class.getName() + ".cambioEstadoUsuario(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        try {
            resp.put("modificado", resultado);
            resp.put("Mensaje", mensaje);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/inactivaToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inactivaToken(
            @QueryParam("jwt") String jwt, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros inactivaToken(): jwt: " + jwt + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESTOKEN SET ACTIVO='N' WHERE TOKEN=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, jwt);
            conteo = query.executeUpdate();
            resultado = conteo > 0 ? true : false;
            if (conteo > 0) {
                System.out.println("modificado");
            } else {
                System.out.println("no modificado: " + resultado);
            }
        } catch (Exception e) {
            System.out.println("Error " + ConexionesKioskos.class.getName() + ".inactivaToken(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        try {
            resp.put("modificado", resultado);
            resp.put("Mensaje", mensaje);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("/inactivaTokensTipo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response inactivaTokensTipo(
            @QueryParam("tipo") String tipo,
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        System.out.println("Parametros inactivaTokensTipo(): seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", tipo: " + tipo + ", cadena: " + cadena);
        boolean resultado = false;
        int conteo = 0;
        BigDecimal retorno = null;
        JSONObject resp = new JSONObject();
        String mensaje = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESTOKEN SET ACTIVO='N' WHERE TIPO=? "
                    + "AND CONEXIONKIOSKO=(SELECT SECUENCIA FROM CONEXIONESKIOSKOS WHERE SEUDONIMO=? AND NITEMPRESA=?)";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, tipo);
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
            System.out.println("Error ApiRest.inactivaTokensTipo(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        try {
            resp.put("modificado", resultado);
            resp.put("Mensaje", mensaje);
        } catch (Exception e) {
            System.out.println("Error al crear JSON respuesta " + e.getMessage());
        }
        System.out.println(resp);

        return Response.ok(
                resp.toString(),
                MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/obtenerFoto/{imagen}")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response obtenerFoto(@PathParam("imagen") String imagen, @QueryParam("cadena") String cadena, @QueryParam("usuario") String usuario, @QueryParam("empresa") String nitEmpresa) {
        System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFoto(): " + "Parametros: "
                + "imagen: " + imagen
                + " cadena: " + cadena
                + " nitEmpresa: " + nitEmpresa
                + " usuario: " + usuario);
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = this.persisGenKio.getPathFoto(nitEmpresa, cadena);
        BigDecimal documento = null;
        try {
            if (imagen == null || imagen.contains("null") || imagen.equals("")) {
                documento = this.persisConKio.getDocumentoPorSeudonimo(usuario, nitEmpresa, cadena);
                if (documento != null) {
                    imagen = nitEmpresa + '_' + documento;
                } else {
                    imagen = "sinFoto.jpg";
                }
            }
        } catch (Exception e) {
            imagen = "sinFoto.jpg";
            System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFoto(): " + "Error-1: " + e.toString());
        }
        System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFoto(): Imagen foto perfil: " + imagen);
        try {
            fis = new FileInputStream(new File(RUTAFOTO + imagen));
            file = new File(RUTAFOTO + imagen);
        } catch (FileNotFoundException ex) {
            System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFoto(): " + "Error-2: " + ex.toString());
            try {
                fis = new FileInputStream(new File(RUTAFOTO + "sinFoto.jpg"));
                file = new File(RUTAFOTO + "sinFoto.jpg");
            } catch (FileNotFoundException ex1) {
                System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFoto(): " + "Error-4: " + ex.toString());
                Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Foto no encontrada: " + imagen, ex1);
                System.getProperty("user.dir");
                System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFoto(): " + "Ruta del proyecto: " + this.getClass().getClassLoader().getResource("").getPath());
            }
        }

        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);

        responseBuilder.header(
                "Content-Disposition", "attachment; filename=\"" + imagen + "\"");
        return responseBuilder.build();
    }

    @GET
    @Path("/obtenerLogo/{imagen}")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response obtenerLogo(@PathParam("imagen") String imagen, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros obtenerLogo(): imagen: " + imagen + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = this.persisGenKio.getPathFoto(nitEmpresa, cadena);
        try {
            fis = new FileInputStream(new File(RUTAFOTO + imagen));
            file = new File(RUTAFOTO + imagen);
            System.out.println("IMAGEN: " + RUTAFOTO + imagen);
        } catch (FileNotFoundException ex) {
            try {
                fis = new FileInputStream(new File(RUTAFOTO + "logodesigner.png"));
                file = new File(RUTAFOTO + "logodesigner.png");

            } catch (FileNotFoundException ex1) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class
                        .getName()).log(Level.SEVERE, "Logo no encontrado: " + imagen, ex1);
            }
        } finally {
            try {
                fis.close();

            } catch (IOException ex) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class
                        .getName()).log(Level.SEVERE, "Error cerrando fis " + imagen, ex);
            }
        }
        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + imagen + "\"");
        return responseBuilder.build();
    }

    @POST
    @Path("/cargarFoto")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response cargarFoto(
            @FormDataParam("fichero") InputStream fileInputStream,
            @FormDataParam("fichero") FormDataContentDisposition fileFormDataContentDisposition,
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        System.out.println("Parametros cargarFoto(): cadena: " + cadena + " ,nit: " + nitEmpresa + " , seudonimo: " + seudonimo);
        String fileName = null;
        String uploadFilePath = null;
        String secEmpl = this.persisConKio.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        boolean resultado = false;
        System.out.println("seudonimo cargarfoto " + seudonimo);
        int conteo = 0;
        String mensaje = "";

        try {
            fileName = fileFormDataContentDisposition.getFileName();
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS CK SET CK.FOTOPERFIL = '" + fileName + "' \n"
                    + "WHERE CK.EMPLEADO= ?  ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            conteo = query.executeUpdate();
            resultado = conteo > 0 ? true : false;
            if (conteo > 0) {
                System.out.println("modificado");
            } else {
                System.out.println("no modificado: " + resultado);

            }
        } catch (Exception e) {
            System.out.println("Error " + ConexionesKioskos.class
                    .getName() + ".cargarFoto(): " + e.getMessage());
            mensaje = "Ha ocurrido un error al subir la foto " + e.getMessage();
        }

        try {

            uploadFilePath = writeToFileServer(fileInputStream, fileName, nitEmpresa, cadena);

        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error: " + ioe.getMessage());
        }
        return Response.ok("Fichero subido a " + uploadFilePath).build();
    }

    private String writeToFileServer(InputStream inputStream, String fileName, String nitEmpresa, String cadena) throws IOException {

        OutputStream outputStream = null;
        String qualifiedUploadFilePath = this.persisGenKio.getPathFoto(nitEmpresa, cadena) + fileName;

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
    @Path("/datosContactoKiosco/{nit}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatosContactoKiosco(@PathParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        BigDecimal res = null;
        List datos = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT K.NOMBRECONTACTO NOMBRE, K.EMAILCONTACTO EMAIL, TELEFONOCONTACTO TELEFONO "
                    + "FROM KIOPERSONALIZACIONES K, EMPRESAS EM "
                    + "WHERE K.EMPRESA=EM.SECUENCIA "
                    + "AND K.TIPOCONTACTO = 'NOMINA' "
                    + "AND EM.NIT= ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            datos = query.getResultList();
        } catch (Exception ex) {
            System.out.println("Usando la consulta tradicional para los datos de contacto");
            try {
                String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
                this.rolesBD.setearPerfil(esquema, cadena);
                String sqlQuery = "SELECT K.NOMBRECONTACTO NOMBRE, K.EMAILCONTACTO EMAIL, TELEFONOCONTACTO TELEFONO "
                        + "FROM KIOPERSONALIZACIONES K, EMPRESAS EM "
                        + "WHERE K.EMPRESA=EM.SECUENCIA "
                        + "AND EM.NIT= ? ";
                Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, nitEmpresa);
                datos = query.getResultList();
            } catch (Exception exi) {
                System.out.println("ex: " + exi);
                res = BigDecimal.ZERO;
            }
        }
        return Response.status(Response.Status.OK).entity(datos).build();
    }

    @GET
    @Path("/validarIngresoSeudonimoKiosco")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarIngresoSeudonimoKiosco(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave,
            @QueryParam("nitEmpresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("ConexionesKioskosFacadeREST" + ".validarIngresoSeudonimoKiosco(): " + "Parametros: "
                + "usuario: " + usuario
                + " clave: " + clave
                + " nitEmpresa: " + nitEmpresa
                + " cadenaConexion: " + cadena);
        boolean ingresoExitoso = false;
        boolean primeringreso = true;
        boolean validaEmail = false;
        String estadoUsuario = "";
        this.cadenasKio.getEsquema(nitEmpresa, cadena);
        String mensaje = "Error no controlado, por favor inténtelo nuevamente.";
        List rs = null;
        BigDecimal documento = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            try {
                documento = this.persisConKio.getDocumentoPorSeudonimo(usuario, nitEmpresa, cadena);
                System.out.println("Consulta documento por Seudonimo: " + documento);
                if (documento == null) {
                    documento = getDocumentoCorreoODocumento(usuario, nitEmpresa, cadena);
                    System.out.println("Consulta documento por email o documento: " + documento);
                }
            } catch (Exception e) {
                documento = getDocumentoCorreoODocumento(usuario, nitEmpresa, cadena);
                System.out.println("Consulta 3 documento por email o documento: " + documento);
            }
            System.out.println("documento asociado a correo o documento: " + usuario + " es " + documento);
            if (documento != null) {
                System.out.println("Ingresa ciclo documento!=null");
                if (validarUsuarioyEmpresa(documento.toString(), nitEmpresa, cadena)) { // valida si existe relacion con la empresa y se encuentra activo
                    if (validarUsuarioRegistrado(documento.toString(), nitEmpresa, cadena)) { // relacion entre conexioneskioskos, empleados y personas
                        System.out.println("usuario " + usuario + " esta registrado en conexioneskioskos");
                        if (validarSeudonimoRegistrado(usuario, nitEmpresa, cadena)) {
                            System.out.println("El seudonimo  " + usuario + " existe y esta relacionado con la empresa seleccionada " + nitEmpresa);
                            /// usuario no validado
                            estadoUsuario = validarEstadoUsuarioSeudonimo(usuario, nitEmpresa, cadena);
                            System.out.println("validarEstadoUsuarioSeudonimo: " + estadoUsuario);
                            if (estadoUsuario.equals("P")) {
                                System.out.println("El usuario " + usuario + " no ha validado su cuenta. Estado PENDIENTE");
                                ingresoExitoso = false;
                                primeringreso = false;
                                validaEmail = true;
                                mensaje = "Cuenta no validada. Por favor valida tu cuenta desde el enlace que se te envió al correo al momento de registrarte.";
                                System.out.println("Mensaje: " + mensaje);
                            } else if (estadoUsuario.equals("S")) {
                                System.out.println("El estado del usuario " + usuario + " es activo: S");
                                if (validarIngresoUsuarioSeudonimoRegistrado(usuario, clave, nitEmpresa, cadena)) {
                                    try {
                                        if (!this.perisEmpleados.validarCodigoUsuario(usuario)) {
                                            System.out.println("El usuario es un correo " + usuario);
                                            if (!this.persisConKio.validarSeudonimoCorreo(usuario, nitEmpresa, cadena)) {
                                                System.out.println("Seudonimo de correo diferente " + usuario);
                                                usuario = this.persisConKio.updateCorreoSeudonimo(usuario, nitEmpresa, cadena);
                                                System.out.println("Seudonimo Actualizado " + usuario);
                                                cambioEstadoSeudonimo(usuario, nitEmpresa, "P", cadena);
                                                estadoUsuario = validarEstadoUsuarioSeudonimo(usuario, nitEmpresa, cadena);
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Genero error actulización de Seudonimo (785) " + e);
                                    }
                                    System.out.println("estado del conexiones kiosko " + estadoUsuario);
                                    if (estadoUsuario.equals("P")) {
                                        mensaje = "El usuario nuevo es: " + usuario + "; Por valor validar cuenta";
                                        System.out.println("Mensaje: " + mensaje);
                                        primeringreso = false;
                                        validaEmail = true;
                                    } else {
                                        mensaje = "El usuario que ingresa es: " + usuario;
                                        System.out.println("Mensaje: " + mensaje);
                                        ingresoExitoso = true;
                                    }

                                } else {
                                    // LA CONTRASEÑA ES INCORRECTA.
                                    ingresoExitoso = false;
                                    mensaje = "La contraseña es inválida.";
                                    System.out.println("La contraseña digita por el usuario " + usuario + " es incorrecta");
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
                            System.out.println("El usuario " + usuario + " no es correcto");
                        }
                    } else {
                        ingresoExitoso = false;
                        primeringreso = false;
                        mensaje = "El usuario no existe. Si no tiene un usuario utilice la opción de Registrarse.";
                        System.out.println("El usuario " + usuario + " no existe. No ha creado su cuenta");
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
            System.out.println("Error: validarIngresoSeudonimoKiosco: " + e.getMessage());
        }

        try {
            JsonObject json = Json.createObjectBuilder()
                    .add("primerIngreso", primeringreso)
                    .add("ingresoExitoso", ingresoExitoso)
                    .add("EstadoUsuario", estadoUsuario)
                    .add("ValidaEmail", validaEmail)
                    .add("Correo", usuario)
                    .add("mensaje", mensaje).
                    build();
            return Response.status(Response.Status.CREATED).entity(json)
                    .build();

        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    public BigDecimal getDocumentoCorreoODocumento(String usuario, String nitEmpresa, String cadena) {
        System.out.println("Parametros getDocumentoCorreoODocumento() usuario: " + usuario + ", cadena: " + cadena);
        BigDecimal documento = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P WHERE lower(P.EMAIL)=lower(?)";
            if (this.perisEmpleados.validarCodigoUsuario(usuario)) {
                sqlQuery += " OR P.NUMERODOCUMENTO=?"; // si el valor es numerico validar por numero de documento
            }
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, usuario);
            if (this.perisEmpleados.validarCodigoUsuario(usuario)) {
                query.setParameter(2, usuario);
            }
            documento = new BigDecimal(query.getSingleResult().toString());

        } catch (Exception e) {
            System.out.println("Error: " + ConexionesKioskosFacadeREST.class
                    .getName() + " getDocumentoCorreoODocumento: " + e.getMessage());
            documento = this.perisEmpleados.getDocumentoXUsuario(cadena, usuario);
        }
        return documento;
    }

    public boolean validarUsuarioyEmpresa(String documento, String nitEmpresa, String cadena) {
        System.out.println("Parametros validarUsuarioyEmpresa(): documento: " + documento + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        boolean resultado = false;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT COUNT(*) count "
                    + " FROM EMPLEADOS e, Empresas em, Personas p "
                    + " WHERE e.empresa = em.secuencia "
                    + " AND P.SECUENCIA=E.PERSONA "
                    + " AND P.NUMERODOCUMENTO = ? "
                    + " AND em.nit = ? "
                    + " AND (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) IN ('ACTIVO','PENSIONADO')) ";
            System.out.println("validarUsuarioyEmpresa() Parametros[ documento: " + documento + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena + "]");
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);

            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            if (instancia == 0) {
                sqlQuery = "SELECT COUNT(*) count "
                        + " FROM KIOAUTORIZADORES e, Personas p "
                        + " , KioAutorizaSoliciVacas kas, Empleados empl, Empresas em "
                        + " WHERE P.SECUENCIA=E.PERSONA "
                        + " AND em.secuencia = empl.empresa "
                        + " AND empl.secuencia = kas.empleado "
                        + " AND kas.kioautorizador = e.secuencia "
                        + " AND P.NUMERODOCUMENTO = ? "
                        + " AND em.nit = ? ";
                System.out.println("validarUsuarioyEmpresa() Autorizadores Parametros[ documento: " + documento + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena + "]");
                System.out.println("Query: " + sqlQuery);
                query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, documento);
                query.setParameter(2, nitEmpresa);

                retorno = (BigDecimal) query.getSingleResult();
                instancia = retorno.intValueExact();
            }
            resultado = instancia > 0;
            System.out.println("resultado validarUsuarioyEmpresa: " + instancia);

        } catch (Exception ex) {
            System.out.println("Error: " + ConexionesKioskos.class
                    .getName() + " validarUsuarioyEmpresa: " + ex.getMessage());
        }
        return resultado;
    }

    /**
     * metodo para cambiar el estado del seudonimo
     *
     * @param usuario
     * @param nitEmpresa
     * @param activo
     * @param cadena
     * @return String Respuesta en formato boolean
     */
    public boolean cambioEstadoSeudonimo(String usuario, String nitEmpresa, String activo, String cadena) {
        System.out.println("Parametros cambioEstadoUsuarioConexionkiosko(): seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa + ", activo: " + activo + ", cadena: " + cadena);
        String res = "creaUsuario: seudonimo: " + usuario + " nitEmpresa: " + nitEmpresa + " activo: " + activo;
        System.out.println(res);
        boolean resultado = false;
        int conteo = 0;
        String mensaje = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS SET ACTIVO=? WHERE SEUDONIMO=? AND NITEMPRESA=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, activo);
            query.setParameter(2, usuario);
            query.setParameter(3, nitEmpresa);
            conteo = query.executeUpdate();
            resultado = conteo > 0; //? true : false;
            if (conteo > 0) {
                System.out.println("modificado");
            } else {
                System.out.println("no modificado: " + resultado);

            }
        } catch (Exception e) {
            System.out.println("Error " + ConexionesKioskos.class
                    .getName() + ".cambioEstadoUsuario(): " + e.getMessage());
            mensaje = "Ha ocurrido un error " + e.getMessage();
        }

        return resultado;
    }

    public boolean validarUsuarioRegistrado(String documento, String nitEmpresa, String cadena) {
        System.out.println("Parametros validarUsuarioRegistrado() [ numerodoDocumento: " + documento + ", nitEmpresa: " + nitEmpresa + " cadena: " + cadena + " ]");
        boolean resultado = false;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT COUNT(*) count "
                    + "FROM CONEXIONESKIOSKOS ck, PERSONAS per, EMPLEADOS e, EMPRESAS em "
                    + "WHERE ck.PERSONA = per.SECUENCIA "
                    + "AND e.persona = per.secuencia "
                    + "AND e.empresa = em.secuencia "
                    + "AND em.nit = ck.nitempresa "
                    + "AND per.numerodocumento = ? "
                    + "AND em.nit = ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            System.out.println("ConexionesKioskosFacadeREST" + ".validarUsuarioRegistrado(): retorno: " + retorno);
            if (retorno.equals(BigDecimal.ZERO)) {
                sqlQuery = "SELECT COUNT(*) count "
                        + " FROM ConexionesKioskos ck, KIOAUTORIZADORES e, Personas p "
                        + " WHERE P.SECUENCIA=E.PERSONA "
                        + " AND ck.persona = p.secuencia "
                        + " AND P.NUMERODOCUMENTO = ? "
                        + " AND EXISTS (SELECT 'X' "
                        + "  FROM KioAutorizaSoliciVacas kas, Empleados empl, Empresas em "
                        + "  WHERE em.secuencia = empl.empresa "
                        + "  AND empl.secuencia = kas.empleado "
                        + "  AND kas.kioautorizador = e.secuencia "
                        + "  AND em.nit = ? ) "
                        + " AND ck.nitempresa = ? ";
                System.out.println("Query_autorizadores: " + sqlQuery);
                query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, documento);
                query.setParameter(2, nitEmpresa);
                query.setParameter(3, nitEmpresa);
                retorno = (BigDecimal) query.getSingleResult();
            }
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
        } catch (Exception e) {
            System.out.println("Error : " + "ConexionesKioskosFacadeREST" + ".validarUsuarioRegistrado " + e.getMessage());
        }
        return resultado;
    }

    public boolean validarSeudonimoRegistrado(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("validarSeudonimoRegistrado()");
        boolean resultado = false;
        String sqlQuery = "SELECT COUNT(*) count "
                + "FROM CONEXIONESKIOSKOS ck, PERSONAS per, EMPLEADOS e, EMPRESAS em "
                + "WHERE "
                + "ck.persona = per.secuencia "
                + "AND e.persona = per.secuencia "
                + "AND e.empresa = em.secuencia "
                + "AND em.nit = ck.nitempresa "
                + "AND lower(ck.SEUDONIMO) = ? "
                + "AND em.nit = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            System.out.println("Parametros: validarSeudonimoRegistrado(): [ seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            if (retorno.equals(BigDecimal.ZERO)) {
                System.out.println("ConexionesKioskosFacadeREST" + ".validarSeudonimoRegistrado(): " + "Consultado Autorizadores ");
                sqlQuery = "SELECT COUNT(*) count "
                        + "FROM ConexionesKioskos ck, Personas p "
                        + " WHERE ck.persona = p.secuencia "
                        + " AND LOWER(ck.SEUDONIMO) = ? "
                        + " AND EXISTS (SELECT 'X' "
                        + "  FROM KioAutorizadores e, KioAutorizaSoliciVacas kas, Empleados empl, Empresas em "
                        + "  WHERE em.secuencia = empl.empresa "
                        + "  AND empl.secuencia = kas.empleado "
                        + "  AND p.secuencia=e.persona "
                        + "  AND kas.kioautorizador = e.secuencia "
                        + "  AND em.nit = ? "
                        + " )"
                        + " AND ck.nitempresa = ? ";
                try {
                    esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
                    this.rolesBD.setearPerfil(esquema, cadena);
                    System.out.println("Parametros: validarSeudonimoRegistrado(): autorizadores: [ seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
                    System.out.println("Query_autroizadores: " + sqlQuery);
                    query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                    query.setParameter(1, seudonimo);
                    query.setParameter(2, nitEmpresa);
                    query.setParameter(3, nitEmpresa);
                    retorno = (BigDecimal) query.getSingleResult();
                    instancia = retorno.intValueExact();
                } catch (Exception ex) {
                    System.out.println("ConexionesKioskosFacadeREST" + ".validarSeudonimoRegistrado(): " + "Error: " + ex.getMessage());
                    System.out.println("ConexionesKioskosFacadeREST" + ".validarSeudonimoRegistrado(): " + "Query: " + sqlQuery);
                }
            }
            resultado = instancia > 0;
        } catch (Exception e) {
            System.out.println("ConexionesKioskosFacadeREST" + ".validarSeudonimoRegistrado(): " + "Error: " + e.getMessage());
            System.out.println("ConexionesKioskosFacadeREST" + ".validarSeudonimoRegistrado(): " + "Query: " + sqlQuery);
        }
        return resultado;
    }

    public String validarEstadoUsuarioSeudonimo(String usuario, String nitEmpresa, String cadena) { // retorna true si el usuario esta activo
        System.out.println("validarEstadoUsuarioSeudonimo()");
        String retorno = null;
        String sqlQuery = "SELECT ck.ACTIVO estado "
                + "FROM CONEXIONESKIOSKOS ck, PERSONAS per, EMPLEADOS e, EMPRESAS em "
                + "WHERE ck.EMPLEADO = e.SECUENCIA "
                + "AND per.secuencia = e.persona "
                + "AND e.empresa = em.secuencia "
                + "AND ck.nitempresa = em.nit "
                + "AND lower(ck.seudonimo) = ? "
                + "AND em.nit = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            System.out.println("validarEstadoUsuarioSeudonimo() Parametros: [usuario: " + usuario + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena + "]");
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
            retorno = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: " + "ConexionesKioskosFacadeREST" + ".validarEstadoUsuarioSeudonimo(): " + e.getMessage());
            System.out.println("ConexionesKioskosFacadeREST" + ".validarEstadoUsuarioSeudonimo(): " + "consultado KioAutorizadores");
            try {
                sqlQuery = "SELECT ck.activo estado "
                        + " FROM ConexionesKioskos ck, Personas p "
                        + " WHERE ck.persona = p.secuencia "
                        + " AND p.numerodocumento = ? "
                        + " AND EXISTS (SELECT 'X' "
                        + "  FROM KioAutorizadores e, KioAutorizaSoliciVacas kas, Empleados empl, Empresas em "
                        + "  WHERE em.secuencia = empl.empresa "
                        + "  AND empl.secuencia = kas.empleado "
                        + "  AND p.secuencia=e.persona "
                        + "  AND kas.kioautorizador = e.secuencia "
                        + "  AND em.nit = ? "
                        + " )"
                        + " AND ck.nitempresa = ? ";
                String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
                this.rolesBD.setearPerfil(esquema, cadena);
                System.out.println("validarEstadoUsuarioSeudonimo() Parametros: [usuario: " + usuario + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena + "]");
                System.out.println("Query_autorizadores: " + sqlQuery);
                Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, usuario);
                query.setParameter(2, nitEmpresa);
                query.setParameter(3, nitEmpresa);
                retorno = query.getSingleResult().toString();
            } catch (Exception ex) {
                System.out.println("Error: " + "ConexionesKioskosFacadeREST" + ".validarEstadoUsuarioSeudonimo(): " + e.getMessage());
                System.out.println("ConexionesKioskosFacadeREST" + ".validarEstadoUsuarioSeudonimo(): " + "consultado KioAutorizadores");
            }
        }
        return retorno;
    }

    public boolean validarIngresoUsuarioSeudonimoRegistrado(String seudonimo, String clave, String nitEmpresa, String cadena) { // retorna true si el usuario esta activo
        boolean resultado = false;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT COUNT(*) count "
                    + "FROM CONEXIONESKIOSKOS ck, Personas per "
                    + "WHERE ck.PERSONA = per.SECUENCIA "
                    + "AND ck.activo = 'S' "
                    + "AND lower(ck.seudonimo) = ? "
                    + "AND ck.PWD = GENERALES_PKG.ENCRYPT(?) "
                    + "AND ck.nitempresa = ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, clave);
            query.setParameter(3, nitEmpresa);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
        } catch (Exception e) {
            System.out.println("Error: validarIngresoUsuarioSeudonimoRegistrado: " + e.getMessage());
        }
        return resultado;
    }

    // token de inicio de sesion
    // como usar: http://localhost:8080/restKiosco-master/wsKiosco/restapi/restKiosco/jwt?usuario=8125176&clave=Prueba01*&nit=811025446 
    @POST
    @Path("/restKiosco/jwt")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJWT(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave, @QueryParam("nit") String nit,
            @QueryParam("cadena") String cadena, @QueryParam("grupo") String grupo) throws UnsupportedEncodingException {
        System.out.println("Parametros getJWT(): usuario: " + usuario + ", clave: " + cadena + ", nit: " + nit + ", cadena: " + cadena + ", grupo: " + grupo);
        Boolean r = validarLogin(usuario, clave, nit, cadena);
        Boolean registroToken = false;
        String passwordEncript = "Manager01";
        long tiempo = System.currentTimeMillis();
        Date fechaCreacion = new Date(tiempo);
        Calendar fechaExpiracion = Calendar.getInstance();
        fechaExpiracion.setTime(new java.util.Date());
        fechaExpiracion.add(Calendar.DAY_OF_YEAR, 30);  // 30 dias a partir de la fecha*/
        BigDecimal documento = this.persisConKio.getDocumentoPorSeudonimo(usuario, nit, cadena);
        String jwtCompleto = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, passwordEncript.getBytes("UTF-8"))
                .setSubject(usuario)
                .setIssuedAt(fechaCreacion)
                .setExpiration(fechaExpiracion.getTime())
                .setIssuer("https://www.designer.com.co") //200721
                .claim("empresa", nit)
                .claim("documento", documento)
                .claim("cadena", cadena)
                .claim("grupo", grupo)
                .compact();
        System.out.println("Token generado: " + jwtCompleto);

        Encriptacion e = new Encriptacion();
        String jwt = e.encrypt(jwtCompleto, passwordEncript);
        System.out.println("Token encriptado: " + jwt);

        registroToken = registraToken(usuario, nit, jwt, "LOGIN", fechaCreacion, fechaCreacion, cadena);

        JsonObject json = Json.createObjectBuilder()
                .add("JWT", jwt)
                .add("registroToken", registroToken)
                .build();

        return Response.status(Response.Status.CREATED).entity(json)
                .build();
    }

    public boolean validarLogin(String codEmple, String clave, String nitEmpresa, String cadena) {
        System.out.println("Parametros validarLogin(): codEmple: " + codEmple + ", clave: " + clave + ", nit: " + nitEmpresa + ", cadena: " + cadena);
        boolean res = false;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sql = "select count(*) as total "
                    + "from conexioneskioskos ck "
                    + "where "
                    + "    lower(ck.seudonimo) = ? "
                    + "and ck.pwd=generales_pkg.encrypt(?) "
                    + " and ck.nitempresa=?";
            System.out.println(sql);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sql);
            query.setParameter(1, codEmple);
            query.setParameter(2, clave);
            query.setParameter(3, nitEmpresa);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            res = instancia > 0;
        } catch (Exception e) {
            System.out.println("Error validarLogin() " + e.getMessage() + " " + e.getLocalizedMessage());
            res = false;
        }
        return res;
    }

    public boolean registraToken(String usuario, String nitEmpresa, String token, String tipo, Date fechacreacion, java.sql.Date fechaexpira, String cadena) {
        int resCon = 0;
        boolean resultado = false;
        try {
            System.out.println("Parametros registraToken(): usuario: " + usuario + ", nitEmpresa: " + nitEmpresa + ", token: " + token + ", "
                    + "\n tipo: " + tipo + ", fechaCreacion: " + fechacreacion + ", fechaExpira: " + fechaexpira + ", cadena: " + cadena);
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sql = "INSERT INTO CONEXIONESTOKEN (CONEXIONKIOSKO, TOKEN, FECHACREACION, FECHAEXPIRACION, TIPO) VALUES "
                    + "((SELECT SECUENCIA FROM CONEXIONESKIOSKOS WHERE SEUDONIMO =? AND NITEMPRESA=?),"
                    + " ?, ?, ?, ? )";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sql);
            System.out.println("Query: " + sql);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
            query.setParameter(3, token);
            query.setParameter(4, (java.sql.Date) fechacreacion);
            query.setParameter(5, fechaexpira);
            query.setParameter(6, tipo);
            resCon = query.executeUpdate();
            resultado = resCon > 0;
            System.out.println("Resultado registra token: " + resultado);
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".registraToken: " + ex.getMessage());
        }
        return resultado;
    }

    @GET
    @Path("/restKiosco/validarJWTActivarCuenta") // verificar nombre
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarJWT(@QueryParam("jwt") String jwt, @QueryParam("cadena") String cadena) {
        System.out.println("validarJWT() jwt: " + jwt + ","
                + "ValidatJWT() cadena: " + cadena);
        boolean validoToken = false;
        String mensaje = "";
        String usuario = "";
        String nit = "";
        String documento = "";
        JsonObject json;
        String cadenaToken = "";
        String grupoEmpresarial = "";
        Encriptacion enc = new Encriptacion();
        String passwordEncriptacion = "Manager01";
        String jwtOriginal = enc.decrypt(jwt, passwordEncriptacion);
        System.out.println("original: " + jwt);
        System.out.println("original1: " + jwtOriginal);
        if (jwtOriginal.equals("N") || jwtOriginal == "N") {
            System.out.println("Entre al if");
            jwtOriginal = jwt;
        }
        System.out.println("original2: " + jwtOriginal);
        try {
            usuario = (String) Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwtOriginal).getBody().getSubject();
            nit = (String) Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwtOriginal).getBody().get("empresa");
            Object obDocumento = Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwtOriginal).getBody().get("documento");
            if (obDocumento instanceof Integer) {
                documento = Integer.toString((Integer) obDocumento);
            } else if (obDocumento instanceof String) {
                documento = (String) obDocumento;
            }
            cadenaToken = (String) Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwtOriginal).getBody().get("cadena");
            grupoEmpresarial = (String) Jwts.parser().setSigningKey("Manager01".getBytes("UTF-8")).parseClaimsJws(jwtOriginal).getBody().get("grupo");
            System.out.println("Información extraida token validaCuenta: usuario: " + usuario + ", nit: " + nit + ", documento: " + documento + ", cadenaToken: " + cadena + ", grupoEmpr: " + grupoEmpresarial);

            try {
                String esquema = this.cadenasKio.getEsquema(nit, cadenaToken);
                this.rolesBD.setearPerfil(esquema, cadena);
                if (validarTokenExistente(jwt, nit, cadenaToken)) {
                    if (getEstadoToken(jwt, nit, cadenaToken).equals("S")) {
                        System.out.println("Token existe en bd");
                        Jwts.parser()
                                .setSigningKey(passwordEncriptacion.getBytes("UTF-8"))
                                .parseClaimsJws(
                                        jwtOriginal
                                );

                        //OK, we can trust this JWT
                        System.out.println("token válido");
                        validoToken = true;
                        mensaje = "Token válido";

                    } else {
                        validoToken = false;
                        // mensaje="El token no es válido o está expirado";
                        mensaje = "El enlace no es válido o se ha expirado.";
                        System.out.println("Estado del Token es distinto de S");
                    }

                } else {
                    //mensaje = "El token no es válido";
                    mensaje = "El enlace no es válido";
                    System.out.println("El token no es valido o no existe en la bd");
                    validoToken = false;
                    json = Json.createObjectBuilder()
                            .add("validoToken", validoToken)
                            .add("mensaje", mensaje)
                            .add("documento", documento)
                            .add("cadena", cadenaToken)
                            .add("grupo", grupoEmpresarial)
                            .build();
                    return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
                            .build();
                }

            } catch (SignatureException e) {

                //don't trust the JWT!
                System.out.println("Error, jwt no válido " + e.getMessage());
                validoToken = false;
                mensaje = "Error, jwt no válido " + e.getMessage();
                json = Json.createObjectBuilder()
                        .add("validoToken", validoToken)
                        .add("mensaje", mensaje)
                        .add("documento", documento)
                        .add("cadena", cadenaToken)
                        .add("grupo", grupoEmpresarial)
                        .build();
                return Response.status(Response.Status.NOT_FOUND.getStatusCode()).entity(json)
                        .build();
            } catch (io.jsonwebtoken.ExpiredJwtException exp) {
                System.out.println("Token expirado. " + exp.getMessage());
                validoToken = false;
                //mensaje ="El token se ha expirado";
                mensaje = "El enlace se ha expirado";
                System.out.println(mensaje);
                json = Json.createObjectBuilder()
                        .add("validoToken", validoToken)
                        .add("mensaje", mensaje)
                        .add("documento", documento)
                        .add("cadena", cadenaToken)
                        .add("grupo", grupoEmpresarial)
                        .build();
                return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
                        .build();
            } catch (UnsupportedEncodingException ex) {
                System.out.println("Error ");
                validoToken = false;
                //mensaje="El token no es válido";
                mensaje = "El enlace no es válido";
                json = Json.createObjectBuilder()
                        .add("validoToken", validoToken)
                        .add("mensaje", mensaje)
                        .add("documento", documento)
                        .add("cadena", cadenaToken)
                        .add("grupo", grupoEmpresarial)
                        .build();
                return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
                        .build();
            } catch (io.jsonwebtoken.MalformedJwtException emj) {
                validoToken = false;
                //mensaje = "El jwt no tiene un formato válido";
                mensaje = "El enlace no es válido";
                System.out.println("El token no tiene un formato válido");
                json = Json.createObjectBuilder()
                        .add("validoToken", validoToken)
                        .add("mensaje", mensaje)
                        .add("documento", documento)
                        .add("cadena", cadenaToken)
                        .add("grupo", grupoEmpresarial)
                        .build();
                return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity("El jwt no tiene un formato válido")
                        .build();
            } catch (Exception e) {
                validoToken = false;
                mensaje = "El enlace no es válido. " + e.getMessage();

            }

        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error " + ConexionesKioskos.class
                    .getName() + "validarJWT: " + ex.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException ie) {
            System.out.println("Error Expiración de token: " + ie.getMessage());
            mensaje = "El enlace se ha expirado, intente iniciar sesión nuevamente para generar uno nuevo.";
            validoToken = false;
            json = Json.createObjectBuilder()
                    .add("validoToken", validoToken)
                    .add("mensaje", mensaje)
                    .add("documento", documento)
                    .add("cadena", cadena)
                    .add("grupo", grupoEmpresarial)
                    .build();
            return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
                    .build();
        }

        json = Json.createObjectBuilder()
                .add("validoToken", validoToken)
                .add("mensaje", mensaje)
                .add("usuario", usuario)
                .add("empresa", nit)
                .add("documento", documento)
                .add("cadena", cadenaToken)
                .add("grupo", grupoEmpresarial)
                .build();

        return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(json)
                .build();
    }

    // validar si existe token
    public boolean validarTokenExistente(String token, String nitEmpresa, String cadena) {
        System.out.println("Parametros validarTokenExistente(): token: " + token + ", cadena: " + cadena);
        boolean resultado = false;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select count(*) as count from conexionestoken where token=? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, token);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            resultado = instancia > 0;
            System.out.println("Resultado validarTokenExistente: " + resultado);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".validarTokenExistente() " + e.getMessage());
            resultado = false;
        }
        return resultado;
    }

    /**
     * Retorna true si el usuario esta activo
     *
     * @param token
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    public String getEstadoToken(String token, String nitEmpresa, String cadena) {
        System.out.println("Parametros getEstadoToken(): token: " + token + ", cadena: " + cadena);
        String estado = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT ACTIVO FROM CONEXIONESTOKEN WHERE TOKEN = ?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, token);
            estado = query.getSingleResult().toString();
            System.out.println("Resultado getEstadoToken(): " + estado);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getEstadoToken(): " + e);
        }
        return estado;
    }

    @GET
    @Path("/restKiosco/documentoconexioneskioskos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentoConexioneskioskos(@QueryParam("seudonimo") String usuario, @QueryParam("nit") String nit, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros getDocumentoConexioneskioskos(): seudonimo: " + usuario + ", nit: " + nit + ", cadena: " + cadena);
        BigDecimal r = this.persisConKio.getDocumentoPorSeudonimo(usuario, nit, cadena);
        String[] parametros = {usuario, nit};
        return Response.ok(
                response("documentoconexioneskioskos", "Usuario: " + usuario + ", nit: " + nit, String.valueOf(r)), MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/restKiosco/logoEmpresa/{nit}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getLogoEmpresa(@PathParam("nit") String nit, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros getLogoEmpresa(): nit: " + nit + ", cadena: " + cadena);
        String r = getLogoEmpresaS(nit, cadena);
        return Response.ok(r, MediaType.TEXT_PLAIN)
                .build();
    }

    public String getLogoEmpresaS(String nitEmpresa, String cadena) {
        ResultSet rs = null;
        String logo = "";
        JSONObject logoE = new JSONObject();
        try {
            logo = this.persisEmpresas.getLogoEmpresa(nitEmpresa, cadena);
            logoE.put("LOGO", logo.substring(0, logo.length() - 4));
        } catch (JSONException ex) {
            System.out.println("ConexionesKioskosFacadeREST" + ".getLogoEmpresaS(): " + "Error-1: " + ex.toString());
            Logger
                    .getLogger(ConexionesKioskosFacadeREST.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return logoE.toString();
    }

    @GET
    @Path("/restKiosco/correoconexioneskioskos/{usuario}/{nit}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCorreoConexioneskioskoS(@PathParam("usuario") String usuario, @PathParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros getCorreoConexioneskioskoS(): usuario: " + usuario + ", nit: " + nitEmpresa + ", cadena: " + cadena);
        String r = this.persisPersonas.getCorreoConexioneskioskos(usuario, nitEmpresa, cadena);
        String[] parametros = {usuario, nitEmpresa};
        return Response.ok(
                response("correoConexioneskioskos", "Usuario: " + usuario + ", nit: " + nitEmpresa,
                        String.valueOf(r)), MediaType.APPLICATION_JSON)
                .build();
    }

    /**
     * Validar si el usuario esta activo y pertenece a la empresa
     *
     * @param usuario
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    @GET
    @Path("/restKiosco/validarUsuarioyEmpresa/{usuario}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarUsuarioyEmpresaS(@PathParam("usuario") String usuario, @PathParam("nitEmpresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        boolean res = validarUsuarioyEmpresa(usuario, nitEmpresa, cadena);
        String[] parametros = {usuario, nitEmpresa};
        try {
            return Response.ok(
                    response("validarUsuarioyEmpresa", "Usuario: " + usuario + ", "
                            + "nitEmpresa: " + nitEmpresa, String.valueOf(res)),
                    MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Validar si el usuario esta registrado y pertenece a la empresa
     *
     * @param usuario
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    @GET
    @Path("/restKiosco/validarSeudonimoyEmpresaRegistrado/{usuario}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarSeudonimoyEmpresaRegistrado(@PathParam("usuario") String usuario, @PathParam("nitEmpresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        boolean res = validarSeudonimoRegistrado(usuario, nitEmpresa, cadena);
        String[] parametros = {usuario, nitEmpresa};
        try {
            return Response.ok(
                    response("validarSeudonimoyEmpresaRegistrado", "Usuario: " + usuario + ", "
                            + "nitEmpresa: " + nitEmpresa, String.valueOf(res)),
                    MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("/restKiosco/generarClave")
    public Response generadorClaveAleatoria(@QueryParam("usuario") String usuario, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros generadorClaveAleatoria(): usuario: " + usuario + ", nit: " + nitEmpresa + ", cadena: " + cadena);
        String nuevaClave = GeneradorClave.generatePassword(); // generador de contraseña aleatoria
        boolean envioCorreo = false;
        boolean updateClave = false;
        updateClave = actualizarClave(usuario, nitEmpresa, nuevaClave, cadena);
        String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        String correo = this.persisPersonas.getCorreoConexioneskioskos(usuario, nitEmpresa, cadena);
        System.out.println("este es el corre que se va a enviar" + correo);
        try {
            if (updateClave) {
                System.out.println("Contraseña actualizada a: " + nuevaClave);
                EnvioCorreo e = new EnvioCorreo();
                envioCorreo = e.enviarNuevaClave(
                        getConfigServidorSMTP(nitEmpresa, cadena),
                        getConfigCorreo(nitEmpresa, "PUERTO", cadena),
                        getConfigCorreo(nitEmpresa, "AUTENTICADO", cadena),
                        getConfigCorreo(nitEmpresa, "STARTTLS", cadena),
                        getConfigCorreo(nitEmpresa, "REMITENTE", cadena),
                        getConfigCorreo(nitEmpresa, "CLAVE", cadena),
                        correo,
                        getNombrePersona(usuario, nitEmpresa, cadena),
                        nuevaClave, "", this.persisGenKio.getPathFoto(nitEmpresa, cadena), nitEmpresa, cadena);
            } else {
                System.out.println("Error al actualizar la contraseña");
            }
        } catch (Exception e) {
            System.out.println("Se genero un erro al enviar el correo");
            envioCorreo = false;
        }

        JsonObject json = Json.createObjectBuilder()
                .add("envioCorreo", envioCorreo)
                .add("updateClave", updateClave)
                //para enviar un Null desde json
                .add("correo", correo != null ? Json.createValue(correo) : JsonValue.NULL)
                .build();
        return Response.ok(json,
                MediaType.APPLICATION_JSON)
                .build();
    }

    public boolean actualizarClave(String seudonimo, String nitEmpresa, String clave, String cadena) {
        System.out.println("Parametros actualizarClave(): seudonimo: " + seudonimo + ", clave: " + clave + ", cadena: " + cadena);
        String datos = null;
        boolean resultado = false;
        int resCon = 0;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS SET PWD=GENERALES_PKG.ENCRYPT(?) where seudonimo=? and nitempresa=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, clave);
            query.setParameter(2, seudonimo);
            query.setParameter(3, nitEmpresa);
            resCon = query.executeUpdate();
            resultado = resCon > 0;
            System.out.println("resultado actualizarClave: " + resultado);

        } catch (Exception e) {
            System.out.println("Error " + ConexionesKioskos.class
                    .getName() + ".actualizarClave: " + e.getMessage());
        }
        return resultado;
    }

    public String getNombrePersona(String usuario, String nitEmpresa, String cadena) {
        System.out.println("Parametros getNombrePersona(): usuario: " + usuario + ", cadena: " + cadena);
        String nombre = "Usuario";
        BigDecimal documento = this.persisConKio.getDocumentoPorSeudonimo(usuario, nitEmpresa, cadena);
        String sqlQuery;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            sqlQuery = "SELECT InitCap(NVL(NOMBRE, 'Usuario')) NOMBRE FROM PERSONAS WHERE NUMERODOCUMENTO=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            nombre = query.getSingleResult().toString();

        } catch (Exception e) {
            System.out.println("Error " + ConexionesKioskos.class
                    .getName() + ".getNombrePersona(): " + e);
        }
        return nombre;
    }

    /**
     * Validar validarUsuarioRegistrado
     *
     * @param usuario
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    @GET
    @Path("/restKiosco/validarUsuarioRegistrado/{usuario}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarUsuarioRegistradoW(@PathParam("usuario") String usuario, @PathParam("nitEmpresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        boolean res = validarUsuarioRegistrado(usuario, nitEmpresa, cadena);
        String[] parametros = {usuario, nitEmpresa};
        try {
            return Response.ok(
                    response("validarUsuarioRegistrado", "Usuario: " + usuario, String.valueOf(res)),
                    MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Validar si el usuario esta activo y pertenece a la empresa
     *
     * @param usuario
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    @GET
    @Path("/restKiosco/getCorreoPersonaEmpresa/{documento}/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCorreoDocumentoEmpresa(@PathParam("documento") String usuario, @PathParam("nitEmpresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String correo = this.persisPersonas.consultarCorreoPersonaEmpresa(usuario, nitEmpresa, cadena);
        return Response.ok(response("getCorreoPersonaEmpresa", "documento: " + usuario + " nitEmpresa: " + nitEmpresa, correo))
                .build();
    }

    // envia token para validar cuenta
    @GET
    @Path("/restKiosco/jwtValidCuenta")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJWTValidCuenta(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave, @QueryParam("nit") String nit,
            @QueryParam("urlKiosco") String urlKiosco, @QueryParam("cadena") String cadena, @QueryParam("grupo") String grupo) throws UnsupportedEncodingException {
        System.out.println("Parametros getJWTValidCuenta(): usuario: " + usuario + ", clave: " + clave + ", nit: " + nit + ", cadena: " + cadena);
        System.out.println("UrlKiosco recibido: " + urlKiosco);
        Boolean r = validarLogin(usuario, clave, nit, cadena);
        String passwordEncript = "Manager01";
        boolean creaRegistro = false;
        boolean estadoEnvioCorreo = false;
        long tiempo = System.currentTimeMillis();
        Date fechaCreacion = new Date(tiempo);
        Date fechaExpiracion = new Date(tiempo + 3600000); //2160000
        String jwtCompleto = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, passwordEncript.getBytes("UTF-8"))
                .setSubject(usuario)
                .setIssuedAt(fechaCreacion)
                .setExpiration(fechaExpiracion) // 1 hora de expiración a partir de la generación del Token
                .setIssuer("https://www.designer.com.co") //200721
                .claim("empresa", nit)
                .claim("documento", this.persisConKio.getDocumentoPorSeudonimo(usuario, nit, cadena))
                .claim("cadena", cadena)
                .claim("grupo", grupo)
                .compact();
        System.out.println("Token generado: " + jwtCompleto);

        Encriptacion e = new Encriptacion();
        String jwt = e.encrypt(jwtCompleto, passwordEncript);
        System.out.println("Token encriptado: " + jwt);
        EnvioCorreo p = new EnvioCorreo();
        creaRegistro = registraToken(usuario, nit, jwt, "VALIDACUENTA", fechaCreacion, fechaExpiracion, cadena);
        if (creaRegistro) {
            System.out.println("Token VALIDACUENTA registrado");
            estadoEnvioCorreo = p.enviarEnlaceValidacionCuenta(
                    getConfigServidorSMTP(nit, cadena),
                    getConfigCorreo(nit, "PUERTO", cadena),
                    getConfigCorreo(nit, "AUTENTICADO", cadena),
                    getConfigCorreo(nit, "STARTTLS", cadena), getConfigCorreo(nit, "REMITENTE", cadena), getConfigCorreo(nit, "CLAVE", cadena),
                    this.persisPersonas.getCorreoConexioneskioskos(usuario, nit, cadena), getNombrePersona(usuario, nit, cadena), usuario, jwt, urlKiosco, nit, cadena);
        }
        JsonObject json = Json.createObjectBuilder()
                .add("JWT", jwt)
                .add("tokenRegistrado", creaRegistro)
                .add("envioCorreo", estadoEnvioCorreo)
                .add("grupo", grupo)
                .build();

        return Response.status(Response.Status.CREATED).entity(json)
                .build();

    }

    /**
     * Validar validarUsuarioSeudonimoRegistrado -> consulta seudonimo, clave y
     * nit correctos
     *
     * @param usuario
     * @param clave
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    @GET
    @Path("/restKiosco/validarUsuarioSeudonimoRegistrado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarUsuarioSeudonimoRegistrado(@QueryParam("usuario") String usuario, @QueryParam("clave") String clave, @QueryParam("nitEmpresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        boolean res = validarIngresoUsuarioSeudonimoRegistrado(usuario, clave, nitEmpresa, cadena);
        String[] parametros = {usuario, nitEmpresa};
        try {
            return Response.ok(
                    response("validarUsuarioSeudonimoRegistrado", "Usuario: " + usuario, String.valueOf(res)),
                    MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            // Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    public String getConfigCorreo(String nitEmpresa, String valor, String cadena) {
        System.out.println("getConfigCorreo()");
        String servidorsmtpConfig = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT " + valor + " FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)"
                    + " AND ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtpConfig = query.getSingleResult().toString();
            System.out.println(valor + ": " + servidorsmtpConfig);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreo" + e.getMessage());
        }
        return servidorsmtpConfig;
    }

    public String getConfigServidorSMTP(String nitEmpresa, String cadena) {
        System.out.println("getConfigCorreoServidorSMTP()");
        String servidorsmtp = "smtp.designer.com.co";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT SERVIDORSMTP FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp = query.getSingleResult().toString();
            System.out.println("Servidor smtp: " + servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreoServidorSMTP: " + e.getMessage());
        }
        return servidorsmtp;
    }

    /**
     * metodo privado para dar formato al JSON de respuesta
     *
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

    @GET
    @Path("/obtenerFotoPerfil")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response obtenerFotoPerfil(@QueryParam("cadena") String cadena, @QueryParam("usuario") String usuario, @QueryParam("nit") String nitEmpresa) {
        System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): Parametros: "
                + "cadena: " + cadena
                + " , nitEmpresa: " + nitEmpresa
                + " , usuario: " + usuario);
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = this.persisGenKio.getPathFoto(nitEmpresa, cadena);
        String imagen = null;
        String sqlQuery = "SELECT CK.FOTOPERFIL "
                + "FROM CONEXIONESKIOSKOS CK, EMPLEADOS E "
                + "WHERE CK.EMPLEADO=E.SECUENCIA "
                + "AND E.SECUENCIA = ? "
                + "AND CK.NITEMPRESA = ? ";
        try {
            String secEmpl = this.persisConKio.getSecuenciaEmplPorSeudonimo(usuario, nitEmpresa, cadena);
            if (secEmpl == null) {
                secEmpl = this.perisEmpleados.getSecEmplPorCodigo(usuario, nitEmpresa, cadena);
            }
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, nitEmpresa);
            imagen = (String) query.getSingleResult();
            System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): imagen: " + imagen);
            System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): RUTAFOTO: " + RUTAFOTO);
            try {
                fis = new FileInputStream(new File(RUTAFOTO + imagen));
                file = new File(RUTAFOTO + imagen);
            } catch (FileNotFoundException ex) {
                System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): imagen no encontrada ");
                System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): Error-1: " + ex.toString());
                try {
                    fis = new FileInputStream(new File(RUTAFOTO + "sinFoto.jpg"));
                    file = new File(RUTAFOTO + "sinFoto.jpg");
                } catch (FileNotFoundException ex1) {
                    System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): sinFoto.jpg no encontrada ");
                    System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): Error-2: " + ex1.toString());
                    Logger
                            .getLogger(ConexionesKioskosFacadeREST.class
                                    .getName()).log(Level.SEVERE, "sinFoto.jpg no encontrada: " + imagen, ex1);
                    System.getProperty("user.dir");
                    System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): " + " Ruta del proyecto: " + this.getClass().getClassLoader().getResource("").getPath());
                }

            }

            Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + imagen + "\"");
            return responseBuilder.build();

        } catch (Exception ex) {
            System.out.println("ConexionesKioskosFacadeREST" + ".obtenerFotoPerfil(): " + "Error: " + ex.toString());
            return Response.status(Response.Status.OK).entity(0).build();
        }

    }

}
