/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.services;

import co.com.designer.kiosko.entidades.KioCausasAusentismos;
import co.com.designer.kiosko.entidades.DiagnosticosCategorias;
import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import javax.ws.rs.core.Response;

/**
 *
 * @author UPC006
 */
@Stateless
@Path("ausentismos")
public class kioCausasAusentismosFacadeREST extends AbstractFacade<KioCausasAusentismos>  {

    public kioCausasAusentismosFacadeREST() {
        super(KioCausasAusentismos.class);
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
    
    @Override
    protected void setearPerfil() {
        try {
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil: " + ex);
        }    }    
    
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
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        String retorno = "";
        String mensaje ="";
        String secEmplJefe = null;
        try {
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            if (secEmpleado!=null){
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
            }else{
                mensaje = "El empleado no existe";
            }
        } catch (Exception e) {
           // return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
           mensaje = "Se ha presentado un error al hacer la consulta. Si el error persiste por favor comuniquese con el área de Talento humano de su empresa.";
        }    
         JsonObject json=Json.createObjectBuilder()
        .add("resultado", retorno)
        .add("mensaje", mensaje)
        .build();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }  
    
    @POST
    @Path("/crearNovedadAusentismo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String crearNovedadAusentismo(
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("fechainicio") String fechainicial, @QueryParam("fechafin") String fechaFin,
            @QueryParam("dias") String dias,
            @QueryParam("causa") String secCausaAusent,
            @QueryParam("clase") String secClaseAusent,
            @QueryParam("tipo") String secTipoAusent,
            @QueryParam("prorroga") String secKioNovedadAusent,
            @QueryParam("observacion") String observacion,
            @QueryParam("anexoadjunto") String anexoAdjunto,
            @QueryParam("cadena") String cadena,
            @QueryParam("urlKiosco") String urlKiosco,
            @QueryParam("grupo") String grupoEmpr, @QueryParam("fechafin") String fechafin) {
        System.out.println("parametros: crearSolicitudVacaciones{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + "\n fechainicio: " + fechainicial + ", fechaFin: " + fechaFin + ", dias: " + dias
                + " causa: " + secCausaAusent + ", clase: " + secClaseAusent + ", tipo: " + secTipoAusent
                + "\n prorroga: " + secKioNovedadAusent + ", observacion: " + observacion
                + ", cadena: " + cadena + ", grupo: " + grupoEmpr);
        System.out.println("link Kiosco: " + urlKiosco);
        System.out.println("grupoEmpresarial: " + grupoEmpr);
        boolean soliciCreada = false;
        boolean soliciValida = false;
        String esquema = null;
        String nombreAnexo = null; // nombre con el que debe guardarse el campo del documento anexo
        try {
            esquema = getEsquema(esquema, cadena);
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
                nombreAnexo = secEmpl + "_" + secCausaAusent + "_" + fechaGeneracion+".pdf";
            }
            if (secEmplJefe != null) {
                System.out.println("creaKioSoliciVacas: EmpleadoJefe: " + secEmplJefe);
                nombreAutorizaSolici += getApellidoNombreXsecEmpl(secEmplJefe, nit, cadena, esquema);
                correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nit, cadena, esquema);
                System.out.println("El empleado tiene relacionado a empleadoJefe " + nombreAutorizaSolici + " - " + correoAutorizaSolici);

                // Registro en tabla KIOSOLICIAUSENTISMOS
                if (creaKioSoliciAusentismos(seudonimo, secEmplJefe, nit, nombreAnexo, fechaGeneracion, cadena, esquema)) {
                    String secKioSoliciAusent = getSecKioSoliciAusent(secEmpl, fechaGeneracion, secEmplJefe, nit, cadena, esquema);
                    System.out.println("secuencia kiosoliciausentismos creada: " + secKioSoliciAusent);
                    
                    int diasIncapacidad =Integer.parseInt(dias);
                    /*if (diasIncapacidad<=2){
                        // Si los días reportados son 2 o menos se deben registrar en una sola novedad
                        if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, 2, fechafin, secKioSoliciAusent, cadena, esquema)){
                            
                        }
                    } else {
                        // Si los dias reportados son más de 2 se deben registrar en dos novedades
                        // Registro novedad primeros 2 dias
                        if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, 2, fechafin, secKioSoliciAusent, cadena, esquema)) {

                            // Registro segunda novedad por los días faltantes
                            if (creaKioNovedadSoliciAusent(seudonimo, nit, fechainicial, secTipoAusent, secClaseAusent, secCausaAusent, diasIncapacidad - 2, fechafin, secKioSoliciAusent, cadena, esquema)) {
                                mensaje = "Novedad de ausentismo reportada exitosamente.";
                                soliciCreada = true;
                            }
                        } else {
                            System.out.println("Ha ocurrido un error al momento de crear el registro de la primera novedad");
                            mensaje = "Ha ocurrido un error y no fue posible reportar la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                        }
                    }*/
                    
                    // Registro en tabla KIOESTADOSSOLICIAUSENT
                    if (creaKioEstadoSoliciAusent(seudonimo, nit, secKioSoliciAusent, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                        System.out.println("Estado de novedad de ausentismo creado.");
                    } else {
                        mensaje = "Ha ocurrido un error y no fue posible crear la novedad de ausentismo, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                    }
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
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }
    
    public boolean creaKioSoliciAusentismos(String seudonimo, String secEmplJefe, String nit, String nombreAnexo, String fechaGeneracion, String cadena, String esquema) {
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
                        + "(EMPLEADO, USUARIO, EMPLEADOJEFE, ACTIVA, FECHAGENERACION, NOMBREANEXO) "
                        + "VALUES "
                        + "(?,  USER, ?, 'S', TO_DATE(?, 'ddmmyyyy HH24miss'), ?)";
                Query query = getEntityManager(cadena).createNativeQuery(sql);
                query.setParameter(1, secEmpleado);
                query.setParameter(2, secEmplJefe);
                query.setParameter(3, fechaGeneracion);
                query.setParameter(4, nombreAnexo);
                conteo = query.executeUpdate();
                System.out.println("Registro KIOSOLICIAUSENTISMOS: " + conteo);
            } else {
                conteo = 0; // No crear la solicitud si no hay un jefe relacionado
            }
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".creaKioSoliciAusentismos(): "+e.getMessage());
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
            System.out.println("Error: "+this.getClass().getName()+".+getSecKioSoliciAusent(): " + e.getMessage());
        }
        return secKioSoliciAusent;
    }      
    
