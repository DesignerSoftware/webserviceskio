package co.com.designer.kiosko.generales;

import co.com.designer.kiosko.entidades.ConfiCorreoKiosko;
import co.com.designer.kiosko.excepciones.EMailExcepcion;
import co.com.designer.persistencia.implementacion.PersistenciaConfiCorreoKiosko;
import com.sun.mail.util.MailConnectException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Thalia Manrique
 * @author Edwin Hastamorir
 */
public class EnvioCorreo {

    private PersistenciaConfiCorreoKiosko persisConfiCorreoKiosko;
    private ConfiCorreoKiosko cck;

    public EnvioCorreo() {

    }

    public boolean enviarCorreo(Properties propiedades, String asunto, String mensaje,
            String destPrincipal,
            List<String> destinatarios, List<ArchivoCorreo> adjuntos) throws Exception {

        boolean envioCorreo = false;
        Session session = null;
        Transport transport = null;
        try {
            // Crea la instancia encargada de gestionar la sesion del usuario.

            if (cck.getAutenticado().equals("S")) {
                // Si requiere autenticacion, crea un objeto Authenticator
                session = Session.getInstance(propiedades,
                        new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(cck.getRemitente(), cck.getClave());
                    }
                });
            } else {
                // Si no requiere autenticacion, crea una sesion sin autenticacion
                session = Session.getInstance(propiedades, null);
            }
            // Crea un manejador del mensaje
            Message message = new MimeMessage(session);
            // Asigna el remitente (quien envia el mensaje)
            message.setFrom(new InternetAddress(cck.getRemitente()));
            // Indica quien es el destinatario principal
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destPrincipal));
            // Adiciona los destinatarios con copia 
            Iterator it1 = destinatarios.iterator();
            while (it1.hasNext()) {
                String destinatario = (String) it1.next();
                System.out.println("destinatario: " + destinatario);
                message.addRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(destinatario));
            }
            // Indica el asunto del correo
            message.setSubject(asunto);

            message.setSentDate(Calendar.getInstance().getTime());
            InternetAddress[] ia = new InternetAddress[1];
            ia[0] = new InternetAddress(cck.getRemitente());
            message.setReplyTo(ia);

//            Multipart mpart = new MimeMultipart("related");
            Multipart mpart = new MimeMultipart("mixed");

            BodyPart mBodyPart = new MimeBodyPart();
            mBodyPart.setContent(mensaje, "text/html; charset=utf-8");
            mpart.addBodyPart(mBodyPart);

            Iterator it2 = adjuntos.iterator();
            while (it2.hasNext()) {
                ArchivoCorreo adjunto = (ArchivoCorreo) it2.next();
                FileDataSource fds = new FileDataSource(adjunto.getRuta() + adjunto.getNombre());
                BodyPart maBodyPart = new MimeBodyPart();
                maBodyPart.setDataHandler(new DataHandler(fds));
                if (adjunto.getTipo().equalsIgnoreCase("imagen")) {
                    maBodyPart.addHeader("Content-ID", adjunto.getTipoMimeContenido());
                }
                if (adjunto.getTipo().equalsIgnoreCase("archivo")) {
                    if ("analisis-dafo.pdf".toLowerCase().contains(".pdf")) {
                        maBodyPart.addHeader("Content-type", adjunto.getTipoMimeContenido());
                    }
                }

                maBodyPart.addHeader("Content-Transfer-Encoding", "base64");
                maBodyPart.setFileName(adjunto.getNombre());
                mpart.addBodyPart(maBodyPart);
            }

            message.setContent(mpart);
            message.saveChanges();

            // Instancia que define los parámetros del protocolo de transporte.
            transport = session.getTransport();
            transport.addConnectionListener(new EscuchaTransporte());
