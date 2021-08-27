package co.com.designer.services;

import co.com.designer.kiosko.correo.EnvioCorreo;
import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
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
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author UPC006
 */

@Stateless
@Path("vacacionesPendientes")
public class VwvacaPendientesEmpleadosFacadeREST extends AbstractFacade<VwVacaPendientesEmpleados> {

    public VwvacaPendientesEmpleadosFacadeREST() {
        super(VwVacaPendientesEmpleados.class);
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
   

   /**
     * @desc Devuelve las solicitudes por empleado, dependiendo el estado que se le envie como parametro
     * @params:
     * @param documento - El parametro documento hace relación al documento del empleado
     * @param nitEmpresa - El parametro nitEmpresa hace referencia al nit de la empresa con la que la persona se logueó
     * @param estado - El parametro estado recibe el estado de las solicitudes que requiere consultar
     * @param cadena - El parametro cadena hace referencia a la unidad de persistencia con la que se deben realizar las consultas
     * @return La lista de solicitudes que cumplen el filtro por empleado y ultimo estado de solicitud
     */    
    @GET
    @Path("/solicitudXEstado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudXEstado(@QueryParam("documento") String documento,
            @QueryParam("empresa") String nitEmpresa,
            @QueryParam("estado") String estado,
            @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        System.out.println("Parametros getSolicitudXEstado(): seudonimo: " + documento + ", empresa: " + nitEmpresa + ", estado: " + estado+", cadena: "+cadena);
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String secEmpl = getSecuenciaEmplPorSeudonimo(documento, nitEmpresa, cadena, esquema);
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "select to_char(ks.fechageneracion, 'dd/mm/yyyy') fechacreacion, \n"
                    + "to_char(kn.fechainicialdisfrute, 'dd/mm/yyyy') fechainicio, kn.dias dias, \n"
                    + "to_char(e.fechaprocesamiento,'dd/mm/yyyy') fechaprocesamiento, e.estado,\n"
                    + "to_char(kn.ADELANTAPAGOHASTA, 'dd/mm/yyyy') fechafin, \n"
                    + "to_char(kn.FECHASIGUIENTEFINVACA, 'dd/mm/yyyy') fecharegreso,\n"
                    + "TO_CHAR(v.INICIALCAUSACION, 'dd/mm/yyyy')||' a '||TO_CHAR(v.FINALCAUSACION, 'dd/mm/yyyy') periodocausado, \n"
                    + " e.MOTIVOPROCESA motivoprocesa, "
                    + "DECODE(E.PERSONAEJECUTA, null, \n"
                    + "(select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido \n"
                    + "from personas pei, empleados ei where ei.persona=pei.secuencia and ei.secuencia=e.empleadoejecuta), \n"
                    + "(select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido from personas pei \n"
                    + "where pei.secuencia=e.PERSONAEJECUTA) "
                    + ") empleadoejecuta,  \n"
                    + "e.secuencia secuencia "
                    + "from KioEstadosSolici e, kiosolicivacas ks, kionovedadessolici kn, VwVacaPendientesEmpleados v\n"
                    + "where \n"
                    + "e.kiosolicivaca = ks.secuencia \n"
                    + "and ks.KIONOVEDADSOLICI = kn.secuencia \n"
                    + "and kn.vacacion=v.RFVACACION\n"
                    + "and ks.empleado = ? \n"
                    + "and e.estado = ? \n"
                    + "and e.FECHAPROCESAMIENTO = (select max(ei.FECHAPROCESAMIENTO) \n"
                    + "from KioEstadosSolici ei, kiosolicivacas ksi \n"
                    + "where ei.kioSoliciVaca = ksi.secuencia \n"
                    + "and ksi.secuencia=ks.secuencia) "
                    + "and v.inicialcausacion>= empleadocurrent_pkg.fechavigenciatipocontrato(ks.empleado, sysdate) "
                    + "order by e.fechaProcesamiento DESC";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, estado);
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
            System.out.println("Error: "+this.getClass().getName()+".getSolicitudXEstado(): " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity("").build();
        }
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
            //" AND (t2.EMPLEADOJEFE = (select secuencia from empleados where codigoempleado=?))) \n" +
            //"  AND (t2.EMPLEADOJEFE =(select ei.secuencia from empleados ei, personas pei where ei.persona=pei.secuencia and pei.numerodocumento=?)))"+
            "  AND (t2.EMPLEADOJEFE =?))"+
            "  AND (t0.SECUENCIA = (SELECT MAX(t3.SECUENCIA) FROM KIOSOLICIVACAS t4, KIOESTADOSSOLICI t3 \n" +
            "  WHERE ((t4.SECUENCIA = t2.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIVACA))))) \n" +
            "  AND ((t2.SECUENCIA = t0.KIOSOLICIVACA) AND (t1.SECUENCIA = t2.EMPLEADO))\n" +
            "  AND t1.PERSONA=P.SECUENCIA\n" +
            "  and t2.KIONOVEDADSOLICI = kn.secuencia\n" +
            "  and kn.vacacion=v.RFVACACION\n" +
            "  ) \n" +
            "  ORDER BY t0.FECHAPROCESAMIENTO DESC";
            //Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secEmplJefe);
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
    @Path("/soliciSinProcesarJefe/{nit}/{jefe}/{estado}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSoliciSinProcesarJefe(@PathParam("nit") String nitEmpresa, @PathParam("jefe") String jefe, 
        @PathParam("estado") String estado, @QueryParam("cadena") String cadena) {
        System.out.println("parametros getSoliciSinProcesarJefe(): nit: "+nitEmpresa+ " jefe "+jefe+" estado: "+estado+ " cadena "+cadena);
        List s = null;
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        try {
        String secuenciaJefe = getSecuenciaEmplPorSeudonimo(jefe, nitEmpresa, cadena, esquema);
        String secuenciaEmpresa = getSecuenciaPorNitEmpresa(nitEmpresa, cadena, esquema);
        //String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String sqlQuery="   SELECT \n" +
        " t1.codigoempleado documento, REPLACE(TRIM(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE), '  ', ' ') NOMBRE,\n" +
        " t0.SECUENCIA, \n" +
        " TO_CHAR(t0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:MI:SS') SOLICITUD, \n" +
        " TO_CHAR(KNS.FECHAINICIALDISFRUTE,'DD/MM/YYYY' ) INICIALDISFRUTEM," + 
        " TO_CHAR(T0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:MI:SS') FECHAULTMODIF,\n" +
        " t0.ESTADO, \n" +
        " t0.MOTIVOPROCESA, t0.NOVEDADSISTEMA, t0.EMPLEADOEJECUTA, t0.PERSONAEJECUTA, t0.KIOSOLICIVACA,\n" +
        " TO_CHAR(KNS.ADELANTAPAGOHASTA, 'DD/MM/YYYY') FECHAFIN,\n" +
        " TO_CHAR(KNS.FECHASIGUIENTEFINVACA,'DD/MM/YYYY') FECHAREGRESO,\n" +
        " KNS.DIAS,\n" +
        " V.INICIALCAUSACION||' a '||V.FINALCAUSACION PERIODOCAUSADO,\n" +
        " (SELECT PER.PRIMERAPELLIDO||' '||PER.SEGUNDOAPELLIDO||' '||PER.NOMBRE FROM PERSONAS PER, EMPLEADOS EMPL\n" +
        " WHERE EMPL.PERSONA=PER.SECUENCIA\n" +
        " AND EMPL.SECUENCIA=JEFE.SECUENCIA) EMPLEADOJEFE,\n" +        
        " KNS.FECHAPAGO FECHAPAGO,\n"+
        " t0.secuencia secuencia" +
        " FROM \n" +
        " KIOESTADOSSOLICI t0, \n" +
        " KIOSOLICIVACAS t2, \n" +
        " EMPLEADOS t1, \n" +   
        " PERSONAS P,\n" +
        " KIONOVEDADESSOLICI KNS,\n" +
        " VwVacaPendientesEmpleados V, \n" +
        " EMPLEADOS JEFE\n" +
        " WHERE \n" +
        " (((((t1.EMPRESA = ?) AND (t0.ESTADO = ?)) AND (t2.EMPLEADOJEFE =?)) \n" +
        " AND (t0.SECUENCIA = (SELECT MAX(t3.SECUENCIA) FROM KIOSOLICIVACAS t4, KIOESTADOSSOLICI t3 \n" +
        " WHERE ((t4.SECUENCIA = t2.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIVACA))))) \n" +
        " AND ((t2.SECUENCIA = t0.KIOSOLICIVACA) AND (t1.SECUENCIA = t2.EMPLEADO))) \n" +
        " AND T1.PERSONA=P.SECUENCIA\n" +
        " AND t2.KIONOVEDADSOLICI=KNS.SECUENCIA\n" +
        " AND KNS.VACACION=v.RFVACACION\n" +
        " AND t2.EMPLEADOJEFE=JEFE.SECUENCIA\n" +        
        " ORDER BY t0.FECHAPROCESAMIENTO DESC\n" ;
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secuenciaEmpresa);
            query.setParameter(2, estado);
            query.setParameter(3, secuenciaJefe);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error "+this.getClass().getName()+".getSoliciSinProcesarJefe: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }    
    
    /**
     * Devuelve las solicitudes pendientes por procesar, relacionadas al kioautorizador
     * @params:
     * @param seudonimo - El parametro seudonimo hace relación al seudonimo del kioautorizador
     * @param nitEmpresa - El parametro nitEmpresa hace referencia al nit de la empresa con la que la persona se logueó
     * @param estado - El parametro estado recibe el estado de las solicitudes que requiere consultar
     * @param cadena - El parametro cadena hace referencia a la unidad de persistencia con la que se deben realizar las consultas
     * @return El número de ítems (números aleatorios) de que consta la serie
     */
    @GET
    @Path("/solicitudesXAutorizador")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudesXAutorizador(@QueryParam("usuario") String seudonimo, @QueryParam("empresa") String nitEmpresa, 
            @PathParam("estado") String estado,
            @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String secSecPerAutorizador = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        System.out.println("Webservice: solicitudesXEmpleadoJefe Parametros: usuario: secPersonaAutorizador: " + secSecPerAutorizador + ", empresa: " + nitEmpresa);
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT \n"
                    + "t3.CODIGOEMPLEADO, P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE NOMBRECOMPLETO,\n"
                    + "to_char(t4.FECHAGENERACION, 'DD/MM/YYYY HH:mm:ss') solicitud,  \n"
                    + "to_char(kn.FECHAINICIALDISFRUTE, 'DD/MM/YYYY HH:mm:ss') FECHAINICIALDISFRUTE,\n"
                    + "to_char(t1.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:mm:ss') FECHAPROCESAMIENTO,\n"
                    + "t1.SECUENCIA, NVL(t1.MOTIVOPROCESA, 'N/A'),  \n"
                    + "to_char(kn.ADELANTAPAGOHASTA, 'DD/MM/YYYY HH:mm:ss') FECHAFINVACACIONES,\n"
                    + "to_char(kn.fechaSiguienteFinVaca, 'DD/MM/YYYY HH:mm:ss') fecharegresolaborar,\n"
                    + "kn.dias,\n"
                    + "TO_CHAR(v.INICIALCAUSACION, 'DD/MM/YYYY')||' a '||TO_CHAR(v.FINALCAUSACION, 'DD/MM/YYYY') periodo,\n"
                    + "(select pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre from personas pei, empleados ei \n"
                    + "where pei.secuencia=ei.persona and t4.EMPLEADOJEFE=ei.secuencia) empleadojefe, TO_CHAR(kn.FECHAPAGO, 'DD/MM/YYYY') FECHAPAGO,\n"
                    + "t1.ESTADO ESTADO \n"
                    + "FROM KIOSOLICIVACAS t4, EMPLEADOS t3, PERSONAS P,\n"
                    + "EMPRESAS t2, KIOESTADOSSOLICI t1, EMPRESAS t0, kionovedadessolici kn,  VwVacaPendientesEmpleados v\n"
                    + "WHERE (((((t2.SECUENCIA = t0.SECUENCIA) AND (t1.ESTADO = 'ENVIADO')) AND (t4.AUTORIZADOR = ?)) \n"
                    + "AND (t1.SECUENCIA = (SELECT MAX(t5.SECUENCIA) FROM KIOESTADOSSOLICI t5, KIOSOLICIVACAS t6 \n"
                    + "WHERE ((t6.SECUENCIA = t4.SECUENCIA) AND (t6.SECUENCIA = t5.KIOSOLICIVACA))))) \n"
                    + "AND (((t4.SECUENCIA = t1.KIOSOLICIVACA) AND (t3.SECUENCIA = t4.EMPLEADO)) AND (t2.SECUENCIA = t3.EMPRESA))) \n"
                    + "AND t3.PERSONA=P.SECUENCIA\n"
                    + "and t4.KIONOVEDADSOLICI = kn.secuencia\n"
                    + "and kn.vacacion=v.RFVACACION";
            //Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secSecPerAutorizador);
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
    
    /**
     * Devuelve las solicitudes pendientes por procesar (la que en su último estado es 'ENVIADO'), relacionadas al kioautorizador
     * @params:
     * @param seudonimo - El parametro seudonimo hace relación al seudonimo del kioautorizador
     * @param nitEmpresa - El parametro nitEmpresa hace referencia al nit de la empresa con la que la persona se logueó
     * @param cadena - El parametro cadena hace referencia a la unidad de persistencia con la que se deben realizar las consultas
     * @return El número de ítems (números aleatorios) de que consta la serie
     */    
    @GET
    @Path("/soliciSinProcesarAutorizador")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSoliciSinProcesarAutorizador(@QueryParam("usuario") String seudonimo, @QueryParam("empresa") String nitEmpresa,  @QueryParam("cadena") String cadena) {
        System.out.println("parametros getSoliciSinProcesarJefe(): nit: " + nitEmpresa + " seudonimo autorizador: " + seudonimo + " cadena " + cadena);
        List s = null;
        try {
            String esquema = null;
            try {
                esquema = getEsquema(nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar esquema " + e.getMessage());
            }
            String secPerAutorizador = getSecPersonaPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = " SELECT\n"
                    + " t3.codigoempleado documento, REPLACE(TRIM(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE), '  ', ' ') NOMBRE,\n"
                    + "t3.SECUENCIA,\n"
                    + "TO_CHAR(t1.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:MI:SS') SOLICITUD, \n"
                    + "TO_CHAR(KNS.FECHAINICIALDISFRUTE,'DD/MM/YYYY' ) INICIALDISFRUTEM,\n"
                    + "TO_CHAR(t1.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:MI:SS') FECHAULTMODIF,\n"
                    + "t1.ESTADO, \n"
                    + "t1.MOTIVOPROCESA, t1.NOVEDADSISTEMA, t1.EMPLEADOEJECUTA, t1.PERSONAEJECUTA, t1.KIOSOLICIVACA,\n"
                    + "TO_CHAR(KNS.ADELANTAPAGOHASTA, 'DD/MM/YYYY') FECHAFIN,\n"
                    + "TO_CHAR(KNS.FECHASIGUIENTEFINVACA,'DD/MM/YYYY') FECHAREGRESO,\n"
                    + "KNS.DIAS,\n"
                    + "V.INICIALCAUSACION||' a '||V.FINALCAUSACION PERIODOCAUSADO,\n"
                    + "(SELECT PER.PRIMERAPELLIDO||' '||PER.SEGUNDOAPELLIDO||' '||PER.NOMBRE FROM PERSONAS PER\n"
                    + "WHERE P.SECUENCIA=T4.AUTORIZADOR) AUTORIZADOR,\n"
                    + "KNS.FECHAPAGO FECHAPAGO,\n"
                    + "t1.secuencia secuencia\n"
                    + " FROM KIOSOLICIVACAS t4, EMPLEADOS t3, PERSONAS P, PERSONAS AUTORIZADOR,\n"
                    + "EMPRESAS t2, KIOESTADOSSOLICI t1, EMPRESAS t0, kionovedadessolici KNS,  VwVacaPendientesEmpleados v\n"
                    + "WHERE (((((t2.SECUENCIA = t0.SECUENCIA) AND (t1.ESTADO = 'ENVIADO')) AND (t4.AUTORIZADOR = ?)) \n"
                    + "AND (t1.SECUENCIA = (SELECT MAX(t5.SECUENCIA) FROM KIOESTADOSSOLICI t5, KIOSOLICIVACAS t6 \n"
                    + "WHERE ((t6.SECUENCIA = t4.SECUENCIA) AND (t6.SECUENCIA = t5.KIOSOLICIVACA))))) \n"
                    + "AND (((t4.SECUENCIA = t1.KIOSOLICIVACA) AND (t3.SECUENCIA = t4.EMPLEADO)) AND (t2.SECUENCIA = t3.EMPRESA))) \n"
                    + "AND t3.PERSONA=P.SECUENCIA "
                    + "and t4.KIONOVEDADSOLICI = KNS.secuencia "
                    + "and KNS.vacacion=v.RFVACACION\n"
                    + "and t4.AUTORIZADOR=AUTORIZADOR.SECUENCIA "
                    + " ORDER BY t1.FECHAPROCESAMIENTO DESC ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPerAutorizador);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getSoliciSinProcesarAutorizador: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }  
    
    @GET
    @Path("/consultarPeriodosPendientesEmpleado")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List consultarPeriodosPendientesEmpleado(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) throws Exception {
        System.out.println("Parametros consultarPeriodosPendientesEmpleado(): seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", cadena: "+cadena);
        List<VwVacaPendientesEmpleados> periodosPendientes = null;
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        Query query = null;
        //String consulta = "select KIOVACACIONES_PKG.DIASDISPOPER(vw.rfVacacion), vw.rfVacacion, vw.empleado, vw.inicialCausacion, vw.finalCausacion, vw.diasPendientes from VwVacaPendientesEmpleados vw where vw.diasPendientes > 0 and vw.empleado.codigoempleado = :codEmple ";
        String consulta="SELECT \n" +
        "VW.RFVACACION, KIOVACACIONES_PKG.DIASDISPOPER(VW.RFVACACION) DIASPENDIENTES, VW.DIASPENDIENTES DIASPENDIENTESREALES, \n" +
        "VW.FINALCAUSACION, VW.INICIALCAUSACION, TO_CHAR(VW.INICIALCAUSACION, 'dd/mm/yyyy')||' a '||TO_CHAR(VW.FINALCAUSACION, 'dd/mm/yyyy') PERIODO \n" +
        "FROM \n" +
        "VWVACAPENDIENTESEMPLEADOS VW, EMPLEADOS E\n" +
        "WHERE \n" +
        "VW.EMPLEADO=E.SECUENCIA AND\n" +
        //"((DIASPENDIENTES > 0) AND (E.CODIGOEMPLEADO = ?))";
        "((DIASPENDIENTES > 0) AND (E.SECUENCIA = ?)) AND VW.INICIALCAUSACION>=empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate)";
        try {
            BigDecimal secuenciaEmpl = new BigDecimal(secEmpl);
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secuenciaEmpl);
            periodosPendientes = query.getResultList();
            return periodosPendientes;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia.");
            System.out.println(this.getClass().getName() + "." + "consultarPeriodosPendientesEmpleado" + "()");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
            System.out.println(this.getClass().getName() + "." + "consultarPeriodosPendientesEmpleado" + "()");
//            throw new Exception(npee.toString());
            return null;
        } catch (Exception e) {
            System.out.println("Error general." + e);
            System.out.println(this.getClass().getName() + "." + "consultarPeriodosPendientesEmpleado" + "()");
            throw new Exception(e.toString());
        }
    }
    
    @GET
    @Path("/consultarPeriodoMasAntiguo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<VwVacaPendientesEmpleados> consultarPeriodoMasAntiguo(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        System.out.println("Parametros consultarPeriodoMasAntiguo(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        List<VwVacaPendientesEmpleados> retorno = null;
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        String consulta = "select vw.* \n"
                + "from VwVacaPendientesEmpleados vw \n"
                + "where vw.empleado = ? \n"
                + "and vw.inicialCausacion = ( \n"
                + "    select min( vwi.inicialcausacion ) \n"
                + "    from ( \n"
                + "    SELECT N.VACACION, SUM(N.DIAS) SUMADIAS \n"
                + "    FROM KIONOVEDADESSOLICI N, KIOSOLICIVACAS S, KIOESTADOSSOLICI E \n"
                + "    WHERE N.SECUENCIA = S.KIONOVEDADSOLICI \n"
                + "    AND S.SECUENCIA = E.KIOSOLICIVACA \n"
                + "    AND E.ESTADO IN ('GUARDADO', 'ENVIADO', 'AUTORIZADO', 'LIQUIDADO' ) \n"
                + "    AND E.SECUENCIA = (SELECT MAX(EI.SECUENCIA) \n"
                + "    FROM KIOESTADOSSOLICI EI \n"
                + "    WHERE EI.KIOSOLICIVACA = E.KIOSOLICIVACA) \n"
                + "    AND NOT EXISTS (SELECT 'x' \n"
                + "      FROM SOLUCIONESNODOS SN, SOLUCIONESFORMULAS SF, DETALLESNOVEDADESSISTEMA DNS \n"
                + "      WHERE SN.SECUENCIA = SF.solucionnodo \n"
                + "      AND SF.novedad = DNS.novedad \n"
                + "      AND DNS.novedadsistema=E.NOVEDADSISTEMA) \n"
                + "      GROUP BY N.VACACION \n"
                + "    ) t, VwVacaPendientesEmpleados VWI \n"
                + "    where vwi.inicialCausacion >= empleadocurrent_pkg.fechatipocontrato(vwi.empleado, sysdate) \n"
                + "    AND VWI.EMPLEADO = vw.empleado \n"
                //+ "    AND (VWI.DIASPENDIENTES - NVL(T.SUMADIAS,0)) <> 0 \n"
                + "    AND (VWI.DIASPENDIENTES - NVL(T.SUMADIAS,0)) > 0 \n"
                + "    AND VWI.rfvacacion = t.VACACION(+) \n"
                + ") ";
        try {
            Query query = getEntityManager(cadena).createNativeQuery(consulta, VwVacaPendientesEmpleados.class);
            query.setParameter(1, secEmpl);
            retorno = query.getResultList();
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + e);
        }
        return retorno;
    }
    
    @GET
    @Path("/consultarDiasPendientesPerMasAntiguo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasPendientesPerMasAntiguo(@QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        System.out.println(this.getClass().getName() + "." + "consultarPeriodoMasAntiguo(): seudonimo: " + seudonimo + ", nitempresa: " + nitEmpresa + ", cadena: " + cadena);
        BigDecimal retorno = new BigDecimal(BigInteger.ZERO);
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        // String consulta = "select vw.diasPendientes "
        String consulta = "select KIOVACACIONES_PKG.DIASDISPOPER(vw.rfVacacion) diaspendientes \n"
                + "from VwVacaPendientesEmpleados vw \n"
                + "where vw.empleado = ? \n"
                + "and vw.inicialCausacion = ( \n"
                + "    select min( vwi.inicialcausacion ) \n"
                + "    from ( \n"
                + "    SELECT N.VACACION, SUM(N.DIAS) SUMADIAS \n"
                + "    FROM KIONOVEDADESSOLICI N, KIOSOLICIVACAS S, KIOESTADOSSOLICI E \n"
                + "    WHERE N.SECUENCIA = S.KIONOVEDADSOLICI \n"
                + "    AND S.SECUENCIA = E.KIOSOLICIVACA \n"
                + "    AND E.ESTADO IN ('GUARDADO', 'ENVIADO', 'AUTORIZADO', 'LIQUIDADO' ) \n"
                + "    AND E.SECUENCIA = (SELECT MAX(EI.SECUENCIA) \n"
                + "    FROM KIOESTADOSSOLICI EI \n"
                + "    WHERE EI.KIOSOLICIVACA = E.KIOSOLICIVACA) \n"
                + "    AND NOT EXISTS (SELECT 'x' \n"
                + "      FROM SOLUCIONESNODOS SN, SOLUCIONESFORMULAS SF, DETALLESNOVEDADESSISTEMA DNS \n"
                + "      WHERE SN.SECUENCIA = SF.solucionnodo \n"
                + "      AND SF.novedad = DNS.novedad \n"
                + "      AND DNS.novedadsistema=E.NOVEDADSISTEMA) \n"
                + "      GROUP BY N.VACACION \n"
                + "    ) t, VwVacaPendientesEmpleados VWI \n"
                + "    where vwi.inicialCausacion >= empleadocurrent_pkg.fechatipocontrato(vwi.empleado, sysdate) \n"
                + "    AND VWI.EMPLEADO = vw.empleado \n"
               // + "    AND (VWI.DIASPENDIENTES - NVL(T.SUMADIAS,0)) <> 0 \n"
                + "    AND (VWI.DIASPENDIENTES - NVL(T.SUMADIAS,0)) > 0 \n"
                + "    AND VWI.rfvacacion = t.VACACION(+) \n"
                + ") ";
        try {
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            retorno = (BigDecimal) query.getSingleResult();
            System.out.println("Dias pendientes periodo más antiguo: " + retorno);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".consultarDiasPendientesPerMasAntiguo." + e);
        }
        return retorno;
    }

    @GET
    @Path("/consultarDiasVacacionesProvisionados")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasVacacionesProvisionados(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        System.out.println("Parametros consultarDiasVacacionesProvisionado(): seudonimo: " + seudonimo + ", nitempresa: " + nitEmpresa + " cadena: " + cadena);
        BigDecimal retorno = BigDecimal.ZERO;
        //String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        String consulta = " select "
                + "round(sn.unidades,2) dias "
                + "from conexioneskioskos ck, empleados empl, personas per, "
                + "solucionesnodos sn, conceptos c, empresas em "
                + "where ck.empleado=empl.secuencia "
                + "and em.secuencia = empleadocurrent_pkg.EmpresaEmpleado(empl.secuencia, sysdate) "
                + "and empl.persona=per.secuencia "
                + "and sn.empleado= empl.secuencia "
                + "and sn.concepto = c.secuencia "
                + "and c.codigo = conceptosreservados_pkg.capturarcodigoconcepto(13,em.codigo) "
                + "and sn.fechapago=(select max(sni.fechapago) "
                + "                  from solucionesnodos sni, conceptos ci "
                + "                  where sni.concepto=ci.secuencia "
                + "				          and ci.codigo=conceptosreservados_pkg.capturarcodigoconcepto(13,em.codigo) "
                + "                  and sni.empleado=sn.empleado "
                + "				          and sni.concepto=sn.concepto "
                + "				          and sni.fechapago <= SYSDATE) "
                + "and empl.secuencia= ? ";
        try {
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".consultarDiasVacacionesProvisionados() :" + e);
        }
        return retorno;
    }
 
    /**
     * Devuelve el total de dias pendientes de vacaciones (periodos cumplidos)
     * @param seudonimo El parametro seudonimo es el usuario 
     * @param nitEmpresa El parametro nitEmpresa es el nit de la empresa del empleado
     * @param cadena El parametro cadena es el nombre de la persistencia de bd
     * @return dias de vacaciones pendientes de periodos cumplidos
     */
    @GET
    @Path("/consultarDiasVacacionesPeriodosCumplidos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasVacacionesPeriodosCumplidos(@QueryParam("usuario") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        System.out.println(this.getClass().getName() + "." + "consultarDiasVacacionesPeriodosCumplidos(): seudonimo: " + seudonimo + ", nitempresa: " + nitEmpresa + ""
                + "\n cadena: " + cadena);
        BigDecimal retorno = BigDecimal.ZERO;
        //String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        String consulta = "select "
                + "nvl(sum(v.diaspendientes), 0) diasPendientes "
                + "from VWVACAPENDIENTESEMPLEADOS  v, empleados e "
                + "where e.secuencia=v.empleado "
                + "and inicialcausacion>=empleadocurrent_pkg.fechavigenciaTipoContrato(e.secuencia, sysdate) "
                + "and e.secuencia=? "
                + "group by e.secuencia";
        try {
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".consultarDiasVacacionesPeriodosCumplidos() :" + e);
        }
        return retorno;
    } 
    
    // Obtiene la cantidad de días solicitados filtrando por el último estado de la solicitud
    // Si no se coloca un parametro trae el total de dias sin tener en cuenta el estado
    @GET
    @Path("/getDiasSoliciVacacionesXUltimoEstado")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasVacacionesSolicitados(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("estado") String estado,
            @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        System.out.println(this.getClass().getName() + "." + "consultarDiasVacacionesSolicitados" + "()");
        BigDecimal retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        retorno = getDiasVacacionesSolicitados(documento, nitEmpresa, estado, cadena, esquema);
        return retorno;
    }
    
    public BigDecimal getDiasVacacionesSolicitados(String documento, String nitEmpresa, String estado, String cadena, String esquema) {
        System.out.println("Parametros getDiasVacacionesSolicitados(): documento: "+documento+", nitEmpresa: "+nitEmpresa+", estado: "+estado+", cadena: "+cadena);
        BigDecimal retorno = BigDecimal.ZERO;
        String consulta = "select "
                + "nvl(sum(kn.dias), 0) dias "
                + "from KioEstadosSolici e,  kiosolicivacas ks, kionovedadessolici kn, VwVacaPendientesEmpleados v "
                + "where "
                + "e.kiosolicivaca = ks.secuencia "
                + "and ks.KIONOVEDADSOLICI = kn.secuencia "
                + "and kn.vacacion=v.RFVACACION "
                + "and ks.empleado = (select ei.secuencia from empleados ei, personas pei, empresas em where ei.persona=pei.secuencia \n"
                + "                  and ei.empresa=em.secuencia and em.nit=? "
                + "                  and pei.numerodocumento=?) "
                + "and e.secuencia = (select max(ei.secuencia) "
                + "from KioEstadosSolici ei, kiosolicivacas ksi "
                + "where ei.kioSoliciVaca = ksi.secuencia "
                + "and ksi.secuencia=ks.secuencia) ";
        if (estado != null) {
            consulta += "and e.estado=? ";
        }
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, documento);
            if (estado != null) {
                query.setParameter(3, estado);
            }
            retorno = (BigDecimal) query.getSingleResult();
            System.out.println("resultados dias estado " + estado + ": " + retorno);
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".getDiasVacacionesSolicitados()." + e);
        }
        return retorno;
    }

    @GET
    @Path("/consultaFechaUltimoPago")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Timestamp consultaFechaUltimoPago(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        System.out.println("Parametros consultaFechaUltimoPago(): usuario: "+seudonimo+", nitempresa: "+nitEmpresa+", cadena: "+cadena);
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
        System.out.println(this.getClass().getName() + "." + "consultaFechaUltimoPago" + "()");
        Timestamp retorno = null;
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        String consulta = "SELECT GREATEST(\n"
                + "                CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1), "
                + "                NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 80), CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO( "
                //+ "                (select secuencia from empleados where codigoempleado=?) "
                + "                ? "
                + "                , 1) "
                + "            )) "
                + "            FROM DUAL ";
        try {
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, secEmpl);
            query.setParameter(3, secEmpl);
            retorno = (Timestamp) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error consultaFechaUltimoPago." + e);
        }
        return retorno;
    }
    
 /*   @GET
    @Path("/verificaExistenciaSolicitud")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal verificaExistenciaSolicitud(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("fechainicio") String fechainicio) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "verificaExistenciaSolicitud" + "()");
        BigDecimal retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        String consulta = "SELECT "
                + "       KIOVACACIONES_PKG.VERIFICAEXISTESOLICITUD((select secuencia from empleados where codigoempleado=?), "
                + "       to_date('?','yyyy-mm-dd') ) "
                + "       FROM DUAL          ";
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, documento);
            query.setParameter(2, fechainicio);
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error consultaFechaUltimoPago." + e);
        }
        return retorno;
    }  */

