package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaSolucionesNodos;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaSolucionesNodos implements IPersistenciaSolucionesNodos {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaConexionesKioskos persisConKiosko;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaSolucionesNodos() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        persisConKiosko = new PersistenciaConexionesKioskos();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();

    }

    @Override
    public List getSaldoProvisiones(String empleado, String nit, String cadena) {

        try {
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(empleado, nit, cadena);
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select sn.fechapago, \n"
                    + "trim(replace(to_char(sn.SALDO,'$999G999G999G999G999G999'), ',','.')) valor, \n"
                    + "sn.unidades, cc.descripcion, cc.codigo \n"
                    + "from comprobantes c, procesos p, empleados e, solucionesnodos sn, cortesprocesos cp, conceptos cc \n"
                    + "where c.empleado = e.secuencia \n"
                    + "and cc.secuencia = sn.concepto \n"
                    + "and sn.empleado = e.secuencia \n"
                    + "and e.secuencia = ? \n"
                    + "and sn.corteproceso = cp.secuencia \n"
                    + "and c.secuencia = cp.comprobante \n"
                    + "and cp.proceso = p.secuencia \n"
                    + "and p.codigo = 11 \n"
                    + "and cc.codigo in (44001, 44002, 44003, 44004,99989,99988,99986,99987,99600,99601,99602,99603) \n"
                    + "and sn.ESTADO = 'CERRADO' \n"
                    + "AND sn.FECHAPAGO = cortesprocesos_pkg.CapturarAnteriorCorte(sn.EMPLEADO,11,sysdate) \n"
                    + "ORDER BY CC.descripcion";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            List provisiones = query.getResultList();
            return provisiones;
        } catch (Exception e) {
            System.out.println("getDatosEmpleadoXNit: Error: en algo de la base de datos. " + e.toString());
            throw e;
        }
    }

    @Override
    public Date getFechaUltimoPago(String seudonimo, String nitEmpresa, String cadena, String esquema) throws Exception {
        System.out.println("Parametros getFechaUltimoPago(): seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", cadena: " + cadena + ", esquema: " + esquema);
        BigDecimal res = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String consulta = "SELECT GREATEST("
                    + "CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1), "
                    + "NVL( CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 80), CORTESPROCESOS_PKG.CAPTURARCORTEPROCESO(?, 1)"
                    + ")) "
                    + "FROM DUAL ";
            Date fechaUltimoPago = null;
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, secEmpleado);
            query.setParameter(3, secEmpleado);
            fechaUltimoPago = (Date) (query.getSingleResult());
            return fechaUltimoPago;
        } catch (PersistenceException pe) {
            System.out.println("Error de persistencia.");
            throw new Exception(pe.toString());
        } catch (NullPointerException npee) {
            System.out.println("Nulo general");
            throw new Exception(npee.toString());
        } catch (Exception e) {
            System.out.println("Error general. " + e);
            throw new Exception(e.toString());
        }
    }
}
