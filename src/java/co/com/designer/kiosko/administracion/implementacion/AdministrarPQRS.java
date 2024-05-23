package co.com.designer.kiosko.administracion.implementacion;

import co.com.designer.kiosko.administracion.interfaz.IAdministrarPQRS;
import co.com.designer.kiosko.generales.EnvioCorreo;
import co.com.designer.persistencia.implementacion.PersistenciaKioPQRS;
import co.com.designer.persistencia.interfaz.IPersistenciaKioPQRS;
import co.com.designer.kiosko.generales.ExtraeCausaExcepcion;
import co.com.designer.persistencia.implementacion.PersistenciaGeneralesKiosko;
import co.com.designer.persistencia.implementacion.PersistenciaKioPersonalizaciones;
import co.com.designer.persistencia.interfaz.IPersistenciaGeneralesKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaKioPersonalizaciones;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Edwin Hastamorir
 */
public class AdministrarPQRS implements IAdministrarPQRS {

    private IPersistenciaKioPQRS persisKioPQRS;
    private IPersistenciaKioPersonalizaciones persisKioPersonaliza;
    private IPersistenciaGeneralesKiosko persisGeneralesKiosko;

    public AdministrarPQRS() {
        this.persisKioPQRS = new PersistenciaKioPQRS();
        this.persisKioPersonaliza = new PersistenciaKioPersonalizaciones();
        this.persisGeneralesKiosko = new PersistenciaGeneralesKiosko();
    }

