package co.com.designer.persistencia.interfaz;

/**
 *
 * @author Edwin Hastamorir
 * @author Wilmer Uribe
 * @author Mateo Coronado
 */
public interface IPersistenciaTiposAusentismos {

    /**
     * Método que permite obtener la secuencia del tipo de ausentismo según 
     * la causa que esté relacionada.
     * Se espera que 
     * 2 - PERMISOS
     * 6 - LICENCIA
     * Sin embargo, esto puede cambiar, de acuerdo a la parametrización de la 
     * base de datos.
     * 
     * @param codigoCausa
     * @param nitEmpresa
     * @param cadena
     * @param esquemaP
     * @return Secuencia del tipo de ausentismo
     */
    public String getSecuenciaTipoAusentismo(String codigoCausa, String nitEmpresa, String cadena, String esquemaP);
}
