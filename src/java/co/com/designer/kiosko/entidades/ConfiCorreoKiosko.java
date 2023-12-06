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
 * @author Edwin Hastamorir
 */
@Entity
public class ConfiCorreoKiosko implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SECUENCIA")
    private BigDecimal secuencia;
    @NotNull
    @Column(name = "EMPRESA")
    private BigDecimal empresa;
    @NotNull
    @Column(name = "SERVIDORSMTP")
    private String servidorSMTP;
    @NotNull
    @Column(name = "PUERTO")
    private String puerto;
    @Column(name = "STARTTLS")
    private String startTLS;
    @NotNull
    @Column(name = "AUTENTICADO")
    private String autenticado;
    @NotNull
    @Column(name = "REMITENTE")
    private String remitente;
    @Column(name = "CLAVE")
    private String clave;
    @Column(name = "USARSSL")
    private String usarSSL;
    
    public BigDecimal getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigDecimal secuencia) {
        this.secuencia = secuencia;
    }

    public BigDecimal getEmpresa() {
        return empresa;
    }

    public void setEmpresa(BigDecimal empresa) {
        this.empresa = empresa;
    }

    public String getServidorSMTP() {
        return servidorSMTP;
    }

    public void setServidorSMTP(String servidorSMTP) {
        this.servidorSMTP = servidorSMTP;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getStartTLS() {
        return startTLS;
    }

    public void setStartTLS(String startTLS) {
        this.startTLS = startTLS;
    }

    public String getAutenticado() {
        return autenticado;
    }

    public void setAutenticado(String autenticado) {
        this.autenticado = autenticado;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getUsarSSL() {
        return usarSSL;
    }

    public void setUsarSSL(String usarSSL) {
        this.usarSSL = usarSSL;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (secuencia != null ? secuencia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConfiCorreoKiosko)) {
            return false;
        }
        ConfiCorreoKiosko other = (ConfiCorreoKiosko) object;
        if ((this.secuencia == null && other.secuencia != null) || (this.secuencia != null && !this.secuencia.equals(other.secuencia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.com.designer.kiosko.entidades.ConfiCorreoKiosko[ secuencia=" + secuencia + " ]";
    }
    
}