    @Override
    public JSONObject crearPQRS(String seudonimo, String nit, String titulo, String mensaje, String cadena, String url) {

        System.out.println("Parametros crearPQRS(): seudonimo " + seudonimo + ", nit: " + nit + ", cadena: " + cadena);

        boolean soliciCreada = false;
        boolean correoEnviado = false;

        // Registro en tabla 
        int res = 0;
        Date fecha = new Date();
        String fechaGeneracion = new SimpleDateFormat("ddMMyyyy HHmmss").format(fecha);
        System.out.println("fecha: " + fechaGeneracion);

        String mensajeTxt = "Nos permitimos informar que se ha enviado "
                + (titulo.equalsIgnoreCase("RECLAMO") ? "el" : "la")
                + " siguiente " 
                + "<b class=\"negrilla\">"
                + titulo + ":</b><br/> "
                + mensaje 
                ;
        String asunto = "¡Nueva PQRSF enviada! " + titulo + " " + fechaGeneracion;
        String saludo = "Estimados colaboradores: ";
        String mensajeCorreo = this.generarMensaje(mensajeTxt, saludo, url);
        titulo = limpiezaTildes(titulo);
        try {
            soliciCreada = this.persisKioPQRS.crearPQRS(seudonimo, nit, titulo, mensaje, cadena) > 0;
        } catch (Exception ex) {
            mensaje = "Ha ocurrido un error y no fue posible enviar la PQRSF, por favor inténtelo de nuevo más tarde. "
                    + ExtraeCausaExcepcion.getLastThrowable(ex).getMessage();
            System.out.println("Ha ocurrido un error al momento de crear el registro");
        }
        if (soliciCreada) {
            try {
                List correos = this.persisKioPersonaliza.getCorreosComiteConvivencia(nit, cadena);
                EnvioCorreo ec = new EnvioCorreo();
                ec.enviarCorreo(nit, cadena, asunto, mensajeCorreo, correos,
                        this.persisGeneralesKiosko.getPathFoto(nit, cadena));
                correoEnviado = true;
                mensaje = "Mensaje Creado con Exito y Envío de correo.";
                System.out.println("PQRSF creada.");

            } catch (Exception ex) {
                System.out.println("Se ha registrado la PQRFS pero ha ocurrido un error al momento de enviar la PQRSF");
                mensaje = "Se ha registrado la PQRFS pero ha ocurrido un error al enviar la PQRFS por correo, "
                        + "por favor intentalo de nuevo más tarde. "
                        + ExtraeCausaExcepcion.getLastThrowable(ex).getMessage();
            }
        }
        System.out.println("crearPQRS: mensaje: " + mensaje);
        // Respuesta
        JSONObject obj = new JSONObject();
        try {
            obj.put("PQRS_Creada", soliciCreada);
            obj.put("mensaje", mensaje);
            obj.put("correoEnviado", correoEnviado);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return obj;
    }

    private String limpiezaTildes(String texto) {
        if (texto.contains("Á") || texto.contains("É")
                || texto.contains("Í") || texto.contains("Ó")
                || texto.contains("Ú")) {
            texto = texto.replace("Á", "A");
            texto = texto.replace("É", "E");
            texto = texto.replace("Í", "I");
            texto = texto.replace("Ó", "O");
            texto = texto.replace("Ú", "U");
        }
        if (texto.contains("á") || texto.contains("é")
                || texto.contains("í") || texto.contains("ó")
                || texto.contains("ú")) {
            texto = texto.replace("á", "a");
            texto = texto.replace("é", "e");
            texto = texto.replace("í", "i");
            texto = texto.replace("ó", "o");
            texto = texto.replace("ú", "u");
        }
        return texto;
    }

    private String generarMensaje(String mensaje, String saludo, String url) {
        String htmlText
                = "<div style=\"background-color: rgba(237, 237, 237, 0.8);\"> \n"
                + "   <div style=\"padding: 10px 0px;\"> \n"
                + "      <div style=\"background-color: white;margin: 0 auto; width: 700px;\"> \n"
                + "         <div \n"
                + "            style=\"margin: 0px auto; text-align: center;font-family: sans-serif;  justify-content: flex-start; padding-top: 20px; font-size: 18px;\"> \n"
                + "            <img src=\"https://www.nomina.com.co/images/images/kiosko/LOGO.png\" alt=\"\" style=\"width: 250px;\"> \n"
                + "            <br/> \n"
                + "            <img src=\"https://www.nomina.com.co/images/images/logoKiosco.png\" alt=\"\" style=\"padding: 0px 0px 0px 0px; width: 700px;\"> \n"
                + "            <div style=\"margin: 0 auto; width: 550px; padding: 5px;\"> \n"
                + "               <h3 style=\"color: #00223c;\">" + saludo + " </h3> \n"
                + "               <p> \n" + mensaje + " <br/><br/> \n"
                + "                 Este reporte se ha generado automáticamente desde Kiosco Nómina Designer. \n"
                + "               </p> \n"
                + "               <br/> \n"
                + "               <div> \n"
                + "                  <a style=\"text-decoration:none; border-radius:5px; padding:10px 48px; color:white; background-color:#3498db;\" \n"
                + "                    href=" + url + " target=\"_blank\" data-saferedirecturl=" + url + "> Ir a Kiosco </a> \n"
                + "              </div> \n"
                + "               <br/> \n"
                + "             </div> \n"
                + "           <hr> \n"
                + "             <a href=\"https://nomina.com.co/\" target=\"_blank\"><img src=\"https://www.nomina.com.co/images/images/kiosko/LOGO_N.png\" style=\"width: 60px;\" alt=\"\"></a> \n"
                + "             <div style=\"padding: 10px 0px 0px 0px;color: #00223c;\"> \n"
                + "             <ul style=\"padding: 0px;margin: 0px;\"> \n"
                + "               <li style=\"display:inline;\"><a href=\"https://www.facebook.com/nominads\" target=\"_blank\"> \n"
                + "                   <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_face.png\" style=\"color: #00223c; padding: 0px 10px; width: 40px;\" alt=\"\"></a> \n"
                + "               </li> \n"
                + "               <li style=\"display:inline;\"><a href=\"https://twitter.com/NominaDesigner\" target=\"_blank\"> \n"
                + "                    <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_twitee.png\" style=\"color: #00223c;padding: 0px 10px;width: 40px;\" alt=\"\"></a> \n"
                + "               </li> \n"
                + "               <li style=\"display:inline;\"><a href=\"https://www.youtube.com/user/nominads\" target=\"_blank\"> \n"
                + "                   <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_youtube.png\" style=\"color: #00223c;padding: 0px 10px;width: 40px;\" alt=\"\"></a> \n"
                + "               </li> \n"
                + "               <li style=\"display:inline;\"><a href=\"https://www.instagram.com/nomina_designer\" target=\"_blank\"> \n"
                + "                   <img src=\"https://www.nomina.com.co/images/images/kiosko/ico_insta.png\" style=\"color: #00223c;padding: 0px 10px;width: 40px;\" alt=\"\"></a> \n"
                + "               </li> \n"
                + "             </ul> \n"
                + "           </div> \n"
                + "           <p style=\"font-size: 0.8rem;padding: 10px 0px;margin: 0px;\">Este reporte se ha generado automáticamente desde Kiosco Nómina Designer.</p> \n"
                + "         </div> \n"
                + "    </div> \n"
                + "  </div> \n"
                + "</div> \n";
        System.out.println("generarMensaje: htmlText: " + htmlText);
        return htmlText;
    }
}
