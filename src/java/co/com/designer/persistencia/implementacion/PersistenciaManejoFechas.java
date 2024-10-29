package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaManejoFechas;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.Date;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaManejoFechas implements IPersistenciaManejoFechas{

    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    
    public PersistenciaManejoFechas() {
        this.persisConexiones = new PersistenciaConexiones();
    }
    
    
    @Override
    public Date getDate(String fechaStr, String cadena) throws PersistenceException, NullPointerException, Exception {
        System.out.println(this.getClass().getName() + "." + "getDate" + "()");
        String consulta = "SELECT "
                + "TO_DATE(?, 'dd/mm/yyyy') "
                + "FROM DUAL ";
        Query query = null;
        Date fechaRegreso = null;
        try {
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaStr);
            fechaRegreso = (Date) (query.getSingleResult());
            System.out.println("getDate(): " + fechaRegreso);
            return fechaRegreso;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia en calculaFechaRegreso.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en calculaFechaRegreso");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general en calculaFechaRegreso. " + e);
            throw new Exception(e.toString());
        }
    }
    
    @Override
    public String nombreDia(int dia) {
        String retorno = "";
        switch (dia) {
            case 1:
                retorno = "DOM";
                break;
            case 2:
                retorno = "LUN";
                break;
            case 3:
                retorno = "MAR";
                break;
            case 4:
                retorno = "MIE";
                break;
            case 5:
                retorno = "JUE";
                break;
            case 6:
                retorno = "VIE";
                break;
            case 7:
                retorno = "SAB";
                break;
            default:
                retorno = "";
                break;
        }
        return retorno;
    }
    
    @Override
    public String getFechaSugerida(String fechaInicio, String dias, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaManejoFechas."+"getFechaSugerida(): "
                + "fechaInicio: "+fechaInicio
                + " dias: "+dias
                + " nitEmpresa: "+nitEmpresa
                + " cadena: "+ cadena
        );
        String resultado = null;
        String consulta = "SELECT TO_CHAR(TO_DATE(?, 'DD/MM/YYYY') + ?,'DD/MM/YYYY') "
                + "FROM DUAL";
        try {
            this.rolesBD = new PersistenciaPerfiles();
            this.cadenasKio = new PersistenciaCadenasKioskosApp();
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaInicio);
            query.setParameter(2, dias);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getFechaSugerida(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }
}
