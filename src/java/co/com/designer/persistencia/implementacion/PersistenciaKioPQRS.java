package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpresas;
import co.com.designer.persistencia.interfaz.IPersistenciaKioPQRS;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioPQRS implements IPersistenciaKioPQRS {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaEmpresas persistenciaEmpresas;
    private IPersistenciaConexionesKioskos persisConexionesKioskos;

    public PersistenciaKioPQRS() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();

    }

    @Override
    public int crearPQRS(String seudonimo, String nit, String titulo, String mensaje, String cadena) throws Exception {
        System.out.println("crearPQRS(): seudonimo: "
                + seudonimo
                + ", nit: " + nit
                + ", titulo: " + titulo
                + ", mensaje: " + mensaje
                + ", cadena: " + cadena);
        int conteo = 0;
        BigDecimal secPersona = null;
        BigDecimal secEmpresa = null;
        String esquema = null;
        this.persistenciaEmpresas = new PersistenciaEmpresas();
        this.persisConexionesKioskos = new PersistenciaConexionesKioskos();
        try {
            esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            secEmpresa = this.persistenciaEmpresas.getSecuenciaPorNitEmpresa(nit, cadena);
            secPersona = this.persisConexionesKioskos.getPersonaPorSeudonimo(seudonimo, nit, cadena);
            String sql = "INSERT INTO KIOPQRS (PERSONA, EMPRESA, TIPO, MENSAJE, FECHAGENERACION, ANONIMA) VALUES "
                    + "( ? , ? , ? , ? , SYSDATE, 'N' ) ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sql);
            //PERSONA
            query.setParameter(1, secPersona);
            //EMPRESA
            query.setParameter(2, secEmpresa);
            query.setParameter(3, titulo);
            query.setParameter(4, mensaje);
            conteo = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("crearPqrs: Error: en algo de la base de datos. " + e.getMessage());
            throw e;
        }
        return conteo;
    }
}
