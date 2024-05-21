package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaKioVacaciones_pkg;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioVacaciones_pkg implements IPersistenciaKioVacaciones_pkg {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaConexionesKioskos persisConKiosko;

    public PersistenciaKioVacaciones_pkg() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persisConKiosko = new PersistenciaConexionesKioskos();
    }

    @Override
    public BigDecimal verificaExistenciaSolicitud(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca, String cadena) throws Exception {
        System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Parametros: "
                + "seudonimo: " + seudonimo
                + "nitEmpresa: " + nitEmpresa
                + "fechaIniVaca: " + fechaIniVaca
                + "cadena: " + cadena
        );
        String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): secEmpleado: " + secEmpleado);

        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICAEXISTESOLICITUD(?, to_date(?,'DD/MM/YYYY') ) "
                + "FROM DUAL ";
        System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): consulta: " + consulta);
        BigDecimal conteo = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechaIniVaca);
            Object res = query.getSingleResult();
            if (res instanceof BigDecimal) {
                conteo = (BigDecimal) res;
                System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): conteo-1: " + conteo);
            } else {
                System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error: "
                        + "El conteo de la solicitud no es BigDecimal. " + res + " tipo: " + res.getClass().getName());
                throw new Exception("El conteo de la solicitud no es BigDecimal. " + res + " tipo: " + res.getClass().getName());
            }
        } catch (NullPointerException npe) {
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error: "
                    + "verificaExistenciaSolicitud: EntiyManager, query o consulta nulos.");
            throw new Exception("verificaExistenciaSolicitud: EntiyManager, query o consulta nulos.");
        }

        System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): conteo-2: " + conteo);
        return conteo;
    }

    @Override
    public BigDecimal consultaTraslapamientos(
            String seudonimo,
            String nitEmpresa,
            String fechaIniVaca,
            String fechaFinVaca, String cadena) throws PersistenceException, NullPointerException, Exception {
        System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Parametros: "
                + "seudonimo: " + seudonimo
                + "nitEmpresa: " + nitEmpresa
                + "fechaIniVaca: " + fechaIniVaca
                + "fechaFinVaca: " + fechaFinVaca
                + "cadena: " + cadena
        );
        String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String consulta = "SELECT "
                + "KIOVACACIONES_PKG.VERIFICARTRASLAPAMIENTO(?, to_date(?,'DD/MM/YYYY') , to_date(?,'DD/MM/YYYY') ) "
                + "FROM DUAL ";
        BigDecimal contTras = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechaFinVaca);
            query.setParameter(3, fechaFinVaca);
            contTras = (BigDecimal) (query.getSingleResult());
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): contTras: " + contTras);
            return contTras;
        } catch (PersistenceException pe) {
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error-1: " + pe.getMessage());
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general en consultaTraslapamientos");
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error-2: " + npee.getMessage());
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("PersistenciaKioVacaciones_pkg" + ".verificaExistenciaSolicitud(): Error-3: " + e.getMessage());
            throw new Exception(e.toString());
        }
    }
}
