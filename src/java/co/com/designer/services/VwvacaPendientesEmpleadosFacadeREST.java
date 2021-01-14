package co.com.designer.services;

import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
        String consulta = "select vw.diasPendientes "
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
    
    @GET
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
    }  

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
    
    @POST
    @Path("/creaKioNovedadSolici")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})    
    public boolean creaKioNovedadSolici(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit, 
            @QueryParam("fechainicio") String fechainicial, @QueryParam("fecharegreso") String fecharegreso, 
            @QueryParam("dias") String dias, @QueryParam("estado") String estado, @QueryParam("vacacion") String RFVACACION) {
        int conteo = 0;
        Date fecha= new Date();
        System.out.println("prueba");
        System.out.println("fecha: "+fecha);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = new GregorianCalendar();
        System.out.println("fecha2: "+c1);
        setearPerfil();
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
        query.setParameter(6, null);
        query.setParameter(7, null);
        query.setParameter(8, RFVACACION);
        conteo = query.executeUpdate();
        return conteo>0;
        /*String secuenciaKioNovedadSolici=null;
        if (conteo>0) {
            String sq2l="select secuencia\n" +
"                from kionovedadessolici \n" +
"                where dias=1 \n" +
"                and fechainicialdisfrute=to_date(?, 'dd/mm/yyyy') \n" +
"                and fechasiguientefinvaca=to_date(?, 'dd/mm/yyyy') \n" +
"                and empleado=? \n" +
"                and tipo='VACACION'\n" +
"                and SUBTIPO='TIEMPO'\n" +
"                and vacacion=? \n" +
"                ";
            Query query2 = getEntityManager().createNativeQuery(sql);
            query2.setParameter(1, fechainicial);
            query2.setParameter(2, fecharegreso);
            query2.setParameter(3, secEmpleado);
            query2.setParameter(4, RFVACACION);
            secuenciaKioNovedadSolici=(String) query2.getSingleResult();
        }
        return secuenciaKioNovedadSolici ;*/
    }
    
    
    /*@POST
    @Path("/creaKioSoliciVacas")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public boolean creaKioSoliciVacas(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nit,
            @QueryParam("secKioNovedadSolici") String secNovedad, @QueryParam("fecharegreso") String fecharegreso,
            @QueryParam("dias") String dias, @QueryParam("estado") String estado, @QueryParam("vacacion") String RFVACACION) {
        int conteo = 0;
        setearPerfil();
        String sql = "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, empleadojefe, activa)\n"
                + "values (?, ?, user, ?, S')";
        Query query = getEntityManager().createNativeQuery(sql);
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nit);
        query.setParameter(1, secEmpleado);
        query.setParameter(2, secNovedad);
        //query.setParameter(3, con);
        conteo = query.executeUpdate();
        return conteo > 0;
    }*/
        
        @GET
        @Path("/getSecuenciaKioNovedadSolici")
        @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})  
        public String getSecuenciaKioNovedadesSolici(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, 
            @QueryParam("fechainicio") String fechainicio, @QueryParam("fecharegreso") String fecharegreso,
            @QueryParam("dias") String dias, @QueryParam("vacacion") String rfVacacion) {
        String sec = null;
        try {
            setearPerfil();
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa);
            String sqlQuery = "select max(secuencia) \n" +
"                from kionovedadessolici \n" +
"                where dias=? \n" +
"                and fechainicialdisfrute=to_date(?, 'dd/mm/yyyy') \n" +
"                and fechasiguientefinvaca=to_date(?, 'dd/mm/yyyy') \n" +
"                and empleado=? \n" +
"                and tipo='VACACION'\n" +
"                and SUBTIPO='TIEMPO'\n" +
"                and vacacion=? "
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
      
    public String consultarSecuenciaEmpleadoJefe(String seudonimo, String nitEmpresa) {
        String secJefe = null;
        try {
            setearPerfil();
            String secuenciaEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa);
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

            query.setParameter(1, secuenciaEmpl);
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
     
     
    @POST
    @Path("/nuevoEstadoSolici")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response setNuevoEstadoSolici(@QueryParam("secuencia") String secuencia, @QueryParam("motivo") String motivo,
            @QueryParam("seudonimo") String seudonimo, @QueryParam("nit") String nit, @QueryParam("estado") String estado, @QueryParam("cadena") String cadena) {
        System.out.println("getSolicitudCancelada()");
        System.out.println("parametros: secuencia: " + secuencia + " motivo " + motivo + " empleado " + seudonimo + " estado: " + estado + " cadena " + cadena);
        List s = null;
        int res = 0;
        try {
            String secuenciaEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nit);
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
            query.setParameter(2, secuenciaEmpl);
            query.setParameter(3, motivo);
            query.setParameter(4, secuencia);
            res = query.executeUpdate();
            return Response.status(Response.Status.OK).entity(res > 0).build();
        } catch (Exception ex) {
            System.out.println("Error setNuevoEstadoSolici: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }
    
   
    
}
