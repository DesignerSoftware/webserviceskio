package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.DiagnosticosCategorias;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaDiagnosticosCategorias;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaDiagnosticosCategorias implements IPersistenciaDiagnosticosCategorias {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaDiagnosticosCategorias() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public List<DiagnosticosCategorias> getDiagnosticosCategorias(String nitEmpresa, String cadena) {
        List<DiagnosticosCategorias> resultado = null;
        String consulta = "SELECT ka "
                + "FROM DiagnosticosCategorias ka "
                + "order by ka.codigo ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createQuery(consulta);
//            query.setParameter("nitempresa", Long.valueOf(nitEmpresa));
            resultado = query.getResultList();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaDiagnosticosCategorias" + ".getDiagnosticosCategorias(): " + "Error-1: " + e.toString());
            return null;
        }
    }
    
    @Override
    public List<DiagnosticosCategorias> getDiagnosticosCategoriasNativo(String nitEmpresa, String cadena) {
        List<DiagnosticosCategorias> resultado = null;
        String consulta = "SELECT secuencia, codigo, descripcion "
                + " FROM DiagnosticosCategorias "
                + " ORDER BY codigo, descripcion ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
//            query.setParameter(1, Long.valueOf(nitEmpresa));
            resultado = query.getResultList();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaDiagnosticosCategorias" + ".getDiagnosticosCategoriasNativo(): " + "Error-1: " + e.toString());
            return null;
        }
    }
}
