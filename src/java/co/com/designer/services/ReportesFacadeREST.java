package co.com.designer.services;

import co.com.designer.kiosko.generales.EnvioCorreo;
import co.com.designer.kiosko.reportes.IniciarReporte;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author usuario
 */
@Stateless
@Path("reportes")
public class ReportesFacadeREST {

    @EJB
    private IniciarReporte iniciarReporte;

    protected EntityManager getEntityManager() {
        String unidadPersistencia = "wsreportePU";
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }

    protected EntityManager getEntityManager(String persistence) {
        String unidadPersistencia = persistence;
        EntityManager em = Persistence.createEntityManagerFactory(unidadPersistencia).createEntityManager();
        return em;
    }

    protected void setearPerfil() {
        try {
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("setearPerfil() Error: " + ex);
        }
    }

    protected void setearPerfil(String esquema, String cadenaPersistencia) {
        try {
            String rol = "ROLKIOSKO";
            if (esquema != null && !esquema.isEmpty()) {
                rol = rol + esquema.toUpperCase();
            }
            System.out.println("setearPerfil(esquema, cadena)");
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager(cadenaPersistencia).createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Error setearPerfil(cadenaPersistencia): " + ex);
        }
    }

    @GET
    @Path("generaReporte/{reporte}/{enviocorreo}/{correo}")
    @Produces("application/pdf")
    public Response generaReporte(@PathParam("reporte") String reporte, //@PathParam("id") BigDecimal id, 
            @PathParam("enviocorreo") boolean envioCorreo, @PathParam("correo") String correo,
            @QueryParam("descripcionReporte") String descripcionReporte,
            @QueryParam("codigoReporte") String codigoReporte, @QueryParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena, @QueryParam("usuario") String seudonimo, @QueryParam("grupo") String grupo, @QueryParam("urlKiosco") String urlKiosco) {
        System.out.println("generaReporte() codigo: " + codigoReporte + " nit: " + nitEmpresa);
        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);
        String urlKio = urlKiosco + "#/login/" + grupo;
        BigDecimal secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("Parametros para generar reporte: [ reporte: " + reporte + ", secuenciaEmpleado: " + secEmpl + ", envioCorreo: " + envioCorreo + ", correo: " + correo
                + ", descripcionReporte: " + descripcionReporte + ", codigo: " + codigoReporte + ", cadena: " + cadena + ", "
                + "\n seudonimo: " + seudonimo + ", grupo: " + grupo + ", urlKiosco: " + urlKiosco + "]");
        Map parametros = new HashMap();
        parametros.put("secuenciaempleado", secEmpl);
        long tiempo = System.currentTimeMillis();
        String nombreReporte = reporte + "_" + secEmpl + "_" + tiempo + ".pdf";
        System.out.println("nombreReporte:" + nombreReporte);
        String rutaGenerado = iniciarReporte.ejecutarReporte(reporte, getPathReportes(nitEmpresa, cadena), getPathArchivosPlanos(nitEmpresa, cadena), nombreReporte, "PDF", parametros, getEntityManager(cadena));
        File file = new File(rutaGenerado);
        System.out.println("Ruta generado: " + rutaGenerado);
        String servidorSmtp = getConfigServidorSMTP(nitEmpresa, cadena);
        String puerto = getConfigCorreo(nitEmpresa, "PUERTO", cadena);
        String remitente = getConfigCorreo(nitEmpresa, "REMITENTE", cadena);
        String clave = getConfigCorreo(nitEmpresa, "CLAVE", cadena);
        String autenticado = getConfigCorreo(nitEmpresa, "AUTENTICADO", cadena);
        EnvioCorreo c = new EnvioCorreo();
        try {
            setearPerfil(esquema, cadena);
            // valida si el reporte tiene auditoria
            BigDecimal retorno = null;
            String query1 = "select count(*) from kioconfigmodulos where codigoopcion=? and nitempresa=?";
            Query query = getEntityManager(cadena).createNativeQuery(query1);
            query.setParameter(1, codigoReporte);
            query.setParameter(2, nitEmpresa);
            retorno = (BigDecimal) query.getSingleResult();
            System.out.println("retorno: " + retorno);
            if (retorno.compareTo(BigDecimal.ZERO) > 0) {
                // si lleva auditoria
                System.out.println("Si lleva auditoria");
                Date fechaGeneracion = new Date();
                String fecha = new SimpleDateFormat("dd/MM/yyyy").format(fechaGeneracion);
                String hora = new SimpleDateFormat("HH:mm").format(fechaGeneracion);
                String mensaje = "Nos permitimos informar que el "
                        + fecha + " a las " + hora
                        + " se generó el reporte " + descripcionReporte
                        + " en el módulo de Kiosco Nómina Designer. "
                        + "La persona que GENERÓ el reporte es: " + getNombrePersonaXSeudonimo(seudonimo, nitEmpresa, cadena);
                String sqlQuery = "select email from kioconfigmodulos where codigoopcion=? and nitempresa=?";
                System.out.println("Query2: " + sqlQuery);
                Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                query2.setParameter(1, codigoReporte);
                query2.setParameter(2, nitEmpresa);
                List lista = query2.getResultList();
                Iterator<String> it = lista.iterator();
                System.out.println("obtener " + lista.get(0));
                System.out.println("size: " + lista.size());
                while (it.hasNext()) {
                    String correoenviar = it.next();
                    System.out.println("correo auditoria: " + correoenviar);
                    System.out.println("codigoopcion: " + codigoReporte);
                    c.pruebaEnvio2(servidorSmtp, puerto,
                            remitente, clave, autenticado, correoenviar,
                            rutaGenerado, nombreReporte,
                            "Auditoria: Reporte Kiosco - " + descripcionReporte,
                            mensaje, getPathFoto(nitEmpresa, cadena), grupo, urlKio);
                }

            } else {
                System.out.println("No lleva auditoria.");
            }

            System.out.println("nombreReporte recibido: " + reporte + " codigo: " + codigoReporte);
            
            if (envioCorreo == true) {
                System.out.println("Se debe enviar correo al empleado: " + correo);
                ConexionesKioskosFacadeREST ck = new ConexionesKioskosFacadeREST();
                // Enviar correo

                c.pruebaEnvio2(servidorSmtp, puerto, remitente, clave, autenticado, correo,
                        rutaGenerado, nombreReporte,
                        "Reporte Kiosco - " + descripcionReporte, "", getPathFoto(nitEmpresa, cadena), grupo, urlKio);
            }

            ResponseBuilder response = Response.ok((Object) file);
            Calendar fechaActual = Calendar.getInstance();
            String nomF = String.valueOf(fechaActual.get(Calendar.YEAR)) + String.valueOf(fechaActual.get(Calendar.MONTH) + 1) + String.valueOf(fechaActual.get(Calendar.DAY_OF_MONTH))
                    + String.valueOf(fechaActual.get(Calendar.HOUR_OF_DAY)) + String.valueOf(fechaActual.get(Calendar.MINUTE)) + String.valueOf(fechaActual.get(Calendar.SECOND))
                    + String.valueOf(fechaActual.get(Calendar.MILLISECOND));
            response.header("Content-Disposition", "attachment; filename=" + nomF + ".pdf");
            return response.build();

        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ":" + e.getMessage());
        } 
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("generaFoto1/{documento}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile1(@PathParam("documento") String documento, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("getFile1() path: generaFoto1");
        String rutaFoto = getPathFoto(nitEmpresa, cadena);
        File file = new File(rutaFoto + documento + ".jpg");
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .build();
    }

    @GET
    @Path("generaFoto/{documento}")
    @Consumes("application/jpeg")
    public Response recibeImagen(@PathParam("file") File file, @QueryParam("nit") String nitEmpresa, @QueryParam("cadena") String cadena) {
        System.out.println("recibeImagen() path: generaFoto");
        String rutaFoto = getPathFoto(nitEmpresa, cadena);
        file = new File(rutaFoto + file + ".jpg");
        return Response.ok(file/*, /*MediaType.APPLICATION_OCTET_STREAM*/)
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .build();
    }

    public String getPathFoto(String nitEmpresa, String cadena) {
        System.out.println("getPathFoto()");
        String rutaFoto = "E:\\DesignerRHN10\\Basico10\\fotos_empleados\\";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaFotos: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathFoto: " + e.getMessage());
        }
        return rutaFoto;
    }

    public String getPathReportes(String nitEmpresa, String cadena) {
        System.out.println("getPathReportes()");
        String rutaFoto = "E:\\DesignerRHN10\\Basico10\\Reportes\\kiosko\\";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHREPORTES FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaReportes: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getPathReportes: " + e.getMessage());
        }
        return rutaFoto;
    }

    public String getPathArchivosPlanos(String nitEmpresa, String cadena) {
        System.out.println("getPathArchivosPlanos()");
        String rutaFoto = "E:\\DesignerRHN10\\Reportes\\ArchivosPlanosRHNDSLkio\\";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT UBICAREPORTES FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaUbicaReportes: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getPathArchivosPlanos: " + e.getMessage());
        }
        return rutaFoto;
    }

    public String getConfigCorreo(String nitEmpresa, String valor, String cadena) {
        System.out.println("getConfigCorreo()");
        String servidorsmtp = "smtp.designer.com.co";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT " + valor + " FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp = query.getSingleResult().toString();
            System.out.println(valor + ": " + servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreo" + e.getMessage());
        }
        return servidorsmtp;
    }

    public String getConfigServidorSMTP(String nitEmpresa, String cadena) {
        System.out.println("getConfigCorreoServidorSMTP()");
        String servidorsmtp = "smtp.designer.com.co";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT SERVIDORSMTP FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            servidorsmtp = query.getSingleResult().toString();
            System.out.println("Servidor smtp: " + servidorsmtp);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreoServidorSMTP: " + e.getMessage());
        }
        return servidorsmtp;
    }

    public String getNombrePersonaXSeudonimo(String usuario, String nitEmpresa, String cadena) {
        System.out.println("getNombrePersonaXSeudonimo()");
        String nombre = "";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE NOMBRECOMPLETO "
                    + "FROM "
                    + "PERSONAS P, CONEXIONESKIOSKOS CK "
                    + "WHERE "
                    + "P.SECUENCIA=CK.PERSONA "
                    + "AND CK.SEUDONIMO=? AND CK.NITEMPRESA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
            nombre = query.getSingleResult().toString();
            System.out.println("getNombrePersonaXSeudonimo: Nombre completo: " + nombre);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + "getNombrePersonaXSeudonimo(): " + e.getMessage());
        }
        return nombre;
    }

    public BigDecimal getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecuenciaEmplPorSeudonimo(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        BigDecimal secuencia = null;
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO FROM EMPLEADOS E, CONEXIONESKIOSKOS CK WHERE CK.EMPLEADO=E.SECUENCIA AND CK.SEUDONIMO=? AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia = (BigDecimal) query.getSingleResult();
            System.out.println("Resultado getSecuenciaEmplPorSeudonimo(): " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }

    @GET
    @Path("/validarFechasCertingresos")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean validarFechasCertificadoIngresosRetenciones(@QueryParam("fechadesde") String fechadesde, @QueryParam("fechahasta") String fechahasta, @QueryParam("cadena") String cadena) {
        System.out.println(this.getClass().getName() + "." + "validarFechasCertificadoIngresosRetenciones(): fechadesde: " + fechadesde + ", fechahasta: " + fechahasta);
        try {
            Date fechaDesde = getDate(fechadesde, cadena);
            Date fechaHasta = getDate(fechahasta, cadena);
            SimpleDateFormat formatoDia, formatoMes, formatoAnio;
            String dia, mes, anio;
            formatoDia = new SimpleDateFormat("dd");
            formatoMes = new SimpleDateFormat("MM");
            formatoAnio = new SimpleDateFormat("yyyy");
            dia = formatoDia.format(fechaDesde);
            mes = formatoMes.format(fechaDesde);
            anio = formatoAnio.format(fechaDesde);

            if (dia.equals("01") && mes.equals("01")) {
                dia = formatoDia.format(fechaHasta);
                mes = formatoMes.format(fechaHasta);
                if (dia.equals("31") && mes.equals("12") && anio.equals(formatoAnio.format(fechaHasta))) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".validarFechasCertificadoIngresosRetenciones: " + e.getMessage());
        }
        return false;
    }

    public Date getDate(String fechaStr, String cadena) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "getDate" + "()");
        String consulta = "SELECT "
                + "TO_DATE(?, 'yyyy-mm-dd') "
                + "FROM DUAL ";
        Query query = null;
        Date fechaRegreso = null;
        try {
            query = getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaStr);
            fechaRegreso = (Date) (query.getSingleResult());
            System.out.println("getDate(): " + fechaRegreso);
            return fechaRegreso;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia en getDate()");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en getDate()");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general en getDate(). " + e);
            throw new Exception(e.toString());
        }
    }

    public String getEsquema(String nitEmpresa, String cadena) {
        System.out.println("Parametros getEsquema(): nitempresa: " + nitEmpresa + ", cadena: " + cadena);
        String esquema = null;
        String sqlQuery;
        try {
            sqlQuery = "SELECT ESQUEMA FROM CADENASKIOSKOSAPP WHERE NITEMPRESA=? AND CADENA=?";
            Query query = getEntityManager("wscadenaskioskosPU").createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, cadena);
            esquema = query.getSingleResult().toString();
            System.out.println("Esquema: " + esquema);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getEsquema(): " + e);
        }
        return esquema;
    }

    @GET
    @Path("generaReporteProvisiones/{reporte}")
    @Produces("application/pdf")
    public Response generaProvisiones(@PathParam("reporte") String reporte, //@PathParam("id") BigDecimal id, 
            @QueryParam("nit") String nitEmpresa,
            @QueryParam("cadena") String cadena, @QueryParam("usuario") String seudonimo) {
        System.out.println("generaReporte() " + " nit: " + nitEmpresa);

        String esquema = getEsquema(nitEmpresa, cadena);
        setearPerfil(esquema, cadena);

        BigDecimal secEmpl = getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("Parametros para generar reporte: [ reporte: " + reporte + ", secuenciaEmpleado: " + secEmpl + ", cadena: " + cadena + ", "
                + "\n seudonimo: " + seudonimo + "]");
        Map parametros = new HashMap();
        parametros.put("secuenciaempleado", secEmpl);

        long tiempo = System.currentTimeMillis();
        String nombreReporte = reporte + "_" + secEmpl + "_" + tiempo + ".pdf";

        String rutaGenerado = iniciarReporte.ejecutarReporte(reporte, getPathReportes(nitEmpresa, cadena), getPathArchivosPlanos(nitEmpresa, cadena), nombreReporte, "PDF", parametros, getEntityManager(cadena));
        File file = new File(rutaGenerado);

        try {
            setearPerfil(esquema, cadena);

            System.out.println("nombreReporte recibido: " + reporte);

            ResponseBuilder response = Response.ok((Object) file);
            Calendar fechaActual = Calendar.getInstance();
            String nomF = String.valueOf(fechaActual.get(Calendar.YEAR)) + String.valueOf(fechaActual.get(Calendar.MONTH) + 1) + String.valueOf(fechaActual.get(Calendar.DAY_OF_MONTH))
                    + String.valueOf(fechaActual.get(Calendar.HOUR_OF_DAY)) + String.valueOf(fechaActual.get(Calendar.MINUTE)) + String.valueOf(fechaActual.get(Calendar.SECOND))
                    + String.valueOf(fechaActual.get(Calendar.MILLISECOND));
            response.header("Content-Disposition", "attachment; filename=" + nomF + ".pdf");
            return response.build();

        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ":" + e.getMessage());
        } 
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
