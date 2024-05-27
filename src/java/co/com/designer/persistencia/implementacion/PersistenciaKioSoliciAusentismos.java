package co.com.designer.persistencia.implementacion;

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
                + "t1.codigoempleado documento, \n"
                + "REPLACE(TRIM(P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE), '  ', ' ') NOMBRE,\n"
                + "KES.SECUENCIA, \n"
                + "TO_CHAR(KES.FECHAPROCESAMIENTO, 'DD/MM/YYYY') SOLICITUD, \n"
                + "TO_CHAR(KSA.FECHAINICIO,'DD/MM/YYYY' ) INICIALAUSENT,\n"
                + "TO_CHAR(KES.FECHAPROCESAMIENTO, 'DD/MM/YYYY') FECHAULTMODIF,\n"
                + "KES.ESTADO, \n"
                + "KES.MOTIVOPROCESA, \n"
                + "KES.SOAUSENTISMO, \n"
                + "TO_CHAR(KSA.FECHAFIN,'DD/MM/YYYY') FECHAREGRESO,\n"
                + "(SELECT DESCRIPCION FROM CAUSASAUSENTISMOS CA WHERE KSA.CAUSAREPORTADA = CA.SECUENCIA) CAUSA,\n"
                + "KSA.DIAS,\n"
                + "(SELECT DESCRIPCION FROM TIPOSAUSENTISMOS TI WHERE KNSA.TIPOAUSENTISMO = TI.SECUENCIA) TIPO,\n"
                + "(SELECT DESCRIPCION FROM CLASESAUSENTISMOS CA WHERE KNSA.CLASEAUSENTISMO = CA.SECUENCIA) CLASE,\n"
                + "DECODE(KNSA.KIONOVEDADPRORROGA, null, 'NO', 'SI'),\n"
                + "DC.CODIGO,\n"
                + "DC.DESCRIPCION,\n"
                + "(SELECT PER.PRIMERAPELLIDO||' '||PER.SEGUNDOAPELLIDO||' '||PER.NOMBRE FROM PERSONAS PER, EMPLEADOS EMPL\n"
                + "WHERE EMPL.PERSONA=PER.SECUENCIA\n"
                + "AND EMPL.SECUENCIA=JEFE.SECUENCIA) EMPLEADOJEFE,        \n"
                + "KSA.OBSERVACION OBSERCAVION,\n"
                + "KNSA.FECHAFINPAGO FECHAPAGO,\n"
                + "KES.secuencia secuencia, \n"
                + "KSA.NOMBREANEXO ANEXO,"
                + "P.NUMERODOCUMENTO NUMDOCUMENTO \n"
                + "FROM \n"
                + "KIOESTADOSSOLICIAUSENT KES, \n"
                + "KIOSOLICIAUSENTISMOS KSA, \n"
                + "EMPLEADOS t1, \n"
                + "PERSONAS P, \n"
                + "KIONOVEDADESSOLICIAUSENT KNSA, \n"
                + "EMPLEADOS JEFE, \n"
                + "DIAGNOSTICOSCATEGORIAS DC \n"
                + ", EMPRESAS EM \n"
                + "WHERE \n"
                + "((((\n"
                + "(t1.EMPRESA = EM.SECUENCIA) AND EM.NIT = ? AND (KES.ESTADO = ?)) AND (KSA.EMPLEADOJEFE = ?))) \n"
                + "AND ((KSA.SECUENCIA = KES.KIOSOLICIAUSENTISMO) AND (t1.SECUENCIA = KSA.EMPLEADO))) \n"
                + "AND T1.PERSONA=P.SECUENCIA\n"
                + "AND KSA.SECUENCIA=KNSA.KIOSOLICIAUSENTISMO\n"
                + "AND KNSA.FECHAINICIALAUSENTISMO = (select MIN(ei.FECHAINICIALAUSENTISMO) \n"
                + "   from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                + "    where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                + "    and ksi.secuencia=KSA.secuencia)\n"
                + "and KES.FECHAPROCESAMIENTO = (select max(ei.FECHAPROCESAMIENTO) \n"
                + "from KIOESTADOSSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi \n"
                + "where ei.KIOSOLICIAUSENTISMO = ksi.secuencia \n"
                + "and ksi.secuencia=KSA.secuencia)\n"
                + "AND (KES.FECHAPROCESAMIENTO = (SELECT MAX(t3.FECHAPROCESAMIENTO) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                + "WHERE ((t4.SECUENCIA = KSA.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO))))\n"
                + "AND KSA.EMPLEADOJEFE=JEFE.SECUENCIA  \n"
                + "AND DC.SECUENCIA(+) = KNSA.DIAGNOSTICOCATEGORIA\n"
                + "ORDER BY KES.FECHAPROCESAMIENTO DESC";
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
    public List getSolicitudesPorJefe(String nitEmpresa, String cadena, String secuenciaJefe) {
        List resultado = null;
        String consulta = "SELECT \n"
                + "t1.CODIGOEMPLEADO, "
                + "P.PRIMERAPELLIDO||' '||P.SEGUNDOAPELLIDO||' '||P.NOMBRE NOMBRECOMPLETO, "
                + "to_char(t2.FECHAGENERACION, 'DD/MM/YYYY HH:mm:ss') SOLICITUD, "
                + "to_char(T0.FECHAPROCESAMIENTO, 'DD/MM/YYYY HH:mm:ss') FECHAPROCESAMIENTO, "
                + "t0.SECUENCIA, NVL(t0.MOTIVOPROCESA, 'N/A'), "
                + "to_char(T2.FECHAINICIO, 'DD/MM/YYYY') FECHAINICIOAUSENTISMO, "
                + "to_char(T2.FECHAFIN, 'DD/MM/YYYY') FECHAFINAUSENTISMO, "
                + "t2.dias, "
                + "(select "
                + "pei.primerapellido||' '||pei.segundoapellido||' '||pei.nombre "
                + "from personas pei, empleados ei "
                + "where pei.secuencia=ei.persona and t2.EMPLEADOJEFE=ei.secuencia) empleadojefe, "
                + "t0.ESTADO ESTADO,"
                + "(SELECT DESCRIPCION FROM CAUSASAUSENTISMOS WHERE SECUENCIA=T2.CAUSAREPORTADA) CAUSA,"
                + "(SELECT T.DESCRIPCION FROM CAUSASAUSENTISMOS C, TIPOSAUSENTISMOS T, CLASESAUSENTISMOS CL "
                + "WHERE C.SECUENCIA=T2.CAUSAREPORTADA AND CL.TIPO=T.SECUENCIA AND C.CLASE=CL.SECUENCIA) TIPO,"
                + "(SELECT CL.DESCRIPCION FROM CAUSASAUSENTISMOS C, TIPOSAUSENTISMOS T, CLASESAUSENTISMOS CL "
                + "WHERE C.SECUENCIA=T2.CAUSAREPORTADA AND CL.TIPO=T.SECUENCIA AND C.CLASE=CL.SECUENCIA) CLASE,"
                + "(SELECT CODIGO FROM DIAGNOSTICOSCATEGORIAS WHERE KN.DIAGNOSTICOCATEGORIA=SECUENCIA) CODDIAGNOSTICO, \n"
                + "(SELECT DESCRIPCION FROM DIAGNOSTICOSCATEGORIAS WHERE KN.DIAGNOSTICOCATEGORIA=SECUENCIA) NOMDIAGNOSTICO,"
                + "T2.OBSERVACION, "
                + "T2.NOMBREANEXO ANEXO, "
                + "(SELECT DECODE(KIONOVEDADPRORROGA, NULL, 'NO', 'SI') FROM KIONOVEDADESSOLICIAUSENT "
                + "WHERE KIOSOLICIAUSENTISMO=T2.SECUENCIA "
                + "AND T2.FECHAINICIO=FECHAINICIALAUSENTISMO) PRORROGA "
                + "FROM KIOESTADOSSOLICIAUSENT t0, KIOSOLICIAUSENTISMOS t2, EMPLEADOS t1, PERSONAS P, "
                + "kionovedadessoliciausent kn "
                + "WHERE (((( "
                + "(t1.EMPRESA = (select secuencia from empresas where nit=?)) \n"
                + "AND (t0.ESTADO IN ('AUTORIZADO', 'RECHAZADO','LIQUIDADO')))   "
                + "AND (t2.EMPLEADOJEFE =?) "
                + ")  "
                + "AND (t0.FECHAPROCESAMIENTO = (SELECT MAX(t3.FECHAPROCESAMIENTO) FROM KIOSOLICIAUSENTISMOS t4, KIOESTADOSSOLICIAUSENT t3 \n"
                + "WHERE ((t4.SECUENCIA = t2.SECUENCIA) AND (t4.SECUENCIA = t3.KIOSOLICIAUSENTISMO))))) "
                + "AND ((t2.SECUENCIA = t0.KIOSOLICIAUSENTISMO) AND (t1.SECUENCIA = t2.EMPLEADO)) "
                + "AND t1.PERSONA=P.SECUENCIA "
                + "and t2.secuencia = kn.kiosoliciausentismo "
                + ") "
                + "AND KN.FECHAINICIALAUSENTISMO = (select MIN(ei.FECHAINICIALAUSENTISMO) "
                + "    from KIONOVEDADESSOLICIAUSENT ei, KIOSOLICIAUSENTISMOS ksi "
                + "    where ei.KIOSOLICIAUSENTISMO = ksi.secuencia "
                + "    and ksi.secuencia=t2.secuencia) "
                + "ORDER BY t0.FECHAPROCESAMIENTO DESC";
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
    public String getSecuenciaSolicitudAusentismo(String secEmpleado, String fechaGeneracion,
            String secEmplJefe, String nitEmpresa, String cadena, String esquemaP) {
        String resultado = null;
        String consulta = "SELECT secuencia "
                + "FROM KioSoliciAusentismos "
                + "WHERE empleado=? "
                + "AND fechaGeneracion = TO_DATE(?, 'ddmmyyyy HH24miss') "
                + "AND empleadoJefe = ? "
                + "AND activa = 'S' ";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechaGeneracion);
            query.setParameter(3, secEmplJefe);
            resultado = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaKioSoliciAusentismos" + ".getSecuenciaSolicitudAusentismo(): " + "Error: " + e.toString());
        }
        return resultado;
    }

    @Override
    public int creaSolicitudAusentismo(String seudonimo, String secEmplJefe, String nitEmpresa, String nombreAnexo, String fechaGeneracion, String fechainicial, String fechaFin, String dias, String observacion, String secCausaAusent, String cadena, String esquemaP) {
        int resultado = -1;
        String consulta = "INSERT INTO KioSoliciAusentismos "
                + "(empleado, usuario, empleadoJefe, activa, fechaGeneracion, nombreAnexo,"
                + "fechaInicio, fechaFin, dias, observacion, causaReportada) "
                + "VALUES "
                + "(?,  USER, ?, 'S', TO_DATE(?, 'ddmmyyyy HH24miss'), ?,"
                + "TO_DATE(?, 'DD/MM/YYYY'), TO_DATE(?, 'DD/MM/YYYY'), ?, ?, ?)";
        this.persisConKiosko = new PersistenciaConexionesKioskos();
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(consulta);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, secEmplJefe);
            query.setParameter(3, fechaGeneracion);
            query.setParameter(4, nombreAnexo);
            query.setParameter(5, fechainicial);
            query.setParameter(6, fechaFin);
            query.setParameter(7, dias);
            query.setParameter(8, observacion);
            query.setParameter(9, secCausaAusent);
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
                + "FROM KioEstados kes, KioSoliciAusentismos ksa \n"
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
