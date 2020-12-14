/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.kiosko.correo;

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
    
    public void pruebaEnvio2(String servidorsmtp, String puerto, String remitente, String clave,
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
      String destinatario = "thalia.manrike@gmail.com";
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
    
    
    public boolean enviarNuevaClave(String servidorsmtp, String puerto, String autenticado, String remitente, String clave, String destinatario, String nombreUsuario,
            String nuevaClave, String urlKiosco) {
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        props.put("mail.smtp.socketFactory.port", puerto);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
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
            String rutaImagenes = getPathFoto();
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreoKiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
// add image to the multipart
            multipart.addBodyPart(messageBodyPart);
// put everything together
            message.setContent(multipart);
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!!");
            envioCorreo = true;
        } catch (MessagingException e) {
            envioCorreo = false;
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }
    
    
    public String getPathFoto() {
        String rutaFoto="";
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
    
    
        public boolean enviarEnlaceValidacionCuenta(String servidorsmtp, String puerto, String autenticado, String remitente, String clave, String destinatario, String nombreUsuario,
            String jwt, String urlKiosco) {
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        props.put("mail.smtp.socketFactory.port", puerto);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
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
                    + "<H1>¡Bienvenido " + nombreUsuario + "!</H1>"
                    + "\n"
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
            String rutaImagenes = getPathFoto();
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreoKiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
// add image to the multipart
            multipart.addBodyPart(messageBodyPart);
// put everything together
            message.setContent(multipart);
// Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!!");
            envioCorreo = true;
        } catch (MessagingException e) {
            envioCorreo = false;
            throw new RuntimeException(e);
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
        
        
    public static void main(String[] args) {
        new EnvioCorreo().enviarCorreo("", "thalia.manrike@gmail.com", "Prueba 2", 
                "Esto es un correo de prueba", "");
    }*/
      
}
