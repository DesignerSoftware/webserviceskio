/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.kiosko.generales;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;



/**
 *
 * @author thali
 */
public class EnvioCorreo {
    
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
            System.out.println("ex: " + ex);
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
            System.out.println("Error setearPerfil(esquema, cadenaPersistencia): " + ex);
        }
    }      
    
    public void pruebaEnvio2No(String servidorsmtp, String puerto, String remitente, String clave,
            String autenticado, String destinatario, String rutaReporte,
            String nombreReporte, String asunto, String mensaje){
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", servidorsmtp);
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port",puerto);
            props.setProperty("mail.smtp.user", remitente);
            props.setProperty("mail.smtp.auth", autenticado);
            
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(true);
            
            BodyPart texto = new MimeBodyPart();
            texto.setText(mensaje);
            
            BodyPart adjunto = new MimeBodyPart();
            adjunto.setDataHandler(new DataHandler(new FileDataSource(rutaReporte)));
            adjunto.setFileName(nombreReporte); // opcional
            
            // Juntar el texto y la imagen adjunta
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(adjunto);
            
            // Construir el mensaje de correo
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.setSubject(asunto);
            message.setContent(multiParte);
            
            //Enviar el correo
            Transport t = session.getTransport("smtp");
            t.connect(remitente, clave);
            t.sendMessage(message,message.getAllRecipients());
            System.out.println("Mail sent successfully!!! "+destinatario);
            t.close();
        } catch (MessagingException ex) {
            Logger.getLogger(EnvioCorreo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void pruebaEnvio(){
        Properties propiedad = new Properties();
      propiedad.setProperty("mail.smtp.host", "smtp.gmail.com");
      propiedad.setProperty("mail.smtp.starttls.enable", "true");
      propiedad.setProperty("mail.smtp.port", "587");
      propiedad.setProperty("mail.smtp.auth", "true");
      
      Session sesion = Session.getDefaultInstance(propiedad);
      String correoEnvia = "pruebaskiosco534@gmail.com";
      String contraseña = "Nomina01";
      String destinatario = "prueba@gmail.com";
      String asunto = "intento 2";
      String mensaje = "Mensaje prueba";
      
      MimeMessage mail = new MimeMessage(sesion);
        try { 
            mail.setFrom(new InternetAddress(correoEnvia));
            mail.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            mail.setSubject(asunto);
            mail.setText(mensaje);

            // esta parte es prueba
 //Mensaje que va en el correo
            BodyPart texto = new MimeBodyPart();
            texto.setText(mensaje);

            //Archivo adjunto
            BodyPart adjunto = null;
                adjunto = new MimeBodyPart();
                FileDataSource archivo = new FileDataSource("C:\\DesignerRHN10\\Reportes\\ArchivosPlanosKiosko\\rep_2003122037.pdf");
                adjunto.setDataHandler(new DataHandler(archivo));
                adjunto.setFileName(archivo.getFile().getName());
            

            //Estructura del contenido (Texto y Adjnto)
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);

            if (adjunto != null) {
                multiParte.addBodyPart(adjunto);
            }

            // Construimos la estructura del correo final
            MimeMessage message = new MimeMessage(mail);
            message.setFrom(new InternetAddress(correoEnvia)); //REMITENTE
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario)); //DESTINATARIO
            message.setSubject(asunto); //ASUNTO
            message.setContent(multiParte); //CONTENIDO

            /// fin prueba
            
            Transport transporte = sesion.getTransport("smtp");
            transporte.connect(correoEnvia, contraseña);
            transporte.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transporte.close();
            
        } catch (AddressException ex) {
            System.out.println("Error "+ex.getMessage());
        } catch (MessagingException ex) {
            System.out.println("Error: "+ex.getMessage());
        }
    }
    
    
   public void pruebaEnvio2(String servidorsmtp, String puerto, String remitente, String clave,
            String autenticado, String destinatario, String rutaReporte,
            String nombreReporte, String asunto, String mensaje, String rutaImagenes, String grupo, String urlKiosco) {

        //String servidorsmtp="smtp.designer.com.co"; 
        /*String servidorsmtp="smtp.gmail.com"; 
       String puerto="587"; */
        //String remitente="kioskodesigner@designer.com.co"; 
        /*String remitente="pruebaskiosco534@gmail.com"; 
       String clave="Nomina01";
       String autenticado="S"; 
       String destinatario="tmanrique@nomina.com.co"; 
       String rutaReporte="D:\\DesignerRHN10\\Reportes\\ArchivosPlanosKiosko\\EVAL-resumen_convocatoria_ANALISTA DE MESA DE AYUDA2020_12_2_11_14_51_943.pdf";
       String nombreReporte="b"; 
       String asunto="Un asunto"; 
       String nombreUsuario="THALIA MANRIQUE";
       Calendar dtGen = Calendar.getInstance();
       String mensaje=  "Nos permitimos informar que el "
                        + dtGen.get(Calendar.DAY_OF_MONTH) + "/" + (dtGen.get(Calendar.MONTH) + 1) + "/" + dtGen.get(Calendar.YEAR) + " a las " + dtGen.get(Calendar.HOUR_OF_DAY) + ":" + dtGen.get(Calendar.MINUTE)
                        + " se generó el reporte " + nombreReporte
                        + " en el módulo de Kiosco Nómina Designer. "
                        + "La persona que GENERÓ el reporte es: "+nombreUsuario;*/
 /*Properties propiedad = new Properties();
      propiedad.put("mail.smtp.host", servidorsmtp);
      propiedad.setProperty("mail.smtp.starttls.enable", "true");
      propiedad.setProperty("mail.smtp.port", puerto);
      propiedad.setProperty("mail.smtp.user", remitente);
      propiedad.setProperty("mail.smtp.auth", "true");*/
        //String rutaImagenes= "D:\\DesignerRHN10\\Basico10\\fotos_empleados\\";
        Properties propiedad = new Properties();
        propiedad.put("mail.smtp.host", servidorsmtp);
        propiedad.setProperty("mail.smtp.starttls.enable", "true");
        propiedad.setProperty("mail.smtp.port", puerto);
        propiedad.setProperty("mail.smtp.user", remitente);
        propiedad.setProperty("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");

        Session sesion = Session.getDefaultInstance(propiedad);

        Session session = Session.getInstance(propiedad,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

// This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
// first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText 
                    
                    = 
                    "<div style=\"background: #00223c;\">\n"
                    + "<div\n"
                    + "        style=\"padding:10%;color:white; background: #00223c;background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/imgcorreoreporte.jpg/);  min-height:100%;background-size:cover\" !important;>\n"
                    + "        <table style=\"max-width:90%;padding:10%;margin:0 auto;border-collapse:collapse\">\n"
                    + "            <tbody>\n"
                    + "                <tr >\n"
                    + "                    <td style=\"text-align:center;padding:0\">\n"
                    + "                        <div style=\"text-align:center\">\n"
                    + "                            <a style='color: white !important;' href="+urlKiosco+" target=\"_blank\"\n"
                    + "                                data-saferedirecturl="+urlKiosco+">\n"
                    + "\n"
                    + "                                <img width=\"80px\" height=\"80px\" style=\"display:block;margin:auto auto 0px auto\"\n"
                    + "                                    src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/kioscologopro.png\">\n"
                    //+ "                                    src=\"https://designer.ci2.co:8181/wsreporte/webresources/conexioneskioskos/obtenerLogo/LogoCI2Grande.png?nit=830056149&cadena=DEFAULT1\">\n"
                    + "                            </a>\n"
                    + "                        </div>\n"
                    + "                    </td>\n"
                    + "                </tr>\n"
                    + "\n"
                    + "                <tr style=\"padding-bottom:2%\">\n"
                    + "                    <td>\n"
                    + "                        <div style=\"margin:2% 4% 4% auto;text-align:justify;font-family:sans-serif\">\n"
                    + "                            <h2 style=\"margin:0 0 5px;text-align:center\">Estimado usuario(a):</h2>\n"
                    + "                            <br>\n"
                    + "                            <h4 style=\"margin:2px;text-align:center\">\n" + mensaje
                    + "                                </h4>\n"
                    + "                            <h4 style=\"text-align:center;margin-top:10px\">\n"
                    + "                                Este reporte se ha generado automáticamente desde Kiosco Nómina Designer.</h4>\n"
                    + "                            <h4>\n"
                    + "                            </h4>\n"
                    + "                            <h4 style=\"text-align:center;margin-top:10px\"> Revisa los archivos\n"
                    + "                                adjuntos.<br></h4>\n"
                    + "                            <div style=\"width:100%;text-align:center\">\n"
                    + "                                <a style=\"text-decoration:none;border-radius:5px;padding:11px 23px;margin-bottom:4%;color:white;background-color:#3498db\"\n"
                    + "                                    href="+urlKiosco+" target=\"_blank\"\n"
                    + "                                    data-saferedirecturl="+urlKiosco+">Ir\n"
                    + "                                    a Kiosco</a>\n"
                    + "                                <br>                                                                \n"
                    + "                                <br>                                                                \n"
                    + "                                    <ul style=\"width: 100%; height: 20px; text-align: center; padding: 10px 0 0 0 !important;\">\n"
                    + "                <li style=\"background: #3b5998; display:inline;\"><a href=\"https://www.facebook.com/nominads\" target=\"_blank\"\n"
                    + "                    > <img width=\"19px\" height=\"19px\" src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/21113922.png\" style=\"width: 19px; height: 19px;  \n"
                    + "                     color: #fff;\n"
                    + "                     background: #000;\n"
                    + "                     padding: 10px 15px;\n"
                    + "                     text-decoration: none;\n"
                    + "                     \n"
                    + "                     background: #3b5998;\"></a></li>"
                    + "                                        <li style=\"background: #00abf0; display:inline;\"><a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                                            class=\"icon-twitter\"> <img width=\"19px\" height=\"19px\" src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733635.png\" style=\"width: 19px; height: 19px;\n"
                    + "                                            color: #fff;\n"
                    + "                                            background: #000;\n"
                    + "                                            padding: 10px 15px;\n"
                    + "                                            text-decoration: none;\n"
                    + "                                            -webkit-transition: all 300ms ease;\n"
                    + "                                            -o-transition: all 300ms ease;\n"
                    + "                                            transition: all 300ms ease;\n"
                    + "                                            background: #00abf0;\"></a></li>\n"
                    + "                                        <li style=\"background: #0ad2ec; display:inline;\"><a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                                                class=\"icon-nomina\"> <img width=\"19px\" height=\"19px\" src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/3522533.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                                                color: #fff;\n"
                    + "                                                background: #000;\n"
                    + "                                                padding: 10px 15px;\n"
                    + "                                                text-decoration: none;\n"
                    + "                                                -webkit-transition: all 300ms ease;\n"
                    + "                                                -o-transition: all 300ms ease;\n"
                    + "                                                transition: all 300ms ease;\n"
                    + "                                                background: #0ad2ec;\"></a></li>\n"
                    + "                                        <li style=\"background: #ce1010; display:inline;\"><a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                                                    class=\"icon-youtube\"> <img width=\"19px\" height=\"19px\" src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733646.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                                                    color: #fff;\n"
                    + "                                                    background: #000;\n"
                    + "                                                    padding: 10px 15px;\n"
                    + "                                                    text-decoration: none;\n"
                    + "                                                    -webkit-transition: all 300ms ease;\n"
                    + "                                                    -o-transition: all 300ms ease;\n"
                    + "                                                    transition: all 300ms ease;\n"
                    + "                                                    background: #ce1010;\"></a></li>        \n"
                    + "                                       \n"
                    + "                                    </ul>\n"
                    + "                                \n"
                    + "                            </td>\n"
                    + "                        </tr>\n"
                    + "            </tbody>\n"
                    + "        </table>\n"
                    + "    </div>"
                    + " </div>"
                    /*+ " <br>"
                    + "  <p style=\"color: #55a532;\" >Imprimir en caso de ser estrictamente necesario, el medio ambiente se lo agradecerá. Ayudemos a evitar el calentamiento global.</p>\n" 
                    + " <br>"
                    + "   <p> Confidencialidad: CI2 S.A  es una empresa consiente de la importancia de la información en cuanto a su "
                    + "disponibilidad, Integridad y confidencialidad, por ello busca su protección a través de sus directrices de gestión "
                    + "de seguridad de la información. Los datos contenidos en  este correo electrónico de CI2 S.A  y en todos sus archivos "
                    + "anexos, es confidencial y/o privilegiada y sólo puede ser utilizada por la(s) persona(s) a la(s) cual(es) está dirigida. "
                    + "Si usted no es el destinatario autorizado, cualquier modificación, retención, difusión, distribución o copia total o "
                    + "parcial de este mensaje y/o de la información contenida en el mismo y/o en sus archivos anexos está prohibida y son "
                    + "sancionadas por la ley. Si por error recibe este mensaje, le ofrecemos disculpas, sírvase borrarlo de inmediato, "
                    + "notificarle de su error a la persona que lo envió y abstenerse de divulgar su contenido y anexos. </p>"*/
                    ;
            messageBodyPart.setContent(htmlText, "text/html");
// add it
            multipart.addBodyPart(messageBodyPart);
// second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreoKiosko.png");
            //String rutaImagenes = "C:\\DesignerRHN10\\Basico10\\fotos_empleados\\";
            DataSource fds = new FileDataSource(rutaImagenes + "imagencorreoreporte.jpg");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
// add image to the multipart
            multipart.addBodyPart(messageBodyPart);

            BodyPart adjunto = new MimeBodyPart();
            System.out.println("Ruta del reporte a enviar: " + rutaReporte);
            adjunto.setDataHandler(new DataHandler(new FileDataSource(rutaReporte)));
            adjunto.setFileName(nombreReporte); // opcional

            // Juntar el texto y la imagen adjunta
            multipart.addBodyPart(adjunto);

// put everything together
            message.setContent(multipart);
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!!");
            System.gc();
        } catch (Exception e) {
            System.out.println("No se ha enviado correo desde reprotes" + e.getMessage());
        }
    }

    public boolean enviarNuevaClave(String servidorsmtp, String puerto, String autenticado, String starttls, String remitente, String clave, String destinatario,
            String nombreUsuario, String nuevaClave, String urlKiosco, String rutaImagenes, String nitEmpresa, String cadena) {
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.port", puerto);
        final String user;
        final String password = "***********";
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Tu nueva clave de Kiosco!");

// This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
// first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText
                    = "<center>"
                    + "<img src=\'cid:image\'>"
                    + "<H1>¡Hola " + nombreUsuario + "!</H1>"
                    + "\n"
                    + "<p style='font-size: 20px'>Tu nueva contraseña de Kiosco es:<br><br>" + nuevaClave + "</p>"
                    + "<br><br><br><br>"
                    + "Este mensaje se generó de manera automática. Por favor no responda o escriba a esta cuenta.\n"
                    + "Si requiere apoyo con alguna duda, por favor comuníquese con el área de Talento Humano de su empresa."
                    + "</center>";
            messageBodyPart.setContent(htmlText, "text/html");
// add it
            multipart.addBodyPart(messageBodyPart);
// second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreoKiosko.png");
            //String rutaImagenes = getPathFoto(nitEmpresa, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreoKiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
// add image to the multipart
            multipart.addBodyPart(messageBodyPart);
// put everything together
            message.setContent(multipart);
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!! " + destinatario);
            envioCorreo = true;
        } catch (MessagingException e) {
            envioCorreo = false;
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    public String getPathFoto(String nitEmpresa, String cadena) {
        String rutaFoto = "";
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
        String rutaReportes = "";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHREPORTES FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaReportes = query.getSingleResult().toString();
            System.out.println("rutaFotos: " + rutaReportes);
        } catch (Exception e) {
            System.out.println("Error: getPathReportes(): " + e.getMessage());
        }
        return rutaReportes;
    }

    public boolean enviarEnlaceValidacionCuenta(String servidorsmtp, String puerto, String autenticado, String starttls, String remitente, String clave, String destinatario, String nombreUsuario, String seudonimo,
            String jwt, String urlKiosco, String nitEmpresa, String cadena) {
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.port", puerto);
        final String user;
        final String password = "***********";
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Bienvenido a Kiosco Designer - ¡Confirma tu cuenta ahora!");

// This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
// first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String urlValidacion = urlKiosco + "/#/validacionCuenta/" + jwt;
            String htmlText
                    = "<center>"
                    + "<img src=\'cid:image\'>"
                    + "<H1>¡Bienvenid@ " + nombreUsuario + "!</H1>"
                    + "\n"
                    + "<p style='font-size: 15px'>Recuerda que tu usuario para ingresar a Kiosco es: <b>" + seudonimo + "</b> </p>"
                    + "<p style='font-size: 20px'>Puedes confirmar tu cuenta a través del siguiente enlace:</p>"
                    + "\n"
                    + "<br>"
                    + "<a href='" + urlValidacion + "' style='width: 80px; border: 1px solid blue; padding: 10px; border-radius: 3px; text-decoration: none; color: black; font-size: 16px'>Confirmar mi cuenta</a>"
                    + "<br><br><br><br>"
                    + "Si el botón no funciona, copie y pegue en su navegador el siguiente enlace: " + urlValidacion
                    + "<br><br>"
                    + "Este mensaje se generó de manera automática. Por favor no responda o escriba a esta cuenta.\n"
                    + "Si requiere apoyo con alguna duda, por favor comuníquese con el área de Talento Humano de su empresa."
                    + "</center>";
            messageBodyPart.setContent(htmlText, "text/html");
// add it
            multipart.addBodyPart(messageBodyPart);
// second part (the image)
            messageBodyPart = new MimeBodyPart();
// DataSource fds = new FileDataSource("C:\\DesignerRHN10\\Basico10\\fotos_empleados\\headerlogocorreoKiosko.png");
            String rutaImagenes = getPathFoto(nitEmpresa, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreoKiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
// add image to the multipart
            multipart.addBodyPart(messageBodyPart);
// put everything together
            message.setContent(multipart);
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!! " + destinatario);
            envioCorreo = true;
        } catch (MessagingException e) {
            envioCorreo = false;
            throw new RuntimeException("no se pudo enviar correo: " + e);
        }
        return envioCorreo;
    }

    /* public boolean enviarCorreo(String cfc, String destinatario, String asunto, String mensaje, String pathAdjunto) {
//        try {
        boolean resEnvio = false;
        // Propiedades de la conexión
        Properties propiedadesConexion = new Properties();
        propiedadesConexion.setProperty("mail.smtp.host", "smtp.gmail.com"); //IP DEL SERVIDOR SMTP
        propiedadesConexion.setProperty("mail.smtp.port", "587");

        if ("S".equalsIgnoreCase("S")) {
            propiedadesConexion.setProperty("mail.smtp.auth", "true");
            if ("N".equalsIgnoreCase("S")) {
                propiedadesConexion.put("mail.smtp.socketFactory.port", "587");
                propiedadesConexion.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            } else if ("S".equalsIgnoreCase("S")) {
                propiedadesConexion.setProperty("mail.smtp.starttls.enable", "true");
            }
        }

        // Preparamos la sesion
        Session session = Session.getDefaultInstance(propiedadesConexion);
        /*Session session = Session.getDefaultInstance(propiedadesConexion, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(cfc.getRemitente(), cfc.getClave());
                }
            });
        try {
            //Mensaje que va en el correo
            /*BodyPart texto = new MimeBodyPart();
            texto.setText(mensaje);

            //Archivo adjunto
            BodyPart adjunto = null;
            if (pathAdjunto != null && !pathAdjunto.isEmpty()) {
                adjunto = new MimeBodyPart();
                FileDataSource archivo = new FileDataSource(pathAdjunto);
                adjunto.setDataHandler(new DataHandler(archivo));
                adjunto.setFileName(archivo.getFile().getName());
            }*/
    //Estructura del contenido (Texto y Adjnto)
    /*MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);

            if (adjunto != null) {
                multiParte.addBodyPart(adjunto);
            }

            // Construimos la estructura del correo final
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("pruebaskiosco534@gmail.com")); //REMITENTE
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario)); //DESTINATARIO
            message.setSubject(asunto); //ASUNTO
            //message.setContent(multiParte); //CONTENIDO

            //Preparamos la conexion con el servidor SMTP
            Transport t = session.getTransport("smtp");

            //Validamos si requiere autenticacion o no.
            if ("S".equalsIgnoreCase("S")) {
                t.connect("pruebaskiosco534@gmail.com", "Nomina01");
            } else {
                t.connect();
            }

            //Enviamos el mensaje
            t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));

            // Cierre de la conexion.
            t.close();

            System.out.println("CORREO ENVIADO EXITOSAMENTE");
//            return true;
            resEnvio = true;
        } catch (NoSuchProviderException nspe) {
            System.out.println("Error enviarCorreo: " + nspe.getMessage());
            resEnvio = false;
        } catch (MessagingException e) {
            System.out.println("Error enviarCorreo: " + e.getMessage());
            resEnvio = false;
        }
        return resEnvio;
    }
        
     */
 /*    public boolean enviarCorreoVacaciones(String servidorsmtp, String puerto, String autenticado, String starttls, String remitente, String clave, String destinatario, 
        String nombreUsuario, String asunto, String mensaje, String nit) {*/
 /*    public boolean enviarCorreoVacaciones(String destinatario,
        String asunto, String mensaje, String urlKiosco, String nit) {*/
    public boolean enviarCorreoVacaciones(String servidorsmtp, String puerto, String autenticado,
            String starttls, String remitente, String clave, String destinatario,
            String asunto, String mensaje, String urlKiosco, String nit, String cadena) {
        System.out.println("Parametros enviarCorreoVacaciones(): servidorsmto: " + servidorsmtp + ", puerto: " + puerto + ", autenticado: " + autenticado + ", starttls: " + starttls + ""
                + "\n remitente: " + remitente + ", clave: " + clave + ", destinatario: " + destinatario + ", asunto: " + asunto + ", nit: " + nit + ", cadena: " + cadena);
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.port", puerto);
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

// This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
// first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText
                    = "<style>"
                    + " a:link { color: white !important; } \n"
                    + "a:visited { color: white !important; } \n"
                    + "a:hover { background: yellow !important; } \n"
                    + "a:active { color: white !important; } "
                    + ".ii a[href] {\n"
                    + "    color: #ccc !important;\n"
                    + "} "
                    + "</style>"
                    + "<div style=\"background: #00223c;\">\n"
                    + "<div\n"
                    + "        style=\"padding:10%;color:white; background: #00223c;background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/imgcorreoreporte.jpg/);  min-height:100%;background-size:cover\">\n"
                    + "        <table style=\"max-width:90%;padding:10%;margin:0 auto;border-collapse:collapse\">\n"
                    + "            <tbody>\n"
                    + "                <tr >\n"
                    + "                    <td style=\"text-align:center;padding:0\">\n"
                    + "                        <div style=\"text-align:center\">\n"
                    + "                            <a href='" + urlKiosco + "' target=\"_blank\"\n"
                    + "                                data-saferedirecturl=" + urlKiosco + ">\n"
                    + "\n"
                    + "                                <img width=\"80px\"  height=\"80px\" style=\"display:block;margin:auto auto 0px auto\"\n"
                    + "                                    src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/kioscologopro.png\">\n"
                    //+ "                                    src=\"https://designer.ci2.co:8181/wsreporte/webresources/conexioneskioskos/obtenerLogo/LogoCI2Grande.png?nit=830056149&cadena=DEFAULT1\">\n"
                    + "                            </a>\n"
                    + "                        </div>\n"
                    + "                    </td>\n"
                    + "                </tr>\n"
                    + "\n"
                    + "                <tr style=\"padding-bottom:2%\">\n"
                    + "                    <td>\n"
                    + "                        <div style=\"margin:2% 4% 4% auto;text-align:justify;font-family:sans-serif\">\n"
                    + "                            <h2 style=\"margin:0 0 5px;text-align:center\">Estimado usuario(a):</h2>\n"
                    + "                            <br>\n"
                    + "                            <h4 style=\"margin:2px;text-align:center\">\n" + mensaje
                    + "                                </h4>\n"
                    + "                            <h4 style=\"text-align:center;margin-top:10px\">\n"
                    + "                                Este correo se ha enviado automáticamente desde Kiosco Nómina Designer.</h4>\n"
                    + "                            <h4>\n"
                    + "                            </h4>\n"
                    /*  + "                            <h4 style=\"color:#ffffff;text-align:center;margin-top:10px\"> Revisa los archivos\n"
                    + "                                adjuntos.<br></h4>\n"*/
                    + "                            <div style=\"width:100%;text-align:center\">\n"
                    + "                                <a style=\"text-decoration:none;border-radius:5px;padding:11px 23px;margin-bottom:4%;color:white;background-color:#3498db\"\n"
                    + "                                    href=" + urlKiosco + " target=\"_blank\"\n"
                    + "                                    data-saferedirecturl=" + urlKiosco + ">Ir\n"
                    + "                                    a Kiosco</a>\n"
                    + "                                <br>                                                                \n"
                    + "                                <br>                                                                \n"
                    + "                                    <ul style=\"width: 100%; height: 20px; text-align: center; padding: 10px 0 0 0 !important;\">\n"
                    + "                <li style=\"background: #3b5998; display:inline;\"><a href=\"https://www.facebook.com/nominads\" target=\"_blank\"\n"
                    + "                    > <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/21113922.png\" style=\"width: 19px; height: 19px;  \n"
                    + "                     color: #fff;\n"
                    + "                     background: #000;\n"
                    + "                     padding: 10px 15px;\n"
                    + "                     text-decoration: none;\n"
                    + "                     \n"
                    + "                     background: #3b5998;\"></a></li>"
                    + "                                        <li style=\"background: #00abf0; display:inline;\"><a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                                            class=\"icon-twitter\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733635.png\" style=\"width: 19px; height: 19px;\n"
                    + "                                            color: #fff;\n"
                    + "                                            background: #000;\n"
                    + "                                            padding: 10px 15px;\n"
                    + "                                            text-decoration: none;\n"
                    + "                                            -webkit-transition: all 300ms ease;\n"
                    + "                                            -o-transition: all 300ms ease;\n"
                    + "                                            transition: all 300ms ease;\n"
                    + "                                            background: #00abf0;\"></a></li>\n"
                    + "                                        <li style=\"background: #0ad2ec; display:inline;\"><a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                                                class=\"icon-nomina\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/3522533.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                                                color: #fff;\n"
                    + "                                                background: #000;\n"
                    + "                                                padding: 10px 15px;\n"
                    + "                                                text-decoration: none;\n"
                    + "                                                -webkit-transition: all 300ms ease;\n"
                    + "                                                -o-transition: all 300ms ease;\n"
                    + "                                                transition: all 300ms ease;\n"
                    + "                                                background: #0ad2ec;\"></a></li>\n"
                    + "                                        <li style=\"background: #ce1010; display:inline;\"><a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                                                    class=\"icon-youtube\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733646.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                                                    color: #fff;\n"
                    + "                                                    background: #000;\n"
                    + "                                                    padding: 10px 15px;\n"
                    + "                                                    text-decoration: none;\n"
                    + "                                                    -webkit-transition: all 300ms ease;\n"
                    + "                                                    -o-transition: all 300ms ease;\n"
                    + "                                                    transition: all 300ms ease;\n"
                    + "                                                    background: #ce1010;\"></a></li>        \n"
                    + "                                       \n"
                    + "                                    </ul>\n"
                    + "                                \n"
                    + "                            </td>\n"
                    + "                        </tr>\n"
                    + "            </tbody>\n"
                    + "        </table>\n"
                    + "    </div>"
                    + " </div>"
                    /*+ " <br>"
                    + "  <p style=\"color: #55a532;\" >Imprimir en caso de ser estrictamente necesario, el medio ambiente se lo agradecerá. Ayudemos a evitar el calentamiento global.</p>\n" 
                    + " <br>"
                    + "   <p> Confidencialidad: CI2 S.A  es una empresa consiente de la importancia de la información en cuanto a su "
                    + "disponibilidad, Integridad y confidencialidad, por ello busca su protección a través de sus directrices de gestión "
                    + "de seguridad de la información. Los datos contenidos en  este correo electrónico de CI2 S.A  y en todos sus archivos "
                    + "anexos, es confidencial y/o privilegiada y sólo puede ser utilizada por la(s) persona(s) a la(s) cual(es) está dirigida. "
                    + "Si usted no es el destinatario autorizado, cualquier modificación, retención, difusión, distribución o copia total o "
                    + "parcial de este mensaje y/o de la información contenida en el mismo y/o en sus archivos anexos está prohibida y son "
                    + "sancionadas por la ley. Si por error recibe este mensaje, le ofrecemos disculpas, sírvase borrarlo de inmediato, "
                    + "notificarle de su error a la persona que lo envió y abstenerse de divulgar su contenido y anexos. </p>"*/
                    ;
            messageBodyPart.setContent(htmlText, "text/html");
// add it
            multipart.addBodyPart(messageBodyPart);
// second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreoKiosko.png");
            String rutaImagenes = getPathFoto(nit, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreoKiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
// add image to the multipart
            multipart.addBodyPart(messageBodyPart);
// put everything together
            message.setContent(multipart);
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!! " + destinatario);
            envioCorreo = true;
        } catch (MessagingException e) {
            envioCorreo = false;
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    public boolean enviarCorreoAusentismos(String servidorsmtp, String puerto, String autenticado,
            String starttls, String remitente, String clave, String destinatario,
            String asunto, String mensaje, String nombreAnexo, String urlKiosco, String nit, String cadena) {
        System.out.println("Parametros enviarCorreoVacaciones(): servidorsmto: " + servidorsmtp + ", puerto: " + puerto + ", autenticado: " + autenticado + ", starttls: " + starttls + ""
                + "\n remitente: " + remitente + ", clave: " + clave + ", destinatario: " + destinatario + ", asunto: " + asunto + ", nit: " + nit + ", cadena: " + cadena);
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.port", puerto);
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

// This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
// first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText
                    = "<style>"
                    + " a:link { color: white !important; } \n"
                    + "a:visited { color: white !important; } \n"
                    + "a:hover { background: yellow !important; } \n"
                    + "a:active { color: white !important; } "
                    + ".ii a[href] {\n"
                    + "    color: #ccc !important;\n"
                    + "} "
                    + "</style>"
                    + "<div style=\"background: #00223c;\">\n"
                    + "<div\n"
                    + "        style=\"padding:10%;color:white; background: #00223c;background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/imgcorreoreporte.jpg/); min-height:100%;background-size:cover\">\n"
                    + "        <table style=\"max-width:90%;padding:10%;margin:0 auto;border-collapse:collapse\">\n"
                    + "            <tbody>\n"
                    + "                <tr >\n"
                    + "                    <td style=\"text-align:center;padding:0\">\n"
                    + "                        <div style=\"text-align:center\">\n"
                    + "                            <a href='" + urlKiosco + "' target=\"_blank\"\n"
                    + "                                data-saferedirecturl=" + urlKiosco + ">\n"
                    + "\n"
                    + "                                <img width=\"80px\" height=\"80px\" style=\"display:block;margin:auto auto 0px auto\"\n"
                    + "                                    src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/kioscologopro.png\">\n"
                    //+ "                                    src=\"https://designer.ci2.co:8181/wsreporte/webresources/conexioneskioskos/obtenerLogo/LogoCI2Grande.png?nit=830056149&cadena=DEFAULT1\">\n"
                    + "                            </a>\n"
                    + "                        </div>\n"
                    + "                    </td>\n"
                    + "                </tr>\n"
                    + "\n"
                    + "                <tr style=\"padding-bottom:2%\">\n"
                    + "                    <td>\n"
                    + "                        <div style=\"margin:2% 4% 4% auto;text-align:justify;font-family:sans-serif\">\n"
                    + "                            <h2 style=\"margin:0 0 5px;text-align:center\">Estimado usuario(a):</h2>\n"
                    + "                            <br>\n"
                    + "                            <h4 style=\"margin:2px;text-align:center\">\n" + mensaje
                    + "                                </h4>\n"
                    + ((nombreAnexo == null || nombreAnexo.equals("null")) ? "" : "<h4 style=\"text-align:center;margin-top:10px\">"
                    + "Adjunto a este correo encontrará el documento anexo a la novedad de ausentismo.")
                    + " </h4>\n"
                    + "                            <h4 style=\"text-align:center;margin-top:10px\">\n"
                    + "                                Este correo se ha enviado automáticamente desde Kiosco Nómina Designer.</h4>\n"
                    + "                            <h4>\n"
                    /*  + "                            <h4 style=\"color:#ffffff;text-align:center;margin-top:10px\"> Revisa los archivos\n"
                    + "                                adjuntos.<br></h4>\n"*/
                    + "                            <div style=\"width:100%;text-align:center\">\n"
                    + "                                <a style=\"text-decoration:none;border-radius:5px;padding:11px 23px;margin-bottom:4%;color:white;background-color:#3498db\"\n"
                    + "                                    href=" + urlKiosco + " target=\"_blank\"\n"
                    + "                                    data-saferedirecturl=" + urlKiosco + ">Ir\n"
                    + "                                    a Kiosco</a>\n"
                    + "                                <br>                                                                \n"
                    + "                                <br>                                                                \n"
                    + "                                    <ul style=\"width: 100%; height: 20px; text-align: center; padding: 10px 0 0 0 !important;\">\n"
                    + "                <li style=\"background: #3b5998; display:inline;\"><a href=\"https://www.facebook.com/nominads\" target=\"_blank\"\n"
                    + "                    > <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/21113922.png\" style=\"width: 19px; height: 19px;  \n"
                    + "                     color: #fff;\n"
                    + "                     background: #000;\n"
                    + "                     padding: 10px 15px;\n"
                    + "                     text-decoration: none;\n"
                    + "                     \n"
                    + "                     background: #3b5998;\"></a></li>"
                    + "                                        <li style=\"background: #00abf0; display:inline;\"><a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                                            class=\"icon-twitter\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733635.png\" style=\"width: 19px; height: 19px;\n"
                    + "                                            color: #fff;\n"
                    + "                                            background: #000;\n"
                    + "                                            padding: 10px 15px;\n"
                    + "                                            text-decoration: none;\n"
                    + "                                            -webkit-transition: all 300ms ease;\n"
                    + "                                            -o-transition: all 300ms ease;\n"
                    + "                                            transition: all 300ms ease;\n"
                    + "                                            background: #00abf0;\"></a></li>\n"
                    + "                                        <li style=\"background: #0ad2ec; display:inline;\"><a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                                                class=\"icon-nomina\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/3522533.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                                                color: #fff;\n"
                    + "                                                background: #000;\n"
                    + "                                                padding: 10px 15px;\n"
                    + "                                                text-decoration: none;\n"
                    + "                                                -webkit-transition: all 300ms ease;\n"
                    + "                                                -o-transition: all 300ms ease;\n"
                    + "                                                transition: all 300ms ease;\n"
                    + "                                                background: #0ad2ec;\"></a></li>\n"
                    + "                                        <li style=\"background: #ce1010; display:inline;\"><a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                                                    class=\"icon-youtube\"> <img width=\"19px\" height=\"19px\" src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733646.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                                                    color: #fff;\n"
                    + "                                                    background: #000;\n"
                    + "                                                    padding: 10px 15px;\n"
                    + "                                                    text-decoration: none;\n"
                    + "                                                    -webkit-transition: all 300ms ease;\n"
                    + "                                                    -o-transition: all 300ms ease;\n"
                    + "                                                    transition: all 300ms ease;\n"
                    + "                                                    background: #ce1010;\"></a></li>        \n"
                    + "                                       \n"
                    + "                                    </ul>\n"
                    + "                                \n"
                    + "                            </td>\n"
                    + "                        </tr>\n"
                    + "            </tbody>\n"
                    + "        </table>\n"
                    + "    </div>"
                    + "</div>"
                    /*+ " <br>"
                    + "  <p style=\"color: #55a532;\" >Imprimir en caso de ser estrictamente necesario, el medio ambiente se lo agradecerá. Ayudemos a evitar el calentamiento global.</p>\n" 
                    + " <br>"
                    + "   <p> Confidencialidad: CI2 S.A  es una empresa consiente de la importancia de la información en cuanto a su "
                    + "disponibilidad, Integridad y confidencialidad, por ello busca su protección a través de sus directrices de gestión "
                    + "de seguridad de la información. Los datos contenidos en  este correo electrónico de CI2 S.A  y en todos sus archivos "
                    + "anexos, es confidencial y/o privilegiada y sólo puede ser utilizada por la(s) persona(s) a la(s) cual(es) está dirigida. "
                    + "Si usted no es el destinatario autorizado, cualquier modificación, retención, difusión, distribución o copia total o "
                    + "parcial de este mensaje y/o de la información contenida en el mismo y/o en sus archivos anexos está prohibida y son "
                    + "sancionadas por la ley. Si por error recibe este mensaje, le ofrecemos disculpas, sírvase borrarlo de inmediato, "
                    + "notificarle de su error a la persona que lo envió y abstenerse de divulgar su contenido y anexos. </p>"*/
                    ;
            messageBodyPart.setContent(htmlText, "text/html");
// add it
            multipart.addBodyPart(messageBodyPart);
// second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreoKiosko.png");
            String rutaImagenes = getPathFoto(nit, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreoKiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            if (nombreAnexo != null) {
                String pathReportes = getPathReportes(nit, cadena);
                BodyPart adjunto = new MimeBodyPart();
                System.out.println("Ruta del reporte a enviar: " + pathReportes + "anexosAusentismos\\" + nombreAnexo);
                adjunto.setDataHandler(new DataHandler(new FileDataSource(pathReportes + "anexosAusentismos\\" + nombreAnexo)));
                adjunto.setFileName(nombreAnexo); // opcional
                multipart.addBodyPart(adjunto);
            }

// put everything together
            message.setContent(multipart);

// add image to the multipart
            multipart.addBodyPart(messageBodyPart);
// put everything together
            message.setContent(multipart);

            // prueba adjuntar archivo
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!! " + destinatario);
            envioCorreo = true;
        } catch (MessagingException e) {
            envioCorreo = false;
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    public String getConfigCorreo(String nitEmpresa, String valor, String cadena) {
        System.out.println("getPathArchivosPlanos()");
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
            System.out.println("Error: " + this.getClass().getName() + ".getConfigCorreo(): " + e.getMessage());
        }
        return servidorsmtp;
    }

    public String getConfigCorreoServidorSMTP(String nitEmpresa, String cadena) {
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
            System.out.println("Error: getConfigCorreoServidorSMTP: " + e.getMessage());
        }
        return servidorsmtp;
    }

    public String getCorreoSoporteKiosco(String nitEmpresa, String cadena) {
        System.out.println("getConfigCorreoServidorSMTP()");
        String emailSoporte = "";
        try {
            String esquema = getEsquema(nitEmpresa, cadena);
            setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EMAILCONTACTO FROM KIOPERSONALIZACIONES WHERE "
                    + "EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?) AND ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            emailSoporte = query.getSingleResult().toString();
            System.out.println("Email soporte: " + emailSoporte);
        } catch (Exception e) {
            System.out.println("Error: getCorreoSoporteKiosco: " + e.getMessage());
        }
        return emailSoporte;
    }

    /*Correo novedad de corrección de información que se envia a RRHH o Auditoria Módulo Vacaciones(No se incluye botón de Ir a Kiosco) */
    public boolean enviarCorreoInformativo(
            String asunto, String saludo, String mensaje, String nit, String urlKiosco, String cadena, String correoDestinatarioMain, String correoCC) {
        System.out.println("enviarCorreoInformativo()");
        String servidorsmtp = getConfigCorreoServidorSMTP(nit, cadena);
        String puerto = getConfigCorreo(nit, "PUERTO", cadena);
        String autenticado = getConfigCorreo(nit, "AUTENTICADO", cadena);
        String starttls = getConfigCorreo(nit, "STARTTLS", cadena);
        String remitente = getConfigCorreo(nit, "REMITENTE", cadena);
        String clave = getConfigCorreo(nit, "CLAVE", cadena);
        //String destinatario = getCorreoSoporteKiosco(nit, cadena);
        System.out.println("Datos enviarCorreoInformativo: " + servidorsmtp + ", puerto: " + puerto + ", autenticado: " + autenticado + ", starttls: " + starttls + ", remitente: " + remitente + ", clave: " + clave);
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.port", puerto);
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            //message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.addRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(correoDestinatarioMain));
            /*message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestinatarioMain));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("mateoc@nomina.com.co"));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("mateocoronadocardona@gmail.com"));*/
            // if (correoUsuario != null) {
            if (correoCC != null) {
                message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(correoCC));
            }
            message.setSubject(asunto);

// This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
// first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText
                    = "\"<style>\"\n"
                    + "                      \" a:link { color: white !important; } \n"
                    + "                      \"a:visited { color: white !important; }\n"
                    + "                      \"a:hover { background: yellow !important; } \n"
                    + "                      \"span.im { color: white !important; } \n"
                    + "                      \"a:active { color: white !important; } "
                    + "                      \".ii a[href] { "
                    + "                      \"    color: #ccc !important; \n"
                    + "                      \"} "
                    + "                      \"</style>\""
                    + "<div style=\"background: #00223c;\">\n"
                    + "<div\n"
                    + "        style=\"padding:10%;color:white ; background: #00223c; background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/imgcorreoreporte.jpg/); min-height:100%;background-size:cover\">\n"
                    + "        <table style=\"max-width:90%;padding:10%;margin:0 auto;border-collapse:collapse\">\n"
                    + "            <tbody>\n"
                    + "                <tr >\n"
                    + "                    <td style=\"text-align:center;padding:0\">\n"
                    + "                        <div style=\"text-align:center\">\n"
                    + "                            <a href='" + urlKiosco + "' target=\"_blank\"\n"
                    + "                                >\n"
                    + "\n"
                    + "                                <img width=\"80px\" height=\"80px\"  style=\"display:block;margin:auto auto 0px auto;\"\n"
                    + "                                    src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/kioscologopro.png\">\n"
                    //+ "                                    src=\"https://designer.ci2.co:8181/wsreporte/webresources/conexioneskioskos/obtenerLogo/LogoCI2Grande.png?nit=830056149&cadena=DEFAULT1\">\n"
                    + "                            </a>\n"
                    + "                        </div>\n"
                    + "                    </td>\n"
                    + "                </tr>\n"
                    + "\n"
                    + "                <tr style=\"padding-bottom:2%\">\n"
                    + "                    <td>\n"
                    + "                        <div style=\"margin:2% 4% 4% auto;text-align:justify;font-family:sans-serif\">\n"
                    //+ "                            <h2 style=\"color:#ffffff !important; margin:0 0 5px;text-align:center\">" + saludo + "</h2>\n"
                    + "                            <h2 style=\"margin:0 0 5px;text-align:center\">" + saludo + "</h2>\n"
                    + "                            <br><br>\n"
                    //+ "                            <h4 style=\"margin:2px;text-align:center\">\n" + mensaje
                    + "                            <h4 style=\"margin:2px;text-align:center\">\n" + mensaje + "</h4>\n"
                    + "                                <br><br> "
                    + "                            <h4 style=\"text-align:center;margin-top:10px\">\n"
                    + "                                Este correo se ha enviado automáticamente desde Kiosco Nómina Designer.</h4>\n"
                    + "                            <h4>\n"
                    + "                            </h4>\n"
                    /*  + "                            <h4 style=\"color:#ffffff;text-align:center;margin-top:10px\"> Revisa los archivos\n"
                    + "                                adjuntos.<br></h4>\n"*/
                    + "                            <div style=\"width:100%;text-align:center\">\n"
                    + "                                <br>                                                                \n"
                    + "                                <br>                                                                \n"
                    + "                                    <ul style=\"width: 100% !important; height: 20px !important; text-align: center !important; padding: 10px 0 0 0 !important;\">\n"
                    + "                <li style=\"background: #3b5998 !important; display:inline !important;\"><a href=\"https://www.facebook.com/nominads\" target=\"_blank\"\n"
                    + "                    > <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/21113922.png\" style=\"width: 19px !important; height: 19px !important;  \n"
                    + "                     color: #fff !important;\n"
                    + "                     background: #000 !important;\n"
                    + "                     padding: 10px 15px !important;\n"
                    + "                     text-decoration: none !important;\n"
                    + "                     \n"
                    + "                     background: #3b5998 !important;\"></a></li>"
                    + "                                        <li style=\"background: #00abf0 !important; display:inline !important;\"><a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                                            class=\"icon-twitter\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733635.png\" style=\"width: 19px !important; height: 19px !important;\n"
                    + "                                            color: #fff !important;\n"
                    + "                                            background: #000 !important;\n"
                    + "                                            padding: 10px 15px !important;\n"
                    + "                                            text-decoration: none !important;\n"
                    + "                                            -webkit-transition: all 300ms ease !important;\n"
                    + "                                            -o-transition: all 300ms ease !important;\n"
                    + "                                            transition: all 300ms ease !important;\n"
                    + "                                            background: #00abf0 !important;\"></a></li>\n"
                    + "                                        <li style=\"background: #0ad2ec !important; display:inline !important;\"><a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                                                class=\"icon-nomina\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/3522533.png\" style=\"width: 19px !important; height: 19px !important; display: inline-block !important;\n"
                    + "                                                color: #fff !important;\n"
                    + "                                                background: #000 !important;\n"
                    + "                                                padding: 10px 15px !important;\n"
                    + "                                                text-decoration: none !important;\n"
                    + "                                                -webkit-transition: all 300ms ease !important;\n"
                    + "                                                -o-transition: all 300ms ease !important;\n"
                    + "                                                transition: all 300ms ease !important;\n"
                    + "                                                background: #0ad2ec !important;\"></a></li>\n"
                    + "                                        <li style=\"background: #ce1010 !important; display:inline !important; \"><a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                                                    class=\"icon-youtube\"> <img width=\"19px\" height=\"19px\"  src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/733646.png\" style=\"width: 19px !important; height: 19px !important; display: inline-block !important;\n"
                    + "                                                    color: #fff !important;\n"
                    + "                                                    background: #000 !important;\n"
                    + "                                                    padding: 10px 15px !important;\n"
                    + "                                                    text-decoration: none !important;\n"
                    + "                                                    -webkit-transition: all 300ms ease !important;\n"
                    + "                                                    -o-transition: all 300ms ease !important;\n"
                    + "                                                    transition: all 300ms ease !important;\n"
                    + "                                                    background: #ce1010 !important;\"></a></li>        \n"
                    + "                                       \n"
                    + "                                    </ul>\n"
                    + "                                \n"
                    + "                            </td>\n"
                    + "                        </tr>\n"
                    + "            </tbody>\n"
                    + "        </table>\n"
                    + "    </div>"
                    + " </div>"
                    /*+ " <br>"
                    + "  <p style=\"color: #55a532;\" >Imprimir en caso de ser estrictamente necesario, el medio ambiente se lo agradecerá. Ayudemos a evitar el calentamiento global.</p>\n" 
                    + " <br>"
                    + "   <p> Confidencialidad: CI2 S.A  es una empresa consiente de la importancia de la información en cuanto a su "
                    + "disponibilidad, Integridad y confidencialidad, por ello busca su protección a través de sus directrices de gestión "
                    + "de seguridad de la información. Los datos contenidos en  este correo electrónico de CI2 S.A  y en todos sus archivos "
                    + "anexos, es confidencial y/o privilegiada y sólo puede ser utilizada por la(s) persona(s) a la(s) cual(es) está dirigida. "
                    + "Si usted no es el destinatario autorizado, cualquier modificación, retención, difusión, distribución o copia total o "
                    + "parcial de este mensaje y/o de la información contenida en el mismo y/o en sus archivos anexos está prohibida y son "
                    + "sancionadas por la ley. Si por error recibe este mensaje, le ofrecemos disculpas, sírvase borrarlo de inmediato, "
                    + "notificarle de su error a la persona que lo envió y abstenerse de divulgar su contenido y anexos. </p>"*/
                    ;
            messageBodyPart.setContent(htmlText, "text/html");
// add it
            multipart.addBodyPart(messageBodyPart);
// second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreoKiosko.png");
            String rutaImagenes = getPathFoto(nit, cadena);
            System.out.println("RutaImagenes: " + rutaImagenes + "headerlogocorreoKiosko.png");
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreoKiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
// add image to the multipart
            multipart.addBodyPart(messageBodyPart);
// put everything together
            message.setContent(multipart);
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!! To:" + correoDestinatarioMain);
            if (correoCC != null) {
                System.out.println("Mail sent successfully!!! Copia correo enviada a " + correoCC);
            }
            envioCorreo = true;
        } catch (MessagingException e) {
            envioCorreo = false;
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    public static void main(String[] args) {
        /*new EnvioCorreo().enviarCorreo("", "thalia.manrike@gmail.com", "Prueba 2", 
                "Esto es un correo de prueba", "");*/
        //new EnvioCorreo().pruebaEnvio3();
//       new EnvioCorreo().pruebaEnvio2();
        //new EnvioCorreo().pruebaEnvio();
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

}
