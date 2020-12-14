package co.com.designer.services;

import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
    public List<VwVacaPendientesEmpleados> consultarPeriodosPendientesEmpleado(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa) throws Exception {
        System.out.println(this.getClass().getName() + "." + "consultarPeriodosPendientesEmpleado" + "()");
        List<VwVacaPendientesEmpleados> periodosPendientes = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        Query query = null;
        String consulta = "select vw from VwVacaPendientesEmpleados vw where vw.diasPendientes > 0 and vw.empleado.codigoempleado = :codEmple ";
        try {
            BigDecimal codigoEmpleado = new BigDecimal(documento);
            query = getEntityManager().createQuery(consulta);
            query.setParameter("codEmple", codigoEmpleado);
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
        String consulta = "select vw.* " +
                " from VwVacaPendientesEmpleados vw " +
                " where vw.empleado = (select ei.secuencia from empleados ei, empresas emi "
                + "where ei.empresa=emi.secuencia and ei.codigoempleado=?) " +
                " and vw.inicialCausacion = ( select min( vwi.inicialCausacion ) " +
                " from VwVacaPendientesEmpleados vwi " +
                " where vwi.empleado = vw.empleado " +
                " and KIOVACACIONES_PKG.DIASDISPOPER(vwi.rfVacacion) > 0 " +
                "and vwi.inicialCausacion >=empleadocurrent_pkg.fechatipocontrato(vw.empleado, sysdate) ) ";
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
    @Path("/consultarDiasVacacionesProvisionados")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BigDecimal consultarDiasVacacionesProvisionados(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "consultarDiasVacacionesProvisionados" + "()");
        BigDecimal retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        String consulta = " select " +
        "round(sn.unidades,2) dias " +
        "from conexioneskioskos ck, empleados empl, personas per, " +
        "solucionesnodos sn, conceptos c, empresas em " +
        "where ck.empleado=empl.secuencia " +
        "and em.secuencia = empleadocurrent_pkg.EmpresaEmpleado(empl.secuencia, sysdate) " +
        "and empl.persona=per.secuencia " +
        "and sn.empleado= empl.secuencia " +
        "and sn.concepto = c.secuencia " +
        "and c.codigo = conceptosreservados_pkg.capturarcodigoconcepto(13,em.codigo) " +
        "and sn.fechapago=(select max(sni.fechapago) " +
        "                  from solucionesnodos sni, conceptos ci " +
        "                  where sni.concepto=ci.secuencia " +
        "				          and ci.codigo=conceptosreservados_pkg.capturarcodigoconcepto(13,em.codigo) " +
        "                  and sni.empleado=sn.empleado " +
        "				          and sni.concepto=sn.concepto " +
        "				          and sni.fechapago <= SYSDATE) " +
        "and empl.secuencia= (SELECT secuencia from empleados where codigoempleado=?) ";
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
        String consulta = "select " +
        "sum(kn.dias) dias " +
        "from KioEstadosSolici e,  kiosolicivacas ks, kionovedadessolici kn, VwVacaPendientesEmpleados v " +
        "where " +
        "e.kiosolicivaca = ks.secuencia " +
        "and ks.KIONOVEDADSOLICI = kn.secuencia " +
        "and kn.vacacion=v.RFVACACION " +
        "and ks.empleado = (select ei.secuencia from empleados ei, personas pei, empresas em where ei.persona=pei.secuencia \n" +
        "                  and ei.empresa=em.secuencia and em.nit=? " +
        "                  and pei.numerodocumento=?) " +
        "and e.secuencia = (select max(ei.secuencia) " +
        "from KioEstadosSolici ei, kiosolicivacas ksi " +
        "where ei.kioSoliciVaca = ksi.secuencia " +
        "and ksi.secuencia=ks.secuencia) ";
        if (estado!=null) {
            consulta += "and e.estado=? ";
        }
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, documento);
            if (estado!=null) {
                query.setParameter(3, estado);
            }
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error consultarDiasVacacionesSolicitados." + e);
        }
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
        String consulta = "SELECT GREATEST(\n" +
"                CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO((select secuencia from empleados where codigoempleado=?), 1), " +
"                NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO((select secuencia from empleados where codigoempleado=?), 80), CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO( " +
"                (select secuencia from empleados where codigoempleado=?) " +
"                , 1) " +
"            )) " +
"            FROM DUAL ";
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
        String consulta = "SELECT " +
"       KIOVACACIONES_PKG.VERIFICAEXISTESOLICITUD((select secuencia from empleados where codigoempleado=?), "
                + "to_date('?','yyyy-mm-dd') ) " +
"                FROM DUAL          ";
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
    public Timestamp calculaFechaRegreso(
            @QueryParam("seudonimo") String seudonimo, 
            @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("fechainicio") String fechainicio,
            @QueryParam("dias") int dias) {
        setearPerfil();
        System.out.println(this.getClass().getName() + "." + "verificaExistenciaSolicitud" + "()");
        Timestamp retorno = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa);
        String consulta = "SELECT " +
"       KIOVACACIONES_PKG.CALCULARFECHAREGRESO((select secuencia from empleados where codigoempleado=?), "
                + "to_date('?','yyyy-mm-dd'), ? ) " +
"                FROM DUAL          ";
        try {
            Query query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, documento);
            query.setParameter(2, fechainicio);
            query.setParameter(3, dias);
            retorno = (Timestamp) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error calculaFechaRegreso." + e);
        }
        return retorno;
    } 
    
    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa) {
       String documento=null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento =  query.getSingleResult().toString();
            System.out.println("documento: "+documento);
        } catch (Exception e) {
            System.out.println("Error: getDocumentoPorSeudonimo: "+e.getMessage());
        }
        return documento;
   }
}
