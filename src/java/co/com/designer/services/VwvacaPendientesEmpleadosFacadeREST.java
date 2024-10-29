package co.com.designer.services;

//import co.com.designer.kiosko.generales.EnvioCorreo;
import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import co.com.designer.kiosko.generales.GenerarCorreo;
import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConexiones;
import co.com.designer.persistencia.implementacion.PersistenciaConexionesKioskos;
import co.com.designer.persistencia.implementacion.PersistenciaEmpleados;
import co.com.designer.persistencia.implementacion.PersistenciaEmpresas;
import co.com.designer.persistencia.implementacion.PersistenciaKioAutorizaSoliciVacas;
import co.com.designer.persistencia.implementacion.PersistenciaKioConfigModulos;
import co.com.designer.persistencia.implementacion.PersistenciaKioSoliciVacas;
import co.com.designer.persistencia.implementacion.PersistenciaKioVacaciones_pkg;
import co.com.designer.persistencia.implementacion.PersistenciaManejoFechas;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.implementacion.PersistenciaPersonas;
import co.com.designer.persistencia.implementacion.PersistenciaSolucionesNodos;
import co.com.designer.persistencia.implementacion.PersistenciaVacaPendientes;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpleados;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpresas;
import co.com.designer.persistencia.interfaz.IPersistenciaKioAutorizaSoliciVacas;
import co.com.designer.persistencia.interfaz.IPersistenciaKioConfigModulos;
import co.com.designer.persistencia.interfaz.IPersistenciaKioSoliciVacas;
import co.com.designer.persistencia.interfaz.IPersistenciaKioVacaciones_pkg;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaPersonas;
import co.com.designer.persistencia.interfaz.IPersistenciaVacaPendientes;
import co.com.designer.persistencia.interfaz.IPersistenciaManejoFechas;
import co.com.designer.persistencia.interfaz.IPersistenciaSolucionesNodos;
import java.math.BigDecimal;
//import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
//import javax.persistence.PersistenceException;
import javax.persistence.Query;
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
 * @author Edwin Hastamorir
 */
@Stateless
@Path("vacacionesPendientes")
public class VwvacaPendientesEmpleadosFacadeREST { //extends AbstractFacade<VwVacaPendientesEmpleados> {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaKioAutorizaSoliciVacas persisAutorizaSoli;
    private IPersistenciaPersonas persisPersonas;
    private IPersistenciaVacaPendientes persisVacaPend;
    private IPersistenciaConexionesKioskos persisConKiosko;
    private IPersistenciaEmpresas persisEmpresa;
    private IPersistenciaKioConfigModulos persisConfigModu;
    private IPersistenciaKioVacaciones_pkg persisVacaPkg;
    private IPersistenciaManejoFechas persisManejoFechas;
    private IPersistenciaSolucionesNodos persisSolNodos;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaKioSoliciVacas persisKioSoliciVacas;
    private IPersistenciaEmpleados persisEmpleados;
    
