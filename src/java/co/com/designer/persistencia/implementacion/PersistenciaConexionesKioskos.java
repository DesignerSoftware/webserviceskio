package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaPersonas;
import java.math.BigDecimal;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaConexionesKioskos implements IPersistenciaConexionesKioskos {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaPersonas persisPersonas;

    public PersistenciaConexionesKioskos() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public BigDecimal validaUsuario(String usuario, String pass, String bd) {
        BigDecimal res = null;
        try {
            this.rolesBD.setearPerfil();
            String sqlQuery = "select count(*) "
                    + "from conexioneskioskos ck, personas per "
                    + "where per.secuencia = ck.persona "
                    + "and per.numerodocumento = ? "
                    + "and generales_pkg.decrypt(ck.pwd) = ? ";
            Query query = this.persistenciaConexiones.getEntityManager().createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, pass);
            res = (BigDecimal) query.getSingleResult();
//            System.out.println("tipo res: "+res.getClass().getName());
//            System.out.println("res: "+res);
        } catch (Exception ex) {
            System.out.println("ex: " + ex);
            res = BigDecimal.ZERO;
        }
//        return String.valueOf(res);
        return res;
    }

    public int updateFechasConexionesKioskos(String usuario, String nitEmpresa, String fechadesde,
            String fechahasta, boolean enviocorreo, String dirigidoa, String cadena) {
        int conteo = 0;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS "
                    + " SET FECHADESDE=TO_DATE(?, 'yyyy-mm-dd'), FECHAHASTA=TO_DATE(?, 'yyyy-mm-dd'), ENVIOCORREO=?, DIRIGIDOA=? "
                    + " WHERE SEUDONIMO=? "
                    + " AND NITEMPRESA=?";
            String envioc = (enviocorreo == true ? "S" : "N");
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, fechadesde);
            query.setParameter(2, fechahasta);
            query.setParameter(3, envioc);
            query.setParameter(4, dirigidoa);
            query.setParameter(5, usuario);
            query.setParameter(6, nitEmpresa);

            conteo = query.executeUpdate();
            System.out.println("update conexioneskioskos: " + conteo);
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
        }
        return conteo;
    }

    @Override
    public int updateClaveConexionesKioskos(String usuario, String nitEmpresa, String clave, String cadena) {
        int conteo = 0;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "UPDATE CONEXIONESKIOSKOS "
                    + " SET PWD=GENERALES_PKG.ENCRYPT(?)"
                    + " WHERE SEUDONIMO=? "
                    + " AND NITEMPRESA=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, clave);
            query.setParameter(2, usuario);
            query.setParameter(3, nitEmpresa);

            conteo = query.executeUpdate();
            System.out.println("update conexioneskioskos: " + conteo);
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
            conteo = 0;
        }
        return conteo;
    }

    @Override
    public String getSecuenciaEmplPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaConexionesKioskos.getSecuenciaEmplPorSeudonimo(): Parametros: "
                +"seudonimo: " + seudonimo 
                + " , nitEmpresa: " + nitEmpresa 
                + " , cadena: " + cadena);
        String secuencia = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT E.SECUENCIA SECUENCIAEMPLEADO "
                    + "FROM EMPLEADOS E, CONEXIONESKIOSKOS CK "
                    + "WHERE CK.EMPLEADO=E.SECUENCIA "
                    + "AND CK.SEUDONIMO= ? "
                    + "AND CK.NITEMPRESA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
            System.out.println("secuencia: " + secuencia);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecuenciaEmplPorSeudonimo: " + e.getMessage());
        }
        return secuencia;
    }

    /**
     * Metodo para validar si el correo es diferente al seudonimo. Creado el
     * 13/10/2021
     *
     * @param usuario
     * @param nitEmpresa
     * @param cadena
     * @return String Respuesta en formato boolean
     */
    @Override
    public boolean validarSeudonimoCorreo(String usuario, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaConexionesKioskos" + ".validarSeudonimoCorreo(): " + "Parametros: [ seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa + " cadena: " + cadena + " ]");
        boolean resultado = false;
        String sqlQuery = "select count(*) "
                + "from conexioneskioskos ck, personas p "
                + "where ck.persona = p.secuencia "
                + "and lower(ck.seudonimo) = lower(p.email) "
                + "and lower(ck.seudonimo) = ? "
                + "and ck.nitempresa = ? ";
        System.out.println("PersistenciaConexionesKioskos" + ".validarSeudonimoCorreo(): " + "Query: " + sqlQuery);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, usuario);
            query.setParameter(2, nitEmpresa);
            BigDecimal retorno = (BigDecimal) query.getSingleResult();
            Integer instancia = retorno.intValueExact();
            System.out.println("PersistenciaConexionesKioskos" + ".validarSeudonimoCorreo(): " + "retorno: " + retorno);
            System.out.println("PersistenciaConexionesKioskos" + ".validarSeudonimoCorreo(): " + "instancia: " + instancia);
            /*if (instancia > 0) {
                resultado = true;
            }
             */
            resultado = instancia > 0;
        } catch (Exception e) {
            System.out.println("PersistenciaConexionesKioskos" + ".validarSeudonimoCorreo(): " + "Error: " + e.getMessage());
        }
        return resultado;
    }

    @Override
    public BigDecimal getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaConexionesKioskos"+".getDocumentoPorSeudonimo(): "+"Parametros: "
                +"seudonimo: " + seudonimo 
                + " nitEmpresa: " + nitEmpresa 
                + " cadena: " + cadena);
        BigDecimal documento = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO "
                    + "FROM PERSONAS P, CONEXIONESKIOSKOS CK "
                    + "WHERE CK.PERSONA=P.SECUENCIA "
                    + "AND lower(CK.SEUDONIMO)=lower(?) "
                    + "AND CK.NITEMPRESA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento = new BigDecimal(query.getSingleResult().toString());
            System.out.println("documento: " + documento);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getDocumentoPorSeudonimo: " + e.getMessage());
        }
        return documento;
    }

    @Override
    public BigDecimal getPersonaPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getPersonaPorSeudonimo() seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        BigDecimal documento = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.SECUENCIA FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND lower(CK.SEUDONIMO)=lower( ? ) AND CK.NITEMPRESA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento = new BigDecimal(query.getSingleResult().toString());
            System.out.println("documento: " + documento);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getPersonaPorSeudonimo: " + e.getMessage());
            throw e;
        }
        return documento;
    }

    // 13/10/2021
    /**
     * metodo para validar si el correo es diferente al seudonumo
     *
     * @param usuario
     * @param nitEmpresa
     * @param cadena
     * @return String Respuesta en formato boolean
     */
    @Override
    public String updateCorreoSeudonimo(String usuario, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaConexionesKioskos" + ".validarSeudonimoCorreo(): " + "Parametros: [ seudonimo: " + usuario + ", nitEmpresa: " + nitEmpresa + " cadena: " + cadena + " ]");
        this.persisPersonas = new PersistenciaPersonas();
        BigDecimal documento = null;
        String correo = null;
        String resultado = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            documento = getDocumentoPorSeudonimo(usuario, nitEmpresa, cadena);
            correo = this.persisPersonas.consultarCorreoPersonaEmpresa(documento.toString(), nitEmpresa, cadena);
            String sqlQuery = "update conexioneskioskos set seudonimo = ? "
                    + "where persona = (select secuencia "
                    + "from personas where numerodocumento = ? ) "
                    + "and nitempresa = ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            System.out.println("correoPesona " + correo);
            System.out.println("documento " + documento);
            query.setParameter(1, correo);
            query.setParameter(2, documento);
            query.setParameter(3, nitEmpresa);
            query.executeUpdate();
            //resultado = query.getSingleResult().toString();
            //System.out.println("Resultado " + resultado);
        } catch (Exception e) {
            System.out.println("Error : " + ConexionesKioskos.class.getName() + ".validarUsuarioRegistrado " + e.getMessage());
        }
        return correo;
    }
}
