package co.com.designer.services;

import co.com.designer.kiosko.administracion.implementacion.GestionArchivos;
import co.com.designer.kiosko.administracion.interfaz.IGestionArchivos;
import co.com.designer.kiosko.entidades.ConfiCorreoKiosko;
import co.com.designer.kiosko.entidades.IntervinientesSolAusent;
import co.com.designer.kiosko.generales.EnvioCorreo;
import co.com.designer.persistencia.implementacion.PersistenciaCausasAusentismos;
import co.com.designer.persistencia.implementacion.PersistenciaConexionesKioskos;
import co.com.designer.persistencia.implementacion.PersistenciaConfiCorreoKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaDiagnosticosCategorias;
import co.com.designer.persistencia.implementacion.PersistenciaEmpleados;
import co.com.designer.persistencia.implementacion.PersistenciaGeneralesKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaKioAusentismo_pkg;
import co.com.designer.persistencia.implementacion.PersistenciaKioAutorizaSoliciVacas;
import co.com.designer.persistencia.implementacion.PersistenciaKioCausasAusentismos;
import co.com.designer.persistencia.implementacion.PersistenciaKioSoliciAusentismos;
import co.com.designer.persistencia.implementacion.PersistenciaPersonas;
import co.com.designer.persistencia.implementacion.PersistenciaKioConfigModulos;
import co.com.designer.persistencia.implementacion.PersistenciaManejoFechas;
import co.com.designer.persistencia.implementacion.PersistenciaTiposAusentismos;
import co.com.designer.persistencia.interfaz.IPersistenciaCausasAusentismos;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaConfiCorreoKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaDiagnosticosCategorias;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpleados;
import co.com.designer.persistencia.interfaz.IPersistenciaGeneralesKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaKioAusentismo_pkg;
import co.com.designer.persistencia.interfaz.IPersistenciaKioAutorizaSoliciVacas;
import co.com.designer.persistencia.interfaz.IPersistenciaKioCausasAusentismos;
import co.com.designer.persistencia.interfaz.IPersistenciaKioConfigModulos;
import co.com.designer.persistencia.interfaz.IPersistenciaKioSoliciAusentismos;
import co.com.designer.persistencia.interfaz.IPersistenciaManejoFechas;
import co.com.designer.persistencia.interfaz.IPersistenciaPersonas;
import co.com.designer.persistencia.interfaz.IPersistenciaTiposAusentismos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Mateo Coronado
 * @author Edwin Hastamorir
 */
@Stateless
@Path("ausentismos")
public class kioCausasAusentismosFacadeREST {

    private IPersistenciaKioCausasAusentismos persisKioCausasAus;
    private IPersistenciaDiagnosticosCategorias persisDiagnosCat;
    private IPersistenciaKioSoliciAusentismos persisKioSoliciAusent;
    private IPersistenciaConexionesKioskos persisConKiosko;
    private IPersistenciaEmpleados persisEmpleados;
    private IPersistenciaPersonas persisPersonas;
    private IPersistenciaKioAutorizaSoliciVacas persisAutorizaSoli;
    private IPersistenciaGeneralesKiosko persisGeneralesKio;
    private IPersistenciaKioConfigModulos persisKioConfigMod;
    private IPersistenciaConfiCorreoKiosko persisConfiCorreoKio;
    private IPersistenciaKioAusentismo_pkg persisKioAusent_pkg;
    private IPersistenciaTiposAusentismos persisTiposAusent;
    private IPersistenciaCausasAusentismos persisCausasAusent;
    private IPersistenciaManejoFechas persisManejoFechas;
    private IGestionArchivos gestionArchivos;

    public kioCausasAusentismosFacadeREST() {
    }