//            transport.addTransportListener(this);
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Hilo interrumpido");
                e.printStackTrace();
            }
            String resulEnvio = "Correo electrónico enviado exitosamente desde "
                    + cck.getRemitente()
                    + " a "
                    + destPrincipal;
            if (!destinatarios.isEmpty()) {
                resulEnvio = resulEnvio + " con copia a otros correos";
            }
            System.out.println(resulEnvio);
            envioCorreo = true;
        } catch (MailConnectException mex) {
            // Le da suficiente tiempo a EventQueue para lanzar sus eventos.
            try {
                Thread.sleep(5);
            } catch (InterruptedException ie) {
            }

            System.out.println(" Mensaje: " + mex.getMessage());
            System.out.println(" Host: " + mex.getHost());
            envioCorreo = false;
            String eme = "";

            Exception ex = mex;
            do {
                if (ex instanceof SendFailedException) {
                    SendFailedException sfex = (SendFailedException) ex;
                    Address[] invalid = sfex.getInvalidAddresses();
                    eme = "Direccion de correo incorrecta.";
                    if (invalid != null) {
                        System.out.println("Direccion de correo incorrecta");
                        for (int i = 0; i < invalid.length; i++) {
                            System.out.println("         " + invalid[i]);
                        }
                    }
                    Address[] validUnsent = sfex.getValidUnsentAddresses();
                    if (validUnsent != null) {
                        System.out.println("Direcciones válidas no enviadas");
                        for (int i = 0; i < validUnsent.length; i++) {
                            System.out.println("         " + validUnsent[i]);
                        }
                    }
                    Address[] validSent = sfex.getValidSentAddresses();
                    if (validSent != null) {
                        System.out.println("Direcciones válidas enviadas");
                        for (int i = 0; i < validSent.length; i++) {
                            System.out.println("         " + validSent[i]);
                        }
                    }
                }
                System.out.println();
                if (ex instanceof MessagingException) {
                    ex = ((MessagingException) ex).getNextException();
                }
                if (ex instanceof UnknownHostException) {
                    System.out.println("Dirección del servidor SMTP incorrecta.");
                    ex = null;
                    eme = "Dirección del servidor SMTP incorrecta.";
                } else {
                    eme = ex.getMessage();
                    ex = null;
                }
                System.out.println("FIN");
            } while (ex != null);
//            throw mex;
            throw new EMailExcepcion(eme);
        } catch (AuthenticationFailedException afe) {
            System.out.println("afe: " + afe.getMessage());
            envioCorreo = false;
            throw new EMailExcepcion("Autenticacion fallida: usuario y clave no aceptada.");
        } catch (NoSuchElementException nsee) {
            System.out.println("Error al leer el listado de archivos adjuntos");
            envioCorreo = false;
            nsee.printStackTrace();
            throw new EMailExcepcion("Error al leer el listado de archivos adjuntos");
        } catch (Exception ex) {
            System.err.println("Error ex-1");
            envioCorreo = false;
            ex.printStackTrace();
            throw new EMailExcepcion(ex.getMessage());
        } finally {
            try {
                // Cerrar el transportador
                if (transport != null) {
                    transport.close();
                }
            } catch (MessagingException mex) {
                /* ignore */ }
        }
        System.gc();
        return envioCorreo;

    }

    /**
     * Configura las propiedades de la conexion utilizando la configuracion
     * guardada en la base de datos
     *
     * @param nit
     * @param cadena
     * @return
     */
    public Properties inicializarConfiguracion(String nit, String cadena) {
        this.persisConfiCorreoKiosko = new PersistenciaConfiCorreoKiosko();
        this.cck = this.persisConfiCorreoKiosko.obtenerConfiguracionCorreoNativo(nit, cadena);

        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", cck.getServidorSMTP());
        propiedades.put("mail.smtp.port", cck.getPuerto());
        if (cck.getAutenticado().equals("S")) {
            //propiedades.setProperty("mail.transport.protocol", "smtps");
            propiedades.put("mail.smtp.auth", "true");
            if (cck.getStartTLS().equals("S")) {
                propiedades.setProperty("mail.smtp.starttls.enable", "true"); //TLSv1.2 is required
                propiedades.setProperty("mail.smtp.starttls.required", "true");  //Oracle Cloud Infrastructure required
            } else if (cck.getUsarSSL().equals("S")) {
                propiedades.setProperty("mail.smtp.ssl.enable", "true"); //SSL
            }
        } else {
            propiedades.setProperty("mail.transport.protocol", "smtp");
            propiedades.put("mail.smtp.auth", "false");
        }
        propiedades.setProperty("mail.smtp.user", cck.getRemitente());
        this.imprimirPropiedades(propiedades);
        return propiedades;

    }

    private void imprimirPropiedades(Properties propiedades) {
        String strPropiedades = "";
        for (String prop : propiedades.stringPropertyNames()) {
            strPropiedades = strPropiedades + prop + " : " + propiedades.get(prop) + "\n";
        }
        System.out.println("Propiedades: " + strPropiedades);
    }

    

}
