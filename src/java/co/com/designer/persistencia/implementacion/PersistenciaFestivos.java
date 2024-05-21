package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaFestivos;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaFestivos implements IPersistenciaFestivos {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaFestivos() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public boolean verificarFestivo(String fechaDisfrute, String nitEmpresa, String cadena) throws Exception {
        System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): " + "Parametros: "
                + "fechaDisfrute: " + fechaDisfrute
                + "nitEmpresa: " + nitEmpresa
                + "cadena: " + cadena
        );
        String consulta = "select COUNT(*) "
                + "FROM FESTIVOS F, PAISES P "
                + "WHERE P.SECUENCIA = F.PAIS "
                + "AND P.NOMBRE = ? "
                + "AND F.DIA = TO_DATE( ? , 'DD/MM/YYYY') ";
        Query query = null;
        BigDecimal conteoDiaFestivo;
        boolean esDiaFestivo;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, "COLOMBIA");
            query.setParameter(2, fechaDisfrute);
            conteoDiaFestivo = new BigDecimal(query.getSingleResult().toString());
            esDiaFestivo = !conteoDiaFestivo.equals(BigDecimal.ZERO);
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): " + fechaDisfrute + " esDiaFestivo: " + esDiaFestivo);
            return esDiaFestivo;
        } catch (PersistenceException pe) {
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error-1: " + pe.getMessage());
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error-2: " + npee.getMessage());
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error-3: " + e.getMessage());
            throw new Exception(e.toString());
        }
    }
}
