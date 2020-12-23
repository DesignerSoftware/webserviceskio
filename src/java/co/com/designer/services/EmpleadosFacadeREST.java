package co.com.designer.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author thalia
 */
@Stateless
@Path("empleados")
public class EmpleadosFacadeREST {
    
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
    
    protected void setearPerfil() {
        try {
            System.out.println("setearPerfil()");
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil: " + ex);
        }
    }

    protected void setearPerfil(String cadena) {
        try {
            System.out.println("setearPerfil(cadena)");
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil(String cadena): " + ex);
        }
    }     
    
    @GET
    @Path("/datosEmpleadoNit/{empleado}/{nit}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDatosEmpleadoNit(@PathParam("empleado") String empleado, @PathParam("nit") String nit, @QueryParam("cadena") String cadena) {
        System.out.println("getDatosEmpleadosNit()");
        System.out.println("parametros: empleado: "+empleado+" nit: "+nit+ " cadena "+cadena);
        List s = null;
        try {
        String documento = getDocumentoPorSeudonimo(empleado, nit);
        setearPerfil();
        String sqlQuery="  select \n" +
"          e.codigoempleado usuario,  \n" +
"          p.nombre nombres, \n" +
"          p.primerapellido apellido1, \n" +
"          p.segundoapellido apellido2,  \n" +
"          decode(p.sexo,'M', 'MASCULINO', 'F', 'FEMENINO', '') sexo,  \n" +
"          to_char(p.FECHANACIMIENTO, 'yyyy-mm-dd') fechaNacimiento, \n" +
"          (select nombre from ciudades where secuencia=p.CIUDADNACIMIENTO) ciudadNacimiento, \n" +
"          p.GRUPOSANGUINEO grupoSanguineo, \n" +
"          p.FACTORRH factorRH, \n" +
"          (select nombrelargo from tiposdocumentos where secuencia=p.TIPODOCUMENTO) tipoDocu, \n" +
"          p.NUMERODOCUMENTO documento,  \n" +
"          (select nombre from ciudades where secuencia=p.CIUDADDOCUMENTO) lugarExpediDocu, \n" +
"          p.EMAIL email, \n" +
"          'DIRECCION' direccion,  \n" +              
"          ck.ULTIMACONEXION ultimaConexion,\n" +
"          em.codigo codigoEmpresa, \n" +     
"          em.nit nitEmpresa,  \n" +
"          em.nombre nombreEmpresa, \n" +
"          empleadocurrent_pkg.descripciontipocontrato(e.secuencia, sysdate) contrato, \n" +
"          empleadocurrent_pkg.ValorBasicoCorte(e.secuencia, sysdate) salario,  \n" +
"          empleadocurrent_pkg.DescripcionCargoCorte(e.secuencia, sysdate) cargo,  \n" +                
// "          DIAS360(empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate), sysdate) diasW,  \n" +
"          empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate) inicioContratoActual,\n" +
// "         (select nombre from estructuras where secuencia=empleadocurrent_pkg.estructuracargocorte(e.secuencia, sysdate)) estructura, \n" +
"          em.logo logoEmpresa, \n" +
"          empleadocurrent_pkg.DireccionAlternativa(p.secuencia, sysdate) direccionPersona,  \n" +
"          t.numerotelefono numeroTelefono, \n" +
"          tt.nombre tipoTelefono \n" +             
"          from  \n" +
"          empleados e, conexioneskioskos ck, empresas em, personas p, telefonos t, tipostelefonos tt \n" +
"          where \n" +
"          e.persona=p.secuencia  \n" +
"          and e.empresa=em.secuencia  \n" +
"          and e.persona = t.persona\n" +
"          and t.fechavigencia = (select max(ti.fechavigencia) from telefonos ti where ti.persona = t.persona and ti.fechavigencia <= sysdate)  \n" +
"          and t.tipotelefono = tt.secuencia  \n" +
"          and ck.empleado=e.secuencia\n" +
"          and p.numerodocumento= ? \n" +
"          and em.nit=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nit);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error getDatosEmpleadoNit: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }
    
    @GET
    @Path("/datosFamiliaEmpleado/{empleado}/{nit}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDatosFamiliaEmpleado(@PathParam("empleado") String empleado, @PathParam("nit") String nit, @QueryParam("cadena") String cadena) {
        System.out.println("getDatosFamiliaEmpleado()");
        System.out.println("parametros: empleado: "+empleado+" nit: "+nit+ " cadena "+cadena);
        List s = null;
        try {
        String documento = getDocumentoPorSeudonimo(empleado, nit);
        setearPerfil();
        String sqlQuery="  select \n" +
"          fam.nombre ||' '|| fam.primerapellido ||' '|| fam.segundoapellido nombreFamiliar,   \n" +
"          t.tipo Parentezco \n" +            
"          from empleados e, empresas em, personas p, familiares f, tiposfamiliares t, personas fam \n" +
"          where \n" +
"          p.secuencia = e.persona  \n" +
"          and e.empresa = em.secuencia  \n" +
"          and f.persona = p.secuencia   \n" + 
"          and f.personafamiliar=fam.secuencia   \n" +        
"          and t.secuencia = f.tipofamiliar  \n" +
"          and p.numerodocumento= ? \n" +
"          and em.nit=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nit);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error getDatosFamiliaEmpleado: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }
    
    @GET
    @Path("/soliciSinProcesarJefe/{nit}/{jefe}/{estado}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSoliciSinProcesarJefe(@PathParam("nit") String nit,@PathParam("jefe") String jefe, 
        @PathParam("estado") String estado, @QueryParam("cadena") String cadena) {
        System.out.println("getSoliciSinProcesarJefe()");
        System.out.println("parametros: nit: "+nit+ " jefe "+jefe+" estado: "+estado+ " cadena "+cadena);
        List s = null;
        try {
        String secuenciaJefe = getSecuenciaEmplPorSeudonimo(jefe, nit );
        String secuenciaEmpresa = getSecuenciaPorNitEmpresa(nit);
        setearPerfil();
        String sqlQuery="   SELECT \n" +
        " t1.codigoempleado, REPLACE(TRIM(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE), '  ', ' ') NOMBRE,\n" +
        " t0.SECUENCIA, \n" +
        " TO_CHAR(t0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:MI:SS') SOLICITUD, \n" +
        " KNS.FECHAINICIALDISFRUTE, TO_CHAR(T0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:MI:SS') FECHAULTMODIF,\n" +
        " t0.ESTADO, \n" +
        " t0.MOTIVOPROCESA, t0.NOVEDADSISTEMA, t0.EMPLEADOEJECUTA, t0.PERSONAEJECUTA, t0.KIOSOLICIVACA,\n" +
        " KNS.ADELANTAPAGOHASTA FECHAFIN,\n" +
        " KNS.FECHASIGUIENTEFINVACA FECHAREGRESO,\n" +
        " KNS.DIAS,\n" +
        " V.INICIALCAUSACION||' a '||V.FINALCAUSACION PERIODOCAUSADO,\n" +
        " (SELECT PER.PRIMERAPELLIDO||' '||PER.SEGUNDOAPELLIDO||' '||PER.NOMBRE FROM PERSONAS PER, EMPLEADOS EMPL\n" +
        " WHERE EMPL.PERSONA=PER.SECUENCIA\n" +
        " AND EMPL.SECUENCIA=JEFE.SECUENCIA) EMPLEADOJEFE,\n" +
        " KNS.FECHAPAGO FECHAPAGO\n" +
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
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, secuenciaEmpresa);
            query.setParameter(2, estado);
            query.setParameter(3, secuenciaJefe);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error getSoliciSinProcesarJefe: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }
    
    public String getDocumentoCorreoODocumento(String usuario) {
       String documento=null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P WHERE P.EMAIL=?";
            if (this.validarCodigoUsuario(usuario)) {
                 sqlQuery+=" OR P.NUMERODOCUMENTO=?"; // si el valor es numerico validar por numero de documento
            }
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, usuario);
            if (this.validarCodigoUsuario(usuario)) {
               query.setParameter(2, usuario);
            }
            documento =  query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: getDocumentoCorreoODocumento: "+e.getMessage());
        }
        return documento;
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
    
    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa) {
       String secuencia=null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO FROM EMPLEADOS E, CONEXIONESKIOSKOS CK WHERE CK.EMPLEADO=E.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia =  query.getSingleResult().toString();
            System.out.println("secuencia: "+secuencia);
        } catch (Exception e) {
            System.out.println("Error: getSecuenciaEmplPorSeudonimo: "+e.getMessage());
        }
        return secuencia;
   }    
    
    public String getSecuenciaPorNitEmpresa( String nitEmpresa) {
       String secuencia=null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT EM.SECUENCIA SECUENCIAEMPRESA FROM EMPRESAS EM WHERE EM.NIT=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            secuencia =  query.getSingleResult().toString();
            System.out.println("secuencia: "+secuencia);
        } catch (Exception e) {
            System.out.println("Error: getSecuenciaPorNitEmpresa: "+e.getMessage());
        }
        return secuencia;
   }  
    
    
    
