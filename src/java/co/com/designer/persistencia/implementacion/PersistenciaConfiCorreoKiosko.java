package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.ConfiCorreoKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConfiCorreoKiosko;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaConfiCorreoKiosko implements IPersistenciaConfiCorreoKiosko {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaConfiCorreoKiosko() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public ConfiCorreoKiosko obtenerServidorCorreo(String nit, String cadena) {
        
        try {
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sql = "SELECT SECUENCIA, EMPRESA, SERVIDORSMTP, PUERTO, STARTTLS, AUTENTICADO, REMITENTE, CLAVE, USARSSL "
                    + "FROM CONFICORREOKIOSKO CCK "
                    + "WHERE EMPRESA=(SELECT SECUENCIA "
                    + " FROM EMPRESAS "
                    + " WHERE NIT= ? )";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sql, ConfiCorreoKiosko.class);
            query.setParameter(1, nit); //EMPRESA
            ConfiCorreoKiosko res = (ConfiCorreoKiosko) query.getSingleResult();
            return res;
        } catch (Exception e) {
            System.out.println("obtenerServidorCorreo: Error: en algo de la base de datos. " + e.getMessage());
            throw e;
        }
    }
}
