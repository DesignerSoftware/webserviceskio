package co.com.designer.kiosko.generales;

import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConexiones;
import co.com.designer.persistencia.implementacion.PersistenciaConfiCorreoKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaEmpresas;
import co.com.designer.persistencia.implementacion.PersistenciaGeneralesKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConfiCorreoKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpresas;
import co.com.designer.persistencia.interfaz.IPersistenciaGeneralesKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaKioPersonalizaciones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Thalia Manrique
 * @author Edwin Hastamorir
 */
public class GenerarCorreo {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaConfiCorreoKiosko persisConfiCorreoKiosko;
    private IPersistenciaGeneralesKiosko persisGeneralesKiosko;
    private IPersistenciaKioPersonalizaciones persisPersonaliza;
    private IPersistenciaEmpresas persisEmpresas;

    private final String urlKio = "https://www.nomina.com.co/images/images/kiosko/";

    public GenerarCorreo() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.persisConfiCorreoKiosko = new PersistenciaConfiCorreoKiosko();
        this.persisGeneralesKiosko = new PersistenciaGeneralesKiosko();
    }

    /**
     * Método para enviar un correo con reporte.
     *
     * @param destinatario
     * @param asunto
     * @param mensaje
     * @param rutaReporte
     * @param nombreReporte
     * @param nitEmpresa
     * @param cadena
     * @param urlKiosco
     */
    public void pruebaEnvio2(String destinatario,
            String asunto,
            String mensaje,
            String rutaReporte,
            String nombreReporte,
            String nitEmpresa,
            String cadena,
            String urlKiosco
    ) {
        EnvioCorreo ec = new EnvioCorreo();
        List<String> destinatarios = new ArrayList();
        List<ArchivoCorreo> adjuntos = new ArrayList();

        String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);

        String htmlText
                = this.plantillaMensajeGenerica(
                        "Reporte",
                        mensaje,
                        urlKiosco,
                        this.urlKio);

        try {
            adjuntos.add(new ArchivoCorreo(rutaImagenes,
//                    "imagencorreoreporte.jpg",
                    "headerlogocorreokiosko.png",
                    "imagen",
                    "<image>"
            ));
            adjuntos.add(new ArchivoCorreo(rutaReporte,
                    nombreReporte,
                    "archivo",
                    "application/pdf"
            ));
            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            ec.enviarCorreo(propiedades,
                    asunto, htmlText,
                    destinatario, destinatarios, adjuntos);
        } catch (Exception e) {
            System.err.println("Error: " + "GenerarCorreo." + "pruebaEnvio2(): " + " al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean enviarNuevaClave(String destinatario,
            String nombreUsuario, String nuevaClave,
            String nitEmpresa, String cadena, String urlKiosco
    ) {
        boolean envioCorreo = false;
        EnvioCorreo ec = new EnvioCorreo();
        List<String> destinatarios = new ArrayList();
        List<ArchivoCorreo> adjuntos = new ArrayList();

        String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);

        try {
            adjuntos.add(new ArchivoCorreo(rutaImagenes,
                    "headerlogocorreokiosko.png",
                    "imagen",
                    "<image>"
            ));

            String mensaje = "Tu nueva contraseña para ingresar al Módulo de Autogestión Kiosco es: <br><br>"
                    + nuevaClave + "";

            String htmlText
                    = this.plantillaMensajeGenerica(
                            "Validación cuenta de correo",
                            mensaje,
                            urlKiosco,
                            this.urlKio);

            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            envioCorreo = ec.enviarCorreo(propiedades,
                    "Tu nueva clave de Kiosco!", htmlText,
                    destinatario, destinatarios, adjuntos);
        } catch (Exception e) {
            System.err.println("Error: " + "GenerarCorreo." + "enviarNuevaClave(): " + " al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        return envioCorreo;
    }

    public boolean enviarEnlaceValidacionCuenta(
            String destinatario, String nombreUsuario, String seudonimo,
            String jwt, String nitEmpresa, String cadena, String urlKiosco) {
        boolean envioCorreo = false;
        this.persisEmpresas = new PersistenciaEmpresas();

        EnvioCorreo ec = new EnvioCorreo();
        List<String> destinatarios = new ArrayList();
        List<ArchivoCorreo> adjuntos = new ArrayList();

        String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);
        String logo = "headerlogocorreokiosko.png";
        
        try {
            adjuntos.add(new ArchivoCorreo(rutaImagenes,
                    logo,
                    "imagen",
                    "<image>"
            ));

            String urlValidacion = "";
            if (urlKiosco.endsWith("/")) {
                urlValidacion = urlKiosco + "#/validacionCuenta/" + jwt;
            } else {
                urlValidacion = urlKiosco + "/#/validacionCuenta/" + jwt;
            }
            String mensaje = "Recuerda que tu usuario para ingresar al Módulo de Autogestión Kiosco es: " + seudonimo
                    + ". <br/> Puedes confirmar dando clic en el botón \"Ir a Kiosco\". <br/>"
                    + "Si el botón no funciona, copie y pegue en su navegador el siguiente enlace: <br/><br/>"
                    + urlValidacion;
            String htmlText
                    = this.plantillaMensajeGenerica(
                            "Validación de cuenta de correo",
                            mensaje,
                            urlValidacion,
                            this.urlKio);

            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            envioCorreo = ec.enviarCorreo(propiedades,
                    "Bienvenido al Módulo de Autogestión Kiosco - ¡Confirma tu cuenta ahora!",
                    htmlText,
                    destinatario, destinatarios, adjuntos);
        } catch (Exception e) {
            //System.err.println("Error al enviar correo: " + e.getMessage());
            System.err.println("Error: " + "GenerarCorreo." + "enviarEnlaceValidacionCuenta(): " + " al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        return envioCorreo;
    }

    public boolean enviarCorreoVacaciones(String destinatario,
            String asunto, String mensaje, String nitEmpresa, String cadena, String urlKiosco) {
        boolean envioCorreo = false;
        EnvioCorreo ec = new EnvioCorreo();
        List<String> destinatarios = new ArrayList();
        List<ArchivoCorreo> adjuntos = new ArrayList();

        String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);

        try {
            adjuntos.add(new ArchivoCorreo(rutaImagenes,
                    "headerlogocorreokiosko.png",
                    "imagen",
                    "<image>"
            ));

            String htmlText
                    = this.plantillaMensajeGenerica(
                            "Vacaciones",
                            mensaje,
                            urlKiosco,
                            this.urlKio);

            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            envioCorreo = ec.enviarCorreo(propiedades,
                    asunto,
                    htmlText,
                    destinatario, destinatarios, adjuntos);
        } catch (Exception e) {
            System.err.println("Error: " + "GenerarCorreo." + "enviarCorreoVacaciones(): " + " al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        return envioCorreo;
    }

    /**
     * Método para enviar los mensajes relacionados con los ausentismos
     *
     * @param destinatario
     * @param asunto
     * @param mensaje
     * @param nombreAnexo
     * @param nitEmpresa
     * @param cadena
     * @param urlKiosco
     * @return
     */
    public boolean enviarCorreoAusentismos(String destinatario,
            String asunto, String mensaje, String nombreAnexo, String nitEmpresa, String cadena, String urlKiosco) {
        boolean envioCorreo = false;
        EnvioCorreo ec = new EnvioCorreo();
        List<String> destinatarios = new ArrayList();
        List<ArchivoCorreo> adjuntos = new ArrayList();

        try {
            String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);
            String pathReportes = this.persisGeneralesKiosko.getPathReportes(nitEmpresa, cadena) + "anexosAusentismos\\";

            adjuntos.add(new ArchivoCorreo(rutaImagenes,
                    "headerlogocorreokiosko.png",
                    "imagen",
                    "<image>"
            ));

            if (nombreAnexo != null) {
                adjuntos.add(new ArchivoCorreo(pathReportes,
                        nombreAnexo,
                        "archivo",
                        "application/pdf"
                ));
            }

            if (nombreAnexo != null && !nombreAnexo.equals("null")) {
                mensaje = mensaje + "</p><p>Adjunto a este correo encontrará el documento anexo a la novedad de ausentismo.";
            }
            String htmlText
                    = this.plantillaMensajeGenerica(
                            "Ausentismo",
                            mensaje,
                            urlKiosco,
                            this.urlKio);

            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            envioCorreo = ec.enviarCorreo(propiedades,
                    asunto,
                    htmlText,
                    destinatario, destinatarios, adjuntos);
        } catch (Exception e) {
            System.err.println("Error: " + "GenerarCorreo." + "enviarCorreoAusentismos(): " + " al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        return envioCorreo;
    }

    /**
     * Correo novedad de corrección de información que se envia a RRHH o
     * Auditoria Módulo Vacaciones(No se incluye botón de Ir a Kiosco)
     *
     * @param correoDestinatarioMain
     * @param correoCC
     * @param asunto
     * @param saludo
     * @param mensaje
     * @param nitEmpresa
     * @param cadena
     * @param urlKiosco
     * @return
     */
    public boolean enviarCorreoInformativo(String correoDestinatarioMain, String correoCC,
            String asunto, String saludo, String mensaje, String nitEmpresa, String cadena, String urlKiosco) {
        boolean envioCorreo = false;
        EnvioCorreo ec = new EnvioCorreo();
        List<String> destinatarios = new ArrayList();
        List<ArchivoCorreo> adjuntos = new ArrayList();
        Calendar fenvio = Calendar.getInstance();

        if (correoCC != null && !correoCC.isEmpty()) {
            destinatarios.add(correoCC);
        }
        try {
            String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);

            adjuntos.add(new ArchivoCorreo(rutaImagenes,
                    "headerlogocorreokiosko.png",
                    "imagen",
                    "<image>"
            ));

            String htmlText
                    = this.plantillaMensajeGenerica(
                            "Comunicado publicado",
                            mensaje,
                            urlKiosco,
                            this.urlKio);

            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            envioCorreo = ec.enviarCorreo(propiedades,
                    asunto,
                    htmlText,
                    correoDestinatarioMain, destinatarios, adjuntos);
        } catch (Exception e) {
            System.err.println("Error: " + "GenerarCorreo." + "enviarCorreoInformativo(): " + "Al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        return envioCorreo;
    }

    /**
     * Correo novedad de corrección de información que se envia a RRHH o
     * Auditoria Módulo Vacaciones(No se incluye botón de Ir a Kiosco)
     *
     * @param correoDestinatarioMain
     * @param asunto
     * @param saludo
     * @param mensaje
     * @param nitEmpresa
     * @param cadena
     * @param url
     * @return
     */
    public boolean enviarCorreoComunicado(String correoDestinatarioMain,
            String asunto, String saludo, String mensaje, String nitEmpresa, String cadena, String url) {
        boolean envioCorreo = false;
        EnvioCorreo ec = new EnvioCorreo();
        List<String> destinatarios = new ArrayList();
        List<ArchivoCorreo> adjuntos = new ArrayList();

        try {
            String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);

            adjuntos.add(new ArchivoCorreo(rutaImagenes,
                    "headerlogocorreokiosko.png",
                    "imagen",
                    "<image>"
            ));

            String htmlText = this.plantillaMensajeGenerica(
                    "Comunicado publicado",
                    mensaje,
                    url,
                    this.urlKio);
            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            envioCorreo = ec.enviarCorreo(propiedades,
                    asunto,
                    htmlText,
                    correoDestinatarioMain, destinatarios, adjuntos);
        } catch (Exception e) {
            System.err.println("Error: " + "GenerarCorreo." + "enviarCorreoComunicado(): " + " al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        return envioCorreo;
    }

    /**
     * Método para enviar mensaje por correo usando la configuración del
     * servidor de correo almacenada en la base de datos para la empresa
     * enviada.
     *
     * @param destinatario
     * @param destinatarios
     * @param asunto
     * @param mensaje
     * @param nitEmpresa
     * @param cadena
     * @return
     */
    public boolean enviarCorreo(String destinatario, List destinatarios, String asunto, String mensaje, String nitEmpresa, String cadena) {
        boolean envioCorreo = false;
        EnvioCorreo ec = new EnvioCorreo();
        List<ArchivoCorreo> adjuntos = new ArrayList();

        try {
            String rutaImagenes = this.persisGeneralesKiosko.getPathFoto(nitEmpresa, cadena);

            adjuntos.add(new ArchivoCorreo(rutaImagenes,
                    "headerlogocorreokiosko.png",
                    "imagen",
                    "<image>"
            ));

            String htmlText = this.plantillaMensajeGenerica(
                    "Mensaje",
                    mensaje,
                    "",
                    this.urlKio);

            Properties propiedades = ec.inicializarConfiguracion(nitEmpresa, cadena);
            envioCorreo = ec.enviarCorreo(propiedades,
                    asunto,
                    htmlText,
                    destinatario, destinatarios, adjuntos);
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }

        return envioCorreo;
    }

    /**
     * Metodo para entregar el mensaje decorado usando una plantilla en HTML.
     *
     * @param titulo Titulo del correo.
     * @param mensajeCorreo Mensaje a mostrar en el correo.
     * @param urlKiosko URL del kiosco que el usuario utilizo
     * @param urlKio URL donde estan publicadas las imagenes del kiosko.
     * @return
     */
    private String plantillaMensajeGenerica(String titulo, String mensajeCorreo, String urlKiosko, String urlKio) {
        String htmlString = "<!DOCTYPE html> \n"
                + "<html lang='es'> \n"
                + "  <head> \n"
                + "    <meta charset='UTF-8'> \n"
                + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'> \n"
                + "    <title> Correo de " + titulo + "</title> \n"
                + "    <style> \n"
                + "        body { \n"
                + "            font-family: sans-serif !important; \n"
                + "        } \n"
                + "        .email-container { \n"
                + "            color: #000 !important; \n"
                + "            font-size: 20px !important; \n"
                + "            padding-top: 1% !important; \n"
                + "            max-width: 600px !important; \n"
                + "            min-width: auto !important; \n"
                + "        } \n"
                + "        .email-content { \n"
                + "            background-color: #ffffff7a !important; \n"
                + "            border-collapse: collapse !important; \n"
                + "            box-shadow: 0px 1px 20px rgb(0, 0, 0, 0.603) !important; \n"
                + "            max-width: 600px !important; \n"
                + "            min-width: auto !important; \n"
                + "            border-radius: 10px !important; \n"
                + "        } \n"
                + "        .container-asunto { \n"
                + "            border-top-left-radius: 10px !important; \n"
                + "            border-top-right-radius: 10px !important; \n"
                + "            padding: 10px 12% !important; \n"
                + "            font-family: 'Open Sans', Arial, sans-serif !important; \n"
                + "            font-size: 20px !important; \n"
                + "            line-height: 28px !important; \n"
                + "            color: #ffffff !important; \n"
                + "            background: linear-gradient(0deg, #00223C, #0868B3) !important; \n"
                + "        } \n"
                + "        .center { \n"
                + "            text-align: center !important; \n"
                + "            margin-top: 10px !important; \n"
                + "        } \n"
                + "        .btn { \n"
                + "            border: none !important; \n"
                + "            color: #fff !important; \n"
                + "            cursor: pointer !important; \n"
                + "            border-radius: 5px !important; \n"
                + "            padding-left: 10px !important; \n"
                + "            box-shadow: 4px 5px 6px rgba(0, 0, 0, 0.603) !important; \n"
                + "            justify-content: center !important; \n"
                + "            display: flex !important; \n"
                + "            text-decoration: none !important; \n"
                + "            margin-bottom: 4% !important; \n"
                + "            height: 40px !important; \n"
                + "            text-align: center !important; \n"
                + "            align-items: center !important; \n"
                + "        } \n"
                + "        .primary { \n"
                + "            background-color: #0868B3 !important; \n"
                + "        } \n"
                + "        .primary:hover { \n"
                + "            stroke-linecap: round !important; \n"
                + "            stroke-linejoin: round !important; \n"
                + "            stroke: #111 !important; \n"
                + "            stroke-width: 2 !important; \n"
                + "            transform: translateX(-5px) !important; \n"
                + "            transition: all 0.2s ease !important; \n"
                + "            transform: scale(0.96) !important; \n"
                + "            background: #014d86 !important; \n"
                + "            color: #ffffff; \n"
                + "        } \n"
                + "        .social-icons ul { \n"
                + "            max-width: 600px !important; \n"
                + "            min-width: auto !important; \n"
                + "            text-align: center !important; \n"
                + "            padding: 10px 0 0 0 !important; \n"
                + "        } \n"
                + "        .social-icons li { \n"
                + "            display: inline !important; \n"
                + "            padding: 10px 6px !important; \n"
                + "            text-decoration: none !important; \n"
                + "            transition: all 300ms ease !important; \n"
                + "        } \n"
                + "        .social-icons img { \n"
                + "            width: 40px !important; \n"
                + "            height: 40px !important; \n"
                + "            display: inline-block !important; \n"
                + "            color: #fff !important; \n"
                + "            box-shadow: 4px 5px 6px rgba(0, 0, 0, 0.603) !important; \n"
                + "            border-radius: 10px !important; \n"
                + "        } \n"
                + "        .txt { \n"
                + "            font-family: 'Open Sans', Arial, sans-serif !important; \n"
                + "            font-size: 16px !important; \n"
                + "            line-height: 28px !important; \n"
                + "            color: #333333 !important; \n"
                + "            text-align: center !important; \n"
                + "        } \n"
                + "        .txt-footer { \n"
                + "            font-family: 'Open Sans', Arial, sans-serif !important; \n"
                + "            font-style: italic; \n"
                + "            font-size: 12px !important; \n"
                + "            line-height: 28px !important; \n"
                + "            color: #333333 !important; \n"
                + "            text-align: center !important; \n"
                + "            margin-bottom: 20px; \n"
                + "        } \n"
                + "        .img-logo { \n"
                + "            display: block !important; \n"
                + "            margin: 20px auto 0px auto !important; \n"
                + "            padding-bottom: 20px !important; \n"
                + "        } \n"
                + "    </style> \n"
                + "</head> \n"
                + "<body> \n"
                + "  <div class='email-container'> \n"
                + "    <table class='email-content center'> \n"
                + "        <tr> \n"
                + "            <td class='container-asunto'> \n"
                + "                <img width='32px' height='32px' style='vertical-align: middle;' \n"
                + "                     src='" + urlKio + "correo.png'> \n"
                + "                <span style='padding-left: 8px; vertical-align: middle; font-weight: bold'> \n"
                + titulo
                + "                </span> \n"
                + "            </td> \n"
                + "        </tr> \n"
                + "        <tbody> \n"
                + "            <tr style='padding-bottom: 2%'> \n"
                + "                <td> \n"
                + "                    <div style='margin: 4%; text-align: justify;'> \n"
                + "                        <div style='text-align: center'> \n"
                //                + "                            <a style='color: white !important;' target='_blank'> \n"
                //                + "                                <img width='auto' height='auto' class='img-logo' \n"
                //                + "                                     src='" + urlKio + "logodesigner-dark-xl.webp'> \n"
                //                + "                            </a> \n"
                + "                            <a style='color: white !important;' target='_blank'> \n"
                + "                                <img width='auto' height='auto' class='img-logo' \n"
                + "                                     src=\'cid:image\'> \n"
                + "                            </a> \n"
                + "                        </div> \n"
                + "                        <h3 style='text-align: center;'>Estimado usuario(a):</h3> \n"
                + "                        <p class='txt'> \n"
                + mensajeCorreo
                + "                        </p> \n"
                + "                        <p class='txt-footer'> \n"
                + "                            <strong> \n"
                + "                              Este mensaje se generó de manera automática. Por favor no responda o escriba a esta cuenta. \n"
                + "                              Si requiere apoyo con alguna duda, por favor comuníquese con el área de Talento Humano de su empresa. \n"
                + "                            </strong> \n"
                + "                        </p> \n";

        if (urlKiosko == null || urlKiosko.isEmpty()) {
            htmlString = htmlString + "                        <a class='btn primary' href='' target='_blank' data-saferedirecturl=''> \n"
                    + "                            <span style='margin-left: 40%; margin-right: 40%; text-align: center'> </span> \n"
                    + "                        </a> \n";
        } else {
            htmlString = htmlString + "                        <a class='btn primary' href='" + urlKiosko + "' target='_blank' data-saferedirecturl='" + urlKiosko + "'> \n"
                    + "                            <span style='margin-left: 40%; margin-right: 40%; text-align: center'>Ir a Kiosco</span> \n"
                    + "                        </a> \n";
        }

        htmlString = htmlString + "                    </div> \n"
                + "                </td> \n"
                + "            </tr> \n"
                + "            <tr> \n"
                + "                <td class='social-icons'> \n"
                + "                    <ul> \n"
                + "                        <li> \n"
                + "                            <a href='https://www.facebook.com/nominads' target='_blank'> \n"
                + "                                <img src='" + urlKio + "21113922.webp'> \n"
                + "                            </a> \n"
                + "                        </li> \n"
                + "                        <li> \n"
                + "                            <a href='https://twitter.com/NominaDesigner' target='_blank'> \n"
                + "                                <img src='" + urlKio + "733635.webp'> \n"
                + "                            </a> \n"
                + "                        </li> \n"
                + "                        <li> \n"
                + "                            <a href='https://www.instagram.com/nomina_designer/?hl=es' target='_blank'> \n"
                + "                                <img src='" + urlKio + "instagram.png'> \n"
                + "                            </a> \n"
                + "                        </li> \n"
                + "                        <li> \n"
                + "                            <a href='https://www.nomina.com.co' target='_blank'> \n"
                + "                                <img src='" + urlKio + "3522533.webp'> \n"
                + "                            </a> \n"
                + "                        </li> \n"
                + "                        <li> \n"
                + "                            <a href='https://www.youtube.com/user/nominads' target='_blank'> \n"
                + "                                <img src='" + urlKio + "733646.webp'> \n"
                + "                            </a> \n"
                + "                        </li> \n"
                + "                    </ul> \n"
                + "                </td> \n"
                + "            </tr> \n"
                + "        </tbody> \n"
                + "    </table> \n"
                + "  </div> \n"
                + "</body> \n"
                + "</html>";
        return htmlString;
    }
}
