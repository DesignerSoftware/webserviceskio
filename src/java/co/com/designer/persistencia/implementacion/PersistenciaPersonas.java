package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaPersonas;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaPersonas implements IPersistenciaPersonas {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaPersonas() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persistenciaConexiones = new PersistenciaConexiones();
    }

    @Override
    public String consultarCorreoPersonaEmpresa(String documento, String nitEmpresa, String cadena) {
        System.out.println("Parametros consultarCorreoPersonaEmpresa(): documento: " + documento + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String datos = null;
        String sqlQuery = "SELECT lower(P.EMAIL) email "
                + "FROM EMPLEADOS e, Empresas em, personas p "
                + "WHERE e.empresa = em.secuencia "
                + "and p.secuencia=e.persona "
                + "AND p.numerodocumento = ? "
                + "AND em.nit = ? "
                //+ "AND (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'ACTIVO' "
                //+ "OR EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) = 'PENSIONADO')"
                + "AND (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(e.secuencia, SYSDATE) IN ('ACTIVO','PENSIONADO') )";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);
            datos = query.getSingleResult().toString();
            if (datos == null) {

            }
        } catch (Exception e) {
            System.out.println("Error: " + "PersistenciaPersonas" + ".consultarCorreoPersonaEmpresa(): " + e.getMessage());
            System.out.println("PersistenciaPersonas" + ".consultarCorreoPersonaEmpresa(): " + "consultado KioAutorizadores");
            sqlQuery = "SELECT lower(P.EMAIL) email "
                    + "FROM KIOAUTORIZADORES e, Personas p "
                    + "WHERE P.SECUENCIA=E.PERSONA "
                    + "AND P.NUMERODOCUMENTO = ? "
                    + "GROUP BY LOWER(P.EMAIL) ";
            try {
                String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
                this.rolesBD.setearPerfil(esquema, cadena);
                Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
                query.setParameter(1, documento);
//                query.setParameter(2, nitEmpresa);
                datos = query.getSingleResult().toString();
            } catch (Exception ex) {
                System.out.println("Error: " + "PersistenciaPersonas" + ".consultarCorreoPersonaEmpresa(): " + ex.getMessage());
                System.out.println("Error: " + "PersistenciaPersonas" + ".consultarCorreoPersonaEmpresa(): \n" + sqlQuery);
                ex.printStackTrace();
            }
        }
        return datos;
    }

    @Override
    public String getApellidoNombreXSecPer(String secPersona, String nitEmpresa, String cadena, String esquema) {
        System.out.println("PersistenciaPersonas" + ".getApellidoNombreXSecPer(): "+"Parametros: secPersona: " + secPersona + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String nombre = null;
        String sqlQuery;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            sqlQuery = "SELECT PRIMERAPELLIDO||' '||SEGUNDOAPELLIDO||' '||NOMBRE nombreCompleto "
                    + "FROM PERSONAS "
                    + "WHERE SECUENCIA= ? ";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPersona);
            nombre = query.getSingleResult().toString();
            System.out.println("Nombre Autorizador vacaciones: " + nombre);
        } catch (Exception e) {
            System.out.println("PersistenciaPersonas" + ".getApellidoNombreXSecPer(): " + "Error: "+e.getMessage());
        }
        return nombre;
    }
    
    public String getApellidoNombreXsecEmpl(String secEmpl, String nitEmpresa, String cadena, String esquema) {
        System.out.println("getApellidoNombreXsecEmpl()");
        String nombre = null;
        //String esquema = getEsquema(nitEmpresa, cadena);
        this.rolesBD.setearPerfil(esquema, cadena);
        try {
            String sqlQuery = "SELECT UPPER(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE) NOMBRE "
                    + " FROM PERSONAS P, EMPLEADOS EMPL "
                    + " WHERE P.SECUENCIA=EMPL.PERSONA "
                    + " AND EMPL.SECUENCIA=?";
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            nombre = (String) query.getSingleResult();
            System.out.println("Resultado getApellidoNombreXsecEmpl(): " + nombre);
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".getApellidoNombreXsecEmpl(): " + e);
        }
        return nombre;
    }
}
