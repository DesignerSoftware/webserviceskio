package co.com.designer.kiosko.entidades.one;

import co.com.designer.kiosko.entidades.Empleados;
import co.com.designer.kiosko.entidades.Empresas;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.6.v20200131-rNA", date="2020-12-13T23:31:41")
@StaticMetamodel(Empleados.class)
public class Empleados_ { 

    public static volatile SingularAttribute<Empleados, Date> fechacreacion;
    public static volatile SingularAttribute<Empleados, String> usuariobd;
    public static volatile SingularAttribute<Empleados, BigDecimal> secuencia;
    public static volatile SingularAttribute<Empleados, BigDecimal> codigoempleado;
    public static volatile SingularAttribute<Empleados, Empresas> empresa;
    public static volatile SingularAttribute<Empleados, String> codigoalternativo;

}