package co.com.designer.services;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.kiosko.entidades.KioVigenciasCIR;
import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.implementacion.PersistenciaConexiones;
import co.com.designer.persistencia.implementacion.PersistenciaOpcionesKioskosAPP;
import co.com.designer.persistencia.implementacion.PersistenciaPerfiles;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaOpcionesKioskosAPP;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("opcioneskioskosapp")
public class OpcionesKioskosFacadeREST { //extends AbstractFacade<OpcionesKioskosApp> {

    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaPerfiles persisPerfiles;
    private IPersistenciaOpcionesKioskosAPP persisOpcionesKio;
    private IPersistenciaCadenasKioskosApp persisCadenasKio;

    public OpcionesKioskosFacadeREST() {
        persisConexiones = new PersistenciaConexiones();
        persisPerfiles = new PersistenciaPerfiles();
        persisOpcionesKio = new PersistenciaOpcionesKioskosAPP();
        persisCadenasKio = new PersistenciaCadenasKioskosApp();
    }

    @GET
    @Path("/{nitEmpresa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOpcioneskioskos(@PathParam("nitEmpresa") String nitEmpresa, @QueryParam("seudonimo") String seudonimo, @QueryParam("cadena") String cadena) {
        List r = getOpcioneskioskosApp(nitEmpresa, seudonimo, cadena);
        return Response.ok(r, MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("/opciones")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List findAlls(@QueryParam("seudonimo") String seudonimo, @QueryParam("nitempresa") String nitEmpresa, @QueryParam("cadena") String cadena) {
        String roles = determinarRol(seudonimo, nitEmpresa, cadena);
        List res = this.persisOpcionesKio.buscarTodos(nitEmpresa, cadena, roles);
        return res;
    }

    /**
     * Devuelve opciones de clase 'MENU'
     *
     * @param seudonimo 
     * @param nitEmpresa
     * @param cadena
     * @return List de opcioneskioskosapp con el filtro por clase 'MENU'
     */
    @GET
    @Path("/opcionesMenu")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List opcionesKiosko(@QueryParam("seudonimo") String seudonimo
            , @QueryParam("nitempresa") String nitEmpresa
            , @QueryParam("cadena") String cadena) {
        String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
        this.persisPerfiles.setearPerfil(esquema, cadena);
        String sqlQuery = "SELECT ok "
                + " FROM OpcionesKioskosApp ok "
                + " WHERE "
                + " ok.empresa.nit=:nitempresa "
                + " and ok.clase = 'MENU'";

        String roles = determinarRol(seudonimo, nitEmpresa, cadena);
        System.out.println("Roles: " + roles);
        if (!roles.contains("NOMINA")) {
            sqlQuery += " and ok.kiorol.nombre not in ('NOMINA') ";
        }
        if (!roles.contains("EMPLEADO")) {
            sqlQuery += " and ok.kiorol.nombre not in ('EMPLEADO') ";
        }
        if (!roles.contains("JEFE")) {
            sqlQuery += " and ok.kiorol.nombre not in ('JEFE') ";
        }
        if (!roles.contains("AUTORIZADOR")) {
            sqlQuery += " and ok.kiorol.nombre not in ('AUTORIZADOR') ";
        }
        if (!roles.contains("AUTORIZAAUSENTISMOS")) {
            sqlQuery += " and ok.kiorol.nombre not in ('AUTORIZAAUSENTISMOS') ";
        }
        if (!roles.contains("RRHH")) {
            sqlQuery += " and ok.kiorol.nombre not in ('RRHH') ";
        }
        sqlQuery += " order by ok.codigo asc";

        Query query = this.persisConexiones.getEntityManager(cadena).createQuery(sqlQuery);
        query.setParameter("nitempresa", Long.parseLong(nitEmpresa));
        List<OpcionesKioskosApp> lista = query.getResultList();

        return lista;
    }

    public List getOpcioneskioskosApp(String nitEmpresa, String seudonimo, String cadena) { // retorna true si el usuario esta activo
        List lista = null;
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena);
        System.out.println("Documento asociado a seudonimo: " + documento);

        try {
            String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
            this.persisPerfiles.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT * FROM OPCIONESKIOSKOSAPP OKA, EMPRESAS EM WHERE OKA.EMPRESA=EM.SECUENCIA AND OKA.CLASE='MENU' AND EM.NIT=? ORDER BY OKA.CODIGO ASC";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            lista = (List) (OpcionesKioskosApp) query.getResultList();
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + "getOpcioneskioskosApp(): " + e);
        }
        return lista;
    }