/*    @GET
    @Path("/consultaTraslapamientos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultaTraslapamientos(EntityManager em, BigDecimal secEmpleado, Date fechaIniVaca, Date fechaFinVaca) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "consultaTraslapamientos" + "()");
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICARTRASLAPAMIENTO(?, ? , ? ) "
                + "FROM DUAL ";
        Query query = null;
        BigDecimal contTras = null;
        try {
            query = em.createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechaIniVaca, TemporalType.DATE);
            query.setParameter(3, fechaFinVaca, TemporalType.DATE);
            contTras = (BigDecimal) (query.getSingleResult());
            System.out.println("Resultado consulta traslapamiento: " + contTras);
            return contTras;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia en consultaTraslapamientos.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en consultaTraslapamientos");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general en consultaTraslapamientos. " + e);
            throw new Exception(e.toString());
        }
    }
*/
    
    
    @GET
    @Path("/calculaFechaRegreso")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List calculaFechaRegreso(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("fechainicio") String fechainicio,
            @QueryParam("dias") int dias,
            @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        System.out.println(this.getClass().getName() + "." + "calculaFechaRegreso" + "()");
        List retorno = getFechaRegreso(fechainicio, dias, seudonimo, nitEmpresa, cadena, esquema);
        return retorno;
    }
    
    @GET
    @Path("/getSolicitudesProcesadasXAutorizador")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudesProcesadasAutorizador(@QueryParam("autorizador") String autorizador,
            @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        int conteo = 0;
        List s = null;
        System.out.println("Webservice: getSolicitudesProcesadasAutorizador Parametros: autorizador: " + autorizador + ", empresa: " + nitEmpresa + ", cadena: " + cadena);
        try {
            String esquema = null;
            try {
                esquema = getEsquema(nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar esquema " + e.getMessage());
            }
            String secPerAutorizador = getSecPersonaPorSeudonimo(autorizador, nitEmpresa, cadena, esquema);
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT\n"
                    + "t1.CODIGOEMPLEADO cedula,\n"
                    + "p.numerodocumento cedulaP,\n"
                    + "p.PRIMERAPELLIDO||' '||p.SEGUNDOAPELLIDO||' '||p.NOMBRE NOMBRECOMPLETO,\n"
                    + "to_char(t2.FECHAGENERACION, 'DD/MM/YYYY HH:mm:ss') solicitud,\n"
                    + "to_char(kn.FECHAINICIALDISFRUTE, 'DD/MM/YYYY HH:mm:ss') FECHAINICIALDISFRUTE,\n"
                    + "to_char(T0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:mm:ss') FECHAPROCESAMIENTO,\n"
                    + "t0.SECUENCIA,  \n"
                    + "NVL(t0.MOTIVOPROCESA, 'N/A') motivo,\n"
                    + "to_char(kn.ADELANTAPAGOHASTA, 'DD/MM/YYYY HH:mm:ss') FECHAFINVACACIONES,\n"
                    + "to_char(kn.fechaSiguienteFinVaca, 'DD/MM/YYYY HH:mm:ss') fecharegresolaborar,\n"
                    + "kn.dias,\n"
                    + "pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre personaAuto,\n"
                    + "TO_CHAR(kn.FECHAPAGO, 'DD/MM/YYYY') FECHAPAGO,\n"
                    + "t0.ESTADO ESTADO,\n"
                    + "TO_CHAR(v.INICIALCAUSACION, 'DD/MM/YYYY')||' a '||TO_CHAR(v.FINALCAUSACION, 'DD/MM/YYYY') periodo\n"
                    + "FROM KIOESTADOSSOLICI t0, KIOSOLICIVACAS t2, EMPLEADOS t1, PERSONAS P, kionovedadessolici kn, personas pei,VwVacaPendientesEmpleados v\n"
                    + "WHERE  \n"
                    + "( t0.ESTADO IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO'))\n"
                    + "AND (t0.SECUENCIA = (SELECT MAX(t3.SECUENCIA) FROM KIOSOLICIVACAS t4, KIOESTADOSSOLICI t3\n"
                    + "WHERE ((t4.SECUENCIA = t2.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIVACA))))\n"
                    + "AND ((t2.SECUENCIA = t0.KIOSOLICIVACA) AND (t1.SECUENCIA = t2.EMPLEADO))\n"
                    + "AND t1.PERSONA = P.SECUENCIA\n"
                    + "and t2.KIONOVEDADSOLICI = kn.secuencia\n"
                    + "and kn.vacacion=v.RFVACACION\n"
                    + "and (t2.EMPLEADOJEFE = pei.secuencia or t2.autorizador = pei.secuencia)\n"
                    + "and t2.autorizador = ? \n"
                    + "order by T0.FECHAPROCESAMIENTO desc";
            //Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPerAutorizador);
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
            System.out.println("Error: "+this.getClass().getName()+"getSolicitudesProcesadasAutorizador()"+ ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity("").build();
        }
    }
    
    @GET
    @Path("/consultaNombreAutorizaVacaciones")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response consultarAutorizaVacaciones(
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
        String secPerKioAutorizador = null;
        String secEmplJefe = null;
        try {
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            if (secEmpleado!=null){
                 secPerKioAutorizador = consultarSecuenciaPerAutorizador(secEmpleado, nitEmpresa, cadena, esquema);
                 retorno = secPerKioAutorizador;
                 if (secPerKioAutorizador!=null) {
                    // Existe relación con kioautorizadores
                    retorno =getApellidoNombreXSecPer(secPerKioAutorizador, nitEmpresa, cadena, esquema);
                 } else {
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
    
    /*
     * Método que devuelve el nombre de la persona de acuerdo a la secuencia de la tabla personas
     * @param secPersona secuencia de la tabla personas
     * @return el apellido||' '||nombre de la persona
     */    
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
        
    
    public List getFechaRegreso(String fechainicio, int dias, String seudonimo, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getFechaRegreso(): seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", fechainicio: "+fechainicio+", dias: "+dias+", cadena: "+cadena);
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        List retorno = null;
        String consulta = "SELECT \n"
                + "TO_CHAR(KIOVACACIONES_PKG.CALCULARFECHAFINVACA( ?, TO_DATE(?, 'YYYY-MM-DD') , \n"
                + "KIOVACACIONES_PKG.CALCULARFECHAREGRESO( ? , TO_DATE(?, 'YYYY-MM-DD') , ? ) , 'S' ), 'DD/MM/YYYY') FECHAFIN,\n"
                + "TO_CHAR(KIOVACACIONES_PKG.CALCULARFECHAREGRESO( ? , TO_DATE(?, 'YYYY-MM-DD') , ? ), 'DD/MM/YYYY') FECHAREGRESO\n"
                + "FROM DUAL ";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechainicio);
            query.setParameter(3, secEmpl);
            query.setParameter(4, fechainicio);
            query.setParameter(5, dias);
            query.setParameter(6, secEmpl);
            query.setParameter(7, fechainicio);
            query.setParameter(8, dias);
            retorno = query.getResultList();
        } catch (Exception e) {
            System.out.println("Error getFechaRegreso." + e);
        }
        return retorno;
    }
    
    @GET
    @Path("/calculaFechaFinVaca")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Timestamp calculaFechaFinVaca(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("fechainicio") String fechainicio,
            @QueryParam("dias") int dias,
            @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema=getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema "+e.getMessage());
        }
        System.out.println(this.getClass().getName() + "." + "calculaFechaFinVaca" + "()");
        Timestamp retorno = getFechaFinVaca(fechainicio, getFechaRegreso(fechainicio, dias, seudonimo, nitEmpresa, cadena, esquema).toString(), dias, seudonimo, nitEmpresa, cadena, esquema);
        return retorno;
    }  
    
    public Timestamp getFechaFinVaca(String fechainicio, String fechafin, int dias, String seudonimo, String nitEmpresa, String cadena, String esquema) {
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        Timestamp retorno = null;
        String consulta = "SELECT KIOVACACIONES_PKG.CALCULARFECHAFINVACA( ?, TO_DATE(?, 'YYYY-MM-DD') , TO_DATE(?,'YYYY-MM-DD HH:MM:SS') , 'S' ) FROM DUAL";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechainicio);
            query.setParameter(3, fechafin);
            retorno = (Timestamp) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error getFechaFinVaca." + e);
        }
        return retorno;
    }
       
    public boolean creaKioNovedadSolici(String seudonimo, String nitEmpresa, String fechainicial, String fecharegreso, String dias, String RFVACACION, String fechaFin, String cadena, String esquema) {
        int conteo = 0;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            System.out.println("parametros creaKioNovedadSolici seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", fechainicial: " + fechainicial + ", fecharegreso: " + fecharegreso + " fecha fin: " + fechaFin + " dias: " + dias + ", rfvacacion: " + RFVACACION);
            String sql = "INSERT INTO KIONOVEDADESSOLICI (EMPLEADO, FECHAINICIALDISFRUTE, DIAS, TIPO, SUBTIPO, FECHASISTEMA, FECHASIGUIENTEFINVACA, ESTADO, \n"
                    + "ADELANTAPAGO, ADELANTAPAGOHASTA, FECHAPAGO, PAGADO, VACACION)\n"
                    + "VALUES\n"
                    + "(?, TO_DATE(?,'DD/MM/YYYY'), ?, 'VACACION', 'TIEMPO', SYSDATE, TO_DATE(?,'DD/MM/YYYY'), 'ABIERTO', ?, TO_DATE(?,'DD/MM/YYYY'), ?, 'N', ?)";
            Query query = getEntityManager(cadena).createNativeQuery(sql);
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechainicial);
            query.setParameter(3, dias);
            query.setParameter(4, fecharegreso);
            query.setParameter(5, null);
            query.setParameter(6, fechaFin);
            query.setParameter(7, null);
            query.setParameter(8, RFVACACION);
            conteo = query.executeUpdate();
            System.out.println("return creaKioNovedadSolici(): " + conteo);
            return conteo > 0;
        } catch (Exception e) {
            System.out.println("Error creaKioNovedadSolici: " + e.getMessage());
            return false;
        }
    }
    
    public boolean creaKioSoliciVacas(String seudonimo, String secEmplJefe, String secPersonaAutorizador, String nit, String secNovedad, String fechaGeneracion, String cadena, String esquema) {
        System.out.println("Parametros creaKioSoliciVacas(): seudonimo: "+seudonimo+", nit: "+nit+", secNovedad: "+secNovedad+", fechaGeneracion: "+fechaGeneracion+
                ", autorizador: "+secPersonaAutorizador+", secEmplJefe: "+secEmplJefe + ", cadena: "+cadena);
        int conteo = 0;
        String secEmpleado = null;
        try {
            //String esquema = getEsquema(nit, cadena);
            setearPerfil(esquema, cadena);
            secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            String sql = "";
            if (secEmplJefe!=null || secPersonaAutorizador!=null ) {
                if (secPersonaAutorizador != null) {
                    System.out.println("creaKioSoliciVacas por kioautorizador");
                    sql += "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, autorizador, activa, fechageneracion) "
                            + "values (?, ?, user, ?, 'S', to_date(?, 'dd/mm/yyyy HH24miss'))";
                } else if (secEmplJefe != null) {
                    System.out.println("creaKioSoliciVacas por empleadojefe");
                    sql += "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, empleadojefe, activa, fechageneracion) "
                            + "values (?, ?, user, ?, 'S', to_date(?, 'dd/mm/yyyy HH24miss'))";
                }
                Query query = getEntityManager(cadena).createNativeQuery(sql);
                query.setParameter(1, secEmpleado);
                query.setParameter(2, secNovedad);
                if (secPersonaAutorizador != null) {
                    query.setParameter(3, secPersonaAutorizador);
                } else {
                    query.setParameter(3, secEmplJefe);
                }
                query.setParameter(4, fechaGeneracion);
                conteo = query.executeUpdate();
                System.out.println("registro kiosolicivaca: " + conteo);
            } else {
                conteo = 0; // No crear la solicitud si no hay un jefe relacionado
            }
        } catch (Exception e) {
            System.out.println("Error creaKioSoliciVacas: " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }
        
    public String getSecuenciaKioNovedadesSolici (String seudonimo, String nitEmpresa,
            String fechainicio, String fecharegreso,
            String dias, String rfVacacion, String cadena, String esquema) {
        System.out.println("Parametros getSecuenciaKioNovedadesSolici(): seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", fechainicio: "+fechainicio+", fecharegreso: "+fecharegreso+", dias: "+dias+", rfVacacion: "+rfVacacion+", cadena: "+cadena);
        String sec = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            String sqlQuery = "select max(secuencia) \n"
                    + "                from kionovedadessolici \n"
                    + "                where dias=? \n"
                    + "                and fechainicialdisfrute=to_date(?, 'dd/mm/yyyy') \n"
                    + "                and fechasiguientefinvaca=to_date(?, 'dd/mm/yyyy') \n"
                    + "                and empleado=? \n"
                    + "                and tipo='VACACION'\n"
                    + "                and SUBTIPO='TIEMPO'\n"
                    + "                and vacacion=? "
                    + "  and secuencia not in (select kionovedadsolici from kiosolicivacas)";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, dias);
            query.setParameter(2, fechainicio);
            query.setParameter(3, fecharegreso);
            query.setParameter(4, secEmpleado);
            query.setParameter(5, rfVacacion);

            sec = query.getSingleResult().toString();
            System.out.println("secuencia kionovedad: " + sec);
        } catch (Exception e) {
            System.out.println("Error: getSecuenciaKioNovedadesSolici: " + e.getMessage());
        }
        return sec;
    }   
       
    public String getSecKioSoliciVacas(String secEmpl, String fechaGeneracion,
            String secEmplJefe, String secPerAutorizador, String kioNovedadSolici, String nitEmpresa, String cadena, String esquema) {
        String secKioSoliciVacas = null;
        String sqlQuery="";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            System.out.println("parametros getSecKioSoliciVacas: secEmpl: " + secEmpl + ", fechaGeneracion: " + fechaGeneracion + ", secEmplJefe: " + secEmplJefe + ", autorizador: "+secPerAutorizador+", kioNovedadSolici " + kioNovedadSolici);
            if (secPerAutorizador!=null) {
             sqlQuery += "select secuencia from kiosolicivacas where empleado=? "
                    + " and fechageneracion=to_date(?, 'dd/mm/yyyy HH24miss') "
                    + " and autorizador=? and activa='S' and kionovedadsolici=?";               
            } else {
            sqlQuery += "select secuencia from kiosolicivacas where empleado=? "
                    + " and fechageneracion=to_date(?, 'dd/mm/yyyy HH24miss') "
                    + " and empleadojefe=? and activa='S' and kionovedadsolici=?";                
            }
            /*String sqlQuery = "select secuencia from kiosolicivacas where empleado=? "
                    + " and kionovedadsolici=? and autorizador=?";*/
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechaGeneracion);
            if (secPerAutorizador != null) {
                query.setParameter(3, secPerAutorizador);
            } else {
                query.setParameter(3, secEmplJefe);
            }
            query.setParameter(4, kioNovedadSolici);
            /*query.setParameter(1, secEmpl);
            query.setParameter(2, fechaGeneracion);
            query.setParameter(3, secEmplJefe);
            query.setParameter(4, secPerAutorizador);
            query.setParameter(5, kioNovedadSolici);*/
            /*query.setParameter(1, secEmpl);
            query.setParameter(2, kioNovedadSolici);            
            query.setParameter(3, secPerAutorizador);    */        
            secKioSoliciVacas = query.getSingleResult().toString();
            System.out.println("SecKioSoliciVacas: " + secKioSoliciVacas);
        } catch (Exception e) {
            System.out.println("Error: getSecKioSoliciVacas: " + e.getMessage());
        }
        return secKioSoliciVacas;
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
    
    
/* 
   Creado 02/06/2021
    
    */    
    public String consultarSecuenciaPerAutorizador(String secEmpleado, String nitEmpresa, String cadena, String esquema) throws Exception {
        String secAutorizador = null;
        String sqlQuery = "select per.secuencia "
                + "from empleados empl, kioautorizadores ka, kioautorizasolicivacas kasv, personas per "
                + "where empl.secuencia = kasv.empleado "
                + "and kasv.kioautorizador = ka.secuencia "
                + "and per.secuencia = ka.persona "
                + "and empl.secuencia = ? ";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            secAutorizador = query.getSingleResult().toString();
            System.out.println("secAutorizador: "+secAutorizador);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".consultarSecuenciaPerAutorizador: " + e.getMessage());//            throw e;
            //throw new Exception(e);
        }
        return secAutorizador;
    }    
        
    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena, String esquema) {
       System.out.println("Parametros getDocumentoPorSeudonimo() seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", cadena: "+cadena);
       String documento=null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND lower(CK.SEUDONIMO)=lower(?) AND CK.NITEMPRESA=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento =  query.getSingleResult().toString();
            System.out.println("documento: "+documento);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getDocumentoPorSeudonimo: "+e.getMessage());
        }
        return documento;
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
    
    public String getSecPersonaPorSeudonimo(String seudonimo, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getSecPersonaPorSeudonimo(): seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", cadena: "+cadena);
        String secuencia = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.SECUENCIA SECUENCIAPERSONA FROM PERSONAS P, CONEXIONESKIOSKOS CK "
                    + "WHERE CK.PERSONA=P.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuenciaEmpl: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getSecPersonaPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }    
     
    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getEmplXsecKioEstadoSolici(): kioEstadoSolici: "+kioEstadoSolici+", cadena: "+cadena);
        String secEmpl = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.EMPLEADO\n"
                    + "FROM \n"
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN\n"
                    + "WHERE\n"
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA\n"
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA\n"
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioEstadoSolici);
            secEmpl = query.getSingleResult().toString();
            System.out.println("Valor getEmplXsecKioEstadoSolici(): " + secEmpl);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getEmplXsecKioEstadoSolici(): " + e.getMessage());
        }
        return secEmpl;
    }
    
    @POST
    @Path("/nuevoEstadoSolici")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response setNuevoEstadoSolici(@QueryParam("secuencia") String secKioEstadoSolici, @QueryParam("motivo") String motivo,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("estado") String estado, 
            @QueryParam("cadena") String cadena, @QueryParam("grupo") String grupoEmpr, @QueryParam("urlKiosco") String urlKiosco) {
        System.out.println("nuevoEstadoSolici()");
        System.out.println("parametros: secuencia: " + secKioEstadoSolici + ", motivo " + motivo + ", empleado " + seudonimo + ", estado: " + estado + ", cadena " + cadena+", nit: "+nitEmpresa+", urlKiosco: "+urlKiosco+", grupoEmpresarial: "+grupoEmpr);
        List s = null;
        int res = 0;
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
            System.out.println("La persona que ejecuta es: "+secEmplEjecuta);
            String secEmplSolicita = getEmplXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena, esquema);
            String secEmplJefe = null;
            String secPerAutoriza = null;
            String nombreAutorizaSolici = "";
            String correoAutorizaSolici = null;
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            if (motivo==null || motivo=="") {
                motivo = " ";
            }
            try {
                secPerAutoriza = getSecPerAutorizadorXsecKioEstadoSolici(secKioEstadoSolici,nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar autorizador relacionado a la solicitud");
            }
            if (secPerAutoriza!=null) {
                nombreAutorizaSolici = getApellidoNombreXSecPer(secPerAutoriza, nitEmpresa, cadena, esquema);
                correoAutorizaSolici = getCorreoXsecPer(secPerAutoriza, nitEmpresa, cadena, esquema);
            } else {
                try {
                    secEmplJefe = getEmplJefeXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
                    if (secEmplJefe!=null) {
                        nombreAutorizaSolici = getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
                        correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
                    }
                } catch (Exception e) {
                    System.out.println("Error al consultar empleadoJefe relacionado a la solicitud");
                }
            }
            String sqlQuery=""; 
            Query query = null;
            if (estado.equals("RECHAZADO")) {
                sqlQuery = "INSERT INTO KIOESTADOSSOLICI "
                        + "(KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, NOVEDADSISTEMA, MOTIVOPROCESA, PERSONAEJECUTA)\n"
                        + "SELECT\n"
                        + "KIOSOLICIVACA, SYSDATE FECHAPROCESAMIENTO, ?, ? EMPLEADOEJECUTA"
                        + ", NOVEDADSISTEMA, ?, ? \n"
                        + "FROM KIOESTADOSSOLICI\n"
                        + "WHERE SECUENCIA=?";
                //String esquema = getEsquema(nitEmpresa, cadena);
                setearPerfil(esquema, cadena);
                query = getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, estado);
                query.setParameter(2, secEmplEjecuta);
                query.setParameter(3, motivo);
                if (estado.equals("CANCELADO")) {
                    query.setParameter(4, null);
                    System.out.println("La solicitud está siendo CANCELADA");
                } else {
                    query.setParameter(4, secPerAutoriza);
                }
                // query.setParameter(4, secPerAutoriza);
                query.setParameter(5, secKioEstadoSolici);
            } else {
                sqlQuery = "INSERT INTO KIOESTADOSSOLICI "
                        + "(KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, NOVEDADSISTEMA, PERSONAEJECUTA)\n"
                        + "SELECT\n"
                        + "KIOSOLICIVACA, SYSDATE FECHAPROCESAMIENTO, ?, ? EMPLEADOEJECUTA"
                        + ", NOVEDADSISTEMA, ? \n"
                        + "FROM KIOESTADOSSOLICI\n"
                        + "WHERE SECUENCIA=?";
                //String esquema = getEsquema(nitEmpresa, cadena);
                setearPerfil(esquema, cadena);
                query = getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, estado);
                query.setParameter(2, secEmplEjecuta);
                if (estado.equals("CANCELADO")) {
                    query.setParameter(3, null);
                    System.out.println("La solicitud está siendo CANCELADA");
                } else {
                    query.setParameter(3, secPerAutoriza);
                }
                // query.setParameter(4, secPerAutoriza);
                query.setParameter(4, secKioEstadoSolici);
            }
            res = query.executeUpdate();
            EnvioCorreo c = new EnvioCorreo();
            String estadoVerbo=estado.equals("CANCELADO")?"CANCELAR":
                    estado.equals("AUTORIZADO")?"PRE-APROBAR":
                    estado.equals("RECHAZADO")?"RECHAZAR":estado;
            String estadoPasado=estado.equals("CANCELADO")?"canceló":
                    estado.equals("AUTORIZADO")?"pre-aprobó":
                    estado.equals("RECHAZADO")?"rechazó":estado;
            String mensaje="Nos permitimos informar que se acaba de "+estadoVerbo+" una solicitud de vacaciones";
                    if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")){
                        mensaje+=" creada para "+getApellidoNombreXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema);
                    }
                    mensaje+=" en el módulo de Kiosco Nómina Designer. Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                    + "<br><br>"
                    + "La persona que "+estadoPasado.toUpperCase()+" LA SOLICITUD es: "+getApellidoNombreXsecEmpl(secEmplEjecuta, nitEmpresa, cadena, esquema)+"<br>";
                    if (estado.equals("CANCELADO")) {
                        mensaje += "La persona a cargo de HACER EL SEGUIMIENTO es: " + nombreAutorizaSolici + "<br>";
                    }
                    mensaje+= "Por favor seguir el proceso en: <a style='color: white !important;' target='_blank' href="+urlKio+">"+urlKio+"</a><br><br>"
                    + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita.<br><br>"
                    + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                    + "<br><a href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a>";
                    
           String fechaInicioDisfrute = getFechaInicioXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
           System.out.println("url Kiosco: "+urlKio);
            if (res>0) {
                System.out.println("solicitud "+estado+" con éxito.");
                String servidorsmtp = getConfigCorreoServidorSMTP(nitEmpresa, cadena, esquema);
                String puerto = getConfigCorreo(nitEmpresa, "PUERTO", cadena, esquema);
                String autenticado = getConfigCorreo(nitEmpresa, "AUTENTICADO", cadena, esquema);
                String starttls = getConfigCorreo(nitEmpresa, "STARTTLS", cadena, esquema);
                String remitente = getConfigCorreo(nitEmpresa, "REMITENTE", cadena, esquema);
                String clave = getConfigCorreo(nitEmpresa, "CLAVE", cadena, esquema);
                if (estado.equals("CANCELADO")){
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema),
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviado a la persona que ejecuta");
                    }
                    /*if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            //getCorreoXsecEmpl(secEmplJefe, cadena),
                            correoAutorizaSolici,
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviado al empleado que solicita asociado");
                    }*/
                }
                
                if (estado.equals("AUTORIZADO") || estado.equals("RECHAZADO")) {
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema),
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviada a la persona que ejecuta");
                    }
                    // Enviar correo al jefe/autorizador de vacaciones
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            correoAutorizaSolici,
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nitEmpresa, cadena)) {
                        System.out.println("Correo enviado al empleado que solicita asociado " + correoAutorizaSolici);
                    }

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

                }
                
               

            } else {
                System.out.println("Error al procesar la solicitud.");
            }
            return Response.status(Response.Status.OK).entity(res > 0).build();
        } catch (Exception ex) {
            System.out.println("Error "+this.getClass().getName()+".setNuevoEstadoSolici: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }
    
    public String getFechaInicioXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        String fechaInicio = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "select "
                    + "TO_CHAR(KN.FECHAINICIALDISFRUTE, 'dd/mm/yyyy') "
                    + "from "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA = KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI=KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            fechaInicio = query.getSingleResult().toString();
            System.out.println("Fecha inicio: " + fechaInicio);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getFechaInicioXsecKioEstadoSolici: " + e.getMessage());
        }
        return fechaInicio;
    }
    
    public String getEmplJefeXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        String secEmplJefe = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.EMPLEADOJEFE "
                    + "FROM "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secEmplJefe = query.getSingleResult().toString();
            System.out.println("Empl jefe asociado: " + secEmplJefe);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getEmplJefeXsecKioEstadoSolici: " + e.getMessage());
        }
        return secEmplJefe;
    } 
    
    public String getSecPerAutorizadorXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecPerAutorizadorXsecKioEstadoSolici(): secKioEstadoSolici: "+secKioEstadoSolici+" nitEmpresa: "+nitEmpresa+", cadena: "+cadena);
        String secPerAutorizador = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.AUTORIZADOR "
                    + "FROM "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secPerAutorizador = query.getSingleResult().toString();
            System.out.println("Secuencia persona KioAutorizador: " + secPerAutorizador);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getSecPerAutorizadorXsecKioEstadoSolici: " + e.getMessage());
        }
        return secPerAutorizador;
    }    

    
    @GET
    @Path("/getDiasNovedadesVaca")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDiasNovedadesVaca(@QueryParam("nit") String nitEmpresa, @QueryParam("empleado") String empleado,
            @QueryParam("cadena") String cadena) {
        System.out.println("Parametros getDiasNovedadesVaca(): nit: " + nitEmpresa + ", usuario: " + empleado + ", cadena: " + cadena);
        List s = null;
        try {
            String esquema = null;
            try {
                esquema = getEsquema(nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar esquema " + e.getMessage());
            }
            String secuenciaEmpleado = getSecuenciaEmplPorSeudonimo(empleado, nitEmpresa, cadena, esquema);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT "
                    + "    tablatotal.empleado, 'TOTAL' tipo, round(nvl(SUM(tablatotal.dias), 0), 2)\n"
                    + "FROM\n"
                    + "    (\n"
                    + "        SELECT\n"
                    + "            e.secuencia empleado, SUM(n.dias) dias\n"
                    + "        FROM\n"
                    + "            novedadessistema n, empleados e, vwvacapendientesempleados v\n"
                    + "        WHERE\n"
                    + "            e.secuencia = n.empleado\n"
                    + "            AND n.tipo = 'VACACION'\n"
                    + "            AND n.subtipo IN ('TIEMPO', 'DINERO')\n"
                    + "            AND v.rfvacacion = n.vacacion\n"
                    + "            AND v.inicialcausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate)\n"
                    + "        GROUP BY e.secuencia\n"
                    + "        UNION\n"
                    + "        SELECT\n"
                    + "            e.secuencia empleado, SUM(k.diasvacadisfrute) dias\n"
                    + "        FROM\n"
                    + "            kioacumvaca k, empleados e\n"
                    + "        WHERE e.secuencia = k.empleado(+)\n"
                    + "        GROUP BY e.secuencia\n"
                    + "        UNION\n"
                    + "        SELECT \n"
                    + "            e.secuencia empleado, SUM(k.diasvacadinero) dias\n"
                    + "        FROM\n"
                    + "            kioacumvaca k, empleados e\n"
                    + "        WHERE e.secuencia = k.empleado(+)\n"
                    + "        GROUP BY e.secuencia\n"
                    + "    ) tablatotal,\n"
                    + "    empleados e\n"
                    + "WHERE\n"
                    + "    tablatotal.empleado = e.secuencia\n"
                    + "    AND e.secuencia = ? \n"
                    + "GROUP BY tablatotal.empleado\n"
                    + "UNION\n"
                    + "( SELECT tabla.empleado secuenciaempl, tabla.tipo tipo, nvl(SUM(tabla.dias), 0) dias\n"
                    + "FROM\n"
                    + "    (\n"
                    + "        ( SELECT e.secuencia empleado, nvl(SUM(n.dias), 0) dias, 'DINERO' tipo\n"
                    + "        FROM\n"
                    + "            novedadessistema n, empleados e, vwvacapendientesempleados v\n"
                    + "        WHERE\n"
                    + "            e.secuencia = n.empleado\n"
                    + "            AND n.tipo = 'VACACION'\n"
                    + "            AND n.subtipo = 'DINERO'\n"
                    + "            AND v.rfvacacion = n.vacacion\n"
                    + "            AND v.inicialcausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate)\n"
                    + "        GROUP BY e.secuencia\n"
                    + "        UNION\n"
                    + "        SELECT e.secuencia empleado, nvl(SUM(k.diasvacadinero), 0) dias, 'DINERO' tipo\n"
                    + "        FROM\n"
                    + "            kioacumvaca k, empleados e\n"
                    + "        WHERE\n"
                    + "            e.secuencia = k.empleado (+)\n"
                    + "        GROUP BY e.secuencia\n"
                    + "        )\n"
                    + "        UNION\n"
                    + "        ( SELECT\n"
                    + "            e.secuencia empleado,\n"
                    + "            round(nvl(SUM(n.dias), 0), 2) dias,\n"
                    + "            'TIEMPO' tipo\n"
                    + "        FROM\n"
                    + "            novedadessistema n, empleados e, vwvacapendientesempleados   v\n"
                    + "        WHERE\n"
                    + "            e.secuencia = n.empleado\n"
                    + "            AND n.tipo = 'VACACION'\n"
                    + "            AND n.subtipo = 'TIEMPO'\n"
                    + "            AND v.rfvacacion = n.vacacion\n"
                    + "            AND v.inicialcausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate)\n"
                    + "        GROUP BY e.secuencia\n"
                    + "        UNION\n"
                    + "        SELECT \n"
                    + "            e.secuencia empleado, round(nvl(SUM(k.diasvacadisfrute), 0), 2) dias, 'TIEMPO' tipo\n"
                    + "        FROM\n"
                    + "            kioacumvaca k, empleados e\n"
                    + "        WHERE\n"
                    + "            e.secuencia = k.empleado(+)\n"
                    + "        GROUP BY e.secuencia\n"
                    + "        )\n"
                    + "    ) tabla\n"
                    + "WHERE\n"
                    + "    tabla.empleado = ? \n"
                    + "GROUP BY tabla.empleado, tabla.tipo\n"
                    + ")";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secuenciaEmpleado);
            query.setParameter(2, secuenciaEmpleado);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getDiasNovedadesVaca: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }
    
    /*Crea nuevo registro kioestadosolici al crear nueva solicitud de vacaciones*/
    public boolean creaKioEstadoSolici(
            String seudonimo, String nit, String kioSoliciVaca, 
            String fechaProcesa, String estado, String motivo, String cadena, String esquema) {
        System.out.println("parametros creaKioEstadoSolici(): seudonimo: " + seudonimo + ", nit: "+nit+", kiosolicivaca: "+kioSoliciVaca+""
                + "\n fechaProcesa: "+fechaProcesa+", estado: " + estado+", cadena: "+cadena);
        int res = 0;
        try {
            String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
            //String esquema = getEsquema(nit, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "INSERT INTO KIOESTADOSSOLICI (KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, MOTIVOPROCESA)\n"
                    + "VALUES (?, to_date(?, 'dd/mm/yyyy HH24miss'), ?, ?, ?)";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioSoliciVaca);
            query.setParameter(2, fechaProcesa);
            query.setParameter(3, estado);
            query.setParameter(4, secEmpl);
            query.setParameter(5, motivo);
            res = query.executeUpdate();
            System.out.println("registro kioestadosolici: "+res);
        } catch (Exception ex) {
            System.out.println("Error "+this.getClass().getName()+".creaKioEstadoSolici: " + ex.getMessage());
            return false;
        }
        return res>0;
    }
    
    private boolean validaFechaInicial(String seudonimo, String nit, String fechaIniVaca, String cadena, String esquema) {
        boolean res;
        BigDecimal conteo = BigDecimal.ZERO;
        try {
           conteo =  verificaExistenciaSolicitud(seudonimo, nit, fechaIniVaca, cadena, esquema);
           res = (conteo.compareTo(new BigDecimal("0")) == 1);
        } catch (Exception ex) {
            System.out.println("validaFechaInicial-excepcion: " + ex.getMessage());
            res = true;
        }
        return res;
    }    
    
    @POST
    @Path("/crearSolicitudVacaciones")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String crearSolicitudVacaciones(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("fechainicio") String fechainicial, @QueryParam("fecharegreso") String fecharegreso,
            @QueryParam("dias") String dias, @QueryParam("vacacion") String RFVACACION, @QueryParam("cadena") String cadena,
            @QueryParam("urlKiosco") String urlKiosco,
            @QueryParam("grupo") String grupoEmpr, @QueryParam("fechafin") String fechafin) {
        System.out.println("crearSolicitudVacaciones{ seudonimo: " + seudonimo + ", nitempresa: " + nit + ","
                + " fechainicio: " + fechainicial + ", fecharegreso: " + fecharegreso + ", dias: " + dias + ", vacacion: " + RFVACACION + ", cadena: " + cadena + ", grupo: " + grupoEmpr);
        System.out.println("link Kiosco: " + urlKiosco);
        System.out.println("grupoEmpresarial: " + grupoEmpr);
        boolean soliciCreada = false;
        boolean soliciValida = false;
        String esquema = null;
        try {
            esquema = getEsquema(esquema, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. "+e.getMessage());
        }
        String mensaje = "";
        String urlKio = urlKiosco + "#/login/" + grupoEmpr;
        String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupoEmpr;
        try {
            boolean res = false;
            boolean valFPago = !validaFechaPago(seudonimo, nit, fechainicial, cadena, esquema);
            boolean valTraslap = validaTraslapamientos(seudonimo, nit, fechainicial, fechafin, cadena, esquema);
            boolean valFInicial = validaFechaInicial(seudonimo, nit, fechainicial, cadena, esquema);
            System.out.println("enviarSolicitud-valFPago: " + valFPago);
            System.out.println("enviarSolicitud-valFInicial: " + valFInicial);
            if (valFPago || valFInicial || valTraslap) {
                mensaje = (valFPago ? "La fecha seleccionada es inferior a la última fecha de pago." : "");
                mensaje = (valFInicial ? mensaje + "Ya existe una solicitud con la fecha inicial de disfrute." : mensaje);
                mensaje = (valTraslap ? mensaje + "Las fechas utilizadas se cruzan con las fechas de otras solicitudes." : mensaje);
                soliciValida = false;
            } else {
                try {
                    soliciValida = true;
                    Date fecha = new Date();
                    String fechaGeneracion = new SimpleDateFormat("dd/MM/yyyy HHmmss").format(fecha);
                    String fechaCorreo = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
                    String horaGeneracion = new SimpleDateFormat("HH:mm").format(fecha);
                    System.out.println("fecha: " + fechaGeneracion);
                    String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena, esquema);
                    String secEmplJefe = null;
                    String secPersonaAutorizador = null;
                    String personaCreaSolici = getApellidoNombreXsecEmpl(secEmpl, nit, cadena, esquema);
                    String nombreAutorizaSolici = "";
                    String correoAutorizaSolici = null;

                    // Consultar EmpleadoJefe/kioAutorizador
                    try {
                        secPersonaAutorizador = consultarSecuenciaPerAutorizador(secEmpl, nit, cadena, esquema);
                    } catch (Exception e) {
                        System.out.println("Error consultando autorizador: " + e.getMessage());
                    }
                    if (secPersonaAutorizador == null) {
                        try {
                            secEmplJefe = consultarSecuenciaEmpleadoJefe(secEmpl, nit, cadena, esquema);
                            if (secEmplJefe != null) {
                                System.out.println("creaKioSoliciVacas: EmpleadoJefe: " + secEmplJefe);
                                nombreAutorizaSolici += getApellidoNombreXsecEmpl(secEmplJefe, nit, cadena, esquema);
                                correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nit, cadena, esquema);
                                System.out.println("El empleado tiene relacionado a empleadoJefe "+nombreAutorizaSolici+" - "+correoAutorizaSolici);
                            } else {
                                System.out.println("El empleado jefe está vacío");
                            }
                        } catch (Exception e) {
                            System.out.println("Error al consultar jefe");
                        }
                    } else {
                        nombreAutorizaSolici += getApellidoNombreXSecPer(secPersonaAutorizador, nit, cadena, esquema);
                        correoAutorizaSolici = getCorreoXsecPer(secPersonaAutorizador, nit, cadena, esquema);
                        System.out.println("El empleado tiene relacionado al autorizador "+nombreAutorizaSolici+" - "+correoAutorizaSolici);
                    }

                    if (secEmplJefe != null || secPersonaAutorizador != null) {
                        System.out.println("Si hay un jefe/autorizador relacionado");
                        // Insertar registro en kionovedadessolici
                        if (creaKioNovedadSolici(seudonimo, nit, fechainicial, fecharegreso, dias, RFVACACION, fechafin, cadena, esquema)) {
                            String secKioNovedad = getSecuenciaKioNovedadesSolici(seudonimo, nit, fechainicial, fecharegreso, dias, RFVACACION, cadena, esquema);
                            System.out.println("secuencia kionovedadsolici creada: " + secKioNovedad);
                            // Insertar registro en kiosolicivacas
                            if (creaKioSoliciVacas(seudonimo, secEmplJefe, secPersonaAutorizador, nit, secKioNovedad, fechaGeneracion, cadena, esquema)) {
                                String secKioSoliciVacas = getSecKioSoliciVacas(secEmpl, fechaGeneracion, secEmplJefe, secPersonaAutorizador, secKioNovedad, nit, cadena, esquema);
                                System.out.println("secuencia kiosolicivacas creada: " + secKioSoliciVacas);
                                // Insertar registro en kioestadossolici
                                if (creaKioEstadoSolici(seudonimo, nit, secKioSoliciVacas, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                    System.out.println("SOLICITUD DE VACACIONES CREADA EXITOSAMENTE!!!");
                                    soliciCreada = true;
                                    mensaje = "Solicitud creada exitosamente.";
                                    String mensajeCorreo = "Nos permitimos informar que se acaba de crear una solicitud de vacaciones en el módulo de Kiosco Nómina Designer. Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                                            + " <br><br> "
                                            + "La persona que CREÓ LA SOLICITUD es: " + personaCreaSolici
                                            + "<br>"
                                            // + "La persona a cargo de DAR APROBACIÓN es: " + getApellidoNombreXsecEmpl(secEmplJefe, cadena)
                                            + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici
                                            + "<br>"+
                                            "La solicitud se creó por "+dias+" días, para ser disfrutados desde el "+fechainicial+" hasta el "+fechafin
                                            + "<br><br>Por favor seguir el proceso en: <a href='" + urlKio + "'>" + urlKio + "</a>"
                                            + "<br><br>"
                                            + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita."
                                            + "<br><br>"
                                            //  + "En caso de que haya olvidado su clave, ingrese a la página de internet, y de clic en ¿Olvidó su clave? y siga los pasos.";
                                            + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                                            + "<br><a href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a>";
                                    EnvioCorreo c = new EnvioCorreo();

                                    String servidorsmtp = getConfigCorreoServidorSMTP(nit, cadena, esquema);
                                    String puerto = getConfigCorreo(nit, "PUERTO", cadena, esquema);
                                    String autenticado = getConfigCorreo(nit, "AUTENTICADO", cadena, esquema);
                                    String starttls = getConfigCorreo(nit, "STARTTLS", cadena, esquema);
                                    String remitente = getConfigCorreo(nit, "REMITENTE", cadena, esquema);
                                    String clave = getConfigCorreo(nit, "CLAVE", cadena, esquema);

                                    if (c.enviarCorreoVacaciones(
                                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                                            getCorreoXsecEmpl(secEmpl, nit, cadena, esquema),
                                            "Solicitud de vacaciones Kiosco - Nueva solicitud: " + fechaCorreo + ". Inicio de vacaciones: " + fechainicial,
                                            mensajeCorreo, urlKio, nit, cadena)) {
                                        System.out.println("Correo enviado al empleado.");
                                    }

                                    // Enviar correo al jefe o autorizador de vacaciones:
                                    if (c.enviarCorreoVacaciones(
                                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                                            correoAutorizaSolici,
                                            "Solicitud de vacaciones Kiosco - Nueva solicitud: " + fechaCorreo + ". Inicio de vacaciones: " + fechainicial,
                                            mensajeCorreo, urlKio, nit, cadena)) {
                                        System.out.println("Correo enviado al jefe.");
                                    }

                                    try {
                                        System.out.println("Consulta si está activa la auditoria..");
                                        if (consultaAuditoria("SOLICITUDVACACIONES", "31", nit, cadena).compareTo(BigDecimal.ZERO) > 0) {
                                            System.out.println("Si debe llevar auditoria crearSolicitud Vacaciones");
                                            String sqlQuery = "select email from kioconfigmodulos where codigoopcion=? and nitempresa=?";
                                            //Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                                            System.out.println("Query2: " + sqlQuery);
                                            Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                                            query2.setParameter(1, "31");
                                            query2.setParameter(2, nit);
                                            List lista = query2.getResultList();
                                            Iterator<String> it = lista.iterator();
                                            System.out.println("obtener " + lista.get(0));
                                            System.out.println("size: " + lista.size());
                                            /*String mensajeAuditoria = "Nos permitimos informar que " + personaCreaSolici
                                                + " generó la SOLICITUD DE VACACIONES el " + fechaCorreo + " a las " + horaGeneracion + " en el módulo de Kiosco Nómina Designer.";*/
                                            String mensajeAuditoria = "Nos permitimos informar que " + personaCreaSolici
                                                    + " generó una SOLICITUD DE VACACIONES el " + fechaCorreo + " a las " + horaGeneracion + " en el módulo de Kiosco Nómina Designer."
                                                    + "<br>"
                                                    + // + "La persona a cargo de DAR APROBACIÓN es: " + getApellidoNombreXsecEmpl(secEmplJefe, cadena)
                                                    "La solicitud se creó por " + dias + " días, para ser disfrutados desde el " + fechainicial + " hasta el " + fechafin
                                                    + "<br>"
                                                    + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici + ".";
                                            while (it.hasNext()) {
                                                String correoenviar = it.next();
                                                System.out.println("correo auditoria: " + correoenviar);
                                                //c.pruebaEnvio2("smtp.gmail.com","587","pruebaskiosco534@gmail.com","Nomina01", "S", correoenviar,
                                                System.out.println("codigoopcion: " + "31");
                                                /*c.enviarCorreoVacaciones(servidorsmtp, puerto, autenticado, starttls, remitente, clave, correoenviar, 
                                            "Auditoria: Nueva Solicitud de vacaciones Kiosco. "+fechaCorreo, mensajeAuditoria, urlKio, nit);*/
                                                c.enviarCorreoInformativo("Auditoria: Nueva Solicitud de vacaciones Kiosco. " + fechaCorreo,
                                                        "Estimado usuario: ", mensajeAuditoria, nit, urlKio, cadena, correoenviar, null);
                                            }
                                        } else {
                                            System.out.println("No lleva auditoria Vacaciones");
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Ha ocurrido un error al intentar consultar o enviar la auditoria");
                                    }
                                } else {
                                    mensaje = "Ha ocurrido un error y no fue posible crear la solicitud, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                                }
                            } else {
                                System.out.println("Ha ocurrido un error al momento de crear el registro 2 de la solicitud");
                                mensaje = "Ha ocurrido un error y no fue posible crear la solicitud, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                            }
                        } else {
                            System.out.println("Ha ocurrido un error al momento de crear el registro 1 de la solicitud");
                            mensaje = "Ha ocurrido un error y no fue posible crear la solicitud, por favor inténtelo de nuevo más tarde. Si el problema persiste comuníquese con el área de nómina y recursos humanos de su empresa";
                        }
                    } else {
                        // Si no hay una persona asignada para autorizar las vacaciones no crear la solicitud
                        soliciCreada = false;
                        mensaje = "No tiene un autorizador de vacaciones relacionado, por favor comuníquese con el área de nómina y recursos humanos de su empresa.";
                    }

                } catch (Exception e) {
                    System.out.println("Ha ocurrido un error: " + e.getMessage());
                    soliciCreada = false;
                    mensaje = "Ha ocurrido un error, por favor inténtelo de nuevo más tarde.";
                }
            }

        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".crearSolicitud. " + e.getMessage());
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("solicitudCreada", soliciCreada);
            obj.put("mensaje", mensaje);
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }
    
    public String getCorreoSoporteKiosco(String nitEmpresa, String cadena) {
        System.out.println("getConfigCorreoServidorSMTP()");
        String emailSoporte="";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EMAILCONTACTO FROM KIOPERSONALIZACIONES WHERE "
                    + "EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?) AND ROWNUM<=1";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            emailSoporte =  query.getSingleResult().toString();
            System.out.println("Email soporte: "+emailSoporte);
        } catch (Exception e) {
            System.out.println("Error: getCorreoSoporteKiosco: "+e.getMessage());
        }
        return emailSoporte;
    }     
        
    
    public String getConfigCorreo(String nitEmpresa, String valor, String cadena, String esquema) {
        System.out.println("getPathArchivosPlanos()");
        String servidorsmtp="smtp.designer.com.co";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT "+valor+" FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp =  query.getSingleResult().toString();
            System.out.println(valor+": "+servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getConfigCorreo(): " +e.getMessage());
        }
        return servidorsmtp;
    }      

    public String getConfigCorreoServidorSMTP(String nitEmpresa, String cadena, String esquema) {
        System.out.println("getConfigCorreoServidorSMTP(): nit: "+cadena+", cadena: "+cadena);
        String servidorsmtp="smtp.designer.com.co";
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT SERVIDORSMTP FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp =  query.getSingleResult().toString();
            System.out.println("Servidor smtp: "+servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getConfigCorreoServidorSMTP: "+e.getMessage());
        }
        return servidorsmtp;
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
    
    
    public String getCorreoXsecPer(String secPersona, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getCorreoXsecPer(): secPer: "+secPersona+", cadena: "+cadena);
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
            System.out.println("getCorreoXsecPer(): "+correo);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+"getCorreoXsecPer(): " + e.getMessage());
        }
        return correo;
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
    
    
   public BigDecimal consultarCodigoJornada(String seudonimo, String nitEmpresa, String fechaDisfrute, String cadena, String esquema) throws Exception {
        System.out.println(this.getClass().getName() + "." + "consultarCodigoJornada" + "()");
        String consulta = "select nvl(j.codigo, 1) "
                + "from vigenciasjornadas v, jornadaslaborales j "
                + "where v.empleado = ? "
                + "and j.secuencia = v.jornadatrabajo "
                + "and v.fechavigencia = (select max(vi.fechavigencia) "
                + "from vigenciasjornadas vi "
                + "where vi.empleado = v.empleado "
                + "and vi.fechavigencia <= to_date( ? , 'dd/mm/yyyy') ) ";
        Query query = null;
        BigDecimal codigoJornada;
        String secEmpleado=getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        /*SimpleDateFormat formatoFecha = new SimpleDateFormat("ddMMyyyy");
        String strFechaDisfrute = formatoFecha.format(fechaDisfrute);*/
        System.out.println("secuencia: " + secEmpleado);
        //System.out.println("fecha en txt: " + strFechaDisfrute);
        System.out.println("fecha en txt: " + fechaDisfrute);
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            //query.setParameter(2, strFechaDisfrute);
            query.setParameter(2, fechaDisfrute);
            codigoJornada = new BigDecimal(query.getSingleResult().toString());
            return codigoJornada;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
//            throw new Exception(npee.toString());
            return null;
        } catch (Exception e) {
            System.out.println("Error general. " + e);
            throw new Exception(e.toString());
        }
    }    
   
   
    private boolean validaFechaPago(String seudonimo, String nitEmpresa, String fechainicialdisfrute, String cadena, String esquema) throws Exception {
            Calendar cl = Calendar.getInstance();
            cl.setTime(getFechaUltimoPago(seudonimo, nitEmpresa, cadena, esquema));
            return getDate(fechainicialdisfrute, cadena).after(cl.getTime());
    }   
   
    public Date getFechaUltimoPago(String seudonimo, String nitEmpresa, String cadena, String esquema) throws Exception {
        BigDecimal res = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            String consulta = "SELECT GREATEST("
                    + "CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1), "
                    + "NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 80), CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1)"
                    + ")) "
                    + "FROM DUAL ";
            Date fechaUltimoPago = null;
            Query query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, secEmpleado);
            query.setParameter(3, secEmpleado);
            fechaUltimoPago = (Date) (query.getSingleResult());
            return fechaUltimoPago;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general. " + e);
            throw new Exception(e.toString());
        }
    }
    
    private String nombreDia(int dia) {
        String retorno = "";
        switch (dia) {
            case 1:
                retorno = "DOM";
                break;
            case 2:
                retorno = "LUN";
                break;
            case 3:
                retorno = "MAR";
                break;
            case 4:
                retorno = "MIE";
                break;
            case 5:
                retorno = "JUE";
                break;
            case 6:
                retorno = "VIE";
                break;
            case 7:
                retorno = "SAB";
                break;
            default:
                retorno = "";
                break;
        }
        return retorno;
    }
    
    public Date getDate(String fechaStr, String cadena) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "getDate" + "()");
        String consulta = "SELECT "
                + "TO_DATE(?, 'dd/mm/yyyy') "
                + "FROM DUAL ";
        Query query = null;
        Date fechaRegreso = null;
        try {
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
       
    public boolean verificarDiaLaboral(String fechaDisfrute, BigDecimal codigoJornada, String nitEmpresa, String cadena) throws Exception {
        System.out.println(this.getClass().getName() + "." + "verificarDiaLaboral" + "()");
        System.out.println("fechaDisfrute: " + fechaDisfrute);
        System.out.println("codigoJornada: " + codigoJornada);
        String consulta = "select COUNT(*) "
                + "FROM JORNADASSEMANALES JS, JORNADASLABORALES JL "
                + "WHERE JL.SECUENCIA = JS.JORNADALABORAL "
                + "AND JL.CODIGO = TO_number( ? ) "
                + "AND JS.DIA = ? ";
        Query query = null;
        BigDecimal conteoDiaLaboral;
        boolean esDiaLaboral;
        int diaSemana;
        String strFechaDisfrute = "";
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(getDate(fechaDisfrute, cadena));
        diaSemana = c.get(Calendar.DAY_OF_WEEK);
        strFechaDisfrute = nombreDia(diaSemana);
        System.out.println("strFechaDisfrute: " + strFechaDisfrute);
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, codigoJornada);
            query.setParameter(2, strFechaDisfrute);
            conteoDiaLaboral = new BigDecimal(query.getSingleResult().toString());
            esDiaLaboral = !conteoDiaLaboral.equals(BigDecimal.ZERO);
            System.out.println(fechaDisfrute+" esDiaLaboral: "+esDiaLaboral);
            return esDiaLaboral;
        } catch (PersistenceException pe) {
            System.out.println("Error de persisl = !conteoDiaLaboral.equals(BigDecimal.ZERO);\n" +
"      tencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general. " + e);
            throw new Exception(e.toString());
        }
    }
    
    public boolean verificarFestivo(String fechaDisfrute, String nitEmpresa, String cadena) throws Exception {
        System.out.println(this.getClass().getName() + "." + "verificarFestivo(): "+fechaDisfrute+", cadena: "+cadena);
        String consulta = "select COUNT(*) "
                + "FROM FESTIVOS F, PAISES P "
                + "WHERE P.SECUENCIA = F.PAIS "
                + "AND P.NOMBRE = ? "
                + "AND F.DIA = TO_DATE( ? , 'DD/MM/YYYY') ";
        Query query = null;
        BigDecimal conteoDiaFestivo;
        boolean esDiaFestivo;
        /*SimpleDateFormat formatoFecha = new SimpleDateFormat("ddMMyyyy");
        String strFechaDisfrute = formatoFecha.format(fechaDisfrute);*/
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, "COLOMBIA");
            //query.setParameter(2, strFechaDisfrute);
            query.setParameter(2, fechaDisfrute);
            conteoDiaFestivo = new BigDecimal(query.getSingleResult().toString());
            esDiaFestivo = !conteoDiaFestivo.equals(BigDecimal.ZERO);
            System.out.println(fechaDisfrute+" esDiaFestivo: "+esDiaFestivo);
            return esDiaFestivo;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error "+this.getClass().getName()+".verificarFestivo " + e);
            throw new Exception(e.toString());
        }
