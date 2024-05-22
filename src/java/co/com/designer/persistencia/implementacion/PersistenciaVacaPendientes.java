package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaVacaPendientes;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaVacaPendientes implements IPersistenciaVacaPendientes {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaConexionesKioskos persisConKiosko;

    public PersistenciaVacaPendientes() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persisConKiosko = new PersistenciaConexionesKioskos();
    }

    @Override
    public List<VwVacaPendientesEmpleados> getPeriodosPendientesEmpleado(String seudonimo, String nitEmpresa, String cadena) {
        List<VwVacaPendientesEmpleados> periodos = null;
        Query query = null;
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String consulta = "SELECT VW.RFVACACION \n"
                + ", KIOVACACIONES_PKG.DIASDISPOPER(VW.RFVACACION) DIASPENDIENTES \n"
                + ", VW.DIASPENDIENTES DIASPENDIENTESREALES \n"
                + ", VW.FINALCAUSACION "
                + ", VW.INICIALCAUSACION "
                + ", TO_CHAR(VW.INICIALCAUSACION, 'dd/mm/yyyy')||' a '||TO_CHAR(VW.FINALCAUSACION, 'dd/mm/yyyy') PERIODO \n"
                + "FROM VWVACAPENDIENTESEMPLEADOS VW, EMPLEADOS E \n"
                + "WHERE VW.EMPLEADO=E.SECUENCIA "
                + "AND DIASPENDIENTES > 0 \n"
                + "AND E.SECUENCIA = ? \n"
                + "AND VW.INICIALCAUSACION >= empleadocurrent_pkg.fechavigenciatipocontrato(e.secuencia, sysdate)";
        try {
            BigDecimal secuenciaEmpl = new BigDecimal(secEmpl);
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secuenciaEmpl);
            periodos = query.getResultList();
            return periodos;
        } catch (PersistenceException pe) {
            System.out.println("PersistenciaVacaPendientes" + ".getPeriodosPendientesEmpleado(): "+"Error-1: " + pe.toString());
//            throw pe;
            return null;
        } catch (NullPointerException npee) {
            System.out.println("PersistenciaVacaPendientes" + ".getPeriodosPendientesEmpleado(): Error-2: " + npee.toString());
            return null;
        } catch (Exception e) {
            System.out.println("PersistenciaVacaPendientes" + ".getPeriodosPendientesEmpleado(): Error-3: " + e.toString());
//            throw e;
            return null;
        }
    }

    @Override
    public List<VwVacaPendientesEmpleados> getPeriodoMasAntiguo(String seudonimo, String nitEmpresa, String cadena) {
        List<VwVacaPendientesEmpleados> periodo = null;
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String consulta = "select vw.rfvacacion rfvacaion \n"
                + ", to_char(vw.inicialcausacion, 'dd/mm/yyyy') || ' a ' || to_char(vw.finalcausacion, 'dd/mm/yyyy') periosidad \n"
                + "from VwVacaPendientesEmpleados vw \n"
                + "where vw.empleado = ? \n"
                + "and vw.inicialCausacion = ( \n"
                + " select min( vwi.inicialcausacion ) \n"
                + " from ( \n"
                + "  SELECT N.VACACION \n"
                + "  , SUM(N.DIAS) SUMADIAS \n"
                + "  FROM KIONOVEDADESSOLICI N, KIOSOLICIVACAS S, KIOESTADOSSOLICI E \n"
                + "  WHERE N.SECUENCIA = S.KIONOVEDADSOLICI \n"
                + "  AND S.SECUENCIA = E.KIOSOLICIVACA \n"
                + "  AND E.ESTADO IN ('GUARDADO', 'ENVIADO', 'AUTORIZADO', 'LIQUIDADO' ) \n"
                + "  AND S.EMPLEADO = ? \n"
                + "  AND E.SECUENCIA = (SELECT MAX(EI.SECUENCIA) \n"
                + "   FROM KIOESTADOSSOLICI EI \n"
                + "   WHERE EI.KIOSOLICIVACA = E.KIOSOLICIVACA) \n"
                + "  AND NOT EXISTS (SELECT 'x' \n"
                + "   FROM SOLUCIONESNODOS SN, SOLUCIONESFORMULAS SF, DETALLESNOVEDADESSISTEMA DNS \n"
                + "   WHERE SN.SECUENCIA = SF.solucionnodo \n"
                + "   AND SF.novedad = DNS.novedad \n"
                + "   AND DNS.novedadsistema=E.NOVEDADSISTEMA \n"
                + "   AND SN.empleado = ? )\n"
                + "  GROUP BY N.VACACION \n"
                + " ) t, VwVacaPendientesEmpleados VWI \n"
                + " where vwi.inicialCausacion >= empleadocurrent_pkg.fechatipocontrato(vwi.empleado, sysdate) \n"
                + " AND VWI.EMPLEADO = vw.empleado \n"
                + " AND (VWI.DIASPENDIENTES - NVL(T.SUMADIAS,0)) > 0 \n"
                + " AND VWI.rfvacacion = t.VACACION(+) \n"
                + ")";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, secEmpl);
            query.setParameter(3, secEmpl);
            periodo = query.getResultList();
        } catch (Exception e) {
            System.out.println("PersistenciaVacaPendientes" + ".consultarPeriodoMasAntiguo(): Error-1: " + e.toString());
        }
        return periodo;
    }

    @Override
    public BigDecimal getDiasPendPeriodoMasAntiguo(String seudonimo, String nitEmpresa, String cadena) {
        BigDecimal retorno = new BigDecimal(BigInteger.ZERO);
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String consulta = "select KIOVACACIONES_PKG.DIASDISPOPER(vw.rfVacacion) diaspendientes \n"
                + "from VwVacaPendientesEmpleados vw \n"
                + "where vw.empleado = ? \n"
                + "and vw.inicialCausacion = ( \n"
                + " select min( vwi.inicialcausacion ) \n"
                + " from ( \n"
                + "  SELECT N.VACACION \n"
                + "  , SUM(N.DIAS) SUMADIAS \n"
                + "  FROM KIONOVEDADESSOLICI N, KIOSOLICIVACAS S, KIOESTADOSSOLICI E \n"
                + "  WHERE N.SECUENCIA = S.KIONOVEDADSOLICI \n"
                + "  AND S.SECUENCIA = E.KIOSOLICIVACA \n"
                + "  AND E.ESTADO IN ('GUARDADO', 'ENVIADO', 'AUTORIZADO', 'LIQUIDADO' ) \n"
                + "  AND S.EMPLEADO = ? \n"
                + "  AND E.SECUENCIA = (SELECT MAX(EI.SECUENCIA) \n"
                + "   FROM KIOESTADOSSOLICI EI \n"
                + "   WHERE EI.KIOSOLICIVACA = E.KIOSOLICIVACA) \n"
                + "  AND NOT EXISTS (SELECT 'x' \n"
                + "   FROM SOLUCIONESNODOS SN, SOLUCIONESFORMULAS SF, DETALLESNOVEDADESSISTEMA DNS \n"
                + "   WHERE SN.SECUENCIA = SF.solucionnodo \n"
                + "   AND SF.novedad = DNS.novedad \n"
                + "   AND DNS.novedadsistema=E.NOVEDADSISTEMA \n"
                + "   AND SN.empleado = ? )\n"
                + "  GROUP BY N.VACACION \n"
                + " ) t, VwVacaPendientesEmpleados VWI \n"
                + " where vwi.inicialCausacion >= empleadocurrent_pkg.fechatipocontrato(vwi.empleado, sysdate) \n"
                + " AND VWI.EMPLEADO = vw.empleado \n"
                + " AND (VWI.DIASPENDIENTES - NVL(T.SUMADIAS,0)) > 0 \n"
                + " AND VWI.rfvacacion = t.VACACION(+) \n"
                + ")";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, secEmpl);
            query.setParameter(3, secEmpl);
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("PersistenciaVacaPendientes" + ".getDiasPendPeriodoMasAntiguo(): Error-1: " + e.toString());
        }
        return retorno;
    }

    @Override
    public BigDecimal getDiasVacacionesPeriodosCumplidos(String seudonimo, String nitEmpresa, String cadena) {
        BigDecimal retorno = new BigDecimal(BigInteger.ZERO);
        String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String consulta = "select "
                + "nvl(sum(v.diaspendientes), 0) diasPendientes "
                + "from VWVACAPENDIENTESEMPLEADOS v, empleados e "
                + "where e.secuencia=v.empleado "
                + "and inicialcausacion>=empleadocurrent_pkg.fechavigenciaTipoContrato(e.secuencia, sysdate) "
                + "and e.secuencia=? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            retorno = (BigDecimal) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("PersistenciaVacaPendientes" + ".getDiasPendPeriodoMasAntiguo(): Error-1: " + e.toString());
        }
        return retorno;
    }

    @Override
    public BigDecimal getDiasVacacionesSolicitados(BigDecimal documento, String nitEmpresa, String estado, String cadena) {
        System.out.println("PersistenciaVacaPendientes" + ".getDiasVacacionesSolicitados(): " + "Parametros: documento: " + documento + ", nitEmpresa: " + nitEmpresa + ", estado: " + estado + ", cadena: " + cadena);
        BigDecimal retorno = BigDecimal.ZERO;
//        String esquema = null;
        String consulta = "select "
                + "nvl(sum(kn.dias), 0) dias \n"
                + "from KioEstadosSolici e, KioSoliciVacas ks, KioNovedadesSolici kn, VwVacaPendientesEmpleados v \n"
                + "where e.kiosolicivaca = ks.secuencia \n"
                + "and ks.KIONOVEDADSOLICI = kn.secuencia \n"
                + "and kn.vacacion=v.RFVACACION \n"
                + "and ks.empleado = (select ei.secuencia \n"
                + "                  from empleados ei, personas pei, empresas em \n"
                + "                  where ei.persona=pei.secuencia \n"
                + "                  and ei.empresa=em.secuencia "
                + "                  and em.nit= ? \n"
                + "                  and pei.numerodocumento = ?) \n"
                + "and e.secuencia = (select max(ei.secuencia) \n"
                + " from KioEstadosSolici ei, kiosolicivacas ksi \n"
                + " where ei.kioSoliciVaca = ksi.secuencia \n"
                + " and ksi.secuencia=ks.secuencia \n"
                + ")";
        if (estado != null) {
            consulta += " and e.estado= ? ";
        }
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, documento);
            if (estado != null) {
                query.setParameter(3, estado);
            }
            retorno = (BigDecimal) query.getSingleResult();
            System.out.println("PersistenciaVacaPendientes" + ".getDiasVacacionesSolicitados(): " + "estado: " + estado + " retorno: " + retorno);
        } catch (Exception e) {
            System.out.println("PersistenciaVacaPendientes" + ".getDiasVacacionesSolicitados(): " + "Error: " + e.toString());
        }
        return retorno;
    }

    
    
    @Override
    public String getPeriodoVacas(String secEmpleado, String refVacas, String cadena, String nitEmpresa) {
        String periodo = null;
        String sqlQuery = "SELECT  "
                + "TO_CHAR(v.INICIALCAUSACION, 'dd/mm/yyyy')||' al '||TO_CHAR(v.FINALCAUSACION, 'dd/mm/yyyy') periocidad\n"
                + "FROM VwVacaPendientesEmpleados v\n"
                + "where v.empleado = ? "
                + "and v.rfvacacion = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, refVacas);
            periodo = query.getSingleResult().toString();
            System.out.println("PersistenciaVacaPendientes" + ".getPeriodoVacas(): " + "periodo: " + periodo);
        } catch (Exception e) {
            System.out.println("PersistenciaVacaPendientes" + ".getPeriodoVacas(): " + "Error: " + e.toString());
        }
        return periodo;
    }
}