    @GET
    @Path("/token")
    public String authenticate(@HeaderParam("authorization") String token) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".authenticate(): Parametros: "
                + "token: " + token);
        return token;
    }

    @GET
    @Path("/causas")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List findAlls(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        this.persisKioCausasAus = new PersistenciaKioCausasAusentismos();
        List lista = this.persisKioCausasAus.getKioCausasAusentismos(nitEmpresa, cadena);
        return lista;
    }

    @GET
    @Path("/codigosDiagnosticos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List getCodigosDiagnosticos(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        this.persisDiagnosCat = new PersistenciaDiagnosticosCategorias();
        List lista = this.persisDiagnosCat.getDiagnosticosCategorias(nitEmpresa, cadena);
        return lista;
    }

    @GET
    @Path("/codigosDiagnosticos2")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List getCodigosDiagnosticos2(@QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        this.persisDiagnosCat = new PersistenciaDiagnosticosCategorias();
        List lista = this.persisDiagnosCat.getDiagnosticosCategoriasNativo(nitEmpresa, cadena);
        return lista;
    }

    @GET
    @Path("/consultaNombreAutorizaAusentismos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response consultarAutorizaAusentismos(
            @QueryParam("usuario") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {

        String retorno = "";
        String mensaje = "";
        String secEmplJefe = null;
        String secPerKioAutorizador = null;

        this.persisAutorizaSoli = new PersistenciaKioAutorizaSoliciVacas();
        this.persisPersonas = new PersistenciaPersonas();
        this.persisEmpleados = new PersistenciaEmpleados();
        this.persisConKiosko = new PersistenciaConexionesKioskos();

        try {
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            if (secEmpleado != null) {
                secPerKioAutorizador = this.persisAutorizaSoli.consultarSecuenciaPorAutorizador(secEmpleado, nitEmpresa, cadena, "", 5);
                retorno = secPerKioAutorizador;
                if (secPerKioAutorizador != null) {
                    // Existe relación con kioautorizadores
                    retorno = this.persisPersonas.getApellidoNombreXSecPer(secPerKioAutorizador, nitEmpresa, cadena, "");
                    mensaje = "Consulta del autorizador de ausentismos exitosa";
                } else {
                    mensaje = "No hay un autorizador";
                    try {
                        secEmplJefe = this.persisEmpleados.consultarSecuenciaEmpleadoJefe(secEmpleado, nitEmpresa, cadena);
                        if (secEmplJefe != null) {
                            retorno = this.persisPersonas.getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, "");
                            System.out.println("Empleado jefe: " + retorno);
                            mensaje = "Consulta del jefe exitosa";
                        } else {
//                            mensaje = "No hay un autorizador/jefe relacionado";
                            mensaje += "jefe relacionado";
                            System.out.println("kioCausasAusentismosFacadeREST" + ".consultarAutorizaAusentismos(): " + "Error-2: " + "El empleado jefe no está registrado.");
                            throw new Exception("El empleado jefe no está registrado.");
                        }
                    } catch (Exception e) {
                        System.out.println("kioCausasAusentismosFacadeREST" + ".consultarAutorizaAusentismos(): " + "Error-3: " + e.toString());
                        return Response.status(Response.Status.OK).entity(mensaje).build();
                    }
                }
            } else {
                mensaje = "El empleado no existe";
            }
        } catch (Exception e) {
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
        System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Parametros: "
                + "{ seudonimo: " + seudonimo
                + ", nitempresa: " + nit
                + ", fechainicio: " + fechainicial
                + ", fechaFin: " + fechaFin
                + ", dias: " + dias
                + "\n, causa: " + secCausaAusent
                + ", diagnostico: " + secCodDiagnostico
                + ", clase: " + secClaseAusent
                + ", tipo: " + secTipoAusent
                + ", prorroga: " + secKioNovedadAusent
                + "\n, observacion: " + observacion
                + ", cadena: " + cadena
                + ", grupo: " + grupoEmpr
                + ", codigoCausa: " + codigoCausa);
        boolean soliciCreada = false;
        String esquema = null;
        // nombre con el que debe guardarse el campo del documento anexo
        String nombreAnexo = null;
        String secKioSoliciAusent = null;
        this.persisTiposAusent = new PersistenciaTiposAusentismos();
        this.persisKioAusent_pkg = new PersistenciaKioAusentismo_pkg();
        this.persisCausasAusent = new PersistenciaCausasAusentismos();
        this.persisManejoFechas = new PersistenciaManejoFechas();
        this.persisPersonas = new PersistenciaPersonas();
        this.persisEmpleados = new PersistenciaEmpleados();
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisAutorizaSoli = new PersistenciaKioAutorizaSoliciVacas();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();
        String secPerKioAutorizador = null;
        Date fecha = new Date();
        String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(fecha);
        String nombreAutorizaSolici = ""; // Nombre Jefe
        String correoAutorizaSolici = null; // Correo Jefe
        String mensaje = "";
        String secEmplJefe = null;
        try {
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena);
            int diasIncapacidad = Integer.parseInt(dias);
            if ("S".equals(anexoAdjunto)) {
                nombreAnexo = "anexo_" + secEmpl + "_" + fechaGeneracion.replaceAll(" ", "");
            }
            String formaLiq = this.persisKioAusent_pkg.getCausaFormaLiquidacion(secCausaAusent, nit, cadena, "");
            String porcentajeLiq = this.persisKioAusent_pkg.getCausaPorcentajeLiquidacion(secCausaAusent, nit, cadena, "");
            String causaOrigen = (String) this.persisCausasAusent.getCausaOrigenIncapacidad(secCausaAusent, nit, cadena);

            secPerKioAutorizador = this.persisAutorizaSoli.consultarSecuenciaPorAutorizador(secEmpl, nit, cadena, "", 5);
            if (secPerKioAutorizador != null) {
                // Existe relación con kioautorizadores
                nombreAutorizaSolici = this.persisPersonas.getApellidoNombreXSecPer(secPerKioAutorizador, nit, cadena, "");
                correoAutorizaSolici = this.persisPersonas.getCorreoPorPersona(secPerKioAutorizador, nit, cadena);
            } else {
                secEmplJefe = this.persisEmpleados.consultarSecuenciaEmpleadoJefe(secEmpl, nit, cadena);

                if (secEmplJefe != null) {
                    System.out.println("creaKioSoliciAusent: EmpleadoJefe: " + secEmplJefe);
                    nombreAutorizaSolici += this.persisPersonas.getApellidoNombreXsecEmpl(secEmplJefe, nit, cadena, esquema);
                    correoAutorizaSolici = this.persisPersonas.getCorreoPorEmpleado(secEmplJefe, nit, cadena);
                    System.out.println("El empleado tiene relacionado a empleadoJefe " + nombreAutorizaSolici + " - " + correoAutorizaSolici);

                } else {
                    System.out.println("El empleado jefe está vacío");
                    // Si no hay una persona asignada para autorizar las vacaciones no crear la solicitud
//                    soliciCreada = false;
                    mensaje = "No tiene un autorizador de ausentismos relacionado, por favor comuníquese con el área de nómina y recursos humanos de su empresa.";
                }
            }
            // Registro en tabla KIOSOLICIAUSENTISMOS
            if (this.persisKioSoliciAusent.creaSolicitudAusentismo(seudonimo, secEmplJefe, secPerKioAutorizador, nit, null, fechaGeneracion, fechainicial, fechaFin, dias, observacion, secCausaAusent, cadena, "") > 0) {
                secKioSoliciAusent = this.persisKioSoliciAusent.getSecuenciaSolicitudAusentismo(secEmpl, fechaGeneracion, secEmplJefe, secPerKioAutorizador, nit, cadena, "");
                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "secKioSoliciAusent: " + secKioSoliciAusent);
                mensaje = "Novedad de ausentismo reportada exitosamente.";
                if (secKioNovedadAusent.equals("null")) {
                    System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "No tiene prorroga ");
                    if (causaOrigen.equals("EG")) {
                        System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Causa enfermedad general ");
                        String secCausaEGPrimeros2Dias = this.persisCausasAusent.getSecuenciaCausaAusentismo("25", nit, cadena);
                        String formaLiqEGPrimeros2Dias = this.persisKioAusent_pkg.getCausaFormaLiquidacion(secCausaEGPrimeros2Dias, nit, cadena, "");
                        String porcentajeLiqEGPrimeros2Dias = this.persisKioAusent_pkg.getCausaPorcentajeLiquidacion(secCausaEGPrimeros2Dias, nit, cadena, "");
                        if (diasIncapacidad <= 2) {
                            // Si los días reportados son 2 o menos se deben registrar en una sola novedad
                            System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Los dias reportados son 2 o menos.");
                            String fechaFin1 = (String) this.persisKioAusent_pkg.getFechaFinAusent(fechainicial, dias, seudonimo, secCausaEGPrimeros2Dias, nit, cadena, "");
                            System.out.println("Fecha novedad 1: " + fechaFin);
                            if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaEGPrimeros2Dias, secCodDiagnostico, Integer.parseInt(dias), fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqEGPrimeros2Dias, porcentajeLiqEGPrimeros2Dias, cadena, esquema) > 0) {
                                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Registrada novedad 1 por 2 dias o menos.");
                                mensaje = "Novedad de ausentismo reportada exitosamente.";

                                // Registro en tabla KIOESTADOSSOLICIAUSENT
                                if (this.persisKioSoliciAusent.creaEstadoSolicitudAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema) > 0) {
                                    soliciCreada = true;
                                    System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Estado de novedad de ausentismo creado.");
                                } else {
                                    mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                }
                            }
                        } else {
                            // Si los dias reportados son más de 2 se deben registrar en dos novedades
                            // Registro novedad primeros 2 dias
                            System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Los días reportados son más de 2.");
                            String fechaFin1 = (String) this.persisKioAusent_pkg.getFechaFinAusent(fechainicial, "2", seudonimo, secCausaEGPrimeros2Dias, nit, cadena, "");
                            if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaEGPrimeros2Dias, secCodDiagnostico, 2, fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqEGPrimeros2Dias, porcentajeLiqEGPrimeros2Dias, cadena, esquema) > 0) {
                                // Registro segunda novedad por los días faltantes
                                String fechainicialEG2 = this.persisManejoFechas.getFechaSugerida(fechaFin1, "1", cadena, esquema); // fecha inicial 2 es fecha fin +1
                                int diasNov2 = Integer.parseInt(dias) - 2;
                                String fechaFin2 = (String) this.persisKioAusent_pkg.getFechaFinAusent(fechainicialEG2, String.valueOf(diasNov2), seudonimo, secCausaAusent, nit, cadena, "");
                                if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicialEG2, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad - 2, fechaFin2, secKioSoliciAusent, "null", formaLiq, porcentajeLiq, cadena, esquema) > 0) {
                                    mensaje = "Novedad de ausentismo reportada exitosamente.";

                                    // Registro en tabla KIOESTADOSSOLICIAUSENT
                                    if (this.persisKioSoliciAusent.creaEstadoSolicitudAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema) > 0) {
                                        soliciCreada = true;
                                        System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Estado de novedad de ausentismo creado.");
//                                            this.persisConexiones.getEntityManager(cadena).close();
                                    } else {
                                        mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                    }
                                }
                            } else {
                                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Ha ocurrido un error al momento de crear el registro de la primera novedad.");
                                mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                            }
                        }
                    } else if (causaOrigen.equals("AT")) {
                        // si la causa es accidente de trabajo 
                        System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Causa accidente de trabajo.");
                        String secCausaATPrimerDia = this.persisCausasAusent.getSecuenciaCausaAusentismo("39", nit, cadena);
                        String formaLiqATPrimerDia = this.persisKioAusent_pkg.getCausaFormaLiquidacion(secCausaATPrimerDia, nit, cadena, "");
                        String porcentajeLiqATPrimerDia = this.persisKioAusent_pkg.getCausaPorcentajeLiquidacion(secCausaATPrimerDia, nit, cadena, "");
                        if (diasIncapacidad <= 1) {
                            // Si los días reportados son 2 o menos se deben registrar en una sola novedad
                            System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Los dias reportados es 1 dia.");
                            String fechaFin1 = (String) this.persisKioAusent_pkg.getFechaFinAusent(fechainicial, dias, seudonimo, secCausaATPrimerDia, nit, cadena, "");
                            System.out.println("Fecha novedad 1: " + fechaFin);
                            if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaATPrimerDia, secCodDiagnostico, Integer.parseInt(dias), fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqATPrimerDia, porcentajeLiqATPrimerDia, cadena, esquema) > 0) {
                                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "registrada novedad 1 dia.");
                                mensaje = "Novedad de ausentismo reportada exitosamente.";

                                // Registro en tabla KIOESTADOSSOLICIAUSENT
                                if (this.persisKioSoliciAusent.creaEstadoSolicitudAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema) > 0) {
                                    soliciCreada = true;
                                    System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Estado de novedad de ausentismo creado.");
                                } else {
                                    mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                }
                            }
                        } else {
                            // Si los dias reportados son más de 1 se deben registrar en dos novedades
                            // Registro novedad primeros 1 dia1
                            System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Los días reportados son más de 1.");
                            String fechaFin1 = (String) this.persisKioAusent_pkg.getFechaFinAusent(fechainicial, "1", seudonimo, secCausaATPrimerDia, nit, cadena, "");
                            if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaATPrimerDia, secCodDiagnostico, 1, fechaFin1, secKioSoliciAusent, secKioNovedadAusent, formaLiqATPrimerDia, porcentajeLiqATPrimerDia, cadena, esquema) > 0) {
                                // Registro segunda novedad por los días faltantes
                                String fechainicialEG2 = this.persisManejoFechas.getFechaSugerida(fechaFin1, "1", nit, cadena); // fecha inicial 2 es fecha fin +1
                                int diasNov2 = Integer.parseInt(dias) - 1;
                                String fechaFin2 = (String) this.persisKioAusent_pkg.getFechaFinAusent(fechainicialEG2, String.valueOf(diasNov2), seudonimo, secCausaAusent, nit, cadena, "");
                                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "fechaFin2: " + fechaFin2);
                                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "se crea novedad por un dia.");
                                if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicialEG2, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad - 1, fechaFin2, secKioSoliciAusent, "null", formaLiq, porcentajeLiq, cadena, esquema) > 0) {
                                    System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "se crea novedad por dos dias.");
                                    mensaje = "Novedad de ausentismo reportada exitosamente.";
                                    // Registro en tabla KIOESTADOSSOLICIAUSENT
                                    if (this.persisKioSoliciAusent.creaEstadoSolicitudAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema) > 0) {
                                        soliciCreada = true;
                                        System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Estado de novedad de ausentismo creado.");
                                    } else {
                                        mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                    }
                                }
                            } else {
                                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Ha ocurrido un error al momento de crear el registro de la primera novedad.");
                                mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                            }
                        }
                    } else {
                        if (codigoCausa.equals("54")) {
                            // si es dia de la familia 
                            String tipoCausaFamilia = this.persisTiposAusent.getSecuenciaTipoAusentismo("2", nit, cadena, "");
                            System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "es solicitud de causa de dia familia.");
                            if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicial, tipoCausaFamilia, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad, fechafin, secKioSoliciAusent, secKioNovedadAusent, formaLiq, porcentajeLiq, cadena, esquema) > 0) {
                                mensaje = "Novedad de ausentismo reportada exitosamente.";
                                // Registro en tabla KIOESTADOSSOLICIAUSENT
                                if (this.persisKioSoliciAusent.creaEstadoSolicitudAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema) > 0) {
                                    soliciCreada = true;
                                    System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Estado de novedad de ausentismo creado.");
                                } else {
                                    mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                }
                            } else {
                                System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Ha ocurrido un error al momento de crear el registrar la novedad.");
                                mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                            }
                        } else {
                            System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Causa diferente a ENFERMEDAD GENERAL.");
                            if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent,
                                    secCausaAusent, secCodDiagnostico, diasIncapacidad, fechafin, secKioSoliciAusent,
                                    secKioNovedadAusent, formaLiq, porcentajeLiq, cadena, esquema) > 0) {
                                mensaje = "Novedad de ausentismo reportada exitosamente.";
                                // Registro en tabla KIOESTADOSSOLICIAUSENT
                                if (this.persisKioSoliciAusent.creaEstadoSolicitudAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion,
                                        "ENVIADO", null, cadena, esquema) > 0) {
                                    soliciCreada = true;
                                    System.out.println("Estado de novedad de ausentismo creado.");
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
                    System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Tiene prorroga ");
                    if (this.persisKioSoliciAusent.creaNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, secCodDiagnostico, diasIncapacidad, fechafin, secKioSoliciAusent, secKioNovedadAusent, formaLiq, porcentajeLiq, cadena, esquema) > 0) {
                        mensaje = "Novedad de ausentismo reportada exitosamente.";
                        // Registro en tabla KIOESTADOSSOLICIAUSENT
                        if (this.persisKioSoliciAusent.creaEstadoSolicitudAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema) > 0) {
                            soliciCreada = true;
                            System.out.println("Estado de novedad de ausentismo creado.");
//                                this.persisConexiones.getEntityManager(cadena).close();
                        } else {
                            mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                        }
                    } else {
                        System.out.println("Ha ocurrido un error al momento de crear el registrar la novedad");
                        mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                    }
                }
            } else {
                System.out.println("Ha ocurrido un error al momento de crear el registro 1 de la solicitud");
                mensaje = "Ha ocurrido un error y no fue posible crear la novedad, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
            }

        } catch (Exception e) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".crearNovedadAusentismo(): " + "Error: " + e.toString());
            e.printStackTrace();
        }

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

    @GET
    @Path("/fechaFinAusentismo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFecha(@HeaderParam("authorization") String token,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena, @QueryParam("fechainicio") String fechaInicio,
            @QueryParam("dias") String dias, @QueryParam("usuario") String seudonimo,
            @QueryParam("causa") String causa) {
        this.persisKioAusent_pkg = new PersistenciaKioAusentismo_pkg();
        String fechaSugerida = this.persisKioAusent_pkg.getFechaFinAusent(fechaInicio, dias, seudonimo, causa, nitEmpresa, cadena, "");
        String msj = this.persisKioAusent_pkg.getMensajeValidacionAusent(fechaInicio, seudonimo, nitEmpresa, cadena, "");
        String formaliq = this.persisKioAusent_pkg.getCausaFormaLiquidacion(causa, nitEmpresa, cadena, "");
        String porcentajeliq = this.persisKioAusent_pkg.getCausaPorcentajeLiquidacion(causa, nitEmpresa, cadena, "");

        JsonObject json = Json.createObjectBuilder()
                .add("fechafin", fechaSugerida)
                .add("msj", msj)
                .add("formaliq", formaliq)
                .add("porcentajeliq", porcentajeliq)
                .build();

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
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();

        String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(empleado, nitEmpresa, cadena);
        List prorroga = this.persisKioSoliciAusent.getProrroga(secEmpleado, causa, fechaInicio, nitEmpresa, cadena, "");
        return prorroga;
    }

    @GET
    @Path("/solicitudXEstado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudXEstado(@QueryParam("documento") String documento,
            @QueryParam("empresa") String nitEmpresa,
            @QueryParam("estado") String estado,
            @QueryParam("cadena") String cadena) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".getSolicitudXEstado(): " + "Parametros: "
                + "seudonimo: " + documento
                + ", empresa: " + nitEmpresa
                + ", estado: " + estado
                + ", cadena: " + cadena);
        List s = null;

        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();

        try {
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(documento, nitEmpresa, cadena);

            s = this.persisKioSoliciAusent.getSolicitudesPorEstado(nitEmpresa, cadena, secEmpl, estado);
            System.out.println("kioCausasAusentismosFacadeREST.getSolicitudXEstado(): Lista de solicitudes por estado ");
            s.forEach(System.out::println);

            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".getSolicitudXEstado(): " + "Error-1: " + ex.toString());
            return Response.status(Response.Status.OK).entity("").build();
        }
    }

    @GET
    @Path("/soliciSinProcesarJefe")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSoliciSinProcesarJefe(@QueryParam("nit") String nitEmpresa, @QueryParam("jefe") String jefe,
            @QueryParam("estado") String estado, @QueryParam("cadena") String cadena) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".getSoliciSinProcesarJefe(): " + "Parametros: "
                + "empresa: " + nitEmpresa
                + ", jefe: " + jefe
                + ", estado: " + estado
                + ", cadena: " + cadena
        );
        List s = null;
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();
        try {
            String secuenciaJefe = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(jefe, nitEmpresa, cadena);
            this.persisKioSoliciAusent.getSolicitudesSinProcesarPorJefe(nitEmpresa, cadena, estado, secuenciaJefe);
            System.out.println("kioCausasAusentismosFacadeREST" + ".getSoliciSinProcesarJefe(): " + "Lista de solicitudes sin procesar por jefe ");

            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".getSoliciSinProcesarJefe(): " + "Error-1: " + ex.toString());
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    @GET
    @Path("/soliciSinProcesarAutorizador")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSoliciSinProcesarAutorizador(
            @QueryParam("nit") String nitEmpresa, @QueryParam("jefe") String jefe,
            @QueryParam("estado") String estado, @QueryParam("cadena") String cadena) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".getSoliciSinProcesarAutorizador(): " + "Parametros: "
                + "empresa: " + nitEmpresa
                + ", autorizador: " + jefe
                + ", estado: " + estado
                + ", cadena: " + cadena
        );
        List s = null;
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();
        try {
            BigDecimal secuenciaAutorizador = this.persisConKiosko.getPersonaPorSeudonimo(jefe, nitEmpresa, cadena);
            s = this.persisKioSoliciAusent.getSolicitudesSinProcesarPorAutorizador(nitEmpresa, cadena, estado, secuenciaAutorizador.toString());
            System.out.println("kioCausasAusentismosFacadeREST" + ".getSoliciSinProcesarAutorizador(): " + "Lista de solicitudes sin procesar por autorizador.");

            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".getSoliciSinProcesarAutorizador(): " + "Error-1: " + ex.toString());
            return Response.status(Response.Status.NOT_FOUND).entity("Error" + ex.toString()).build();
        }
    }

    @GET
    @Path("/validaFechaNovedadEmpleadoXJefe")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getvalidaFechaNovedadEmpleadoXJefe(@QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena, @QueryParam("fechainicio") String fechaInicio,
            @QueryParam("usuario") String seudonimo) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".getvalidaFechaNovedadEmpleadoXJefe(): " + "Parametros: "
                + "nit: " + nitEmpresa
                + " fechainicio " + fechaInicio
                + " usuario: " + seudonimo);
        String Msj = null;
        this.persisKioAusent_pkg = new PersistenciaKioAusentismo_pkg();
        try {
            Msj = this.persisKioAusent_pkg.getMensajeValidacionAusent(fechaInicio, seudonimo, nitEmpresa, cadena, "");
            JsonObject json = Json.createObjectBuilder()
                    .add("valida", Msj)
                    .build();

            return Response.status(Response.Status.OK).entity(json).build();
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".getvalidaFechaNovedadEmpleadoXJefe(): " + "Error: " + ex.toString());
            return Response.status(Response.Status.NOT_FOUND).entity("Error" + ex.toString()).build();
        }
    }

    @POST
    @Path("/nuevoEstadoSolici")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response setNuevoEstadoSolici(@QueryParam("secuencia") String secKioEstadoSolici,
            @QueryParam("motivo") String motivo,
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("estado") String estado,
            @QueryParam("fechainicio") String fechaInicio,
            @QueryParam("cadena") String cadena, @QueryParam("grupo") String grupoEmpr,
            @QueryParam("urlKiosco") String urlKiosco) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Parametros: "
                + "secKioEstadoSolici: " + secKioEstadoSolici
                + "motivo: " + motivo
                + "seudonimo: " + seudonimo
                + "nitEmpresa: " + nitEmpresa
                + "estado: " + estado
                + "fechaInicio: " + fechaInicio
                + "cadena: " + cadena
                + "grupoEmpr: " + grupoEmpr
                + "urlKiosco: " + urlKiosco
        );

        List s = null;
        int res = 0;
        this.persisConfiCorreoKio = new PersistenciaConfiCorreoKiosko();
        this.persisPersonas = new PersistenciaPersonas();
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();

        String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(new Date());
        String urlKio = urlKiosco + "#/login/" + grupoEmpr;
        String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupoEmpr;
        String secEmplSolicita = "";
        String secEmplEjecuta = "";
        String secPerAutoriza = null;
        String secEmplJefe = null;
        String nombreAutorizaSolici = "";
        String correoAutorizaSolici = null;
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        try {
            IntervinientesSolAusent intervinientes = this.persisKioSoliciAusent.getIntervinientesPorEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
//            String secEmplEjecuta = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            if (!intervinientes.getAutorizador().toPlainString().isEmpty()) {
//                secEmplEjecuta = intervinientes.getAutorizador().toPlainString();
                secPerAutoriza = intervinientes.getAutorizador().toPlainString();
            } else if (!intervinientes.getEmpleadoJefe().toPlainString().isEmpty()) {
                secEmplEjecuta = intervinientes.getEmpleadoJefe().toPlainString();
            } else {
//                System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Error: " + "Al consultar quien procesa la solicitud");
                throw new Exception("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Error: " + "Al consultar quien procesa la solicitud");
            }

            System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "La persona que ejecuta es: " + secPerAutoriza);
            System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "El empleado que ejecuta es: " + secEmplEjecuta);
