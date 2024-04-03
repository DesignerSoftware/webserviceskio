package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpresas;
import co.com.designer.persistencia.interfaz.IPersistenciaKioPersonalizaciones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioPersonalizaciones implements IPersistenciaKioPersonalizaciones{
    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    //private IPersistenciaEmpresas persistenciaEmpresas;

    public PersistenciaKioPersonalizaciones() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
//        this.persistenciaEmpresas = new PersistenciaEmpresas();
    }
    
    @Override
    public List getCorreosComiteConvivencia(String nit, String cadena) throws Exception {
        try {
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
//            BigDecimal secEmpresa = this.persistenciaEmpresas.getSecuenciaPorNitEmpresa(nit, cadena);
            String sqlQuery = "SELECT KP.EMAILCONTACTO "
                    + "FROM KIOPERSONALIZACIONES KP, EMPRESAS EM "
                    + "WHERE KP.TIPOCONTACTO = 'COMITE_CONVIVENCIA' "
                    + "AND KP.EMPRESA = EM.SECUENCIA "
                    + "AND EM.NIT = ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nit); //EMPRESA
            List correosComiteConvivencia =  query.getResultList();
            return correosComiteConvivencia;
        } catch (Exception e) {
            System.out.println("getCorreosComiteConvivencia: Error: en algo de la base de datos. " + e.getMessage());
//            e.printStackTrace();
            throw e;
        } 
    }
}
