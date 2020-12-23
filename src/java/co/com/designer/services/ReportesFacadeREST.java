package co.com.designer.services;

//import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.kiosko.correo.EnvioCorreo;
import co.com.designer.kiosko.reportes.IniciarReporte;
import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.MediaType;
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

//    @PersistenceContext(unitName = "wsreportePU")
//    private EntityManager em;

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
            String rol = "ROLKIOSKO";
            String sqlQuery = "SET ROLE " + rol + " IDENTIFIED BY RLKSK ";
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            query.executeUpdate();
        } catch (Exception ex) {
            System.out.println("setearPerfil() Error: " + ex);
        }
    }

    @GET
    @Path("generaReporte/{reporte}/{id}/{enviocorreo}/{correo}")
    @Produces("application/pdf")
    public Response generaReporte(@PathParam("reporte") String reporte, @PathParam("id") BigDecimal id, 
            @PathParam("enviocorreo") boolean envioCorreo, @PathParam("correo") String correo, 
            @QueryParam("descripcionReporte") String descripcionReporte, 
            @QueryParam("codigoReporte") String codigoReporte, @QueryParam("nit") String nit,
            @QueryParam("cadena") String cadena) {
        System.out.println("generaReporte() codigo: "+codigoReporte+" nit: "+nit);
        //this.getEntityManager(cadena);
        setearPerfil();
        System.out.println("Parametros para generar reporte: [ reporte: "+reporte+ ", secuenciaEmpleado: "+id+
                ", descripcionReporte: "+descripcionReporte+ ", codigo: "+codigoReporte+" ]");
        Map parametros = new HashMap();
        parametros.put("secuenciaempleado", id);
//        String rutaGenerado = iniciarReporte.ejecutarReporte("kioCertificacionStrabag", "C:\\DesignerRHN\\Basico\\Reportes\\", "C:\\DesignerRHN\\Reportes\\ArchivosPlanos\\", "rep_2003122037.pdf", "PDF", parametros, getEntityManager());
        //String rutaGenerado = iniciarReporte.ejecutarReporte(reporte, "C:\\DesignerRHN\\Basico\\Reportes\\", "C:\\DesignerRHN\\Reportes\\ArchivosPlanos\\", "rep_2003122037.pdf", "PDF", parametros, getEntityManager());
        long tiempo=System.currentTimeMillis();
        String nombreReporte = reporte+"_"+id+"_"+tiempo+".pdf";
        System.out.println("nombreReporte:" + nombreReporte);
//        String rutaGenerado = iniciarReporte.ejecutarReporte(reporte, "C:\\DesignerRHN10\\Basico10\\reportesKiosko\\", "C:\\DesignerRHN10\\Reportes\\ArchivosPlanosKiosko\\", nombreReporte, "PDF", parametros, getEntityManager());
        //String rutaGenerado = iniciarReporte.ejecutarReporte(reporte, "C:\\DesignerRHN12\\Basico12\\ReportesKiosko\\", "C:\\DesignerRHN12\\Reportes\\ArchivosPlanosRHNPKKiosko\\", nombreReporte, "PDF", parametros, getEntityManager());
        String rutaGenerado = iniciarReporte.ejecutarReporte(reporte, getPathReportes(), getPathArchivosPlanos(), nombreReporte, "PDF", parametros, getEntityManager());
        File file = new File(rutaGenerado);
        System.out.println("Ruta generado: "+rutaGenerado);
        EnvioCorreo c= new EnvioCorreo();
        try {
            // setearPerfil();
            // valida si el reporte tiene auditoria
            BigDecimal retorno = null;
            String query1="select count(*) from kioconfigmodulos where codigoopcion=? and nitempresa=?";
            // Query query = getEntityManager(cadena).createNativeQuery(query1);
            System.out.println("Query: "+query1);
            Query query = getEntityManager().createNativeQuery(query1);
            query.setParameter(1, codigoReporte);
            query.setParameter(2, nit);
            retorno = (BigDecimal) query.getSingleResult();
            System.out.println("retorno: "+retorno);
            if (retorno.compareTo(BigDecimal.ZERO) > 0) {
                // si lleva auditoria
                System.out.println("Si lleva auditoria");
                String sqlQuery = "select email from kioconfigmodulos where codigoopcion=? and nitempresa=?";
                //Query query2 = getEntityManager(cadena).createNativeQuery(sqlQuery);
                System.out.println("Query2: "+sqlQuery);
                Query query2 = getEntityManager().createNativeQuery(sqlQuery);
                query2.setParameter(1, codigoReporte);
                query2.setParameter(2, nit);
                List lista = query2.getResultList();
                Iterator<String> it = lista.iterator();
                System.out.println("obtener "+lista.get(0));
                System.out.println("size: "+lista.size());
                while(it.hasNext()) {
                    String correoenviar = it.next();
                    System.out.println("correo auditoria: "+correoenviar);
                    //c.pruebaEnvio2("smtp.gmail.com","587","pruebaskiosco534@gmail.com","Nomina01", "S", correoenviar,
                  System.out.println("codigoopcion: "+codigoReporte);
                  c.pruebaEnvio2(getConfigCorreoServidorSMTP(nit),getConfigCorreo(nit, "PUERTO"),
                          getConfigCorreo(nit, "REMITENTE"), getConfigCorreo(nit, "CLAVE"), getConfigCorreo(nit, "AUTENTICADO"), correoenviar,
                  rutaGenerado, nombreReporte,
                  "Auditoria Reporte Kiosco - " + descripcionReporte, "Mensaje enviado automáticamente, por favor no responda a este correo.", getPathFoto()); 
                }
                
            } else {
                System.out.println("No lleva auditoria.");
            }
                
            System.out.println("nombreReporte recibido: "+reporte+" codigo: "+codigoReporte);
            /*if (reporte.equals("Kio_CertificaQue") || reporte.equals("kiodesigner01") || reporte.equals("kiodesigner02")) {
                setearPerfil();      
            }*/
            if (envioCorreo == true){
                System.out.println("Se debe enviar correo al empleado: "+correo);
            ConexionesKioskosFacadeREST ck = new ConexionesKioskosFacadeREST();
            // Enviar correo
            
             //c.pruebaEnvio2(getConfigCorreoServidorSMTP(nit),getConfigCorreo(nit, "PUERTO"),getConfigCorreo(nit, "REMITENTE"),getConfigCorreo(nit, "CLAVE"), getConfigCorreo(nit, "AUTENTICADO"), correo,
            c.pruebaEnvio2(getConfigCorreoServidorSMTP(nit),getConfigCorreo(nit, "PUERTO") ,getConfigCorreo(nit, "REMITENTE"),getConfigCorreo(nit, "CLAVE"), 
                    getConfigCorreo(nit, "AUTENTICADO"), correo,
                  rutaGenerado, nombreReporte,
                  "Reporte Kiosco - " + descripcionReporte, "Mensaje enviado automáticamente, por favor no responda a este correo.", getPathFoto());
            }
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        } finally {
            this.getEntityManager().close();
        }
        ResponseBuilder response = Response.ok((Object) file);
        Calendar fechaActual = Calendar.getInstance();
        String nomF = String.valueOf(fechaActual.get(Calendar.YEAR))+String.valueOf(fechaActual.get(Calendar.MONTH)+1)+String.valueOf(fechaActual.get(Calendar.DAY_OF_MONTH))
                +String.valueOf(fechaActual.get(Calendar.HOUR_OF_DAY))+String.valueOf(fechaActual.get(Calendar.MINUTE))+String.valueOf(fechaActual.get(Calendar.SECOND))
                +String.valueOf(fechaActual.get(Calendar.MILLISECOND));
        response.header("Content-Disposition", "attachment; filename="+nomF+".pdf");
        return response.build();
    }
    
    
    @GET
    @Path("generaFoto1/{documento}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFile1(@PathParam("documento") String documento) {
        System.out.println("getFile1() path: generaFoto1");
      //File file = new File("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\"+documento+".jpg");
      String rutaFoto=getPathFoto();
      File file = new File(rutaFoto+documento+".jpg");
        return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\""+file.getName()+"\"")
                .build();            
    }
    
   // @GET
   // @Path("generaFoto/{documento}")
   // @Produces("application/jpeg")
   // public Response getFile(@PathParam("documento") String documento) {
      //File file = new File("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\"+documento+".jpg");
    //  String rutaFoto=getPathFoto();
    //  File file = new File(rutaFoto+documento+".jpg");
    //    return Response.ok(file/*, /*MediaType.APPLICATION_OCTET_STREAM*/)
    //            .header("Content-Disposition", "attachment; filename=\""+file.getName()+"\"")
    //            .build();            
    //}
    
    @GET
    @Path("generaFoto/{documento}")
    @Consumes("application/jpeg")
    public Response recibeImagen(@PathParam("file") File file) {
       System.out.println("recibeImagen() path: generaFoto");
       //file = new File("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\"+file+".jpg");
       String rutaFoto = getPathFoto();
       file = new File(rutaFoto+file+".jpg");
        return Response.ok(file/*, /*MediaType.APPLICATION_OCTET_STREAM*/)
                .header("Content-Disposition", "attachment; filename=\""+file.getName()+"\"")
                .build();            
    }
    
    public String getPathFoto() {
        System.out.println("getPathFoto()");
        String rutaFoto="E:\\DesignerRHN10\\Basico10\\fotos_empleados\\";
        try {
            setearPerfil();
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            rutaFoto =  query.getSingleResult().toString();
            System.out.println("rutaFotos: "+rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathFoto: "+e.getMessage());
        }
        return rutaFoto;
    }
    
    public String getPathReportes() {
        System.out.println("getPathReportes()");
        String rutaFoto="E:\\DesignerRHN10\\Basico10\\Reportes\\kiosko\\";
        try {
            setearPerfil();
            String sqlQuery = "SELECT PATHREPORTES FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            rutaFoto =  query.getSingleResult().toString();
            System.out.println("rutaReportes: "+rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathReportes: "+e.getMessage());
        }
        return rutaFoto;
    }
    
    public String getPathArchivosPlanos() {
        System.out.println("getPathArchivosPlanos()");
        String rutaFoto="E:\\DesignerRHN10\\Reportes\\ArchivosPlanosRHNDSLkio\\";
        try {
            setearPerfil();
            String sqlQuery = "SELECT UBICAREPORTES FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: "+sqlQuery);
            Query query = getEntityManager().createNativeQuery(sqlQuery);
            rutaFoto =  query.getSingleResult().toString();
            System.out.println("rutaUbicaReportes: "+rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathArchivosPlanos: "+e.getMessage());
        }
        return rutaFoto;
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

}
