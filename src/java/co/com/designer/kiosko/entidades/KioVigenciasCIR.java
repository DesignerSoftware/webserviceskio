
package co.com.designer.kiosko.entidades;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "KIOVIGENCIASCIR")
@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "KioVigenciasCIR.findAll", query = "SELECT k FROM KioVigenciasCIR k order by k.ano ASC")})
public class KioVigenciasCIR implements Serializable {
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SECUENCIA")
    private BigInteger secuencia;
    
    @JoinColumn(name = "OPCIONKIOSKOAPP", referencedColumnName = "SECUENCIA")
    @ManyToOne
    @Basic(optional = false)
    @NotNull
//    @Column(name = "OPCIONKIOSKOAPP")
    private OpcionesKioskosApp opcionkioskoapp;
    
    @JoinColumn(name = "EMPRESA", referencedColumnName = "SECUENCIA")
    @ManyToOne
    @Basic(optional = false)
    @NotNull
//    @Column(name = "EMPRESA")
    private Empresas empresa;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "ANO")
    private int ano;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "ESTADO")
    private String estado;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "ANOARCHIVO")
    private int anoArchivo;

    public KioVigenciasCIR() {
    }

    public KioVigenciasCIR(BigInteger secuencia, OpcionesKioskosApp opcionkioskoapp, Empresas empresa, int ano, String estado, int anoArchivo) {
        this.secuencia = secuencia;
        this.opcionkioskoapp = opcionkioskoapp;
        this.empresa = empresa;
        this.ano = ano;
        this.estado = estado;
        this.anoArchivo = anoArchivo;
    } 

   

    public int getAnoArchivo() {
        return anoArchivo;
    }

    public void setAnoArchivo(int anoArchivo) {
        this.anoArchivo = anoArchivo;
    }

   

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public OpcionesKioskosApp getOpcionkioskoapp() {
        return opcionkioskoapp;
    }

    public void setOpcionkioskoapp(OpcionesKioskosApp opcionkioskoapp) {
        this.opcionkioskoapp = opcionkioskoapp;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
    
    

    
    @Override
    public int hashCode(){
        int hash = 0;
        hash += (secuencia != null ? secuencia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
       // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(obj instanceof KioVigenciasCIR)) {
            return false;
        }
        KioVigenciasCIR other = (KioVigenciasCIR) obj;
        if ((this.secuencia == null && other.secuencia != null) || (this.secuencia != null && !this.secuencia.equals(other.secuencia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "KioVigenciasCIR{" + "secuencia=" + secuencia + ", opcionkioskoapp=" + opcionkioskoapp + ", empresa=" + empresa + ", ano=" + ano + ", estado=" + estado + ", anoArchivo=" + anoArchivo + '}';
    }

  

    
    
    

   
    
    
    
    
}
