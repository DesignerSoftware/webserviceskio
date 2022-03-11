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
import javax.persistence.TemporalType;

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
    private String fechainicio;
    @Column(name = "FECHAFIN")
    private String fechafin;
    @Column(name = "FORMATO")
    private String formato;
    @Column(name = "ESTADO")
    private String estado;
    @Column(name = "FECHAMODIFICADO")
    private String fechamodificado;

    public RrHh(BigInteger secuencia, String titulo, String descripcion, String nombreadjunto, String fechainicio, String fechafin, String formato, String estado, String fechamodificado) {
        this.secuencia = secuencia;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreadjunto = nombreadjunto;
        this.fechainicio = fechainicio;
        this.fechafin = fechafin;
        this.formato = formato;
        this.estado = estado;
        this.fechamodificado = fechamodificado;
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

    public String getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(String fechainicio) {
        this.fechainicio = fechainicio;
    }

    public String getFechafin() {
        return fechafin;
    }

    public void setFechafin(String fechafin) {
        this.fechafin = fechafin;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechamodificado() {
        return fechamodificado;
    }

    public void setFechamodificado(String fechamodificado) {
        this.fechamodificado = fechamodificado;
    }

    
        
}
