package co.com.designer.persistencia.interfaz;

/**
 *
 * @author Edwin Hastamorir
 * @author Wilmer Uribe
 * @author Mateo Coronado
 */
public interface IPersistenciaCausasAusentismos {
    public String getCausaOrigenIncapacidad(String causa, String nitEmpresa, String cadena);
    public String getSecuenciaCausaAusentismo(String codCausa, String nitEmpresa, String cadena);
}
