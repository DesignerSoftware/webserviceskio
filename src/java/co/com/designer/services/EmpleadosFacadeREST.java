package co.com.designer.services;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.kiosko.entidades.Recordatorios;
import co.com.designer.kiosko.generales.GenerarCorreo;
import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConexiones;
import co.com.designer.persistencia.implementacion.PersistenciaEmpleados;
import co.com.designer.persistencia.implementacion.PersistenciaKioPersonalizaciones;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.implementacion.PersistenciaSolucionesNodos;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpleados;
import co.com.designer.persistencia.interfaz.IPersistenciaKioPersonalizaciones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaSolucionesNodos;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.ws.rs.GET;
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
 * @author Thalia Manrique
 * @author Mateo Bohorquez
 * @author Wilmer Uribe
 */
@Stateless
@Path("empleados")
public class EmpleadosFacadeREST {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaEmpleados persisEmpleados;
    private IPersistenciaSolucionesNodos persisSolNod;
    private IPersistenciaKioPersonalizaciones persisPersonalizaciones;

    public EmpleadosFacadeREST() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persisConexiones = new PersistenciaConexiones();
        persisEmpleados = new PersistenciaEmpleados();
        persisSolNod = new PersistenciaSolucionesNodos();

    }

    @GET
    @Path("/datosEmpleadoNit/{empleado}/{nit}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDatosEmpleadoNit(@PathParam("empleado") String empleado,
            @PathParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        System.out.println("parametros getDatosEmpleadosNit():  empleado: " + empleado + " nit: " + nitEmpresa + " cadena " + cadena);
        List s = null;
        try {
            s = this.persisEmpleados.getDatosEmpleadoNit(empleado, nitEmpresa, cadena);
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + "getDatosEmpleadoNit: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    @GET
    @Path("/datosFamiliaEmpleado/{empleado}/{nit}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDatosFamiliaEmpleado(@PathParam("empleado") String empleado, @PathParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("EmpleadosFacadeREST" + ".getDatosFamiliaEmpleado(): " + "Parametros: "
                + "empleado: " + empleado + " nit: " + nitEmpresa + " cadena: " + cadena
        );
        List s = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select \n"
                    + "fam.nombre ||' '|| fam.primerapellido ||' '|| fam.segundoapellido nombreFamiliar, t.tipo Parentesco, \n"
                    + "decode(ltel.secuencia, null, ' ', \n"
                    + "ltel.tipotelefono||' - '|| \n"
                    + "--to_char(ltel.fechavigencia,'DD/MM/YYYY')||' '|| \n"
                    + "ltel.numerotelefono) telefono \n"
                    + "from ConexionesKioskos ck, empresas em, personas p, familiares f, tiposfamiliares t, personas fam , \n"
                    + "(select tel.secuencia, tel.persona, tel.fechavigencia, tel.numerotelefono, ttel.nombre tipotelefono \n"
                    + "from telefonos tel, tipostelefonos ttel, ConexionesKioskos cki \n"
                    + "where ttel.secuencia = tel.tipotelefono \n"
                    + "AND cki.persona = tel.persona \n"
                    + "and cki.seudonimo = ? \n"
                    + "and cki.nitEmpresa = ? \n"
                    + "and tel.fechavigencia = (select max(teli.fechavigencia) \n"
                    + "    from telefonos teli \n"
                    + "    where teli.persona = tel.persona \n"
                    + "    and teli.tipotelefono = tel.tipotelefono) ) ltel \n"
                    + "where \n"
                    + "p.secuencia = ck.persona \n"
                    + "and em.nit = ck.nitEmpresa \n"
                    + "and f.persona = p.secuencia \n"
                    + "and f.personafamiliar=fam.secuencia \n"
                    + "and t.secuencia = f.tipofamiliar \n"
                    + "and fam.secuencia = ltel.persona(+) \n"
                    + "and ck.seudonimo = ? \n"
                    + "and ck.nitEmpresa = ? \n"
                    + "order by fam.nombre, fam.primerapellido, fam.segundoapellido, ltel.tipotelefono";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, empleado);
            query.setParameter(2, nitEmpresa);
            query.setParameter(3, empleado);
            query.setParameter(4, nitEmpresa);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println(this.getClass().getName() + ".getDatosFamiliaEmpleado(): " + "Error " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error " + ex).build();
        }
    }

    @GET
    @Path("/telefonosEmpleadoNit")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTelefonosEmpleado(@QueryParam("usuario") String seudonimo, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("EmpleadoFacadeREST" + ".getTelefonosEmpleado(): " + "Parametros: "
                + "empleado: " + seudonimo + " nit: " + nitEmpresa + " cadena " + cadena
        );
        List s = null;
        try {
            String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select "
                    + "t.numerotelefono numeroTelefono,  "
                    + "tt.nombre tipoTelefono "
                    + "from conexioneskioskos ck, empresas em, personas p, telefonos t, tipostelefonos tt "
                    + "where ck.persona=p.secuencia "
                    + "and ck.nitEmpresa=em.nit "
                    + "and t.persona = p.secuencia "
                    + "and t.fechavigencia = (select max(ti.fechavigencia) "
                    + "  from telefonos ti "
                    + "  where ti.persona = t.persona "
                    + "  and ti.fechavigencia <= sysdate) "
                    + "and t.tipotelefono = tt.secuencia(+) "
                    + "and p.numerodocumento= ? "
                    + "and em.nit= ? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);

            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getTelefonosEmpleado(): " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    /**
     * Devuelve Lista de experiencias laborales
     *
     * @param empleado
     * @param nitEmpresa
     * @param cadena
     * @return Lista de experiencias laborales
     */
    @GET
    @Path("/datosExperienciaLab")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getDatosExperienciaLab(@QueryParam("empleado") String empleado, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("EmpleadosFacadeREST" + ".getDatosExperienciaLab(): " + "Parametros: "
                + "empleado: " + empleado + " nit: " + nitEmpresa + " cadena: " + cadena
        );
        List exLab = null;
        try {
            String secEmpl = getDocumentoPorSeudonimo(empleado, nitEmpresa, cadena);
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select \n"
                    + "p.numerodocumento, \n"
                    + "ex.empresa empresa, \n"
                    + "to_char(ex.fechadesde,'DD/MM/YYYY'), \n"
                    + "to_char(ex.fechahasta,'DD/MM/YYYY'), \n"
                    + "ex.jefeinmediato, \n"
                    + "ex.telefono, \n"
                    + "ex.cargo, \n"
                    + "ex.alcance, \n"
                    + "mo.nombre motivo, \n"
                    + "sec.descripcion sectorec \n"
                    + "from hvexperienciaslaborales ex, hvhojasdevida hv, personas p, motivosretiros mo, \n"
                    + "sectoreseconomicos sec, ConexionesKioskos ck \n"
                    + "where ex.hojadevida = hv.secuencia \n"
                    + "and hv.persona = p.secuencia \n"
                    + "and ck.persona = p.secuencia \n"
                    + "and ex.motivoretiro = mo.secuencia(+) \n"
                    + "and ex.sectoreconomico = sec.secuencia(+) \n"
                    + "and p.numerodocumento = ? \n"
                    + "and ck.nitEmpresa = ? \n"
                    + "order by ex.fechahasta";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, nitEmpresa);
            exLab = query.getResultList();
            exLab.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(exLab).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getDatosExperienciaLab: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    public String getDocumentoCorreoODocumento(String usuario, String nitEmpresa, String cadena) {
        System.out.println("Parametros getDocumentoCorreoODocumento() usuario: " + usuario + ", cadena: " + cadena);
        String documento = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P WHERE lower(P.EMAIL)=lower(?)";
            if (this.validarCodigoUsuario(usuario)) {
                // si el valor es numerico validar por numero de documento
                sqlQuery += " OR P.NUMERODOCUMENTO=?";
            }
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, usuario);
            if (this.validarCodigoUsuario(usuario)) {
                query.setParameter(2, usuario);
            }
            documento = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println(ConexionesKioskosFacadeREST.class.getName() + " getDocumentoCorreoODocumento: " + "Error: " + e.getMessage());
            try {
                String sqlQuery2 = "SELECT P.NUMERODOCUMENTO DOCUMENTO "
                        + "FROM PERSONAS P, EMPLEADOS E "
                        + "WHERE "
                        + "P.SECUENCIA=E.PERSONA "
                        + "AND (lower(P.EMAIL)=lower(?)";
                if (this.validarCodigoUsuario(usuario)) {
                    // si el valor es numerico validar por codigoempleado
                    sqlQuery2 += " OR E.CODIGOEMPLEADO=?";
                }
                sqlQuery2 += ")";
                System.out.println("Query2: " + sqlQuery2);
                Query query2 = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery2);
                query2.setParameter(1, usuario);
                if (this.validarCodigoUsuario(usuario)) {
                    query2.setParameter(2, usuario);
                }
                documento = query2.getSingleResult().toString();
                System.out.println("Validación documentoPorEmpleado: " + documento);
            } catch (Exception ex) {
                System.out.println("Error 2: " + ConexionesKioskos.class.getName() + " getDocumentoCorreoODocumento(): ");
            }
        }
        return documento;
    }

    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getDocumentoPorSeudonimo() seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String documento = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO "
                    + "FROM PERSONAS P, CONEXIONESKIOSKOS CK "
                    + "WHERE CK.PERSONA=P.SECUENCIA "
                    + "AND lower(CK.SEUDONIMO)=lower(?) "
                    + "AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento = query.getSingleResult().toString();
            System.out.println("documento: " + documento);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getDocumentoPorSeudonimo: " + e.getMessage());
        }
        return documento;
    }

    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secuencia = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO "
                    + "FROM EMPLEADOS E, CONEXIONESKIOSKOS CK "
                    + "WHERE CK.EMPLEADO=E.SECUENCIA "
                    + "AND CK.SEUDONIMO= ? "
                    + "AND CK.NITEMPRESA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuencia: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }

    public String getSecuenciaPorNitEmpresa(String nitEmpresa, String cadena) {
        String secuencia = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EM.SECUENCIA SECUENCIAEMPRESA "
                    + "FROM EMPRESAS EM "
                    + "WHERE EM.NIT= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuencia: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecuenciaPorNitEmpresa: " + e.getMessage());
        }
        return secuencia;
    }

    public boolean validarCodigoUsuario(String usuario) {
        boolean resultado = false;
        try {
            BigInteger numUsuario = new BigInteger(usuario);
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
        System.out.println("Parametros getSecuencia(): usuario: " + usuario + ", cadena: " + cadena);
        BigDecimal res = null;
        try {
            String sqlQuery = "SELECT EMPLEADO FROM CONEXIONESKIOSKOS WHERE SEUDONIMO= ? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            res = (BigDecimal) query.getSingleResult();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ex);
            res = BigDecimal.ZERO;
        }
        return res.toString();
    }

    public BigDecimal consultarCodigoJornada(String seudonimo, String nitEmpresa, String fechaDisfrute, String cadena) throws Exception {
        System.out.println("Parametros consultarCodigoJornada()): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", fechaDisfrute: " + fechaDisfrute + ", cadena: " + cadena);
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
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("secuencia: " + secEmpleado);
        System.out.println("fecha en txt: " + fechaDisfrute);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
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

    private boolean validaFechaPago(String seudonimo, String nitEmpresa, String fechainicialdisfrute, String cadena) throws Exception {
        Calendar cl = Calendar.getInstance();
        cl.setTime(getFechaUltimoPago(seudonimo, nitEmpresa, cadena));
        return getDate(fechainicialdisfrute, cadena).after(cl.getTime());
    }

    public Date getFechaUltimoPago(String seudonimo, String nitEmpresa, String cadena) throws Exception {
        System.out.println("Parametros getFechaUltimoPago(): seudonimo: " + seudonimo + ", nitempresa: " + nitEmpresa + ", cadena: " + cadena);
        BigDecimal res = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String consulta = "SELECT GREATEST("
                    + "CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1), "
                    + "NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 80)"
                    + ", CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1))) "
                    + "FROM DUAL ";
            Date fechaUltimoPago = null;
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
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
            System.out.println("Error " + this.getClass().getName() + ".getFechaUltimoPago():  " + e);
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
                + "TO_DATE( ?, 'dd/mm/yyyy') "
                + "FROM DUAL ";
        Query query = null;
        Date fechaRegreso = null;
        try {
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaStr);
            fechaRegreso = (Date) (query.getSingleResult());
            return fechaRegreso;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia en calculaFechaRegreso.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en " + this.getClass().getName() + ".calculaFechaRegreso");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general en calculaFechaRegreso. " + e);
            throw new Exception(e.toString());
        }
    }

    public boolean verificarDiaLaboral(String fechaDisfrute, BigDecimal codigoJornada, String nitEmpresa, String cadena) throws Exception {
        System.out.println(this.getClass().getName() + "." + "verificarDiaLaboral(): fechaDisfrute: " + fechaDisfrute + ", codigoJornada: " + codigoJornada + ", cadena: " + cadena);
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
        String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        c.setTime(getDate(fechaDisfrute, cadena));
        diaSemana = c.get(Calendar.DAY_OF_WEEK);
        strFechaDisfrute = nombreDia(diaSemana);
        System.out.println("strFechaDisfrute: " + strFechaDisfrute);
        try {
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
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
            System.out.println("Error " + this.getClass().getName() + ".verificarDiaLaboral(): " + e);
            throw new Exception(e.toString());
        }
    }

    public boolean verificarFestivo(String fechaDisfrute, String nitEmpresa, String cadena) throws Exception {
        System.out.println(this.getClass().getName() + "." + "verificarFestivo(): fechaDisfrute: " + fechaDisfrute + ", cadena: " + cadena);
        String consulta = "select COUNT(*) "
                + "FROM FESTIVOS F, PAISES P "
                + "WHERE P.SECUENCIA = F.PAIS "
                + "AND P.NOMBRE = ? "
                + "AND F.DIA = TO_DATE( ? , 'DD/MM/YYYY') ";
        Query query = null;
        BigDecimal conteoDiaFestivo;
        boolean esDiaFestivo;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, "COLOMBIA");
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
            System.out.println("Error verificarFestivo(): fechaDisfrute: " + fechaDisfrute + ", cadena: " + cadena + e);
            throw new Exception(e.toString());
        }
    }

    public BigDecimal consultaTraslapamientos(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca,
            String fechaFinVaca,
            String cadena) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "consultaTraslapamientos" + "()");
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICARTRASLAPAMIENTO( ?, ? , ? ) "
                + "FROM DUAL ";
        Query query = null;
        BigDecimal contTras = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
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
            System.out.println("Error general en " + this.getClass().getName() + "consultaTraslapamientos(). " + e);
            throw new Exception(e.toString());
        }
    }

    /**
     * Método que valida si la fecha de disfrute que recibe como parametro ya tiene una solicitud asociada
     * 
     * @param seudonimo
     * @param nitEmpresa
     * @param fechaIniVaca
     * @param cadena
     * @return 
     * @throws java.lang.Exception 
     */
    public BigDecimal verificaExistenciaSolicitud(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca,
            String cadena) throws Exception {
        System.out.println("Parametros verificaExistenciaSolicitud(): seudonimo: " + seudonimo + ", nitempresa: " + nitEmpresa + ", fechaIniVaca: " + fechaIniVaca + ", cadena: " + cadena);
        String secEmpleado = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("verificaExistenciaSolicitud-secEmpleado: " + secEmpleado);
        System.out.println("verificaExistenciaSolicitud-fechaIniVaca: " + fechaIniVaca);
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICAEXISTESOLICITUD(?, to_date(?,'DDMMYYYY') ) "
                + "FROM DUAL ";
        System.out.println("verificaExistenciaSolicitud-consulta: " + consulta);
        Query query = null;
        BigDecimal conteo = null;
        try {
            try {
                String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
                this.rolesBD.setearPerfil(esquema, cadena);
                query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
                query.setParameter(1, secEmpleado);
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
                mensaje += "La fecha seleccionada es inferior a la última fecha de pago.";
                valido = false;
            } else if (verificarFestivo(fechainicialdisfrute, nitEmpresa, cadena)) {
                mensaje += "La fecha seleccionada es un día festivo.";
                valido = false;
            } else if (!verificarDiaLaboral(fechainicialdisfrute, codigoJornada, nitEmpresa, cadena)) {
                mensaje += "La fecha seleccionada no es un día laboral.";
                valido = false;
            }

        } catch (Exception ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            mensaje = "Ha ocurrido un error, por favor intentalo de nuevo más tarde.";
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

    @GET
    @Path("/validaFechaInicioAusentismo")
    @Produces(MediaType.APPLICATION_JSON)
    public String validaFechaInicioSoliciAusentismo(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("fechainicio") String fechainicialdisfrute, @QueryParam("cadena") String cadena) {
        String mensaje = "";
        boolean valido = true;
        try {
            if (!validaFechaPago(seudonimo, nitEmpresa, fechainicialdisfrute, cadena)) {
                mensaje += "La fecha seleccionada es inferior a la última fecha de pago.";
                valido = false;
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            mensaje = "Ha ocurrido un error, por favor intentalo de nuevo más tarde.";
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

    public String getApellidoNombreXsecEmpl(String secEmpl, String nitEmpresa, String cadena) {
        System.out.println("getApellidoNombreXsecEmpl() secuenciaEmpl: " + secEmpl);
        String nombre = null;
        String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        try {
            String sqlQuery = "SELECT UPPER(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE) NOMBRE "
                    + " FROM PERSONAS P, EMPLEADOS EMPL "
                    + " WHERE P.SECUENCIA=EMPL.PERSONA "
                    + " AND EMPL.SECUENCIA=?";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            nombre = (String) query.getSingleResult();
            System.out.println("nombre: " + nombre);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getApellidoNombreXsecEmpl(): " + e);
        }
        return nombre;
    }

    @GET
    @Path("/enviaReporteInfoRRHH")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean enviaReporteInfoRRHH(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa,
            @QueryParam("observacion") String observacionNovedad, @QueryParam("asunto") String asunto,
            @QueryParam("urlKiosco") String urlKiosco, @QueryParam("grupo") String grupo, @QueryParam("cadena") String cadena) {

        System.out.println("EmpleadosFacadeREST." + "enviaReporteInfoRRHH(): Parametros: "
                + "seudonimo " + seudonimo
                + " nit: " + nitEmpresa
                + " urlKiosco: " + urlKiosco
                + " grupo: " + grupo
                + " cadena: " + cadena);

        this.persisPersonalizaciones = new PersistenciaKioPersonalizaciones();

        String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String nombreEmpl = getApellidoNombreXsecEmpl(secEmpl, nitEmpresa, cadena);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        String mensaje = "Nos permitimos informar que "
                + nombreEmpl
                + " ha reportado la siguiente "
                + "observación desde el módulo Kiosco para su validación: "
                + "<br><br>"
                + observacionNovedad;

        String correoUsuario = getCorreoConexioneskioskos(seudonimo, nitEmpresa, cadena);

        boolean enviado = false;
        asunto += " de " + nombreEmpl + " " + fecha;
        try {
            GenerarCorreo e = new GenerarCorreo();
            List correos = this.persisPersonalizaciones.getCorreosContactosNomina(nitEmpresa, cadena, "");
            String correoDestinatarios = "";
            Iterator<String> it = correos.iterator();
            System.out.println("size: " + correos.size());
            while (it.hasNext()) {
                String correoenviar = it.next();
                System.out.println("correo auditoria: " + correoenviar);
                correoDestinatarios += correoenviar;
                if (it.hasNext()) {
                    correoDestinatarios += ",";
                }
            }
            enviado = e.enviarCorreoInformativo(
                    correoDestinatarios,
                    correoUsuario,
                    asunto, "Estimado personal de Nómina y RRHH:", mensaje, nitEmpresa, cadena, urlKiosco + "#/login/" + grupo
            );
        } catch (Exception ex) {
            enviado = false;
            Logger.getLogger(EmpleadosFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return enviado;
    }

    /**
     * 
     * @param seudonimo
     * @param nitEmpresa
     * @param cadena
     * @return 
     */
    public String getCorreoConexioneskioskos(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros " + this.getClass().getName() + ".getCorreoConexioneskioskos(): seudonimo: " + seudonimo + ", empresa: " + nitEmpresa + ", cadena: " + cadena);
        String correo = null;
        String sqlQuery;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            sqlQuery = "SELECT P.EMAIL "
                    + "FROM PERSONAS P, conexioneskioskos ck "
                    + "WHERE p.secuencia=ck.persona "
                    + "AND ck.seudonimo= ? "
                    + "and ck.nitempresa= ? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getCorreoConexioneskioskos(): " + e.getMessage());
            System.out.println("seudonimo: " + seudonimo + " - nitEmpresa: " + nitEmpresa);
        }
        return correo;
    }

    @GET
    @Path("/educacionesFormales")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducacionesFormales(@QueryParam("usuario") String seudonimo, @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("parametros getEducacionesFormales(): usuario: " + seudonimo + " nitEmpresa: " + nitEmpresa + " cadena " + cadena);
        List s = null;
        String secEmpl = null;
        try {
            secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar secuencia del empleado");
        }
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select "
                    + "te.nombre, p.descripcion, i.descripcion institucion, to_char(vf.fechavigencia, 'dd/mm/yyyy') fechaVigencia, "
                    + "to_char(vf.fechavencimiento, 'dd/mm/yyyy') fechaVencimiento, ad.descripcion adiest, vf.NUMEROTARJETA, vf.OBSERVACION, "
                    + "to_char(FECHAEXPEDICIONTARJETA, 'dd/mm/yyyy') fechaExpedicionTarjeta"
                    + ", to_char(FECHAVENCIMIENTOTARJETA, 'dd/mm/yyyy') fechaVencimientoTarjeta "
                    + "from "
                    + "personas per, vigenciasformales vf, tiposeducaciones te, profesiones p, instituciones i, adiestramientosf ad "
                    + ", ConexionesKioskos ck "
                    + "where "
                    + "vf.persona=per.secuencia "
                    + "AND ck.persona = per.secuencia "
                    + "and vf.tipoeducacion=te.secuencia "
                    + "and vf.profesion = p.secuencia "
                    + "and vf.institucion = i.secuencia(+) "
                    + "and ad.secuencia(+)=vf.adiestramientof "
                    + "AND ck.seudonimo = ? "
                    + "AND ck.nitEmpresa = ? "
                    + "order by vf.fechavigencia DESC ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println(this.getClass().getName() + ".getEducacionesFormales(): " + "Error-1: " + ex);
            try {
                String sqlQuery = "select "
                        + "te.nombre, p.descripcion, i.descripcion institucion, to_char(vf.fechavigencia, 'dd/mm/yyyy') fechaVigencia, "
                        + "to_char(vf.fechavencimiento, 'dd/mm/yyyy') fechaVencimiento, ad.descripcion adiest, vf.NUMEROTARJETA, vf.OBSERVACION, "
                        + "to_char(FECHAEXPEDICIONTARJETA, 'dd/mm/yyyy') fechaExpedicionTarjeta, to_char(FECHAVENCIMIENTOTARJETA, 'dd/mm/yyyy') fechaVencimientoTarjeta "
                        + "from "
                        + "empleados e, personas per, vigenciasformales vf, tiposeducaciones te, profesiones p, instituciones i, adiestramientosnf ad "
                        + "where "
                        + "vf.persona=per.secuencia "
                        + "and e.persona=per.secuencia "
                        + "and vf.tipoeducacion=te.secuencia "
                        + "and vf.profesion = p.secuencia "
                        + "and vf.institucion = i.secuencia(+) "
                        + "and ad.secuencia(+)=vf.adiestramientof "
                        + "and e.secuencia=? "
                        + "order by vf.fechavigencia desc";
            } catch (Exception e) {
                System.out.println(this.getClass().getName() + ".getEducacionesFormales(): " + "Error-2: " + e);
            }
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    @GET
    @Path("/educacionesNoFormales")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEducacionesNoFormales(@QueryParam("usuario") String seudonimo, @QueryParam("empresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("parametros getEducacionesNoFormales(): usuario: " + seudonimo + " nitEmpresa: " + nitEmpresa + " cadena " + cadena);
        List s = null;
        try {
            String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select "
                    + "c.nombre CURSO, vf.titulo, i.descripcion institucion, to_char(vf.fechavigencia, 'dd/mm/yyyy') fechavigencia"
                    + ", to_char(vf.fechavencimiento, 'dd/mm/yyyy') fechaVencimiento, "
                    + "ad.DESCCRIPCION adiest"
                    + ", vf.OBSERVACION \n"
                    + "from "
                    + "ConexionesKioskos ck, personas per, vigenciasnoformales vf, cursos c, instituciones i, adiestramientosnf ad "
                    + "where "
                    + "vf.persona=per.secuencia "
                    + "and ck.persona=per.secuencia "
                    + "and vf.curso = c.secuencia "
                    + "and vf.institucion = i.secuencia(+) "
                    + "and ad.secuencia(+)=vf.adiestramientonf "
                    + "and ck.seudonimo= ? "
                    + "and ck.nitEmpresa= ? "
                    + "order by vf.fechavigencia desc";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            s = query.getResultList();
            s.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            System.out.println(this.getClass().getName() + "getEducacionesNoFormales(): " + "Error " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

    @GET
    @Path("/getNotificaciones")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNotificaciones(@QueryParam("usuario") String seudonimo,
            @QueryParam("tipoNotificacion") String tipoNotificacion,
            @QueryParam("empresa") String nitEmpresa,
            @QueryParam("cadena") String cadena) {
        List s = null;
        String secEmpl = null;
        try {
            secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        } catch (Exception e) {
            System.out.println("Error al consultar secuencia del empleado");
        }
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "";
            if (tipoNotificacion.equals("VACACION")) {
                sqlQuery = "SELECT \n"
                        + "COUNT(*) \n"
                        + "FROM \n"
                        + "KIOESTADOSSOLICI KES, \n"
                        + "KIOSOLICIVACAS KS, \n"
                        + "KIONOVEDADESSOLICI KNS, \n"
                        + "EMPLEADOS JEFE, \n"
                        + "VwVacaPendientesEmpleados V \n"
                        + "WHERE \n"
                        + "KES.ESTADO = 'ENVIADO' "
                        + "AND KS.EMPLEADOJEFE =? \n"
                        + "AND KES.SECUENCIA = (SELECT MAX(t3.SECUENCIA) FROM KIOSOLICIVACAS t4, KIOESTADOSSOLICI t3 \n"
                        + "WHERE t4.SECUENCIA = KS.SECUENCIA AND t4.SECUENCIA = t3.KIOSOLICIVACA \n"
                        + "AND KS.SECUENCIA = KES.KIOSOLICIVACA) \n"
                        + "AND KS.KIONOVEDADSOLICI=KNS.SECUENCIA \n"
                        + "AND KS.EMPLEADOJEFE=JEFE.SECUENCIA \n"
                        + "AND KNS.VACACION=v.RFVACACION \n"
                        + "AND V.INICIALCAUSACION>=EMPLEADOCURRENT_PKG.FECHATIPOCONTRATO(KS.EMPLEADO, sysdate)";

            } else if (tipoNotificacion.equals("AUSENTISMO")) {

                sqlQuery = "SELECT COUNT(*) \n"
                        + "FROM \n"
                        + "KIOESTADOSSOLICIAUSENT KES, \n"
                        + "KIOSOLICIAUSENTISMOS KSA, \n"
                        + "KIONOVEDADESSOLICIAUSENT KNSA, \n"
                        + "EMPLEADOS JEFE \n"
                        + "WHERE \n"
                        + "KES.ESTADO = 'ENVIADO' \n"
                        + "AND KSA.EMPLEADOJEFE = ? \n"
                        + "AND KSA.SECUENCIA = KES.KIOSOLICIAUSENTISMO \n"
                        + "AND KSA.SECUENCIA=KNSA.KIOSOLICIAUSENTISMO \n"
                        + "AND KNSA.FECHAINICIALAUSENTISMO = (select MIN(ei.FECHAINICIALAUSENTISMO) \n"
                        + "from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                        + "where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                        + "and ksi.secuencia=KSA.secuencia) \n"
                        + "and KES.FECHAPROCESAMIENTO = (select max(ei.FECHAPROCESAMIENTO) \n"
                        + "from KIOESTADOSSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                        + "where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                        + "and ksi.secuencia=KSA.secuencia) \n"
                        + "AND KES.FECHAPROCESAMIENTO = (SELECT MAX(t3.FECHAPROCESAMIENTO) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                        + "WHERE t4.SECUENCIA = KSA.SECUENCIA AND t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO) \n"
                        + "AND KSA.EMPLEADOJEFE=JEFE.SECUENCIA  \n";
            } else if (tipoNotificacion.equals("RRHH")) {
                sqlQuery = "SELECT \n"
                        + "COUNT(*) \n"
                        + "FROM KIOMENSAJESRRHH rh, empresas em \n"
                        + "WHERE \n"
                        + "rh.empresa = em.secuencia \n"
                        + "and em.nit = ? \n"
                        + "AND RH.FECHAINICIO <= SYSDATE \n"
                        + "AND RH.FECHAFIN >= SYSDATE \n"
                        + "AND RH.ESTADO = 'ACTIVO' ";
            }
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            if (tipoNotificacion.equals("VACACION")) {
                query.setParameter(1, secEmpl);
            } else if (tipoNotificacion.equals("AUSENTISMO")) {
                query.setParameter(1, secEmpl);
            } else if (tipoNotificacion.equals("RRHH")) {
                query.setParameter(1, nitEmpresa);
            }
            s = query.getResultList();
            return Response.status(Response.Status.OK).entity(s).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.OK).entity(0).build();
        }
    }

    @GET
    @Path("/obtenerAnexosDocumentos/")
    public List obtenerAnexosDocumentos(@QueryParam("empleado") String empleado, @QueryParam("cadena") String cadena, @QueryParam("empresa") String nitEmpresa) {
        System.out.println("Parametros obtenerAnexosDocumentos(): empleado: " + empleado + ", cadena: " + cadena + ", nitEmpresa: " + nitEmpresa);
        File carpeta = null;
        String secEmpresa = null;
        String secEmpleado = null;
        List documentos = new ArrayList();
        String rutaArchivo = getPathDocumentos(nitEmpresa, cadena);
        try {
            secEmpresa = getSecuenciaPorNitEmpresa(nitEmpresa, cadena);
            secEmpleado = getSecuenciaEmplPorSeudonimo(empleado, nitEmpresa, cadena);
            carpeta = new File(rutaArchivo + "\\" + secEmpresa + "\\" + secEmpleado + "\\");
            System.out.println("carpeta: " + carpeta);
            File[] listado = null;
            listado = carpeta.listFiles();
            System.out.println(documentos);
            System.out.println("documentos ");
            System.out.println("Listado: " + listado);
            int totalArchivos = 0;
            try {
                totalArchivos = listado.length;
            } catch (Exception e) {
                System.out.println("Error al contar cantidad de archivos : " + e.getMessage());
            }
            if (listado == null || totalArchivos <= 0) {
                System.out.println("No hay elementos dentro de la carpeta actual");
            } else {
                try {
                    System.out.println("Cantidad de archivos: " + listado.length);
                    for (int i = 0; i < listado.length; i++) {
                        System.out.println("i=" + i);
                        System.out.println("nombre con ext " + listado[i]);
                        System.out.println("confima ext " + listado[i].getName() + " " + listado[i].getName().endsWith(".pdf"));
                        if (listado[i].getName().endsWith(".pdf") == true || listado[i].getName().endsWith(".PDF") == true) {
                            System.out.println(listado[i].getName());
                            documentos.add(listado[i].getName());
                            System.out.println(documentos);
                        } else {
                            System.out.println("Elemento no registrado");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error al validar extensiones " + e.getMessage());
                }
                System.out.println("Fin ejecución for");
            }
            System.out.println(documentos);
        } catch (Exception ex) {
            System.out.println("Error: " + this.getClass().getName() + " " + ex.getMessage());
        }

        return documentos;
    }

    @GET
    @Path("/decargarAnexo/")
    @Produces({"application/pdf"})
    public Response decargarAnexo(@QueryParam("usuario") String usuario, @QueryParam("cadena") String cadena,
            @QueryParam("empresa") String nitEmpresa, @QueryParam("anexo") String anexo) {
        System.out.println("Parametros decargarAnexo(): anexo: " + anexo + ", cadena: " + cadena + ", "
                + "nitEmpresa: " + nitEmpresa + " usuario " + usuario);
        FileInputStream fis = null;
        File file = null;
        String secEmpresa = null;
        String secEmpleado = null;
        secEmpresa = getSecuenciaPorNitEmpresa(nitEmpresa, cadena);
        secEmpleado = getSecuenciaEmplPorSeudonimo(usuario, nitEmpresa, cadena);
        String rutaArchivo = getPathDocumentos(nitEmpresa, cadena) + "\\" + secEmpresa + "\\" + secEmpleado + "\\" /* "E:\\CLIENTES\\CONSTRUCTORA DE MARCAS S.A.S"*/;
        System.out.println("ruta para encontrar anexos " + rutaArchivo);
        try {
            fis = new FileInputStream(new File(rutaArchivo + anexo));
            file = new File(rutaArchivo + anexo);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConexionesKioskosFacadeREST.class
                    .getName()).log(Level.SEVERE, "Anexo no encontrada: " + anexo, ex);
            System.getProperty("user.dir");
            System.out.println("Ruta del proyecto: " + this.getClass().getClassLoader().getResource("").getPath());;
        } finally {
            try {
                fis.close();

            } catch (IOException ex) {
                Logger.getLogger(ConexionesKioskosFacadeREST.class
                        .getName()).log(Level.SEVERE, "Error cerrando fis " + anexo, ex);
            }
        }
        Response.ResponseBuilder responseBuilder = Response.ok((Object) file);
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + anexo + "\"");
        return responseBuilder.build();
    }

    @GET
    @Path("generaQR")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response generaQR(@QueryParam("documento") String documento, @QueryParam("celular") String celular, @QueryParam("correo") String correo,
            @QueryParam("nomEmpresa") String empresa, @QueryParam("cargo") String cargo,
            @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("generaQR() path: empleado: " + documento + " nitEmpresa: " + nitEmpresa + " cadena: " + cadena
                + " celular: " + celular + " correo: " + correo + " cargo: " + cargo);
        boolean QR = false;
        try {
            boolean result = obtenerAnexo(documento, nitEmpresa, cadena);
            System.out.println("resultado : " + result);
            if (result) {
                System.out.println("toca crea el QR");
                QR = newQR(documento, celular, correo, empresa, cargo, nitEmpresa, cadena);
                return Response.status(Response.Status.CREATED).entity(QR)
                        .build();
            } else {
                System.out.println("no toca crea el QR");
            }
        } catch (Exception e) {
            System.out.println("hubo un error al momento de generar el codigo QR: " + e);
        }

        return Response.status(Response.Status.CREATED).entity(QR)
                .build();
    }

    public boolean newQR(String documento, String celular, String correo,
            String empresa, String cargo,
            String nitEmpresa, String cadena) throws Exception {
        int qr_image_width = 200;
        int qr_image_height = 200;
        String IMAGE_FORMAT = "png";
        String secEmpleado = getSecuenciaEmplPorSeudonimo(documento, nitEmpresa, cadena);
        BufferedImage image;
        String pathReprotes = getPathReportes(nitEmpresa, cadena);
        System.out.println("Path Img: " + pathReprotes);
        String nomArchivo = documento + "QR.png";
        String pathQR = pathReprotes + nomArchivo;
        String nombre = getNombreApellidoXsecEmpleado(secEmpleado, nitEmpresa, cadena);
        String data = nombre + " \n"
                + "Número: " + celular + " \n"
                + "Correo: " + correo + " \n"
                + "Empresa: " + empresa + " \n"
                + "Cargo: " + cargo + " \n ";

        // Encode URL in QR format
        BitMatrix matrix = null;
        Writer writer = new QRCodeWriter();
        try {
            matrix = writer.encode(data, BarcodeFormat.QR_CODE, qr_image_width, qr_image_height);
        } catch (WriterException e) {
            e.printStackTrace(System.err);
        }

        // Create buffered image to draw to
        image = new BufferedImage(qr_image_width, qr_image_height, BufferedImage.TYPE_INT_RGB);

        // Iterate through the matrix and draw the pixels to the image
        for (int y = 0; y < qr_image_height; y++) {
            for (int x = 0; x < qr_image_width; x++) {
                int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
                image.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
            }
        }

        // Write the image to a file
        FileOutputStream qrCode = new FileOutputStream(pathQR);
        if (ImageIO.write(image, IMAGE_FORMAT, qrCode)) {
            qrCode.close();
            return true;
        } else {
            qrCode.close();
            return false;
        }
    }

    public boolean obtenerAnexo(String documento, String nitEmpresa, String cadena) throws IOException, Exception {
        FileInputStream fis = null;
        File file = null;
        String msj = "";
        boolean valida = false;

        String RUTAFOTO = getPathReportes(nitEmpresa, cadena) + documento + "QR.png";
        try {
            fis = new FileInputStream(new File(RUTAFOTO));
            file = new File(RUTAFOTO);
            System.out.println("file.exists(): " + file.exists());
            System.out.println(file);
            //System.out.println("file.exists(): " + file.delete());
            if (file.exists()) {
                fis.close();
                msj = "Archivo encontrado";
                System.out.println("El archivo existe");
                if (!file.delete()) {
                    System.out.println("Error no se ha podido eliminar el  archivo");
                    System.err.println(
                            "I cannot find '" + file + "' ('" + file.getAbsolutePath() + "')");
                    msj = "Archivo no encontrado";
                    System.out.println("msj");
                    valida = false;
                    return valida;
                } else {
                    System.out.println("Se ha eliminado el archivo exitosamente");
                    msj = "Archivo eliminado correctamente y creado de nuevo";
                    System.out.println("msj");
                    //newQR(documento, nitEmpresa, cadena);
                    valida = true;
                    return valida;
                }

            } else {
                msj = "No exite se crea nuevo";
                System.out.println("msj");
                //newQR(documento, nitEmpresa, cadena);
                valida = true;
                return valida;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger("Anexo no encontrado: " + ex);
            msj = "Archivo no encontrado";
            System.out.println("msj");
            System.getProperty("user.dir");
            System.out.println(msj);
            valida = true;
            return valida;

        }
    }

    @GET
    @Path("existeFoto")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response exiteFoto(@QueryParam("usuario") String usuario, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) throws IOException, Exception {
        System.out.println("exiteFoto() path: USUARIO: " + usuario + " nitEmpresa: " + nitEmpresa + " cadena: " + cadena);
        FileInputStream fis = null;
        File file = null;
        boolean foto = false;
        String rutaFOTO;
        String imagen;
        String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
        String secEmpl = getSecuenciaEmplPorSeudonimo(usuario, nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        String sqlQuery = "SELECT CK.FOTOPERFIL FROM CONEXIONESKIOSKOS CK, EMPLEADOS E WHERE CK.EMPLEADO=E.SECUENCIA AND E.SECUENCIA= ? ";
        Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
        query.setParameter(1, secEmpl);
        imagen = (String) query.getSingleResult();
        rutaFOTO = getPathReportes(nitEmpresa, cadena) + imagen;
        System.out.println("rutaFOTO : ++++++ " + rutaFOTO);
        try {
            fis = new FileInputStream(new File(rutaFOTO));
            file = new File(rutaFOTO);
            System.out.println("file.exists(): " + file.exists());
            System.out.println(file);
            if (file.exists()) {
                System.out.println("Archivo encontrado");
                foto = true;

            } else {
                System.out.println("No exite se crea nuevo");
                foto = false;
            }
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger("Anexo no encontrado: " + ex);
            System.getProperty("user.dir");
            foto = false;
        }

        return Response.status(Response.Status.CREATED).entity(foto)
                .build();
    }

    public String getPathDocumentos(String nitEmpresa, String cadena) {
        System.out.println("Parametros getPathReportes(): cadena: " + cadena);
        String rutaFoto = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHARCHIVO FROM GENERALES WHERE ROWNUM<=1 ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaFotos: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getPathReportes(): " + e.getMessage());
        }
        return rutaFoto;
    }

    public String getPathReportes(String nitEmpresa, String cadena) {
        System.out.println("getPathReportes()");
        String rutaFoto = "E:\\DesignerRHN10\\Basico10\\Reportes\\kiosko\\";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1 ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaReportes: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getPathReportes: " + e.getMessage());
        }
        return rutaFoto;
    }

    public String getNombreApellidoXsecEmpleado(String secEmpelado, String nitEmpresa, String cadena) {
        String nombre = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NOMBRE || ' ' || P.PRIMERAPELLIDO \n"
                    + "FROM PERSONAS P, EMPLEADOS E WHERE E.PERSONA = P.SECUENCIA \n"
                    + "AND E.SECUENCIA = ? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpelado);
            nombre = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getPathReportes: " + e.getMessage());
        }
        return nombre;
    }

    /**
     * Devuelve una lista de proverbios
     *
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    @GET
    @Path("/proverbios")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProverbios(@QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("parametros getProverbios():" + " nit: " + nitEmpresa + " cadena: " + cadena);
        List exLab = null;
        try {

            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select \n"
                    + "r.secuencia secuencia, \n"
                    + "r.autor autor, \n"
                    + "r.mensaje mensaje \n"
                    + "from recordatorios r \n"
                    + "where r.tipo = 'PROVERBIO' ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery, Recordatorios.class
            );

            exLab = query.getResultList();
            return Response.status(Response.Status.OK).entity(exLab).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getProverbios: " + ex);
            return Response.status(Response.Status.OK).entity(0).build();
        }
    }

    /**
     * Consulta el neto de los ultimos 6 meses
     *
     * @param nitEmpresa
     * @param cadena
     * @param seudonimo
     * @return
     */
    @GET
    @Path("/ultimospagos")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUltimosPagos(@QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena,
            @QueryParam("seudonimo") String seudonimo) {
        System.out.println("parametros getUltimosPagos():" + " nit: " + nitEmpresa + " cadena: " + cadena
                + " seudonimo: " + seudonimo);
        List exLab = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            String secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT \n"
                    + "SN.FECHAPAGO FECHACORTE, \n"
                    + "TRIM(to_char(SN.FECHAPAGO,'dd-Mon','nls_date_language=spanish')) FechaCorte1, \n"
                    + "trim(replace(to_char(sum( \n"
                    + "  DECODE(SN.TIPO, 'PAGO', SN.VALOR, -SN.VALOR) \n"
                    + "  ),'$999G999G999G999G999G999'), ',','.')) NETO, \n"
                    + "sum( \n"
                    + "  DECODE(SN.TIPO,'PAGO', SN.VALOR, -SN.VALOR) \n"
                    + "  ) NETO1 \n"
                    + "FROM SOLUCIONESNODOS SN, CONCEPTOS C, CONEXIONESKIOSKOS CK \n"
                    + "WHERE \n"
                    + "SN.CONCEPTO = C.SECUENCIA \n"
                    + "AND CK.EMPLEADO = SN.EMPLEADO \n"
                    + "AND SN.TIPO IN ('PAGO', 'DESCUENTO') \n"
                    + "AND CK.EMPLEADO = ? \n"
                    + "AND (SN.FECHAPAGO BETWEEN \n"
                    + " (ADD_MONTHS(NVL(EMPLEADOCURRENT_PKG.FechaRetiro(SN.EMPLEADO,sysdate),cortesprocesos_pkg.CapturarAnteriorCorte(SN.EMPLEADO,1,sysdate+1)),-6)+1) \n"
                    + " AND nvl(EMPLEADOCURRENT_PKG.FechaRetiro(SN.EMPLEADO,sysdate),cortesprocesos_pkg.CapturarAnteriorCorte(SN.EMPLEADO,1,sysdate+1))) \n"
                    + "and exists (select 'x' \n"
                    + " from cortesprocesos cp, procesos p, tipospagos tp \n"
                    + " where cp.proceso = p.secuencia \n"
                    + " and cp.secuencia = SN.corteproceso \n"
                    + " and tp.secuencia = p.tipopago \n"
                    + " and tp.codigo=1 \n"
                    + " AND p.codigo in (1,2,4,5,8,9,10,14,16,17,20,21,25,26,46,80,81)) \n"
                    + "AND SN.ESTADO='CERRADO' \n"
                    + "group by \n"
                    + "SN.FECHAPAGO, \n"
                    + "TRIM(to_char(SN.FECHAPAGO,'dd-Mon','nls_date_language=spanish')) \n"
                    + "ORDER BY SN.FECHAPAGO ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            exLab = query.getResultList();
            return Response.status(Response.Status.OK).entity(exLab).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getUltimosPagos: " + ex);
            return Response.status(Response.Status.OK).entity(0).build();
        }
    }

    /**
     * Consulta los valores de la ultima provision.
     *
     * @param nitEmpresa
     * @param cadena
     * @param seudonimo
     * @return
     */
    @GET
    @Path("/provisiones")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProvisiones(@QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena,
            @QueryParam("seudonimo") String seudonimo) {
        System.out.println("parametros getProvisiones():" + " nit: " + nitEmpresa + " cadena: " + cadena
                + " seudonimo: " + seudonimo);
        List exLab = null;
        try {
            exLab = this.persisSolNod.getSaldoProvisiones(seudonimo, nitEmpresa, cadena);
            return Response.status(Response.Status.OK).entity(exLab).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getProvisiones: " + ex);
            return Response.status(Response.Status.OK).entity(0).build();
        }
    }
}
