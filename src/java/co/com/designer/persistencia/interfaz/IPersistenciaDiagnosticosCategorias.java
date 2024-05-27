package co.com.designer.persistencia.interfaz;

import co.com.designer.kiosko.entidades.DiagnosticosCategorias;
import java.util.List;

/**
 *
 * @author Edwin Hastamorir
 * @author Wilmer Uribe 
 * @author Mateo Coronado
 */
public interface IPersistenciaDiagnosticosCategorias {
    public List<DiagnosticosCategorias> getDiagnosticosCategorias(String nitEmpresa, String cadena);
    public List getDiagnosticosCategoriasNativo(String nitEmpresa, String cadena);
}
