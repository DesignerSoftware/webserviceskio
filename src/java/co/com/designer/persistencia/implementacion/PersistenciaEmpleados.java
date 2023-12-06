package co.com.designer.persistencia.implementacion;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import co.com.designer.persistencia.interfaz.IPersistenciaCadenasKioskosApp;
import co.com.designer.persistencia.interfaz.IPersistenciaConexiones;
import co.com.designer.persistencia.interfaz.IPersistenciaEmpleados;
import co.com.designer.persistencia.interfaz.IPersistenciaPerfiles;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Query;

/**
 *
 * @author Edwin Hastamorir
 */
public class PersistenciaEmpleados implements IPersistenciaEmpleados {

//    private IPersistenciaPerfiles rolesBD;
    private IPersistenciaConexiones persistenciaConexiones;
//    private IPersistenciaCadenasKioskosApp cadenasKio;

    public PersistenciaEmpleados() {
        this.persistenciaConexiones = new PersistenciaConexiones();
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
                sqlQuery2 += " OR E.CODIGOEMPLEADO= ? "; // si el valor es numerico validar por codigoempleado
            }
            sqlQuery2 += ") ";
            System.out.println("Query2: " + sqlQuery2);
            Query query2 = this.persistenciaConexiones.getEntityManager(cadena).createNativeQuery(sqlQuery2);
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
    
    
}
