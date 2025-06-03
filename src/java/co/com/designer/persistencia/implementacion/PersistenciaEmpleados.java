package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpleados;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaEmpleados implements IPersistenciaEmpleados {

    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaConexionesKioskos persisConexionesKio;
    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaEmpleados() {
        this.persisConexiones = new PersistenciaConexiones();
        this.persisConexionesKio = new PersistenciaConexionesKioskos();
        this.rolesBD = new PersistenciaPerfiles();
        this.cadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @Override
    public BigDecimal getDocumentoXUsuario(String cadena, String usuario) {
        BigDecimal documento = null;
        try {
            String sqlQuery2 = "SELECT P.NUMERODOCUMENTO DOCUMENTO "
                    + "FROM PERSONAS P, EMPLEADOS E "
                    + "WHERE "
                    + "P.SECUENCIA=E.PERSONA "
                    + "AND ( lower(P.EMAIL) = lower( ? ) ";
            if (validarCodigoUsuario(usuario)) {
                // si el valor es numerico validar por codigoempleado
                sqlQuery2 += " OR E.CODIGOEMPLEADO= ? "; 
            }
            sqlQuery2 += ") ";
            System.out.println("Query2: " + sqlQuery2);
            Query query2 = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery2);
            query2.setParameter(1, usuario);
            if (validarCodigoUsuario(usuario)) {
                query2.setParameter(2, usuario);
            }
            documento = new BigDecimal(query2.getSingleResult().toString());
            System.out.println("Validación documentoPorEmpleado: " + documento);
        } catch (Exception ex) {
            System.out.println("Error 2: " + ConexionesKioskos.class.getName() + " getDocumentoCorreoODocumento(): ");
        }
        return documento;
    }

    @Override
    public boolean validarCodigoUsuario(String usuario) {
        boolean resultado = false;
        BigInteger numUsuario;
        try {
            numUsuario = new BigInteger(usuario);
            resultado = true;
        } catch (NumberFormatException nfe) {
            resultado = false;
            System.out.println("Error validarCodigoUsuario: " + nfe.getMessage());
        }
        return resultado;
    }

    @Override
    public List getDatosEmpleadoNit(String empleado, String nit, String cadena) {

        try {
            BigDecimal documento = this.persisConexionesKio.getDocumentoPorSeudonimo(empleado, nit, cadena);
            String esquema = this.cadenasKio.getEsquema(nit, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "select \n"
                    + "e.codigoempleado usuario, \n"
                    + "p.nombre ||' '|| p.primerapellido ||' '|| p.segundoapellido nombres, \n"
                    + "p.primerapellido apellido1, \n"
                    + "p.segundoapellido apellido2, \n"
                    + "decode(p.sexo,'M', 'MASCULINO', 'F', 'FEMENINO', '') sexo, \n"
                    + "to_char(p.FECHANACIMIENTO, 'dd-MM-yyyy') fechaNacimiento, \n"
                    + "(select nombre from ciudades where secuencia=p.CIUDADNACIMIENTO) ciudadNacimiento, \n"
                    + "p.GRUPOSANGUINEO grupoSanguineo,\n"
                    + "p.FACTORRH factorRH, \n"
                    + "(select nombrelargo from tiposdocumentos where secuencia=p.TIPODOCUMENTO) tipoDocu, \n"
                    + "p.NUMERODOCUMENTO documento, \n"
                    + "(select nombre from ciudades where secuencia=p.CIUDADDOCUMENTO) lugarExpediDocu, \n"
                    + "p.EMAIL email, \n"
                    + "'DIRECCION' direccion, \n"
                    + "ck.ULTIMACONEXION ultimaConexion, \n"
                    + "em.codigo codigoEmpresa, \n"
                    + "em.nit nitEmpresa, \n"
                    + "em.nombre nombreEmpresa, \n"
                    + "empleadocurrent_pkg.descripciontipocontrato(e.secuencia, sysdate) contrato, \n"
                    + "trim(to_char(empleadocurrent_pkg.ValorBasicoCorte(e.secuencia, \n"
                    + "  nvl(GREATEST( \n"
                    + "      cortesprocesos_pkg.CapturarAnteriorCorte(e.secuencia,1,sysdate), \n"
                    + "      cortesprocesos_pkg.CapturarAnteriorCorte(e.secuencia,80,sysdate) \n"
                    + "    ),cortesprocesos_pkg.CapturarAnteriorCorte(e.secuencia,1,sysdate)) \n"
                    + "  ),'$999G999G999G999G999G999')) salario, \n"
                    + "empleadocurrent_pkg.DescripcionCargoCorte(e.secuencia, sysdate) cargo, \n"
                    + "empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate) inicioContratoActual, \n"
                    + "em.logo logoEmpresa, \n"
                    + "UPPER(empleadocurrent_pkg.DireccionAlternativa(p.secuencia, sysdate)) direccionPersona, \n"
                    + "empleadocurrent_pkg.CentrocostoNombre(e.secuencia) centroscostos, \n"
                    + "empleadocurrent_pkg.EdadPersona(p.secuencia, sysdate) || ' AÑOS' edad, \n"
                    + "EmpleadoCurrent_pkg.Afiliacion(e.secuencia, 3, TRUNC(sysdate), sysdate) entidadfp, \n"
                    + "EmpleadoCurrent_pkg.Afiliacion(e.secuencia, 1, TRUNC(sysdate), sysdate) entidadeps, \n"
                    + "EmpleadoCurrent_pkg.Afiliacion(e.secuencia, 2, TRUNC(sysdate), sysdate) entidadarp, \n"
                    + "EmpleadoCurrent_pkg.Afiliacion(e.secuencia, 12, TRUNC(sysdate), sysdate) entidadcesantias, \n"
                    + "empleadocurrent_pkg.Afiliacion(e.secuencia, 14, TRUNC(sysdate), sysdate) cajaCompensacion, \n"
                    + "to_char(empleadocurrent_pkg.fechaVigenciaTipoContrato(e.secuencia, sysdate), 'dd-MM-yyyy') fechaContratacion \n"
                    + "from \n"
                    + "empleados e, conexioneskioskos ck, empresas em, personas p \n"
                    + "where \n"
                    + "e.persona=p.secuencia \n"
                    + "and e.empresa=em.secuencia \n"
                    + "and ck.empleado=e.secuencia \n"
                    + "and p.numerodocumento= ? \n"
                    + "and em.nit=? ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nit);
            List datosEmpleado = query.getResultList();
            return datosEmpleado;
        } catch (Exception e) {
            System.out.println("getDatosEmpleadoXNit: Error: en algo de la base de datos. " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String getSecEmplPorCodigo(String codigo, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaEmpleados" + ".getEmplPorDocumentoEmpresa(): " + "Parametros: "
                + "codigo: " + codigo
                + ", nitEmpresa: "
                + nitEmpresa + ", cadena: " + cadena);
        String secuencia = null;
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT E.SECUENCIA "
                    + "FROM EMPLEADOS E "
                    + "WHERE E.CODIGOEMPLEADO=? "
                    + "AND EMPLEADOCURRENT_PKG.TIPOTRABAJADORCORTE(E.SECUENCIA, SYSDATE) in ('ACTIVO', 'PENSIONADO') ";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, codigo);
            secuencia = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaEmpleados" + ".getSecEmplPorCodigo: " + "Error: " + e.toString());
        }
        return secuencia;
    }

    @Override
    public String getSecEmplPorDocumentoYEmpresa(String documento, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaEmpleados" + ".getEmplPorDocumentoEmpresa(): " + "Parametros: "
                + "documento: " + documento
                + " , nitEmpresa: " + nitEmpresa
                + " , cadena: " + cadena);
        String secuencia = null;
        String sqlQuery = "SELECT E.SECUENCIA "
                + "FROM PERSONAS P, EMPLEADOS E, EMPRESAS EM "
                + "WHERE E.PERSONA=P.SECUENCIA "
                + "AND E.EMPRESA=EM.SECUENCIA "
                + "AND P.NUMERODOCUMENTO=? "
                + "AND EM.NIT=? "
                + "AND EMPLEADOCURRENT_PKG.TIPOTRABAJADORCORTE(E.SECUENCIA, SYSDATE) IN ('ACTIVO', 'PENSIONADO') ";
        System.out.println("PersistenciaEmpleados" + ".getEmplPorDocumentoEmpresa(): " + "sqlQuery: " + sqlQuery);
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaEmpleados" + ".getSecEmplPorDocumentoYEmpresa: " + "Error: " + e.toString());
        }
        return secuencia;
    }

    @Override
    public String consultarSecuenciaEmpleadoJefe(String secEmpleado, String nitEmpresa, String cadena) {
        System.out.println("PersistenciaEmpleados" + ".consultarSecuenciaEmpleadoJefe(): " + "Parametros: "
                + "documento: " + secEmpleado
                + " , nitEmpresa: " + nitEmpresa
                + " , cadena: " + cadena);
        String secuencia = null;
        String sqlQuery = "select empj.secuencia \n"
                + "from empleados empl, empresas em, vigenciascargos vc, estructuras es, organigramas o"
                + ", empleados empj, personas pj \n"
                + "where em.secuencia = empl.empresa \n"
                + "and vc.empleado = empl.secuencia \n"
                + "and es.secuencia = vc.estructura\n"
                + "and o.secuencia = es.organigrama \n"
                + "and em.secuencia = o.empresa \n"
                + "and vc.empleadojefe = empj.secuencia \n"
                + "and pj.secuencia = empj.persona \n"
                + "and empl.secuencia = ? \n"
                + "and em.nit = ? \n"
                + "and vc.fechavigencia = (select max(vci.fechavigencia) \n"
                + "                        from vigenciascargos vci \n"
                + "                        where vci.empleado = vc.empleado \n"
                + "                       and vci.fechavigencia <= sysdate)";
        try {
            String esquema = this.cadenasKio.getEsquema(nitEmpresa, cadena);
            this.rolesBD.setearPerfil(esquema, cadena);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, secEmpleado);
            query.setParameter(2, nitEmpresa);
            secuencia = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("PersistenciaEmpleados" + ".consultarSecuenciaEmpleadoJefe: " + "Error: " + e.toString());
        }
        return secuencia;
    }
}
