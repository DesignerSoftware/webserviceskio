/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.services;

import co.com.designer.kiosko.generales.EnvioCorreo;
import co.com.designer.kiosko.entidades.KioCausasAusentismos;
import co.com.designer.kiosko.entidades.DiagnosticosCategorias;
import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.io.FileFilter;

/**
 *
 * @author UPC006
 */
@Stateless
@Path("ausentismos")
public class kioCausasAusentismosFacadeREST extends AbstractFacade<KioCausasAusentismos> {

    public kioCausasAusentismosFacadeREST() {
        super(KioCausasAusentismos.class);
    }

    @Override
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

    @Override
    protected void setearPerfil() {
        try {
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil: " + ex);
        }
    }

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

    @GET
    @Path("/token")
    public String authenticate(@HeaderParam("authorization") String token) {
        System.out.println("Token recibido: " + token);
        //return Response.ok("Token="+token).build();
        /*Response.ResponseBuilder response = Response.ok("token=" + token);
        response.header("my-header1", token);
        return response.build();*/
        //return Response.ok().status(Response.Status.OK).entity(token.toString()).build();
        /*return Response.ok(
        token)
        .build();*/
        return token;
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
        List<OpcionesKioskosApp> lista = query.getResultList();
        return lista;
    }

    @GET
    @Path("/codigosDiagnosticos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List getCodigosDiagnosticos(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String sqlQuery = "SELECT ka "
                + " FROM DiagnosticosCategorias ka order by ka.codigo ";
        Query query = getEntityManager(cadena).createQuery(sqlQuery);
        List<DiagnosticosCategorias> lista = query.getResultList();
        return lista;
    }

    @GET
    @Path("/codigosDiagnosticos2")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List getCodigosDiagnosticos2(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        List lista = null;
        String sqlQuery = "SELECT secuencia, codigo, descripcion "
                + " FROM DIAGNOSTICOSCATEGORIAS order by codigo, descripcion ";
        Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
        lista = query.getResultList();
        //lista.forEach(System.out::println);
        return lista;
    }

