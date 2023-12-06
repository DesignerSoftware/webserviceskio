package co.com.designer.persistencia.interfaz;

import java.math.BigDecimal;

/**
 *
 * @author Edwin Hastamorir
 */
public interface IPersistenciaConexionesKioskos {
    public BigDecimal validaUsuario(String usuario, String pass, String bd);
    public int updateFechasConexionesKioskos(String usuario, String nitEmpresa, String fechadesde,
            String fechahasta, boolean enviocorreo, String dirigidoa, String cadena);
    public int updateClaveConexionesKioskos(String usuario, String nitEmpresa, String clave, String cadena);
    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena);
    public boolean validarSeudonimoCorreo(String usuario, String nitEmpresa, String cadena);
    public BigDecimal getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena);
    public BigDecimal getPersonaPorSeudonimo(String seudonimo, String nitEmpresa, String cadena);
    public String updateCorreoSeudonimo(String usuario, String nitEmpresa, String cadena);
}