//        return false;
    }

    private boolean validaTraslapamientos(String seudonimo, String nitempresa, String fechaIniVaca, String fechaFinVaca, String cadena, String esquema) {
        System.out.println("Parametros validaTraslapamientos(): usuario: "+seudonimo+", nitEmpresa: "+nitempresa+", fechaIniVaca: "+fechaIniVaca+", fechaFinVaca: "+fechaFinVaca+", cadena: "+cadena);
        boolean res = false;
        try {
            res = !BigDecimal.ZERO.equals(consultaTraslapamientos(seudonimo, nitempresa, fechaIniVaca, fechaFinVaca, cadena, esquema));
            //si es igual a cero, no hay traslapamientos.
            //falso si es cero, verdadero si es diferente de cero.
        } catch (Exception e) {
            System.out.println("validaTraslapamientos-excepcion: " + e.getMessage());
        }
        return res;
    }    

    public BigDecimal consultaTraslapamientos(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca,
            String fechaFinVaca, String cadena, String esquema) throws PersistenceException, NullPointerException, Exception {
        System.out.println("Parametros consultaTraslapamientos(): seudonimo: " + seudonimo + ", nitempresa: " + nitEmpresa + ", fechaIniVaca: " + fechaIniVaca + ", fechaFinVaca: " + fechaFinVaca + ", cadena: " + cadena);
        System.out.println(this.getClass().getName() + "." + "consultaTraslapamientos" + "()");
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICARTRASLAPAMIENTO(?, to_date(?,'DD/MM/YYYY') , to_date(?,'DD/MM/YYYY') ) "
                + "FROM DUAL ";
        Query query = null;
        BigDecimal contTras = null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            //query.setParameter(2, fechaIniVaca, TemporalType.DATE);
            //query.setParameter(3, fechaFinVaca, TemporalType.DATE);
            query.setParameter(2, fechaFinVaca);
            query.setParameter(3, fechaFinVaca);
            contTras = (BigDecimal) (query.getSingleResult());
            System.out.println("Resultado consulta traslapamiento: " + contTras);
            return contTras;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia en consultaTraslapamientos.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en consultaTraslapamientos");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general en consultaTraslapamientos. " + e);
            throw new Exception(e.toString());
        }
    }
    
    
    /*
       Método que valida si la fecha de disfrute que recibe como parametro ya tiene una solicitud asociada
    */
    public BigDecimal verificaExistenciaSolicitud(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca, String cadena, String esquema) throws Exception {
        System.out.println("verificaExistenciaSolicitud() fechaInicio: " + fechaIniVaca + ",nitempresa: " + nitEmpresa + ", fechaIniVaca: " + fechaIniVaca + ", cadena: " + cadena);
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
        System.out.println(this.getClass().getName() + ".verificaExistenciaSolicitud()");
        System.out.println("verificaExistenciaSolicitud-secEmpleado: " + secEmpleado);
        System.out.println("verificaExistenciaSolicitud-fechaIniVaca: " + fechaIniVaca);
        /*SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
        String txtFecha = formato.format(fechaIniVaca);*/
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICAEXISTESOLICITUD(?, to_date(?,'DD/MM/YYYY') ) "
                + "FROM DUAL ";
        System.out.println("verificaExistenciaSolicitud-consulta: " + consulta);
        Query query = null;
        BigDecimal conteo = null;
        try {
            try {
                //String esquema = getEsquema(nitEmpresa, cadena);
                setearPerfil(esquema, cadena);
                query = getEntityManager(cadena).createNativeQuery(consulta);
                query.setParameter(1, secEmpleado);
                // query.setParameter(2, txtFecha);
                query.setParameter(2, fechaIniVaca);
            } catch (NullPointerException npe) {
                throw new Exception("verificaExistenciaSolicitud: EntiyManager, query o consulta nulos.");
            }
            Object res = query.getSingleResult();
            System.out.println("verificaExistenciaSolicitud-res: " + res);
            if (res instanceof BigDecimal) {
                conteo = (BigDecimal) res;
                System.out.println("verificaExistenciaSolicitud-conteo: " + conteo);
            } else {
                throw new Exception("El conteo de la solicitud no es BigDecimal. " + res + " tipo: " + res.getClass().getName());
            }
        } catch (Exception e) {
            System.out.println("verificaExistenciaSolicitud-excepcion: " + e.getMessage());
//            throw e;
            throw new Exception("Error verificando si la solicitud ya existe " + e);
        }
        System.out.println("verificaExistenciaSolicitud-conteo: " + conteo);
        return conteo;
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
    
    public String getSecuenciaPorNitEmpresa( String nitEmpresa, String cadena, String esquema) {
       String secuencia=null;
        try {
            //String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EM.SECUENCIA SECUENCIAEMPRESA FROM EMPRESAS EM WHERE EM.NIT=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            secuencia =  query.getSingleResult().toString();
            System.out.println("secuencia: "+secuencia);
        } catch (Exception e) {
            System.out.println("Error: "+this.getClass().getName()+".getSecuenciaPorNitEmpresa: "+e.getMessage());
        }
        return secuencia;
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