    @GET
    @Path("/consultaNombreAutorizaAusentismos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response consultarAutorizaAusentismos(
            @QueryParam("usuario") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema = getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        String retorno = "";
        String mensaje = "";
        String secEmplJefe = null;
        try {
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            if (secEmpleado != null) {
                try {
                    secEmplJefe = consultarSecuenciaEmpleadoJefe(secEmpleado, nitEmpresa, cadena, esquema);
                    if (secEmplJefe != null) {
                        retorno = getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
                        System.out.println("Empleado jefe: " + retorno);
                    } else {
                        mensaje = "No hay un autorizador/jefe relacionado";
                        throw new Exception("El empleado jefe no está registrado.");
                    }
                } catch (Exception e) {
                    System.out.println("Error consultando jefe: " + e.getMessage());
                    return Response.status(Response.Status.OK).entity("Error").build();
                }
            } else {
                mensaje = "El empleado no existe";
            }
        } catch (Exception e) {
            // return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
            mensaje = "Se ha presentado un error al hacer la consulta. Si el error persiste por favor comuniquese con el área de Talento humano de su empresa.";
        }
        JsonObject json = Json.createObjectBuilder()
                .add("resultado", retorno)
                .add("mensaje", mensaje)
                .build();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/crearNovedadAusentismo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String crearNovedadAusentismo(
            @HeaderParam("authorization") String token,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("fechainicio") String fechainicial, @QueryParam("fechafin") String fechaFin,
            @QueryParam("dias") String dias,
            @QueryParam("causa") String secCausaAusent,
            @QueryParam("diagnostico") String secCodDiagnostico,
            @QueryParam("clase") String secClaseAusent,
            @QueryParam("tipo") String secTipoAusent,
            @QueryParam("prorroga") String secKioNovedadAusent,
            @QueryParam("observacion") String observacion,
            @QueryParam("anexoadjunto") String anexoAdjunto,
            @QueryParam("cadena") String cadena,
            @QueryParam("urlKiosco") String urlKiosco,
            @QueryParam("grupo") String grupoEmpr,
            @QueryParam("fechafin") String fechafin,
            @QueryParam("codigoCausa") String codigoCausa) {
        System.out.println("Token recibido crearNovedadAusentismo: " + token);
        System.out.println("parametros: crearNovedadAusentismo{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + "\n fechainicio: " + fechainicial + ", fechaFin: " + fechaFin + ", dias: " + dias
                + " causa: " + secCausaAusent + ", diagnostico: " + secCodDiagnostico + ", clase: " + secClaseAusent + ", tipo: " + secTipoAusent
                + "\n prorroga: " + secKioNovedadAusent + ", observacion: " + observacion
                + ", cadena: " + cadena + ", grupo: " + grupoEmpr + ", codigoCausa: " + codigoCausa);
        System.out.println("link Kiosco: " + urlKiosco);
        System.out.println("grupoEmpresarial: " + grupoEmpr);
        boolean soliciCreada = false;
        boolean soliciValida = false;
        String esquema = null;
        String nombreAnexo = null; // nombre con el que debe guardarse el campo del documento anexo
        String secKioSoliciAusent = null;
        try {
            //esquema = getEsquema(esquema, cadena);
            esquema = getEsquema(nit, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. " + e.getMessage());
        }

        try {
            Date fecha = new Date();
            String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(fecha);
            String fechaCorreo = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
            String horaGeneracion = new SimpleDateFormat("HH:mm").format(fecha);
            System.out.println("fecha: " + fechaGeneracion);
            String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            String secEmplJefe = null;
            String personaCreaSolici = getApellidoNombreXsecEmpl(secEmpl, nit, cadena, esquema);
            String nombreAutorizaSolici = ""; // Nombre Jefe
            String correoAutorizaSolici = null; // Correo Jefe
            String mensaje = "";
            String urlKio = urlKiosco + "#/login/" + grupoEmpr;
            String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupoEmpr;
            secEmplJefe = consultarSecuenciaEmpleadoJefe(secEmpl, nit, cadena, esquema);
            if ("S".equals(anexoAdjunto)) {
                nombreAnexo = "anexo_" + secEmpl + "_" + fechaGeneracion.replaceAll(" ", "");
            }
            if (secEmplJefe != null) {
                System.out.println("creaKioSoliciAusent: EmpleadoJefe: " + secEmplJefe);
                nombreAutorizaSolici += getApellidoNombreXsecEmpl(secEmplJefe, nit, cadena, esquema);
                correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nit, cadena, esquema);
                System.out.println("El empleado tiene relacionado a empleadoJefe " + nombreAutorizaSolici + " - " + correoAutorizaSolici);

                // Registro en tabla KIOSOLICIAUSENTISMOS
                if (creaKioSoliciAusentismos(seudonimo, secEmplJefe, nit, null, fechaGeneracion, fechainicial, fechaFin, dias, observacion, secCausaAusent, cadena, esquema)) {
                    secKioSoliciAusent = getSecKioSoliciAusent(secEmpl, fechaGeneracion, secEmplJefe, nit, cadena, esquema);
                    System.out.println("secuencia kiosoliciausentismos creada: " + secKioSoliciAusent);
                    int diasIncapacidad = Integer.parseInt(dias);

                    String formaLiq = getCausaFormaLiq(secCausaAusent, cadena, esquema);
                    String porcentajeLiq = getCausaPorcentajeLiq(secCausaAusent, cadena, esquema);
                    String causaOrigen = (String) getCausaOrigenIncapacidad(secCausaAusent, cadena, esquema);
                    /*if (causaOrigen == null) {
                        System.out.println("es tipo nulo" + causaOrigen);
                        causaOrigen = "null";
                    }
                    if (secKioNovedadAusent.equals("null")) {
                        System.out.println("es tipo nulo2" + causaOrigen);
                    }
                    System.out.println("causa Origen Insertar:" + causaOrigen);*/
                    if (secKioNovedadAusent.equals("null")) {
                        System.out.println("No tiene prorroga");
                        if (causaOrigen.equals("EG")) {
                            System.out.println("Causa enfermedad general");
                            String secCausaEGPrimeros2Dias = getSecCausaPrimerosDias("25", cadena, esquema);
                            String formaLiqEGPrimeros2Dias = getCausaFormaLiq(secCausaEGPrimeros2Dias, cadena, esquema);
                            String porcentajeLiqEGPrimeros2Dias = getCausaPorcentajeLiq(secCausaEGPrimeros2Dias, cadena, esquema);
                            if (diasIncapacidad <= 2) {
                                // Si los días reportados son 2 o menos se deben registrar en una sola novedad
                                System.out.println("Los dias reportados son 2 o menos.");
                                String fechaFin1 = (String) calculaFechafinAusent(fechainicial, dias, seudonimo, secCausaEGPrimeros2Dias, cadena, nit, esquema);
                                //fechaFin1 = getDateYMD(fechaFin1, cadena, esquema);
                                System.out.println("Fecha novedad 1: " + fechaFin);
                                if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaEGPrimeros2Dias, secCodDiagnostico, Integer.parseInt(dias), fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqEGPrimeros2Dias, porcentajeLiqEGPrimeros2Dias, cadena, esquema)) {
                                    System.out.println("registrada novedad 1 por 2 dias o menos");
                                    mensaje = "Novedad de ausentismo reportada exitosamente.";

                                    // Registro en tabla KIOESTADOSSOLICIAUSENT
                                    if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                        soliciCreada = true;
                                        System.out.println("Estado de novedad de ausentismo creado.");
                                        getEntityManager(cadena).close();
                                    } else {
                                        mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                    }
                                }
                            } else {
                                // Si los dias reportados son más de 2 se deben registrar en dos novedades
                                // Registro novedad primeros 2 dias
                                System.out.println("Los días reportados son más de 2.");
                                String fechaFin1 = (String) calculaFechafinAusent(fechainicial, "2", seudonimo, secCausaEGPrimeros2Dias, cadena, nit, esquema);
                                if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaEGPrimeros2Dias, secCodDiagnostico, 2, fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqEGPrimeros2Dias, porcentajeLiqEGPrimeros2Dias, cadena, esquema)) {
                                    // Registro segunda novedad por los días faltantes
                                    String fechainicialEG2 = getFechaSugerida3(fechaFin1, "1", cadena, esquema); // fecha inicial 2 es fecha fin +1
                                    int diasNov2 = Integer.parseInt(dias) - 2;
                                    String fechaFin2 = (String) calculaFechafinAusent(fechainicialEG2, String.valueOf(diasNov2), seudonimo, secCausaAusent, cadena, nit, esquema);
                                    if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicialEG2, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad - 2, fechaFin2, secKioSoliciAusent, "null", formaLiq, porcentajeLiq, cadena, esquema)) {
                                        mensaje = "Novedad de ausentismo reportada exitosamente.";

                                        // Registro en tabla KIOESTADOSSOLICIAUSENT
                                        if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                            soliciCreada = true;
                                            System.out.println("Estado de novedad de ausentismo creado.");
                                            getEntityManager(cadena).close();
                                        } else {
                                            mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                        }
                                    }
                                } else {
                                    System.out.println("Ha ocurrido un error al momento de crear el registro de la primera novedad");
                                    mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                                }
                            }
                            // si la causa es accidente de trabajo 
                        } else if (causaOrigen.equals("AT")) {
                            System.out.println("Causa enfermedad general");
                            String secCausaATPrimerDia = getSecCausaPrimerosDias("39", cadena, esquema);
                            String formaLiqATPrimerDia = getCausaFormaLiq(secCausaATPrimerDia, cadena, esquema);
                            String porcentajeLiqATPrimerDia = getCausaPorcentajeLiq(secCausaATPrimerDia, cadena, esquema);
                            if (diasIncapacidad <= 1) {
                                // Si los días reportados son 2 o menos se deben registrar en una sola novedad
                                System.out.println("Los dias reportados es 1 dia.");
                                String fechaFin1 = (String) calculaFechafinAusent(fechainicial, dias, seudonimo, secCausaATPrimerDia, cadena, nit, esquema);
                                //fechaFin1 = getDateYMD(fechaFin1, cadena, esquema);
                                System.out.println("Fecha novedad 1: " + fechaFin);
                                if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaATPrimerDia, secCodDiagnostico, Integer.parseInt(dias), fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqATPrimerDia, porcentajeLiqATPrimerDia, cadena, esquema)) {
                                    System.out.println("registrada novedad 1 dia");
                                    mensaje = "Novedad de ausentismo reportada exitosamente.";

                                    // Registro en tabla KIOESTADOSSOLICIAUSENT
                                    if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                        soliciCreada = true;
                                        System.out.println("Estado de novedad de ausentismo creado.");
                                        getEntityManager(cadena).close();
                                    } else {
                                        mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                    }
                                }
                            } else {
                                // Si los dias reportados son más de 1 se deben registrar en dos novedades
                                // Registro novedad primeros 1 dia1
                                System.out.println("Los días reportados son más de 1.");
                                System.out.println(esquema);
                                String fechaFin1 = (String) calculaFechafinAusent(fechainicial, "1", seudonimo, secCausaATPrimerDia, cadena, nit, esquema);
                                if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaATPrimerDia, secCodDiagnostico, 1, fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqATPrimerDia, porcentajeLiqATPrimerDia, cadena, esquema)) {
                                    // Registro segunda novedad por los días faltantes
                                    String fechainicialEG2 = getFechaSugerida3(fechaFin1, "1", cadena, esquema); // fecha inicial 2 es fecha fin +1
                                    int diasNov2 = Integer.parseInt(dias) - 1;
                                    String fechaFin2 = (String) calculaFechafinAusent(fechainicialEG2, String.valueOf(diasNov2), seudonimo, secCausaAusent, cadena, nit, esquema);
                                    System.out.println(fechaFin2);
                                    System.out.println(esquema);
                                    System.out.println("se crea novedad por un dia");
                                    if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicialEG2, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad - 1, fechaFin2, secKioSoliciAusent, "null", formaLiq, porcentajeLiq, cadena, esquema)) {
                                        System.out.println("se crea novedad por dos dias");
                                        mensaje = "Novedad de ausentismo reportada exitosamente.";
                                        // Registro en tabla KIOESTADOSSOLICIAUSENT
                                        if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                            soliciCreada = true;
                                            System.out.println("Estado de novedad de ausentismo creado.");
                                            getEntityManager(cadena).close();
                                        } else {
                                            mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                        }

                                    }
                                } else {
                                    System.out.println("Ha ocurrido un error al momento de crear el registro de la primera novedad");
                                    mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                                }
                            }
                        } else {
                            // si es dia de la familia 
                            if (codigoCausa.equals("54")) {
                                String tipoCausaFamilia = getSecTipoCausa("2", cadena, esquema);
                                System.out.println("es solicitud de causa de dia familia");
                                if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, tipoCausaFamilia, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad, fechafin, secKioSoliciAusent, secKioNovedadAusent, formaLiq, porcentajeLiq, cadena, esquema)) {
                                    mensaje = "Novedad de ausentismo reportada exitosamente.";
                                    // Registro en tabla KIOESTADOSSOLICIAUSENT
                                    if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                        soliciCreada = true;
                                        System.out.println("Estado de novedad de ausentismo creado.");
                                        getEntityManager(cadena).close();
                                    } else {
                                        mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                    }
                                } else {
                                    System.out.println("Ha ocurrido un error al momento de crear el registrar la novedad");
                                    mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                                }
                            } else {
                                System.out.println("Causa diferente a ENFERMEDAD GENERAL");
                                if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad, fechafin, secKioSoliciAusent, secKioNovedadAusent, formaLiq, porcentajeLiq, cadena, esquema)) {
                                    mensaje = "Novedad de ausentismo reportada exitosamente.";
                                    // Registro en tabla KIOESTADOSSOLICIAUSENT
                                    if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                        soliciCreada = true;
                                        System.out.println("Estado de novedad de ausentismo creado.");
                                        getEntityManager(cadena).close();
                                    } else {
                                        mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                    }
                                } else {
                                    System.out.println("Ha ocurrido un error al momento de crear el registrar la novedad");
                                    mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                                }
                            }
                        }
                    } else {
                        System.out.println("Tiene prorroga");
                        if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad, fechafin, secKioSoliciAusent, secKioNovedadAusent, formaLiq, porcentajeLiq, cadena, esquema)) {
                            mensaje = "Novedad de ausentismo reportada exitosamente.";
                            // Registro en tabla KIOESTADOSSOLICIAUSENT
                            if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                soliciCreada = true;
                                System.out.println("Estado de novedad de ausentismo creado.");
                                getEntityManager(cadena).close();
                            } else {
                                mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                            }
                        } else {
                            System.out.println("Ha ocurrido un error al momento de crear el registrar la novedad");
                            mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                        }
                    }
                    /*if (diasIncapacidad <= 2) {
                        // Si los días reportados son 2 o menos se deben registrar en una sola novedad
                        secCausaAusent = ''; 
                        System.out.println("Los dias reportados son 2 o menos.");
                        String fechaFin1 = (String) calculaFechafinAusent(fechainicial, dias, seudonimo, secCausaAusent, cadena, nit, esquema).getString("fechafin");
                        //fechaFin1 = getDateYMD(fechaFin1, cadena, esquema);
                        System.out.println("Fecha novedad 1: "+fechaFin);
                        if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, Integer.parseInt(dias), fechaFin1, secKioSoliciAusent, formaLiq, porcentajeLiq, cadena, esquema)) {
                            System.out.println("registrada novedad 1 por 2 dias o menos");
                            mensaje = "Novedad de ausentismo reportada exitosamente.";
                            soliciCreada = true;
                        }
                    } else {
                        // Si los dias reportados son más de 2 se deben registrar en dos novedades
                        // Registro novedad primeros 2 dias
                        System.out.println("Los días reportados son más de 2.");
                        if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, 2, fechafin, secKioSoliciAusent, formaLiq, porcentajeLiq,cadena, esquema)) {

                            // Registro segunda novedad por los días faltantes
                            if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad - 2, fechafin, secKioSoliciAusent, formaLiq, porcentajeLiq, cadena, esquema)) {
                                mensaje = "Novedad de ausentismo reportada exitosamente.";
                                soliciCreada = true;
                            }
                        } else {
                            System.out.println("Ha ocurrido un error al momento de crear el registro de la primera novedad");
                            mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                        }
                    }*/

                } else {
                    System.out.println("Ha ocurrido un error al momento de crear el registro 1 de la solicitud");
                    mensaje = "Ha ocurrido un error y no fue posible crear la novedad, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                }
            } else {
                System.out.println("El empleado jefe está vacío");
                // Si no hay una persona asignada para autorizar las vacaciones no crear la solicitud
                soliciCreada = false;
                mensaje = "No tiene un autorizador de ausentismos relacionado, por favor comuníquese con el área de nómina y recursos humanos de su empresa.";
            }
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".crearNovedadAusentismo()");
        }
        String mensaje = "";
        String urlKio = urlKiosco + "#/login/" + grupoEmpr;
        String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupoEmpr;
        // Respuesta
        JSONObject obj = new JSONObject();
        try {
            obj.put("NovedadCreada", soliciCreada);
            obj.put("mensaje", mensaje);
            obj.put("anexo", nombreAnexo); // retorna el nombre de como deberia guardarse el documento anexo
            obj.put("solicitud", secKioSoliciAusent);
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }

    public boolean creaKioSoliciAusentismos(String seudonimo, String secEmplJefe, String nit, String nombreAnexo, String fechaGeneracion, String fechainicial, String fechaFin, String dias, String observacion, String secCausaAusent, String cadena, String esquema) {
        System.out.println("Parametros creaKioSoliciAusentismos(): seudonimo: " + seudonimo + ", nit: " + nit + ", nombreAnexo: " + nombreAnexo + ", fechaGeneracion: " + fechaGeneracion
                + ", secEmplJefe: " + secEmplJefe + ", cadena: " + cadena);
        int conteo = 0;
        String secEmpleado = null;
        try {
            //String esquema = getEsquema(nit, cadena);
            setearPerfil(esquema, cadena);
            secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            if (secEmplJefe != null) {
                String sql = "INSERT INTO KIOSOLICIAUSENTISMOS "
                        + "(EMPLEADO, USUARIO, EMPLEADOJEFE, ACTIVA, FECHAGENERACION, NOMBREANEXO,"
                        + "FECHAINICIO, FECHAFIN, DIAS, OBSERVACION, CAUSAREPORTADA) "
                        + "VALUES "
                        + "(?,  USER, ?, 'S', TO_DATE(?, 'ddmmyyyy HH24miss'), ?,"
                        + "TO_DATE(?, 'DD/MM/YYYY'), TO_DATE(?, 'DD/MM/YYYY'), ?, ?, ?)";
                Query query = getEntityManager(cadena).createNativeQuery(sql);
                query.setParameter(1, secEmpleado);
                query.setParameter(2, secEmplJefe);
                query.setParameter(3, fechaGeneracion);
                query.setParameter(4, nombreAnexo);
                query.setParameter(5, fechainicial);
                query.setParameter(6, fechaFin);
                query.setParameter(7, dias);
                query.setParameter(8, observacion);
                query.setParameter(9, secCausaAusent);
                conteo = query.executeUpdate();
                System.out.println("Registro KIOSOLICIAUSENTISMOS: " + conteo);
            } else {
                conteo = 0; // No crear la solicitud si no hay un jefe relacionado
            }
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".creaKioSoliciAusentismos(): " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }

    /*Retorna la secuencia del registro en kioSoliciAusentismos*/
    public String getSecKioSoliciAusent(String secEmpl, String fechaGeneracion,
            String secEmplJefe, String nitEmpresa, String cadena, String esquema) {
        String secKioSoliciAusent = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            System.out.println("parametros getSecKioSoliciAusent(): secEmpl: " + secEmpl + ", fechaGeneracion: " + fechaGeneracion + ", secEmplJefe: " + secEmplJefe);

            String sqlQuery = "SELECT SECUENCIA FROM KIOSOLICIAUSENTISMOS WHERE EMPLEADO=? "
                    + " AND FECHAGENERACION=TO_DATE(?, 'ddmmyyyy HH24miss') "
                    + " AND EMPLEADOJEFE=? AND ACTIVA='S' ";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechaGeneracion);
            query.setParameter(3, secEmplJefe);
            secKioSoliciAusent = query.getSingleResult().toString();
            System.out.println("SecKioSoliciAusent: " + secKioSoliciAusent);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".+getSecKioSoliciAusent(): " + e.getMessage());
        }
        return secKioSoliciAusent;
    }

    public boolean creaKioNovedadSoliciAusent(String seudonimo, String nitEmpresa, String fechainicial,
            String secTipo, String secClase, String secCausa, String secCodDiagnostico,
            int dias, String fechaFin, String kioSoliciAusentismo, String secKioNovedadSoliciAusent, String formaLiq, String porcentajeLiq,
            String cadena, String esquema) {
        int conteo = 0;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            System.out.println("parametros creaKioNovedadSolici seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", fechainicial: " + fechainicial
                    + " fecha fin: " + fechaFin + " dias: " + dias + " secTipo" + secTipo + " secClase " + secClase + " secCausa "
                    + " secCodDiagnostico " + secCodDiagnostico + " kioSoliciAusentismo " + kioSoliciAusentismo + " secKioNovedadSoliciAusent "
                    + secKioNovedadSoliciAusent + " porcentajeLiq " + porcentajeLiq + " formaLiq " + formaLiq);
            String sql = "INSERT INTO KIONOVEDADESSOLICIAUSENT "
                    + "(EMPLEADO, FECHAINICIALAUSENTISMO, DIAS, TIPO, SUBTIPO, "
                    + "TIPOAUSENTISMO, CLASEAUSENTISMO, CAUSAAUSENTISMO, "
                    + "FECHASISTEMA, FECHAFINAUSENTISMO, ESTADO, \n"
                    + "FECHAINICIALPAGO, FECHAFINPAGO, FECHAEXPEDICION, FORMALIQUIDACION, PORCENTAJELIQ, "
                    + "DIAGNOSTICOCATEGORIA, KIOSOLICIAUSENTISMO, KIONOVEDADPRORROGA, PAGADO) \n"
                    + "VALUES \n"
                    // + "(?, TO_DATE(?,'DD/MM/YYYY'), ?, 'AUSENTISMO', 'AUSENTISMO', "
                    + "(?, TO_DATE(?,'DD/MM/YYYY'), ?, 'AUSENTISMO', 'AUSENTISMO', "
                    + "?, ?, ?, "
                    //+ "SYSDATE, TO_DATE(?,'DD/MM/YYYY'), 'ABIERTO', "
                    + "SYSDATE, TO_DATE(?,'DD/MM/YYYY'), 'ABIERTO', "
                    //+ "?, TO_DATE(?,'DD/MM/YYYY'), SYSDATE, ?, ?, "
                    + "TO_DATE(?,'DD/MM/YYYY'), TO_DATE(?,'DD/MM/YYYY'), SYSDATE, ?, ?, "
                    + "?, ?, ?, 'N')";
            Query query = getEntityManager(cadena).createNativeQuery(sql);
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechainicial);
            query.setParameter(3, dias);
            query.setParameter(4, secTipo);
            query.setParameter(5, secClase);
            query.setParameter(6, secCausa);
            query.setParameter(7, fechaFin); // fecha fin ausentismo
            query.setParameter(8, fechainicial); // fecha inicial pago
            query.setParameter(9, fechaFin); // fecha fin pago
            query.setParameter(10, formaLiq); // formaLiquidacion
            query.setParameter(11, porcentajeLiq); // porcentLiq
            query.setParameter(12, secCodDiagnostico.equals("null") ? null : secCodDiagnostico); // diagnostico
            query.setParameter(13, kioSoliciAusentismo); // kioSoliciAusentismo
            query.setParameter(14, secKioNovedadSoliciAusent.equals("null") ? null : secKioNovedadSoliciAusent); // kioNovedadProrroga
            System.out.println("Pruebas de nose3 ");
            conteo = query.executeUpdate();
            System.out.println("Pruebas de nose4 ");
            System.out.println("return creaKioNovedadSolici(): " + conteo);
            return conteo > 0;
        } catch (Exception e) {
            System.out.println("Error creaKioNovedadSolici: " + e.getMessage());
            return false;
        }
    }

    /*Crea nuevo registro kioEstadosSoliciAusent */
    public boolean creaKioEstadoSoliciAusent(
            String seudonimo, String nit, String kioSoliciAusentismo,
            String fechaProcesa, String estado, String motivo, String cadena, String esquema) {
        System.out.println("parametros creaKioEstadoSoliciAusent(): seudonimo: " + seudonimo + ", nit: " + nit + ", kiosoliciausentismo: " + kioSoliciAusentismo + ""
                + "\n fechaProcesa: " + fechaProcesa + ", estado: " + estado + ", cadena: " + cadena);
        int res = 0;
        try {
            String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            //String esquema = getEsquema(nit, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "INSERT INTO KIOESTADOSSOLICIAUSENT "
                    + "(KIOSOLICIAUSENTISMO, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, MOTIVOPROCESA)\n"
                    + "VALUES "
                    + "(?, TO_DATE(?, 'ddmmyyyy HH24miss'), ?, ?, ?)";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioSoliciAusentismo);
            query.setParameter(2, fechaProcesa);
            query.setParameter(3, estado);
            query.setParameter(4, secEmpl);
            query.setParameter(5, motivo);
            res = query.executeUpdate();
            System.out.println("registro kioestadosoliciausent: " + res);
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".creaKioEstadoSoliciAusent(): " + ex.getMessage());
            return false;
        }
        return res > 0;
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

    public String getEmplJefeXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        String secEmplJefe = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSA.EMPLEADOJEFE \n"
                    + "FROM \n"
                    + "KIOESTADOSSOLICIAUSENT KES, \n"
                    + "KIOSOLICIAUSENTISMOS KSA "
                    + "WHERE \n"
                    + "KES.KIOSOLICIAUSENTISMO=KSA.SECUENCIA \n"
                    + "AND KES.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secEmplJefe = query.getSingleResult().toString();
            System.out.println("Empl jefe asociado: " + secEmplJefe);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getEmplJefeXsecKioEstadoSolici: " + e.getMessage());
        }
        return secEmplJefe;
    }

    public String getCorreoXsecPer(String secPersona, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getCorreoXsecPer(): secPer: " + secPersona + ", cadena: " + cadena);
        String correo = null;
        String sqlQuery;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT P.EMAIL "
                    + " FROM PERSONAS P "
                    + " WHERE "
                    + " P.SECUENCIA=? ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPersona);
            System.out.println("getCorreoXsecPer(): " + correo);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + "getCorreoXsecPer(): " + e.getMessage());
        }
        return correo;
    }

    public String getApellidoNombreXSecPer(String secPersona, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getApellidoNombreXSecPer(): secPersona: " + secPersona + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String nombre = null;
        //boolean seudonimo_documento = validarCodigoUsuario(usuario);
        String sqlQuery;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            // if (seudonimo_documento) {
            sqlQuery = "SELECT PRIMERAPELLIDO||' '||SEGUNDOAPELLIDO||' '||NOMBRE nombreCompleto FROM PERSONAS WHERE SECUENCIA=?";
            //} else {
            //  sqlQuery = "SELECT InitCap(NVL(NOMBRE, 'USUARIO')) NOMBRE FROM PERSONAS WHERE EMAIL=?";
            //}
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPersona);
            nombre = query.getSingleResult().toString();
            System.out.println("Nombre Autorizador vacaciones: " + nombre);
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + "." + "getApellidoNombreXSecPer" + "()");
        }
        return nombre;
    }

    public String getSecPerAutorizadorXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecPerAutorizadorXsecKioEstadoSolici(): secKioEstadoSolici: " + secKioEstadoSolici + " nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secPerAutorizador = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSA.AUTORIZADOR \n"
                    + "FROM \n"
                    + "KIOESTADOSSOLICIAUSENT KES, \n"
                    + "KIOSOLICIAUSENTISMOS KSA, \n"
                    + "KIONOVEDADESSOLICIAUSENT  KNSA \n"
                    + "WHERE\n"
                    + "KES.KIOSOLICIAUSENTISMO=KSA.SECUENCIA\n"
                    + "AND KNSA.KIOSOLICIAUSENTISMO = KSA.SECUENCIA \n"
                    + "AND KES.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secPerAutorizador = query.getSingleResult().toString();
            System.out.println("Secuencia persona KioAutorizador: " + secPerAutorizador);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecPerAutorizadorXsecKioEstadoSolici: " + e.getMessage());
        }
        return secPerAutorizador;
    }

    public String consultarSecuenciaEmpleadoJefe(String secEmpleado, String nitEmpresa, String cadena, String esquema) {
        System.out.println("parametros consultarSecuenciaEmpleadoJefe: secEmpleado: " + secEmpleado + ", cadena: " + cadena);
        String secJefe = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "select empj.secuencia \n"
                    + "from empleados empl, empresas em, vigenciascargos vc, estructuras es, organigramas o, empleados empj, personas pj \n"
                    + "where em.secuencia = empl.empresa \n"
                    + "and vc.empleado = empl.secuencia \n"
                    + "and es.secuencia = vc.estructura\n"
                    + "and o.secuencia = es.organigrama \n"
                    + "and em.secuencia = o.empresa \n"
                    + "and vc.empleadojefe = empj.secuencia \n"
                    + "and pj.secuencia = empj.persona \n"
                    + "and empl.secuencia = ? \n"
                    + "and vc.fechavigencia = (select max(vci.fechavigencia) \n"
                    + "                        from vigenciascargos vci \n"
                    + "                        where vci.empleado = vc.empleado \n"
                    + "                       and vci.fechavigencia <= sysdate)";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            secJefe = query.getSingleResult().toString();
            System.out.println("secuencia jefe: " + secJefe);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".consultarSecuenciaEmpleadoJefe: " + e.getMessage());
        }
        return secJefe;
    }

    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getEmplXsecKioEstadoSolici(): kioEstadoSolici: " + kioEstadoSolici + ", cadena: " + cadena);
        String secEmpl = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSA.EMPLEADO\n"
                    + "FROM \n"
                    + "KIOESTADOSSOLICIAUSENT KES, \n"
                    + "KIOSOLICIAUSENTISMOS KSA \n"
                    + "WHERE\n"
                    + "KES.KIOSOLICIAUSENTISMO=KSA.SECUENCIA "
                    + "AND KES.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioEstadoSolici);
            secEmpl = query.getSingleResult().toString();
            System.out.println("Valor getEmplXsecKioEstadoSolici(): " + secEmpl);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getEmplXsecKioEstadoSolici(): " + e.getMessage());
        }
        return secEmpl;
    }

    public String getApellidoNombreXsecEmpl(String secEmpl, String nitEmpresa, String cadena, String esquema) {
        System.out.println("getApellidoNombreXsecEmpl()");
        String nombre = null;
        //String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        try {
            String sqlQuery = "SELECT UPPER(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE) NOMBRE "
                    + " FROM PERSONAS P, EMPLEADOS EMPL "
                    + " WHERE P.SECUENCIA=EMPL.PERSONA "
                    + " AND EMPL.SECUENCIA=?";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            nombre = (String) query.getSingleResult();
            System.out.println("Resultado getApellidoNombreXsecEmpl(): " + nombre);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getApellidoNombreXsecEmpl(): " + e);
        }
        return nombre;
    }

    public String getCorreoXsecEmpl(String secEmpl, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getCorreoXsecEmpl(): secEmpl: " + secEmpl + ", cadena: " + cadena);
        System.out.println("sec Empleado: " + secEmpl);
        String correo = null;
        String sqlQuery;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT P.EMAIL "
                    + " FROM CONEXIONESKIOSKOS CK, EMPLEADOS E, PERSONAS P "
                    + " WHERE CK.EMPLEADO=E.SECUENCIA "
                    + " AND P.SECUENCIA=E.PERSONA "
                    + " AND CK.EMPLEADO=?";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            System.out.println("getCorreoXsecEmpl(): " + correo);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + "getCorreoXsecEmpl(): " + e.getMessage());
        }
        return correo;
    }

    @GET
    @Path("/fechaFinAusentismo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFecha(@HeaderParam("authorization") String token,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena, @QueryParam("fechainicio") String fechaInicio,
            @QueryParam("dias") String dias, @QueryParam("usuario") String seudonimo,
            @QueryParam("causa") String causa) {
        System.out.println("Token recibido fechafin: " + token);
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String fechaSugerida = calculaFechafinAusent(fechaInicio, dias, seudonimo, causa, cadena, nitEmpresa, esquema);
        String msj = getMsj(fechaInicio, seudonimo, cadena, nitEmpresa, esquema);
        String formaliq = getCausaFormaLiq(causa, cadena, esquema);
        String porcentajeliq = getCausaPorcentajeLiq(causa, cadena, esquema);

        JsonObject json = Json.createObjectBuilder()
                .add("fechafin", fechaSugerida)
                .add("msj", msj)
                .add("formaliq", formaliq)
                .add("porcentajeliq", porcentajeliq)
                .build();
        /*return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();*/
        return Response.status(Response.Status.CREATED).entity(json)
                .build();
    }

    @GET
    @Path("/prorroga")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List getProrrogaSig(@QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena, @QueryParam("empleado") String empleado,
            @QueryParam("causa") String causa, @QueryParam("fechainicio") String fechaInicio) {
        System.out.println("Parametros getProrrogaSig: nitempresa: " + nitEmpresa + ", causa: " + causa + ", fechainicio: " + fechaInicio);
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String secEmpleado = getSecuenciaEmplPorSeudonimo(empleado, nitEmpresa, cadena, esquema);
        System.out.println("datos para encontrar la prorroga " + secEmpleado + nitEmpresa + cadena + esquema);
        List prorroga = getProrroga(secEmpleado, causa, fechaInicio, cadena, esquema);

        /*return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();*/
        return prorroga;
    }

    @GET
    @Path("/solicitudXEstado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudXEstado(@QueryParam("documento") String documento,
            @QueryParam("empresa") String nitEmpresa,
            @QueryParam("estado") String estado,
            @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        System.out.println("Parametros getSolicitudXEstado(): seudonimo: " + documento + ", empresa: " + nitEmpresa + ", estado: " + estado + ", cadena: " + cadena);
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String secEmpl = getSecuenciaEmplPorSeudonimo(documento, nitEmpresa, cadena, esquema);
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "select \n"
                    + "to_char(KSA.fechageneracion, 'dd/mm/yyyy') fechacreacion, \n"
                    + "TO_CHAR(KSA.FECHAINICIO,'DD/MM/YYYY' ) INICIALAUSENT,\n"
                    + "KSA.dias dias, \n"
                    + "to_char(KES.fechaprocesamiento,'dd/mm/yyyy') fechaprocesamiento, \n"
                    + "KES.estado,\n"
                    + "TO_CHAR(KSA.FECHAFIN,'DD/MM/YYYY') FECHAREGRESO,\n"
                    + "KES.MOTIVOPROCESA motivoprocesa, \n"
                    + "(SELECT DESCRIPCION FROM CAUSASAUSENTISMOS CA WHERE KSA.CAUSAREPORTADA = CA.SECUENCIA) CAUSA,\n"
                    + "(SELECT DESCRIPCION FROM TIPOSAUSENTISMOS TI WHERE KNSA.TIPOAUSENTISMO = TI.SECUENCIA),\n"
                    + "(SELECT DESCRIPCION FROM CLASESAUSENTISMOS CA WHERE KNSA.CLASEAUSENTISMO = CA.SECUENCIA),\n"
                    + "(SELECT CODIGO FROM DIAGNOSTICOSCATEGORIAS DC WHERE KNSA.DIAGNOSTICOCATEGORIA = DC.SECUENCIA),\n"
                    + "(SELECT DESCRIPCION FROM DIAGNOSTICOSCATEGORIAS DC WHERE KNSA.DIAGNOSTICOCATEGORIA = DC.SECUENCIA),\n"
                    + "DECODE(KNSA.KIONOVEDADPRORROGA, null, 'NO', 'SI'),\n"
                    + "DECODE(KES.PERSONAEJECUTA, null, \n"
                    + "(select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido \n"
                    + "from personas pei, empleados ei where ei.persona=pei.secuencia and ei.secuencia=KSA.empleadojefe), \n"
                    + "(select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido from personas pei \n"
                    + "where pei.secuencia=KSA.AUTORIZADOR) \n"
                    + ") empleadoejecuta,  \n"
                    + "KES.secuencia secuencia, \n"
                    + "KSA.NOMBREANEXO ANEXO \n"
                    + "from KIOESTADOSSOLICIAUSENT KES, \n"
                    + "KIOSOLICIAUSENTISMOS KSA, \n"
                    + "KIONOVEDADESSOLICIAUSENT  KNSA \n"
                    + "where \n"
                    + "KSA.SECUENCIA = KES.KIOSOLICIAUSENTISMO\n"
                    + "AND KSA.SECUENCIA=KNSA.KIOSOLICIAUSENTISMO\n"
                    + "AND KNSA.FECHAINICIALAUSENTISMO = (select MIN(ei.FECHAINICIALAUSENTISMO) \n"
                    + "    from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                    + "    where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                    + "    and ksi.secuencia=KSA.secuencia)\n"
                    + "and KES.FECHAPROCESAMIENTO = (select MAX(ei.FECHAPROCESAMIENTO) \n"
                    + "from KIOESTADOSSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                    + "where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                    + "and ksi.secuencia=KSA.secuencia) \n"
                    + "AND (KES.FECHAPROCESAMIENTO = (SELECT MAX(t3.FECHAPROCESAMIENTO) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                    + "WHERE ((t4.SECUENCIA = KSA.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO))))\n"
                    + "and KSA.empleado = ?\n"
                    + "and KES.estado = ?\n"
                    + "order by KES.fechaProcesamiento DESC";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, estado);
            s = query.getResultList();
            s.forEach(System.out::println);

            if (s.size() > 0) {
                for (int i = 0; i < s.size(); i++) {
                    JsonObject json = Json.createObjectBuilder()
                            .add("1", s.get(0).toString())
                            .build();
                }
            }
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + this.getClass().getName() + ".getSolicitudXEstado(): " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity("").build();
        }
    }

    /*@GET
    @Path("/solicitudesXEmpleadoJefe")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudesXEmpleadoJefe(@QueryParam("usuario") String seudonimo,
            @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String secEmplJefe = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        System.out.println("Webservice: solicitudesXEmpleadoJefe Parametros: usuario: secEmplJefe: " + secEmplJefe + ", empresa: " + nitEmpresa);
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT \n"
                    + "t1.codigoempleado documento, \n"
                    + "REPLACE(TRIM(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE), '  ', ' ') NOMBRE,\n"
                    + "KES.SECUENCIA, \n"
                    + "TO_CHAR(KES.FECHAPROCESAMIENTO, 'DD/MM/YYYY') SOLICITUD, \n"
                    + "TO_CHAR(KSA.FECHAINICIO,'DD/MM/YYYY' ) INICIALAUSENT,\n"
                    + "KES.ESTADO, \n"
                    + "KES.MOTIVOPROCESA, \n"
                    + "KES.SOAUSENTISMO, \n"
                    + "TO_CHAR(KSA.FECHAFIN,'DD/MM/YYYY') FECHAREGRESO,\n"
                    + "(SELECT DESCRIPCION FROM CAUSASAUSENTISMOS CA WHERE KSA.CAUSAREPORTADA = CA.SECUENCIA) CAUSA,\n"
                    + "KSA.DIAS,\n"
                    + "(SELECT DESCRIPCION FROM TIPOSAUSENTISMOS TI WHERE KNSA.TIPOAUSENTISMO = TI.SECUENCIA),\n"
                    + "(SELECT DESCRIPCION FROM CLASESAUSENTISMOS CA WHERE KNSA.CLASEAUSENTISMO = CA.SECUENCIA),\n"
                    + "KNSA.CAUSAAUSENTISMO,\n"
                    + "KNSA.KIONOVEDADPRORROGA,\n"
                    + "DC.CODIGO,\n"
                    + "DC.DESCRIPCION,\n"
                    + "(SELECT PER.PRIMERAPELLIDO||' '||PER.SEGUNDOAPELLIDO||' '||PER.NOMBRE FROM PERSONAS PER, EMPLEADOS EMPL\n"
                    + "WHERE EMPL.PERSONA=PER.SECUENCIA\n"
                    + "AND EMPL.SECUENCIA=JEFE.SECUENCIA) EMPLEADOJEFE,        \n"
                    + "KNSA.FECHAFINPAGO FECHAPAGO,\n"
                    + "KES.secuencia secuencia\n"
                    + "FROM KIOESTADOSSOLICIAUSENT KES, KIOSOLICIAUSENTISMOS KSA, EMPLEADOS t1, PERSONAS P, KIONOVEDADESSOLICIAUSENT KNSA,\n"
                    + "DIAGNOSTICOSCATEGORIAS DC, EMPLEADOS JEFE\n"
                    + "WHERE (((((t1.EMPRESA = (select secuencia from empresas where nit= ?)) \n"
                    + "AND (KES.ESTADO IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO'))) \n"
                    + "AND (KSA.EMPLEADOJEFE = ?)) \n"
                    + " AND (KSA.EMPLEADOJEFE =(select ei.secuencia from empleados ei, personas pei where ei.persona=pei.secuencia and pei.numerodocumento=?)))\n"
                    + "AND (KSA.EMPLEADOJEFE =?))\n"
                    + "AND (KES.SECUENCIA = (SELECT MAX(t3.SECUENCIA) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                    + "WHERE ((t4.SECUENCIA = KSA.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO))))\n"
                    + "AND ((KSA.SECUENCIA = KES.KIOSOLICIAUSENTISMO) AND (t1.SECUENCIA = KSA.EMPLEADO))\n"
                    + "AND t1.PERSONA=P.SECUENCIA\n"
                    + "AND KSA.SECUENCIA=KNSA.KIOSOLICIAUSENTISMO\n"
                    + "AND KNSA.SECUENCIA = (select min(ei.SECUENCIA) \n"
                    + "    from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                    + "    where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                    + "    and ksi.secuencia=KSA.secuencia)\n"
                    + "AND KSA.EMPLEADOJEFE=JEFE.SECUENCIA \n"
                    + "AND DC.SECUENCIA(+) = KNSA.DIAGNOSTICOCATEGORIA\n"
                    + "ORDER BY KES.FECHAPROCESAMIENTO DESC";
            //Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secEmplJefe);
            query.setParameter(3, seudonimo);
            query.setParameter(4, secEmplJefe);
            s = query.getResultList();
            s.forEach(System.out::println);

            if (s.size() > 0) {
                for (int i = 0; i < s.size(); i++) {
                    JsonObject json = Json.createObjectBuilder()
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
    }*/
    @GET
    @Path("/soliciSinProcesarJefe")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSoliciSinProcesarJefe(@QueryParam("nit") String nitEmpresa, @QueryParam("jefe") String jefe,
            @QueryParam("estado") String estado, @QueryParam("cadena") String cadena) {
        System.out.println("parametros getSoliciSinProcesarJefe(): nit: " + nitEmpresa + " jefe " + jefe + " estado: " + estado + " cadena " + cadena);
        List s = null;
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        try {
            String secuenciaJefe = getSecuenciaEmplPorSeudonimo(jefe, nitEmpresa, cadena, esquema);
            String secuenciaEmpresa = getSecuenciaPorNitEmpresa(nitEmpresa, cadena, esquema);
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT \n"
                    + "t1.codigoempleado documento, \n"
                    + "REPLACE(TRIM(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE), '  ', ' ') NOMBRE,\n"
                    + "KES.SECUENCIA, \n"
                    + "TO_CHAR(KES.FECHAPROCESAMIENTO, 'DD/MM/YYYY') SOLICITUD, \n"
                    + "TO_CHAR(KSA.FECHAINICIO,'DD/MM/YYYY' ) INICIALAUSENT,\n"
                    + "TO_CHAR(KES.FECHAPROCESAMIENTO, 'DD/MM/YYYY') FECHAULTMODIF,\n"
                    + "KES.ESTADO, \n"
                    + "KES.MOTIVOPROCESA, \n"
                    + "KES.SOAUSENTISMO, \n"
                    + "TO_CHAR(KSA.FECHAFIN,'DD/MM/YYYY') FECHAREGRESO,\n"
                    + "(SELECT DESCRIPCION FROM CAUSASAUSENTISMOS CA WHERE KSA.CAUSAREPORTADA = CA.SECUENCIA) CAUSA,\n"
                    + "KSA.DIAS,\n"
                    + "(SELECT DESCRIPCION FROM TIPOSAUSENTISMOS TI WHERE KNSA.TIPOAUSENTISMO = TI.SECUENCIA) TIPO,\n"
                    + "(SELECT DESCRIPCION FROM CLASESAUSENTISMOS CA WHERE KNSA.CLASEAUSENTISMO = CA.SECUENCIA) CLASE,\n"
                    + "DECODE(KNSA.KIONOVEDADPRORROGA, null, 'NO', 'SI'),\n"
                    + "DC.CODIGO,\n"
                    + "DC.DESCRIPCION,\n"
                    + "(SELECT PER.PRIMERAPELLIDO||' '||PER.SEGUNDOAPELLIDO||' '||PER.NOMBRE FROM PERSONAS PER, EMPLEADOS EMPL\n"
                    + "WHERE EMPL.PERSONA=PER.SECUENCIA\n"
                    + "AND EMPL.SECUENCIA=JEFE.SECUENCIA) EMPLEADOJEFE,        \n"
                    + "KSA.OBSERVACION OBSERCAVION,\n"
                    + "KNSA.FECHAFINPAGO FECHAPAGO,\n"
                    + "KES.secuencia secuencia, \n"
                    + "KSA.NOMBREANEXO ANEXO,"
                    + "P.NUMERODOCUMENTO NUMDOCUMENTO \n"
                    + "FROM \n"
                    + "KIOESTADOSSOLICIAUSENT KES,\n"
                    + "KIOSOLICIAUSENTISMOS KSA,\n"
                    + "EMPLEADOS t1,    \n"
                    + "PERSONAS P,\n"
                    + "KIONOVEDADESSOLICIAUSENT KNSA,\n"
                    + "EMPLEADOS JEFE,\n"
                    + "DIAGNOSTICOSCATEGORIAS DC\n"
                    + "WHERE \n"
                    + "((((\n"
                    + "(t1.EMPRESA = ?) AND (KES.ESTADO = ?)) AND (KSA.EMPLEADOJEFE = ?))) \n"
                    + "AND ((KSA.SECUENCIA = KES.KIOSOLICIAUSENTISMO) AND (t1.SECUENCIA = KSA.EMPLEADO))) \n"
                    + "AND T1.PERSONA=P.SECUENCIA\n"
                    + "AND KSA.SECUENCIA=KNSA.KIOSOLICIAUSENTISMO\n"
                    + "AND KNSA.FECHAINICIALAUSENTISMO = (select MIN(ei.FECHAINICIALAUSENTISMO) \n"
                    + "   from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                    + "    where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                    + "    and ksi.secuencia=KSA.secuencia)\n"
                    + "and KES.FECHAPROCESAMIENTO = (select max(ei.FECHAPROCESAMIENTO) \n"
                    + "from KIOESTADOSSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                    + "where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                    + "and ksi.secuencia=KSA.secuencia)\n"
                    + "AND (KES.FECHAPROCESAMIENTO = (SELECT MAX(t3.FECHAPROCESAMIENTO) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                    + "WHERE ((t4.SECUENCIA = KSA.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO))))\n"
                    + "AND KSA.EMPLEADOJEFE=JEFE.SECUENCIA  \n"
                    + "AND DC.SECUENCIA(+) = KNSA.DIAGNOSTICOCATEGORIA\n"
                    + "ORDER BY KES.FECHAPROCESAMIENTO DESC";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secuenciaEmpresa);
            query.setParameter(2, estado);
            query.setParameter(3, secuenciaJefe);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getSoliciSinProcesarJefe: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    @GET
    @Path("/validaFechaNovedadEmpleadoXJefe")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getvalidaFechaNovedadEmpleadoXJefe(@QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena, @QueryParam("fechainicio") String fechaInicio,
            @QueryParam("usuario") String seudonimo) {
        System.out.println("parametros getvalidaFechaNovedadEmpleadoXJefe(): nit: " + nitEmpresa + " fechainicio " + fechaInicio + " usuario: " + seudonimo);
        String Msj = null;
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);

            Msj = getMsj(fechaInicio, seudonimo, cadena, nitEmpresa, esquema);
            JsonObject json = Json.createObjectBuilder()
                    .add("valida", Msj)
                    .build();

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getvalidaFechaNovedadEmpleadoXJefe: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    @POST
    @Path("/nuevoEstadoSolici")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response setNuevoEstadoSolici(@QueryParam("secuencia") String secKioEstadoSolici, @QueryParam("motivo") String motivo,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("estado") String estado,
            @QueryParam("fechainicio") String fechaInicio,
            @QueryParam("cadena") String cadena, @QueryParam("grupo") String grupoEmpr, @QueryParam("urlKiosco") String urlKiosco) {
        System.out.println("nuevoEstadoSolici()");
        System.out.println("parametros: secuencia: " + secKioEstadoSolici + ", motivo " + motivo + ", empleado " + seudonimo + ", estado: " + estado + ", cadena " + cadena + ", nit: " + nitEmpresa + ", urlKiosco: " + urlKiosco + ", grupoEmpresarial: " + grupoEmpr);
        System.out.println("esta es la fecha inicio: " + fechaInicio);

        List s = null;
        int res = 0;
        String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(new Date());
        String urlKio = urlKiosco + "#/login/" + grupoEmpr;
        String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupoEmpr;
        String esquema = null;
        try {
            esquema = getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        try {
            String secEmplEjecuta = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            System.out.println("La persona que ejecuta es: " + secEmplEjecuta);
            String secEmplSolicita = getEmplXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena, esquema);
            String secEmplJefe = null;
            String secPerAutoriza = null;
            String nombreAutorizaSolici = "";
            String correoAutorizaSolici = null;
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            if (motivo == null || motivo == "") {
                motivo = " ";
            }
            /*try {
                secPerAutoriza = getSecPerAutorizadorXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar autorizador relacionado a la solicitud");
            }*/
            if (secPerAutoriza != null) {
                nombreAutorizaSolici = getApellidoNombreXSecPer(secPerAutoriza, nitEmpresa, cadena, esquema);
                correoAutorizaSolici = getCorreoXsecPer(secPerAutoriza, nitEmpresa, cadena, esquema);
            } else {
                try {
                    secEmplJefe = getEmplJefeXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
                    if (secEmplJefe != null) {
                        nombreAutorizaSolici = getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
                        correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
                    }
                } catch (Exception e) {
                    System.out.println("Error al consultar empleadoJefe relacionado a la solicitud");
                }
            }
            String sqlQuery = "";
            Query query = null;
            if (estado.equals("RECHAZADO")) {
                sqlQuery = "INSERT INTO KIOESTADOSSOLICIAUSENT \n"
                        + "(KIOSOLICIAUSENTISMO, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, SOAUSENTISMO, MOTIVOPROCESA, PERSONAEJECUTA)\n"
                        + "SELECT\n"
                        + "KIOSOLICIAUSENTISMO, TO_DATE(?, 'ddmmyyyy HH24miss') , ?, ? EMPLEADOEJECUTA\n"
                        + ", SOAUSENTISMO, ?, ?\n"
                        + "FROM KIOESTADOSSOLICIAUSENT\n"
                        + "WHERE SECUENCIA=?";
                //String esquema = getEsquema(nitEmpresa, cadena);
                setearPerfil(esquema, cadena);
                query = getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, fechaGeneracion);
                query.setParameter(2, estado);
                query.setParameter(3, secEmplEjecuta);
                query.setParameter(4, motivo);
                query.setParameter(5, secPerAutoriza); // null
                // query.setParameter(4, secPerAutoriza);
                query.setParameter(6, secKioEstadoSolici);
            } else {
                sqlQuery = "INSERT INTO KIOESTADOSSOLICIAUSENT \n"
                        + "(KIOSOLICIAUSENTISMO, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, SOAUSENTISMO, PERSONAEJECUTA)\n"
                        + "SELECT\n"
                        + "KIOSOLICIAUSENTISMO, TO_DATE(?, 'ddmmyyyy HH24miss'), ?, ? EMPLEADOEJECUTA \n"
                        + ", SOAUSENTISMO, ? \n"
                        + "FROM KIOESTADOSSOLICIAUSENT \n"
                        + "WHERE SECUENCIA=?";
                //String esquema = getEsquema(nitEmpresa, cadena);
                setearPerfil(esquema, cadena);
                query = getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, fechaGeneracion);
                query.setParameter(2, estado);
                query.setParameter(3, secEmplEjecuta);
                if (estado.equals("CANCELADO")) {
                    query.setParameter(4, null);
                    System.out.println("La solicitud está siendo CANCELADA");
                } else {
                    query.setParameter(4, secPerAutoriza);
                }
                // query.setParameter(4, secPerAutoriza);
                query.setParameter(5, secKioEstadoSolici);
            }
            res = query.executeUpdate();
            EnvioCorreo c = new EnvioCorreo();
            String estadoVerbo = estado.equals("CANCELADO") ? "CANCELAR"
                    : estado.equals("AUTORIZADO") ? "PRE-APROBAR"
                    : estado.equals("RECHAZADO") ? "RECHAZAR" : estado;
            String estadoPasado = estado.equals("CANCELADO") ? "canceló"
                    : estado.equals("AUTORIZADO") ? "pre-aprobó"
                    : estado.equals("RECHAZADO") ? "rechazó" : estado;
            String mensaje = "Nos permitimos informar que se acaba de " + estadoVerbo + " una novedad de ausentismo";
            if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")) {
                mensaje += " creada para " + getApellidoNombreXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema);
            }
            mensaje += " en el módulo de Kiosco Nómina Designer. Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                    + "<br><br>"
                    + "La persona que " + estadoPasado.toUpperCase() + " LA SOLICITUD es: " + getApellidoNombreXsecEmpl(secEmplEjecuta, nitEmpresa, cadena, esquema) + "<br>";
            if (estado.equals("CANCELADO")) {
                mensaje += "La persona a cargo de HACER EL SEGUIMIENTO es: " + nombreAutorizaSolici + "<br>";
            }
            mensaje += "Por favor seguir el proceso en: <a style='color: white !important;' target='_blank' href=" + urlKio + ">" + urlKio + "</a><br><br>"
                    + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita.<br><br>"
                    + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                    + "<br><a style='color: white !important;' href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a>";

            String fechaInicioAusent = fechaInicio;
            System.out.println("esta es la fecha inicio: " + fechaInicioAusent);
            System.out.println("url Kiosco: " + urlKio);
            if (res > 0) {
                System.out.println("solicitud " + estado + " con éxito.");
                String servidorsmtp = getConfigCorreoServidorSMTP(nitEmpresa, cadena, esquema);
                String puerto = getConfigCorreo(nitEmpresa, "PUERTO", cadena, esquema);
                String autenticado = getConfigCorreo(nitEmpresa, "AUTENTICADO", cadena, esquema);
                String starttls = getConfigCorreo(nitEmpresa, "STARTTLS", cadena, esquema);
                String remitente = getConfigCorreo(nitEmpresa, "REMITENTE", cadena, esquema);
                String clave = getConfigCorreo(nitEmpresa, "CLAVE", cadena, esquema);
                if (estado.equals("CANCELADO")) {
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema),
                            "Solicitud de AUSENTISMO Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de AUSENTISMO: " + fechaInicioAusent,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviado a la persona que ejecuta");
                    }
                    //------------ comentado
                    /*if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            //getCorreoXsecEmpl(secEmplJefe, cadena),
                            correoAutorizaSolici,
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioAusent,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviado al empleado que solicita asociado");
                    }*/
                    //----------
                }

                if (estado.equals("AUTORIZADO") || estado.equals("RECHAZADO")) {
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema),
                            "Solicitud de AUSENTISMO Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de AUSENTISMO: " + fechaInicioAusent,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviada a la persona que ejecuta");
                    }
                    // Enviar correo al jefe/autorizador de vacaciones
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            correoAutorizaSolici,
                            "Solicitud de AUSENTISMO Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de AUSENTISMO: " + fechaInicioAusent,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviado al empleado que solicita asociado " + correoAutorizaSolici);
                    }
                    //--------------comentado 
                    // Enviar correo de autoria
                    /*boolean auditProcesarJefe = consultaAuditoria("SOLICITUDVACACIONES", "33", nitEmpresa, cadena).compareTo(BigDecimal.ZERO) > 0;
                    boolean auditProcesarAutorizador =consultaAuditoria("SOLICITUDVACACIONES", "35", nitEmpresa, cadena).compareTo(BigDecimal.ZERO) > 0;
                    if (auditProcesarJefe || auditProcesarAutorizador) {
                        System.out.println("Si debe llevar auditoria procesar solicitud Vacaciones");
                        String sqlQueryAud = "select email from kioconfigmodulos where codigoopcion=? and nitempresa=?";
                        //Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                        System.out.println("Query2: " + sqlQuery);
                        Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                        if (secPerAutoriza!=null) {
                          query2.setParameter(1, "35");  
                            System.out.println("codigoOpcion 35");                          
                        } else {
                          query2.setParameter(1, "33"); 
                            System.out.println("codigoOpcion 33");
                        }
                        query2.setParameter(2, nitEmpresa);
                        List lista = query2.getResultList();
                        Iterator<String> it = lista.iterator();
                        System.out.println("obtener " + lista.get(0));
                        System.out.println("size: " + lista.size());
                        // String mensajeAuditoria = "Nos permitimos informar que " + personaCreaSolici
                           // + " generó la SOLICITUD DE VACACIONES el " + fechaCorreo + " a las " + horaGeneracion + " en el módulo de Kiosco Nómina Designer.";
                      String mensajeAuditoria="Nos permitimos informar que se acaba de "+estadoVerbo+" una solicitud de vacaciones";
                    if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")){
                        mensaje+=" creada para "+getApellidoNombreXsecEmpl(secEmplSolicita, nitEmpresa, cadena);
                    }
                    mensajeAuditoria+=" en el módulo de Kiosco Nómina Designer. Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                    + "<br><br>"
                    + "La persona que "+estadoPasado.toUpperCase()+" LA SOLICITUD es: "+getApellidoNombreXsecEmpl(secEmplEjecuta, nitEmpresa, cadena)+"<br>";
                        while (it.hasNext()) {
                            String correoenviar = it.next();
                            System.out.println("correo auditoria: " + correoenviar);
                            //c.pruebaEnvio2("smtp.gmail.com","587","pruebaskiosco534@gmail.com","Nomina01", "S", correoenviar,
                            // c.enviarCorreoVacaciones(servidorsmtp, puerto, autenticado, starttls, remitente, clave, correoenviar, 
                        "Auditoria: Nueva Solicitud de vacaciones Kiosco. "+fechaCorreo, mensajeAuditoria, urlKio, nit);
                            c.enviarCorreoInformativo("Auditoria: Se ha "+estadoPasado+" una Solicitud de vacaciones Kiosco. " + fecha,
                                    "Estimado usuario: ", mensajeAuditoria, nitEmpresa, urlKio, cadena, correoenviar, null);
                        }
                    } else {
                        System.out.println("No lleva auditoria Vacaciones");
                    }*/
                    //--------------
                }
            } else {
                System.out.println("Error al procesar la solicitud.");
            }
            return Response.status(Response.Status.OK).entity(res > 0).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".setNuevoEstadoSolici: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    /**
     *
     */
    /*public static Date ParseFecha(String fecha)
    {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
        } 
        catch (ParseException ex) 
        {
            System.out.println(ex);
        }
        System.out.println("Convertir a Date");
        return fechaDate;
    }*/
    /**
     * metodo publico para convertir a tipo fecha
     *
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater fecha en String, cadena del cliente, Esquema para setear
     * rol
     * @param result una variable en tipo Date
     * @return Date
     */
    public Date getDate(String fechaStr, String cadena, String esquema) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "getDate" + "()");
        String consulta = "SELECT "
                + "TO_DATE(?, 'dd/mm/yyyy') "
                + "FROM DUAL ";
        Query query = null;
        Date fechaRegreso = null;
        try {
            setearPerfil(esquema, cadena);
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaStr);
            fechaRegreso = (Date) (query.getSingleResult());
            System.out.println("getDate(): " + fechaRegreso);
            return fechaRegreso;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia en calculaFechaRegreso.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en calculaFechaRegreso");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general en calculaFechaRegreso. " + e);
            throw new Exception(e.toString());
        }
    }

    /**/
    public List getProrroga(String empleado, String causa, String fechaInicial, String cadena, String esquema) {
        System.out.println("Parametros getProrroga(): empleado: " + empleado + ", causa: " + causa);
        System.out.println("Entre a getProrroga");
        List prorroga = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT \n"
                    + "KNSA.secuencia, \n"
                    + "TO_CHAR(KNSA.FECHAFINAUSENTISMO+1, 'YYYY-MM-DD') finsiguiente, \n"
                    + "CA.DESCRIPCION, \n"
                    + "(select B.CODIGO from DIAGNOSTICOSCATEGORIAS B where B.secuencia = KNSA.diagnosticocategoria) codigo, \n"
                    + "(select  B.DESCRIPCION  from DIAGNOSTICOSCATEGORIAS B where B.secuencia = KNSA.diagnosticocategoria) descripcion, \n"
                    + "TO_CHAR(KNSA.FECHAINICIALAUSENTISMO, 'dd/mm/yyyy') FECHA, \n"
                    + "TO_CHAR(KNSA.FECHAFINAUSENTISMO , 'dd/mm/yyyy') FECHAFIN, \n"
                    + "KNSA.dias \n"
                    + "FROM \n"
                    + "CAUSASAUSENTISMOS CA, KIONOVEDADESSOLICIAUSENT KNSA, KIOESTADOSSOLICIAUSENT KES, KIOSOLICIAUSENTISMOS KSA \n"
                    + "WHERE CA.secuencia = KNSA.CAUSAAUSENTISMO \n"
                    + "AND KNSA.KIOSOLICIAUSENTISMO = KSA.SECUENCIA \n"
                    + "and KES.KIOSOLICIAUSENTISMO=KSA.SECUENCIA \n"
                    + "AND KNSA.EMPLEADO = ? \n"
                    + "AND KNSA.CAUSAAUSENTISMO in (?) \n"
                    + "AND KNSA.FECHAINICIALAUSENTISMO = (SELECT MAX(KNSAI.FECHAINICIALAUSENTISMO) \n"
                    + "FROM KIONOVEDADESSOLICIAUSENT KNSAI \n"
                    + "WHERE KNSAI.SECUENCIA=KNSA.SECUENCIA \n"
                    + "AND KNSA.KIOSOLICIAUSENTISMO=KNSAI.KIOSOLICIAUSENTISMO \n"
                    //+ "AND KNSAI.FECHAINICIALAUSENTISMO<=TO_DATE(?, 'YYYY-MM-DD')"
                    + ") \n"
                    + "AND KNSA.SECUENCIA NOT IN (\n"
                    + "    SELECT a1.KIONOVEDADPRORROGA FROM KIONOVEDADESSOLICIAUSENT a1 \n"
                    + "    WHERE a1.KIONOVEDADPRORROGA IS NOT NULL)"
                    + "AND KES.FECHAPROCESAMIENTO=(SELECT MAX(KESI.FECHAPROCESAMIENTO) \n"
                    + "FROM KIOESTADOSSOLICIAUSENT KESI \n"
                    + "WHERE KESI.SECUENCIA=KES.SECUENCIA \n"
                    + "AND KES.ESTADO IN ('ENVIADO','AUTORIZADO','LIQUIDADO'))";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, empleado);
            query.setParameter(2, causa);
            query.setParameter(3, fechaInicial);
            System.out.println("datos para encontrar la prorroga " + empleado + causa + esquema + cadena);
            prorroga = (List) query.getResultList();
            System.out.println("prorroga: " + prorroga);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getProrroga(): " + e);
        }
        return prorroga;
    }

    public String getDateYMD(String fechaStr, String cadena, String esquema) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "getDate" + "()");
        String consulta = "SELECT "
                + "TO_CHAR(?, 'yyyy-mm-dd') "
                + "FROM DUAL ";
        Query query = null;
        String fechaRegreso = null;
        try {
            setearPerfil(esquema, cadena);
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaStr);
            fechaRegreso = (String) (query.getSingleResult());
            System.out.println("getDate(): " + fechaRegreso);
            return fechaRegreso;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia en getDateYMD.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en getDateYMD");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general en getDateYMD. " + e);
            throw new Exception(e.toString());
        }
    }

    public String getCausaPorcentajeLiq(String causa, String cadena, String esquema) {
        System.out.println("Parametros getCausaPorcentajeLiq(): causa: " + causa);
        System.out.println("Entre a getCausaPorcentajeLiq");
        String porcentaje = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "select kioausentismo_pkg.CAUSAPORCENTAJELIQUIDACION(?) from dual ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, causa);
            porcentaje = query.getSingleResult().toString();
            System.out.println("porcentaje: " + porcentaje);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getCausaPorcentajeLiq(): " + e);
            porcentaje = null;
        }
        return porcentaje;
    }

    public String getCausaFormaLiq(String causa, String cadena, String esquema) {
        System.out.println("Parametros getCausaFormaLiq(): causa: " + causa);
        System.out.println("Entre a getCausaOrigen");
        String formaLiq = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "select kioausentismo_pkg.CAUSAFORMALIQUIDACION(?) from dual ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, causa);
            formaLiq = query.getSingleResult().toString();
            /*Iterator<String> it= CausaFormaLiquidacion.iterator();
            while(it.hasNext()) {
              System.out.println(it.next());
            }*/
            System.out.println("formaLiq: " + formaLiq);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/

        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getCausaOrigen(): " + e);
            formaLiq = null;
        }
        return formaLiq;
    }

    public String getMsj(String fechaInicio, String seudonimo,
            String cadena, String nitEmpresa, String esquema) {
        //mensaje 2
        System.out.println("Parametros getMsj(): nitEmpresa: " + nitEmpresa + " fechaInicio: " + fechaInicio + " seudonimo: " + seudonimo);
        System.out.println("Entre a getMsj");
        String msj = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "select kioausentismo_pkg.MENSAJEVALIDACIONAUSENT(?,to_date(?, 'dd/mm/yyyy'),?) from dual";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, fechaInicio);
            query.setParameter(3, nitEmpresa);
            msj = query.getSingleResult().toString();
            /*Iterator<String> it= CausaFormaLiquidacion.iterator();
            while(it.hasNext()) {
              System.out.println(it.next());
            }*/
            System.out.println("msj: " + msj);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/

        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getMsj(): " + e);
            msj = null;
        }
        return msj;
    }

    /**
     * metodo publico para consultar el acronimo de la causa seleccionada
     *
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia de la causa, cadena del cliente, Esquema para
     * setear rol
     * @param result una variable en tipo Sting con un tamaño de 2 caracteres
     * @return String
     */
    public String getCausaOrigenIncapacidad(String causa, String cadena, String esquema) {
        //mensaje 2
        System.out.println("Parametros getCausaOrigen(): causa: " + causa);
        System.out.println("Entre a getCausaOrigen");
        String CausaOrigenIncapacidad = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT ORIGENINCAPACIDAD "
                    + "FROM CAUSASAUSENTISMOS CA "
                    + "WHERE CA.SECUENCIA=? ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, causa);
            CausaOrigenIncapacidad = query.getSingleResult().toString();
            /*Iterator<String> it= CausaFormaLiquidacion.iterator();
            while(it.hasNext()) {
              System.out.println(it.next());
            }*/
            System.out.println("CausaOrigenIncapacidad: " + CausaOrigenIncapacidad);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/

        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getCausaOrigenIncapacidad(): " + e.getMessage());
            CausaOrigenIncapacidad = null;
        }
        return CausaOrigenIncapacidad;
    }

    // Método que retorna la secuencia de la causa correspondiente a los 2 primeros días para ENFERMEDAD GENERAL (cód 25)
    public String getSecCausaPrimerosDias(String codCausa, String cadena, String esquema) {
        //Secuencia de causa 
        System.out.println("Parametros getSecCausaEGPrimeros2Dias(): cadena: " + cadena + ", esquema: " + esquema);
        System.out.println("Entre a getSecCausaEGPrimeros2Dias");
        String secCausa = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "select secuencia from causasausentismos where codigo=? ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, codCausa);
            secCausa = query.getSingleResult().toString();
            /*Iterator<String> it= CausaFormaLiquidacion.iterator();
            while(it.hasNext()) {
              System.out.println(it.next());
            }*/
            System.out.println("getCausaFormaLiquidacion: " + secCausa);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/

        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getCausaFormaLiquidacion(): " + e.getMessage());
            secCausa = null;
        }
        return secCausa;
    }

    /**
     * metodo publico para consultar la fecha sugerida de regreso
     *
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater fechainicio, dias solicitados ,cadena del cliente,
     * Esquema para setear rol
     * @param result una variable en String con la fecha calculada
     * @return String
     */
    public String getFechaSugerida3(String fechaInicio, String dias, String cadena, String esquema) {
        //mensaje 4
        System.out.println("Parametros getFechaSugerida(): fechaInicio: " + fechaInicio + ", dias: " + dias);
        System.out.println("Entre a getFechaSugerida3");
        String fechaSugerida = null;
        String sqlQuery;

        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT TO_CHAR(TO_DATE(?, 'dd/mm/yyyy') + ?,'dd/mm/yyyy') FROM DUAL ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, fechaInicio);
            query.setParameter(2, dias);
            fechaSugerida = query.getSingleResult().toString();
            System.out.println("fechaSugerida: " + fechaSugerida);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getFechaSugerida3(): " + e);
        }
        return fechaSugerida;
    }

    /*
    Metodo que ejecuta la logica de sugerir fecha
     */
    public String calculaFechafinAusent(String fechaInicio, String dias, String seudonimo, String causa,
            String cadena, String nitEmpresa, String esquema) {
        //Se Crean las variable necesarias 
        System.out.println("Parametros calculaFechafinAusent() { seudonimo: " + seudonimo
                + ", nit: " + nitEmpresa + ", fechainicio: " + fechaInicio + ", dias: " + dias
                + ", secCausa: " + causa + ", esquema: " + esquema + " }");

        String fechaSugerida = null;
        String sqlQuery;

        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "select TO_CHAR(TO_DATE(kioausentismo_pkg.CALCULAFECHAFINAUSENT("
                    + "?, ?, ?, ? ,  ?)), 'DD/MM/YYYY') from dual";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, fechaInicio);
            query.setParameter(2, dias);
            query.setParameter(3, seudonimo);
            query.setParameter(4, causa);
            query.setParameter(5, nitEmpresa);
            fechaSugerida = query.getSingleResult().toString();
            System.out.println("fechaSugerida: " + fechaSugerida);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".calculaFechafinAusent(): " + e);
        }
        return fechaSugerida;
    }

    @POST
    @Path("/cargarAnexoAusentismo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response cargarAnexo(
            @FormDataParam("fichero") InputStream fileInputStream,
            @FormDataParam("fichero") FormDataContentDisposition fileFormDataContentDisposition,
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("solicitud") String secKioSoliciAusentismo,
            @QueryParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        String fileName = null;
        String uploadFilePath = null;
        try {
            fileName = fileFormDataContentDisposition.getFileName();
            uploadFilePath = writeToFileServer(fileInputStream, fileName, nitEmpresa, cadena);
            if (updateAnexoKioSoliciAusentismo(seudonimo, nitEmpresa, fileName, secKioSoliciAusentismo, cadena)) {
                System.out.println("Nombre de archivo actualizado en la solicitud");
            } else {
                System.out.println("Archivo subido pero no se actualizó el nombre en la solicitud.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error: " + this.getClass().getName() + ".cargarAnexo()" + ioe.getMessage());
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error " + this.getClass().getName() + ".cargarAnexo() " + e.getMessage());
        }
        return Response.ok("Fichero subido a " + uploadFilePath).build();
    }

    private String writeToFileServer(InputStream inputStream, String fileName, String nitEmpresa, String cadena) throws IOException {

        OutputStream outputStream = null;
        //String qualifiedUploadFilePath = UPLOAD_FILE_SERVER + fileName;
        String qualifiedUploadFilePath = getPathReportes(nitEmpresa, cadena) + "anexosAusentismos\\" + fileName;

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
    @Path("/enviaCorreoNuevoAusentismo")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean enviaCorreoNuevoAusentismo(@QueryParam("usuario") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("solicitud") String secKioSoliciAusentismo,
            @QueryParam("observacion") String observacionNovedad, @QueryParam("asunto") String asunto,
            @QueryParam("urlKiosco") String urlKiosco, @QueryParam("grupo") String grupo, @QueryParam("cadena") String cadena) {
        System.out.println("Parametros enviaCorreoNuevoAusentismo(): seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", secKioSoliciAusentismo: " + secKioSoliciAusentismo + " urlKiosco " + urlKiosco
                + " grupo " + grupo);
        String esquema = null;
        try {
            esquema = getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        String urlKio = urlKiosco + "#/login/" + grupo;
        String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupo;
        String nombreAutorizaSolici = "";
        String correoAutorizaSolici = null;
        String dias = "";
        String fechaFin = "";
        String fechaCorreo = "";
        String fechaInicial = "";
        String personaCreaSolici = "";
        String nombreAnexo = null;
        String secEmplJefe = getEmplJefeXsecKioSoliciAusentismo(secKioSoliciAusentismo, nitEmpresa, cadena);
        if (secEmplJefe != null) {
            nombreAutorizaSolici = getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
            correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
        }
        List<Object[]> list = null;
        list = getObjectDetalleAusentismo(secKioSoliciAusentismo, nitEmpresa, cadena);
        System.out.println("size: " + list.size());
        System.out.println("list 0:" + list.get(0)[1]);
        try {
            if (list.size() > 0 && list != null) {
                personaCreaSolici = list.get(0)[0].toString();
                fechaCorreo = list.get(0)[1].toString();
                fechaInicial = list.get(0)[2].toString();
                fechaFin = list.get(0)[3].toString();
                dias = list.get(0)[4].toString();
                nombreAnexo = list.get(0)[5].toString();
                nombreAutorizaSolici = list.get(0)[8].toString();
                /*for (Object[] q1 : list) {
                String name = q1[0].toString() + " " + q1[1].toString() + " ";
                personaCreaSolici = q1[0].toString();
                personaCreaSolici = list.get(0)[0].toString();
                //fechaCorreo = q1[1].toString();
                fechaInicial = q1[3].toString();
                fechaFin = q1[4].toString();
                dias = q1[5].toString();
                nombreAutorizaSolici = q1[8].toString();
                System.out.println(name);
            }*/
            }
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".enviaCorreoNuevoAusentismo() " + e.getMessage());
        }
        /*for (Object[] q1 : list) {
            String name = q1[0].toString() + " " + q1[1].toString() + " ";
            personaCreaSolici = q1[0].toString();
            fechaCorreo = q1[1].toString();
            fechaInicial = q1[3].toString();
            fechaFin = q1[4].toString();
            dias = q1[5].toString();
            nombreAutorizaSolici = q1[8].toString();
            System.out.println(name);
        }*/
        String mensaje = "Nos permitimos informar que se acaba de reportar un ausentismo en el módulo de Kiosco Nómina Designer. Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                + " <br><br> "
                + "La persona que REPORTÓ LA NOVEDAD AUSENTISMO es: " + personaCreaSolici
                + "<br>"
                // + "La persona a cargo de DAR APROBACIÓN es: " + getApellidoNombreXsecEmpl(secEmplJefe, cadena)
                + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici
                + "<br>"
                + "La novedad se reportó por " + dias + " días, desde el " + fechaInicial + " hasta el " + fechaFin
                + "<br><br>Por favor seguir el proceso en: <a style='color: white !important;' href='" + urlKio + "'>" + urlKio + "</a>"
                + "<br><br>"
                + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita."
                + "<br><br>"
                //  + "En caso de que haya olvidado su clave, ingrese a la página de internet, y de clic en ¿Olvidó su clave? y siga los pasos.";
                + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                + "<br><a style='color: white !important;' href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a>";

        String correoUsuario = getCorreoConexioneskioskos(seudonimo, nitEmpresa, cadena);

        boolean enviado = true;
        asunto += " de " + personaCreaSolici + " " + fecha;
        try {
            EnvioCorreo e = new EnvioCorreo();
            //if (e.enviarCorreoInformativo("Módulo Kiosco: Reporte de corrección de información de "+nombreEmpl+" "+fecha,
            String servidorsmtp = getConfigCorreoServidorSMTP(nitEmpresa, cadena, esquema);
            String puerto = getConfigCorreo(nitEmpresa, "PUERTO", cadena, esquema);
            String autenticado = getConfigCorreo(nitEmpresa, "AUTENTICADO", cadena, esquema);
            String starttls = getConfigCorreo(nitEmpresa, "STARTTLS", cadena, esquema);
            String remitente = getConfigCorreo(nitEmpresa, "REMITENTE", cadena, esquema);
            String clave = getConfigCorreo(nitEmpresa, "CLAVE", cadena, esquema);
            String correoEmpleado = getCorreoXsecEmpl(secEmpl, nitEmpresa, cadena, esquema);
            if (e.enviarCorreoAusentismos(
                    servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                    correoEmpleado,
                    "Novedad de AUSENTISMO Kiosco - Nuevo reporte: " + fechaCorreo + ". Inicio de ausentismo: " + fechaInicial,
                    mensaje, nombreAnexo, urlKio, nitEmpresa, cadena)) {
                System.out.println("Correo enviado al empleado.");
            }

            // Enviar correo al jefe o autorizador de ausentismos:
            if (e.enviarCorreoAusentismos(
                    servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                    correoAutorizaSolici,
                    "Novedad de AUSENTISMO Kiosco - Nuevo reporte: " + fechaCorreo + ". Inicio de ausentismo: " + fechaInicial,
                    mensaje, nombreAnexo, urlKio, nitEmpresa, cadena)) {
                System.out.println("Correo enviado al jefe.");
            }

            try {
                System.out.println("Consulta si está activa la auditoria..");

                if (consultaAuditoria("AUSENTISMOS", "41", nitEmpresa, cadena).compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("Si debe llevar auditoria crearNovedad Ausentismo");
                    String sqlQuery = "select email from kioconfigmodulos where codigoopcion=? and nitempresa=?";
                    //Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                    System.out.println("Query2: " + sqlQuery);
                    Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                    query2.setParameter(1, "41");
                    query2.setParameter(2, nitEmpresa);
                    List lista = query2.getResultList();
                    Iterator<String> it = lista.iterator();
                    System.out.println("obtener " + lista.get(0));
                    System.out.println("size: " + lista.size());
                    /*String mensajeAuditoria = "Nos permitimos informar que " + personaCreaSolici
                                                + " generó la SOLICITUD DE VACACIONES el " + fechaCorreo + " a las " + horaGeneracion + " en el módulo de Kiosco Nómina Designer.";*/
                    String mensajeAuditoria = "Nos permitimos informar que " + personaCreaSolici
                            + " reportó una NOVEDAD DE AUSENTISMO el " + fechaCorreo + " en el módulo de Kiosco Nómina Designer."
                            + "<br>"
                            + // + "La persona a cargo de DAR APROBACIÓN es: " + getApellidoNombreXsecEmpl(secEmplJefe, cadena)
                            "La solicitud se creó por " + dias + " días, para ser disfrutados desde el " + fechaInicial + " hasta el " + fechaFin
                            + "<br>"
                            + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici + ".";
                    while (it.hasNext()) {
                        String correoenviar = it.next();
                        System.out.println("correo auditoria: " + correoenviar);
                        //c.pruebaEnvio2("smtp.gmail.com","587","pruebaskiosco534@gmail.com","Nomina01", "S", correoenviar,
                        System.out.println("codigoopcion: " + "41");
                        /*c.enviarCorreoVacaciones(servidorsmtp, puerto, autenticado, starttls, remitente, clave, correoenviar, 
                                            "Auditoria: Nueva Solicitud de vacaciones Kiosco. "+fechaCorreo, mensajeAuditoria, urlKio, nit);*/
                        e.enviarCorreoInformativo("Auditoria: Nueva novedad de AUSENTISMO Kiosco. " + fechaCorreo,
                                "Estimado usuario: ", mensajeAuditoria, nitEmpresa, urlKio, cadena, correoenviar, null);
                    }
                } else {
                    System.out.println("No lleva auditoria Ausentismos");
                }
            } catch (Exception ex) {
                System.out.println("Ha ocurrido un error al intentar consultar o enviar la auditoria " + ex.getMessage());
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            mensaje = "Ha ocurrido un error, por favor intentalo de nuevo más tarde.";
        }
        return enviado;
    }

    @GET
    @Path("/solicitudesXEmpleadoJefe")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudesXEmpleadoJefe(@QueryParam("usuario") String seudonimo,
            @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String secEmplJefe = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        System.out.println("Webservice: solicitudesXEmpleadoJefe Parametros: usuario: secEmplJefe: " + secEmplJefe + ", empresa: " + nitEmpresa);
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT \n"
                    + "t1.CODIGOEMPLEADO, "
                    + "P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE NOMBRECOMPLETO, "
                    + "to_char(t2.FECHAGENERACION, 'DD/MM/YYYY HH:mm:ss') SOLICITUD, "
                    + "to_char(T0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:mm:ss') FECHAPROCESAMIENTO, "
                    + "t0.SECUENCIA, NVL(t0.MOTIVOPROCESA, 'N/A'), "
                    + "to_char(T2.FECHAINICIO, 'DD/MM/YYYY') FECHAINICIOAUSENTISMO, "
                    + "to_char(T2.FECHAFIN, 'DD/MM/YYYY') FECHAFINAUSENTISMO, "
                    + "t2.dias, "
                    + "(select "
                    + "pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre "
                    + "from personas pei, empleados ei "
                    + "where pei.secuencia=ei.persona and t2.EMPLEADOJEFE=ei.secuencia) empleadojefe, "
                    + "t0.ESTADO ESTADO,"
                    + "(SELECT DESCRIPCION FROM CAUSASAUSENTISMOS WHERE SECUENCIA=T2.CAUSAREPORTADA) CAUSA,"
                    + "(SELECT T.DESCRIPCION FROM CAUSASAUSENTISMOS C, TIPOSAUSENTISMOS T, CLASESAUSENTISMOS CL "
                    + "WHERE C.SECUENCIA=T2.CAUSAREPORTADA AND CL.TIPO=T.SECUENCIA AND C.CLASE=CL.SECUENCIA) TIPO,"
                    + "(SELECT CL.DESCRIPCION FROM CAUSASAUSENTISMOS C, TIPOSAUSENTISMOS T, CLASESAUSENTISMOS CL "
                    + "WHERE C.SECUENCIA=T2.CAUSAREPORTADA AND CL.TIPO=T.SECUENCIA AND C.CLASE=CL.SECUENCIA) CLASE,"
                    + "(SELECT CODIGO FROM DIAGNOSTICOSCATEGORIAS WHERE KN.DIAGNOSTICOCATEGORIA=SECUENCIA) CODDIAGNOSTICO, \n"
                    + "(SELECT DESCRIPCION FROM DIAGNOSTICOSCATEGORIAS WHERE KN.DIAGNOSTICOCATEGORIA=SECUENCIA) NOMDIAGNOSTICO,"
                    + "T2.OBSERVACION, "
                    + "T2.NOMBREANEXO ANEXO, "
                    + "(SELECT DECODE(KIONOVEDADPRORROGA, NULL, 'NO', 'SI') FROM KIONOVEDADESSOLICIAUSENT "
                    + "WHERE KIOSOLICIAUSENTISMO=T2.SECUENCIA "
                    + "AND T2.FECHAINICIO=FECHAINICIALAUSENTISMO) PRORROGA "
                    + "FROM KIOESTADOSSOLICIAUSENT t0, KIOSOLICIAUSENTISMOS t2, EMPLEADOS t1, PERSONAS P, "
                    + "kionovedadessoliciausent kn "
                    + "WHERE (((( "
                    + "(t1.EMPRESA = (select secuencia from empresas where nit=?)) \n"
                    + "AND (t0.ESTADO IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO')))   "
                    + "AND (t2.EMPLEADOJEFE =?) "
                    + ")  "
                    + "AND (t0.FECHAPROCESAMIENTO = (SELECT MAX(t3.FECHAPROCESAMIENTO) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                    + "WHERE ((t4.SECUENCIA = t2.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO))))) "
                    + "AND ((t2.SECUENCIA = t0.KIOSOLICIAUSENTISMO) AND (t1.SECUENCIA = t2.EMPLEADO)) "
                    + "AND t1.PERSONA=P.SECUENCIA "
                    + "and t2.secuencia = kn.kiosoliciausentismo "
                    + ") "
                    + "AND KN.FECHAINICIALAUSENTISMO = (select MIN(ei.FECHAINICIALAUSENTISMO) "
                    + "    from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi "
                    + "    where ei.KIOSOLICIAUSENTISMO = ksi.secuencia "
                    + "    and ksi.secuencia=t2.secuencia) "
                    + "ORDER BY t0.FECHAPROCESAMIENTO DESC";
            //Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secEmplJefe);
            s = query.getResultList();
            s.forEach(System.out::println);

            if (s.size() > 0) {
                for (int i = 0; i < s.size(); i++) {
                    JsonObject json = Json.createObjectBuilder()
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
    @Path("/obtenerAnexo/")
    @Produces({"application/pdf"})
    public Response obtenerAnexo(@QueryParam("anexo") String anexo, @QueryParam("cadena") String cadena, @QueryParam("empresa") String nitEmpresa) {
        System.out.println("Parametros obtenerAnexo(): anexo: " + anexo + ", cadena: " + cadena + ", nitEmpresa: " + nitEmpresa);
        FileInputStream fis = null;
        File file = null;
        String RUTAFOTO = getPathReportes(nitEmpresa, cadena) + "\\anexosAusentismos\\";
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

    /*Retorna un objeto con los detalles del ausentismo, recibe el empleado y la secuencia de la solicitud*/
    public List<Object[]> getObjectDetalleAusentismo(String secKioSoliciAusentismo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getObjectDetalleAusentismo(): secKioSoliciAusentismo: " + secKioSoliciAusentismo + ", empresa: " + nitEmpresa + ", cadena: " + cadena);
        List<Object[]> list = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "select "
                    + "(SELECT P.NOMBRE||' '|| P.PRIMERAPELLIDO || ' ' || P.SEGUNDOAPELLIDO FROM PERSONAS P, "
                    + "EMPLEADOS E WHERE P.SECUENCIA=E.PERSONA "
                    + "AND E.SECUENCIA=EMPLEADO) NOMBREEMPLEADO, "
                    + "TO_CHAR(FECHAGENERACION, 'DD/MM/YYYY') FECHAGENERACION, "
                    + "TO_CHAR(FECHAINICIO, 'DD/MM/YYYY') FECHAINICIO, "
                    + "TO_CHAR(FECHAFIN, 'DD/MM/YYYY') FECHAFIN, "
                    + "DIAS, "
                    + "NOMBREANEXO, "
                    + "OBSERVACION, "
                    + "CAUSAREPORTADA, "
                    + "(SELECT P.NOMBRE||' '|| P.PRIMERAPELLIDO || ' ' || P.SEGUNDOAPELLIDO FROM PERSONAS P, EMPLEADOS E WHERE E.PERSONA=P.SECUENCIA "
                    + "AND EMPLEADOJEFE = E.SECUENCIA) NOMBREJEFE "
                    + "FROM KIOSOLICIAUSENTISMOS WHERE SECUENCIA=? ";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secKioSoliciAusentismo);
            list = query.getResultList();

            /*for (Object[] q1 : list) {

                String name = q1[0].toString()+" "+q1[1].toString()+" ";
                System.out.println(name);
                //..
                //do something more on 
            }*/
            //query.setParameter(1, secKioSoliciAusentismo);
            //objeto = query.getResultList();
            System.out.println("lista: " + list.toString());
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getObjectDetalleAusentismo(): " + e.getMessage());
        }
        return list;
    }

    private BigDecimal consultaAuditoria(String nombreModulo, String codigoOpc, String nitEmpresa, String cadena) {
        BigDecimal retorno;
        String query1 = "SELECT COUNT(*) FROM KIOCONFIGMODULOS WHERE NOMBREMODULO=? AND CODIGOOPCION=? AND NITEMPRESA=?";
        // Query query = getEntityManager(cadena).createNativeQuery(query1);
        System.out.println("Query: " + query1);
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        Query query = getEntityManager(cadena).createNativeQuery(query1);
        query.setParameter(1, nombreModulo);
        query.setParameter(2, codigoOpc);
        query.setParameter(3, nitEmpresa);
        retorno = (BigDecimal) query.getSingleResult();
        System.out.println("consultaAuditoria(): " + retorno);
        System.out.println("consultaAuditoria retorno: " + retorno);
        /*if (retorno.compareTo(BigDecimal.ZERO) > 0) {
                
            } */
        return retorno;
    }

    private boolean updateAnexoKioSoliciAusentismo(
            String seudonimo, String nitEmpresa, String nombreAnexo, String secKioSoliciAusentismo, String cadena) {
        System.out.println("Parametros updateAnexoKioSoliciAusentismo(): seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", nombreAnexo: " + nombreAnexo + ", secKioSolicAusentismo: " + secKioSoliciAusentismo + ", cadena: " + cadena);
        boolean resultado = false;
        int conteo = 0;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE KIOSOLICIAUSENTISMOS SET NOMBREANEXO=? WHERE SECUENCIA=?";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nombreAnexo);
            query.setParameter(2, secKioSoliciAusentismo);
            conteo = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".updateAnexoKioSoliciAusentismo() " + e.getMessage());
        }
        return conteo > 0;
    }

    public String getEmplJefeXsecKioSoliciAusentismo(String secKioSoliciAusentismo, String nitEmpresa, String cadena) {
        String secEmplJefe = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSA.EMPLEADOJEFE "
                    + "FROM "
                    + "KIOSOLICIAUSENTISMOS KSA "
                    + "WHERE "
                    + "KSA.SECUENCIA = ?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secKioSoliciAusentismo);
            secEmplJefe = query.getSingleResult().toString();
            System.out.println("Empl jefe asociado: " + secEmplJefe);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getEmplJefeXsecKioSoliciAusentismo(): " + e.getMessage());
        }
        return secEmplJefe;
    }

    public String getCorreoConexioneskioskos(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros " + this.getClass().getName() + ".getCorreoConexioneskioskos(): seudonimo: " + seudonimo + ", empresa: " + nitEmpresa + ", cadena: " + cadena);
        String correo = null;
        String sqlQuery;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT P.EMAIL FROM PERSONAS P, conexioneskioskos ck WHERE p.secuencia=ck.persona and "
                    + " ck.seudonimo=? and ck.nitempresa=?";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getCorreoConexioneskioskos(): " + e.getMessage());
        }
        return correo;
    }

    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secuencia = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
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

    public String getConfigCorreo(String nitEmpresa, String valor, String cadena, String esquema) {
        System.out.println("getPathArchivosPlanos()");
        String servidorsmtp = "smtp.designer.com.co";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT " + valor + " FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp = query.getSingleResult().toString();
            System.out.println(valor + ": " + servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreo(): " + e.getMessage());
        }
        return servidorsmtp;
    }

    public String getConfigCorreoServidorSMTP(String nitEmpresa, String cadena, String esquema) {
        System.out.println("getConfigCorreoServidorSMTP(): nit: " + cadena + ", cadena: " + cadena);
        String servidorsmtp = "smtp.designer.com.co";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT SERVIDORSMTP FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp = query.getSingleResult().toString();
            System.out.println("Servidor smtp: " + servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreoServidorSMTP: " + e.getMessage());
        }
        return servidorsmtp;
    }

    public String getPathReportes(String nitEmpresa, String cadena) {
        System.out.println("Parametros getPathReportes(): cadena: " + cadena);
        String rutaFoto = "";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHREPORTES FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaFotos: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getPathReportes(): " + e.getMessage());
        }
        return rutaFoto;
    }

    public String getEsquema(String nitempresa, String cadena) {
        System.out.println("Parametros getEsquema(): nitempresa: " + nitempresa + ", cadena: " + cadena);
        String esquema = null;
        String sqlQuery;
        try {
            sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=?";
            Query query = getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitempresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("Esquema: " + esquema);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getEsquema(): " + e);
        }
        return esquema;
    }

    // 20220201 consultar tipo y clase de dia de la familia
    public String getSecTipoCausa(String codigoCausa, String cadena, String esquema) {
        System.out.println("Parametros getSecTipoCausa(): codigoCausa: " + codigoCausa);
        String secClase = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            /*
            2 - PERMISOS
            6 - LICENSIA 
            */
            sqlQuery = "SELECT SECUENCIA FROM TIPOSAUSENTISMOS WHERE CODIGO=? ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, codigoCausa);
            secClase = query.getSingleResult().toString();
            System.out.println("secClase: " + secClase);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getCausaPorcentajeLiq(): " + e);
            secClase = "";
        }
        return secClase;
    }
}
