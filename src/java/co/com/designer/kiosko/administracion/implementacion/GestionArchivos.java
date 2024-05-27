package co.com.designer.kiosko.administracion.implementacion;

import co.com.designer.kiosko.administracion.interfaz.IGestionArchivos;
import co.com.designer.persistencia.implementacion.PersistenciaGeneralesKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaGeneralesKiosko;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

/**
 *
 * @author Edwin Hastamorir
 */
public class GestionArchivos implements IGestionArchivos {

    private IPersistenciaGeneralesKiosko persisGeneralesKio;

    public GestionArchivos() {
        this.persisGeneralesKio = new PersistenciaGeneralesKiosko();
    }

    @Override
    public String writeToFileServerAusentismos(InputStream inputStream, String fileName, String nitEmpresa, String cadena) throws IOException {

        OutputStream outputStream = null;
        String qualifiedUploadFilePath = null;
        
        try {
            qualifiedUploadFilePath = this.persisGeneralesKio.getPathAusentismos(nitEmpresa, cadena) + fileName;
            outputStream = new FileOutputStream(new File(qualifiedUploadFilePath));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
        } catch (FileNotFoundException fnfe) {
            System.out.println(this.getClass().getName() + ".writeToFileServer(): " + "Error-1: " + fnfe.toString());
            System.out.println(this.getClass().getName() + ".writeToFileServer(): " + "Hubo un problema al enviar el archivo.");
            return "N";
        } catch (IOException ioe) {
            System.out.println(this.getClass().getName() + ".writeToFileServer(): " + "Error-2: " + ioe.toString());
//            ioe.printStackTrace();
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ".writeToFileServer(): " + "Error-3: " + e.toString());
        } finally {
            outputStream.close();
        }
        return qualifiedUploadFilePath;
    }
}
