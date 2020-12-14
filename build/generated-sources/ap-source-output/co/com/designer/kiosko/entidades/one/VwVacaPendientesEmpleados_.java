package co.com.designer.kiosko.entidades.one;

import co.com.designer.kiosko.entidades.Empleados;
import co.com.designer.kiosko.entidades.VwVacaPendientesEmpleados;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.6.v20200131-rNA", date="2020-12-13T23:31:41")
@StaticMetamodel(VwVacaPendientesEmpleados.class)
public class VwVacaPendientesEmpleados_ { 

    public static volatile SingularAttribute<VwVacaPendientesEmpleados, Date> inicialCausacion;
    public static volatile SingularAttribute<VwVacaPendientesEmpleados, Empleados> empleado;
    public static volatile SingularAttribute<VwVacaPendientesEmpleados, BigDecimal> rfVacacion;
    public static volatile SingularAttribute<VwVacaPendientesEmpleados, BigDecimal> diasPendientes;
    public static volatile SingularAttribute<VwVacaPendientesEmpleados, Date> finalCausacion;

}