    public VwvacaPendientesEmpleadosFacadeREST() {
//        super(VwVacaPendientesEmpleados.class);
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.persisAutorizaSoli = new PersistenciaKioAutorizaSoliciVacas();
        this.persisPersonas = new PersistenciaPersonas();
        this.persisVacaPend = new PersistenciaVacaPendientes();
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        this.persisEmpresa = new PersistenciaEmpresas();
        this.persisConfigModu = new PersistenciaKioConfigModulos();
        this.persisVacaPkg = new PersistenciaKioVacaciones_pkg();
        this.persisSolNodos = new PersistenciaSolucionesNodos();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    /*
    @GET
    //@Path("/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List findAlls() {
        this.rolesBD.setearPerfil();
        return super.findAll();
    }
     */
    /**
     * @desc Devuelve las solicitudes por empleado, dependiendo el estado que se
     * le envie como parametro
     * @params:
     * @param documento - El parametro documento hace relación al documento del
     * empleado
     * @param nitEmpresa - El parametro nitEmpresa hace referencia al nit de la
     * empresa con la que la persona se logueó
     * @param estado - El parametro estado recibe el estado de las solicitudes
     * que requiere consultar
     * @param cadena - El parametro cadena hace referencia a la unidad de
     * persistencia con la que se deben realizar las consultas
     * @return La lista de solicitudes que cumplen el filtro por empleado y
     * ultimo estado de solicitud
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
        System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getSolicitudXEstado(): Parametros: "
                + "seudonimo: " + documento
                + ", empresa: " + nitEmpresa
                + ", estado: " + estado
                + ", cadena: " + cadena);
        try {
            String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(documento, nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
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
                    + ") empleadoejecuta, \n"
                    + "e.secuencia secuencia "
                    + "from KioEstadosSolici e, kiosolicivacas ks, kionovedadessolici kn, VwVacaPendientesEmpleados v \n"
                    + "where \n"
                    + "e.kiosolicivaca = ks.secuencia \n"
                    + "and ks.KIONOVEDADSOLICI = kn.secuencia \n"
                    + "and kn.vacacion=v.RFVACACION \n"
                    + "and ks.empleado = ? \n"
                    + "and e.estado = ? \n"
                    + "and e.FECHAPROCESAMIENTO = (select max(ei.FECHAPROCESAMIENTO) \n"
                    + "from KioEstadosSolici ei, kiosolicivacas ksi \n"
                    + "where ei.kioSoliciVaca = ksi.secuencia \n"
                    + "and ksi.secuencia=ks.secuencia) "
                    + "and v.inicialcausacion>= EMPLEADOCURRENT_PKG.FECHATIPOCONTRATO(ks.empleado, sysdate) "
                    + "order by e.fechaProcesamiento DESC";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, estado);
            s = query.getResultList();
            s.forEach(System.out::println);

            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getSolicitudXEstado(): " + "Error: " + ex);
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
        String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        String secEmplJefe = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("Webservice: solicitudesXEmpleadoJefe Parametros: usuario: secEmplJefe: " + secEmplJefe + ", empresa: " + nitEmpresa);
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "  SELECT \n"
                    + "  t1.CODIGOEMPLEADO, P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE NOMBRECOMPLETO, \n"
                    + "  to_char(t2.FECHAGENERACION, 'DD/MM/YYYY HH:mm:ss') solicitud, \n"
                    + "  to_char(kn.FECHAINICIALDISFRUTE, 'DD/MM/YYYY HH:mm:ss') FECHAINICIALDISFRUTE, \n"
                    + "  to_char(T0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:mm:ss') FECHAPROCESAMIENTO, \n"
                    + "  t0.SECUENCIA, NVL(t0.MOTIVOPROCESA, 'N/A'), \n"
                    + "  to_char(kn.ADELANTAPAGOHASTA, 'DD/MM/YYYY HH:mm:ss') FECHAFINVACACIONES, \n"
                    + "  to_char(kn.fechaSiguienteFinVaca, 'DD/MM/YYYY HH:mm:ss') fecharegresolaborar, \n"
                    + "  kn.dias, \n"
                    + "  TO_CHAR(v.INICIALCAUSACION, 'DD/MM/YYYY')||' a '||TO_CHAR(v.FINALCAUSACION, 'DD/MM/YYYY') periodo,\n"
                    + "  (select pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre from personas pei, empleados ei\n"
                    + "  where pei.secuencia=ei.persona and t2.EMPLEADOJEFE=ei.secuencia) empleadojefe,\n"
                    + "  TO_CHAR(kn.FECHAPAGO, 'DD/MM/YYYY') FECHAPAGO\n, "
                    + "  t0.ESTADO ESTADO "
                    + "  FROM KIOESTADOSSOLICI t0, KIOSOLICIVACAS t2, EMPLEADOS t1, PERSONAS P, kionovedadessolici kn, VwVacaPendientesEmpleados v \n"
                    + "  WHERE (((((t1.EMPRESA = (select secuencia from empresas where nit=?)) \n"
                    + "  AND (t0.ESTADO IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO'))) "
                    + "  AND (t2.EMPLEADOJEFE =?))"
                    + "  AND (t0.SECUENCIA = (SELECT MAX(t3.SECUENCIA) FROM KIOSOLICIVACAS t4, KIOESTADOSSOLICI t3 \n"
                    + "  WHERE ((t4.SECUENCIA = t2.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIVACA))))) \n"
                    + "  AND ((t2.SECUENCIA = t0.KIOSOLICIVACA) AND (t1.SECUENCIA = t2.EMPLEADO))\n"
                    + "  AND t1.PERSONA=P.SECUENCIA\n"
                    + "  and t2.KIONOVEDADSOLICI = kn.secuencia\n"
                    + "  and kn.vacacion=v.RFVACACION\n"
                    + "  AND V.INICIALCAUSACION>=EMPLEADOCURRENT_PKG.FECHATIPOCONTRATO(t1.secuencia, sysdate)\n"
                    + "  ) \n"
                    + "  ORDER BY t0.FECHAPROCESAMIENTO DESC";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secEmplJefe);
            s = query.getResultList();
            s.forEach(System.out::println);
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
        System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getSoliciSinProcesarJefe(): " + "Parametros: "
                + "nit: " + nitEmpresa
                + " jefe " + jefe
                + " estado: " + estado
                + " cadena " + cadena);
        List s = null;
        String sqlQuery = "SELECT \n"
                + " empl.codigoempleado documento \n"
                + ", REPLACE(TRIM(per.primerApellido||' '||per.segundoApellido||' '||per.nombre), '  ', ' ') nombre \n"
                + ", empl.secuencia \n"
                + ", TO_CHAR(ksv.fechaGeneracion, 'DD/MM/YYYY HH:MI:SS') SOLICITUD \n"
                + ", TO_CHAR(kns.fechaInicialDisfrute,'DD/MM/YYYY' ) INICIALDISFRUTEM \n"
                + ", TO_CHAR(kes.fechaProcesamiento, 'DD/MM/YYYY HH:MI:SS') FECHAULTMODIF \n"
                + ", kes.estado \n"
                + ", kes.motivoProcesa \n"
                + ", kes.novedadSistema \n"
                + ", kes.empleadoEjecuta \n"
                + ", kes.personaEjecuta \n"
                + ", kes.kioSoliciVaca \n"
                + ", TO_CHAR(kns.adelantaPagoHasta, 'DD/MM/YYYY') FECHAFIN \n"
                + ", TO_CHAR(kns.fechaSiguienteFinVaca,'DD/MM/YYYY') FECHAREGRESO \n"
                + ", kns.dias \n"
                + ", v.inicialCausacion||' a '||v.finalCausacion PERIODOCAUSADO \n"
                + ", (SELECT perJ.primerApellido||' '||perJ.segundoApellido||' '||perJ.nombre \n"
                + "   FROM Personas perJ, Empleados jefe \n"
                + "   WHERE perJ.secuencia = jefe.persona \n"
                + "   and jefe.secuencia = ksv.empleadoJefe) empleadoJefe \n"
                + ", kns.fechaPago FECHAPAGO \n"
                + ", kes.secuencia secuencia \n"
                + "FROM Empresas em, Empleados empl, Personas per \n"
                + ", KioSoliciVacas ksv, KioNovedadesSolici kns, KioEstadosSolici kes \n"
                + ", VwVacaPendientesEmpleados v \n"
                + "WHERE empl.empresa = em.secuencia \n"
                + "AND per.secuencia = empl.persona \n"
                + "AND ksv.empleado = empl.secuencia \n"
                + "AND ksv.kioNovedadSolici = kns.secuencia \n"
                + "AND kes.kioSoliciVaca = ksv.secuencia \n"
                + "AND v.empleado = empl.secuencia \n"
                + "AND v.rfVacacion = kns.vacacion \n"
                + "AND kes.secuencia = (SELECT MAX(kesi.secuencia) \n"
                + "  FROM KioEstadosSolici kesi \n"
                + "  WHERE kesi.kioSoliciVaca = kes.kioSoliciVaca ) \n"
                + "AND v.inicialCausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(empl.secuencia, sysdate) \n"
                + "AND kes.estado = ? \n"
                + "AND ksv.empleadoJefe = ? \n"
                + "AND em.nit = ? \n"
                + "ORDER BY \n"
                + "kns.fechaInicialDisfrute, ksv.fechaGeneracion, kes.fechaProcesamiento";
        String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        try {
            String secuenciaJefe = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(jefe, nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, estado);
            query.setParameter(2, secuenciaJefe);
            query.setParameter(3, nitEmpresa);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getSoliciSinProcesarJefe: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    /**
     * Devuelve las solicitudes pendientes por procesar, relacionadas al
     * kioautorizador
     *
     * @params:
     * @param seudonimo - El parametro seudonimo hace relación al seudonimo del
     * kioautorizador
     * @param nitEmpresa - El parametro nitEmpresa hace referencia al nit de la
     * empresa con la que la persona se logueó
     * @param estado - El parametro estado recibe el estado de las solicitudes
     * que requiere consultar
     * @param cadena - El parametro cadena hace referencia a la unidad de
     * persistencia con la que se deben realizar las consultas
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
        String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        String secSecPerAutorizador = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getSolicitudesXAutorizador(): " + "Parametros: usuario: secPersonaAutorizador: " + secSecPerAutorizador + ", empresa: " + nitEmpresa);
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
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
                    + "and kn.vacacion=v.RFVACACION"
                    + "AND V.INICIALCAUSACION>=EMPLEADOCURRENT_PKG.FECHATIPOCONTRATO(t3.secuencia, sysdate)";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secSecPerAutorizador);
            s = query.getResultList();
            s.forEach(System.out::println);

            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
            return Response.status(Response.Status.OK).entity("").build();
        }
    }

    /**
     * Devuelve las solicitudes pendientes por procesar (la que en su último
     * estado es 'ENVIADO'), relacionadas al kioautorizador
     *
     * @params:
     * @param seudonimo - El parametro seudonimo hace relación al seudonimo del
     * kioautorizador
     * @param nitEmpresa - El parametro nitEmpresa hace referencia al nit de la
     * empresa con la que la persona se logueó
     * @param cadena - El parametro cadena hace referencia a la unidad de
     * persistencia con la que se deben realizar las consultas
     * @return El número de ítems (números aleatorios) de que consta la serie
     */
    @GET
    @Path("/soliciSinProcesarAutorizador")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSoliciSinProcesarAutorizador(@QueryParam("usuario") String seudonimo, @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getSoliciSinProcesarAutorizador(): " + "Parametros: "
                + "nit: " + nitEmpresa
                + " seudonimo autorizador: " + seudonimo
                + " cadena " + cadena);
        List s = null;
        String sqlQuery = "SELECT \n"
                + " empl.codigoempleado documento \n"
                + ", REPLACE(TRIM(per.primerApellido||' '||per.segundoApellido||' '||per.nombre), '  ', ' ') nombre \n"
                + ", empl.secuencia \n"
                + ", TO_CHAR(ksv.fechaGeneracion, 'DD/MM/YYYY HH:MI:SS') SOLICITUD \n"
                + ", TO_CHAR(kns.fechaInicialDisfrute,'DD/MM/YYYY' ) INICIALDISFRUTEM \n"
                + ", TO_CHAR(kes.fechaProcesamiento, 'DD/MM/YYYY HH:MI:SS') FECHAULTMODIF \n"
                + ", kes.estado \n"
                + ", kes.motivoProcesa \n"
                + ", kes.novedadSistema \n"
                + ", kes.empleadoEjecuta \n"
                + ", kes.personaEjecuta \n"
                + ", kes.kioSoliciVaca \n"
                + ", TO_CHAR(kns.adelantaPagoHasta, 'DD/MM/YYYY') FECHAFIN \n"
                + ", TO_CHAR(kns.fechaSiguienteFinVaca,'DD/MM/YYYY') FECHAREGRESO \n"
                + ", kns.dias \n"
                + ", v.inicialCausacion||' a '||v.finalCausacion PERIODOCAUSADO \n"
                + ", (SELECT perAut.primerApellido||' '||perAut.segundoApellido||' '||perAut.nombre \n"
                + "   FROM Personas perAut \n"
                + "   WHERE perAut.secuencia = ksv.autorizador) autorizador \n"
                + ", kns.fechaPago FECHAPAGO \n"
                + ", kes.secuencia secuencia \n"
                + "FROM Empresas em, Empleados empl, Personas per \n"
                + ", KioSoliciVacas ksv, KioNovedadesSolici kns, KioEstadosSolici kes \n"
                + ", VwVacaPendientesEmpleados v \n"
                + "WHERE empl.empresa = em.secuencia \n"
                + "AND per.secuencia = empl.persona \n"
                + "AND ksv.empleado = empl.secuencia \n"
                + "AND ksv.kioNovedadSolici = kns.secuencia \n"
                + "AND kes.kioSoliciVaca = ksv.secuencia \n"
                + "AND v.empleado = empl.secuencia \n"
                + "AND v.rfVacacion = kns.vacacion \n"
                + "AND kes.secuencia = (SELECT MAX(kesi.secuencia) \n"
                + "  FROM KioEstadosSolici kesi \n"
                + "  WHERE kesi.kioSoliciVaca = kes.kioSoliciVaca ) \n"
                + "AND kes.estado = 'ENVIADO' \n"
                + "AND v.inicialCausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(empl.secuencia, sysdate) \n"
                + "AND ksv.autorizador = ? \n"
                + "AND em.nit = ? \n"
                + "ORDER BY \n"
                + "kns.fechaInicialDisfrute, ksv.fechaGeneracion, kes.fechaProcesamiento";
        try {
            String esquema = null;
            try {
                esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar esquema " + e.getMessage());
            }
//            String secPerAutorizador = getSecPersonaPorSeudonimo(seudonimo, nitEmpresa, cadena, esquema);
            String secPerAutorizador = this.persisConKiosko.getPersonaPorSeudonimo(seudonimo, nitEmpresa, cadena).toPlainString() ;;
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPerAutorizador);
            query.setParameter(2, nitEmpresa);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getSoliciSinProcesarAutorizador(): " + "Error: " + ex.toString());
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    @GET
    @Path("/consultarPeriodosPendientesEmpleado")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List consultarPeriodosPendientesEmpleado(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) throws Exception {
        System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".consultarPeriodosPendientesEmpleado(): " + "Parametros: seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        List<VwVacaPendientesEmpleados> periodosPendientes = null;
        periodosPendientes = this.persisVacaPend.getPeriodosPendientesEmpleado(seudonimo, nitEmpresa, cadena);
        return periodosPendientes;
    }

    @GET
    @Path("/consultarPeriodoMasAntiguo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<VwVacaPendientesEmpleados> consultarPeriodoMasAntiguo(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        List<VwVacaPendientesEmpleados> retorno = null;
        retorno = this.persisVacaPend.getPeriodoMasAntiguo(seudonimo, nitEmpresa, cadena);
        return retorno;
    }

    @GET
    @Path("/consultarDiasPendientesPerMasAntiguo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasPendientesPerMasAntiguo(@QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        BigDecimal retorno = BigDecimal.ZERO;
        retorno = this.persisVacaPend.getDiasPendPeriodoMasAntiguo(seudonimo, nitEmpresa, cadena);
        return retorno;
    }

    @GET
    @Path("/consultarDiasVacacionesProvisionados")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasVacacionesProvisionados(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String esquema = null;
        try {
            esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        System.out.println("Parametros consultarDiasVacacionesProvisionado(): seudonimo: " + seudonimo + ", nitempresa: " + nitEmpresa + " cadena: " + cadena);
        BigDecimal retorno = BigDecimal.ZERO;
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
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
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".consultarDiasVacacionesProvisionados() :" + e);
        }
        return retorno;
    }