    public boolean validarCodigoUsuario(String usuario) {
        boolean resultado = false;
        BigInteger numUsuario;
        try {
            numUsuario = new BigInteger(usuario);
            resultado = true;
        } catch (NumberFormatException nfe) {
            resultado = false;
        }
        return resultado;
    }
    
    @GET
    @Path("{usuario}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSecuencia(@PathParam("usuario") String usuario, @QueryParam("cadena") String cadena) {
        BigDecimal res = null;
        try {
            setearPerfil();
            String sqlQuery = "SELECT EMPLEADO FROM CONEXIONESKIOSKOS WHERE SEUDONIMO=?";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            res = (BigDecimal) query.getSingleResult();
        } catch (Exception ex) {
            System.out.println("Error getSecuencia() path /empleados: " + ex);
            res = BigDecimal.ZERO;
        }
        return res.toString();
    }
    
    // por validar
   public BigDecimal consultarCodigoJornada(String secEmpleado, Date fechaDisfrute) throws Exception {
        System.out.println(this.getClass().getName() + "." + "consultarCodigoJornada" + "()");
        String consulta = "select nvl(j.codigo, 1) "
                + "from vigenciasjornadas v, jornadaslaborales j "
                + "where v.empleado = ? "
                + "and j.secuencia = v.jornadatrabajo "
                + "and v.fechavigencia = (select max(vi.fechavigencia) "
                + "from vigenciasjornadas vi "
                + "where vi.empleado = v.empleado "
                + "and vi.fechavigencia <= to_date( ? , 'ddmmyyyy') ) ";
        Query query = null;
        BigDecimal codigoJornada;
        SimpleDateFormat formatoFecha = new SimpleDateFormat("ddMMyyyy");
        String strFechaDisfrute = formatoFecha.format(fechaDisfrute);
        System.out.println("secuencia: " + secEmpleado);
        System.out.println("fecha en txt: " + strFechaDisfrute);
        try {
            query = getEntityManager().createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, strFechaDisfrute);
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
    
}
