package co.com.designer.kiosko.generales;

import co.com.designer.kiosko.entidades.ConfiCorreoKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConfiCorreoKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import com.sun.mail.util.MailConnectException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
//import javax.ejb.EJB;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.Query;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConfiCorreoKiosko;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author thali
 */
public class EnvioCorreo {

    private String urlKio = "https://www.nomina.com.co/images/images/kiosko/";

//    @EJB
    private IPersistenciaPerfiles rolesBD;

//    @EJB
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaConfiCorreoKiosko persisConfiCorreoKiosko;

    public EnvioCorreo() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    private void imprimirPropiedades(Properties propiedades) {
        String strPropiedades = "";
        for (String prop : propiedades.stringPropertyNames()) {
            strPropiedades = strPropiedades + prop + " : " + propiedades.get(prop) + "\n";
        }
        System.out.println("Propiedades: " + strPropiedades);
    }

    /**
     * Método para enviar un correo electrónico.
     *
     * @param servidorsmtp Dirección del servidor SMTP
     * @param puerto Puerto del servidor SMTP
     * @param remitente Cuenta del remitente del mensaje
     * @param clave Clave de la cuenta del remitente del mensaje
     * @param autenticado Marca para deterinar si el servidor usa autenticación
     * @param destinatario Cuenta del destinatario del mensaje de correo
     * @param rutaReporte Ruta del archivo de reportes generado, el cual debe
     * ser enviado
     * @param nombreReporte Nombre del archivo a adjuntar al mensaje
     * @param asunto Asunto que se le pondrá al correo electrónico.
     * @param mensaje Menaje que se lo pondrá a los correos electrónicos
     * @param rutaImagenes Ruta donde se publicarán las imágenes
     * @param grupo Grupo empresarial al que pertenece el cliente
     * @param urlKiosco URL donde se depliega el correo
     */
    public void pruebaEnvio2(String servidorsmtp, String puerto, String remitente, String clave,
            String autenticado, String destinatario, String rutaReporte,
            String nombreReporte, String asunto, String mensaje, String rutaImagenes, String grupo, String urlKiosco) {

        Properties propiedad = new Properties();
//        propiedad.setProperty("mail.transport.protocol", autenticado.equals("S") ? "smtps" : "smtp"); 
        propiedad.setProperty("mail.smtp.host", servidorsmtp);
        propiedad.setProperty("mail.smtp.port", puerto);
        propiedad.setProperty("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
//        propiedad.setProperty("mail.smtp.auth.login.disable", "true");
        //propiedad.setProperty("mail.smtp.ssl.enable", "true"); //para SSL
        //propiedad.setProperty("mail.smtp.ssl.protocols", "TLSv1.2"); // para SSL
        //the default authorization order is "LOGIN PLAIN DIGEST-MD5 NTLM". 'LOGIN' must be disabled since Email Delivery authorizes as 'PLAIN'
        propiedad.setProperty("mail.smtp.starttls.enable", "true"); //TLSv1.2 is required
        propiedad.setProperty("mail.smtp.starttls.required", "true");  //Oracle Cloud Infrastructure required
        //propiedad.setProperty("mail.smtp.ssl.trust", servidorsmtp); // para SSL
        propiedad.setProperty("mail.smtp.user", remitente);
        this.imprimirPropiedades(propiedad);

        Session session = Session.getDefaultInstance(propiedad);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

            // This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText
                    = "<div style=\"background: #00223c;\">\n"
                    + "  <div\n"
                    + "   style=\"padding:10%;color:white; background: #00223c; background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(" + urlKio + "imgcorreoreporte.jpg);  min-height:100%; background-size:cover\" !important;>\n"
                    + "    <table style=\"max-width:90%; padding:10%; margin:0 auto; border-collapse:collapse;\">\n"
                    + "      <tbody>\n"
                    + "        <tr>\n"
                    + "          <td style=\"text-align:center; padding: 0;\">\n"
                    + "            <div style=\"text-align:center\">\n"
                    + "              <a style='color: white !important;' href=" + urlKiosco + " target=\"_blank\"\n"
                    + "                 data-saferedirecturl=" + urlKiosco + ">\n"
                    + "                <img width=\"80px\" height=\"80px\" style=\"display:block;margin:auto auto 0px auto\"\n"
                    //+ "                     src=\"https://www.designer.com.co:8178/wsreporte/webresources/conexioneskioskos/obtenerFoto/kioscologopro.png\">\n"
                    + "                     src=\"" + urlKio + "kioscologopro.png\">\n"
                    //+ "                     src=\"https://designer.ci2.co:8181/wsreporte/webresources/conexioneskioskos/obtenerLogo/LogoCI2Grande.png?nit=830056149&cadena=DEFAULT1\">\n"
                    + "              </a>\n"
                    + "            </div>\n"
                    + "          </td>\n"
                    + "        </tr>\n"
                    + "        <tr style=\"padding-bottom:2%\">\n"
                    + "          <td>\n"
                    + "            <div style=\"margin:2% 4% 4% auto; text-align:justify; font-family:sans-serif;\">\n"
                    + "              <h2 style=\"margin:0 0 5px;text-align:center\">Estimado usuario(a):</h2>\n"
                    + "              <br>\n"
                    + "              <h4 style=\"margin:2px;text-align:center\">\n"
                    + mensaje
                    + "              </h4>\n"
                    + "              <h4 style=\"text-align:center; margin-top:10px;\">\n"
                    + "Este reporte se ha generado automáticamente desde Kiosco Nómina Designer."
                    + "              </h4>\n"
                    + "              <h4 style=\"text-align:center; margin-top:10px;\">"
                    + "Revisa los archivos adjuntos."
                    + "              </h4><br>\n"
                    + "              <div style=\"width:100%; text-align:center;\">\n"
                    + "                <a style=\"text-decoration:none; border-radius:5px; padding:11px 23px; margin-bottom:4%; color:white; background-color:#3498db;\"\n"
                    + "                   href=" + urlKiosco + " target=\"_blank\"\n"
                    + "                   data-saferedirecturl=" + urlKiosco + ">"
                    + "Ir a Kiosco\n"
                    + "                </a>\n"
                    + "                <br>\n"
                    + "                <br>\n"
                    + "                <ul style=\"width: 100%; height: 20px; text-align: center; padding: 10px 0 0 0 !important;\">\n"
                    + "                  <li style=\"background: #3b5998; display:inline;\">"
                    + "                    <a href=\"https://www.facebook.com/nominads\" target=\"_blank\">\n"
                    + "                      <img width=\"19px\" height=\"19px\" src=\"" + urlKio + "21113922.png\" style=\"width: 19px; height: 19px;  \n"
                    + "                           color: #fff;\n"
                    + "                           background: #000;\n"
                    + "                           padding: 10px 15px;\n"
                    + "                           text-decoration: none;\n"
                    + "                           background: #3b5998;\">\n"
                    + "                    </a>\n"
                    + "                  </li>\n"
                    + "                  <li style=\"background: #00abf0; display:inline;\">\n"
                    + "                    <a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                       class=\"icon-twitter\"> \n"
                    + "                      <img width=\"19px\" height=\"19px\" src=\"" + urlKio + "733635.png\" style=\"width: 19px; height: 19px;\n"
                    + "                           color: #fff;\n"
                    + "                           background: #000;\n"
                    + "                           padding: 10px 15px;\n"
                    + "                           text-decoration: none;\n"
                    + "                           -webkit-transition: all 300ms ease;\n"
                    + "                           -o-transition: all 300ms ease;\n"
                    + "                           transition: all 300ms ease;\n"
                    + "                           background: #00abf0;\">\n"
                    + "                    </a>\n"
                    + "                  </li>\n"
                    + "                  <li style=\"background: #0ad2ec; display:inline;\">\n"
                    + "                    <a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                       class=\"icon-nomina\"> \n"
                    + "                      <img width=\"19px\" height=\"19px\" src=\"" + urlKio + "3522533.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                           color: #fff;\n"
                    + "                           background: #000;\n"
                    + "                           padding: 10px 15px;\n"
                    + "                           text-decoration: none;\n"
                    + "                           -webkit-transition: all 300ms ease;\n"
                    + "                           -o-transition: all 300ms ease;\n"
                    + "                           transition: all 300ms ease;\n"
                    + "                           background: #0ad2ec;\">\n"
                    + "                    </a>\n"
                    + "                  </li>\n"
                    + "                  <li style=\"background: #ce1010; display:inline;\">\n"
                    + "                    <a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                       class=\"icon-youtube\"> \n"
                    + "                      <img width=\"19px\" height=\"19px\" src=\"" + urlKio + "733646.png\" \n"
                    + "                           style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                           color: #fff;\n"
                    + "                           background: #000;\n"
                    + "                           padding: 10px 15px;\n"
                    + "                           text-decoration: none;\n"
                    + "                           -webkit-transition: all 300ms ease;\n"
                    + "                           -o-transition: all 300ms ease;\n"
                    + "                           transition: all 300ms ease;\n"
                    + "                           background: #ce1010;\"> \n"
                    + "                    </a>\n"
                    + "                  </li> \n"
                    + "                </ul>\n"
                    + "              </div>\n"
                    + "            </div>\n"
                    + "          </td>\n"
                    + "        </tr>\n"
                    + "      </tbody>\n"
                    + "    </table>\n"
                    + "  </div>"
                    + "</div>";

            messageBodyPart.setContent(htmlText, "text/html");
// add it
            multipart.addBodyPart(messageBodyPart);
            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreokiosko.png");
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
            // Create a transport.
            Transport transport = session.getTransport();
            try {
                //Transport.send(message);
                System.out.println("Sending Email now...standby...");
                int intPort = Integer.parseInt(puerto);

                // Connect to OCI Email Delivery using the SMTP credentials specified.
                transport.connect(servidorsmtp,
                        intPort,
                        remitente,
                        clave);

                // Send email.
                transport.sendMessage(message, message.getAllRecipients());
                System.out.println("Mail sent successfully!!!");
            } catch (Exception ex) {
                System.out.println("El correo no fue enviado. Error message: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                // Close & terminate the connection.
                transport.close();
            }
            System.gc();
        } catch (Exception e) {
            System.out.println("No se ha enviado correo desde reprotes " + e.getMessage());
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
//        props.put("mail.smtp.ssl.trust", servidorsmtp);
        props.put("mail.smtp.port", puerto);
        final String user;
        final String password = "***********";
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
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
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreokiosko.png");
            //String rutaImagenes = getPathFoto(nitEmpresa, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreokiosko.png");
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    public String getPathFoto(String nitEmpresa, String cadena) {
        String rutaFoto = "";
        String esquema = "";
        try {
            esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("rutaFotos: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("Error: getPathFoto: " + e.getMessage());
            e.printStackTrace();
        }
        return rutaFoto;
    }

    public String getPathReportes(String nitEmpresa, String cadena) {
        String rutaReportes = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHREPORTES FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
        props.setProperty("mail.transport.protocol", autenticado.equals("S") ? "smtps" : "smtp");
        props.put("mail.smtp.host", servidorsmtp);
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.ssl.trust", servidorsmtp);
        props.put("mail.smtp.port", puerto);
//        final String user;
//        final String password = "***********";
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
            String urlValidacion = "";
            if (urlKiosco.endsWith("/")) {
                urlValidacion = urlKiosco + "#/validacionCuenta/" + jwt;
                //System.out.println("urlValidacion_1 ");
            } else {
                urlValidacion = urlKiosco + "/#/validacionCuenta/" + jwt;
                //System.out.println("urlValidacion_2 ");
            }
            //System.out.println("urlValidacion: "+urlValidacion);
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
            // DataSource fds = new FileDataSource("C:\\DesignerRHN10\\Basico10\\fotos_empleados\\headerlogocorreokiosko.png");
            String rutaImagenes = getPathFoto(nitEmpresa, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreokiosko.png");
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
            e.printStackTrace();
            throw new RuntimeException("no se pudo enviar correo: " + e);
        }
        return envioCorreo;
    }

    public boolean enviarCorreoVacaciones(String servidorsmtp, String puerto, String autenticado,
            String starttls, String remitente, String clave, String destinatario,
            String asunto, String mensaje, String urlKiosco, String nit, String cadena) {
        System.out.println("Parametros enviarCorreoVacaciones(): servidorsmto: " + servidorsmtp + ", puerto: " + puerto + ", autenticado: " + autenticado + ", starttls: " + starttls + ""
                + "\n remitente: " + remitente + ", clave: " + clave + ", destinatario: " + destinatario + ", asunto: " + asunto + ", nit: " + nit + ", cadena: " + cadena + ", urlKIOSKO: " + urlKiosco);
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        props.setProperty("mail.transport.protocol", autenticado.equals("S") ? "smtps" : "smtp");
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        //props.put("mail.smtp.ssl.trust", servidorsmtp);
        props.put("mail.smtp.port", puerto);
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
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
                    + " a:visited { color: white !important; } \n"
                    + " a:hover { background: yellow !important; } \n"
                    + " a:active { color: white !important; } "
                    + " .ii a[href] {\n"
                    + "   color: #ccc !important;\n"
                    + " } "
                    + "</style>"
                    + "<div style=\"background: #00223c; color: white;\">\n"
                    + "<div\n"
                    + " style=\"padding:10%; color:white; background: #00223c; background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(" + urlKio + "imgcorreoreporte.jpg);  min-height:100%; background-size:cover;\"> \n"
                    + "  <table style=\"max-width:90%; padding:10%; margin:0 auto; border-collapse:collapse; \">\n"
                    + "    <tbody>\n"
                    + "      <tr>\n"
                    + "        <td style=\"text-align:center; padding:0;\">\n"
                    + "          <div style=\"text-align:center;\">\n"
                    + "            <a href='" + urlKiosco + "' target=\"_blank\"\n"
                    + "               data-saferedirecturl=" + urlKiosco + ">\n"
                    + "              <img width=\"80px\"  height=\"80px;\" style=\"display:block; margin:auto auto 0px auto;\"\n"
                    + "                   src=\"" + urlKio + "kioscologopro.png\">\n"
                    //+ "                   src=\"https://designer.ci2.co:8181/wsreporte/webresources/conexioneskioskos/obtenerLogo/LogoCI2Grande.png?nit=830056149&cadena=DEFAULT1\">\n"
                    + "            </a>\n"
                    + "          </div>\n"
                    + "        </td>\n"
                    + "      </tr>\n"
                    + "      <tr style=\"padding-bottom:2%\">\n"
                    + "        <td>\n"
                    + "          <div style=\"margin:2% 4% 4% auto; text-align:justify; font-family:sans-serif;\">\n"
                    + "            <h2 style=\"margin:0 0 5px;text-align:center\">Estimado usuario(a):</h2>\n"
                    + "            <br>\n"
                    + "            <h4 style=\"margin:2px;text-align:center\">\n"
                    + mensaje
                    + "            </h4>\n"
                    + "            <h4 style=\"text-align:center; margin-top:10px;\">\n"
                    + "Este correo se ha enviado automáticamente desde Kiosco Nómina Designer.\n"
                    + "            </h4>\n"
                    /*+ "            <h4 style=\"color:#ffffff; text-align:center; margin-top: 10px;\">\n"
                    + "Revisa los archivos adjuntos.\n"
                    + "            </h4><br>\n"*/
                    + "            <div style=\"width:100%; text-align:center;\">\n"
                    + "              <a style=\"text-decoration:none; border-radius:5px; padding:11px 23px; margin-bottom:4%; color:white; background-color:#3498db;\"\n"
                    + "                 href=" + urlKiosco + " target=\"_blank\"\n"
                    + "                 data-saferedirecturl=" + urlKiosco + ">"
                    + "Ir a Kiosco\n"
                    + "              </a>\n"
                    + "              <br>\n"
                    + "              <br>\n"
                    + "              <ul style=\"width: 100%; height: 20px; text-align: center; padding: 10px 0 0 0 !important;\">\n"
                    + "              <li style=\"background: #3b5998; display:inline;\">"
                    + "                <a href=\"https://www.facebook.com/nominads\" target=\"_blank\"\n >"
                    + "                  <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "21113922.png\" style=\"width: 19px; height: 19px; \n"
                    + "                       color: #fff;\n"
                    + "                       background: #000;\n"
                    + "                       padding: 10px 15px;\n"
                    + "                       text-decoration: none;\n"
                    + "                       background: #3b5998;\">"
                    + "                </a>"
                    + "              </li>"
                    + "              <li style=\"background: #00abf0; display:inline;\">"
                    + "                <a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                   class=\"icon-twitter\"> "
                    + "                  <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "733635.png\" style=\"width: 19px; height: 19px;\n"
                    + "                       color: #fff;\n"
                    + "                       background: #000;\n"
                    + "                       padding: 10px 15px;\n"
                    + "                       text-decoration: none;\n"
                    + "                       -webkit-transition: all 300ms ease;\n"
                    + "                       -o-transition: all 300ms ease;\n"
                    + "                       transition: all 300ms ease;\n"
                    + "                       background: #00abf0;\">"
                    + "                </a>"
                    + "              </li>\n"
                    + "              <li style=\"background: #0ad2ec; display:inline;\">"
                    + "                <a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                   class=\"icon-nomina\"> "
                    + "                  <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "3522533.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                       color: #fff;\n"
                    + "                       background: #000;\n"
                    + "                       padding: 10px 15px;\n"
                    + "                       text-decoration: none;\n"
                    + "                       -webkit-transition: all 300ms ease;\n"
                    + "                       -o-transition: all 300ms ease;\n"
                    + "                       transition: all 300ms ease;\n"
                    + "                       background: #0ad2ec;\">"
                    + "                </a>"
                    + "              </li>\n"
                    + "              <li style=\"background: #ce1010; display:inline;\">"
                    + "                <a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                   class=\"icon-youtube\"> <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "733646.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                   color: #fff;\n"
                    + "                   background: #000;\n"
                    + "                   padding: 10px 15px;\n"
                    + "                   text-decoration: none;\n"
                    + "                   -webkit-transition: all 300ms ease;\n"
                    + "                   -o-transition: all 300ms ease;\n"
                    + "                   transition: all 300ms ease;\n"
                    + "                   background: #ce1010;\">"
                    + "                </a> \n"
                    + "              </li> \n"
                    + "            </ul>\n"
                    + "          </td>\n"
                    + "        </tr>\n"
                    + "      </tbody>\n"
                    + "    </table>\n"
                    + "  </div>"
                    + "</div>";
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);
            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreokiosko.png");
            String rutaImagenes = getPathFoto(nit, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreokiosko.png");
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    public boolean enviarCorreoAusentismos(String servidorsmtp, String puerto, String autenticado,
            String starttls, String remitente, String clave, String destinatario,
            String asunto, String mensaje, String nombreAnexo, String urlKiosco, String nit, String cadena) {
        System.out.println("Parametros enviarCorreoVacaciones(): servidorsmto: "
                + servidorsmtp + ", puerto: "
                + puerto + ", autenticado: "
                + autenticado + ", starttls: "
                + starttls + ""
                + "\n remitente: "
                + remitente
                + ", clave: "
                + clave + ", destinatario: "
                + destinatario + ", asunto: "
                + asunto + ", nit: "
                + nit + ", cadena: "
                + cadena);
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        props.setProperty("mail.transport.protocol", autenticado.equals("S") ? "smtps" : "smtp");
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
//        props.put("mail.smtp.ssl.trust", servidorsmtp);
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
                    + " a:visited { color: white !important; } \n"
                    + " a:hover { background: yellow !important; } \n"
                    + " a:active { color: white !important; } "
                    + " .ii a[href] {\n"
                    + "   color: #ccc !important;\n"
                    + " } "
                    + "</style>"
                    + "<div style=\"background: #00223c;\">\n"
                    + "  <div\n"
                    + "   style=\"padding:10%;color:white; background: #00223c;background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(" + urlKio + "imgcorreoreporte.jpg); min-height:100%;background-size:cover\">\n"
                    + "    <table style=\"max-width:90%;padding:10%;margin:0 auto;border-collapse:collapse\">\n"
                    + "      <tbody>\n"
                    + "        <tr>\n"
                    + "          <td style=\"text-align:center;padding:0\">\n"
                    + "            <div style=\"text-align:center\">\n"
                    + "              <a href='" + urlKiosco + "' target=\"_blank\"\n"
                    + "                  data-saferedirecturl=" + urlKiosco + ">\n"
                    + "                <img width=\"80px\" height=\"80px\" style=\"display:block;margin:auto auto 0px auto\"\n"
                    + "                      src=\"" + urlKio + "kioscologopro.png\">\n"
                    //+ "                      alt=\""+urlKio+"conexioneskioskos/obtenerFoto/kioscologopro.png\">\n"
                    //+ "                      src=\"https://designer.ci2.co:8181/wsreporte/webresources/conexioneskioskos/obtenerLogo/LogoCI2Grande.png?nit=830056149&cadena=DEFAULT1\">\n"
                    + "              </a>\n"
                    + "            </div>\n"
                    + "          </td>\n"
                    + "        </tr>\n"
                    + "        <tr style=\"padding-bottom:2%\">\n"
                    + "          <td>\n"
                    + "            <div style=\"margin:2% 4% 4% auto; text-align:justify; font-family:sans-serif;\">\n"
                    + "              <h2 style=\"margin:0 0 5px;text-align:center\">Estimado usuario(a):</h2>\n"
                    + "              <br>\n"
                    + "              <h4 style=\"margin:2px;text-align:center\">\n"
                    + mensaje
                    + "              </h4>\n"
                    + ((nombreAnexo == null || nombreAnexo.equals("null")) ? "" : "<h4 style=\"text-align:center;margin-top:10px\">"
                    + "Adjunto a este correo encontrará el documento anexo a la novedad de ausentismo.</h4>")
                    + "              <h4 style=\"text-align:center; margin-top:10px;\">\n"
                    + "Este correo se ha enviado automáticamente desde Kiosco Nómina Designer.\n"
                    + "              </h4>\n"
                    /*+ "              <h4 style=\"color:#ffffff; text-align:center; margin-top:10px;\"> "
                    + "Revisa los archivos adjuntos.\n"
                    + "              </h4><br>\n"*/
                    + "              <div style=\"width:100%; text-align:center;\">\n"
                    + "                <a style=\"text-decoration:none; border-radius:5px; padding:11px 23px; margin-bottom:4%; color:white; background-color:#3498db;\"\n"
                    + "                    href=" + urlKiosco + " target=\"_blank\"\n"
                    + "                    data-saferedirecturl=" + urlKiosco + ">"
                    + "Ir a Kiosco\n"
                    + "                </a>\n"
                    + "                <br>\n"
                    + "                <br>\n"
                    + "                <ul style=\"width: 100%; height: 20px; text-align: center; padding: 10px 0 0 0 !important;\">\n"
                    + "                  <li style=\"background: #3b5998; display:inline;\">"
                    + "                    <a href=\"https://www.facebook.com/nominads\" target=\"_blank\"> \n"
                    + "                       <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "21113922.png\" style=\"width: 19px; height: 19px;  \n"
                    + "                            color: #fff;\n"
                    + "                            background: #000;\n"
                    + "                            padding: 10px 15px;\n"
                    + "                            text-decoration: none;\n"
                    + "                            background: #3b5998;\">"
                    + "                    </a>"
                    + "                  </li>"
                    + "                  <li style=\"background: #00abf0; display:inline;\">"
                    + "                    <a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                        class=\"icon-twitter\"> <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "733635.png\" style=\"width: 19px; height: 19px;\n"
                    + "                        color: #fff;\n"
                    + "                        background: #000;\n"
                    + "                        padding: 10px 15px;\n"
                    + "                        text-decoration: none;\n"
                    + "                        -webkit-transition: all 300ms ease;\n"
                    + "                        -o-transition: all 300ms ease;\n"
                    + "                        transition: all 300ms ease;\n"
                    + "                        background: #00abf0;\">"
                    + "                    </a>"
                    + "                  </li>\n"
                    + "                  <li style=\"background: #0ad2ec; display:inline;\">"
                    + "                    <a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                        class=\"icon-nomina\"> "
                    + "                       <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "3522533.png\" style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                            color: #fff;\n"
                    + "                            background: #000;\n"
                    + "                            padding: 10px 15px;\n"
                    + "                            text-decoration: none;\n"
                    + "                            -webkit-transition: all 300ms ease;\n"
                    + "                            -o-transition: all 300ms ease;\n"
                    + "                            transition: all 300ms ease;\n"
                    + "                            background: #0ad2ec;\">"
                    + "                    </a>"
                    + "                  </li>\n"
                    + "                  <li style=\"background: #ce1010; display:inline;\">"
                    + "                    <a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                        class=\"icon-youtube\"> "
                    + "                      <img width=\"19px\" height=\"19px\" src=\"" + urlKio + "733646.png\" "
                    + "                           alt=\"" + urlKio + "733646.png\""
                    + "                           style=\"width: 19px; height: 19px; display: inline-block;\n"
                    + "                           color: #fff;\n"
                    + "                           background: #000;\n"
                    + "                           padding: 10px 15px;\n"
                    + "                           text-decoration: none;\n"
                    + "                           -webkit-transition: all 300ms ease;\n"
                    + "                           -o-transition: all 300ms ease;\n"
                    + "                           transition: all 300ms ease;\n"
                    + "                           background: #ce1010;\">"
                    + "                    </a>"
                    + "                  </li>\n"
                    + "                </ul>\n"
                    + "              </div>"
                    + "            </div>"
                    + "          </td>\n"
                    + "        </tr>\n"
                    + "      </tbody>\n"
                    + "    </table>\n"
                    + "   </div>"
                    + "</div>";
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
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);
            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreokiosko.png");
            String rutaImagenes = getPathFoto(nit, cadena);
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreokiosko.png");
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    public String getConfigCorreo(String nitEmpresa, String valor, String cadena) {
        System.out.println("getPathArchivosPlanos()");
        String servidorsmtp = "smtp.designer.com.co";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT " + valor + " FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT SERVIDORSMTP FROM CONFICORREOKIOSKO WHERE EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?)";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT EMAILCONTACTO FROM KIOPERSONALIZACIONES WHERE "
                    + "EMPRESA=(SELECT SECUENCIA FROM EMPRESAS WHERE NIT=?) "
                    + "AND TIPOCONTACTO='NOMINA' "
                    + "AND ROWNUM<=1 ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
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
        props.setProperty("mail.transport.protocol", autenticado.equals("S") ? "smtps" : "smtp");
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.ssl.trust", servidorsmtp);
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
                    + "        style=\"padding:10%;color:white ; background: #00223c; background-image:linear-gradient(rgba(3,20,64,1.0),rgba(0,0,0,0.5)),url(" + urlKio + "imgcorreoreporte.jpg); min-height:100%;background-size:cover\">\n"
                    + "        <table style=\"max-width:90%;padding:10%;margin:0 auto;border-collapse:collapse\">\n"
                    + "            <tbody>\n"
                    + "                <tr >\n"
                    + "                    <td style=\"text-align:center;padding:0\">\n"
                    + "                        <div style=\"text-align:center\">\n"
                    + "                            <a href='" + urlKiosco + "' target=\"_blank\"\n"
                    + "                                >\n"
                    + "\n"
                    + "                                <img width=\"80px\" height=\"80px\"  style=\"display:block;margin:auto auto 0px auto;\"\n"
                    + "                                    src=\"" + urlKio + "kioscologopro.png\">\n"
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
                    + "                    > <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "21113922.png\" style=\"width: 19px !important; height: 19px !important;  \n"
                    + "                     color: #fff !important;\n"
                    + "                     background: #000 !important;\n"
                    + "                     padding: 10px 15px !important;\n"
                    + "                     text-decoration: none !important;\n"
                    + "                     \n"
                    + "                     background: #3b5998 !important;\"></a></li>"
                    + "                                        <li style=\"background: #00abf0 !important; display:inline !important;\"><a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"\n"
                    + "                                            class=\"icon-twitter\"> <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "733635.png\" style=\"width: 19px !important; height: 19px !important;\n"
                    + "                                            color: #fff !important;\n"
                    + "                                            background: #000 !important;\n"
                    + "                                            padding: 10px 15px !important;\n"
                    + "                                            text-decoration: none !important;\n"
                    + "                                            -webkit-transition: all 300ms ease !important;\n"
                    + "                                            -o-transition: all 300ms ease !important;\n"
                    + "                                            transition: all 300ms ease !important;\n"
                    + "                                            background: #00abf0 !important;\"></a></li>\n"
                    + "                                        <li style=\"background: #0ad2ec !important; display:inline !important;\"><a href=\"https://www.nomina.com.co/\" target=\"_blank\"\n"
                    + "                                                class=\"icon-nomina\"> <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "3522533.png\" style=\"width: 19px !important; height: 19px !important; display: inline-block !important;\n"
                    + "                                                color: #fff !important;\n"
                    + "                                                background: #000 !important;\n"
                    + "                                                padding: 10px 15px !important;\n"
                    + "                                                text-decoration: none !important;\n"
                    + "                                                -webkit-transition: all 300ms ease !important;\n"
                    + "                                                -o-transition: all 300ms ease !important;\n"
                    + "                                                transition: all 300ms ease !important;\n"
                    + "                                                background: #0ad2ec !important;\"></a></li>\n"
                    + "                                        <li style=\"background: #ce1010 !important; display:inline !important; \"><a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"\n"
                    + "                                                    class=\"icon-youtube\"> <img width=\"19px\" height=\"19px\"  src=\"" + urlKio + "733646.png\" style=\"width: 19px !important; height: 19px !important; display: inline-block !important;\n"
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
                    + " </div>" /*+ " <br>"
                    + "  <p style=\"color: #55a532;\" >Imprimir en caso de ser estrictamente necesario, el medio ambiente se lo agradecerá. Ayudemos a evitar el calentamiento global.</p>\n" 
                    + " <br>"
                    + "   <p> Confidencialidad: CI2 S.A  es una empresa consiente de la importancia de la información en cuanto a su "
                    + "disponibilidad, Integridad y confidencialidad, por ello busca su protección a través de sus directrices de gestión "
                    + "de seguridad de la información. Los datos contenidos en  este correo electrónico de CI2 S.A  y en todos sus archivos "
                    + "anexos, es confidencial y/o privilegiada y sólo puede ser utilizada por la(s) persona(s) a la(s) cual(es) está dirigida. "
                    + "Si usted no es el destinatario autorizado, cualquier modificación, retención, difusión, distribución o copia total o "
                    + "parcial de este mensaje y/o de la información contenida en el mismo y/o en sus archivos anexos está prohibida y son "
                    + "sancionadas por la ley. Si por error recibe este mensaje, le ofrecemos disculpas, sírvase borrarlo de inmediato, "
                    + "notificarle de su error a la persona que lo envió y abstenerse de divulgar su contenido y anexos. </p>"*/;
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);
            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreokiosko.png");
            String rutaImagenes = getPathFoto(nit, cadena);
            System.out.println("RutaImagenes: " + rutaImagenes + "headerlogocorreokiosko.png");
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreokiosko.png");
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    /**
     * Correo novedad de corrección de información que se envia a RRHH o
     * Auditoria Módulo Vacaciones(No se incluye botón de Ir a Kiosco)
     *
     * @param asunto Asunto del mensaje de correo.
     * @param saludo Saludo del correo
     * @param mensaje Mensaje principal del correo
     * @param nit NIT de la empresa a la que pertence el empleado
     * @param cadena Cadena de conexión configurada para la empresa
     * @param correoDestinatarioMain Correo del destinatario
     * @param url URL en la que está configurada la empresa
     * @return
     */
    public boolean enviarCorreoComunicado(
            String asunto, String saludo, String mensaje, String nit, String cadena, String correoDestinatarioMain, String url) {
        System.out.println("enviarCorreoComunicado()");
        String servidorsmtp = getConfigCorreoServidorSMTP(nit, cadena);
        String puerto = getConfigCorreo(nit, "PUERTO", cadena);
        String autenticado = getConfigCorreo(nit, "AUTENTICADO", cadena);
        String starttls = getConfigCorreo(nit, "STARTTLS", cadena);
        String remitente = getConfigCorreo(nit, "REMITENTE", cadena);
        String clave = getConfigCorreo(nit, "CLAVE", cadena);
        //String destinatario = getCorreoSoporteKiosco(nit, cadena);
        System.out.println("Datos enviarCorreoComunicado: " + servidorsmtp + ", puerto: " + puerto + ", autenticado: " + autenticado + ", starttls: " + starttls + ", remitente: " + remitente + ", clave: " + clave);
        boolean envioCorreo = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", servidorsmtp);
        props.setProperty("mail.transport.protocol", autenticado.equals("S") ? "smtps" : "smtp");
        //props.put("mail.smtp.socketFactory.port", puerto);
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // props.put("mail.smtp.auth", autenticado == "S" ? "true" : "false");
        props.put("mail.smtp.auth", autenticado.equals("S") ? "true" : "false");
        props.put("mail.smtp.starttls.enable", starttls.equals("S") ? "true" : "false");
        props.put("mail.smtp.ssl.trust", servidorsmtp);
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
            message.setSubject(asunto);
            // This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String font = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css";
            String css = "https://www.nomina.com.co/images/images/kiosko/estilo_correo.css";
            String htmlText
                    = "<div style=\"background-color: rgba(237, 237, 237, 0.8);\">\n"
                    + "   <div style=\"padding: 10px 0px;\">"
                    + "      <div style=\"background-color: white;margin: 0 auto; width: 700px;\">\n"
                    + "         <div\n"
                    + "            style=\"margin: 0px auto;text-align: center;font-family: sans-serif;  justify-content: flex-start; padding-top: 20px; font-size: 18px;\">\n"
                    + "            <!-- <h3 >NÓMINA DESIGNER</h3> -->\n"
                    + "            <img src=\"https://www.nomina.com.co/images/images/kiosko/LOGO.png\" alt=\"\" style=\"width: 250px;\">\n"
                    + "            <br>\n"
                    + "            <img src=\"https://www.nomina.com.co/images/images/logoKiosco.png\" alt=\"\" style=\"padding: 0px 0px 0px 0px; width: 700px;\">"
                    + "            <div style=\"margin: 0 auto;width: 550px;padding: 5px;\">\n"
                    + "               <h3 style=\"color: #00223c;\">" + saludo + " </h3>\n"
                    + "               <p>" + mensaje + " </b>\n"
                    + "                   Este reporte se ha generado automáticamente desde Kiosco Nómina Designer.\n"
                    + "                 Revisa los archivos adjuntos.</p>\n"
                    + "                <h4>Revisa los archivos adjuntos</h4>\n"
                    + "                <br>\n"
                    + "                <div>\n"
                    + "                   <a style=\"text-decoration:none;border-radius:5px;padding:10px 48px; color:white;background-color:#3498db;\"\n"
                    + "                    href=" + url + " target=\"_blank\" data-saferedirecturl=" + url + "> Ir a Kiosco </a>\n"
                    + "\n"
                    + "                <!-- <a style=\"text-decoration:none;border-radius:5px;margin:11px 23px;color:white;background-color:#3498db\"\n"
                    + "                     href=\"+urlKiosco+\" target=\"_blank\" data-saferedirecturl=\"+urlKiosco+\">Ir a Kiosco</a> -->\n"
                    + "              </div>\n"
                    + "               <br>\n"
                    + "             </div>\n"
                    + "             <hr>\n"
                    + "             <a href=\"https://nomina.com.co/\" target=\"_blank\"><img src=\"https://www.nomina.com.co/images/images/kiosko/LOGO_N.png\" style=\"width: 60px;\" alt=\"\"></a>\n"
                    + "             <div style=\"padding: 10px 0px 0px 0px;color: #00223c;\">\n"
                    + "               <ul style=\"padding: 0px;margin: 0px;\">\n"
                    + "                 <li style=\"display:inline;\"><a href=\"https://www.facebook.com/nominads\" target=\"_blank\">\n"
                    + "                   <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_face.png\" style=\"color: #00223c;padding: 0px 10px;width: 40px;\" alt=\"\"></a>\n"
                    + "                 </li>\n"
                    + "                  <li style=\"display:inline;\"><a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\">\n"
                    + "                    <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_twitee.png\" style=\"color: #00223c;padding: 0px 10px;width: 40px;\" alt=\"\"></a>\n"
                    + "                 </li>\n"
                    + "                <li style=\"display:inline;\"><a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"> \n"
                    + "                   <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_youtube.png\" style=\"color: #00223c;padding: 0px 10px;width: 40px;\" alt=\"\"></a>\n"
                    + "                </li>\n"
                    + "                <li style=\"display:inline;\"><a href=\"https://www.instagram.com/nomina_designer\" target=\"_blank\"> \n"
                    + "                   <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_insta.png\" style=\"color: #00223c;padding: 0px 10px;width: 40px;\" alt=\"\"></a>\n"
                    + "                 </li>\n"
                    + "               </ul>\n"
                    + "           </div>\n"
                    + "             <p style=\"font-size: 0.8rem;padding: 10px 0px;margin: 0px;\">Este reporte se ha generado automáticamente desde Kiosco Nómina Designer.</p>\n"
                    + "          <!-- <img src=\"https://www.nomina.com.co/images/images/logoCorreoHeader.png\" alt=\"\" style=\"padding: 0px 0px;\"> -->\n"
                    + "         </div>\n"
                    + "    </div>"
                    + "  </div>"
                    + "</div>" /*+ " <br>"
                    + "  <p style=\"color: #55a532;\" >Imprimir en caso de ser estrictamente necesario, el medio ambiente se lo agradecerá. Ayudemos a evitar el calentamiento global.</p>\n" 
                    + " <br>"
                    + "   <p> Confidencialidad: CI2 S.A  es una empresa consiente de la importancia de la información en cuanto a su "
                    + "disponibilidad, Integridad y confidencialidad, por ello busca su protección a través de sus directrices de gestión "
                    + "de seguridad de la información. Los datos contenidos en  este correo electrónico de CI2 S.A  y en todos sus archivos "
                    + "anexos, es confidencial y/o privilegiada y sólo puede ser utilizada por la(s) persona(s) a la(s) cual(es) está dirigida. "
                    + "Si usted no es el destinatario autorizado, cualquier modificación, retención, difusión, distribución o copia total o "
                    + "parcial de este mensaje y/o de la información contenida en el mismo y/o en sus archivos anexos está prohibida y son "
                    + "sancionadas por la ley. Si por error recibe este mensaje, le ofrecemos disculpas, sírvase borrarlo de inmediato, "
                    + "notificarle de su error a la persona que lo envió y abstenerse de divulgar su contenido y anexos. </p>"*/;
            messageBodyPart.setContent(htmlText, "text/html; charset=utf-8");
            // add it
            multipart.addBodyPart(messageBodyPart);
            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            //DataSource fds = new FileDataSource("C:\\DesignerRHN12\\Basico12\\fotos_empleados\\headerlogocorreokiosko.png");
            String rutaImagenes = getPathFoto(nit, cadena);
            System.out.println("RutaImagenes: " + rutaImagenes + "headerlogocorreokiosko.png");
            DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreokiosko.png");
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
            // add image to the multipart
            multipart.addBodyPart(messageBodyPart);
            // put everything together
            message.setContent(multipart);
            // Send the actual HTML message, as big as you like
            Transport.send(message);
            System.out.println("Mail sent successfully!!! To:" + correoDestinatarioMain);
            envioCorreo = true;
        } catch (SendFailedException e) {
            System.out.println("No recipient addresses: No hay correos para enviar ");
            e.printStackTrace();
            envioCorreo = false;
        } catch (MailConnectException e) {
            System.out.println("No se puedo establecer conexión con el servidor de correo " + e);
            e.printStackTrace();
            envioCorreo = false;
        } catch (MessagingException e) {
            envioCorreo = false;
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return envioCorreo;
    }

    /**
     * Método para enviar mensaje por correo usando la configuración del
     * servidor de correo almacenada en la base de datos para la empresa
     * enviada.
     *
     * @param nit NIT de la empresa a la que pertenece el usuario.
     * @param cadena Nombre de la cadena de conexión configurada.
     * @param asunto Asunto del correo electrónico.
     * @param mensaje Mensaje del correo electrónico en formato HTML con
     * codificación UTF-8
     * @param destinatarios Lista de destinatarios con destino directo.
     * @param rutaImagenes Ruta donde se encuenta la imagen del encabezado.
     * @return Respuesta del envío del correo electrónico.
     */
    public boolean enviarCorreo(String nit, String cadena, String asunto, String mensaje, List destinatarios, String rutaImagenes) throws Exception {
        this.persisConfiCorreoKiosko = new PersistenciaConfiCorreoKiosko();
        try {
            ConfiCorreoKiosko cck = this.persisConfiCorreoKiosko.obtenerServidorCorreo(nit, cadena);

            Properties props = new Properties();
            props.put("mail.smtp.host", cck.getServidorSMTP());
            props.put("mail.smtp.port", cck.getPuerto());
            if (cck.getAutenticado().equals("S")) {
                props.setProperty("mail.transport.protocol", "smtps");
                props.put("mail.smtp.auth", "true");
                if (cck.getStartTLS().equals("S")) {
                    props.put("mail.smtp.starttls.enable", "true");
                }
                if (cck.getUsarSSL().equals("S")) {
                    props.put("mail.smtp.ssl.trust", cck.getServidorSMTP());
                }
            } else {
                props.setProperty("mail.transport.protocol", "smtp");
                props.put("mail.smtp.auth", "false");
            }

            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(cck.getRemitente(), cck.getClave());
                }
            });
            boolean envioCorreo = false;
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(cck.getRemitente()));
                Iterator it = destinatarios.iterator();
                while (it.hasNext()) {
                    String destinatario = (String) it.next();
                    System.out.println("destinatario: "+destinatario);
                    message.addRecipients(Message.RecipientType.TO,
                            InternetAddress.parse( destinatario ));
                }
                message.setSubject(asunto);
                // This mail has 2 part, the BODY and the embedded image
                MimeMultipart multipart = new MimeMultipart("related");
                // first part (the html)
                BodyPart messageBodyPart = new MimeBodyPart();

                messageBodyPart.setContent(mensaje, "text/html; charset=utf-8");
                // add it
                multipart.addBodyPart(messageBodyPart);
                // second part (the image)
//                messageBodyPart = new MimeBodyPart();

//                DataSource fds = new FileDataSource(rutaImagenes + "headerlogocorreokiosko.png");
//                messageBodyPart.setDataHandler(new DataHandler(fds));
//                messageBodyPart.setHeader("Content-ID", "<image>");
                // add image to the multipart
//                multipart.addBodyPart(messageBodyPart);
                // put everything together
                message.setContent(multipart);
                // Send the actual HTML message, as big as you like
                Transport.send(message);
                System.out.println("Mail sent successfully!!! ");
                envioCorreo = true;
            } catch (SendFailedException e) {
                System.out.println("No recipient addresses: No hay correos para enviar ");

                envioCorreo = false;
                throw e;
            } catch (MailConnectException e) {
                System.out.print("No se puedo establecer conexión con el servidor de correo ");
                System.out.print(" Mensaje: " + e.getMessage());
                System.out.print(" Host: " + e.getHost());
                envioCorreo = false;
                throw e;
            } catch (MessagingException e) {
                System.out.println("No se pudo enviar ");
                envioCorreo = false;
                throw e;
            }
            return envioCorreo;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
