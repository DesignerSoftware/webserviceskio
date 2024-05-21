package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaJornadasLaborales;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaManejoFechas;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaJornadasLaborales implements IPersistenciaJornadasLaborales {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaManejoFechas persisManejoFechas;

    public PersistenciaJornadasLaborales() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    public boolean verificarDiaLaboral(String fechaDisfrute, BigDecimal codigoJornada, String nitEmpresa, String cadena) throws Exception {
        System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): " + "Parametros: "
                + "fechaDisfrute: " + fechaDisfrute
                + "codigoJornada: " + codigoJornada
                + "nitEmpresa: " + nitEmpresa
                + "cadena: " + cadena
        );
        String consulta = "select COUNT(*) "
                + "FROM JORNADASSEMANALES JS, JORNADASLABORALES JL "
                + "WHERE JL.SECUENCIA = JS.JORNADALABORAL "
                + "AND JL.CODIGO = TO_number( ? ) "
                + "AND JS.DIA = ? ";
        Query query = null;
        BigDecimal conteoDiaLaboral;
        boolean esDiaLaboral;
        int diaSemana;
        String strFechaDisfrute = "";
        GregorianCalendar c = new GregorianCalendar();
        persisManejoFechas = new PersistenciaManejoFechas();
        c.setTime(persisManejoFechas.getDate(fechaDisfrute, cadena));
        diaSemana = c.get(Calendar.DAY_OF_WEEK);
        strFechaDisfrute = persisManejoFechas.nombreDia(diaSemana);
        System.out.println("strFechaDisfrute: " + strFechaDisfrute);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, codigoJornada);
            query.setParameter(2, strFechaDisfrute);
            conteoDiaLaboral = new BigDecimal(query.getSingleResult().toString());
            esDiaLaboral = !conteoDiaLaboral.equals(BigDecimal.ZERO);
            System.out.println(fechaDisfrute + " esDiaLaboral: " + esDiaLaboral);
            return esDiaLaboral;
        } catch (PersistenceException pe) {
            System.out.println("Error de persisl = !conteoDiaLaboral.equals(BigDecimal.ZERO);\n"
                    + "      tencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general. " + e);
            throw new Exception(e.toString());
        }
    }

}
