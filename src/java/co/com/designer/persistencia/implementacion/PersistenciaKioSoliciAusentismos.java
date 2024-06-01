package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.IntervinientesSolAusent;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaKioSoliciAusentismos;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioSoliciAusentismos implements IPersistenciaKioSoliciAusentismos {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaConexionesKioskos persisConKiosko;

    public PersistenciaKioSoliciAusentismos() {
        this.rolesBD = new PersistenciaPerfiles();
        this.persisConexiones = new PersistenciaConexiones();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public List getSolicitudesPorEstado(String nitEmpresa, String cadena, String secEmpl, String estado) {
        List resultado = null;
        String consulta = "select \n"
                + "to_char(KSA.fechageneracion, 'dd/mm/yyyy') fechacreacion, \n"
                + "TO_CHAR(KSA.FECHAINICIO,'DD/MM/YYYY' ) INICIALAUSENT, \n"
                + "KSA.dias dias, \n"
                + "to_char(KES.fechaprocesamiento,'dd/mm/yyyy') fechaprocesamiento, \n"
                + "KES.estado, \n"
                + "TO_CHAR(KSA.FECHAFIN,'DD/MM/YYYY') FECHAREGRESO, \n"
                + "KES.MOTIVOPROCESA motivoprocesa, \n"
                + "(SELECT DESCRIPCION FROM CAUSASAUSENTISMOS CA WHERE KSA.CAUSAREPORTADA = CA.SECUENCIA) CAUSA, \n"
                + "(SELECT DESCRIPCION FROM TIPOSAUSENTISMOS TI WHERE KNSA.TIPOAUSENTISMO = TI.SECUENCIA), \n"
                + "(SELECT DESCRIPCION FROM CLASESAUSENTISMOS CA WHERE KNSA.CLASEAUSENTISMO = CA.SECUENCIA), \n"
                + "(SELECT CODIGO FROM DIAGNOSTICOSCATEGORIAS DC WHERE KNSA.DIAGNOSTICOCATEGORIA = DC.SECUENCIA), \n"
                + "(SELECT DESCRIPCION FROM DIAGNOSTICOSCATEGORIAS DC WHERE KNSA.DIAGNOSTICOCATEGORIA = DC.SECUENCIA), \n"
                + "DECODE(KNSA.KIONOVEDADPRORROGA, null, 'NO', 'SI'), \n"
                + "DECODE(KES.PERSONAEJECUTA, null, \n"
                + "(select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido \n"
                + "from personas pei, empleados ei where ei.persona=pei.secuencia and ei.secuencia=KSA.empleadojefe), \n"
                + "(select pei.nombre||' '||pei.primerapellido||' '||pei.segundoapellido from personas pei \n"
                + "where pei.secuencia=KSA.AUTORIZADOR) \n"
                + ") empleadoejecuta, \n"
                + "KES.secuencia secuencia, \n"
                + "KSA.NOMBREANEXO ANEXO \n"
                + "from KIOESTADOSSOLICIAUSENT KES, \n"
                + "KIOSOLICIAUSENTISMOS KSA, \n"
                + "KIONOVEDADESSOLICIAUSENT  KNSA \n"
                + "where \n"
                + "KSA.SECUENCIA = KES.KIOSOLICIAUSENTISMO \n"
                + "AND KSA.SECUENCIA=KNSA.KIOSOLICIAUSENTISMO \n"
                + "AND KNSA.FECHAINICIALAUSENTISMO = (select MIN(ei.FECHAINICIALAUSENTISMO) \n"
                + "    from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                + "    where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                + "    and ksi.secuencia=KSA.secuencia) \n"
                + "and KES.FECHAPROCESAMIENTO = (select MAX(ei.FECHAPROCESAMIENTO) \n"
                + "from KIOESTADOSSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                + "where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                + "and ksi.secuencia=KSA.secuencia) \n"
                + "AND (KES.FECHAPROCESAMIENTO = (SELECT MAX(t3.FECHAPROCESAMIENTO) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                + "WHERE ((t4.SECUENCIA = KSA.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO)))) \n"
                + "and KSA.empleado = ? \n"
                + "and KES.estado = ? \n"
                + "order by KES.fechaProcesamiento DESC";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpl);
            query.setParameter(2, estado);
            resultado = query.getResultList();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSolicitudesPorEstado(): " + "Error-1: " + e.toString());
            return null;
        }
    }

    @Override
    public List getSolicitudesSinProcesarPorJefe(String nitEmpresa, String cadena, String estado, String secuenciaJefe) {
        List resultado = null;
        String consulta = "SELECT \n"
                + "empl.codigoempleado documento, \n"
                + "REPLACE(TRIM(p.primerapellido)||' '||TRIM(p.segundoapellido)||' '||TRIM(p.nombre), '  ', ' ') nombre, \n"
                + "kes.secuencia, \n"
                + "TO_CHAR(kes.fechaprocesamiento, 'DD/MM/YYYY') solicitud, \n"
                + "TO_CHAR(ksa.fechainicio,'DD/MM/YYYY' ) inicialausent, \n"
                + "TO_CHAR(kes.fechaprocesamiento, 'DD/MM/YYYY') fechaultmodif, \n"
                + "kes.estado, \n"
                + "kes.motivoprocesa, \n"
                + "kes.soausentismo, \n"
                + "TO_CHAR(ksa.fechafin,'DD/MM/YYYY') fecharegreso, \n"
                + "(SELECT descripcion FROM CausasAusentismos ca WHERE ksa.causareportada = ca.secuencia) causa, \n"
                + "ksa.dias,\n"
                + "(SELECT descripcion FROM TiposAusentismos ti WHERE knsa.tipoausentismo = ti.secuencia) tipo, \n"
                + "(SELECT descripcion FROM ClasesAusentismos ca WHERE knsa.claseausentismo = ca.secuencia) clase, \n"
                + "DECODE(knsa.kionovedadprorroga, NULL, 'no', 'si'), \n"
                + "(SELECT dc.codigo FROM DiagnosticosCategorias dc WHER dc.secuencia = knsa.diagnosticocategoria) codigo, \n"
                + "(SELECT dc.descripcion FROM DiagnosticosCategorias dc WHER dc.secuencia = knsa.diagnosticocategoria) descripcion, \n"
                + "(select per.primerapellido||' '||per.segundoapellido||' '||per.nombre \n"
                + " FROM personas per, empleados empli \n"
                + " WHERE empli.persona=per.secuencia \n"
                + " AND empli.secuencia=jefe.secuencia) empleadojefe, \n"
                + "ksa.observacion obsercavion, \n"
                + "knsa.fechafinpago fechapago, \n"
                + "kes.secuencia secuencia, \n"
                + "ksa.nombreanexo anexo, \n"
                + "p.numerodocumento numdocumento \n"
                + "FROM KioSoliciAusentismos ksa \n"
                + ", Empleados empl \n"
                + ", Personas p \n"
                + ", Empresas em \n"
                + ", KioEstadosSoliciAusent kes \n"
                + ", KioNovedadesSoliciAusent knsa \n"
                + ", Empleados jefe \n"
                + "WHERE \n"
                + "empl.secuencia = ksa.empleado \n"
                + "AND empl.persona = p.secuencia \n"
                + "AND empl.empresa = em.secuencia \n"
                + "AND ksa.secuencia = kes.kiosoliciausentismo  \n"
                + "AND ksa.secuencia=knsa.kiosoliciausentismo \n"
                + "AND ksa.empleadojefe=jefe.secuencia \n"
                + "AND knsa.fechainicialausentismo = (SELECT MIN(ei.fechainicialausentismo) \n"
                + "  FROM kionovedadessoliciausent ei \n"
                + "  WHERE ei.kiosoliciausentismo = ksa.secuencia) \n"
                + "AND kes.fechaprocesamiento = (SELECT MAX(ei.fechaprocesamiento) \n"
                + "  FROM kioestadossoliciausent ei \n"
                + "  WHERE ei.kiosoliciausentismo = ksa.secuencia) \n"
                + "AND em.nit = ? \n"
                + "AND kes.estado = ? \n"
                + "AND ksa.empleadojefe = ? \n"
                + "ORDER BY kes.fechaprocesamiento DESC";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, estado);
            query.setParameter(3, secuenciaJefe);
            resultado = query.getResultList();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSolicitudesSinProcesarPorJefe(): " + "Error-1: " + e.toString());
            return null;
        }
    }

    @Override
    public List getSolicitudesSinProcesarPorAutorizador(String nitEmpresa, String cadena, String estado, String secAutorizador) {
        System.out.println("PersistenciaKioSoliciAusentismos" + ".getSolicitudesSinProcesarPorAutorizador(): " + "Parametros: "
                + "nitEmpresa: " + nitEmpresa
                + " cadena: " + cadena
                + " estado: " + estado
                + " secAutorizador: " + secAutorizador
        );
        List resultado = null;
        String consulta = "SELECT \n"
                + "empl.codigoempleado documento, \n"
                + "REPLACE(TRIM(p.primerapellido||' '||p.segundoapellido||' '||p.nombre), '  ', ' ') nombre, \n"
                + "kes.secuencia, \n"
                + "TO_CHAR(ksa.fechageneracion, 'DD/MM/YYYY') solicitud, \n"
                + "TO_CHAR(knsa.fechainicialausentismo,'DD/MM/YYYY' ) inicialausent, \n"
                + "TO_CHAR(kes.fechaprocesamiento, 'DD/MM/YYYY') fechaultmodif, \n"
                + "kes.estado, \n"
                + "kes.motivoprocesa, \n"
                + "kes.soausentismo, \n"
                + "TO_CHAR(ksa.fechafin,'DD/MM/YYYY') fechaRegreso, \n"
                + "(SELECT descripcion FROM CausasAusentismos ca WHERE ksa.causareportada = ca.secuencia) causa, \n"
                + "ksa.dias, \n"
                + "(SELECT descripcion FROM TiposAusentismos ti WHERE knsa.tipoausentismo = ti.secuencia) tipo, \n"
                + "(SELECT descripcion FROM ClasesAusentismos ca WHERE knsa.claseausentismo = ca.secuencia) clase, \n"
                + "DECODE(knsa.kionovedadprorroga, null, 'NO', 'SI'), \n"
                + "(SELECT dc.codigo FROM DiagnosticosCategorias dc where dc.secuencia = knsa.diagnosticocategoria) codigo, \n"
                + "(SELECT dc.descripcion FROM DiagnosticosCategorias dc where dc.secuencia = knsa.diagnosticocategoria) descripcion, \n"
                + "(auto.primerapellido||' '||auto.segundoapellido||' '||auto.nombre) EMPLEADOJEFE, \n"
                + "ksa.observacion obsercavion, \n"
                + "knsa.fechafinpago fechapago, \n"
                + "kes.secuencia secuencia, \n"
                + "ksa.nombreanexo anexo, \n"
                + "p.numerodocumento numdocumento \n"
                + "FROM \n"
                + "KioSoliciAusentismos ksa \n"
                + ", Empleados empl \n"
                + ", Personas p \n"
                + ", KioNovedadesSoliciausent knsa \n"
                + ", KioEstadosSoliciausent kes \n"
                + ", Empresas em \n"
                + ", Personas auto \n"
                + "WHERE \n"
                + "ksa.empleado = empl.secuencia \n"
                + "AND knsa.kiosoliciausentismo = ksa.secuencia \n"
                + "AND kes.kiosoliciausentismo = ksa.secuencia \n"
                + "AND empl.persona = p.secuencia \n"
                + "AND empl.empresa = em.secuencia \n"
                + "AND ksa.autorizador = auto.secuencia \n"
                + "AND kes.fechaprocesamiento = (select max(ei.fechaprocesamiento) \n"
                + "  FROM kioestadossoliciausent ei \n"
                + "  WHERE ei.kiosoliciausentismo = ksa.secuencia) \n"
                + "AND knsa.fechainicialausentismo = (select MIN(ei.fechainicialausentismo) \n"
                + "  FROM KioNovedadesSoliciAusent ei \n"
                + "  WHERE ei.kiosoliciausentismo = ksa.secuencia) \n"
                + "AND em.nit = ? \n"
                + "AND kes.estado = ? \n"
                + "AND auto.secuencia = ? \n"
                + "ORDER BY kes.fechaprocesamiento DESC";
        System.out.println("PersistenciaKioSoliciAusentismos" + ".getSolicitudesSinProcesarPorAutorizador(): ");
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, estado);
            query.setParameter(3, secAutorizador);
            resultado = query.getResultList();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSolicitudesSinProcesarPorAutorizador(): " + "Error-1: " + e.toString());
            return null;
        }
    }

    @Override
    public List getSolicitudesPorJefe(String nitEmpresa, String cadena, String secuenciaJefe) {
        List resultado = null;
        String consulta = "SELECT empl.codigoempleado, \n"
                + "p.primerapellido||' '||p.segundoapellido||' '||p.nombre nombrecompleto, \n"
                + "TO_CHAR(ksa.fechageneracion, 'DD/MM/YYYY HH:MM:SS') solicitud, \n"
                + "TO_CHAR(kes.fechaprocesamiento, 'DD/MM/YYYY HH:MM:SS') fechaprocesamiento, \n"
                + "kes.secuencia, \n"
                + "NVL(kes.motivoprocesa, 'N/A'), \n"
                + "TO_CHAR(ksa.fechainicio, 'DD/MM/YYYY') fechainicioausentismo, \n"
                + "TO_CHAR(ksa.fechafin, 'DD/MM/YYYY') fechafinausentismo, \n"
                + "ksa.dias, \n"
                + "(SELECT pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre \n"
                + "  FROM Personas pei, Empleados ei \n"
                + "  WHERE pei.secuencia=ei.persona \n"
                + "  AND ksa.empleadojefe=ei.secuencia) empleadojefe, \n"
                + "kes.estado estado, \n"
                + "(SELECT ca.descripcion FROM CausasAusentismos ca WHERE secuencia=ksa.causareportada) causa, \n"
                + "(SELECT t.descripcion \n"
                + "  FROM CausasAusentismos c, TiposAusentismos t, ClasesAusentismos cl \n"
                + "  WHERE c.secuencia=ksa.causareportada \n"
                + "  AND cl.tipo=t.secuencia \n"
                + "  AND c.clase=cl.secuencia) tipo,\n"
                + "(SELECT cl.descripcion \n"
                + "  FROM CausasAusentismos c, TiposAusentismos t, ClasesAusentismos cl \n"
                + "  WHERE c.secuencia=ksa.causareportada \n"
                + "  AND cl.tipo=t.secuencia AND c.clase=cl.secuencia) clase, \n"
                + "(SELECT dc.codigo FROM DiagnosticosCategorias dc where kn.diagnosticocategoria=dc.secuencia) coddiagnostico, \n"
                + "(SELECT dc.descripcion FROM DiagnosticosCategorias dc where kn.diagnosticocategoria=dc.secuencia) nomdiagnostico, \n"
                + "ksa.observacion, \n"
                + "ksa.nombreanexo anexo, \n"
                + "(SELECT DECODE(kni.kionovedadprorroga, NULL, 'NO', 'SI') \n"
                + "  FROM KioNovedadesSoliciausent kni \n"
                + "  WHERE kni.kiosoliciausentismo=ksa.secuencia \n"
                + "  AND ksa.fechainicio=kni.fechainicialausentismo) prorroga \n"
                + "FROM KioSoliciAusentismos ksa \n"
                + ", KioEstadosSoliciausent kes \n"
                + ", Empleados empl \n"
                + ", Empresas em \n"
                + ", Personas p \n"
                + ", KioNovedadesSoliciausent kn \n"
                + "WHERE \n"
                + "empl.empresa = em.secuencia \n"
                + "AND ksa.empleado = empl.secuencia \n"
                + "AND empl.persona=p.secuencia \n"
                + "AND kes.kiosoliciausentismo = ksa.secuencia \n"
                + "AND kn.kiosoliciausentismo = ksa.secuencia \n"
                + "AND kes.fechaprocesamiento = (SELECT MAX(kesi.fechaprocesamiento) \n"
                + "  FROM KioEstadosSoliciAusent kesi \n"
                + "  WHERE kesi.kioSoliciAusentismo = ksa.secuencia) \n"
                + "AND kn.fechainicialausentismo = (SELECT MIN(ei.fechainicialausentismo) \n"
                + "    FROM kionovedadessoliciausent ei \n"
                + "    WHERE ei.kiosoliciausentismo = ksa.secuencia) \n"
                + "AND kes.estado IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO') \n"
                + "AND em.nit= ? \n"
                + "AND ksa.empleadojefe = ? \n"
                + "ORDER BY kes.fechaprocesamiento DESC";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secuenciaJefe);
            resultado = query.getResultList();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSolicitudesPorJefe(): " + "Error-1: " + e.toString());
            return null;
        }
    }

    @Override
    public List getSolicitudesPorAutorizador(String nitEmpresa, String cadena, String secuenciaAutorizador) {
        List resultado = null;
        String consulta = "SELECT empl.codigoempleado, \n"
                + "p.primerapellido||' '||p.segundoapellido||' '||p.nombre nombrecompleto, \n"
                + "TO_CHAR(ksa.fechageneracion, 'DD/MM/YYYY HH:MM:SS') solicitud, \n"
                + "TO_CHAR(kes.fechaprocesamiento, 'DD/MM/YYYY HH:MM:SS') fechaprocesamiento, \n"
                + "kes.secuencia secEstadoSolici, \n"
                + "NVL(kes.motivoprocesa, 'N/A') motivoprocesa, \n"
                + "TO_CHAR(ksa.fechainicio, 'DD/MM/YYYY') fechainicioausentismo, \n"
                + "TO_CHAR(ksa.fechafin, 'DD/MM/YYYY') fechafinausentismo, \n"
                + "ksa.dias, \n"
                + "(SELECT pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre \n"
                + "  FROM Personas pei, Empleados ei \n"
                + "  WHERE pei.secuencia=ei.persona \n"
                + "  AND ksa.empleadojefe=ei.secuencia) empleadojefe, \n"
                + "kes.estado estado, \n"
                + "(SELECT ca.descripcion FROM CausasAusentismos ca WHERE secuencia=ksa.causareportada) causa, \n"
                + "(SELECT t.descripcion \n"
                + "  FROM CausasAusentismos c, TiposAusentismos t, ClasesAusentismos cl \n"
                + "  WHERE c.secuencia=ksa.causareportada \n"
                + "  AND cl.tipo=t.secuencia \n"
                + "  AND c.clase=cl.secuencia) tipo, \n"
                + "(SELECT cl.descripcion \n"
                + "  FROM CausasAusentismos c, TiposAusentismos t, ClasesAusentismos cl \n"
                + "  WHERE c.secuencia=ksa.causareportada \n"
                + "  AND cl.tipo=t.secuencia AND c.clase=cl.secuencia) clase, \n"
                + "(SELECT dc.codigo FROM DiagnosticosCategorias dc where kn.diagnosticocategoria=dc.secuencia) coddiagnostico, \n"
                + "(SELECT dc.descripcion FROM DiagnosticosCategorias dc where kn.diagnosticocategoria=dc.secuencia) nomdiagnostico, \n"
                + "ksa.observacion, \n"
                + "ksa.nombreanexo anexo, \n"
                + "(SELECT DECODE(kni.kionovedadprorroga, NULL, 'NO', 'SI') \n"
                + "  FROM KioNovedadesSoliciausent kni \n"
                + "  WHERE kni.kiosoliciausentismo=ksa.secuencia \n"
                + "  AND ksa.fechainicio=kni.fechainicialausentismo) prorroga \n"
                + ", (SELECT pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre \n"
                + "  FROM Personas pei \n"
                + "  WHERE ksa.autorizador=pei.secuencia) autorizador \n"
                + "FROM KioSoliciAusentismos ksa \n"
                + ", KioEstadosSoliciausent kes \n"
                + ", Empleados empl \n"
                + ", Empresas em \n"
                + ", Personas p \n"
                + ", KioNovedadesSoliciausent kn \n"
                + "WHERE \n"
                + "empl.empresa = em.secuencia \n"
                + "AND ksa.empleado = empl.secuencia \n"
                + "AND empl.persona=p.secuencia \n"
                + "AND kes.kiosoliciausentismo = ksa.secuencia \n"
                + "AND kn.kiosoliciausentismo = ksa.secuencia \n"
                + "AND kes.fechaprocesamiento = (SELECT MAX(kesi.fechaprocesamiento) \n"
                + "  FROM KioEstadosSoliciAusent kesi \n"
                + "  WHERE kesi.kioSoliciAusentismo = ksa.secuencia) \n"
                + "AND kn.fechainicialausentismo = (SELECT MIN(ei.fechainicialausentismo) \n"
                + "    FROM kionovedadessoliciausent ei \n"
                + "    WHERE ei.kiosoliciausentismo = ksa.secuencia) \n"
                + "AND kes.estado IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO') \n"
                + "AND em.nit= ? \n"
                + "AND ksa.autorizador = ? \n"
                + "ORDER BY kes.fechaprocesamiento DESC";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secuenciaAutorizador);
            resultado = query.getResultList();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSolicitudesPorAutorizador(): " + "Error-1: " + e.toString());
            return null;
        }
    }

    @Override
    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena) {
        String resultado = null;
        String consulta = "SELECT KSA.EMPLEADO\n"
                + "FROM \n"
                + "KIOESTADOSSOLICIAUSENT KES, \n"
                + "KIOSOLICIAUSENTISMOS KSA \n"
                + "WHERE\n"
                + "KES.KIOSOLICIAUSENTISMO=KSA.SECUENCIA "
                + "AND KES.SECUENCIA=?";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, kioEstadoSolici);
            resultado = query.getSingleResult().toString();
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getEmplXsecKioEstadoSolici-2(): " + "Error-1: " + e.toString());
            return null;
        }
    }

    @Override
    public IntervinientesSolAusent getIntervinientesPorEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaKioSoliciAusentismos" + ".getIntervinientesPorEstadoSolici-2(): " + "Parametros: "
                + "kioEstadoSolici: " + kioEstadoSolici
                + " nitEmpresa: " + nitEmpresa
                + " cadena: " + cadena
        );
        IntervinientesSolAusent resultado = null;
        String consulta = "SELECT kes.secuencia secEstadoSol \n"
                + ", ksa.empleado \n"
                + ", ksa.empleadoJefe \n"
                + ", ksa.autorizador \n"
                + "FROM KioEstadosSoliciAusent kes \n"
                + ", KioSoliciAusentismos ksa \n"
                + "WHERE kes.kioSoliciAusentismo = ksa.secuencia \n"
                + "AND kes.secuencia = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta, IntervinientesSolAusent.class);
            query.setParameter(1, kioEstadoSolici);
            resultado = (IntervinientesSolAusent) query.getSingleResult();
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getIntervinientesPorEstadoSolici-2(): " + "resultado: " + resultado.toString());
            return resultado;
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getIntervinientesPorEstadoSolici-2(): " + "Error-1: " + e.toString());
            return null;
        }
    }

    @Override
    public String getSecuenciaSolicitudAusentismo(String secEmpleado, String fechaGeneracion,
            String secEmplJefe, String secAutorizador,
            String nitEmpresa, String cadena, String esquemaP) {
        String resultado = null;
        String consulta = "SELECT secuencia "
                + "FROM KioSoliciAusentismos "
                + "WHERE empleado=? "
                + "AND fechaGeneracion = TO_DATE(?, 'ddmmyyyy HH24miss') "
                + "AND (empleadoJefe = ? "
                + " OR autorizador = ? )"
                + "AND activa = 'S' ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechaGeneracion);
            query.setParameter(3, secEmplJefe);
            query.setParameter(4, secAutorizador);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSecuenciaSolicitudAusentismo(): " + "Error: " + e.toString());
        }
        return resultado;
    }

    @Override
    public int creaSolicitudAusentismo(String seudonimo, String secEmplJefe, String secAutorizador, String nitEmpresa,
            String nombreAnexo, String fechaGeneracion, String fechainicial, String fechaFin, String dias, String observacion,
            String secCausaAusent, String cadena, String esquemaP) {
        System.out.println("PersistenciaKioSoliciAusentismos" + ".creaSolicitudAusentismo(): " + "Parametros: "
                + "seudonimo: " + seudonimo
                + " secEmplJefe: " + secEmplJefe
                + " secAutorizador: " + secAutorizador
                + " nitEmpresa: " + nitEmpresa
                + " nombreAnexo: " + nombreAnexo
                + " fechaGeneracion: " + fechaGeneracion
                + " fechainicial: " + fechainicial
                + " fechaFin: " + fechaFin
                + " dias: " + dias
                + " observacion: " + observacion
                + " secCausaAusent: " + secCausaAusent
                + " cadena: " + cadena
                + " esquemaP: " + esquemaP
        );
        int resultado = -1;
        String consulta = "INSERT INTO KioSoliciAusentismos "
                + "(empleado, usuario, empleadoJefe, autorizador, activa, fechaGeneracion, nombreAnexo,"
                + "fechaInicio, fechaFin, dias, observacion, causaReportada) "
                + "VALUES "
                + "(?,  USER, ?, ?, 'S', TO_DATE(?, 'ddmmyyyy HH24miss'), ?,"
                + "TO_DATE(?, 'DD/MM/YYYY'), TO_DATE(?, 'DD/MM/YYYY'), ?, ?, ?)";
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, secEmplJefe);
            query.setParameter(3, secAutorizador);
            query.setParameter(4, fechaGeneracion);
            query.setParameter(5, nombreAnexo);
            query.setParameter(6, fechainicial);
            query.setParameter(7, fechaFin);
            query.setParameter(8, dias);
            query.setParameter(9, observacion);
            query.setParameter(10, secCausaAusent);
            resultado = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".creaSolicitudAusentismo(): " + "Error: " + e.toString());
        }
        return resultado;
    }

    @Override
    public int creaEstadoSolicitudAusent(String seudonimo, String nitEmpresa, String kioSoliciAusentismo,
            String fechaProcesa, String estado, String motivo, String cadena, String esquemaP) {
        int resultado = -1;
        String consulta = "INSERT INTO KIOESTADOSSOLICIAUSENT "
                + "(KIOSOLICIAUSENTISMO, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, MOTIVOPROCESA)\n"
                + "VALUES "
                + "(?, TO_DATE(?, 'ddmmyyyy HH24miss'), ?, ?, ?)";
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        try {
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, kioSoliciAusentismo);
            query.setParameter(2, fechaProcesa);
            query.setParameter(3, estado);
            query.setParameter(4, secEmpl);
            query.setParameter(5, motivo);
            resultado = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".creaEstadoSolicitudAusent-1(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public int creaEstadoSolicitudAusent(String nitEmpresa, String cadena, String fechaGeneracion, String estado, String secEmplEjecuta,
            String secPerAutoriza, String secKioEstadoSolici, String motivo, String esquemaP) {
        int resultado = -1;
        String consulta = "INSERT INTO KIOESTADOSSOLICIAUSENT \n"
                + "(KIOSOLICIAUSENTISMO, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, SOAUSENTISMO, MOTIVOPROCESA, PERSONAEJECUTA) \n"
                + "SELECT \n"
                + "KIOSOLICIAUSENTISMO"
                + ", TO_DATE( ? , 'ddmmyyyy HH24miss') "
                + ", ? "
                + ", ? EMPLEADOEJECUTA \n"
                + ", SOAUSENTISMO "
                + ", ? "
                + ", ? \n"
                + "FROM KIOESTADOSSOLICIAUSENT \n"
                + "WHERE SECUENCIA= ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaGeneracion);
            query.setParameter(2, estado);
            query.setParameter(3, secEmplEjecuta);
            query.setParameter(4, motivo);
            query.setParameter(5, secPerAutoriza); // null
            query.setParameter(6, secKioEstadoSolici);
            resultado = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".creaEstadoSolicitudAusent-2(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public int creaEstadoSolicitudAusent(String nitEmpresa, String cadena, String fechaGeneracion, String estado, String secEmplEjecuta,
            String secPerAutoriza, String secKioEstadoSolici) {
        int resultado = -1;
        String consulta = "INSERT INTO KIOESTADOSSOLICIAUSENT \n"
                + "(KIOSOLICIAUSENTISMO, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, SOAUSENTISMO, PERSONAEJECUTA)\n"
                + "SELECT\n"
                + "KIOSOLICIAUSENTISMO, TO_DATE(?, 'ddmmyyyy HH24miss'), ?, ? EMPLEADOEJECUTA \n"
                + ", SOAUSENTISMO, ? \n"
                + "FROM KIOESTADOSSOLICIAUSENT \n"
                + "WHERE SECUENCIA=?";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, fechaGeneracion);
            query.setParameter(2, estado);
            query.setParameter(3, secEmplEjecuta);
            query.setParameter(4, secPerAutoriza);
            query.setParameter(5, secKioEstadoSolici);
            resultado = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".creaEstadoSolicitudAusent-3(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public int creaNovedadSoliciAusent(String seudonimo, String nitEmpresa, String fechainicial,
            String secTipo, String secClase, String secCausa, String secCodDiagnostico,
            int dias, String fechaFin, String kioSoliciAusentismo, String secKioNovedadSoliciAusent, String formaLiq, String porcentajeLiq,
            String cadena, String esquemaP) {
        int resultado = -1;
        String consulta = "INSERT INTO KIONOVEDADESSOLICIAUSENT "
                + "(EMPLEADO, FECHAINICIALAUSENTISMO, DIAS, TIPO, SUBTIPO, "
                + "TIPOAUSENTISMO, CLASEAUSENTISMO, CAUSAAUSENTISMO, "
                + "FECHASISTEMA, FECHAFINAUSENTISMO, ESTADO, \n"
                + "FECHAINICIALPAGO, FECHAFINPAGO, FECHAEXPEDICION, FORMALIQUIDACION, PORCENTAJELIQ, "
                + "DIAGNOSTICOCATEGORIA, KIOSOLICIAUSENTISMO, KIONOVEDADPRORROGA, PAGADO) \n"
                + "VALUES \n"
                + "(?, TO_DATE(?,'DD/MM/YYYY'), ?, 'AUSENTISMO', 'AUSENTISMO', "
                + "?, ?, ?, "
                + "SYSDATE, TO_DATE(?,'DD/MM/YYYY'), 'ABIERTO', "
                + "TO_DATE(?,'DD/MM/YYYY'), TO_DATE(?,'DD/MM/YYYY'), SYSDATE, ?, ?, "
                + "?, ?, ?, 'N')";
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        try {
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechainicial);
            query.setParameter(3, dias);
            query.setParameter(4, secTipo);
            query.setParameter(5, secClase);
            query.setParameter(6, secCausa);
            query.setParameter(7, fechaFin); // fecha fin ausentismo
            query.setParameter(8, fechainicial); // fecha inicial pago
            query.setParameter(9, fechaFin); // fecha fin pago
            query.setParameter(10, formaLiq); // formaLiquidacion
            query.setParameter(11, porcentajeLiq); // porcentLiq
            query.setParameter(12, secCodDiagnostico.equals("null") ? null : secCodDiagnostico); // diagnostico
            query.setParameter(13, kioSoliciAusentismo); // kioSoliciAusentismo
            query.setParameter(14, secKioNovedadSoliciAusent.equals("null") ? null : secKioNovedadSoliciAusent); // kioNovedadProrroga
            resultado = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".creaEstadoSolicitudAusent-1(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public String getEmplJefeXsecKioSoliciAusentismo(String secKioSoliciAusentismo, String nitEmpresa, String cadena) {
        String resultado = null;
        String consulta = "SELECT KSA.EMPLEADOJEFE "
                + "FROM "
                + "KIOSOLICIAUSENTISMOS KSA "
                + "WHERE KSA.SECUENCIA = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secKioSoliciAusentismo);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getEmplJefeXsecKioSoliciAusentismo(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public List getDetalleAusentismo(String secKioSoliciAusentismo, String nitEmpresa, String cadena) {
        List resultado = null;
        String consulta = "SELECT "
                + "(SELECT p.nombre||' '|| p.primerApellido || ' ' || p.segundoApellido "
                + "  FROM Personas P, Empleados E "
                + "  WHERE p.secuencia = e.persona "
                + "  AND e.secuencia=ksa.empleado) nombreEmpleado, "
                + "TO_CHAR(ksa.fechaGeneracion, 'DD/MM/YYYY') fechaGeneracion, "
                + "TO_CHAR(ksa.fechaInicio, 'DD/MM/YYYY') fechaInicio, "
                + "TO_CHAR(ksa.fechaFin, 'DD/MM/YYYY') fechaFin, "
                + "dias, "
                + "nombreAnexo, "
                + "observacion, "
                + "causaReportada, "
                + "(SELECT p.nombre||' '|| p.primerApellido || ' ' || p.segundoApellido "
                + "  FROM Personas p, Empleados e "
                + "  WHERE e.persona = p.secuencia "
                + "  AND empleadoJefe = e.secuencia) nombreJefe "
                + "FROM KioSoliciAusentismos ksa "
                + "WHERE ksa.secuencia = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secKioSoliciAusentismo);
            resultado = query.getResultList();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getEmplJefeXsecKioSoliciAusentismo(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public int updateAnexoKioSoliciAusentismo(String seudonimo, String nitEmpresa,
            String nombreAnexo, String secKioSoliciAusentismo, String cadena) {
        int resultado = -1;
        String consulta = "UPDATE KIOSOLICIAUSENTISMOS "
                + "SET NOMBREANEXO= ? "
                + "WHERE SECUENCIA= ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, nombreAnexo);
            query.setParameter(2, secKioSoliciAusentismo);
            resultado = query.executeUpdate();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".updateAnexoKioSoliciAusentismo(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public String getSecuenciaJefeEstadoSolici(String secKioEstadoSoliciAus, String nitEmpresa, String cadena) {
        String resultado = null;
        String consulta = "SELECT ksa.empleadoJefe \n"
                + "FROM KioEstadosSoliciAusent kes, KioSoliciAusentismos ksa \n"
                + "WHERE kes.kioSoliciAusentismo=ksa.secuencia \n"
                + "AND kes.secuencia = ? ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secKioEstadoSoliciAus);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSecuenciaJefeEstadoSolici(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }

    @Override
    public List getProrroga(String empleado, String causa, String fechaInicial, String nitEmpresa, String cadena, String esquemaP) {
        List resultado = null;
        String consulta = "SELECT \n"
                + "KNSA.secuencia, \n"
                + "TO_CHAR(KNSA.FECHAFINAUSENTISMO+1, 'YYYY-MM-DD') finsiguiente, \n"
                + "CA.DESCRIPCION, \n"
                + "(select B.CODIGO from DIAGNOSTICOSCATEGORIAS B where B.secuencia = KNSA.diagnosticocategoria) codigo, \n"
                + "(select  B.DESCRIPCION  from DIAGNOSTICOSCATEGORIAS B where B.secuencia = KNSA.diagnosticocategoria) descripcion, \n"
                + "TO_CHAR(KNSA.FECHAINICIALAUSENTISMO, 'dd/mm/yyyy') FECHA, \n"
                + "TO_CHAR(KNSA.FECHAFINAUSENTISMO , 'dd/mm/yyyy') FECHAFIN, \n"
                + "KNSA.dias \n"
                + "FROM \n"
                + "CAUSASAUSENTISMOS CA, KIONOVEDADESSOLICIAUSENT KNSA, KIOESTADOSSOLICIAUSENT KES, KIOSOLICIAUSENTISMOS KSA \n"
                + "WHERE CA.secuencia = KNSA.CAUSAAUSENTISMO \n"
                + "AND KNSA.KIOSOLICIAUSENTISMO = KSA.SECUENCIA \n"
                + "and KES.KIOSOLICIAUSENTISMO=KSA.SECUENCIA \n"
                + "AND KNSA.EMPLEADO = ? \n"
                + "AND KNSA.CAUSAAUSENTISMO in (?) \n"
                + "AND KNSA.FECHAINICIALAUSENTISMO = (SELECT MAX(KNSAI.FECHAINICIALAUSENTISMO) \n"
                + "FROM KIONOVEDADESSOLICIAUSENT KNSAI \n"
                + "WHERE KNSAI.SECUENCIA=KNSA.SECUENCIA \n"
                + "AND KNSA.KIOSOLICIAUSENTISMO=KNSAI.KIOSOLICIAUSENTISMO \n"
                + ") \n"
                + "AND KNSA.SECUENCIA NOT IN (\n"
                + "    SELECT a1.KIONOVEDADPRORROGA FROM KIONOVEDADESSOLICIAUSENT a1 \n"
                + "    WHERE a1.KIONOVEDADPRORROGA IS NOT NULL)"
                + "AND KES.FECHAPROCESAMIENTO=(SELECT MAX(KESI.FECHAPROCESAMIENTO) \n"
                + "FROM KIOESTADOSSOLICIAUSENT KESI \n"
                + "WHERE KESI.SECUENCIA=KES.SECUENCIA \n"
                + "AND KES.ESTADO IN ('ENVIADO','AUTORIZADO','LIQUIDADO'))";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, empleado);
            query.setParameter(2, causa);
            query.setParameter(3, fechaInicial);
            resultado = query.getResultList();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSecuenciaJefeEstadoSolici(): " + "Error-1: " + e.toString());
        }
        return resultado;
    }
}
