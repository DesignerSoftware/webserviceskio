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

}
