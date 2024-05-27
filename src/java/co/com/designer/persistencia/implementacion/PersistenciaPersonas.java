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
        System.out.println("PersistenciaPersonas" + ".consultarCorreoPersonaEmpresa(): Parametros: "
                + "documento: " + documento
                + ", nitEmpresa: " + nitEmpresa
                + ", cadena: " + cadena);
        String datos = null;
        String sqlQuery = "SELECT lower(P.EMAIL) email "
                + "FROM EMPLEADOS e, Empresas em, personas p "
                + "WHERE e.empresa = em.secuencia "
                + "and p.secuencia=e.persona "
                + "AND p.numerodocumento = ? "
                + "AND em.nit = ? "
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
                datos = query.getSingleResult().toString();
            } catch (Exception ex) {
                System.out.println("Error: " + "PersistenciaPersonas" + ".consultarCorreoPersonaEmpresa(): " + ex.toString());
                System.out.println("Error: " + "PersistenciaPersonas" + ".consultarCorreoPersonaEmpresa(): \n" + sqlQuery);
            }
        }
        return datos;
    }

    @Override
    public String getApellidoNombreXSecPer(String secPersona, String nitEmpresa, String cadena, String esquema) {
        System.out.println("PersistenciaPersonas" + ".getApellidoNombreXSecPer(): " + "Parametros: secPersona: " + secPersona + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
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
            System.out.println("PersistenciaPersonas" + ".getApellidoNombreXSecPer(): " + "Error: " + e.getMessage());
        }
        return nombre;
    }

    @Override
    public String getApellidoNombreXsecEmpl(String secEmpl, String nitEmpresa, String cadena, String esquemaP) {
        System.out.println("PersistenciaPersonas" + ".getApellidoNombreXsecEmpl(): " + "Parametros: "
                + "secEmpl: " + secEmpl
                + " nitEmpresa: " + nitEmpresa
                + " cadena: " + cadena
        );
        String nombre = null;
        String sqlQuery = "SELECT UPPER(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE) NOMBRE "
                + " FROM PERSONAS P, EMPLEADOS EMPL "
                + " WHERE P.SECUENCIA=EMPL.PERSONA "
                + " AND EMPL.SECUENCIA=?";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            nombre = (String) query.getSingleResult();
            System.out.println("PersistenciaPersonas" + ".getApellidoNombreXsecEmpl(): " + "nombre: " + nombre);
        } catch (Exception e) {
            System.out.println("PersistenciaPersonas" + ".getApellidoNombreXsecEmpl(): " + "Error: " + e.toString());
        }
        return nombre;
    }

    @Override
    public String getCorreoConexioneskioskos(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaPersonas" + ".getCorreoConexioneskioskos(): " + "Parametros: "
                + "seudonimo: " + seudonimo
                + ", empresa: " + nitEmpresa
                + ", cadena: " + cadena);
        String correo = null;
        String sqlQuery = "SELECT P.EMAIL "
                + "FROM PERSONAS P, conexioneskioskos ck "
                + "WHERE p.secuencia=ck.persona "
                + "AND lower(ck.seudonimo)=? "
                + "AND ck.nitempresa=?";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaPersonas" + ".getCorreoConexioneskioskos(): " + "Error-1: " + e.toString());
        }
        return correo;
    }

    @Override
    public String getCorreoPorEmpleado(String secEmpleado, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaPersonas" + ".getCorreoPorEmpleado(): " + "Parametros: "
                + "seudonimo: " + secEmpleado
                + ", empresa: " + nitEmpresa
                + ", cadena: " + cadena);
        String correo = null;
        String sqlQuery = "SELECT P.EMAIL "
                + "FROM Personas p, Empleados empl " + ", Empresas em "
                + "WHERE p.secuencia = empl.persona "
                + "AND em.secuencia = empl.empresa "
                + "AND empl.secuencia = ? "
                + "AND ck.nitempresa = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, nitEmpresa);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaPersonas" + ".getCorreoPorEmpleado(): " + "Error-1: " + e.toString());
        }
        return correo;
    }

    @Override
    public String getCorreoPorPersona(String secPersona, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaPersonas" + ".getCorreoPorPersona(): " + "Parametros: "
                + "seudonimo: " + secPersona
                + ", empresa: " + nitEmpresa
                + ", cadena: " + cadena);
        String correo = null;
        String sqlQuery = "SELECT P.EMAIL "
                + "FROM Personas p "
                + "WHERE p.secuencia = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secPersona);
            correo = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaPersonas" + ".getCorreoPorPersona(): " + "Error-1: " + e.toString());
        }
        return correo;
    }
}