    public boolean creaKioNovedadSoliciAusent(String seudonimo, String nitEmpresa, String fechainicial,
            String secTipo, String secClase, String secCausa,
            int dias, String fechaFin, String kioSoliciAusentismo, String cadena, String esquema) {
        int conteo = 0;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            System.out.println("parametros creaKioNovedadSolici seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", fechainicial: " + fechainicial + " fecha fin: " + fechaFin + " dias: " + dias );
            String sql = "INSERT INTO KIONOVEDADESSOLICI "
                    + "(EMPLEADO, FECHAINICIALAUSENTISMO, DIAS, TIPO, SUBTIPO, "
                    + "TIPOAUSENTISMO, CLASEAUSENTISMO, CAUSAAUSENTISMO, "
                    + "FECHASISTEMA, FECHAFIN, ESTADO, \n"
                    + "FECHAINICIALPAGO, FECHAFINPAGO, FECHAEXPEDICION, FORMALIQUIDACION, PORCENTAJELIQ, "
                    + "DIAGNOSTICOCATEGORIA, KIOSOLICIAUSENTISMO, KIONOVEDADPRORROGA, PAGADO) \n"
                    + "VALUES \n"
                    + "(?, TO_DATE(?,'DD/MM/YYYY'), ?, 'AUSENTISMO', 'AUSENTISMO', "
                    + "?, ?, ?, "
                    + "SYSDATE, TO_DATE(?,'DD/MM/YYYY'), 'ABIERTO', "
                    + "?, TO_DATE(?,'DD/MM/YYYY'), SYSDATE, ?, ?, "
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
            query.setParameter(10, null); // formaLiquidacion
            query.setParameter(11, null); // porcentLiq
            query.setParameter(12, null); // diagnostico
            query.setParameter(13, kioSoliciAusentismo); // kioSoliciAusentismo
            query.setParameter(14, null); // kioNovedadProrroga
            conteo = query.executeUpdate();
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
        System.out.println("parametros creaKioEstadoSoliciAusent(): seudonimo: " + seudonimo + ", nit: "+nit+", kiosoliciausentismo: "+kioSoliciAusentismo+""
                + "\n fechaProcesa: "+fechaProcesa+", estado: " + estado+", cadena: "+cadena);
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
            System.out.println("registro kioestadosoliciausent: "+res);
        } catch (Exception ex) {
            System.out.println("Error "+this.getClass().getName()+".creaKioEstadoSoliciAusent(): " + ex.getMessage());
            return false;
        }
        return res>0;
    }    
    
    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", cadena: "+cadena);
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
            System.out.println("Error: "+this.getClass().getName()+".getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }   
    
    public String consultarSecuenciaEmpleadoJefe(String secEmpleado, String nitEmpresa, String cadena, String esquema) {
        System.out.println("parametros consultarSecuenciaEmpleadoJefe: secEmpleado: "+secEmpleado+", cadena: "+cadena);
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
            System.out.println("Error: "+this.getClass().getName()+".consultarSecuenciaEmpleadoJefe: " + e.getMessage());
        }
        return secJefe;
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
            System.out.println("Resultado getApellidoNombreXsecEmpl(): "+nombre);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getApellidoNombreXsecEmpl(): " + e);
        }
        return nombre;
    }    
    
    public String getCorreoXsecEmpl(String secEmpl, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getCorreoXsecEmpl(): secEmpl: "+secEmpl+", cadena: "+cadena);
        System.out.println("sec Empleado: "+secEmpl);
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
            System.out.println("getCorreoXsecEmpl(): "+correo);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+"getCorreoXsecEmpl(): " + e.getMessage());
        }
        return correo;
    }    
    
    public String getEsquema( String nitempresa, String cadena) {
        System.out.println("Parametros getEsquema(): nitempresa: "+nitempresa+", cadena: "+cadena);
        String esquema = null;
        String sqlQuery;
        try {
            sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=?";
            Query query = getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitempresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("Esquema: "+esquema);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getEsquema(): " + e);
        } 
        return esquema;
    }      

 
    
    @GET
    @Path("/fechaFinAusentismo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getFecha(@QueryParam("nitempresa") String nitEmpresa
                , @QueryParam("cadena") String cadena,@QueryParam("fechaInicio") String fechaInicio
                ,@QueryParam("dias") String dias,@QueryParam("empleado") String empleado
                ,@QueryParam("causa") String causa) {
           String esquema = getEsquema(nitEmpresa, cadena);
           setearPerfil(esquema, cadena);
           JsonObject fechaSugerida = prueba(fechaInicio, dias, empleado, causa, cadena, nitEmpresa, esquema);
                    
            /*return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();*/
            return Response.status(Response.Status.CREATED).entity(fechaSugerida)
                    .build();
    } 
     /**

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
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater fecha en String, cadena del cliente, Esquema para setear rol
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
            System.out.println("getDate(): "+fechaRegreso);
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
    
    /**
     * metodo publico para consultar la fecha de contratacion del empleado
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia del empleado, fechainicio, cadena del cliente, Esquema para setear rol
     * @param result una variable en tipo Date con la fecha del resultado
     * @return Date
     */
       public Date getFechaContrato( String empleado, String fechaInic, String cadena, String esquema) {
        System.out.println("Parametros getFechaContrato(): empleado: "+empleado+", fechaInicio: "+fechaInic);
        System.out.println("Entre a getFechaContrato");
        Date fechaVigencia = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT V.FECHAVIGENCIA FROM VIGENCIASTIPOSCONTRATOS V "
                    + " WHERE V.EMPLEADO=? "
                    + " AND V.FECHAVIGENCIA = "
                    + "     (SELECT MAX(FECHAVIGENCIA) FROM VIGENCIASTIPOSCONTRATOS VI "
                    + "     WHERE V.EMPLEADO = VI.empleado " 
                    + "     AND VI.FECHAVIGENCIA <= to_date(?, 'dd/mm/yyyy')) ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, empleado);
            query.setParameter(2, fechaInic);
            fechaVigencia = (Date) query.getSingleResult();
            System.out.println("fechaVigencia: "+fechaVigencia);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getFechaContrato(): " + e);
        } 
        return fechaVigencia;
    } 
       
    /**
     * metodo publico para consultar la Secuencia del tipo trabajdor del empleado
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia del empleado, fechainicio, cadena del cliente, Esquema para setear rol
     * @param result una variable en tipo String para traer la secuencia del tipo de trabajador
     * @return String
     */   
    public String getTipoTrabajador( String empleado, String fechaInic, String cadena, String esquema) {
        System.out.println("Parametros getTipoTrabajador(): empleado: "+empleado+", fechaInicio: "+fechaInic);
        System.out.println("Entre a getTipoTrabajador");
        String tipoTabajador = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT TIPOTRABAJADOR FROM VIGENCIASTIPOSTRABAJADORES V "
                    + " WHERE V.EMPLEADO= ? "
                    + " AND V.FECHAVIGENCIA = "
                    + "     (SELECT MAX(FECHAVIGENCIA) FROM VIGENCIASTIPOSTRABAJADORES VI "
                    + "     WHERE V.EMPLEADO = VI.empleado "
                    + "     AND VI.FECHAVIGENCIA <= to_date(?, 'dd/mm/yyyy')) ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, empleado);
            query.setParameter(2, fechaInic);
            tipoTabajador = query.getSingleResult().toString();
            System.out.println("tipoTabajador: "+tipoTabajador);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getTipoTrabajador(): " + e);
        } 
        return tipoTabajador;
    } 
    /**
     * metodo publico para consultar los dias del tipo trabajador 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia del TipoTrabajador, fechainicio, cadena del cliente, Esquema para setear rol
     * @param result una variable en tipo String con la cantidad de dias de tipo trabajador
     * @return String
     */
    public String getDiasTrabajador( String tipoTrabador, String fechaInic, String cadena, String esquema) {
        //mensaje 2
        System.out.println("Parametros getDiasTrabajador(): tipoTrabador: "+tipoTrabador+", fechaInicio: "+fechaInic);
        System.out.println("Entre a getDiasTrabajador");
        String dias = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT DIAS FROM VIGENCIASDIASTT VD "
                    + " WHERE VD.TIPOTRABAJADOR=? "
                    + " AND ROWNUM=1 "
                    + " AND VD.FECHAVIGENCIA = "
                    + "     (SELECT MAX(VDI.FECHAVIGENCIA) FROM VIGENCIASDIASTT VDI " 
                    + "     WHERE VDI.TIPOTRABAJADOR=VD.TIPOTRABAJADOR " 
                    + "     AND VDI.FECHAVIGENCIA <= to_date(?, 'dd/mm/yyyy')) ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, tipoTrabador);
            query.setParameter(2, fechaInic);
            dias = query.getSingleResult().toString();
            System.out.println("dias: "+dias);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getDiasTrabajador(): " + e);
        } 
        return dias;
    }
    /**
     * metodo publico para consultar el acronimo de la causa seleccionada 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia de la causa, cadena del cliente, Esquema para setear rol
     * @param result una variable en tipo Sting con un tamaño de 2 caracteres 
     * @return String
     */
    
    public String getCausaOrigenIncapacidad( String causa , String cadena, String esquema) {
        //mensaje 2
        System.out.println("Parametros getCausaOrigen(): causa: "+causa);
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
            System.out.println("CausaOrigenIncapacidad: "+ CausaOrigenIncapacidad);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/
          

        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getCausaOrigen(): " + e);
            CausaOrigenIncapacidad = null;
        } 
        return CausaOrigenIncapacidad;
    }
    /**
     * metodo publico para consultar la secuencia de la causa de MATERNIDAD Y PATERNIDAD
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater Acronimo de la causa, cadena del cliente, Esquema para setear rol
     * @param result una variable en tipo String con la secuencia de la causa de MATERNIDAD Y PATERNIDAD 
     * @return String
     */
    public String getSecuenciaCausa( String causa , String cadena, String esquema) {
        //Secuencia de causa 
        System.out.println("Parametros getCausaOrigen(): causa: "+causa);
        System.out.println("Entre a getCausaFormaLiquidacion");
        String CausaFormaLiquidacion = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT causasausentismos_pkg.CAPTURARsecuencia(?) "
                    + "FROM DUAL ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, causa);
            CausaFormaLiquidacion = query.getSingleResult().toString();
            /*Iterator<String> it= CausaFormaLiquidacion.iterator();
            while(it.hasNext()) {
              System.out.println(it.next());
            }*/
            System.out.println("getCausaFormaLiquidacion: "+ CausaFormaLiquidacion);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/
          

        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getCausaFormaLiquidacion(): " + e);
            CausaFormaLiquidacion = null;
        } 
        return CausaFormaLiquidacion;
    }
    /**
     * metodo publico para consultar la forma de liquidacion de la causa seleccionada 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia de la causa, cadena del cliente, Esquema para setear rol
     * @param result una variable en tipo String con el nombre de la liquidacion
     * @return String
     */
    public String getCausaFormaLiquidacion( String causa , String cadena, String esquema) {
        //mensaje 2
        System.out.println("Parametros getCausaOrigen(): causa: "+causa);
        System.out.println("Entre a getCausaFormaLiquidacion");
        String CausaFormaLiquidacion = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT FORMALIQUIDACION "
                    + "FROM CAUSASAUSENTISMOS CA "
                    + "WHERE CA.SECUENCIA=? ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, causa);
            CausaFormaLiquidacion = query.getSingleResult().toString();
            /*Iterator<String> it= CausaFormaLiquidacion.iterator();
            while(it.hasNext()) {
              System.out.println(it.next());
            }*/
            System.out.println("getCausaFormaLiquidacion: "+ CausaFormaLiquidacion);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/
          

        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getCausaFormaLiquidacion(): " + e);
            CausaFormaLiquidacion = null;
        } 
        return CausaFormaLiquidacion;
    }
    /**
     * metodo publico para consultar el orcentaje de liquidacion por defecto
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia de la causa, cadena del cliente, Esquema para setear rol
     * @param result una variable en tipo String con un numero 
     * @return String
     */
    public String getCausaPorcentajeLiq( String causa , String cadena, String esquema) {
        //mensaje 2
        System.out.println("Parametros getCausaOrigen(): causa: "+causa);
        System.out.println("Entre a getCausaFormaLiquidacion");
        String CausaFormaLiquidacion = null;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT PORCENTAJELIQUIDACION "
                    + "FROM CAUSASAUSENTISMOS CA "
                    + "WHERE CA.SECUENCIA=? ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, causa);
            CausaFormaLiquidacion = query.getSingleResult().toString();
            /*Iterator<String> it= CausaFormaLiquidacion.iterator();
            while(it.hasNext()) {
              System.out.println(it.next());
            }*/
            System.out.println("getCausaFormaLiquidacion: "+ CausaFormaLiquidacion);
            /*
            CausaFormaLiquidacion.forEach(System.out::println);
            System.out.println("CausaFormaLiquidacion 4: " + CausaFormaLiquidacion.get(0).toString());
            System.out.println("CausaFormaLiquidacion 1: " + CausaFormaLiquidacion);
            System.out.println("CausaFormaLiquidacion 3: " + CausaFormaLiquidacion.toArray()[0]);*/
          

        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getCausaFormaLiquidacion(): " + e);
            CausaFormaLiquidacion = null;
        } 
        return CausaFormaLiquidacion;
    }
    /**
     * metodo publico para consultar la fecha de inicio de solicitud para validar que no exista una solicitud de ausentismo ya creada 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia del empleado, fechainicio, cadena del cliente, Esquema para setear rol
     * @param result una variable en BigDecimal con 0=ninguna y 1=Si esxite
     * @return BigDecimal
     */
    public BigDecimal getValidaFechaAusentismo(String empleado, String fechaInic, String cadena, String esquema) {
        //mensaje 3
        System.out.println("Parametros getValidaFecha(): empleado: "+empleado+", fechaInic: "+fechaInic);
        System.out.println("Entre a getValidaFecha");
        BigDecimal haySolici = BigDecimal.ZERO;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT COUNT(*) " +
                       " FROM SOAUSENTISMOS SO WHERE EMPLEADO = ? " +
                       " AND to_date(?, 'dd/mm/yyyy') BETWEEN SO.FECHA AND SO.FECHAFINAUS ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, empleado);
            query.setParameter(2, fechaInic);
            haySolici = (BigDecimal) query.getSingleResult();
            System.out.println("haySolici: "+haySolici);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getValidaFecha(): " + e);
        } 
        return haySolici;
    }
    /**
     * metodo publico para consultar la fecha de inicio de solicitud para validar que no exista una solicitud de vacaciones ya creada 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater secuencia del empleado, fechainicio, cadena del cliente, Esquema para setear rol
     * @param result una variable en BigDecimal con 0=ninguna y 1=Si esxite
     * @return BigDecimal
     */
    public BigDecimal getValidaFechaVacacion(String empleado, String fechaInic, String cadena, String esquema) {
        //mensaje 3
        System.out.println("Parametros getValidaFechaVacacion(): empleado: "+empleado+", fechaInic: "+fechaInic);
        System.out.println("Entre a getValidaFechaVacacion");
        BigDecimal haySolici = BigDecimal.ZERO;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "select  COUNT(*) "
                       +"FROM NOVEDADESSISTEMA NS WHERE NS.EMPLEADO = ? "
                       +"AND NS.TIPO='VACACION' AND NS.SUBTIPO='TIEMPO' " 
                       +" AND to_date(?, 'dd/mm/yyyy') BETWEEN NS.FECHAINICIALDISFRUTE AND NS.FECHASIGUIENTEFINVACA-1 ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, empleado);
            query.setParameter(2, fechaInic);
            haySolici = (BigDecimal) query.getSingleResult();
            System.out.println("haySolici: "+haySolici);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getValidaFechaVacacion(): " + e);
        } 
        return haySolici;
    }
    /**
     * metodo publico para consultar la fecha sugerida de regreso cuando es menor a 30 dias 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater fechainicio, dias solicitados ,cadena del cliente, Esquema para setear rol
     * @param result una variable en String con la fecha calculada
     * @return String
     */
    public String getFechaSugerida(String fechaInicio, String dias, String cadena, String esquema) {
        //mensaje 4
        System.out.println("Parametros getFechaSugerida(): fechaInicio: "+fechaInicio+", dias: "+dias);
        System.out.println("Entre a getFechaSugerida");
        String fechaSugerida = null;
        String sqlQuery;
        
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT TO_CHAR(TO_DATE(?, 'dd/mm/yyyy') + ? - 1 ,'dd/mm/yyyy' ) FROM DUAL ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, fechaInicio);
            query.setParameter(2, dias);
            fechaSugerida = query.getSingleResult().toString();
            System.out.println("fechaSugerida: "+fechaSugerida);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getFechaSugerida(): " + e);
        } 
        return fechaSugerida;
    }   
    /**
     * metodo publico para consultar la fecha sugerida de regreso 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater fechainicio, dias solicitados ,cadena del cliente, Esquema para setear rol
     * @param result una variable en String con la fecha calculada
     * @return String
     */
    public String getFechaSugerida2(String fechaInicio, String dias, BigDecimal cantDiasBD, String cadena, String esquema) {
        //mensaje 4
        System.out.println("Parametros getFechaSugerida(): fechaInicio: "+fechaInicio+", cantidadDias DB "+ cantDiasBD + ", dias: "+dias);
        System.out.println("Entre a getFechaSugerida2");
        System.out.println("Cantidad de dias bd"+ cantDiasBD);
        String fechaSugerida = null;
        String sqlQuery;
        
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT TO_CHAR(TO_DATE(?, 'dd/mm/yyyy') + ? - ? ,'dd/mm/yyyy') FROM DUAL ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, fechaInicio);
            query.setParameter(2, dias);
            query.setParameter(3, cantDiasBD);
            fechaSugerida = query.getSingleResult().toString();
            System.out.println("fechaSugerida: "+fechaSugerida);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getFechaSugerida2(): " + e);
        } 
        return fechaSugerida;
    }
    /**
     * metodo publico para consultar la fecha sugerida de regreso 
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater fechainicio, dias solicitados ,cadena del cliente, Esquema para setear rol
     * @param result una variable en String con la fecha calculada
     * @return String
     */
    public String getFechaSugerida3(String fechaInicio, String dias, String cadena, String esquema) {
        //mensaje 4
        System.out.println("Parametros getFechaSugerida(): fechaInicio: "+fechaInicio+", dias: "+dias);
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
            System.out.println("fechaSugerida: "+fechaSugerida);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getFechaSugerida3(): " + e);
        } 
        return fechaSugerida;
    }
    /**
     * metodo publico para consultar la Cantidad de dias que hay entre fecha inicio y fecha sugerida
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater fechainicio, Fecha sugerida ,cadena del cliente, Esquema para setear rol
     * @param result una variable en String con la fecha calculada
     * @return String
     */
    public BigDecimal getCalculaDias(String fechaInicio, String fechaSugerida, String cadena, String esquema) {
        //mensaje 4
        System.out.println("Parametros getCalculaDias(): fechaInicio: "+fechaInicio+", fechaSugerida: "+fechaSugerida);
        System.out.println("Entre a getCalculaDias");
        BigDecimal diasCalculados = BigDecimal.ZERO;
        String sqlQuery;
        try {
            setearPerfil(esquema, cadena);
            sqlQuery = "SELECT DIAS360(to_date(?, 'dd/mm/yyyy'), "
                    + "to_date(?, 'dd/mm/yyyy')) "
                    + "FROM DUAL ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, fechaInicio);
            query.setParameter(2, fechaSugerida);
            diasCalculados = (BigDecimal) query.getSingleResult();
            System.out.println("diasCalculados: "+diasCalculados);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getCalculaDias(): " + e);
        } 
        return diasCalculados;
    }
    /*
    Metodo que ejecuta la logica de sugerir fecha
    */
    public JsonObject prueba(String fechaInicio,String dias,String documento,String causa,
            String cadena, String nitEmpleado, String esquema) {
        //Se Crean las variable necesarias 
        System.out.println("Entre a prueba");
        Date fechaContrato = null;
        String tipoTrabajador = null;
        String fechaFin = null;
        String diasTipoTrabador = null;
        String CausaOrigenIncapacidad = null;
        String CausaFormaLiquidacion = null;
        String CausaPorcentajeLiq = null;
        String secCausaBD= null;
        BigDecimal cantidadDiasBD = BigDecimal.ZERO;
        BigDecimal validaFechaAusentismo = BigDecimal.ZERO;
        BigDecimal validaFechaVacacion = BigDecimal.ZERO;
        String msj = null;
        
        //Se ejecutan metodos 
        String secEmpl = getSecuenciaEmplPorSeudonimo(documento, nitEmpleado, cadena);
        fechaContrato = getFechaContrato(secEmpl,fechaInicio, cadena, esquema);
        System.out.println("Sali de getFechaContrato");
        tipoTrabajador = getTipoTrabajador(secEmpl, fechaInicio, cadena, esquema);
        System.out.println("Sali de getTipoTrabajador");
        diasTipoTrabador = getDiasTrabajador(tipoTrabajador, fechaInicio, cadena, esquema);
        System.out.println("Sali de getDiasTrabajador");
        CausaOrigenIncapacidad = getCausaOrigenIncapacidad(causa, cadena, esquema);
        System.out.println("Sali de getCausaOrigen");
        CausaFormaLiquidacion =getCausaFormaLiquidacion(causa, cadena , esquema);
        System.out.println("Sali de getCausaFormaLiquidacion");
        CausaPorcentajeLiq = getCausaPorcentajeLiq(causa, cadena, esquema);
        System.out.println("Sali de getCausaPorcentajeLiq");
        System.out.println(CausaOrigenIncapacidad);
        //System.out.println("CausaFormaLiquidacion 4: " + CausaOrigen.get(0).toString());
        
        Date fechaPrueba = null;
        int diasSolicitados = Integer.parseInt(dias);
        int diasTipoTraj = Integer.parseInt(diasTipoTrabador);
        secCausaBD = getSecuenciaCausa("MA", cadena , esquema);
        //diasSolicitados = Integer.parseInt();
        try {
            fechaPrueba = getDate(fechaInicio, cadena, esquema);
            System.out.println("Se convirtio");
        } catch (NullPointerException ex) {
            Logger.getLogger(kioCausasAusentismosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(kioCausasAusentismosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        Se valida que sí la forma de liquidacion es nula traiga un valor por defecto  para 
        formula de liquidacion y porcentaje de liquidacion 
        */
        if (CausaFormaLiquidacion == null) {
            System.out.println("Entre a validar ");
            if (CausaOrigenIncapacidad == "AT" || CausaOrigenIncapacidad == "EP"
                || CausaOrigenIncapacidad == "EG" || CausaOrigenIncapacidad == "MA") {
                System.out.println("Entre a validar ");
                CausaFormaLiquidacion = "IBC MES ANTERIOR";
                CausaPorcentajeLiq = "100";
            } else {
                System.out.println("Entre a validar2");
                CausaFormaLiquidacion = "BASICO";
                CausaPorcentajeLiq = "100";
            }
        } 
        //System.out.println("causa "+ CausaFormaLiquidacion);
        //System.out.println("porcentaje "+ CausaPorcentajeLiq);
        // se crea la logica para consultar fecha sugerida
        if(fechaContrato.before(fechaPrueba)) {
            validaFechaAusentismo = getValidaFechaAusentismo(secEmpl, fechaInicio, cadena, esquema);
            System.out.println("ausemtismos" +validaFechaAusentismo);
            if (validaFechaAusentismo.signum()>0) {
                System.out.println("Caso 1");
                msj = "Ya existe un registro en ese rango de fechas de ausentismo";
                System.out.println(msj);
                fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                cantidadDiasBD = getCalculaDias(fechaInicio, fechaFin, cadena, esquema);
                System.out.println("Numero de dias "+diasSolicitados);
                System.out.println("diasTipoTrabajador "+diasTipoTraj);
                System.out.println("Causa Origen  "+CausaOrigenIncapacidad);
                if (diasSolicitados>30 && 
                    ((diasTipoTraj==30 && (CausaOrigenIncapacidad != "MA" ||
                                                        CausaOrigenIncapacidad != "EG" ||
                                                        CausaOrigenIncapacidad != "EP" ||
                                                        CausaOrigenIncapacidad != "AT"))
                      ||  (CausaOrigenIncapacidad == "MA" ||
                          CausaOrigenIncapacidad == "EG" ||
                          CausaOrigenIncapacidad == "EP" ||
                          CausaOrigenIncapacidad == "AT")) 
                    ) {
                    System.out.println("si pude validar");
                    //fechaFin = getFechaSugerida2(fechaInicio, dias, cantidadDiasBD, cadena);
                    fechaFin = getFechaSugerida3(fechaInicio, dias, cadena, esquema);
                    if(secCausaBD.equals(causa)) {
                        System.out.println("Si es permiso de Maternidad");
                        fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                    }
                } else {
                    fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                } 
                      
            } else {
                validaFechaVacacion = getValidaFechaVacacion(secEmpl, fechaInicio, cadena, esquema);
                System.out.println("Validacion fecha vacaciones"+validaFechaVacacion);
                if (validaFechaVacacion.signum()>0) {
                    System.out.println("Caso 2");
                    msj = "Ya existe un registro en ese rango de fechas de vacaicones";
                    System.out.println(msj);
                    fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                    cantidadDiasBD = getCalculaDias(fechaInicio, fechaFin, cadena, esquema);
                    System.out.println("Numero de dias "+diasSolicitados);
                    System.out.println("diasTipoTrabajador "+diasTipoTraj);
                    System.out.println("Causa Origen  "+CausaOrigenIncapacidad);
                    if (diasSolicitados>30 && 
                            ((diasTipoTraj==30 && (CausaOrigenIncapacidad != "MA" ||
                                                        CausaOrigenIncapacidad != "EG" ||
                                                        CausaOrigenIncapacidad != "EP" ||
                                                        CausaOrigenIncapacidad != "AT"))
                                ||  (CausaOrigenIncapacidad == "MA" ||
                                  CausaOrigenIncapacidad == "EG" ||
                                  CausaOrigenIncapacidad == "EP" ||
                                  CausaOrigenIncapacidad == "AT")) 
                        ) {
                        System.out.println("si pude validar");
                        //fechaFin = getFechaSugerida2(fechaInicio, dias, cantidadDiasBD, cadena);
                        fechaFin = getFechaSugerida3(fechaInicio, dias, cadena, esquema);
                        if(secCausaBD.equals(causa)) {
                            System.out.println("Si es permiso de Maternidad");
                            fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                        }
                    } else {
                    fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                    }
                } else {
                    System.out.println("Caso 3");
                    msj = "No tiene novedades solicitadas";
                    System.out.println(msj);
                    fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                    cantidadDiasBD = getCalculaDias(fechaInicio, fechaFin, cadena, esquema);
                    System.out.println("Numero de dias "+diasSolicitados);
                    System.out.println("diasTipoTrabajador "+diasTipoTraj);
                    System.out.println("Causa Origen  "+CausaOrigenIncapacidad);
                    if (diasSolicitados>30 && 
                    ((diasTipoTraj==30 && (CausaOrigenIncapacidad != "MA" ||
                                                        CausaOrigenIncapacidad != "EG" ||
                                                        CausaOrigenIncapacidad != "EP" ||
                                                        CausaOrigenIncapacidad != "AT"))
                            ||  (CausaOrigenIncapacidad == "MA" ||
                                CausaOrigenIncapacidad == "EG" ||
                                CausaOrigenIncapacidad == "EP" ||
                                CausaOrigenIncapacidad == "AT")) 
                        ) {
                        System.out.println("si pude validar");
                        //fechaFin = getFechaSugerida2(fechaInicio, dias, cantidadDiasBD, cadena);
                        fechaFin = getFechaSugerida3(fechaInicio, dias, cadena, esquema);
                        if(secCausaBD.equals(causa)) {
                            System.out.println("Si es permiso de Maternidad");
                            fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                        }
                    } else{
                        fechaFin = getFechaSugerida(fechaInicio, dias, cadena, esquema);
                    }
                }
            }
        } else {
            System.out.println("La fecha contrato es menor que la fecha prueba");
        }
        JsonObject json=Json.createObjectBuilder()
                        .add("fechafin", fechaFin)
                        .add("mensaje", msj)
                        .add("porcentajeLiquidaicon", CausaPorcentajeLiq)
                        .add("formaLiquidaicon", CausaFormaLiquidacion)
                    .build();
        return json;
    }
  
    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", cadena: "+cadena);
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
            System.out.println("Error: "+this.getClass().getName()+".getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }
}
