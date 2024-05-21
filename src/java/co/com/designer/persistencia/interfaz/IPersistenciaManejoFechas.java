package co.com.designer.persistencia.interfaz;

import java.util.Date;
import javax.persistence.PersistenceException;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaManejoFechas {
    
    public Date getDate(String fechaStr, String cadena) throws PersistenceException, NullPointerException, Exception;
    public String nombreDia(int dia);
}
