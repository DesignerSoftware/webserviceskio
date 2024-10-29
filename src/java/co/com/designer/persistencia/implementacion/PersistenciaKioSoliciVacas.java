package co.com.designer.persistencia.implementacion;

import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaKioSoliciVacas;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaKioSoliciVacas implements IPersistenciaKioSoliciVacas {

    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp cadenasKio;
    private IPersistenciaConexionesKioskos persisConKiosko;

    public PersistenciaKioSoliciVacas() {
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
        this.persisConexiones = new PersistenciaConexiones();
        this.persisConKiosko = new PersistenciaConexionesKioskos();
    }

    /*Crea nuevo registro kioestadosolici al crear nueva solicitud de vacaciones*/
    @Override
    public boolean creaKioEstadoSolici(
            String seudonimo, String nit, String kioSoliciVaca,
            String fechaProcesa, String estado, String motivo, String cadena, String esquema) {
        System.out.println("parametros creaKioEstadoSolici(): seudonimo: " + seudonimo + ", nit: " + nit + ", kiosolicivaca: " + kioSoliciVaca + ""
                + "\n fechaProcesa: " + fechaProcesa + ", estado: " + estado + ", cadena: " + cadena);

        int res = 0;
        try {
            String secEmpl = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "INSERT INTO KIOESTADOSSOLICI (KIOSOLICIVACA, FECHAPROCESAMIENTO, ESTADO, EMPLEADOEJECUTA, MOTIVOPROCESA)\n"
                    + "VALUES (?, to_date(?, 'dd/mm/yyyy HH24miss'), ?, ?, ?)";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioSoliciVaca);
            query.setParameter(2, fechaProcesa);
            query.setParameter(3, estado);
            query.setParameter(4, secEmpl);
            query.setParameter(5, motivo);
            res = query.executeUpdate();
            System.out.println("registro kioestadosolici: " + res);
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".creaKioEstadoSolici: " + ex.getMessage());
            return false;
        }
        return res > 0;
    }

    @Override
    public String getSecPerAutorizadorXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        System.out.println("Parametros getSecPerAutorizadorXsecKioEstadoSolici(): secKioEstadoSolici: " + secKioEstadoSolici + " nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String secPerAutorizador = null;
        try {
            String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.AUTORIZADOR "
                    + "FROM KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA "
                    + "AND KE.SECUENCIA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secPerAutorizador = query.getSingleResult().toString();
            System.out.println("Secuencia persona KioAutorizador: " + secPerAutorizador);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getSecPerAutorizadorXsecKioEstadoSolici: " + e.getMessage());
        }
        return secPerAutorizador;
    }

    @Override
    public String getEmplJefeXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        String secEmplJefe = null;
        try {
            String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.EMPLEADOJEFE "
                    + "FROM KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA "
                    + "AND KE.SECUENCIA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            secEmplJefe = query.getSingleResult().toString();
            System.out.println("Empl jefe asociado: " + secEmplJefe);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getEmplJefeXsecKioEstadoSolici: " + e.getMessage());
        }
        return secEmplJefe;
    }

    @Override
    public String getFechaInicioXsecKioEstadoSolici(String secKioEstadoSolici, String nitEmpresa, String cadena) {
        String fechaInicio = null;
        try {
            String esquema = cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select "
                    + "TO_CHAR(KN.FECHAINICIALDISFRUTE, 'dd/mm/yyyy') "
                    + "FROM KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN "
                    + "WHERE "
                    + "KE.KIOSOLICIVACA = KSV.SECUENCIA "
                    + "AND KSV.KIONOVEDADSOLICI=KN.SECUENCIA "
                    + "AND KE.SECUENCIA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, secKioEstadoSolici);
            fechaInicio = query.getSingleResult().toString();
            System.out.println("Fecha inicio: " + fechaInicio);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getFechaInicioXsecKioEstadoSolici: " + e.getMessage());
        }
        return fechaInicio;
    }

    @Override
    public String getEmplXsecKioEstadoSolici(String kioEstadoSolici, String nitEmpresa, String cadena, String esquema) {
        System.out.println("Parametros getEmplXsecKioEstadoSolici(): kioEstadoSolici: " + kioEstadoSolici + ", cadena: " + cadena);
        String secEmpl = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT KSV.EMPLEADO \n"
                    + "FROM KIOESTADOSSOLICI KE, KIOSOLICIVACAS KSV, KIONOVEDADESSOLICI KN \n"
                    + "WHERE \n"
                    + "KE.KIOSOLICIVACA=KSV.SECUENCIA \n"
                    + "AND KSV.KIONOVEDADSOLICI = KN.SECUENCIA \n"
                    + "AND KE.SECUENCIA= ? ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, kioEstadoSolici);
            secEmpl = query.getSingleResult().toString();
            System.out.println("Valor getEmplXsecKioEstadoSolici(): " + secEmpl);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getEmplXsecKioEstadoSolici(): " + e.getMessage());
        }
        return secEmpl;
    }

    @Override
    public boolean creaKioNovedadSolici(String seudonimo, String nitEmpresa, String fechainicial, String fecharegreso, String dias, String RFVACACION, String fechaFin, String cadena, String esquema) {
        int conteo = 0;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            System.out.println("parametros creaKioNovedadSolici seudonimo: " + seudonimo + ", nit: " + nitEmpresa + ", fechainicial: " + fechainicial + ", fecharegreso: " + fecharegreso + " fecha fin: " + fechaFin + " dias: " + dias + ", rfvacacion: " + RFVACACION);
            String sql = "INSERT INTO KIONOVEDADESSOLICI (EMPLEADO, FECHAINICIALDISFRUTE, DIAS, TIPO, SUBTIPO, FECHASISTEMA, FECHASIGUIENTEFINVACA, ESTADO, \n"
                    + "ADELANTAPAGO, ADELANTAPAGOHASTA, FECHAPAGO, PAGADO, VACACION)\n"
                    + "VALUES\n"
                    + "(?, TO_DATE(?,'DD/MM/YYYY'), ?, 'VACACION', 'TIEMPO', SYSDATE, TO_DATE(?,'DD/MM/YYYY'), 'ABIERTO', ?, TO_DATE(?,'DD/MM/YYYY'), ?, 'N', ?)";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sql);
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, fechainicial);
            query.setParameter(3, dias);
            query.setParameter(4, fecharegreso);
            query.setParameter(5, null);
            query.setParameter(6, fechaFin);
            query.setParameter(7, null);
            query.setParameter(8, RFVACACION);
            conteo = query.executeUpdate();
            System.out.println("PersistenciaKioSoliciVacas." + "creaKioNovedadSolici(): conteo: " + conteo);
            return conteo > 0;
        } catch (Exception e) {
            System.out.println("Error: " + "PersistenciaKioSoliciVacas." + "creaKioNovedadSolici(): " + e.toString());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean creaKioSoliciVacas(String seudonimo, String secEmplJefe, String secPersonaAutorizador, String nit, String secNovedad, String fechaGeneracion, String cadena, String esquema) {
        System.out.println("Parametros creaKioSoliciVacas(): seudonimo: " + seudonimo + ", nit: " + nit + ", secNovedad: " + secNovedad + ", fechaGeneracion: " + fechaGeneracion
                + ", autorizador: " + secPersonaAutorizador + ", secEmplJefe: " + secEmplJefe + ", cadena: " + cadena);
        int conteo = 0;
        String secEmpleado = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nit, cadena);
            String sql = "";
            if (secEmplJefe != null || secPersonaAutorizador != null) {
                if (secPersonaAutorizador != null) {
                    System.out.println("creaKioSoliciVacas por kioautorizador");
                    sql += "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, autorizador, activa, fechageneracion) "
                            + "values (?, ?, user, ?, 'S', to_date(?, 'dd/mm/yyyy HH24miss'))";
                } else if (secEmplJefe != null) {
                    System.out.println("creaKioSoliciVacas por empleadojefe");
                    sql += "insert into kiosolicivacas (empleado, kionovedadsolici, usuario, empleadojefe, activa, fechageneracion) "
                            + "values (?, ?, user, ?, 'S', to_date(?, 'dd/mm/yyyy HH24miss'))";
                }
                Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sql);
                query.setParameter(1, secEmpleado);
                query.setParameter(2, secNovedad);
                if (secPersonaAutorizador != null) {
                    query.setParameter(3, secPersonaAutorizador);
                } else {
                    query.setParameter(3, secEmplJefe);
                }
                query.setParameter(4, fechaGeneracion);
                conteo = query.executeUpdate();
                System.out.println("registro kiosolicivaca: " + conteo);
            } else {
                conteo = 0; // No crear la solicitud si no hay un jefe relacionado
            }
        } catch (Exception e) {
            System.out.println("Error creaKioSoliciVacas: " + e.getMessage());
            conteo = 0;
        }
        return conteo > 0;
    }

    @Override
    public String getSecuenciaKioNovedadesSolici(String seudonimo, String nitEmpresa,
            String fechainicio, String fecharegreso,
            String dias, String rfVacacion, String cadena, String esquema) {
        System.out.println("Parametros getSecuenciaKioNovedadesSolici(): seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", fechainicio: " + fechainicio + ", fecharegreso: " + fecharegreso + ", dias: " + dias + ", rfVacacion: " + rfVacacion + ", cadena: " + cadena);
        String sec = null;
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            String secEmpleado = this.persisConKiosko.getSecuenciaEmplPorSeudonimo(seudonimo, nitEmpresa, cadena);
            String sqlQuery = "select max(secuencia) \n"
                    + "                from KioNovedadesSolici \n"
                    + "                where dias=? \n"
                    + "                and fechainicialdisfrute=to_date(?, 'dd/mm/yyyy') \n"
                    + "                and fechasiguientefinvaca=to_date(?, 'dd/mm/yyyy') \n"
                    + "                and empleado=? \n"
                    + "                and tipo='VACACION' \n"
                    + "                and SUBTIPO='TIEMPO' \n"
                    + "                and vacacion=? "
                    + "  and secuencia not in (select kionovedadsolici from kiosolicivacas)";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, dias);
            query.setParameter(2, fechainicio);
            query.setParameter(3, fecharegreso);
            query.setParameter(4, secEmpleado);
            query.setParameter(5, rfVacacion);

            sec = query.getSingleResult().toString();
            System.out.println("secuencia kionovedad: " + sec);
        } catch (Exception e) {
            System.out.println("Error: getSecuenciaKioNovedadesSolici: " + e.getMessage());
        }
        return sec;
    }

    @Override
    public String getSecKioSoliciVacas(String secEmpl, String fechaGeneracion,
            String secEmplJefe, String secPerAutorizador, String kioNovedadSolici, String nitEmpresa, String cadena, String esquema) {
        String secKioSoliciVacas = null;
        String sqlQuery = "";
        try {
            this.rolesBD.setearPerfil(esquema, cadena);
            System.out.println("parametros getSecKioSoliciVacas: secEmpl: " + secEmpl + ", fechaGeneracion: " + fechaGeneracion + ", secEmplJefe: " + secEmplJefe + ", autorizador: " + secPerAutorizador + ", kioNovedadSolici " + kioNovedadSolici);
            if (secPerAutorizador != null) {
                sqlQuery += "select secuencia "
                        + "from kiosolicivacas "
                        + "where empleado=? "
                        + " and fechageneracion=to_date(?, 'dd/mm/yyyy HH24miss') "
                        + " and autorizador=? and activa='S' and kionovedadsolici=?";
            } else {
                sqlQuery += "select secuencia from kiosolicivacas where empleado=? "
                        + " and fechageneracion=to_date(?, 'dd/mm/yyyy HH24miss') "
                        + " and empleadojefe=? and activa='S' and kionovedadsolici=?";
            }
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpl);
            query.setParameter(2, fechaGeneracion);
            if (secPerAutorizador != null) {
                query.setParameter(3, secPerAutorizador);
            } else {
                query.setParameter(3, secEmplJefe);
            }
            query.setParameter(4, kioNovedadSolici);
            secKioSoliciVacas = query.getSingleResult().toString();
            System.out.println("SecKioSoliciVacas: " + secKioSoliciVacas);
        } catch (Exception e) {
            System.out.println("Error: getSecKioSoliciVacas: " + e.getMessage());
        }
        return secKioSoliciVacas;
    }
}
