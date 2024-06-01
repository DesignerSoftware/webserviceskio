package co.com.designer.persistencia.interfaz;

import javax.ejb.Local;

/**
 *
 * @author usuario
 */
@Local
public interface IPersistenciaPerfiles {
//    public void setearPerfil();
//    public void setearPerfil(String cadena);
    public void setearPerfil(String esquema, String cadenaPersistencia);
}
