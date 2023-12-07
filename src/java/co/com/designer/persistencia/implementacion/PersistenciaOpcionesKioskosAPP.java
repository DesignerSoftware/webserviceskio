package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaOpcionesKioskosAPP;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaOpcionesKioskosAPP implements IPersistenciaOpcionesKioskosAPP{

    private IPersistenciaPerfiles persisPerfiles;
    private IPersistenciaConexiones persisConexiones;
    private IPersistenciaCadenasKioskosApp persisCadenasKio;

    public PersistenciaOpcionesKioskosAPP() {
        persisConexiones = new PersistenciaConexiones();
        persisPerfiles = new PersistenciaPerfiles();
        persisCadenasKio = new PersistenciaCadenasKioskosApp();
    }
    
    @Override
    public List getOpciones(String nitEmpresa, String secuencia, String cadena) { 
        System.out.println("Parametros getOpciones(): nitEmpresa: " + nitEmpresa + ", secuencia: " + secuencia + ", cadena: " + cadena);
        List lista = null;
        try {
            String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
            this.persisPerfiles.setearPerfil(esquema, cadena);
            String sqlQuery = "SELECT OKA.CODIGO, OKA.DESCRIPCION "
                    + "FROM OPCIONESKIOSKOSAPP OKA, "
                    + "EMPRESAS EM WHERE OKA.EMPRESA=EM.SECUENCIA AND OKA.CLASE='MENU' AND EM.NIT=? "
                    + "AND OKA.SECUENCIA=? ORDER BY OKA.CODIGO ASC";
            Query query = this.persisConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery);
            query.setParameter(1, nitEmpresa);
            query.setParameter(2, secuencia);
            //objArray.put(query.getResultList());
            lista = query.getResultList();
            // lista.forEach(System.out::println);
            Iterator<String> it = lista.iterator();
            while (it.hasNext()) {
                System.out.println(it.next().toString());
            }
        } catch (Exception e) {
            System.out.println("Error " + this.getClass().getName() + "getOpciones(): " + e);
        }
        return lista;
    }

    @Override
    public List buscarTodos(String nitEmpresa, String cadena, String roles) {
        String esquema = this.persisCadenasKio.getEsquema(nitEmpresa, cadena);
        this.persisPerfiles.setearPerfil(esquema, cadena);
        String sqlQuery = "SELECT ok "
                + " FROM OpcionesKioskosApp ok "
                + " WHERE "
                + " ok.empresa.nit=:nitempresa ";
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
        /*list for (int i = 0; i < lista.size(); i++) {
                    System.out.println("Recorre 2 "+lista.get(1));
                }*/
        return lista;
    }
}
