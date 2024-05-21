package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaManejoFechas;
import java.util.Date;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaManejoFechas implements IPersistenciaManejoFechas{

    private IPersistenciaConexiones persisConexiones;
    
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
}
