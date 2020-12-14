package co.com.designer.kiosko.entidades.two;

import co.com.designer.kiosko.entidades.ConexionesKioskos;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.6.v20200131-rNA", date="2020-12-13T23:31:41")
@StaticMetamodel(ConexionesKioskos.class)
public class ConexionesKioskos_ { 

    public static volatile SingularAttribute<ConexionesKioskos, Date> fechadesde;
    public static volatile SingularAttribute<ConexionesKioskos, BigDecimal> persona;
    public static volatile SingularAttribute<ConexionesKioskos, Date> ultimaconexion;
    public static volatile SingularAttribute<ConexionesKioskos, Date> fechahasta;
    public static volatile SingularAttribute<ConexionesKioskos, Long> nitEmpresa;
    public static volatile SingularAttribute<ConexionesKioskos, String> observaciones;
    public static volatile SingularAttribute<ConexionesKioskos, BigDecimal> secuencia;
    public static volatile SingularAttribute<ConexionesKioskos, String> seudonimo;
    public static volatile SingularAttribute<ConexionesKioskos, String> enviocorreo;
    public static volatile SingularAttribute<ConexionesKioskos, String> dirigidoa;
    public static volatile SingularAttribute<ConexionesKioskos, String> activo;

}