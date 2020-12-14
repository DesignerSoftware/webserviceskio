package co.com.designer.kiosko.entidades.two;

import co.com.designer.kiosko.entidades.Empresas;
import co.com.designer.kiosko.entidades.KioRoles;
import co.com.designer.kiosko.entidades.OpcionesKioskosApp;
import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.6.v20200131-rNA", date="2020-12-13T23:31:41")
@StaticMetamodel(OpcionesKioskosApp.class)
public class OpcionesKioskosApp_ { 

    public static volatile SingularAttribute<OpcionesKioskosApp, String> descripcion;
    public static volatile SingularAttribute<OpcionesKioskosApp, String> icono;
    public static volatile SingularAttribute<OpcionesKioskosApp, String> codigo;
    public static volatile SingularAttribute<OpcionesKioskosApp, String> nombreruta;
    public static volatile SingularAttribute<OpcionesKioskosApp, String> ayuda;
    public static volatile SingularAttribute<OpcionesKioskosApp, BigInteger> secuencia;
    public static volatile SingularAttribute<OpcionesKioskosApp, Empresas> empresa;
    public static volatile SingularAttribute<OpcionesKioskosApp, KioRoles> kiorol;
    public static volatile SingularAttribute<OpcionesKioskosApp, OpcionesKioskosApp> opcionkioskopadre;
    public static volatile SingularAttribute<OpcionesKioskosApp, String> clase;
    public static volatile SingularAttribute<OpcionesKioskosApp, String> reqDestino;

}