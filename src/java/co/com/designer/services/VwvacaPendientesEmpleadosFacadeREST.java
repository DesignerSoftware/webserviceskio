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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
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
        }
    }
   
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List findAlls() {
         setearPerfil();
         return super.findAll();
    }
   
    @GET
    @Path("/consultarPeriodosPendientesEmpleado")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List consultarPeriodosPendientesEmpleado(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa) throws Exception {
        System.out.println(this.getClass().getName() + "." + "consultarPeriodosPendientesEmpleado" + "()");
        List<VwVacaPendientesEmpleados> periodosPendientes = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        Query query = null;
        //String consulta = "select KIOVACACIONES_PKG.DIASDISPOPER(vw.rfVacacion), vw.rfVacacion, vw.empleado, vw.inicialCausacion, vw.finalCausacion, vw.diasPendientes from VwVacaPendientesEmpleados vw where vw.diasPendientes > 0 and vw.empleado.codigoempleado = :codEmple ";
        String consulta="SELECT \n" +
        "VW.RFVACACION, KIOVACACIONES_PKG.DIASDISPOPER(VW.RFVACACION) DIASPENDIENTES, VW.DIASPENDIENTES DIASPENDIENTESREALES, \n" +
        "VW.FINALCAUSACION, VW.INICIALCAUSACION, TO_CHAR(VW.INICIALCAUSACION, 'dd/mm/yyyy')||' a '||TO_CHAR(VW.FINALCAUSACION, 'dd/mm/yyyy') PERIODO \n" +
        "FROM \n" +
        "VWVACAPENDIENTESEMPLEADOS VW, EMPLEADOS E\n" +
        "WHERE \n" +
        "VW.EMPLEADO=E.SECUENCIA AND\n" +
        "((DIASPENDIENTES > 0) AND (E.CODIGOEMPLEADO = ?))";
        try {
            BigDecimal codigoEmpleado = new BigDecimal(documento);
            query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, codigoEmpleado);
            periodosPendientes = query.getResultList();
            return periodosPendientes;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
//            throw new Exception(npee.toString());
            return null;
        } catch (Exception e) {
            System.out.println("Error general." + e);
            throw new Exception(e.toString());
        }
    }
    
    @GET
    @Path("/consultarPeriodoMasAntiguo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<VwVacaPendientesEmpleados> consultarPeriodoMasAntiguo(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "consultarPeriodoMasAntiguo" + "()");
        List<VwVacaPendientesEmpleados> retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        String consulta = "select vw.* "
                + " from VwVacaPendientesEmpleados vw "
                + " where vw.empleado = (select ei.secuencia from empleados ei, empresas emi "
                + "where ei.empresa=emi.secuencia and ei.codigoempleado=?) "
                + " and vw.inicialCausacion = ( select min( vwi.inicialCausacion ) "
                + " from VwVacaPendientesEmpleados vwi "
                + " where vwi.empleado = vw.empleado "
                + " and KIOVACACIONES_PKG.DIASDISPOPER(vwi.rfVacacion) > 0 "
                + "and vwi.inicialCausacion >=empleadocurrent_pkg.fechatipocontrato(vw.empleado, sysdate) ) ";
        try {
            Query query = getEntityManager().createNativeQuery(consulta, VwVacaPendientesEmpleados.class);
            query.setParameter(1, documento);
            retorno = query.getResultList();
        } catch (Exception e) {
            System.out.println("Error general." + e);
        }
        return retorno;
    }
    
    @GET
    @Path("/consultarDiasPendientesPerMasAntiguo")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasPendientesPerMasAntiguo(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "consultarPeriodoMasAntiguo" + "()");
        BigDecimal retorno = new BigDecimal(BigInteger.ZERO);
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        // String consulta = "select vw.diasPendientes "
        String consulta = "select KIOVACACIONES_PKG.DIASDISPOPER(vw.rfVacacion) diaspendientes "
                + " from VwVacaPendientesEmpleados vw "
                + " where vw.empleado = (select ei.secuencia from empleados ei, empresas emi "
                + "where ei.empresa=emi.secuencia and ei.codigoempleado=?) "
                + " and vw.inicialCausacion = ( select min( vwi.inicialCausacion ) "
                + " from VwVacaPendientesEmpleados vwi "
                + " where vwi.empleado = vw.empleado "
                + " and KIOVACACIONES_PKG.DIASDISPOPER(vwi.rfVacacion) > 0 "
                + "and vwi.inicialCausacion >=empleadocurrent_pkg.fechatipocontrato(vw.empleado, sysdate) ) ";
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, documento);
            retorno = (BigDecimal) query.getSingleResult();
            System.out.println("Dias pendientes periodo más antiguo: " + retorno);
        } catch (Exception e) {
            System.out.println("Error consultarDiasPendientesPerMasAntiguo." + e);
        }
        return retorno;
    }
    
    @GET
    @Path("/consultarDiasVacacionesProvisionados")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasVacacionesProvisionados(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "consultarDiasVacacionesProvisionados" + "()");
        BigDecimal retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
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
                + "and empl.secuencia= (SELECT secuencia from empleados where codigoempleado=?) ";
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, documento);
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error general." + e);
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
            @QueryParam("estado") String estado) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "consultarDiasVacacionesSolicitados" + "()");
        BigDecimal retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        retorno = getDiasVacacionesSolicitados(documento, nitEmpresa, estado);
        return retorno;
    }
    
    public BigDecimal getDiasVacacionesSolicitados(String documento, String nitEmpresa, String estado) {
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
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, documento);
            if (estado != null) {
                query.setParameter(3, estado);
            }
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error consultarDiasVacacionesSolicitados." + e);
        }
        System.out.println("resultados dias estado " + estado + ": " + retorno);
        return retorno;
    }

    @GET
    @Path("/consultaFechaUltimoPago")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Timestamp consultaFechaUltimoPago(
            @QueryParam("seudonimo") String seudonimo,
            @QueryParam("nitempresa") String nitEmpresa) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "consultaFechaUltimoPago" + "()");
        Timestamp retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        String consulta = "SELECT GREATEST(\n"
                + "                CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO((select secuencia from empleados where codigoempleado=?), 1), "
                + "                NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO((select secuencia from empleados where codigoempleado=?), 80), CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO( "
                + "                (select secuencia from empleados where codigoempleado=?) "
                + "                , 1) "
                + "            )) "
                + "            FROM DUAL ";
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, documento);
            query.setParameter(2, documento);
            query.setParameter(3, documento);
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
            @QueryParam("dias") int dias) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "calculaFechaRegreso" + "()");
        List retorno = getFechaRegreso(fechainicio, dias, seudonimo, nitEmpresa);
        return retorno;
    }
    
    public List getFechaRegreso(String fechainicio, int dias, String seudonimo, String nitEmpresa) {
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        List retorno = null;
        String consulta = "SELECT \n"
                + "TO_CHAR(KIOVACACIONES_PKG.CALCULARFECHAFINVACA( (select secuencia from empleados where codigoempleado=?), TO_DATE(?, 'YYYY-MM-DD') , \n"
                + "KIOVACACIONES_PKG.CALCULARFECHAREGRESO( (select secuencia from empleados where codigoempleado=?) , TO_DATE(?, 'YYYY-MM-DD') , ? ) , 'S' ), 'DD/MM/YYYY') FECHAFIN,\n"
                + "TO_CHAR(KIOVACACIONES_PKG.CALCULARFECHAREGRESO( (select secuencia from empleados where codigoempleado=?) , TO_DATE(?, 'YYYY-MM-DD') , ? ), 'DD/MM/YYYY') FECHAREGRESO\n"
                + "FROM DUAL ";
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, documento);
            query.setParameter(2, fechainicio);
            query.setParameter(3, documento);
            query.setParameter(4, fechainicio);
            query.setParameter(5, dias);
            query.setParameter(6, documento);
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
            @QueryParam("dias") int dias) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "calculaFechaFinVaca" + "()");
        Timestamp retorno = getFechaFinVaca(fechainicio, getFechaRegreso(fechainicio, dias, seudonimo, nitEmpresa).toString(), dias, seudonimo, nitEmpresa);
        return retorno;
    }  
    
    public Timestamp getFechaFinVaca(String fechainicio, String fechafin, int dias, String seudonimo, String nitEmpresa) {
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        Timestamp retorno = null;
        String consulta = "SELECT KIOVACACIONES_PKG.CALCULARFECHAFINVACA( (select secuencia from empleados where codigoempleado=?), TO_DATE(?, 'YYYY-MM-DD') , TO_DATE(?,'YYYY-MM-DD HH:MM:SS') , 'S' ) FROM DUAL";
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, documento);
            query.setParameter(2, fechainicio);
            query.setParameter(3, fechafin);
            retorno = (Timestamp) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error getFechaFinVaca." + e);
        }
        return retorno;
    }
       
    public boolean creaKioNovedadSolici(String seudonimo, String nit, String fechainicial, String fecharegreso, String dias, String RFVACACION, String fechaFin) {
        int conteo = 0;
        try {
        setearPerfil();
        System.out.println("parametros creaKioNovedadSolici seudonimo: "+seudonimo+", nit: "+nit+", fechainicial: "+fechainicial+", fecharegreso: "+fecharegreso+"fecha fin: "+fechaFin +" dias: "+dias+", rfvacacion: "+RFVACACION);
        String sql = "INSERT INTO KIONOVEDADESSOLICI (EMPLEADO, FECHAINICIALDISFRUTE, DIAS, TIPO, SUBTIPO, FECHASISTEMA, FECHASIGUIENTEFINVACA, ESTADO, \n"
                + "ADELANTAPAGO, ADELANTAPAGOHASTA, FECHAPAGO, PAGADO, VACACION)\n"
                + "VALUES\n"
                + "(?, ?, ?, 'VACACION', 'TIEMPO', SYSDATE, ?, 'ABIERTO', ?, ?, ?, 'N', ?)";
        Query query = getEntityManager().createNativeQuery(sql);
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit);
        query.setParameter(1, secEmpleado);
        query.setParameter(2, fechainicial);
        query.setParameter(3, dias);
        query.setParameter(4, fecharegreso);
        query.setParameter(5, null);
        query.setParameter(6, fechaFin);
        query.setParameter(7, null);
        query.setParameter(8, RFVACACION);
        conteo = query.executeUpdate();
        return conteo>0;
        } catch (Exception e) {
            System.out.println("Error creaKioNovedadSolici: "+e.getMessage());
            return false;
        }
    }
    
    public boolean creaKioSoliciVacas(String seudonimo, String nit, String secNovedad, String fechaGeneracion) {
        int conteo = 0;
        try {
            setearPerfil();
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit);
            String secEmplJefe = consultarSecuenciaEmpleadoJefe(secEmpleado);
            String sql = "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, empleadojefe, activa, fechageneracion) "
                    + "values (?, ?, user, ?, 'S', to_date(?, 'dd/mm/yyyy HH24miss'))";
            Query query = getEntityManager().createNativeQuery(sql);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, secNovedad);
            query.setParameter(3, secEmplJefe);
            query.setParameter(4, fechaGeneracion);
            conteo = query.executeUpdate();
            System.out.println("registro kiosolicivaca: "+conteo);
        } catch (Exception e) {
            System.out.println("Error creaKioSoliciVacas: " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }
        
    public String getSecuenciaKioNovedadesSolici (String seudonimo, String nitEmpresa,
            String fechainicio, String fecharegreso,
            String dias, String rfVacacion) {
        String sec = null;
        try {
            setearPerfil();
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa);
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
            Query query = getEntityManager().createNativeQuery(sqlQuery);

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
            String secEmplJefe, String kioNovedadSolici) {
        String documento = null;
        try {
            setearPerfil();
            System.out.println("parametros getSecKioSoliciVacas: secEmpl: "+secEmpl+", fechaGeneracion: "+fechaGeneracion+", secEmplJefe "+secEmplJefe+", kioNovedadSolici "+kioNovedadSolici );
            String sqlQuery = "select secuencia from kiosolicivacas where empleado=? and fechageneracion=to_date(?, 'dd/mm/yyyy HH24miss') and empleadojefe=? and activa='S' and kionovedadsolici=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechaGeneracion);
            query.setParameter(3, secEmplJefe);
            query.setParameter(4, kioNovedadSolici);
            documento = query.getSingleResult().toString();
            System.out.println("SecKioSoliciVacas: " + documento);
        } catch (Exception e) {
            System.out.println("Error: getSecKioSoliciVacas: " + e.getMessage());
        }
        return documento;
    }    
      
    public String consultarSecuenciaEmpleadoJefe(String secEmpleado) {
        System.out.println("parametros consultarSecuenciaEmpleadoJefe: secEmpleado: "+secEmpleado);
        String secJefe = null;
        try {
            setearPerfil();
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
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            secJefe = query.getSingleResult().toString();
            System.out.println("secuencia jefe: " + secJefe);
        } catch (Exception e) {
            System.out.println("Error: consultarSecuenciaEmpleadoJefe: " + e.getMessage());
        }
        return secJefe;
    }        
        
    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa) {
        String documento = null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento = query.getSingleResult().toString();
            System.out.println("documento: " + documento);
        } catch (Exception e) {
            System.out.println("Error: getDocumentoPorSeudonimo: " + e.getMessage());
        }
        return documento;
    }
    
    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa) {
        String secuencia = null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO FROM EMPLEADOS E, CONEXIONESKIOSKOS CK WHERE CK.EMPLEADO=E.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuencia: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }
     
    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici) {
        String secEmpl = null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT KSV.EMPLEADO\n"
                    + "FROM \n"
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN\n"
                    + "WHERE\n"
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA\n"
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA\n"
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, kioEstadoSolici);
            secEmpl = query.getSingleResult().toString();
            System.out.println("secuencia empleado: " + secEmpl);
        } catch (Exception e) {
            System.out.println("Error: getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secEmpl;
    }
    
    @POST
    @Path("/nuevoEstadoSolici")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response setNuevoEstadoSolici(@QueryParam("secuencia") String secKioEstadoSolici, @QueryParam("motivo") String motivo,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit, @QueryParam("estado") String estado, 
            @QueryParam("cadena") String cadena, @QueryParam("grupo") String grupoEmpr, @QueryParam("urlKiosco") String urlKiosco) {
        System.out.println("nuevoEstadoSolici()");
        System.out.println("parametros: secuencia: " + secKioEstadoSolici + ", motivo " + motivo + ", empleado " + seudonimo + ", estado: " + estado + ", cadena " + cadena+", nit: "+nit+", urlKiosco: "+urlKiosco+", grupoEmpresarial: "+grupoEmpr);
        List s = null;
        int res = 0;
        try {
            String secEmplEjecuta = getSecuenciaEmplPorSeudonimo(seudonimo, nit);
            String secEmplSolicita = getEmplXsecKioEstadoSolici(secKioEstadoSolici);
            String secEmplJefe = getEmplJefeXsecKioEstadoSolici(secKioEstadoSolici);
            setearPerfil();
            String sqlQuery = "INSERT INTO KIOESTADOSSOLICI "
                    + "(KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, NOVEDADSISTEMA, MOTIVOPROCESA, PERSONAEJECUTA)\n"
                    + "SELECT\n"
                    + "KIOSOLICIVACA, SYSDATE FECHAPROCESAMIENTO, ?, ? EMPLEADOEJECUTA"
                    + ", NOVEDADSISTEMA, ?, null\n"
                    + "FROM KIOESTADOSSOLICI\n"
                    + "WHERE SECUENCIA=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, estado);
            query.setParameter(2, secEmplEjecuta);
            query.setParameter(3, motivo);
            query.setParameter(4, secKioEstadoSolici);
            res = query.executeUpdate();
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            EnvioCorreo c = new EnvioCorreo();
            String estadoVerbo=estado.equals("CANCELADO")?"CANCELAR":
                    estado.equals("AUTORIZADO")?"PRE-APROBAR":
                    estado.equals("RECHAZADO")?"RECHAZAR":estado;
            String estadoPasado=estado.equals("CANCELADO")?"canceló":
                    estado.equals("AUTORIZADO")?"pre-aprobó":
                    estado.equals("RECHAZADO")?"rechazó":estado;
            String mensaje="Nos permitimos informar que se acaba de "+estadoVerbo+" una solicitud de vacaciones";
                    if (estado.equals("RECHAZADO") || estado.equals("AUTORIZADO")){
                        mensaje+=" creada para "+getApellidoNombreXsecEmpl(secEmplSolicita);
                    }
                    mensaje+=" en el módulo de Kiosco Nómina Designer. Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                    + "<br><br>"
                    + "La persona que "+estadoPasado.toUpperCase()+" LA SOLICITUD es: "+getApellidoNombreXsecEmpl(secEmplEjecuta)+"<br>";
                    if (estado.equals("CANCELADO")) {
                        mensaje += "La persona a cargo de HACER EL SEGUIMIENTO es: " + getApellidoNombreXsecEmpl(secEmplJefe) + "<br>";
                    }
                    mensaje+= "Por favor seguir el proceso en: "+urlKiosco+"<br><br>"
                    + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita.<br><br>"
                    + "En caso de que haya olvidado su clave, ingrese a la página de internet, y de clic en ¿Olvidó su clave? y siga los pasos.";
           String fechaInicioDisfrute = getFechaInicioXsecKioEstadoSolici(secKioEstadoSolici);
           String urlKio =  urlKiosco + "/login/" + grupoEmpr;
           System.out.println("url Kiosco: "+urlKio);
            if (res>0) {
                System.out.println("solicitud "+estado+" con éxito.");
                String servidorsmtp = getConfigCorreoServidorSMTP(nit);
                String puerto = getConfigCorreo(nit, "PUERTO");
                String autenticado = getConfigCorreo(nit, "AUTENTICADO");
                String starttls = getConfigCorreo(nit, "STARTTLS");
                String remitente = getConfigCorreo(nit, "REMITENTE");
                String clave = getConfigCorreo(nit, "CLAVE");
                if (estado.equals("CANCELADO")){
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplSolicita),
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nit)) {
                        System.out.println("Correo enviada a la persona que ejecuta");
                    }
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplJefe),
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nit)) {
                        System.out.println("Correo enviado al empleado que solicita asociado");
                    }
                }
                
                if (estado.equals("AUTORIZADO") || estado.equals("RECHAZADO")){
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplSolicita),
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nit)) {
                        System.out.println("Correo enviada a la persona que ejecuta");
                    }
                    if (c.enviarCorreoVacaciones(
                            servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                            getCorreoXsecEmpl(secEmplEjecuta),
                            "Solicitud de vacaciones Kiosco - " + estadoPasado + ": " + fecha + ". Inicio de vacaciones: " + fechaInicioDisfrute,
                            mensaje, urlKio, nit)) {
                        System.out.println("Correo enviado al empleado que solicita asociado");
                    }                    
                }

            } else {
                System.out.println("Error al procesar la solicitud.");
            }
            return Response.status(Response.Status.OK).entity(res > 0).build();
        } catch (Exception ex) {
            System.out.println("Error setNuevoEstadoSolici: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }
    
    public String getFechaInicioXsecKioEstadoSolici(String secKioEstadoSolici) {
        String fechaInicio = null;
        try {
            setearPerfil();
            String sqlQuery = "select "
                    + "TO_CHAR(KN.FECHAINICIALDISFRUTE, 'dd/mm/yyyy') "
                    + "from "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA = KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI=KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            fechaInicio = query.getSingleResult().toString();
            System.out.println("Fecha inicio: " + fechaInicio);
        } catch (Exception e) {
            System.out.println("Error: getFechaInicioXsecKioEstadoSolici: " + e.getMessage());
        }
        return fechaInicio;
    }
    
    public String getEmplJefeXsecKioEstadoSolici(String secKioEstadoSolici) {
        String secEmplJefe = null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT KSV.EMPLEADOJEFE "
                    + "FROM "
                    + "KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secEmplJefe = query.getSingleResult().toString();
            System.out.println("Empl jefe asociado: " + secEmplJefe);
        } catch (Exception e) {
            System.out.println("Error: getEmplJefeXsecKioEstadoSolici: " + e.getMessage());
        }
        return secEmplJefe;
    }    

    
    @GET
    @Path("/getDiasNovedadesVaca")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDiasNovedadesVaca(@QueryParam("nit") String nit, @QueryParam("empleado") String empleado,
            @QueryParam("cadena") String cadena) {
        System.out.println("getDiasNovedadesVaca()");
        System.out.println("parametros: nit: " + nit + " empleado " + empleado + " cadena " + cadena);
        List s = null;
        try {
            String secuenciaEmpleado = getSecuenciaEmplPorSeudonimo(empleado, nit);
            setearPerfil();
            String sqlQuery = "select \n" 
                    + "tablaTotal.empleado, 'TOTAL' tipo, NVL(sum(tablaTotal.dias), 0 )\n"
                    + "from\n"
                    + "(select e.secuencia empleado, sum(n.dias) dias\n"
                    + "from\n"
                    + "novedadessistema n, empleados e\n"
                    + "where e.secuencia=n.empleado\n"
                    + "and tipo='VACACION'\n"
                    + "and subtipo in ('TIEMPO', 'DINERO')\n"
                    + "group by e.secuencia\n"
                    + "union\n"
                    + "select e.secuencia empleado,\n"
                    + "sum(k.diasvacadisfrute+k.diasvacadinero) dias\n"
                    + "from kioacumvaca k, empleados e\n"
                    + "where\n"
                    + "e.secuencia=k.empleado(+)\n"
                    + "group by e.secuencia) tablaTotal, empleados e where tablaTotal.empleado=e.secuencia\n"
                    + "and e.secuencia=?\n"
                    + "group by tablaTotal.empleado\n"
                    + "union\n"
                    + "(select tabla.empleado secuenciaEmpl, tabla.tipo tipo, NVL(sum(tabla.dias), 0) dias\n"
                    + "from \n"
                    + "(\n"
                    + "(select e.secuencia empleado, NVL(sum(n.dias), 0) dias, 'DINERO' tipo\n"
                    + "from\n"
                    + "novedadessistema n, empleados e\n"
                    + "where e.secuencia=n.empleado\n"
                    + "and tipo='VACACION'\n"
                    + "and subtipo='DINERO'\n"
                    + "group by e.secuencia\n"
                    + "union\n"
                    + "select e.secuencia empleado,\n"
                    + "NVL(sum(k.diasvacadinero), 0) dias, 'DINERO' tipo\n"
                    + "from kioacumvaca k, empleados e\n"
                    + "where\n"
                    + "e.secuencia=k.empleado(+)\n"
                    + "group by e.secuencia)\n"
                    + "union\n"
                    + "(select e.secuencia empleado, NVL(sum(n.dias), 0) dias, 'TIEMPO' tipo\n"
                    + "from\n"
                    + "novedadessistema n, empleados e\n"
                    + "where e.secuencia=n.empleado\n"
                    + "and tipo='VACACION'\n"
                    + "and subtipo='TIEMPO'\n"
                    + "group by e.secuencia\n"
                    + "union\n"
                    + "select e.secuencia empleado,\n"
                    + "NVL(sum(k.diasvacadisfrute), 0) dias, 'TIEMPO' tipo\n"
                    + "from kioacumvaca k, empleados e\n"
                    + "where\n"
                    + "e.secuencia=k.empleado(+)\n"
                    + "group by e.secuencia)\n"
                    + ") tabla\n"
                    + "where tabla.empleado=?\n"
                    + "group by tabla.empleado, tabla.tipo)";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, secuenciaEmpleado);
            query.setParameter(2, secuenciaEmpleado);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error getDiasNovedadesVaca: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }    
    
    /*Crea nuevo registro kioestadosolici al crear nueva solicitud de vacaciones*/
    public boolean creaKioEstadoSolici(
            String seudonimo, String nit, String kioSoliciVaca, 
            String fechaProcesa, String estado, String motivo) {
        System.out.println("parametros: seudonimo: empleado " + seudonimo + ", nit: "+nit+", kiosolicivaca: "+kioSoliciVaca+" estado: " + estado);
        int res = 0;
        try {
            String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit);
            setearPerfil();
            String sqlQuery = "INSERT INTO KIOESTADOSSOLICI (KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, MOTIVOPROCESA)\n"
                    + "VALUES (?, to_date(?, 'dd/mm/yyyy HH24miss'), ?, ?, ?)";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, kioSoliciVaca);
            query.setParameter(2, fechaProcesa);
            query.setParameter(3, estado);
            query.setParameter(4, secEmpl);
            query.setParameter(5, motivo);
            res = query.executeUpdate();
            System.out.println("registro kioestadosolici: "+res);
        } catch (Exception ex) {
            System.out.println("Error creaKioEstadoSolici: " + ex.getMessage());
            return false;
        }
        return res>0;
    }
    
    private boolean validaFechaInicial(String seudonimo, String nit, String fechaIniVaca) {
        boolean res;
        BigDecimal conteo = BigDecimal.ZERO;
        try {
           conteo =  verificaExistenciaSolicitud(seudonimo, nit, fechaIniVaca);
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
        System.out.println("crearSolicitudVacaciones");
        System.out.println("link Kiosco: "+urlKiosco);
        System.out.println("grupoEmpresarial: "+grupoEmpr);
        boolean soliciCreada = false;
        boolean soliciValida = false;
        String mensaje = "";
        String urlKio = urlKiosco+"#/login/"+grupoEmpr;
        
        try {
        boolean res = false;
        boolean valFPago = !validaFechaPago(seudonimo, nit, fechainicial);
        boolean valTraslap = validaTraslapamientos(seudonimo, nit, fechainicial, fechafin);
        boolean valFInicial = validaFechaInicial(seudonimo, nit, fechainicial);
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
                String pattern = "dd/MM/yyyy HHmmss";
                String patternFechaCorreo = "dd/MM/yyyy";
                Date fecha = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                SimpleDateFormat simpleDateFormatCorreo = new SimpleDateFormat(patternFechaCorreo);
                String fechaGeneracion = simpleDateFormat.format(fecha);
                String fechaCorreo = simpleDateFormatCorreo.format(fecha);
                System.out.println("fecha: " + fechaGeneracion);
                String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit);
                String secEmplJefe = consultarSecuenciaEmpleadoJefe(secEmpl);
                // Insertar registro en kionovedadessolici
                if (creaKioNovedadSolici(seudonimo, nit, fechainicial, fecharegreso, dias, RFVACACION, fechafin)) {
                    String secKioNovedad = getSecuenciaKioNovedadesSolici(seudonimo, nit, fechainicial, fecharegreso, dias, RFVACACION);
                    System.out.println("secuencia kionovedadsolici creada: " + secKioNovedad);
                    // Insertar registro en kiosolicivacas
                    if (creaKioSoliciVacas(seudonimo, nit, secKioNovedad, fechaGeneracion)) {
                        String secKioSoliciVacas = getSecKioSoliciVacas(secEmpl, fechaGeneracion, secEmplJefe, secKioNovedad);
                        System.out.println("secuencia kiosolicivacas creada: " + secKioSoliciVacas);
                        // Insertar registro en kioestadossolici
                        if (creaKioEstadoSolici(seudonimo, nit, secKioSoliciVacas, fechaGeneracion, "ENVIADO", null)) {
                            soliciCreada = true;
                            mensaje = "Solicitud creada exitosamente.";
                            String mensajeCorreo = "Nos permitimos informar que se acaba de crear una solicitud de vacaciones en el módulo de Kiosco Nómina Designer. Por favor llevar el caso desde su cuenta de usuario en el portal de Kiosco y continuar con el proceso."
                                    + " <br><br> "
                                    + "La persona que CREÓ LA SOLICITUD es: "+getApellidoNombreXsecEmpl(secEmpl)
                                    + "<br>"
                                    + "La persona a cargo de DAR APROBACIÓN es: "+getApellidoNombreXsecEmpl(secEmplJefe)
                                    + "<br>"
                                    + "Por favor seguir el proceso en: <a href='"+urlKio+"'>"+urlKio+"</a>"
                                    + "<br><br>"
                                    + "Si no puede ingresar, necesitará instalar la última versión de su navegador, la cual podrá descargar de forma gratuita."
                                    + "<br><br>"
                                    + "En caso de que haya olvidado su clave, ingrese a la página de internet, y de clic en ¿Olvidó su clave? y siga los pasos.";
                            EnvioCorreo c = new EnvioCorreo();
                            
                            String servidorsmtp = getConfigCorreoServidorSMTP(nit);
                            String puerto = getConfigCorreo(nit, "PUERTO");
                            String autenticado = getConfigCorreo(nit, "AUTENTICADO");
                            String starttls = getConfigCorreo(nit, "STARTTLS");
                            String remitente = getConfigCorreo(nit, "REMITENTE");
                            String clave = getConfigCorreo(nit, "CLAVE");
                            
                            if (c.enviarCorreoVacaciones(
                                    servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                                    getCorreoXsecEmpl(secEmpl),
                                    "Solicitud de vacaciones Kiosco - Nueva solicitud: "+fechaCorreo+". Inicio de vacaciones: "+fechainicial, 
                                    mensajeCorreo, urlKiosco, nit)){
                                System.out.println("Correo enviado al empleado.");
                            } 
                            if (c.enviarCorreoVacaciones(
                                    servidorsmtp, puerto, autenticado, starttls, remitente, clave,
                                    getCorreoXsecEmpl(secEmplJefe),
                                    "Solicitud de vacaciones Kiosco - Nueva solicitud: "+fechaCorreo+". Inicio de vacaciones: "+fechainicial, 
                                    mensajeCorreo, urlKiosco, nit)){
                                System.out.println("Correo enviado al jefe.");
                            }                         
                        } else {
                            mensaje = "Ha ocurrido un error y no fue posible crear la solicitud, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                        }
                    } else {
                        System.out.println("Ha ocurrido un error al momento de crear el registro 2 de la solicitud");
                        mensaje= "Ha ocurrido un error y no fue posible crear la solicitud, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                    }
                } else {
                    System.out.println("Ha ocurrido un error al momento de crear el registro 1 de la solicitud");
                    mensaje = "Ha ocurrido un error y no fue posible crear la solicitud, por favor inténtelo de nuevo más tarde. Si el problema persiste comuniquese con el área de nómina y recursos humanos de su empresa";
                }

            } catch (Exception e) {
                System.out.println("Ha ocurrido un error: " + e.getMessage());
                soliciCreada=false;
                mensaje="Ha ocurrido un error, por favor inténtelo de nuevo más tarde.";
            }            
        }   
        
        } catch(Exception e) {
            System.out.println("Error crearSolicitud. "+e.getMessage());
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
    
        public String getConfigCorreo(String nit, String valor) {
        System.out.println("getPathArchivosPlanos()");
        String servidorsmtp="smtp.designer.com.co";
        try {
            setearPerfil();
            String sqlQuery = "SELECT "+valor+" FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, nit);
            servidorsmtp =  query.getSingleResult().toString();
            System.out.println(valor+": "+servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
        return servidorsmtp;
    }    
    
    public String getConfigCorreoServidorSMTP(String nit) {
        System.out.println("getConfigCorreoServidorSMTP()");
        String servidorsmtp="smtp.designer.com.co";
        try {
            setearPerfil();
            String sqlQuery = "SELECT SERVIDORSMTP FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, nit);
            servidorsmtp =  query.getSingleResult().toString();
            System.out.println("Servidor smtp: "+servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: getConfigCorreoServidorSMTP: "+e.getMessage());
        }
        return servidorsmtp;
    } 
    
    
    public String getCorreoXsecEmpl(String secEmpl) {
        System.out.println("getCorreoXsecEmpl()");
        System.out.println("sec Empleado: "+secEmpl);
        String correo = null;
        String sqlQuery;
        try {
            setearPerfil();
            sqlQuery = "SELECT P.EMAIL "
                    + " FROM CONEXIONESKIOSKOS CK, EMPLEADOS E, PERSONAS P "
                    + " WHERE CK.EMPLEADO=E.SECUENCIA "
                    + " AND P.SECUENCIA=E.PERSONA "
                    + " AND CK.EMPLEADO=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error getCorreoXsecEmpl(): " + e.getMessage());
        }
        return correo;
    }
    
    public String getApellidoNombreXsecEmpl(String secEmpl) {
        System.out.println("getApellidoNombreXsecEmpl()");
        String nombre = null;
        setearPerfil();
        try {
            String sqlQuery = "SELECT UPPER(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE) NOMBRE "
                    + " FROM PERSONAS P, EMPLEADOS EMPL "
                    + " WHERE P.SECUENCIA=EMPL.PERSONA "
                    + " AND EMPL.SECUENCIA=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            nombre = (String) query.getSingleResult();
            System.out.println("nombre: "+nombre);
        } catch (Exception e) {
            System.out.println("Error getNombrePersona(): " + e);
        }
        return nombre;
    } 
    
    
   public BigDecimal consultarCodigoJornada(String seudonimo, String nitEmpresa, String fechaDisfrute) throws Exception {
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
        String secEmpleado=getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa);
        /*SimpleDateFormat formatoFecha = new SimpleDateFormat("ddMMyyyy");
        String strFechaDisfrute = formatoFecha.format(fechaDisfrute);*/
        System.out.println("secuencia: " + secEmpleado);
        //System.out.println("fecha en txt: " + strFechaDisfrute);
        System.out.println("fecha en txt: " + fechaDisfrute);
        try {
            query = getEntityManager().createNativeQuery(consulta);
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
   
   
    private boolean validaFechaPago(String seudonimo, String nitEmpresa, String fechainicialdisfrute) throws Exception {
            Calendar cl = Calendar.getInstance();
            cl.setTime(getFechaUltimoPago(seudonimo, nitEmpresa));
            return getDate(fechainicialdisfrute).after(cl.getTime());
    }   
   
    public Date getFechaUltimoPago(String seudonimo, String nitempresa) throws Exception {
        BigDecimal res = null;
        try {
        setearPerfil();
        String secEmpleado=getSecuenciaEmplPorSeudonimo(seudonimo, nitempresa);
        String consulta = "SELECT GREATEST("
                + "CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1), "
                + "NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 80), CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1)"
                + ")) "
                + "FROM DUAL ";
        Date fechaUltimoPago = null;
            Query query = getEntityManager().createNativeQuery(consulta);
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
    
    public Date getDate(String fechaStr) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "getDate" + "()");
        String consulta = "SELECT "
                + "TO_DATE(?, 'dd/mm/yyyy') "
                + "FROM DUAL ";
        Query query = null;
        Date fechaRegreso = null;
        try {
            query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, fechaStr);
            fechaRegreso = (Date) (query.getSingleResult());
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
       
    public boolean verificarDiaLaboral(String fechaDisfrute, BigDecimal codigoJornada) throws Exception {
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
        c.setTime(getDate(fechaDisfrute));
        diaSemana = c.get(Calendar.DAY_OF_WEEK);
        strFechaDisfrute = nombreDia(diaSemana);
        System.out.println("strFechaDisfrute: " + strFechaDisfrute);
        try {
            query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, codigoJornada);
            query.setParameter(2, strFechaDisfrute);
            conteoDiaLaboral = new BigDecimal(query.getSingleResult().toString());
            esDiaLaboral = !conteoDiaLaboral.equals(BigDecimal.ZERO);
            return esDiaLaboral;
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
    
    public boolean verificarFestivo(String fechaDisfrute) throws Exception {
        System.out.println(this.getClass().getName() + "." + "verificarFestivo" + "()");
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
            query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, "COLOMBIA");
            //query.setParameter(2, strFechaDisfrute);
            query.setParameter(2, fechaDisfrute);
            conteoDiaFestivo = new BigDecimal(query.getSingleResult().toString());
            esDiaFestivo = !conteoDiaFestivo.equals(BigDecimal.ZERO);
            return esDiaFestivo;
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
//        return false;
    }

    private boolean validaTraslapamientos(String seudonimo, String nitempresa, String fechaIniVaca, String fechaFinVaca) {
        boolean res = false;
        try {
            res = !BigDecimal.ZERO.equals(consultaTraslapamientos(seudonimo, nitempresa, fechaIniVaca, fechaFinVaca));
            //si es igual a cero, no hay traslapamientos.
            //falso si es cero, verdadero si es diferente de cero.
        } catch (Exception e) {
            System.out.println("validaTraslapamientos-excepcion: " + e.getMessage());
        }
        return res;
    }    

    public BigDecimal consultaTraslapamientos(
             String seudonimo,
             String nitempresa, 
             String fechaIniVaca, 
             String fechaFinVaca) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "consultaTraslapamientos" + "()");
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitempresa);
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICARTRASLAPAMIENTO(?, ? , ? ) "
                + "FROM DUAL ";
        Query query = null;
        BigDecimal contTras = null;
        try {
            query = getEntityManager().createNativeQuery(consulta);
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
            String nitempresa,
            String fechaIniVaca) throws Exception {
        System.out.println("verificaExistenciaSolicitud() fechaInicio: "+fechaIniVaca);
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitempresa);
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
                query = getEntityManager().createNativeQuery(consulta);
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
            System.out.println("verificaExistenciaSolicitud-excepcion: " + e);
//            throw e;
            throw new Exception("Error verificando si la solicitud ya existe " + e);
        }
        System.out.println("verificaExistenciaSolicitud-conteo: " + conteo);
        return conteo;
    }    
    
    
    
}