    public String buscarTodos(String id) {
        return "";
    }

    private String[] getCodigosfiltrarOpcionesKioscosApp(String documento, String nit, String cadena) {
        System.out.println(this.getClass().getName() + ".filtrarOpcionesKioskosApp()");
        String[] codigos = null;
        String roles = determinarRol(documento, nit, cadena);
        System.out.println("Roles: " + roles);
        if (!roles.contains("JEFE")) {
            //recorrer la lista de opciones en profundidad con el fin de encontrar las opciones propias del jefe
            //y quitarlas.
            System.out.println("no es rol jefe.");
            if (codigos == null) {
                codigos = new String[5];
                codigos[0] = "0133";
                codigos[1] = "0134";
                codigos[2] = "01393";
                codigos[3] = "0144";
                codigos[4] = "0145";
            } else {
                String[] tmp = new String[codigos.length + 5];
                System.out.println("longitud tmp: " + tmp.length);
                System.out.println("longitud codigos: " + codigos.length);
                for (int j = 0; j < codigos.length; j++) {
                    tmp[j] = codigos[j];
                }
                tmp[tmp.length - 5] = "0145";
                tmp[tmp.length - 4] = "0144";
                tmp[tmp.length - 3] = "01393";
                tmp[tmp.length - 2] = "0133";
                tmp[tmp.length - 1] = "0134";
                codigos = tmp;
            }
        }
        if (!roles.contains("EMPLEADO")) {
            System.out.println("no es rol empleado.");
            if (codigos == null) {
                codigos = new String[13];
                codigos[0] = "0121";
                codigos[1] = "0122";
                codigos[2] = "0123";
                codigos[3] = "0124";
                codigos[4] = "0125";
                codigos[5] = "0126";
                codigos[6] = "0127";
                codigos[7] = "0131";
                codigos[8] = "0132";
                codigos[9] = "01392";
                codigos[10] = "0141";
                codigos[11] = "0142";
                codigos[12] = "0143";
            } else {
                String[] tmp = new String[codigos.length + 13];
                for (int j = 0; j < codigos.length; j++) {
                    tmp[j] = codigos[j];
                }
                tmp[tmp.length - 1] = "0143";
                tmp[tmp.length - 2] = "0142";
                tmp[tmp.length - 3] = "0141";
                tmp[tmp.length - 4] = "01392";
                tmp[tmp.length - 5] = "0132";
                tmp[tmp.length - 6] = "0131";
                tmp[tmp.length - 7] = "0127";
                tmp[tmp.length - 8] = "0126";
                tmp[tmp.length - 9] = "0125";
                tmp[tmp.length - 10] = "0124";
                tmp[tmp.length - 11] = "0123";
                tmp[tmp.length - 12] = "0122";
                tmp[tmp.length - 13] = "0121";
                codigos = tmp;
            }
        }
        if (!roles.contains("AUTORIZADOR")) {
            System.out.println("no es rol autorizador.");
            if (codigos == null) {
                codigos = new String[4];
                codigos[0] = "0139";
                codigos[1] = "01391";
                codigos[2] = "0146";
                codigos[3] = "0147";
                
            } else {
                String[] tmp = new String[codigos.length + 4];
                for (int j = 0; j < codigos.length; j++) {
                    tmp[j] = codigos[j];
                }
                tmp[tmp.length - 4] = "0139";
                tmp[tmp.length - 3] = "01391";
                tmp[tmp.length - 2] = "0146";
                tmp[tmp.length - 1] = "0147";
                codigos = tmp;
            }
        }
        System.out.println("Codigos listados: ");

        if (codigos != null && codigos.length > 0) {
            for (String codigo : codigos) {
                System.out.println("No permitido al usuario: " + codigo);
            }
        }

        return codigos;
    }

