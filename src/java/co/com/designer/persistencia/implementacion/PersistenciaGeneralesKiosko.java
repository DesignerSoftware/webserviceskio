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

    public PersistenciaGeneralesKiosko() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persistenciaConexiones = new PersistenciaConexiones();
    }

    @Override
    public String getPathFoto(String nitEmpresa, String cadena) {
        System.out.println("PersistenciaGeneralesKiosko" + ".getPathFoto(): " + "Parametros: "
                + "nitEmpresa: " + nitEmpresa
                + " cadena: " + cadena);
        String UPLOAD_FILE_SERVER = "E:\\DesignerRHN10\\Basico10\\fotos_empleados\\";
        String rutaFoto = UPLOAD_FILE_SERVER;
        String sqlQuery = "SELECT pathFoto "
                + "FROM generalesKiosko "
                + "WHERE ROWNUM<=1 ";
        System.out.println("PersistenciaGeneralesKiosko" + ".getPathFoto(): " + "sqlQuery: " + sqlQuery);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaGeneralesKiosko" + ".getPathFoto(): " + "Error: " + e.toString());
        }
        return rutaFoto;
    }

    @Override
    public String getPathReportes(String nitEmpresa, String cadena) {
        System.out.println("PersistenciaGeneralesKiosko" + ".getPathReportes(): " + "Parametros: "
                + "nitEmpresa: " + nitEmpresa
                + " cadena: " + cadena);
        String rutaFoto = "";
        String sqlQuery = "SELECT pathReportes "
                + "FROM generalesKiosko "
                + "WHERE ROWNUM<=1 ";
        System.out.println("PersistenciaGeneralesKiosko" + ".getPathReportes(): " + "Query: " + sqlQuery);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);

            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaFoto = query.getSingleResult().toString();
            System.out.println("PersistenciaGeneralesKiosko" + ".getPathReportes(): " + "rutaFoto: " + rutaFoto);
        } catch (Exception e) {
            System.out.println("PersistenciaGeneralesKiosko" + ".getPathReportes(): " + "Error: " + e.toString());
        }
        return rutaFoto;
    }
    
    @Override
    public String getPathAusentismos(String nitEmpresa, String cadena) {
        System.out.println("PersistenciaGeneralesKiosko" + ".getPathAusentismos(): " + "Parametros: "
                + "nitEmpresa: " + nitEmpresa
                + " cadena: " + cadena);
        String rutaAnexos = "";
        String sqlQuery = "SELECT pathReportes "
                + "FROM generalesKiosko "
                + "WHERE ROWNUM<=1 ";
        System.out.println("PersistenciaGeneralesKiosko" + ".getPathAusentismos(): " + "Query: " + sqlQuery);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);

            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            rutaAnexos = query.getSingleResult().toString()+"anexosAusentismos\\";
            System.out.println("PersistenciaGeneralesKiosko" + ".getPathAusentismos(): " + "rutaAnexos: " + rutaAnexos);
        } catch (Exception e) {
            System.out.println("PersistenciaGeneralesKiosko" + ".getPathAusentismos(): " + "Error: " + e.toString());
        }
        return rutaAnexos;
    }
}
