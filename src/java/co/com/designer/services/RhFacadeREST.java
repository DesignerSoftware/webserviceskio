package co.com.designer.services;

import co.com.designer.kiosko.entidades.RrHh;
import co.com.designer.kiosko.generales.GenerarCorreo;
import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConexiones;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
 * @author Mateo Coronado
 */
@Stateless
@Path("rrhh")
public class RhFacadeREST {

    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaPerfiles rolesBD;

    public RhFacadeREST() {
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.rolesBD = new PersistenciaPerfiles();
    }

    public String getCorreosXempleadosActivos(String nitEmpresa, String cadena, String esquema) {
        System.out.println("getCorreosXempleadosActivos()");
        List emailSoporte = null;
        String correoDestinatarios = "";
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.EMAIL \n"
                    + "FROM EMPLEADOS E, PERSONAS P, EMPRESAS EM\n"
                    + "WHERE \n"
                    + "P.SECUENCIA=E.PERSONA \n"
                    + "AND E.EMPRESA = EM.SECUENCIA \n"
                    + "AND EM.NIT = ? \n"
                    + "AND EMPLEADOCURRENT_PKG.TIPOTRABAJADORCORTE(E.SECUENCIA, SYSDATE)='ACTIVO'\n"
                    + "AND P.EMAIL IS NOT NULL ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            emailSoporte = query.getResultList();
            Iterator<String> it = emailSoporte.iterator();
            System.out.println("size: " + emailSoporte.size());
            while (it.hasNext()) {
                String correoenviar = it.next();
                correoDestinatarios += correoenviar;
                if (it.hasNext()) {
                    correoDestinatarios += ",";
                }
            }
            System.out.println("Emails soporte: " + correoDestinatarios);
        } catch (Exception e) {
            System.out.println("Error: getCorreoRecursosHumanos: " + e.getMessage());
        }
        return correoDestinatarios;
    }

    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getDocumentoPorSeudonimo() seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String documento = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO "
                    + "FROM PERSONAS P, CONEXIONESKIOSKOS CK "
                    + "WHERE CK.PERSONA=P.SECUENCIA "
                    + "AND lower(CK.SEUDONIMO)=lower(?) "
                    + "AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento = query.getSingleResult().toString();
            System.out.println("documento: " + documento);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getDocumentoPorSeudonimo: " + e.getMessage());
        }
        return documento;
    }

    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secuencia = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO FROM EMPLEADOS E, CONEXIONESKIOSKOS CK WHERE CK.EMPLEADO=E.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EM.SECUENCIA SECUENCIAEMPRESA FROM EMPRESAS EM WHERE EM.NIT=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
            this.rolesBD.setearPerfil(esquema, cadena);
            secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            secEmpresa = getSecuenciaPorNitEmpresa(nit, cadena, esquema);
            String sql = "INSERT INTO KIOMENSAJESRRHH (EMPRESA, TITULO, DESCRIPCION, \n"
                    + "NOMBREADJUNTO, FECHAINICIO, FECHAFIN, \n"
                    + "USUARIO, FECHACREACION, ESTADO, FECHAMODIFICADO) \n"
                    + "VALUES \n"
                    + "(?, ?, ?, ? \n"
                    + ", to_date(?, 'dd/mm/yyyy'),to_date(?, 'dd/mm/yyyy'),?,TO_DATE(?, 'ddmmyyyy HH24miss'), \n"
                    + " 'ACTIVO' , TO_DATE(?, 'ddmmyyyy HH24miss'))";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sql);
            query.setParameter(1, secEmpresa);//EMPRESA
            query.setParameter(2, titulo);
            query.setParameter(3, mensaje);
            query.setParameter(4, nombreAnexo);
            query.setParameter(5, fechainicial);
            query.setParameter(6, fechaFin);
            query.setParameter(7, secEmpleado);
            query.setParameter(8, fechaGeneracion);
            query.setParameter(9, fechaGeneracion);
            conteo = query.executeUpdate();
            System.out.println("Registro KIOSMENSAJE: " + conteo);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".creaMensaje(): " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }

    public boolean updateMensaje(String seudonimo, String secuenciamsj, String nit, String nombreAnexo, String fechaGeneracion,
            String fechainicial, String fechaFin, String titulo, String mensaje, String cadena, String esquema, String estado) {
        System.out.println("Parametros creaMensaje(): seudonimo: " + seudonimo + ", nit: " + nit + ", nombreAnexo: " + nombreAnexo + ", fechaGeneracion: " + fechaGeneracion
                + ", cadena: " + cadena + ", secuenciamsj: " + secuenciamsj + ", estado: " + estado);
        int conteo = 0;
        String secEmpleado = null;
        String secEmpresa = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            String sql = "UPDATE KIOMENSAJESRRHH \n"
                    + "set TITULO = ?, DESCRIPCION = ?,\n"
                    + "NOMBREADJUNTO = ?, FECHAINICIO = to_date(?, 'dd/mm/yyyy'),\n"
                    + "FECHAFIN = to_date(?, 'dd/mm/yyyy'), USUARIO = ?,\n"
                    + "ESTADO = ?,\n"
                    + "FECHAMODIFICADO = to_date(?, 'ddmmyyyy HH24miss')\n"
                    + "WHERE SECUENCIA = ?";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sql);
            query.setParameter(1, titulo);//EMPRESA
            query.setParameter(2, mensaje);
            query.setParameter(3, nombreAnexo);
            query.setParameter(4, fechainicial);
            query.setParameter(5, fechaFin);
            query.setParameter(6, secEmpleado);
            query.setParameter(7, estado);
            query.setParameter(8, fechaGeneracion);
            query.setParameter(9, secuenciamsj);
            conteo = query.executeUpdate();
            System.out.println("UPDATE KIOSMENSAJE: " + conteo);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".creaMensaje(): " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }

    public boolean deleteMensaje(String seudonimo, String secuenciamsj, String nit, String cadena, String esquema) {
        System.out.println("Parametros deleteMensaje(): seudonimo: " + seudonimo + ", nit: " + nit
                + ", cadena: " + cadena + ", secuenciamsj: " + secuenciamsj);
        int conteo = 0;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sql = "DELETE KIOMENSAJESRRHH \n"
                    + "WHERE SECUENCIA = ? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sql);
            query.setParameter(1, secuenciamsj);
            conteo = query.executeUpdate();
            System.out.println("deleteMensaje: " + conteo);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".deleteMensaje(): " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }

    public String getPathFoto(String nitEmpresa, String cadena) {
        System.out.println("Parametros getPathFoto(): cadena: " + cadena);
        String rutaFoto = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaFotos: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathFoto(): " + e.getMessage());
        }
        return rutaFoto;
    }

    private String writeToFileServer(InputStream inputStream, String fileName, String nitEmpresa, String cadena) throws IOException {

        OutputStream outputStream = null;
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
    @Produces({MediaType.APPLICATION_JSON})
    public Response getConsultarMsj(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("parametros consultarmsj(): nit: " + nitEmpresa + " cadena " + cadena);
        List s = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT RH.SECUENCIA SECUENCIA, \n"
                    + "RH.TITULO TITULO, \n"
                    + "replace(RH.DESCRIPCION, '\\n', '<br>') DESCRIPCION, \n"
                    + "NVL(RH.NOMBREADJUNTO,'N') NOMBREADJUNTO, \n"
                    + "TO_CHAR(RH.FECHAINICIO, 'MM/DD/YYYY') FECHAINICIO1, \n"
                    + "TO_CHAR(RH.FECHAINICIO, 'DD/MM/YYYY') FECHAINICIO, \n"
                    + "TO_CHAR(RH.FECHAFIN, 'MM/DD/YYYY') FECHAFIN1,\n"
                    + "TO_CHAR(RH.FECHAFIN, 'DD/MM/YYYY') FECHAFIN,\n"
                    + "NVL(SUBSTR(TRIM(RH.NOMBREADJUNTO),INSTR(TRIM(RH.NOMBREADJUNTO),'.'),5), \n"
                    + "  'N') FORMATO, \n"
                    + "RH.ESTADO ESTADO,\n"
                    + "TO_CHAR(RH.FECHACREACION, 'MM/DD/YYYY') FECHACREACION1,\n"
                    + "TO_CHAR(RH.FECHACREACION, 'DD/MM/YYYY') FECHACREACION\n"
                    + "FROM KIOMENSAJESRRHH rh, empresas em  \n"
                    + "WHERE \n"
                    + "rh.empresa = em.secuencia\n"
                    + "and em.nit = ?\n"
                    + "ORDER BY RH.FECHACREACION DESC";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery, RrHh.class);
            query.setParameter(1, nitEmpresa);
            s = query.getResultList();
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + "getDatosEmpleadoNit: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }

    }

    @GET
    @Path("/consultarmsjActivos")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getConsultarMsjActivos(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("parametros consultarmsj(): nit: " + nitEmpresa + " cadena " + cadena);
        List s = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT RH.SECUENCIA SECUENCIA, \n"
                    + "RH.TITULO TITULO, \n"
                    + "replace(RH.DESCRIPCION, '\\n', '<br>') DESCRIPCION, \n"
                    + "NVL(RH.NOMBREADJUNTO,'N') NOMBREADJUNTO, \n"
                    + "TO_CHAR(RH.FECHAINICIO, 'DD/MM/YYYY') FECHAINICIO, \n"
                    + "TO_CHAR(RH.FECHAFIN, 'DD/MM/YYYY') FECHAFIN,\n"
                    + "NVL(SUBSTR(TRIM(RH.NOMBREADJUNTO),INSTR(TRIM(RH.NOMBREADJUNTO),'.'),5), \n"
                    + "  'N') FORMATO, \n"
                    + "RH.ESTADO ESTADO \n"
                    + "FROM KIOMENSAJESRRHH rh, empresas em  \n"
                    + "WHERE \n"
                    + "rh.empresa = em.secuencia\n"
                    + "and em.nit = ?\n"
                    + "AND RH.FECHAINICIO <= SYSDATE\n"
                    + "AND RH.FECHAFIN >= SYSDATE\n "
                    + "AND RH.ESTADO = 'ACTIVO'\n "
                    + "ORDER BY RH.FECHACREACION DESC";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery, RrHh.class);
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
            @QueryParam("cadena") String cadena, @QueryParam("correo") String correo,
            @QueryParam("url") String url) {
        System.out.println("Token recibido crearMensajeRh: " + token);
        System.out.println("parametros: crearMensajeRh{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + "\n fechainicio: " + fechainicial + ", fechaFin: " + fechaFin + ", titulo: " + titulo
                + " anexoAdjunto: " + anexoAdjunto + " mensaje: " + mensaje + ", cadena: " + cadena
                + " extenciondjunto: " + extenciondjunto + " correo: " + correo);
        boolean soliciCreada = false;
        boolean correoEnviado = false;
        String esquema = null;
        // nombre con el que debe guardarse el campo del documento anexo
        String nombreAnexo = null; 
        try {
            esquema = this.cadenasKio.getEsquema(nit, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. " + e.getMessage());
        }

        try {
            Date fecha = new Date();
            String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(fecha);
            String fechaGeneracion1 = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
            System.out.println("fecha: " + fechaGeneracion);
            if (!anexoAdjunto.contains("N") || !anexoAdjunto.equals("N")) {
                nombreAnexo = nit + "_anexo_" + fechaGeneracion.replaceAll(" ", "") + extenciondjunto;
            }

            // Registro en tabla KIOMENSAJESRRHH
            if (creaMensaje(seudonimo, nit, nombreAnexo, fechaGeneracion, fechainicial, fechaFin,
                    titulo, mensaje, cadena, esquema)) {
                //ENVIO DE CORREO 
                if (correo == "S" || correo.equals("S")) {
                    String mensajeCorreo = "Nos permitimos informar que se ha reportado el siguiente comunicado: "
                            + "<br><br><b class=\"negrilla\">"
                            + titulo + ":</b> "
                            + mensaje;
                    System.out.println("Parametros enviaKIOMENSAJESRRHH(): seudonimo " + seudonimo + ", nit: " + nit + ", cadena: " + cadena);
                    String asunto = "¡Nuevo Comunicado Disponible! " + fechaGeneracion1;
                    try {
                        GenerarCorreo e = new GenerarCorreo();
                        if (e.enviarCorreoComunicado(
                                getCorreosXempleadosActivos(nit, cadena, esquema),
                                 asunto,
                                 "Estimados Colaboradores:",
                                 mensajeCorreo,
                                 nit,
                                 cadena,
                                 url)) {
                            soliciCreada = true;
                            correoEnviado = true;
                            mensaje = "Mensaje Creado con Exito y Envío de correo.";
                            System.out.println("Mensaje creado.");
                        } else {
                            soliciCreada = true;
                            mensaje = "Mensaje Creado con Exito, Se presento un inconveniente al enviar el correo.";
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
                        mensaje = "Ha ocurrido un error, por favor intentalo de nuevo más tarde.";
                    }
                } else {
                    soliciCreada = true;
                    correoEnviado = true;
                    mensaje = "Mensaje Creado con Exito.";
                    System.out.println("Mensaje creado.");
                }
            } else {
                System.out.println("Ha ocurrido un error al momento de crear el registro");
                mensaje = "Ha ocurrido un error y no fue posible crear el comunicado, por favor inténtelo de nuevo más tarde.";
            }
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".crearMensajeRh()");
        }
        // Respuesta
        JSONObject obj = new JSONObject();
        try {
            obj.put("NovedadCreada", soliciCreada);
            obj.put("mensaje", mensaje);
            obj.put("correoEnviado", correoEnviado);
            obj.put("anexo", nombreAnexo); // retorna el nombre de como deberia guardarse el documento anexo
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }

    @POST
    @Path("/updateMensajeRh")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String updateMensajeRh(
            @HeaderParam("authorization") String token,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("fechainicio") String fechainicial, @QueryParam("fechafin") String fechaFin,
            @QueryParam("titulo") String titulo, @QueryParam("mensaje") String mensaje,
            @QueryParam("anexoadjunto") String anexoAdjunto, @QueryParam("extenciondjunto") String extenciondjunto,
            @QueryParam("cadena") String cadena, @QueryParam("secuenciamsj") String secuenciamsj, @QueryParam("estado") String estado) {
        System.out.println("Token recibido updateMensajeRh: " + token);
        System.out.println("parametros: updateMensajeRh{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + "\n fechainicio: " + fechainicial + ", fechaFin: " + fechaFin + ", titulo: " + titulo
                + " anexoAdjunto: " + anexoAdjunto + " mensaje: " + mensaje + ", cadena: " + cadena
                + " extenciondjunto: " + extenciondjunto + " secuenciamsj: " + secuenciamsj
                + ", estado: " + estado);
        boolean soliciCreada = false;
        String esquema = null;
        // nombre con el que debe guardarse el campo del documento anexo
        String nombreAnexo = null; 
        try {
            esquema = this.cadenasKio.getEsquema(nit, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. " + e.getMessage());
        }

        try {
            Date fecha = new Date();
            String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(fecha);
            System.out.println("fecha: " + fechaGeneracion);
            if (!anexoAdjunto.contains("N") || !anexoAdjunto.equals("N")) {
                if (anexoAdjunto.contains("S") || anexoAdjunto.equals("S")) {
                    nombreAnexo = nit + "_anexo_" + fechaGeneracion.replaceAll(" ", "") + extenciondjunto;
                } else {
                    nombreAnexo = anexoAdjunto;
                }
            }

            // Registro en tabla KIOSOLICIAUSENTISMOS
            if (updateMensaje(seudonimo, secuenciamsj, nit, nombreAnexo, fechaGeneracion, fechainicial, fechaFin,
                    titulo, mensaje, cadena, esquema, estado)) {
                soliciCreada = true;
                System.out.println("Mensaje actualizado.");
                mensaje = "Mensaje Actualizado con Exito.";
            } else {
                System.out.println("Ha ocurrido un error al momento de Actualizar el registro del comunicado");
                mensaje = "Ha ocurrido un error y no fue posible modificar el comunicado, por favor inténtelo de nuevo más tarde.";
            }
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".updateMensajeRh()");
        }
        // Respuesta
        JSONObject obj = new JSONObject();
        try {
            obj.put("NovedadModificada", soliciCreada);
            obj.put("mensaje", mensaje);
            // retorna el nombre de como deberia guardarse el documento anexo
            obj.put("anexo", nombreAnexo); 
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }

    @POST
    @Path("/deleteMensajeRh")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String deleteMensajeRh(
            @HeaderParam("authorization") String token,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("cadena") String cadena, @QueryParam("secuenciamsj") String secuenciamsj) {
        System.out.println("Token recibido deleteMensajeRh: " + token);
        System.out.println("parametros: deleteMensajeRh{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + ", cadena: " + cadena + " secuenciamsj: " + secuenciamsj);
        boolean soliciCreada = false;
        String esquema = null;
        String mensaje = null;
        try {
            esquema = this.cadenasKio.getEsquema(nit, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. " + e.getMessage());
        }

        try {
            // Registro en tabla KIOSOLICIAUSENTISMOS
            if (deleteMensaje(seudonimo, secuenciamsj, nit, cadena, esquema)) {
                soliciCreada = true;
                System.out.println("Mensaje Eliminado.");
                mensaje = "Mensaje Eliminado con Exito.";
            } else {
                System.out.println("Ha ocurrido un error al momento de Eliminar el registro del comunicado");
                mensaje = "Ha ocurrido un error y no fue posible eliminar el comunicado, por favor inténtelo de nuevo más tarde.";
            }
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".deleteMensajeRh()");
        }
        // Respuesta
        JSONObject obj = new JSONObject();
        try {
            obj.put("NovedadModificada", soliciCreada);
            obj.put("mensaje", mensaje);
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

    @GET
    @Path("/reenviarcorreo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String reenviarCorreo(
            @HeaderParam("authorization") String token,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("titulo") String titulo, @QueryParam("mensaje") String mensaje,
            @QueryParam("cadena") String cadena, @QueryParam("url") String url) {
        System.out.println("Token recibido reenviarCorreo: " + token);
        System.out.println("parametros: reenviarCorreo{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + ", titulo: " + titulo + " mensaje: " + mensaje + ", cadena: " + cadena);
        boolean sendEmail = false;
        String esquema = null;
        try {
            esquema = this.cadenasKio.getEsquema(nit, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. " + e.getMessage());
        }

        try {
            Date fecha = new Date();
            String fechaGeneracion = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
            //ENVIO DE CORREO 
            String mensajeCorreo = "Nos permitimos informar que se ha reportado el siguiente comunicado: "
                    + "<br><br><b style=\"color: #00223c;\">" + titulo + ":</b> "
                    + mensaje;
            String asunto = "¡Nuevo Comunicado Disponible! " + fechaGeneracion;
            try {
                GenerarCorreo e = new GenerarCorreo();
                if (e.enviarCorreoComunicado(
                        getCorreosXempleadosActivos(nit, cadena, esquema),
                         asunto,
                         "Estimado Usuarios:",
                         mensajeCorreo,
                         nit,
                         cadena,
                         url)) {
                    sendEmail = true;
                    mensaje = "Mensaje Enviado con Exito.";
                    System.out.println("Mensaje creado.");
                } else {
                    sendEmail = false;
                    mensaje = "Se presento un inconveniente al enviar el correo.";
                }
            } catch (Exception ex) {
                Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
                mensaje = "Ha ocurrido un error, por favor intentalo de nuevo más tarde.";
            }

        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".crearMensajeRh()");
        }
        // Respuesta
        JSONObject obj = new JSONObject();
        try {
            obj.put("correoEnviado", sendEmail);
            obj.put("mensaje", mensaje);
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }
}
