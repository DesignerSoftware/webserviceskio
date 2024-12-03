package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaKioPersonalizaciones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioPersonalizaciones implements IPersistenciaKioPersonalizaciones {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaKioPersonalizaciones() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persistenciaConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public List getCorreosComiteConvivencia(String nit, String cadena) throws Exception {
        try {
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KP.EMAILCONTACTO "
                    + "FROM KIOPERSONALIZACIONES KP, EMPRESAS EM "
                    + "WHERE KP.TIPOCONTACTO = 'COMITE_CONVIVENCIA' "
                    + "AND KP.EMPRESA = EM.SECUENCIA "
                    + "AND EM.NIT = ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            //EMPRESA
            query.setParameter(1, nit); 
            List correosComiteConvivencia = query.getResultList();
            return correosComiteConvivencia;
        } catch (Exception e) {
            System.out.println("getCorreosComiteConvivencia: Error: en algo de la base de datos. " + e.toString());
            throw e;
        }
    }

    @Override
    public String getCorreoContacto(String nitEmpresa, String cadena) {
        System.out.println("PersistenciaKioPersonalizaciones." + "getCorreoContacto()");

        String emailSoporte = "";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT emailContacto "
                    + "FROM KioPersonalizaciones kp, Empresas em "
                    + "WHERE kp.empresa=em.secuencia "
                    + "and em.nit= ?  "
                    + "and kp.tipoContacto='NOMINA' "
                    + "AND ROWNUM<=1 ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            emailSoporte = query.getSingleResult().toString();
            System.out.println("Email soporte: " + emailSoporte);
        } catch (Exception e) {
            System.out.println("Error: getCorreoSoporteKiosco: " + e.getMessage());
        }
        return emailSoporte;
    }

    @Override
    public List getDatosContactoKioscoNomina(String nit, String cadena, String esquemaP) throws Exception {

        try {
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT K.NOMBRECONTACTO NOMBRE, K.EMAILCONTACTO EMAIL, TELEFONOCONTACTO TELEFONO "
                    + "FROM KIOPERSONALIZACIONES K, EMPRESAS EM "
                    + "WHERE K.EMPRESA=EM.SECUENCIA "
                    + "AND K.TIPOCONTACTO = 'NOMINA' "
                    + "AND EM.NIT= ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            //EMPRESA
            query.setParameter(1, nit); 
            List correosNomina = query.getResultList();
            return correosNomina;
        } catch (Exception e) {
            System.out.println("Error: " + "PersistenciaKioPersonalizaciones." + "getDatosContactoKioscoNomina(): "
                    + "Error: en algo de la base de datos. " + e.toString());
            throw e;
        }
    }

    @Override
    public List getDatosContactoKiosco(String nit, String cadena, String esquemaP) throws Exception {

        try {
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT K.NOMBRECONTACTO NOMBRE, K.EMAILCONTACTO EMAIL, TELEFONOCONTACTO TELEFONO "
                    + "FROM KIOPERSONALIZACIONES K, EMPRESAS EM "
                    + "WHERE K.EMPRESA=EM.SECUENCIA "
                    + "AND EM.NIT= ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nit); //EMPRESA
            List correosContacto = query.getResultList();
            return correosContacto;
        } catch (Exception e) {
            System.out.println("Error: " + "PersistenciaKioPersonalizaciones." + "getDatosContactoKiosco(): "
                    + "Error: en algo de la base de datos. " + e.toString());
            throw e;
        }
    }

    @Override
    public List getCorreosContactosNomina(String nit, String cadena, String esquemaP) throws Exception {
        System.out.println("PersistenciaKioPersonalizacioens." + "getCorreosContactosNomina(): " + "Parametros: "
                + "nit: " + nit
                + " cadena: " + cadena
                + " esquemaP: " + esquemaP
        );
        String sqlQuery = "SELECT K.EMAILCONTACTO EMAIL "
                + "FROM KIOPERSONALIZACIONES K, EMPRESAS EM "
                + "WHERE K.EMPRESA=EM.SECUENCIA "
                + "AND K.TIPOCONTACTO = 'NOMINA' "
                + "AND EM.NIT= ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            //EMPRESA
            query.setParameter(1, nit); 
            List correosNomina = query.getResultList();
            return correosNomina;
        } catch (Exception e) {
            System.out.println("Error: " + "PersistenciaKioPersonalizaciones." + "getDatosContactoKioscoNomina(): "
                    + "Error: en algo de la base de datos. " + e.toString());
            throw e;
        }
    }
}
