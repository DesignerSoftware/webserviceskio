package co.com.designer.kiosko.generales;

import java.sql.SQLException;

/**
 *
 * @author Felipe Triviño
 */
public class ExtraeCausaExcepcion {

    /**
     * Metodo encargado de retornar el ultimo error que se capturo en los try -
     * catch.
     *
     * @param e Exception
     * @return Retorna el ultimo error capturado.
     */
    public static Throwable getLastThrowable(Exception e) {
        Throwable t;
        int cont = 1;
        t = e;
        while ( t.getCause() != null && cont<=1000){
            cont+=1;
            t = t.getCause();
        }
        
        return t;
    }

    public static int obtenerCodigoSQLException(Exception e) throws Exception {
        int codigo = 0;
        Throwable t = getLastThrowable(e);
        if (t instanceof SQLException) {
            SQLException sqle = (SQLException) t;
            codigo = sqle.getErrorCode();
        } 
        return codigo;
    }

    public static String obtenerMensajeSQLException(Exception e) {
        String mensaje = "";
        Throwable t = getLastThrowable(e);
        if (t instanceof SQLException) {
            SQLException sqle = (SQLException) t;
            mensaje = t.getMessage();
        } 
        return mensaje;
    }
}