    public String getDocumentoPorSeudonimo(String seudonimo, String nitEmpresa, String cadena) {
        System.out.println("Parametros getDocumentoPorSeudonimo() seudonimo: " + seudonimo + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
        String documento = null;
        try {
            String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
            this.persisPerfiles.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P, CONEXIONESKIOSKOS CK WHERE CK.PERSONA=P.SECUENCIA AND lower(CK.SEUDONIMO)=lower(?) AND CK.NITEMPRESA=?";
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, seudonimo);
            query.setParameter(2, nitEmpresa);
            documento = query.getSingleResult().toString();
            System.out.println("documento: " + documento);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".getDocumentoPorSeudonimo: " + e.getMessage());
        }
        return documento;
    }

    public String determinarRol(String seudonimo, String nitEmpresa, String cadena) {
        String documento = getDocumentoPorSeudonimo(seudonimo, nitEmpresa, cadena);
        String rol = "";
        try {
            if (esPersona(documento, nitEmpresa, cadena)) {
                rol = "PERSONA";
            } else {
                rol = "";
            }

            if (esAutorizador(documento, nitEmpresa, cadena)) {
                rol = rol + ";AUTORIZADOR";
            }

            if (consultarEmpleadoXPersoEmpre(documento, nitEmpresa, cadena)) {
                rol = rol + ";EMPLEADO";
            }

            if (esJefe(documento, nitEmpresa, cadena)) {
                rol = rol + ";JEFE";
                rol = rol + ";AUTORIZAAUSENTISMOS";
            }

            if (esRRHH(documento, nitEmpresa, cadena)) {
                rol = rol + ";RRHH";
            }

            System.out.println("rol:" + rol);
            return rol;
        } catch (Exception ex) {
            System.out.println("Error determinarRol(): " + ex.getMessage());
        }
        return rol;
    }

    public boolean esPersona(String documento, String nitEmpresa, String cadena) {
        boolean retorno = false;
        String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
        this.persisPerfiles.setearPerfil(esquema, cadena);
        String sqlQuery = "SELECT COUNT(*) count FROM CONEXIONESKIOSKOS CK, PERSONAS P WHERE CK.PERSONA=P.SECUENCIA AND P.NUMERODOCUMENTO=?";
        try {
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            BigDecimal conteo = BigDecimal.ZERO;
            conteo = (BigDecimal) query.getSingleResult();
            System.out.println("Conteo de persona: " + conteo);
            retorno = conteo.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            System.out.println("Error esPersona: " + e.getMessage());
        }
        return retorno;
    }

    public boolean esAutorizador(String documento, String nitEmpresa, String cadena) {
        boolean retorno = false;
        String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
        this.persisPerfiles.setearPerfil(esquema, cadena);
        String sqlQuery = "select count(*) count "
                + "from kioautorizadores ka "
                + "where ka.persona = (select secuencia from personas where numerodocumento=?) ";
        try {
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            BigDecimal conteo = BigDecimal.ZERO;
            conteo = (BigDecimal) query.getSingleResult();
            System.out.println("Conteo de esAutorizador: " + conteo);
            return conteo.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            System.out.println("Error esAutorizador: " + e.getMessage());
        }
        return retorno;
    }

    public boolean esRRHH(String documento, String nitEmpresa, String cadena) {
        boolean retorno = false;
        String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
        this.persisPerfiles.setearPerfil(esquema, cadena);
        String sqlQuery = "select count(*) count \n"
                + "from kioautorizadores ka \n"
                + "where ka.persona = (select secuencia from personas where numerodocumento=?)\n"
                + "and ka.kiomodulo = (select secuencia from kiomodulos where nombre like 'RRHH') ";
        try {
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, documento);
            BigDecimal conteo = BigDecimal.ZERO;
            conteo = (BigDecimal) query.getSingleResult();
            System.out.println("Conteo de esRRHH: " + conteo);
            return conteo.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            System.out.println("Error esRRHH: " + e.getMessage());
        }
        return retorno;
    }

    public String getDocumentoCorreoODocumento(String usuario, String nitEmpresa, String cadena) {
        System.out.println("Parametros getDocumentoCorreoODocumento() usuario: " + usuario + ", cadena: " + cadena);
        String documento = null;
        try {
            String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
            this.persisPerfiles.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT P.NUMERODOCUMENTO DOCUMENTO FROM PERSONAS P WHERE lower(P.EMAIL)=lower(?)";
            if (this.validarCodigoUsuario(usuario)) {
                sqlQuery += " OR P.NUMERODOCUMENTO=?"; // si el valor es numerico validar por numero de documento
            }
            System.out.println("Query: " + sqlQuery);
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);

            query.setParameter(1, usuario);
            if (this.validarCodigoUsuario(usuario)) {
                query.setParameter(2, usuario);
            }
            documento = query.getSingleResult().toString();
        } catch (Exception e) {
            System.out.println("Error: " + ConexionesKioskosFacadeREST.class.getName() + " getDocumentoCorreoODocumento: " + e.getMessage());
            try {
                String sqlQuery2 = "SELECT P.NUMERODOCUMENTO DOCUMENTO "
                        + "FROM PERSONAS P, EMPLEADOS E "
                        + "WHERE P.SECUENCIA=E.PERSONA "
                        + "AND (lower(P.EMAIL)=lower(?)";
                if (this.validarCodigoUsuario(usuario)) {
                    sqlQuery2 += " OR E.CODIGOEMPLEADO=?"; // si el valor es numerico validar por codigoempleado
                }
                sqlQuery2 += ")";
                System.out.println("Query2: " + sqlQuery2);
                Query query2 = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery2);
                query2.setParameter(1, usuario);
                if (this.validarCodigoUsuario(usuario)) {
                    query2.setParameter(2, usuario);
                }
                documento = query2.getSingleResult().toString();
                System.out.println("Validación documentoPorEmpleado: " + documento);
            } catch (Exception ex) {
                System.out.println("Error 2: " + ConexionesKioskos.class.getName() + " getDocumentoCorreoODocumento(): ");
            }
        }
        return documento;
    }

    public boolean validarCodigoUsuario(String usuario) {
        boolean resultado = false;
        try {
            BigInteger numUsuario = new BigInteger(usuario);
            resultado = true;
        } catch (NumberFormatException nfe) {
            resultado = false;
        }
        return resultado;
    }

    public boolean consultarEmpleadoXPersoEmpre(String numeroDocumento, String nitEmpresa, String cadena) throws Exception {
        String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
        this.persisPerfiles.setearPerfil(esquema, cadena);
        String sqlQuery = "select count(*) count "
                + "from personas per, empleados empl, empresas em, vigenciascargos vc, estructuras es, organigramas o "
                + "where per.secuencia = empl.persona "
                + "and em.secuencia = empl.empresa "
                + "and vc.empleado = empl.secuencia "
                + "and es.secuencia = vc.estructura "
                + "and o.secuencia = es.organigrama "
                + "and em.secuencia = o.empresa "
                + "and per.numerodocumento = ? "
                + "and em.nit = ? "
                + "and vc.fechavigencia = (select max(vci.fechavigencia) "
                + "                        from vigenciascargos vci "
                + "                        where vci.empleado = vc.empleado "
                + "                        and vci.fechavigencia <= sysdate) "
                + "and (EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(empl.secuencia, SYSDATE) = 'ACTIVO' OR EMPLEADOCURRENT_PKG.TipoTrabajadorCorte(empl.secuencia, SYSDATE) = 'PENSIONADO')";
        boolean empleado = false;
        try {
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, numeroDocumento);
            query.setParameter(2, nitEmpresa);
            BigDecimal conteo = BigDecimal.ZERO;
            conteo = (BigDecimal) query.getSingleResult();
            empleado = conteo.compareTo(BigDecimal.ZERO) > 0; // verificar
            System.out.println("conteo de empleado: " + conteo);
        } catch (Exception e) {
            System.out.println("Error consultarEmpleadoXPersoEmpre: " + e.getMessage());
        }
        return empleado;
    }

    /**
     * Método que devuelve el número de ítems (números aleatorios) existentes en
     * la serie
     *
     * @param secEmpleado
     * @param nitEmpresa
     * @param cadena
     * @return El número de ítems (números aleatorios) de que consta la serie
     */
    public boolean esJefe(String secEmpleado, String nitEmpresa, String cadena) {
        boolean retorno = false;
        try {
            String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
            this.persisPerfiles.setearPerfil(esquema, cadena);
            System.out.println("Parametros esJefe(): secEmpleado: " + secEmpleado + ", nitEmpresa: " + nitEmpresa + ", cadena: " + cadena);
            String sqlQuery = "select count(*) count \n"
                    + "from vigenciascargos vc, vigenciastipostrabajadores vtt, "
                    + "tipostrabajadores tt, estructuras es, organigramas o, empresas em \n"
                    + "where \n"
                    + "vc.estructura = es.secuencia \n"
                    + "and es.organigrama = o.secuencia \n"
                    + "and o.empresa = em.secuencia \n"
                    + "and vc.empleado = vtt.empleado \n"
                    + "and vtt.tipotrabajador = tt.secuencia \n"
                    + "and tt.tipo IN ('ACTIVO', 'PENSIONADO') \n"
                    + "and em.secuencia = (select secuencia from empresas where nit=?) "
                    + "and vc.empleadojefe in (select ei.secuencia from empleados ei, personas pei, empresas emi where ei.persona=pei.secuencia "
                    + "                         and pei.numerodocumento=? "
                    + "                         and ei.empresa=emi.secuencia and emi.nit=? "
                    + "                         and empleadocurrent_pkg.tipotrabajadorcorte(ei.secuencia, sysdate)='ACTIVO' ) \n"
                    + "and vtt.fechavigencia = (select max(vtti.fechavigencia) \n"
                    + "                        from vigenciastipostrabajadores vtti \n"
                    + "                        where vtti.empleado = vtt.empleado \n"
                    + "                        and vtti.fechavigencia <= sysdate) \n"
                    + "and vc.fechavigencia = (select max(vci.fechavigencia) \n"
                    + "                        from vigenciascargos vci \n"
                    + "                        where vci.empleado = vc.empleado \n"
                    + "                        and vci.fechavigencia <= sysdate) ";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secEmpleado);
            query.setParameter(3, nitEmpresa);
            BigDecimal conteo = BigDecimal.ZERO;
            conteo = (BigDecimal) query.getSingleResult();
            System.out.println("Conteo de persona: " + conteo);
            retorno = conteo.compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + ".esJefe():" + e.getMessage());
            retorno = false;
        }
        return retorno;
    }

    /**
     * 
     * @param nitEmpresa
     * @param opcionkioskoapp
     * @param cadena
     * @return 
     */
    @GET
    @Path("/kiovigenciasCIR")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAnosCIR(@QueryParam("nit") String nitEmpresa, @QueryParam("opcionkioskoapp") String opcionkioskoapp, @QueryParam("cadena") String cadena) {
        System.out.println("parametros kiovigenciasCIR():" + " opionkioskoapp" + opcionkioskoapp + " nit: " + nitEmpresa + " cadena: " + cadena);
        List exLab = null;
        try {

            String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
            this.persisPerfiles.setearPerfil(esquema, cadena);
            String sqlQuery = "select \n"
                    + "kc.secuencia secuencia,\n"
                    + "kc.ano ano, \n"
                    + "kc.anoarchivo anoarchivo, \n"
                    + "kc.estado estado \n"
                    + "from kiovigenciascir kc  \n"
                    + "where kc.opcionkioskoapp = (select secuencia from opcioneskioskosapp where codigo = ? and empresa =kc.empresa) \n"
                    + "and kc.empresa = (select secuencia from empresas where nit=?)"
                    + "and kc.estado = 'S' "
                    + "ORDER BY ano";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery, KioVigenciasCIR.class);
            query.setParameter(1, opcionkioskoapp);
            query.setParameter(2, nitEmpresa);
            exLab = query.getResultList();
            exLab.forEach(System.out::println);
            return Response.status(Response.Status.OK).entity(exLab).build();
        } catch (Exception ex) {
            System.out.println("Error " + this.getClass().getName() + ".getAnosCIR: " + ex);
            return Response.status(Response.Status.NOT_FOUND).entity("Error").build();
        }
    }

}