//            String secEmplSolicita = this.persisKioSoliciAusent.getEmplXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
            secEmplSolicita = intervinientes.getEmpleado().toPlainString();

            if (motivo == null || motivo.equalsIgnoreCase("")) {
                motivo = " ";
            }

            if (secPerAutoriza != null) {
                nombreAutorizaSolici = this.persisPersonas.getApellidoNombreXSecPer(secPerAutoriza, nitEmpresa, cadena, "");
                correoAutorizaSolici = this.persisPersonas.getCorreoPorPersona(secPerAutoriza, nitEmpresa, cadena);
            } else {
                try {
                    secEmplJefe = this.persisKioSoliciAusent.getSecuenciaJefeEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
                    if (secEmplJefe != null) {
                        nombreAutorizaSolici = this.persisPersonas.getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, "");
                        correoAutorizaSolici = this.persisPersonas.getCorreoPorEmpleado(secEmplJefe, nitEmpresa, cadena);
                    }
                } catch (Exception e) {
                    System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Error-2: " + e.toString());
                    System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Error-2: " + "Error al consultar empleadoJefe relacionado a la solicitud");
                }
            }
            if (estado.equals("RECHAZADO")) {
                res = this.persisKioSoliciAusent.creaEstadoSolicitudAusent(nitEmpresa, cadena, fechaGeneracion, estado, secEmplEjecuta, secPerAutoriza, secKioEstadoSolici, motivo, "");
            } else {
                if (estado.equals("CANCELADO")) {
                    secPerAutoriza = null;
                    System.out.println("La solicitud está siendo CANCELADA");
                }
                res = this.persisKioSoliciAusent.creaEstadoSolicitudAusent(nitEmpresa, cadena, fechaGeneracion, estado, secEmplEjecuta, secPerAutoriza, secKioEstadoSolici);
            }
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Error-2: " + ex.toString());
            return Response.status(Response.Status.NOT_FOUND).entity("Error-2" + ex.toString()).build();
        }
        System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "solicitud " + estado + " con éxito.");
        try {
            if (res > 0) {
                EnvioCorreo c = new EnvioCorreo();
                String estadoVerbo = estado.equals("CANCELADO") ? "CANCELAR"
                        : estado.equals("AUTORIZADO") ? "PRE-APROBAR"
                        : estado.equals("RECHAZADO") ? "RECHAZAR" : estado;
                String estadoPasado = estado.equals("CANCELADO") ? "canceló"
                        : estado.equals("AUTORIZADO") ? "pre-aprobó"
                        : estado.equals("RECHAZADO") ? "rechazó" : estado;

                String mensaje = "Nos permitimos informar que se acaba de " + estadoVerbo + " una novedad de ausentismo";
                if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")) {
                    mensaje += " creada para " + this.persisPersonas.getApellidoNombreXsecEmpl(secEmplSolicita, nitEmpresa, cadena, "");
                }
                mensaje += " en el módulo de Kiosco Nómina Designer. ";
                if (estado.equals("AUTORIZADO")) {
                    mensaje += "Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso.";
                }
                mensaje += "<br><br>";
                if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")) {
                    mensaje += "La persona que " + estadoPasado.toUpperCase() + " LA SOLICITUD es: " + nombreAutorizaSolici;
                    mensaje += "<br>";
                }
                if (estado.equals("CANCELADO")) {
                    mensaje += "La persona a cargo de HACER EL SEGUIMIENTO es: " + nombreAutorizaSolici + "<br>";
                }
                if (estado.equals("AUTORIZADO")) {
                    mensaje += "Por favor seguir el proceso en: <a style='color: white !important;' target='_blank' href=" + urlKio + ">" + urlKio + "</a><br><br>"
                            + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita.<br><br>"
                            + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                            + "<br><a style='color: white !important;' href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a>";
                }
                String fechaInicioAusent = fechaInicio;
                System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "fechaInicioAusent: " + fechaInicioAusent);
                System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "urlKio: " + urlKio);

                
                ConfiCorreoKiosko cck = this.persisConfiCorreoKio.obtenerConfiguracionCorreoNativo(nitEmpresa, cadena);
                String servidorsmtp = cck.getServidorSMTP();
                
                if (estado.equals("CANCELADO")) {
                    if (c.enviarCorreoVacaciones(
                            cck.getServidorSMTP(), cck.getPuerto(), cck.getAutenticado(), cck.getStartTLS(), cck.getRemitente(), cck.getClave(),
                            this.persisPersonas.getCorreoPorEmpleado(secEmplSolicita, nitEmpresa, cadena),
                            "Solicitud de AUSENTISMO Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de AUSENTISMO: " + fechaInicioAusent,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Correo enviado a la persona que ejecuta");
                    }

                }

                if (estado.equals("AUTORIZADO") || estado.equals("RECHAZADO")) {
                    if (c.enviarCorreoVacaciones(
                            cck.getServidorSMTP(), cck.getPuerto(), cck.getAutenticado(), cck.getStartTLS(), cck.getRemitente(), cck.getClave(),
                            this.persisPersonas.getCorreoPorEmpleado(secEmplSolicita, nitEmpresa, cadena),
                            "Solicitud de AUSENTISMO Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de AUSENTISMO: " + fechaInicioAusent,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Correo enviada a la persona que ejecuta.");
                    }
                    // Enviar correo al jefe/autorizador de vacaciones
                    if (c.enviarCorreoVacaciones(
                            cck.getServidorSMTP(), cck.getPuerto(), cck.getAutenticado(), cck.getStartTLS(), cck.getRemitente(), cck.getClave(),
                            correoAutorizaSolici,
                            "Solicitud de AUSENTISMO Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de AUSENTISMO: " + fechaInicioAusent,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Correo enviado al empleado que solicita asociado " + correoAutorizaSolici);
                    }

                }
            } else {
                System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Error al procesar la solicitud.");
            }
            return Response.status(Response.Status.OK).entity(res > 0).build();
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".setNuevoEstadoSolici(): " + "Error-3: " + ex.toString());
            return Response.status(Response.Status.NOT_FOUND).entity("Error-3: " + ex.toString()).build();
        }
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
        boolean cargueArchivo = false;

        this.gestionArchivos = new GestionArchivos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();

        try {
            fileName = fileFormDataContentDisposition.getFileName();
            uploadFilePath = this.gestionArchivos.writeToFileServerAusentismos(fileInputStream, fileName, nitEmpresa, cadena);
            if (uploadFilePath.equals("N")) {
                cargueArchivo = false;
            } else {
                if (this.persisKioSoliciAusent.updateAnexoKioSoliciAusentismo(seudonimo, nitEmpresa, fileName, secKioSoliciAusentismo, cadena) > 0) {
                    System.out.println("Nombre de archivo actualizado en la solicitud");
                    cargueArchivo = true;
                } else {
                    System.out.println("Archivo subido pero no se actualizó el nombre en la solicitud.");
                    cargueArchivo = true;
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Error: " + this.getClass().getName() + ".cargarAnexo()" + ioe.getMessage());
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error " + this.getClass().getName() + ".cargarAnexo() " + e.getMessage());
        }
        return Response.ok(cargueArchivo).build();
    }

    @GET
    @Path("/enviaCorreoNuevoAusentismo")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean enviaCorreoNuevoAusentismo(@QueryParam("usuario") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("solicitud") String secKioSoliciAusentismo,
            @QueryParam("observacion") String observacionNovedad, @QueryParam("asunto") String asunto,
            @QueryParam("urlKiosco") String urlKiosco, @QueryParam("grupo") String grupo, @QueryParam("cadena") String cadena) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".enviaCorreoNuevoAusentismo(): " + "Parametros: "
                + "seudonimo: " + seudonimo
                + ", nit: " + nitEmpresa
                + ", secKioSoliciAusentismo: " + secKioSoliciAusentismo
                + " urlKiosco " + urlKiosco
                + " grupo " + grupo);

        this.persisConfiCorreoKio = new PersistenciaConfiCorreoKiosko();
        this.persisKioConfigMod = new PersistenciaKioConfigModulos();
        this.persisPersonas = new PersistenciaPersonas();
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();

        String secPerKioAutorizador = null;

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
        List<Object[]> list = null;
        String secEmpl = null;

        try {
            secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            secPerKioAutorizador = this.persisAutorizaSoli.consultarSecuenciaPorAutorizador(secEmpl, nitEmpresa, cadena, "", 5);
            if (secPerKioAutorizador != null) {
                // Existe relación con kioautorizadores
                nombreAutorizaSolici = this.persisPersonas.getApellidoNombreXSecPer(secPerKioAutorizador, nitEmpresa, cadena, "");
                correoAutorizaSolici = this.persisPersonas.getCorreoPorPersona(secPerKioAutorizador, nitEmpresa, cadena);
            } else {
                String secEmplJefe = this.persisKioSoliciAusent.getEmplJefeXsecKioSoliciAusentismo(secKioSoliciAusentismo, nitEmpresa, cadena);
                if (secEmplJefe != null) {
                    nombreAutorizaSolici = this.persisPersonas.getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, "");
                    correoAutorizaSolici = this.persisPersonas.getCorreoPorEmpleado(secEmplJefe, nitEmpresa, cadena);
                }
            }

            list = this.persisKioSoliciAusent.getDetalleAusentismo(secKioSoliciAusentismo, nitEmpresa, cadena);
            System.out.println("kioCausasAusentismoFacadeREST" + ".enviaCorreoNuevoAusentismo(): " + "Tamaño lista: " + list.size());
        } catch (Exception e) {
            System.out.println("kioCausasAusentismoFacadeREST" + ".enviaCorreoNuevoAusentismo(): " + "Error-1: " + e.toString());
        }

        try {
            if (!list.isEmpty()) {
                personaCreaSolici = list.get(0)[0].toString();
                fechaCorreo = list.get(0)[1].toString();
                fechaInicial = list.get(0)[2].toString();
                fechaFin = list.get(0)[3].toString();
                dias = list.get(0)[4].toString();
                nombreAnexo = list.get(0)[5].toString();
                nombreAutorizaSolici = list.get(0)[8].toString();

            }
        } catch (Exception e) {
            System.out.println("kioCausasAusentismoFacadeREST" + ".enviaCorreoNuevoAusentismo(): " + "Error-2: " + e.toString());
        }

        String mensaje = "Nos permitimos informar que se acaba de reportar un ausentismo en el módulo de Kiosco Nómina Designer. "
                + "Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                + " <br><br> "
                + "La persona que REPORTÓ LA NOVEDAD AUSENTISMO es: " + personaCreaSolici
                + "<br>"
                + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici
                + "<br>"
                + "La novedad se reportó por " + dias + " días, desde el " + fechaInicial + " hasta el " + fechaFin
                + "<br><br>Por favor seguir el proceso en: <a style='color: white !important;' href='" + urlKio + "'>" + urlKio + "</a>"
                + "<br><br>"
                + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita."
                + "<br><br>"
                + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                + "<br><a style='color: white !important;' href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a>";

        boolean enviado = true;
        asunto += " de " + personaCreaSolici + " " + fecha;
        EnvioCorreo e = new EnvioCorreo();
        try {
            String correoUsuario = this.persisPersonas.getCorreoConexioneskioskos(seudonimo, nitEmpresa, cadena);
            ConfiCorreoKiosko cck = this.persisConfiCorreoKio.obtenerConfiguracionCorreoNativo(nitEmpresa, cadena);
            String servidorsmtp = cck.getServidorSMTP();
            String puerto = cck.getPuerto();
            String autenticado = cck.getAutenticado();
            String starttls = cck.getStartTLS();
            String remitente = cck.getRemitente();
            String clave = cck.getClave();
            String correoEmpleado = this.persisPersonas.getCorreoPorEmpleado(secEmpl, nitEmpresa, cadena);
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

                if (this.persisKioConfigMod.consultaAuditoria("AUSENTISMOS", "41", nitEmpresa, cadena).compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("Si debe llevar auditoria crearNovedad Ausentismo");
                    List<String> lista = this.persisKioConfigMod.consultarCorreosAuditoria("AUSENTISMOS", "41", nitEmpresa, cadena);
                    Iterator<String> it = lista.iterator();

                    String mensajeAuditoria = "Nos permitimos informar que " + personaCreaSolici
                            + " reportó una NOVEDAD DE AUSENTISMO el " + fechaCorreo + " en el módulo de Kiosco Nómina Designer."
                            + "<br>"
                            + "La solicitud se creó por " + dias + " días, para ser disfrutados desde el " + fechaInicial + " hasta el " + fechaFin
                            + "<br>"
                            + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici + ".";
                    while (it.hasNext()) {
                        String correoenviar = it.next();
                        System.out.println("correo auditoria: " + correoenviar);
                        System.out.println("codigoopcion: " + "41");

                        e.enviarCorreoInformativo("Auditoria: Nueva novedad de AUSENTISMO Kiosco. " + fecha,
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
        System.out.println("kioCausasAusentismosFacadeREST" + ".getSolicitudesXEmpleadoJefe(): " + "Parametros: "
                + "seudonimo: " + seudonimo
                + "nitEmpresa: " + nitEmpresa
                + "cadena: " + cadena
        );
        List s = null;
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();

        try {
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            s = this.persisKioSoliciAusent.getSolicitudesPorJefe(nitEmpresa, cadena, secEmpl);
            s.forEach(System.out::println);

            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".getSolicitudesXEmpleadoJefe(): " + "Error: " + ex.toString());
            return Response.status(Response.Status.OK)
                    .entity("kioCausasAusentismosFacadeREST" + ".getSolicitudesXEmpleadoJefe(): " + "Error: " + ex.toString())
                    .build();
        }
    }
    
    @GET
    @Path("/solicitudesPorAutorizador")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getsolicitudesPorAutorizador(@QueryParam("usuario") String seudonimo,
            @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".getsolicitudesPorAutorizador(): " + "Parametros: "
                + "seudonimo: " + seudonimo
                + "nitEmpresa: " + nitEmpresa
                + "cadena: " + cadena
        );
        List s = null;
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisKioSoliciAusent = new PersistenciaKioSoliciAusentismos();

        try {
            BigDecimal secEmpl = this.persisConKiosko.getPersonaPorSeudonimo(seudonimo, nitEmpresa, cadena);
            s = this.persisKioSoliciAusent.getSolicitudesPorAutorizador(nitEmpresa, cadena, secEmpl.toPlainString());
            s.forEach(System.out::println);

            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("kioCausasAusentismosFacadeREST" + ".getsolicitudesPorAutorizador(): " + "Error: " + ex.toString());
            return Response.status(Response.Status.OK)
                    .entity("kioCausasAusentismosFacadeREST" + ".getsolicitudesPorAutorizador(): " + "Error: " + ex.toString())
                    .build();
        }
    }

    @GET
    @Path("/obtenerAnexo/")
    @Produces({"application/pdf"})
    public Response obtenerAnexo(@QueryParam("anexo") String anexo, @QueryParam("cadena") String cadena, @QueryParam("empresa") String nitEmpresa) {
        System.out.println("kioCausasAusentismosFacadeREST" + ".obtenerAnexo(): " + "Parametros: "
                + "anexo: " + anexo
                + ", cadena: " + cadena
                + ", nitEmpresa: " + nitEmpresa);
        FileInputStream fis = null;
        File file = null;
        this.persisGeneralesKio = new PersistenciaGeneralesKiosko();
        try {
//            String RUTAFOTO = this.persisGeneralesKio.getPathReportes(nitEmpresa, cadena) + "\\anexosAusentismos\\";
            String RUTAFOTO = this.persisGeneralesKio.getPathAusentismos(nitEmpresa, cadena);
            fis = new FileInputStream(new File(RUTAFOTO + anexo));
            file = new File(RUTAFOTO + anexo);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Anexo no encontrada: " + anexo, ex);
            System.getProperty("user.dir");
            System.out.println("kioCausasAusentismosFacadeREST" + ".obtenerAnexo(): " + "Ruta del proyecto: " + this.getClass().getClassLoader().getResource("").getPath());;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                System.out.println("kioCausasAusentismosFacadeREST" + ".obtenerAnexo(): " + "Error-1: " + ex.toString());
                Logger.getLogger(ConexionesKioskosFacadeREST.class.getName()).log(Level.SEVERE, "Error cerrando fis " + anexo, ex);
            }
        }
        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + anexo + "\"");
        return responseBuilder.build();
    }
}
