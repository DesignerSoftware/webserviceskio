package co.com.designer.kiosko.administracion.interfaz;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IGestionArchivos {

    public String writeToFileServerAusentismos(InputStream inputStream, String fileName, String nitEmpresa, String cadena) throws IOException;
}
