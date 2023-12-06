package co.com.designer.kiosko.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Edwin Hastamorir
 */
@Entity
//@Table(name = "GENERALESKIOSKO")
@XmlRootElement
//@NamedQueries({
//@NamedQuery(name = "Empleados.findAll", query = "SELECT R FROM GENERALESKIOSKO R")})
public class GeneralesKiosko implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SECUENCIA")
    private BigDecimal secuencia;
    @Column(name = "PATHREPORTES")
    private String pathReportes;
    @Column(name = "UBICAREPORTES")
    private String ubicaReportes;
    @Column(name = "PATHFOTO")
    private String pathFoto;

    public BigDecimal getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigDecimal secuencia) {
        this.secuencia = secuencia;
    }

    public String getPathReportes() {
        return pathReportes;
    }

    public void setPathReportes(String pathReportes) {
        this.pathReportes = pathReportes;
    }

    public String getUbicaReportes() {
        return ubicaReportes;
    }

    public void setUbicaReportes(String ubicaReportes) {
        this.ubicaReportes = ubicaReportes;
    }

    public String getPathFoto() {
        return pathFoto;
    }

    public void setPathFoto(String pathFoto) {
        this.pathFoto = pathFoto;
    }
    
}
