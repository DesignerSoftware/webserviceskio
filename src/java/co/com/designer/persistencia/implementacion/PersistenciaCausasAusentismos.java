package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaCausasAusentismos;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 * @author Wilmer Uribe
 * @author Mateo Coronado
 */
public class PersistenciaCausasAusentismos implements IPersistenciaCausasAusentismos {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaCausasAusentismos() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public String getCausaOrigenIncapacidad(String causa, String nitEmpresa, String cadena) {
        String resultado = null;
        String consulta = "SELECT origenIncapacidad "
                + "FROM CausasAusentismos ca "
                + "WHERE ca.secuencia = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, causa);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getCausaOrigenIncapacidad(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public String getSecuenciaCausaAusentismo(String codCausa, String nitEmpresa, String cadena) {
        String resultado = null;
        String consulta = "SELECT ca.secuencia "
                + "FROM CausasAusentismos ca "
                + "WHERE ca.codigo = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, codCausa);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSecuenciaCausaAusentismo(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }
}
