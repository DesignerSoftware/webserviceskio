/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.kiosko.entidades;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author UPC007
 */
@Entity
public class RrHh  implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SECUENCIA")
    private BigInteger secuencia;
    @Column(name = "TITULO")
    private String titulo;
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Column(name = "NOMBREADJUNTO")
    private String nombreadjunto;
    @Column(name = "FECHAINICIO")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechainicio;
    @Column(name = "FECHAFIN")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechafin;
    @Column(name = "FORMATO")
    private String formato;

    public RrHh(BigInteger secuencia, String titulo, String descripcion, String nombreadjunto, Date fechainicio, Date fechafin, String formato) {
        this.secuencia = secuencia;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreadjunto = nombreadjunto;
        this.fechainicio = fechainicio;
        this.fechafin = fechafin;
        this.formato = formato;
    }

    public RrHh() {
    }

    public BigInteger getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigInteger secuencia) {
        this.secuencia = secuencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreadjunto() {
        return nombreadjunto;
    }

    public void setNombreadjunto(String nombreadjunto) {
        this.nombreadjunto = nombreadjunto;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    
    
}
