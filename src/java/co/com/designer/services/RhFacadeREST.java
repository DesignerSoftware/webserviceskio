/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.services;

import co.com.designer.kiosko.entidades.RrHh;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author UPC007
 */
@Stateless
@Path("rrhh")
public class RhFacadeREST {

    protected EntityManager getEntityManager() {
        String unidadPersistencia = "wsreportePU";
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }

    protected EntityManager getEntityManager(String persistence) {
        String unidadPersistencia = persistence;
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }

    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getDocumentoPorSeudonimo() seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String documento = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND lower(CK.SEUDONIMO)=lower(?) AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento = query.getSingleResult().toString();
            System.out.println("documento: " + documento);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getDocumentoPorSeudonimo: " + e.getMessage());
        }
        return documento;
    }

    public String getEsquema(String nitEmpresa, String cadena) {
        System.out.println("Parametros getEsquema(): nitempresa: " + nitEmpresa + ", cadena: " + cadena);
        String esquema = null;
        String sqlQuery;
        try {
            sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=?";
            Query query = getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("Esquema: " + esquema);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getEsquema(): " + e);
        }
        return esquema;
    }

    protected void setearPerfil(String esquema, String cadenaPersistencia) {
        try {
            String rol = "ROLKIOSKO";
            if (esquema != null && !esquema.isEmpty()) {
                rol = rol + esquema.toUpperCase();
            }
            System.out.println("setearPerfil(esquema, cadena)");
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager(cadenaPersistencia).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil(cadenaPersistencia): " + ex);
        }
    }

    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secuencia = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO FROM EMPLEADOS E, CONEXIONESKIOSKOS CK WHERE CK.EMPLEADO=E.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuenciaEmpl: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }

    public String getSecuenciaPorNitEmpresa(String nitEmpresa, String cadena, String esquema) {
        String secuencia = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EM.SECUENCIA SECUENCIAEMPRESA FROM EMPRESAS EM WHERE EM.NIT=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuencia: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecuenciaPorNitEmpresa: " + e.getMessage());
        }
        return secuencia;
    }

    public boolean creaMensaje(String seudonimo, String nit, String nombreAnexo, String fechaGeneracion,
            String fechainicial, String fechaFin, String titulo, String mensaje, String cadena, String esquema) {
        System.out.println("Parametros creaMensaje(): seudonimo: " + seudonimo + ", nit: " + nit + ", nombreAnexo: " + nombreAnexo + ", fechaGeneracion: " + fechaGeneracion
                + ", cadena: " + cadena);
        int conteo = 0;
        String secEmpleado = null;
        String secEmpresa = null;
        try {
            //String esquema = getEsquema(nit, cadena);
            setearPerfil(esquema, cadena);
            secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            secEmpresa = getSecuenciaPorNitEmpresa(nit, cadena, esquema);
            String sql = "INSERT INTO KIOMENSAJESRRHH (EMPRESA, TITULO, DESCRIPCION, \n"
                    + "NOMBREADJUNTO, FECHAINICIO, FECHAFIN, \n"
                    + "USUARIO, FECHACREACION) \n"
                    + "VALUES \n"
                    + "(?, ?, ?, ? \n"
                    + ", to_date(?, 'DD/MM/YYYY'),to_date(?, 'DD/MM/YYYY'),?,TO_DATE(?, 'ddmmyyyy HH24miss'))";
            Query query = getEntityManager(cadena).createNativeQuery(sql);
            query.setParameter(1, secEmpresa);//EMPRESA
            query.setParameter(2, titulo);
            query.setParameter(3, mensaje);
            query.setParameter(4, nombreAnexo);
            query.setParameter(5, fechainicial);
            query.setParameter(6, fechaFin);
            query.setParameter(7, secEmpleado);
            query.setParameter(8, fechaGeneracion);
            conteo = query.executeUpdate();
            System.out.println("Registro KIOSMENSAJE: " + conteo);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".creaMensaje(): " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }

    public String getPathFoto(String nitEmpresa, String cadena) {
        System.out.println("Parametros getPathFoto(): cadena: " + cadena);
        String rutaFoto = "";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaFotos: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathFoto(): " + e.getMessage());
        }
        return rutaFoto;
    }

    private String writeToFileServer(InputStream inputStream, String fileName, String nitEmpresa, String cadena) throws IOException {

        OutputStream outputStream = null;
        //String qualifiedUploadFilePath = UPLOAD_FILE_SERVER + fileName;
        String qualifiedUploadFilePath = getPathFoto(nitEmpresa, cadena) + "rrhh\\" + fileName;
        System.out.println("fichero de subir archivo " + qualifiedUploadFilePath);
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
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".writeToFileServer() " + e.getMessage());
        } finally {
            outputStream.close();
        }
        return qualifiedUploadFilePath;
    }

    @GET
    @Path("/consultarmsj")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getConsultarMsj(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("parametros consultarmsj(): nit: " + nitEmpresa + " cadena " + cadena);
        List s = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT RH.SECUENCIA SECUENCIA, \n"
                    + "RH.TITULO TITULO, \n"
                    + "replace(RH.DESCRIPCION, '\\n', '<br>') DESCRIPCION, \n"
                    + "NVL(RH.NOMBREADJUNTO,'N') NOMBREADJUNTO, \n"
                    + "TO_CHAR(RH.FECHAINICIO, 'DD/MM/YYYY') FECHAINICIO, \n"
                    + "TO_CHAR(RH.FECHAFIN, 'DD/MM/YYYY') FECHAFIN,\n"
                    + "NVL(SUBSTR(TRIM(RH.NOMBREADJUNTO),INSTR(TRIM(RH.NOMBREADJUNTO),'.'),5), \n"
                    + "  'N') FORMATO \n"
                    + "FROM KIOMENSAJESRRHH rh, empresas em  \n"
                    + "WHERE \n"
                    + "rh.empresa = em.secuencia\n"
                    + "and em.nit = ?\n"
                    + "AND (TO_CHAR(RH.FECHAINICIO, 'DD/MM/YYYY') <= TO_CHAR(SYSDATE, 'DD/MM/YYYY')\n"
                    + "AND TO_CHAR(RH.FECHAFIN, 'DD/MM/YYYY') >= TO_CHAR(SYSDATE, 'DD/MM/YYYY'))";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery, RrHh.class);
            query.setParameter(1, nitEmpresa);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + "getDatosEmpleadoNit: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }

    }

    @POST
    @Path("/crearMensajeRh")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String crearMensajeRh(
            @HeaderParam("authorization") String token,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("fechainicio") String fechainicial, @QueryParam("fechafin") String fechaFin,
            @QueryParam("titulo") String titulo, @QueryParam("mensaje") String mensaje,
            @QueryParam("anexoadjunto") String anexoAdjunto, @QueryParam("extenciondjunto") String extenciondjunto,
            @QueryParam("cadena") String cadena) {
        System.out.println("Token recibido crearNovedadAusentismo: " + token);
        System.out.println("parametros: crearNovedadAusentismo{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + "\n fechainicio: " + fechainicial + ", fechaFin: " + fechaFin + ", titulo: " + titulo
                + " anexoAdjunto: " + anexoAdjunto + " mensaje: " + mensaje + ", cadena: " + cadena
                + " extenciondjunto: " + extenciondjunto);
        boolean soliciCreada = false;
        String esquema = null;
        String nombreAnexo = null; // nombre con el que debe guardarse el campo del documento anexo
        try {
            //esquema = getEsquema(esquema, cadena);
            esquema = getEsquema(nit, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. " + e.getMessage());
        }

        try {
            Date fecha = new Date();
            String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(fecha);
            System.out.println("fecha: " + fechaGeneracion);
            String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            if (!anexoAdjunto.contains("N") || !anexoAdjunto.equals("N")) {
                nombreAnexo = nit + "_anexo_" + fechaGeneracion.replaceAll(" ", "") + extenciondjunto;
            }

            // Registro en tabla KIOSOLICIAUSENTISMOS
            if (creaMensaje(seudonimo, nit, nombreAnexo, fechaGeneracion, fechainicial, fechaFin,
                    titulo, mensaje, cadena, esquema)) {
                soliciCreada = true;
                System.out.println("Mensaje creado.");
                mensaje = "Mensaje Creado con Exito.";
            } else {
                System.out.println("Ha ocurrido un error al momento de crear el registro 1 de la solicitud");
                mensaje = "Ha ocurrido un error y no fue posible crear la novedad, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
            }
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".crearNovedadAusentismo()");
        }
        // Respuesta
        JSONObject obj = new JSONObject();
        try {
            obj.put("NovedadCreada", soliciCreada);
            obj.put("mensaje", mensaje);
            obj.put("anexo", nombreAnexo); // retorna el nombre de como deberia guardarse el documento anexo
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }

    @POST
    @Path("/cargarAnexoPdf")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response cargarAnexo(
            @FormDataParam("fichero") InputStream fileInputStream,
            @FormDataParam("fichero") FormDataContentDisposition fileFormDataContentDisposition,
            @QueryParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        String fileName = null;
        String uploadFilePath = null;
        try {
            fileName = fileFormDataContentDisposition.getFileName();
            uploadFilePath = writeToFileServer(fileInputStream, fileName, nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error " + this.getClass().getName() + ".cargarAnexo() " + e.getMessage());
        }
        System.out.println("fuchero para uploadFilePath: " + uploadFilePath);
        return Response.ok("Fichero subido a " + uploadFilePath).build();
    }

    @POST
    @Path("/cargarAnexoImg")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response cargarFoto(
            @FormDataParam("fichero") InputStream fileInputStream,
            @FormDataParam("fichero") FormDataContentDisposition fileFormDataContentDisposition,
            @QueryParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        String fileName = null;
        String uploadFilePath = null;

        try {
            fileName = fileFormDataContentDisposition.getFileName();
            uploadFilePath = writeToFileServer(fileInputStream, fileName, nitEmpresa, cadena);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error: " + ioe.getMessage());
        } finally {
        }
        return Response.ok("Fichero subido a " + uploadFilePath).build();
    }

    @GET
    @Path("/obtenerFotoMsj/{imagen}")
    @Produces({"image/png", "image/jpg", "image/gif"})
    public Response obtenerFoto(@PathParam("imagen") String imagen, @QueryParam("cadena") String cadena, @QueryParam("empresa") String nitEmpresa) {
        System.out.println("Parametros obtenerFoto(): imagen: " + imagen + ", cadena: " + cadena + ", nitEmpresa: " + nitEmpresa);
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = getPathFoto(nitEmpresa, cadena);
        String documento = null;
        System.out.println("Imagen foto perfil: " + imagen);
        try {
            fis = new FileInputStream(new File(RUTAFOTO + "rrhh\\" + imagen));
            file = new File(RUTAFOTO + "rrhh\\" + imagen);
        } catch (FileNotFoundException ex) {

            Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Foto no encontrada: " + imagen, ex);
            System.getProperty("user.dir");
            System.out.println("Ruta del proyecto: " + this.getClass().getClassLoader().getResource("").getPath());;

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
    @Path("/obtenerAnexo/")
    @Produces({"application/pdf"})
    public Response obtenerAnexo(@QueryParam("anexo") String anexo, @QueryParam("cadena") String cadena, @QueryParam("empresa") String nitEmpresa) {
        System.out.println("Parametros obtenerAnexo(): anexo: " + anexo + ", cadena: " + cadena + ", nitEmpresa: " + nitEmpresa);
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = getPathFoto(nitEmpresa, cadena) + "\\rrhh\\";
        try {
            fis = new FileInputStream(new File(RUTAFOTO + anexo));
            file = new File(RUTAFOTO + anexo);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Anexo no encontrada: " + anexo, ex);
            System.getProperty("user.dir");
            System.out.println("Ruta del proyecto: " + this.getClass().getClassLoader().getResource("").getPath());;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Error cerrando fis " + anexo, ex);
            }
        }
        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + anexo + "\"");
        return responseBuilder.build();
    }
}
