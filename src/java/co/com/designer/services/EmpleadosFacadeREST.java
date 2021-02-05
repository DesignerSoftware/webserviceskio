package co.com.designer.services;

import co.com.designer.kiosko.correo.EnvioCorreo;
import co.com.designer.kiosko.entidades.ConexionesKioskos;
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
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        String documento = getDocumentoPorSeudonimo(empleado, nit, cadena);
        setearPerfil(cadena);
        String sqlQuery="  select \n"
                    + "          e.codigoempleado usuario,  \n"
                    + "          p.nombre ||' '|| p.primerapellido ||' '|| p.segundoapellido nombres, \n"
                    + "          p.primerapellido apellido1, \n"
                    + "          p.segundoapellido apellido2,  \n"
                    + "          decode(p.sexo,'M', 'MASCULINO', 'F', 'FEMENINO', '') sexo,  \n"
                    + "          to_char(p.FECHANACIMIENTO, 'dd-MM-yyyy') fechaNacimiento, \n"
                    + "          (select nombre from ciudades where secuencia=p.CIUDADNACIMIENTO) ciudadNacimiento, \n"
                    + "          p.GRUPOSANGUINEO grupoSanguineo, \n"
                    + "          p.FACTORRH factorRH, \n"
                    + "          (select nombrelargo from tiposdocumentos where secuencia=p.TIPODOCUMENTO) tipoDocu, \n"
                    + "          p.NUMERODOCUMENTO documento,  \n"
                    + "          (select nombre from ciudades where secuencia=p.CIUDADDOCUMENTO) lugarExpediDocu, \n"
                    + "          p.EMAIL email, \n"
                    + "          'DIRECCION' direccion,  \n"
                    + "          ck.ULTIMACONEXION ultimaConexion,\n"
                    + "          em.codigo codigoEmpresa, \n"
                    + "          em.nit nitEmpresa,  \n"
                    + "          em.nombre nombreEmpresa, \n"
                    + "          empleadocurrent_pkg.descripciontipocontrato(e.secuencia, sysdate) contrato, \n"
                    + "          trim(to_char(empleadocurrent_pkg.ValorBasicoCorte(e.secuencia, sysdate),'$999G999G999G999G999G999')) salario,   \n"
                    + "          empleadocurrent_pkg.DescripcionCargoCorte(e.secuencia, sysdate) cargo,  \n"
                    + // "          DIAS360(empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate), sysdate) diasW,  \n" +
                    "            empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate) inicioContratoActual,\n"
                    + // "         (select nombre from estructuras where secuencia=empleadocurrent_pkg.estructuracargocorte(e.secuencia, sysdate)) estructura, \n" +
                    "            em.logo logoEmpresa, \n"
                    + "          empleadocurrent_pkg.DireccionAlternativa(p.secuencia, sysdate) direccionPersona,  \n"
                    + "          t.numerotelefono numeroTelefono, \n"
                    + "          tt.nombre tipoTelefono,  \n"
                    + "          empleadocurrent_pkg.CentrocostoNombre(e.secuencia) centroscostos,  \n"
                    + "          empleadocurrent_pkg.EdadPersona(p.secuencia, sysdate) || ' AÑOS' edad,  \n"
                    + "          empleadocurrent_pkg.entidadafp(e.secuencia) entidadfp,\n"
                    + "          empleadocurrent_pkg.entidadeps(e.secuencia) entidadeps, \n"
                    + "          empleadocurrent_pkg.entidadarp(e.secuencia)  entidadarp,\n"
                    + "          empleadocurrent_pkg.entidadcesantias(e.secuencia, sysdate) entidadcesantias,"
                    + "          empleadocurrent_pkg.Afiliacion(e.secuencia , te.codigo, sysdate, sysdate) cajaCompensacion,          \n"
                    + "          to_char(empleadocurrent_pkg.FechaIngreso(e.secuencia), 'dd-MM-yyyy') fechaContratacion   \n"
                    + "          from  \n"
                    + "          empleados e, conexioneskioskos ck, empresas em, personas p, telefonos t, tipostelefonos tt, VigenciasAfiliaciones V , tiposentidades te \n"
                    + "          where \n"
                    + "          e.persona=p.secuencia  \n"
                    + "          and e.empresa=em.secuencia  \n"
                    + "          and e.persona = t.persona\n"
                    + "          and t.fechavigencia = (select max(ti.fechavigencia) from telefonos ti where ti.persona = t.persona and ti.fechavigencia <= sysdate)  \n"
                    + "          and t.tipotelefono = tt.secuencia  \n"
                    + "          and ck.empleado=e.secuencia\n"
                    + "          and e.secuencia = v.empleado\n"
                    + "          and te.secuencia = v.tipoentidad\n"
                    + "          and te.codigo = 14\n"
                    + "          and p.numerodocumento= ? \n"
                    + "          and em.nit=?";
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
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
        System.out.println("parametros: empleado: " + empleado + " nit: " + nit + " cadena " + cadena);
        List s = null;
        try {
            String secEmpl = getSecuenciaEmplPorSeudonimo(empleado, nit, cadena);
            setearPerfil();
            String sqlQuery = "select \n"
                    + "fam.nombre ||' '|| fam.primerapellido ||' '|| fam.segundoapellido nombreFamiliar, t.tipo Parentesco,\n"
                    + "decode(ltel.secuencia, null, ' ',\n"
                    + "ltel.tipotelefono||' - '||\n"
                    + "--to_char(ltel.fechavigencia,'DD/MM/YYYY')||' '||\n"
                    + "ltel.numerotelefono) telefono\n"
                    + "from empleados e, empresas em, personas p, familiares f, tiposfamiliares t, personas fam , \n"
                    + "(select tel.secuencia, tel.persona, tel.fechavigencia, tel.numerotelefono, ttel.nombre tipotelefono\n"
                    + "from telefonos tel, tipostelefonos ttel\n"
                    + "where ttel.secuencia = tel.tipotelefono\n"
                    + "and tel.fechavigencia = (select max(teli.fechavigencia) \n"
                    + "    from telefonos teli\n"
                    + "    where teli.persona = tel.persona\n"
                    + "    and teli.tipotelefono = tel.tipotelefono) ) ltel\n"
                    + "where   \n"
                    + "p.secuencia = e.persona    \n"
                    + "and e.empresa = em.secuencia    \n"
                    + "and f.persona = p.secuencia      \n"
                    + "and f.personafamiliar=fam.secuencia  \n"
                    + "and t.secuencia = f.tipofamiliar \n"
                    + "and fam.secuencia = ltel.persona(+) \n"
                    + "and e.secuencia = ? "
                    + "order by fam.nombre, fam.primerapellido, fam.segundoapellido, ltel.tipotelefono";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);

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
        String secuenciaJefe = getSecuenciaEmplPorSeudonimo(jefe, nit, cadena );
        String secuenciaEmpresa = getSecuenciaPorNitEmpresa(nit);
        setearPerfil();
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
    
    public String getDocumentoCorreoODocumento(String usuario, String cadena) {
       System.out.println("Parametros getDocumentoCorreoODocumento() usuario: "+usuario+", cadena: "+cadena);
       String documento=null;
        try {
            setearPerfil(cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P WHERE P.EMAIL=?";
            if (this.validarCodigoUsuario(usuario)) {
                 sqlQuery+=" OR P.NUMERODOCUMENTO=?"; // si el valor es numerico validar por numero de documento
            }
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, usuario);
            if (this.validarCodigoUsuario(usuario)) {
               query.setParameter(2, usuario);
            }
            documento =  query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: "+ConexionesKioskosFacadeREST.class.getName()+" getDocumentoCorreoODocumento: "+e.getMessage());
            try {
                String sqlQuery2 = "SELECT P.NUMERODOCUMENTO DOCUMENTO "
                        + "FROM PERSONAS P, EMPLEADOS E "
                        + "WHERE "
                        + "P.SECUENCIA=E.PERSONA "
                        + "AND (P.EMAIL=?";
                if (this.validarCodigoUsuario(usuario)) {
                    sqlQuery2 += " OR E.CODIGOEMPLEADO=?"; // si el valor es numerico validar por codigoempleado
                }
                sqlQuery2+=")";
                System.out.println("Query2: " + sqlQuery2);
                Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery2);
                query2.setParameter(1, usuario);
                if (this.validarCodigoUsuario(usuario)) {
                    query2.setParameter(2, usuario);
                }
                documento =  query2.getSingleResult().toString();
                System.out.println("Validación documentoPorEmpleado: "+documento);
            } catch (Exception ex) {
                System.out.println("Error 2: " + ConexionesKioskos.class.getName() + " getDocumentoCorreoODocumento(): ");
            }
        }
        return documento;
   }
    
    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
       System.out.println("getDocumentoPorSeudonimo()");
       String documento=null;
        try {
            setearPerfil(cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento =  query.getSingleResult().toString();
            System.out.println("documento: "+documento);
        } catch (Exception e) {
            System.out.println("Error: getDocumentoPorSeudonimo: "+e.getMessage());
        }
        return documento;
   }   
      
    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: "+seudonimo+", nitEmpresa: "+nitEmpresa+", cadena: "+cadena);
       String secuencia=null;
        try {
            setearPerfil(cadena);
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO FROM EMPLEADOS E, CONEXIONESKIOSKOS CK WHERE CK.EMPLEADO=E.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

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
    
   public BigDecimal consultarCodigoJornada(String seudonimo, String nitEmpresa, String fechaDisfrute, String cadena) throws Exception {
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
        String secEmpleado=getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
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
   
   
    private boolean validaFechaPago(String seudonimo, String nitEmpresa, String fechainicialdisfrute, String cadena) throws Exception {
            Calendar cl = Calendar.getInstance();
            cl.setTime(getFechaUltimoPago(seudonimo, nitEmpresa, cadena));
            return getDate(fechainicialdisfrute).after(cl.getTime());
    }   
   
    public Date getFechaUltimoPago(String seudonimo, String nitempresa, String cadena) throws Exception {
        BigDecimal res = null;
        try {
        setearPerfil(cadena);
        String secEmpleado=getSecuenciaEmplPorSeudonimo(seudonimo, nitempresa, cadena);
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
      

    public BigDecimal consultaTraslapamientos(
             String seudonimo,
             String nitempresa, 
             String fechaIniVaca, 
             String fechaFinVaca,
             String cadena) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "consultaTraslapamientos" + "()");
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitempresa, cadena);
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
            String fechaIniVaca,
            String cadena) throws Exception {
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitempresa, cadena);
        System.out.println(this.getClass().getName() + ".verificaExistenciaSolicitud()");
        System.out.println("verificaExistenciaSolicitud-secEmpleado: " + secEmpleado);
        System.out.println("verificaExistenciaSolicitud-fechaIniVaca: " + fechaIniVaca);
        /*SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
        String txtFecha = formato.format(fechaIniVaca);*/
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICAEXISTESOLICITUD(?, to_date(?,'DDMMYYYY') ) "
                + "FROM DUAL ";
        System.out.println("verificaExistenciaSolicitud-consulta: " + consulta);
        Query query = null;
        BigDecimal conteo = null;
        try {
            try {
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
            System.out.println("verificaExistenciaSolicitud-excepcion: " + e);
//            throw e;
            throw new Exception("Error verificando si la solicitud ya existe " + e);
        }
        System.out.println("verificaExistenciaSolicitud-conteo: " + conteo);
        return conteo;
    }
    
    @GET
    @Path("/validaFechaInicioVacaciones")
    @Produces(MediaType.APPLICATION_JSON)
    public String validaFechaInicioSoliciVacaciones(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, 
            @QueryParam("fechainicio") String fechainicialdisfrute, @QueryParam("cadena") String cadena) {
        String mensaje = "";
        boolean valido = true;
        try { 
            BigDecimal codigoJornada = consultarCodigoJornada(seudonimo, nitEmpresa, fechainicialdisfrute, cadena);
            if (!validaFechaPago(seudonimo, nitEmpresa, fechainicialdisfrute, cadena)) {
                mensaje+= "La fecha seleccionada es inferior a la última fecha de pago.";
                valido = false;
            } else if (verificarFestivo(fechainicialdisfrute)){
                mensaje+= "La fecha seleccionada es un día festivo.";
                valido = false;
            } else if (!verificarDiaLaboral(fechainicialdisfrute,codigoJornada)){
                mensaje+= "La fecha seleccionada no es un día laboral.";
                valido = false;
            }
            
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            mensaje =  "Ha ocurrido un error, por favor intentalo de nuevo más tarde.";
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("valido", valido);
            obj.put("mensaje", mensaje);
        } catch (JSONException ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj.toString();
    }
    
    public String getApellidoNombreXsecEmpl(String secEmpl) {
        System.out.println("getApellidoNombreXsecEmpl() secuenciaEmpl: "+secEmpl);
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
            System.out.println("Error getApellidoNombreXsecEmpl(): " + e);
        }
        return nombre;
    }
    
    @GET
    @Path("/enviaReporteInfoRRHH")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean enviaReporteInfoRRHH(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, 
            @QueryParam("observacion") String observacionNovedad, @QueryParam("urlKiosco") String urlKiosco, @QueryParam("grupo") String grupo, @QueryParam("cadena") String cadena) {
        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String nombreEmpl = getApellidoNombreXsecEmpl(secEmpl);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        String mensaje = "Nos permitimos informar que "+nombreEmpl+" ha reportado la siguiente "
                + "observación desde el módulo Kiosco para su validación: "
                + "<br><br>"
                + observacionNovedad;
        System.out.println("enviaReporteInfoRRHH(): seudonimo "+seudonimo+", nit: "+nitEmpresa+", urlKiosco: "+urlKiosco+", grupo: "+grupo);
        boolean enviado = true;
        try { 
            EnvioCorreo e= new EnvioCorreo();
            if (e.enviarCorreoInformativo("Módulo Kiosco: Reporte de corrección de información de "+nombreEmpl+" "+fecha,
                    "Estimado personal de Nómina y RRHH:", mensaje, nitEmpresa)) {
                enviado = true;
            } else {
                enviado= false;
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            mensaje =  "Ha ocurrido un error, por favor intentalo de nuevo más tarde.";
        }
        return enviado;
    }        
    
}
