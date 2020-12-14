/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.kiosko.entidades;

import java.io.Serializable;
import java.math.BigInteger;
//import java.math.BigDecimal;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Felipe Triviño
 */
@Entity
@Table(name = "OPCIONESKIOSKOSAPP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OpcionesKioskosApp.findAll", query = "SELECT o FROM OpcionesKioskosApp o order by o.codigo ASC")})
public class OpcionesKioskosApp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CODIGO")
    private String codigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "DESCRIPCION")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SECUENCIA")
    private BigInteger secuencia;
    @Size(max = 1000)
    @Column(name = "AYUDA")
    private String ayuda;
    @Size(max = 100)
    @Column(name = "ICONO")
    private String icono;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CLASE")
    private String clase;
    /*@Size(max = 20)
    @Column(name = "TIPOREPORTE")
    private String tiporeporte;*/
    @Size(max = 50)
    @Column(name = "NOMBRERUTA")
    private String nombreruta;
    @Size(max = 1)
    @Column(name = "REQDESTINO")
    private String reqDestino;
    @JoinColumn(name = "OPCIONKIOSKOPADRE", referencedColumnName = "SECUENCIA")
    @ManyToOne
    private OpcionesKioskosApp opcionkioskopadre;
    @JoinColumn(name = "EMPRESA", referencedColumnName = "SECUENCIA")
    @ManyToOne
    private Empresas empresa;
    @JoinColumn(name = "KIOROL", referencedColumnName = "SECUENCIA")
    @ManyToOne
    private KioRoles kiorol;

    /*@Transient
    private OpcionesKioskosApp opcionesHijas;
    @Transient
    private boolean destino;*/

    public OpcionesKioskosApp() {
    }

    public OpcionesKioskosApp(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public OpcionesKioskosApp(BigInteger secuencia, String codigo, String descripcion, String clase) {
        this.secuencia = secuencia;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.clase = clase;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public String getAyuda() {
        return ayuda;
    }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

   /* public String getTiporeporte() {
        return tiporeporte;
    }

    public void setTiporeporte(String tiporeporte) {
        this.tiporeporte = tiporeporte;
    }*/

    public String getNombreruta() {
        return nombreruta;
    }

    public void setNombreruta(String nombreruta) {
        this.nombreruta = nombreruta;
    }

    public String getReqDestino() {
        return reqDestino;
    }

    public void setReqDestino(String reqDestino) {
        this.reqDestino = reqDestino;
    }
    
    public OpcionesKioskosApp getOpcionkioskopadre() {
        return opcionkioskopadre;
    }

    public void setOpcionkioskopadre(OpcionesKioskosApp opcionkioskopadre) {
        this.opcionkioskopadre = opcionkioskopadre;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    public KioRoles getKiorol() {
        return kiorol;
    }

    public void setKiorol(KioRoles kiorol) {
        this.kiorol = kiorol;
    }
    
    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    /*public List<OpcionesKioskosApp> getOpcionesHijas() {
        return (List<OpcionesKioskosApp>) opcionesHijas;
    }*/

    /*public void setOpcionesHijas(OpcionesKioskosApp opcionesHijas) {
        this.opcionesHijas = opcionesHijas;
    }

    public boolean isDestino() {
        /*if ("S".equalsIgnoreCase(reqDestino)){
            this.destino=true;
        }else{
            this.destino=false;
        }*/
        /*destino = "S".equalsIgnoreCase(reqDestino);
        return destino;
    }*/

    public void setDestino(boolean destino) {
        //this.destino = destino;
        this.reqDestino = (destino ? "S" : "N");
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
        if (!(object instanceof OpcionesKioskosApp)) {
            return false;
        }
        OpcionesKioskosApp other = (OpcionesKioskosApp) object;
        if ((this.secuencia == null && other.secuencia != null) || (this.secuencia != null && !this.secuencia.equals(other.secuencia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.com.kiosko.administrar.entidades.OpcionesKioskosApp[ secuencia=" + secuencia + " ]";
    }
}