    /**
     * Devuelve el total de dias pendientes de vacaciones (periodos cumplidos)
     *
     * @param seudonimo El parametro seudonimo es el usuario
     * @param nitEmpresa El parametro nitEmpresa es el nit de la empresa del
     * empleado
     * @param cadena El parametro cadena es el nombre de la persistencia de bd
     * @return dias de vacaciones pendientes de periodos cumplidos
     */
    @GET
    @Path("/consultarDiasVacacionesPeriodosCumplidos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasVacacionesPeriodosCumplidos(@QueryParam("usuario") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
//        BigDecimal retorno = BigDecimal.ZERO;
        BigDecimal retorno = this.persisVacaPend.getDiasVacacionesPeriodosCumplidos(seudonimo, nitEmpresa, cadena);
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
        System.out.println(this.getClass().getName() + "." + "consultarDiasVacacionesSolicitados" + "()");
        BigDecimal retorno = null;
        BigDecimal documento = this.persisConKiosko.getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena);
        retorno = this.persisVacaPend.getDiasVacacionesSolicitados(documento, nitEmpresa, estado, cadena);
        return retorno;
    }

    @GET
    @Path("/consultaFechaUltimoPago")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Timestamp consultaFechaUltimoPago(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        System.out.println("Parametros consultaFechaUltimoPago(): usuario: " + seudonimo + ", nitempresa: " + nitEmpresa + ", cadena: " + cadena);
        String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        System.out.println(this.getClass().getName() + "." + "consultaFechaUltimoPago" + "()");
        Timestamp retorno = null;
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String consulta = "SELECT GREATEST(\n"
                + " CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1), "
                + " NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 80)"
                + " , CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO( ? , 1 )))"
                + " FROM DUAL ";
        try {
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, secEmpl);
            query.setParameter(3, secEmpl);
            retorno = (Timestamp) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error consultaFechaUltimoPago." + e);
        }
        return retorno;
    }

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
            esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        System.out.println(this.getClass().getName() + "." + "calculaFechaRegreso" + "()");
        List retorno = this.persisVacaPkg.getFechaRegreso(fechainicio, dias, seudonimo, nitEmpresa, cadena, esquema);
        return retorno;
    }

    @GET
    @Path("/getSolicitudesProcesadasXAutorizador")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSolicitudesProcesadasAutorizador(@QueryParam("autorizador") String autorizador,
            @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getSolicitudesProcesadasAutorizador(): " + "Parametros: "
                + "autorizador: " + autorizador
                + " empresa: " + nitEmpresa
                + " cadena: " + cadena);
        int conteo = 0;
        List s = null;
        String sqlQuery = "SELECT \n"
                + " empl.codigoEmpleado cedula \n"
                + ", per.numeroDocumento cedulaP \n"
                + ", REPLACE(TRIM(per.primerApellido||' '||per.segundoApellido||' '||per.nombre), '  ', ' ') nombreCompleto \n"
                + ", TO_CHAR(ksv.fechaGeneracion, 'DD/MM/YYYY HH:MI:SS') solicitud \n"
                + ", TO_CHAR(kns.fechaInicialDisfrute,'DD/MM/YYYY' ) fechaInicialDisfrute \n"
                + ", TO_CHAR(kes.fechaProcesamiento, 'DD/MM/YYYY HH:MI:SS') fechaProcesamiento \n"
                + ", kes.secuencia \n"
                + ", NVL(kes.motivoProcesa, 'N/A') motivo \n"
                + ", TO_CHAR(kns.adelantaPagoHasta, 'DD/MM/YYYY HH:mm:ss') fechafinvacaciones \n"
                + ", TO_CHAR(kns.fechaSiguienteFinVaca,'DD/MM/YYYY') fechaRegresoLaborar \n"
                + ", kns.dias \n"
                + ", (SELECT perAut.primerApellido||' '||perAut.segundoApellido||' '||perAut.nombre \n"
                + "   FROM Personas perAut \n"
                + "   WHERE perAut.secuencia = ksv.autorizador) personaAuto \n"
                + ", TO_CHAR(kns.fechapago, 'DD/MM/YYYY') fechaPago \n"
                + ", kes.estado \n"
                + ", TO_CHAR(v.inicialcausacion, 'DD/MM/YYYY')||' a '||TO_CHAR(v.finalcausacion, 'DD/MM/YYYY') periodo \n"
                + "FROM Empresas em, Empleados empl, Personas per \n"
                + ", KioSoliciVacas ksv, KioNovedadesSolici kns, KioEstadosSolici kes \n"
                + ", VwVacaPendientesEmpleados v \n"
                + "WHERE empl.empresa = em.secuencia \n"
                + "AND per.secuencia = empl.persona \n"
                + "AND ksv.empleado = empl.secuencia \n"
                + "AND ksv.kioNovedadSolici = kns.secuencia \n"
                + "AND kes.kioSoliciVaca = ksv.secuencia \n"
                + "AND v.empleado = empl.secuencia \n"
                + "AND v.rfVacacion = kns.vacacion \n"
                + "AND kes.secuencia = (SELECT MAX(kesi.secuencia) \n"
                + "  FROM KioEstadosSolici kesi \n"
                + "  WHERE kesi.kioSoliciVaca = kes.kioSoliciVaca ) \n"
                + "AND v.inicialCausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(empl.secuencia, sysdate) \n"
                + "AND kes.estado in ('AUTORIZADO', 'RECHAZADO','LIQUIDADO') \n"
                + "AND ksv.autorizador = ? \n"
                + "AND em.nit = ? \n"
                + "ORDER BY \n"
                + "kns.fechaInicialDisfrute, ksv.fechaGeneracion, kes.fechaProcesamiento ";
        try {
            String esquema = null;
            try {
                esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar esquema " + e.getMessage());
            }
//            String secPerAutorizador = getSecPersonaPorSeudonimo(autorizador, nitEmpresa, cadena, esquema);
            String secPerAutorizador = this.persisConKiosko.getPersonaPorSeudonimo(autorizador, nitEmpresa, cadena).toPlainString() ;
            this.rolesBD.setearPerfil(esquema, cadena);

            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPerAutorizador);
            query.setParameter(2, nitEmpresa);
            s = query.getResultList();
            s.forEach(System.out::println);

            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error: " + this.getClass().getName() + "getSolicitudesProcesadasAutorizador()" + ex);
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
        
        this.persisEmpleados = new PersistenciaEmpleados();
        
        String esquema = null;
        try {
            esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        String retorno = "";
        String mensaje = "";
        String secPerKioAutorizador = null;
        String secEmplJefe = null;
        try {
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            if (secEmpleado != null) {
                secPerKioAutorizador = this.persisAutorizaSoli.consultarSecuenciaPorAutorizadorVaca(secEmpleado, nitEmpresa, cadena, esquema);
                retorno = secPerKioAutorizador;
                if (secPerKioAutorizador != null) {
                    // Existe relación con kioautorizadores
                    retorno = this.persisPersonas.getApellidoNombreXSecPer(secPerKioAutorizador, nitEmpresa, cadena, esquema);
                } else {
                    try {
//                        secEmplJefe = consultarSecuenciaEmpleadoJefe(secEmpleado, nitEmpresa, cadena, esquema);
                        secEmplJefe = this.persisEmpleados.consultarSecuenciaEmpleadoJefe(secEmpleado, nitEmpresa, cadena);
                        if (secEmplJefe != null) {
                            retorno = this.persisPersonas.getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
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
            } else {
                mensaje = "El empleado no existe";
            }
        } catch (Exception e) {
            System.out.println("Error : " + "VwvacaPendientesEmpleadosFacadeREST" + ".consultarAutorizaVacaciones " + e.getMessage());
            mensaje = "Se ha presentado un error al hacer la consulta. Si el error persiste por favor comuniquese con el área de Talento humano de su empresa.";
        }
        JsonObject json = Json.createObjectBuilder()
                .add("resultado", retorno)
                .add("mensaje", mensaje)
                .build();
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    /*
    public List getFechaRegreso(String fechainicio, int dias, String seudonimo, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getFechaRegreso(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", fechainicio: " + fechainicio + ", dias: " + dias + ", cadena: " + cadena);
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        List retorno = null;
        String consulta = "SELECT \n"
                + "TO_CHAR(KIOVACACIONES_PKG.CALCULARFECHAFINVACA( ?, TO_DATE(?, 'YYYY-MM-DD') , \n"
                + "KIOVACACIONES_PKG.CALCULARFECHAREGRESO( ? , TO_DATE(?, 'YYYY-MM-DD') , ? ) , 'S' ), 'DD/MM/YYYY') FECHAFIN,\n"
                + "TO_CHAR(KIOVACACIONES_PKG.CALCULARFECHAREGRESO( ? , TO_DATE(?, 'YYYY-MM-DD') , ? ), 'DD/MM/YYYY') FECHAREGRESO\n"
                + "FROM DUAL ";
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
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
    */

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
            esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        System.out.println(this.getClass().getName() + "." + "calculaFechaFinVaca" + "()");
//        Timestamp retorno = getFechaFinVaca(fechainicio, getFechaRegreso(fechainicio, dias, seudonimo, nitEmpresa, cadena, esquema).toString(), dias, seudonimo, nitEmpresa, cadena, esquema);
        Timestamp retorno = this.persisVacaPkg.getFechaFinVaca(fechainicio, this.persisVacaPkg.getFechaRegreso(fechainicio, dias, seudonimo, nitEmpresa, cadena, esquema).toString(), dias, seudonimo, nitEmpresa, cadena, esquema);
        return retorno;
    }

    /*
    public Timestamp getFechaFinVaca(String fechainicio, String fechafin, int dias, String seudonimo, String nitEmpresa, String cadena, String esquema) {
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        Timestamp retorno = null;
        String consulta = "SELECT KIOVACACIONES_PKG.CALCULARFECHAFINVACA( ?, TO_DATE(?, 'YYYY-MM-DD') , TO_DATE(?,'YYYY-MM-DD HH:MM:SS') , 'S' ) FROM DUAL";
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechainicio);
            query.setParameter(3, fechafin);
            retorno = (Timestamp) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error getFechaFinVaca." + e);
        }
        return retorno;
    }
    */

    /*
    public boolean creaKioNovedadSolici(String seudonimo, String nitEmpresa, String fechainicial, String fecharegreso, String dias, String RFVACACION, String fechaFin, String cadena, String esquema) {
        int conteo = 0;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            System.out.println("parametros creaKioNovedadSolici seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", fechainicial: " + fechainicial + ", fecharegreso: " + fecharegreso + " fecha fin: " + fechaFin + " dias: " + dias + ", rfvacacion: " + RFVACACION);
            String sql = "INSERT INTO KIONOVEDADESSOLICI (EMPLEADO, FECHAINICIALDISFRUTE, DIAS, TIPO, SUBTIPO, FECHASISTEMA, FECHASIGUIENTEFINVACA, ESTADO, \n"
                    + "ADELANTAPAGO, ADELANTAPAGOHASTA, FECHAPAGO, PAGADO, VACACION)\n"
                    + "VALUES\n"
                    + "(?, TO_DATE(?,'DD/MM/YYYY'), ?, 'VACACION', 'TIEMPO', SYSDATE, TO_DATE(?,'DD/MM/YYYY'), 'ABIERTO', ?, TO_DATE(?,'DD/MM/YYYY'), ?, 'N', ?)";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sql);
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
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
    */

    /*
    public boolean creaKioSoliciVacas(String seudonimo, String secEmplJefe, String secPersonaAutorizador, String nit, String secNovedad, String fechaGeneracion, String cadena, String esquema) {
        System.out.println("Parametros creaKioSoliciVacas(): seudonimo: " + seudonimo + ", nit: " + nit + ", secNovedad: " + secNovedad + ", fechaGeneracion: " + fechaGeneracion
                + ", autorizador: " + secPersonaAutorizador + ", secEmplJefe: " + secEmplJefe + ", cadena: " + cadena);
        int conteo = 0;
        String secEmpleado = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena);
            String sql = "";
            if (secEmplJefe != null || secPersonaAutorizador != null) {
                if (secPersonaAutorizador != null) {
                    System.out.println("creaKioSoliciVacas por kioautorizador");
                    sql += "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, autorizador, activa, fechageneracion) "
                            + "values (?, ?, user, ?, 'S', to_date(?, 'dd/mm/yyyy HH24miss'))";
                } else if (secEmplJefe != null) {
                    System.out.println("creaKioSoliciVacas por empleadojefe");
                    sql += "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, empleadojefe, activa, fechageneracion) "
                            + "values (?, ?, user, ?, 'S', to_date(?, 'dd/mm/yyyy HH24miss'))";
                }
                Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sql);
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
    */

    /*
    public String getSecuenciaKioNovedadesSolici(String seudonimo, String nitEmpresa,
            String fechainicio, String fecharegreso,
            String dias, String rfVacacion, String cadena, String esquema) {
        System.out.println("Parametros getSecuenciaKioNovedadesSolici(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", fechainicio: " + fechainicio + ", fecharegreso: " + fecharegreso + ", dias: " + dias + ", rfVacacion: " + rfVacacion + ", cadena: " + cadena);
        String sec = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String sqlQuery = "select max(secuencia) \n"
                    + "                from KioNovedadesSolici \n"
                    + "                where dias=? \n"
                    + "                and fechainicialdisfrute=to_date(?, 'dd/mm/yyyy') \n"
                    + "                and fechasiguientefinvaca=to_date(?, 'dd/mm/yyyy') \n"
                    + "                and empleado=? \n"
                    + "                and tipo='VACACION' \n"
                    + "                and SUBTIPO='TIEMPO' \n"
                    + "                and vacacion=? "
                    + "  and secuencia not in (select kionovedadsolici from kiosolicivacas)";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

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
    */

    /*
    public String getSecKioSoliciVacas(String secEmpl, String fechaGeneracion,
            String secEmplJefe, String secPerAutorizador, String kioNovedadSolici, String nitEmpresa, String cadena, String esquema) {
        String secKioSoliciVacas = null;
        String sqlQuery = "";
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            System.out.println("parametros getSecKioSoliciVacas: secEmpl: " + secEmpl + ", fechaGeneracion: " + fechaGeneracion + ", secEmplJefe: " + secEmplJefe + ", autorizador: " + secPerAutorizador + ", kioNovedadSolici " + kioNovedadSolici);
            if (secPerAutorizador != null) {
                sqlQuery += "select secuencia "
                        + "from kiosolicivacas "
                        + "where empleado=? "
                        + " and fechageneracion=to_date(?, 'dd/mm/yyyy HH24miss') "
                        + " and autorizador=? and activa='S' and kionovedadsolici=?";
            } else {
                sqlQuery += "select secuencia from kiosolicivacas where empleado=? "
                        + " and fechageneracion=to_date(?, 'dd/mm/yyyy HH24miss') "
                        + " and empleadojefe=? and activa='S' and kionovedadsolici=?";
            }
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechaGeneracion);
            if (secPerAutorizador != null) {
                query.setParameter(3, secPerAutorizador);
            } else {
                query.setParameter(3, secEmplJefe);
            }
            query.setParameter(4, kioNovedadSolici);
            secKioSoliciVacas = query.getSingleResult().toString();
            System.out.println("SecKioSoliciVacas: " + secKioSoliciVacas);
        } catch (Exception e) {
            System.out.println("Error: getSecKioSoliciVacas: " + e.getMessage());
        }
        return secKioSoliciVacas;
    }
    */

    /*
    public String consultarSecuenciaEmpleadoJefe(String secEmpleado, String nitEmpresa, String cadena, String esquema) {
        System.out.println("parametros consultarSecuenciaEmpleadoJefe: secEmpleado: " + secEmpleado + ", cadena: " + cadena);
        String secJefe = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
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
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            secJefe = query.getSingleResult().toString();
            System.out.println("secuencia jefe: " + secJefe);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".consultarSecuenciaEmpleadoJefe: " + e.getMessage());
        }
        return secJefe;
    }
    */

    /*
    public String getSecPersonaPorSeudonimo(String seudonimo, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getSecPersonaPorSeudonimo(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secuencia = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.SECUENCIA SECUENCIAPERSONA "
                    + "FROM PERSONAS P, CONEXIONESKIOSKOS CK "
                    + "WHERE CK.PERSONA=P.SECUENCIA "
                    + "AND CK.SEUDONIMO=? "
                    + "AND CK.NITEMPRESA=? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuenciaEmpl: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecPersonaPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }
    */

    /*
    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getEmplXsecKioEstadoSolici(): kioEstadoSolici: " + kioEstadoSolici + ", cadena: " + cadena);
        String secEmpl = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.EMPLEADO \n"
                    + "FROM \n"
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN \n"
                    + "WHERE \n"
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA \n"
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA \n"
                    + "AND KE.SECUENCIA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioEstadoSolici);
            secEmpl = query.getSingleResult().toString();
            System.out.println("Valor getEmplXsecKioEstadoSolici(): " + secEmpl);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getEmplXsecKioEstadoSolici(): " + e.getMessage());
        }
        return secEmpl;
    }
    */

    @POST
    @Path("/nuevoEstadoSolici")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response setNuevoEstadoSolici(@QueryParam("secuencia") String secKioEstadoSolici, @QueryParam("motivo") String motivo,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("estado") String estado,
            @QueryParam("cadena") String cadena, @QueryParam("grupo") String grupoEmpr, @QueryParam("urlKiosco") String urlKiosco,
            @QueryParam("fechaInicio") String fechaInicio, @QueryParam("fechaFin") String fechaFin, @QueryParam("dias") String dias,
            @QueryParam("periodo") String periodo) {
        System.out.println("nuevoEstadoSolici()");
        System.out.println("parametros: secuencia: " + secKioEstadoSolici + ", motivo " + motivo + ", empleado " + seudonimo + ", estado: " + estado + ", cadena " + cadena + ", nit: " + nitEmpresa + ", urlKiosco: " + urlKiosco + ", grupoEmpresarial: " + grupoEmpr
                + "fecha inicio " + fechaInicio + ", fechaFin: " + fechaFin + ", dias: " + dias + ", periodo: " + periodo);
        
        this.persisKioSoliciVacas = new PersistenciaKioSoliciVacas();
        
        List s = null;
        int res = 0;
        String urlKio = urlKiosco + "#/login/" + grupoEmpr;
        String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupoEmpr;
        String esquema = null;
        try {
            esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar esquema " + e.getMessage());
        }
        try {
            String secEmplEjecuta = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            System.out.println("La persona que ejecuta es: " + secEmplEjecuta);
            String secEmplSolicita = this.persisKioSoliciVacas.getEmplXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena, esquema);
            String secEmplJefe = null;
            String secPerAutoriza = null;
            String nombreAutorizaSolici = "";
            String correoAutorizaSolici = null;
            String fecha = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date());
            if (motivo == null || motivo.isEmpty()) {
                motivo = " ";
            }
            try {
                secPerAutoriza = this.persisKioSoliciVacas.getSecPerAutorizadorXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar autorizador relacionado a la solicitud");
            }
            if (secPerAutoriza != null) {
                nombreAutorizaSolici = this.persisPersonas.getApellidoNombreXSecPer(secPerAutoriza, nitEmpresa, cadena, esquema);
//                correoAutorizaSolici = getCorreoXsecPer(secPerAutoriza, nitEmpresa, cadena, esquema);
                correoAutorizaSolici = this.persisPersonas.getCorreoPorPersona(secPerAutoriza, nitEmpresa, cadena);
            } else {
                try {
                    secEmplJefe = this.persisKioSoliciVacas.getEmplJefeXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
                    if (secEmplJefe != null) {
                        nombreAutorizaSolici = this.persisPersonas.getApellidoNombreXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
//                        correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nitEmpresa, cadena, esquema);
                        correoAutorizaSolici = this.persisPersonas.getCorreoPorEmpleado(secEmplJefe, nitEmpresa, cadena);
                    }
                } catch (Exception e) {
                    System.out.println("Error al consultar empleadoJefe relacionado a la solicitud");
                }
            }
            String sqlQuery = "";
            Query query = null;
            if (estado.equals("RECHAZADO")) {
                sqlQuery = "INSERT INTO KIOESTADOSSOLICI "
                        + "(KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, NOVEDADSISTEMA, MOTIVOPROCESA, PERSONAEJECUTA) \n"
                        + "SELECT \n"
                        + "KIOSOLICIVACA, SYSDATE FECHAPROCESAMIENTO, ? , ? EMPLEADOEJECUTA "
                        + ", NOVEDADSISTEMA, ?, ? \n"
                        + "FROM KIOESTADOSSOLICI \n"
                        + "WHERE SECUENCIA= ? ";
                this.rolesBD.setearPerfil(esquema, cadena);
                query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, estado);
                query.setParameter(2, secEmplEjecuta);
                query.setParameter(3, motivo);
                if (estado.equals("CANCELADO")) {
                    query.setParameter(4, null);
                    System.out.println("La solicitud está siendo CANCELADA");
                } else {
                    query.setParameter(4, secPerAutoriza);
                }
                query.setParameter(5, secKioEstadoSolici);
            } else {
                sqlQuery = "INSERT INTO KIOESTADOSSOLICI "
                        + "(KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, NOVEDADSISTEMA, PERSONAEJECUTA) \n"
                        + "SELECT \n"
                        + "KIOSOLICIVACA, SYSDATE FECHAPROCESAMIENTO, ?, ? EMPLEADOEJECUTA"
                        + ", NOVEDADSISTEMA, ? \n"
                        + "FROM KIOESTADOSSOLICI \n"
                        + "WHERE SECUENCIA=?";
                this.rolesBD.setearPerfil(esquema, cadena);
                query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, estado);
                query.setParameter(2, secEmplEjecuta);
                if (estado.equals("CANCELADO")) {
                    query.setParameter(3, null);
                    System.out.println("La solicitud está siendo CANCELADA");
                } else {
                    query.setParameter(3, secPerAutoriza);
                }
                query.setParameter(4, secKioEstadoSolici);
            }
            res = query.executeUpdate();
            //EnvioCorreo c = new EnvioCorreo();
            GenerarCorreo c = new GenerarCorreo();
            String estadoVerbo = estado.equals("CANCELADO") ? "CANCELAR"
                    : estado.equals("AUTORIZADO") ? "PRE-APROBAR"
                    : estado.equals("RECHAZADO") ? "RECHAZAR" : estado;
            String estadoPasado = estado.equals("CANCELADO") ? "canceló"
                    : estado.equals("AUTORIZADO") ? "pre-aprobó"
                    : estado.equals("RECHAZADO") ? "rechazó" : estado;
            String mensaje = "<div style=\"color: black;\"><p>Nos permitimos informar que se acaba de " + estadoVerbo + " una solicitud de vacaciones";
            if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")) {
                mensaje += " creada para " + this.persisPersonas.getApellidoNombreXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema);
            }
            mensaje += " en el módulo de Kiosco Nómina Designer.";
            if (estado.equals("AUTORIZADO")) {
                mensaje += " Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso.";
            }
            mensaje += "</p>"
                    + "<br><br>"
                    + "<p>La persona que " + estadoPasado.toUpperCase() + " LA SOLICITUD es: " + this.persisPersonas.getApellidoNombreXsecEmpl(secEmplEjecuta, nitEmpresa, cadena, esquema) + "</p><br>";
            if (estado.equals("CANCELADO")) {
                mensaje += "La persona a cargo de HACER EL SEGUIMIENTO es: " + nombreAutorizaSolici + "<br>";
            }
            if (estado.equals("AUTORIZADO")) {
                mensaje += "Por favor seguir el proceso en: <a style='color: black !important;' target='_blank' href=" + urlKio + ">" + urlKio + "</a>";
                mensaje += "<br><br>"
                        + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita.<br><br>"
                        + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                        + "<br><a style='color: black !important;' href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a></div>";
            }

            String fechaInicioDisfrute = this.persisKioSoliciVacas.getFechaInicioXsecKioEstadoSolici(secKioEstadoSolici, nitEmpresa, cadena);
            System.out.println("url Kiosco: " + urlKio);
            if (res > 0) {
                System.out.println("solicitud " + estado + " con éxito.");
//                String servidorsmtp = getConfigCorreoServidorSMTP(nitEmpresa, cadena, esquema);
//                String puerto = getConfigCorreo(nitEmpresa, "PUERTO", cadena, esquema);
//                String autenticado = getConfigCorreo(nitEmpresa, "AUTENTICADO", cadena, esquema);
//                String starttls = getConfigCorreo(nitEmpresa, "STARTTLS", cadena, esquema);
//                String remitente = getConfigCorreo(nitEmpresa, "REMITENTE", cadena, esquema);
//                String clave = getConfigCorreo(nitEmpresa, "CLAVE", cadena, esquema);
                if (estado.equals("CANCELADO")) {
                    //if (c.enviarCorreoVacaciones(
                    //        servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                    //        getCorreoXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema),
                    //        "Solicitud de VACACIONES Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de VACACIONES: " + fechaInicioDisfrute,
                    //        mensaje, urlKio, nitEmpresa, cadena)) {
                    if (c.enviarCorreoVacaciones(
                            this.persisPersonas.getCorreoPorEmpleado(secEmplSolicita, nitEmpresa, cadena),
                             "Solicitud de VACACIONES Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de VACACIONES: " + fechaInicioDisfrute,
                             mensaje, nitEmpresa, cadena, urlKio)) {
                        System.out.println("Correo enviado a la persona que ejecuta");
                    }
                }

                if (estado.equals("AUTORIZADO") || estado.equals("RECHAZADO")) {
                    //if (c.enviarCorreoVacaciones(
                    //        servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                    //        getCorreoXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema),
                    //        "Solicitud de VACACIONES Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de VACACIONES: " + fechaInicioDisfrute,
                    //        mensaje, urlKio, nitEmpresa, cadena)) {
                    if (c.enviarCorreoVacaciones(
                            this.persisPersonas.getCorreoPorEmpleado(secEmplSolicita, nitEmpresa, cadena),
                            "Solicitud de VACACIONES Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de VACACIONES: " + fechaInicioDisfrute,
                            mensaje, nitEmpresa, cadena, urlKio)) {
                        System.out.println("Correo enviada a la persona que ejecuta");
                    }
                    //if (c.enviarCorreoVacaciones(
                    //        servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                    //        correoAutorizaSolici,
                    //        "Solicitud de VACACIONES Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de VACACIONES: " + fechaInicioDisfrute,
                    //        mensaje, urlKio, nitEmpresa, cadena)) {
                    if (c.enviarCorreoVacaciones(
                            correoAutorizaSolici,
                            "Solicitud de VACACIONES Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de VACACIONES: " + fechaInicioDisfrute,
                            mensaje, nitEmpresa, cadena, urlKio)) {
                        System.out.println("Correo enviado al empleado que solicita asociado " + correoAutorizaSolici);
                    }

                    // Enviar correo de autoria
                    if (estado.equals("AUTORIZADO")) {
                        boolean auditProcesarJefe = this.persisConfigModu.consultaAuditoria("SOLICITUDVACACIONES", "33", nitEmpresa, cadena).compareTo(BigDecimal.ZERO) > 0;
                        boolean auditProcesarAutorizador = this.persisConfigModu.consultaAuditoria("SOLICITUDVACACIONES", "35", nitEmpresa, cadena).compareTo(BigDecimal.ZERO) > 0;
                        System.out.println("auditoria para solicitudes de vacas procesar 1 " + auditProcesarJefe);
                        System.out.println("auditoria para solicitudes de vacas procesar 2 " + auditProcesarAutorizador);
                        if (auditProcesarJefe || auditProcesarAutorizador) {
                            System.out.println("Si debe llevar auditoria procesar solicitud Vacaciones");
                            String sqlQueryAud = "select email from kioconfigmodulos where codigoopcion=? and nitempresa=?";
                            System.out.println("Query2: " + sqlQueryAud);
                            Query query2 = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQueryAud);
                            if (auditProcesarJefe) {
                                query2.setParameter(1, "33");
                                System.out.println("codigoOpcion 33");
                            } else {
                                query2.setParameter(1, "35");
                                System.out.println("codigoOpcion 35");
                            }
                            query2.setParameter(2, nitEmpresa);
                            List lista = query2.getResultList();
                            Iterator<String> it = lista.iterator();
                            System.out.println("obtener " + lista.get(0));
                            System.out.println("size: " + lista.size());
                            String mensajeAuditoria = "Nos permitimos informar que se acaba de " + estadoVerbo + " una solicitud de VACACIONES";
                            if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")) {
                                mensajeAuditoria += " creada para " + this.persisPersonas.getApellidoNombreXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema);
                            }
                            mensajeAuditoria += " en el módulo de Kiosco Nómina Designer. Por favor continuar con el proceso desde el aplicativo designer para generar la novedad de nomina."
                                    + "<br><br>"
                                    + "La persona que " + estadoPasado.toUpperCase() + " LA SOLICITUD es: " + nombreAutorizaSolici//getApellidoNombreXsecEmpl(secEmplSolicita, nitEmpresa, cadena, esquema)+"<br>";
                                    + "<br>"
                                    + "La solicitud se creó por " + dias + " días, para ser disfrutados desde el " + fechaInicio + " hasta el " + fechaFin
                                    + "<br>"
                                    + " Del periodo: " + periodo
                                    + "<br>";
                            while (it.hasNext()) {
                                String correoenviar = it.next();
                                System.out.println("correo auditoria: " + correoenviar);
                                c.enviarCorreoInformativo(
                                        correoenviar
                                        , null
                                        , "Auditoria: Se ha " + estadoPasado + " una Solicitud de vacaciones Kiosco. " + fecha
                                        , "Estimado usuario: ", mensajeAuditoria, nitEmpresa, cadena, urlKio);
                            }
                        } else {
                            System.out.println("No lleva auditoria Vacaciones");
                        }
                    }
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

    /*
    public String getFechaInicioXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        String fechaInicio = null;
        try {
            String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select "
                    + "TO_CHAR(KN.FECHAINICIALDISFRUTE, 'dd/mm/yyyy') "
                    + "from "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA = KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI=KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            fechaInicio = query.getSingleResult().toString();
            System.out.println("Fecha inicio: " + fechaInicio);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getFechaInicioXsecKioEstadoSolici: " + e.getMessage());
        }
        return fechaInicio;
    }
    */

    /*
    public String getEmplJefeXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        String secEmplJefe = null;
        try {
            String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.EMPLEADOJEFE "
                    + "FROM "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secEmplJefe = query.getSingleResult().toString();
            System.out.println("Empl jefe asociado: " + secEmplJefe);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getEmplJefeXsecKioEstadoSolici: " + e.getMessage());
        }
        return secEmplJefe;
    }
    */

    /*
    public String getSecPerAutorizadorXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecPerAutorizadorXsecKioEstadoSolici(): secKioEstadoSolici: " + secKioEstadoSolici + " nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secPerAutorizador = null;
        try {
            String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.AUTORIZADOR "
                    + "FROM "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secPerAutorizador = query.getSingleResult().toString();
            System.out.println("Secuencia persona KioAutorizador: " + secPerAutorizador);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecPerAutorizadorXsecKioEstadoSolici: " + e.getMessage());
        }
        return secPerAutorizador;
    }
    */

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
                esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            } catch (Exception e) {
                System.out.println("Error al consultar esquema " + e.getMessage());
            }
            String secuenciaEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(empleado, nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT "
                    + "    tablatotal.empleado, 'TOTAL' tipo, round(nvl(SUM(tablatotal.dias), 0), 2) dias \n"
                    + "FROM \n"
                    + "    ( \n"
                    + "        SELECT \n"
                    + "            e.secuencia empleado, SUM(n.dias) dias \n"
                    + "        FROM \n"
                    + "            novedadessistema n, empleados e, vwvacapendientesempleados v \n"
                    + "        WHERE \n"
                    + "            e.secuencia = n.empleado \n"
                    + "            AND n.tipo = 'VACACION' \n"
                    + "            AND n.subtipo IN ('TIEMPO', 'DINERO') \n"
                    + "            AND v.rfvacacion = n.vacacion \n"
                    + "            AND v.inicialcausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate) \n"
                    + "        GROUP BY e.secuencia \n"
                    + "        UNION \n"
                    + "        SELECT \n"
                    + "            e.secuencia empleado, SUM(k.diasvacadisfrute) dias \n"
                    + "        FROM \n"
                    + "            kioacumvaca k, empleados e \n"
                    + "        WHERE e.secuencia = k.empleado(+) \n"
                    + "        GROUP BY e.secuencia \n"
                    + "        UNION \n"
                    + "        SELECT \n"
                    + "            e.secuencia empleado, SUM(k.diasvacadinero) dias \n"
                    + "        FROM \n"
                    + "            kioacumvaca k, empleados e \n"
                    + "        WHERE e.secuencia = k.empleado(+) \n"
                    + "        GROUP BY e.secuencia \n"
                    + "    ) tablatotal, \n"
                    + "    empleados e \n"
                    + "WHERE \n"
                    + "    tablatotal.empleado = e.secuencia \n"
                    + "    AND e.secuencia = ? \n"
                    + "GROUP BY tablatotal.empleado \n"
                    + "UNION \n"
                    + "( SELECT tabla.empleado secuenciaempl, tabla.tipo tipo, nvl(SUM(tabla.dias), 0) dias \n"
                    + "FROM \n"
                    + "    ( \n"
                    + "        ( SELECT e.secuencia empleado, nvl(SUM(n.dias), 0) dias, 'DINERO' tipo \n"
                    + "        FROM \n"
                    + "            novedadessistema n, empleados e, vwvacapendientesempleados v \n"
                    + "        WHERE \n"
                    + "            e.secuencia = n.empleado \n"
                    + "            AND n.tipo = 'VACACION' \n"
                    + "            AND n.subtipo = 'DINERO' \n"
                    + "            AND v.rfvacacion = n.vacacion \n"
                    + "            AND v.inicialcausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate) \n"
                    + "        GROUP BY e.secuencia \n"
                    + "        UNION \n"
                    + "        SELECT e.secuencia empleado, nvl(SUM(k.diasvacadinero), 0) dias, 'DINERO' tipo \n"
                    + "        FROM \n"
                    + "            kioacumvaca k, empleados e \n"
                    + "        WHERE \n"
                    + "            e.secuencia = k.empleado (+) \n"
                    + "        GROUP BY e.secuencia \n"
                    + "        ) \n"
                    + "        UNION \n"
                    + "        ( SELECT \n"
                    + "            e.secuencia empleado, \n"
                    + "            round(nvl(SUM(n.dias), 0), 2) dias, \n"
                    + "            'TIEMPO' tipo \n"
                    + "        FROM \n"
                    + "            novedadessistema n, empleados e, vwvacapendientesempleados v \n"
                    + "        WHERE \n"
                    + "            e.secuencia = n.empleado \n"
                    + "            AND n.tipo = 'VACACION' \n"
                    + "            AND n.subtipo = 'TIEMPO' \n"
                    + "            AND v.rfvacacion = n.vacacion \n"
                    + "            AND v.inicialcausacion >= empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate) \n"
                    + "        GROUP BY e.secuencia \n"
                    + "        UNION \n"
                    + "        SELECT \n"
                    + "            e.secuencia empleado, round(nvl(SUM(k.diasvacadisfrute), 0), 2) dias, 'TIEMPO' tipo \n"
                    + "        FROM \n"
                    + "            kioacumvaca k, empleados e \n"
                    + "        WHERE \n"
                    + "            e.secuencia = k.empleado(+) \n"
                    + "        GROUP BY e.secuencia \n"
                    + "        ) \n"
                    + "    ) tabla \n"
                    + "WHERE \n"
                    + "    tabla.empleado = ? \n"
                    + "GROUP BY tabla.empleado, tabla.tipo \n"
                    + ")";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
 /*
    public boolean creaKioEstadoSolici(
            String seudonimo, String nit, String kioSoliciVaca,
            String fechaProcesa, String estado, String motivo, String cadena, String esquema) {
        System.out.println("parametros creaKioEstadoSolici(): seudonimo: " + seudonimo + ", nit: " + nit + ", kiosolicivaca: " + kioSoliciVaca + ""
                + "\n fechaProcesa: " + fechaProcesa + ", estado: " + estado + ", cadena: " + cadena);
        int res = 0;
        try {
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "INSERT INTO KIOESTADOSSOLICI (KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, MOTIVOPROCESA)\n"
                    + "VALUES (?, to_date(?, 'dd/mm/yyyy HH24miss'), ?, ?, ?)";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioSoliciVaca);
            query.setParameter(2, fechaProcesa);
            query.setParameter(3, estado);
            query.setParameter(4, secEmpl);
            query.setParameter(5, motivo);
            res = query.executeUpdate();
            System.out.println("registro kioestadosolici: " + res);
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".creaKioEstadoSolici: " + ex.getMessage());
            return false;
        }
        return res > 0;
    }
     */
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
        
        this.persisKioSoliciVacas = new PersistenciaKioSoliciVacas();
        this.persisEmpleados = new PersistenciaEmpleados();
        
        boolean soliciCreada = false;
        boolean soliciValida = false;
        String esquema = null;
        try {
            esquema = cadenasKio.getEsquema(nit, cadena);
        } catch (Exception e) {
            System.out.println("Error: No se pudo consultar esquema. " + e.getMessage());
        }
        String mensaje = "";
        String urlKio = urlKiosco + "#/login/" + grupoEmpr;
        String urlKioOlvidoClave = urlKiosco + "#/olvidoClave/" + grupoEmpr;
        try {
            boolean res = false;
            this.persisManejoFechas = new PersistenciaManejoFechas();
            Calendar clFechaPago = Calendar.getInstance();
            clFechaPago.setTime(this.persisSolNodos.getFechaUltimoPago(seudonimo, nit, cadena, esquema));
            boolean valFPago = !persisManejoFechas.getDate(fechainicial, cadena).after(clFechaPago.getTime());
            boolean valTraslap = !BigDecimal.ZERO.equals(this.persisVacaPkg.consultaTraslapamientos(seudonimo, nit, fechainicial, fechafin, cadena));
            boolean valFInicial = (this.persisVacaPkg.verificaExistenciaSolicitud(seudonimo, nit, fechainicial, cadena).compareTo(BigDecimal.ZERO) == 1);
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
                    String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena);
                    String secEmplJefe = null;
                    String secPersonaAutorizador = null;
                    String personaCreaSolici = this.persisPersonas.getApellidoNombreXsecEmpl(secEmpl, nit, cadena, esquema);
                    String nombreAutorizaSolici = "";
                    String correoAutorizaSolici = null;

                    // Consultar EmpleadoJefe/kioAutorizador
                    try {
                        secPersonaAutorizador = this.persisAutorizaSoli.consultarSecuenciaPorAutorizadorVaca(secEmpl, nit, cadena, esquema);
                    } catch (Exception e) {
                        System.out.println("Error consultando autorizador: " + e.getMessage());
                    }
                    if (secPersonaAutorizador == null) {
                        try {
//                            secEmplJefe = consultarSecuenciaEmpleadoJefe(secEmpl, nit, cadena, esquema);
                            secEmplJefe = this.persisEmpleados.consultarSecuenciaEmpleadoJefe(secEmpl, nit, cadena);
                            if (secEmplJefe != null) {
                                System.out.println("creaKioSoliciVacas: EmpleadoJefe: " + secEmplJefe);
                                nombreAutorizaSolici += this.persisPersonas.getApellidoNombreXsecEmpl(secEmplJefe, nit, cadena, esquema);
//                                correoAutorizaSolici = getCorreoXsecEmpl(secEmplJefe, nit, cadena, esquema);
                                correoAutorizaSolici = this.persisPersonas.getCorreoPorEmpleado(secEmplJefe, nit, cadena);
                                System.out.println("El empleado tiene relacionado a empleadoJefe " + nombreAutorizaSolici + " - " + correoAutorizaSolici);
                            } else {
                                System.out.println("El empleado jefe está vacío");
                            }
                        } catch (Exception e) {
                            System.out.println("Error al consultar jefe");
                        }
                    } else {
                        nombreAutorizaSolici += this.persisPersonas.getApellidoNombreXSecPer(secPersonaAutorizador, nit, cadena, esquema);
//                        correoAutorizaSolici = getCorreoXsecPer(secPersonaAutorizador, nit, cadena, esquema);
                        correoAutorizaSolici = this.persisPersonas.getCorreoPorPersona(secPersonaAutorizador, nit, cadena);
                        System.out.println("El empleado tiene relacionado al autorizador " + nombreAutorizaSolici + " - " + correoAutorizaSolici);
                    }

                    if (secEmplJefe != null || secPersonaAutorizador != null) {
                        System.out.println("Si hay un jefe/autorizador relacionado");
                        // Insertar registro en kionovedadessolici
                        if ( this.persisKioSoliciVacas.creaKioNovedadSolici(seudonimo, nit, fechainicial, fecharegreso, dias, RFVACACION, fechafin, cadena, esquema)) {
                            String secKioNovedad = this.persisKioSoliciVacas.getSecuenciaKioNovedadesSolici(seudonimo, nit, fechainicial, fecharegreso, dias, RFVACACION, cadena, esquema);
                            System.out.println("secuencia kionovedadsolici creada: " + secKioNovedad);
                            // Insertar registro en kiosolicivacas
                            if (this.persisKioSoliciVacas.creaKioSoliciVacas(seudonimo, secEmplJefe, secPersonaAutorizador, nit, secKioNovedad, fechaGeneracion, cadena, esquema)) {
                                String secKioSoliciVacas = this.persisKioSoliciVacas.getSecKioSoliciVacas(secEmpl, fechaGeneracion, secEmplJefe, secPersonaAutorizador, secKioNovedad, nit, cadena, esquema);
                                System.out.println("secuencia kiosolicivacas creada: " + secKioSoliciVacas);
                                // Insertar registro en kioestadossolici
                                if (this.persisKioSoliciVacas.creaKioEstadoSolici(seudonimo, nit, secKioSoliciVacas, fechaGeneracion, "ENVIADO", null, cadena, esquema)) {
                                    System.out.println("SOLICITUD DE VACACIONES CREADA EXITOSAMENTE!!!");
                                    soliciCreada = true;
                                    String Periosidad = this.persisVacaPend.getPeriodoVacas(secEmpl, RFVACACION, cadena, nit);
                                    mensaje = "Solicitud creada exitosamente.";
                                    String mensajeCorreo = "Nos permitimos informar que se acaba de crear una solicitud de vacaciones en el módulo de autogestión Kiosco. Por favor llevar el caso desde su cuenta de usuario en el portal del módulo autogestión Kiosco y continuar con el proceso."
                                            + " <br><br> "
                                            + "La persona que CREÓ LA SOLICITUD es: " + personaCreaSolici
                                            + "<br>"
                                            + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici
                                            + "<br>"
                                            + "La solicitud se creó por " + dias + " días, para ser disfrutados desde el " + fechainicial + " hasta el " + fechafin
                                            + "<br>"
                                            + "Del periodo: " + Periosidad
                                            + "<br><br>Por favor seguir el proceso en: <a style='color: white !important;' href='" + urlKio + "'>" + urlKio + "</a>"
                                            + "<br><br>"
                                            + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita."
                                            + "<br><br>"
                                            + "En caso de que haya olvidado su clave podrá generar una nueva haciendo clic en ¿Olvidó su clave? en el módulo Kiosco o a través del link: "
                                            + "<br><a style='color: white !important;' href='" + urlKioOlvidoClave + "'>" + urlKioOlvidoClave + "</a>";

                                    //EnvioCorreo c = new EnvioCorreo();
                                    GenerarCorreo c = new GenerarCorreo();

//                                    String servidorsmtp = getConfigCorreoServidorSMTP(nit, cadena, esquema);
//                                    String puerto = getConfigCorreo(nit, "PUERTO", cadena, esquema);
//                                    String autenticado = getConfigCorreo(nit, "AUTENTICADO", cadena, esquema);
//                                    String starttls = getConfigCorreo(nit, "STARTTLS", cadena, esquema);
//                                    String remitente = getConfigCorreo(nit, "REMITENTE", cadena, esquema);
//                                    String clave = getConfigCorreo(nit, "CLAVE", cadena, esquema);
                                    //if (c.enviarCorreoVacaciones(
                                    //        servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                                    //        getCorreoXsecEmpl(secEmpl, nit, cadena, esquema),
                                    //        "Solicitud de VACACIONES Kiosco - Nueva solicitud: " + fechaCorreo + ". Inicio de VACACIONES: " + fechainicial,
                                    //        mensajeCorreo, urlKio, nit, cadena)) {
                                    if (c.enviarCorreoVacaciones(
                                            this.persisPersonas.getCorreoPorEmpleado(secEmpl, nit, cadena),
                                             "Solicitud de VACACIONES Kiosco - Nueva solicitud: " + fechaCorreo + ". Inicio de VACACIONES: " + fechainicial,
                                             mensajeCorreo, nit, cadena, urlKio)) {
                                        System.out.println("Correo enviado al empleado.");
                                    }

                                    // Enviar correo al jefe o autorizador de vacaciones:
                                    //if (c.enviarCorreoVacaciones(
                                    //        servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                                    //        correoAutorizaSolici,
                                    //        "Solicitud de VACACIONES Kiosco - Nueva solicitud: " + fechaCorreo + ". Inicio de VACACIONES: " + fechainicial,
                                    //        mensajeCorreo, urlKio, nit, cadena)) {
                                    if (c.enviarCorreoVacaciones(
                                            correoAutorizaSolici,
                                             "Solicitud de VACACIONES Kiosco - Nueva solicitud: " + fechaCorreo + ". Inicio de VACACIONES: " + fechainicial,
                                             mensajeCorreo, nit, cadena, urlKio)) {
                                        System.out.println("Correo enviado al jefe.");
                                    }

                                    try {
                                        System.out.println("Consulta si está activa la auditoria..");
                                        if (this.persisConfigModu.consultaAuditoria("SOLICITUDVACACIONES", "31", nit, cadena).compareTo(BigDecimal.ZERO) > 0) {
                                            System.out.println("Si debe llevar auditoria crearSolicitud Vacaciones");
                                            String sqlQuery = "select email from kioconfigmodulos where codigoopcion=? and nitempresa=?";
                                            System.out.println("Query2: " + sqlQuery);
                                            Query query2 = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                                            query2.setParameter(1, "31");
                                            query2.setParameter(2, nit);
                                            List lista = query2.getResultList();
                                            Iterator<String> it = lista.iterator();
                                            System.out.println("obtener " + lista.get(0));
                                            System.out.println("size: " + lista.size());
                                            String mensajeAuditoria = "Nos permitimos informar que " + personaCreaSolici
                                                    + " generó una SOLICITUD DE VACACIONES el " + fechaCorreo + " a las " + horaGeneracion + " en el módulo de autogestión Kiosco."
                                                    + "<br>"
                                                    + "La solicitud se creó por " + dias + " días, para ser disfrutados desde el " + fechainicial + " hasta el " + fechafin
                                                    + "<br>"
                                                    + "La persona a cargo de DAR APROBACIÓN es: " + nombreAutorizaSolici + ".";
                                            while (it.hasNext()) {
                                                String correoenviar = it.next();
                                                System.out.println("correo auditoria: " + correoenviar);
                                                System.out.println("codigoopcion: " + "31");
                                                c.enviarCorreoInformativo(
                                                        correoenviar 
                                                        , null
                                                        , "Auditoria: Nueva Solicitud de VACACIONES Kiosco. " + fechaCorreo
                                                        , "Estimado usuario: ", mensajeAuditoria, nit, cadena, urlKio);
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
                    System.out.println("Error-1:"+"VwvacaPendientesEmpleadosFacadeREST" + ".crearSolicitudVacaciones(): " + e.toString());
                    soliciCreada = false;
                    mensaje = "Ha ocurrido un error, por favor inténtelo de nuevo más tarde.";
                }
            }

        } catch (Exception e) {
            System.out.println("Error-2: "+"VwvacaPendientesEmpleadosFacadeREST" + ".crearSolicitudVacaciones(): Error-2: " + e.toString());
            e.printStackTrace();
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

    /*
    public String getCorreoSoporteKiosco(String nitEmpresa, String cadena) {
        System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getConfigCorreoServidorSMTP()");
        String emailSoporte = "";
        String sqlQuery = "SELECT EMAILCONTACTO "
                + "FROM KIOPERSONALIZACIONES "
                + "WHERE TIPOCONTACTO = 'NOMINA' "
                + "EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT= ? ) "
                + "AND ROWNUM<=1";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getConfigCorreoServidorSMTP(): " + "Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            emailSoporte = query.getSingleResult().toString();
            System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getConfigCorreoServidorSMTP(): " + "emailSoporte: " + emailSoporte);
        } catch (Exception e) {
            System.out.println("VwvacaPendientesEmpleadosFacadeREST" + ".getConfigCorreoServidorSMTP(): " + "Error-1: " + e.toString());
        }
        return emailSoporte;
    }
     */

 /*
    public String getConfigCorreo(String nitEmpresa, String valor, String cadena, String esquema) {
        System.out.println("getPathArchivosPlanos()");
        String servidorsmtp = "smtp.designer.com.co";
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT " + valor + " "
                    + "FROM CONFICORREOKIOSKO "
                    + "WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT= ? )";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp = query.getSingleResult().toString();
            System.out.println(valor + ": " + servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreo(): " + e.getMessage());
        }
        return servidorsmtp;
    }
     */

 /*
    public String getConfigCorreoServidorSMTP(String nitEmpresa, String cadena, String esquema) {
        System.out.println("getConfigCorreoServidorSMTP(): nit: " + cadena + ", cadena: " + cadena);
        String servidorsmtp = "smtp.designer.com.co";
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT SERVIDORSMTP "
                    + "FROM CONFICORREOKIOSKO "
                    + "WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp = query.getSingleResult().toString();
            System.out.println("Servidor smtp: " + servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreoServidorSMTP: " + e.getMessage());
        }
        return servidorsmtp;
    }*/

 /*
    public String getCorreoXsecEmpl(String secEmpl, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getCorreoXsecEmpl(): secEmpl: " + secEmpl + ", cadena: " + cadena);
        System.out.println("sec Empleado: " + secEmpl);
        String correo = null;
        String sqlQuery;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            sqlQuery = "SELECT P.EMAIL "
                    + " FROM EMPLEADOS E, PERSONAS P "
                    + " WHERE  "
                    + " P.SECUENCIA=E.PERSONA "
                    + " AND E.SECUENCIA= ? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            System.out.println("getCorreoXsecEmpl(): " + correo);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + "getCorreoXsecEmpl(): " + e.getMessage());
        }
        return correo;
    }
     */

 /*
    public String getCorreoXsecPer(String secPersona, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getCorreoXsecPer(): secPer: " + secPersona + ", cadena: " + cadena);
        String correo = null;
        String sqlQuery;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            sqlQuery = "SELECT P.EMAIL "
                    + " FROM PERSONAS P "
                    + " WHERE "
                    + " P.SECUENCIA=? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPersona);
            System.out.println("getCorreoXsecPer(): " + correo);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + "getCorreoXsecPer(): " + e.getMessage());
        }
        return correo;
    }
     */

 /*
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
        String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("secuencia: " + secEmpleado);
        System.out.println("fecha en txt: " + fechaDisfrute);
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechaDisfrute);
            codigoJornada = new BigDecimal(query.getSingleResult().toString());
            return codigoJornada;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
            return null;
        } catch (Exception e) {
            System.out.println("Error general. " + e);
            throw new Exception(e.toString());
        }
    }
     */

 /*
    public String getEsquema(String nitempresa, String cadena) {
        System.out.println("Parametros getEsquema(): nitempresa: " + nitempresa + ", cadena: " + cadena);
        String esquema = null;
        String sqlQuery;
        try {
            sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=? ";
            Query query = this.persisConexiones.getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitempresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("Esquema: " + esquema);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getEsquema(): " + e);
        }
        return esquema;
    }
     */
}
