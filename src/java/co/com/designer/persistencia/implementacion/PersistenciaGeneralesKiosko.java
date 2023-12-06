package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaGeneralesKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaGeneralesKiosko implements IPersistenciaGeneralesKiosko {
    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    final String UPLOAD_FILE_SERVER = "E:\\DesignerRHN10\\Basico10\\fotos_empleados\\";

    public PersistenciaGeneralesKiosko() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persistenciaConexiones = new PersistenciaConexiones();
    }

    @Override
    public String getPathFoto(String nitEmpresa, String cadena) {
        System.out.println("getPathFoto(): parametros: cadena: " + cadena);
        String rutaFoto = UPLOAD_FILE_SERVER;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT PATHFOTO FROM GENERALESKIOSKO WHERE ROWNUM<=1";
            System.out.println("getPathFoto: Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("getPathFoto: rutaFotos: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("getPathFoto(): Error: " + e.getMessage());
        }
        return rutaFoto;
    }
}
