package co.com.designer.kiosko.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Edwin Yesid Hastamorir
 */
@Entity
public class IntervinientesSolAusent implements Serializable  {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SECESTADOSOL")
    private BigDecimal secEstadoSol;
    @Column(name = "EMPLEADO")
    private BigDecimal empleado;
    @Column(name = "EMPLEADOJEFE")
    private BigDecimal empleadoJefe;
    @Column(name = "AUTORIZADOR")
    private BigDecimal autorizador;

    public BigDecimal getSecEstadoSol() {
        return secEstadoSol;
    }

    public void setSecEstadoSol(BigDecimal secEstadoSol) {
        this.secEstadoSol = secEstadoSol;
    }

    public BigDecimal getEmpleado() {
        return empleado;
    }

    public void setEmpleado(BigDecimal empleado) {
        this.empleado = empleado;
    }

    public BigDecimal getEmpleadoJefe() {
        return empleadoJefe;
    }

    public void setEmpleadoJefe(BigDecimal empleadoJefe) {
        this.empleadoJefe = empleadoJefe;
    }

    public BigDecimal getAutorizador() {
        return autorizador;
    }

    public void setAutorizador(BigDecimal autorizador) {
        this.autorizador = autorizador;
    }

    @Override
    public String toString() {
        return "IntervinientesSolAusent{" + "secEstadoSol=" + secEstadoSol + ", empleado=" + empleado + ", empleadoJefe=" + empleadoJefe + ", autorizador=" + autorizador + '}';
    }

    
    
}
