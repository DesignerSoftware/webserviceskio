package co.com.designer.kiosko.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//import javax.persistence.Transient;
//import javax.validation.constraints.Size; 200817
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Edwin Yesid Hastamorir
 */
@Entity
@Table(name = "CONEXIONESKIOSKOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConexionesKioskos.findAll", query = "SELECT c FROM ConexionesKioskos c")})
public class ConexionesKioskos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SECUENCIA")
    private BigDecimal secuencia;
   // @Size(max = 20, message = "El seudónimo puede tener máximo 20 letras.") 200817
    @Column(name = "SEUDONIMO")
    private String seudonimo;
//    @Column(name = "PWD")
//    private byte[] pwd;
 //   @Size(max = 1) 200817
    @Column(name = "ACTIVO")
    private String activo;
//    @Size(max = 50)
//    @Column(name = "RESPUESTA1")
//    private byte[] respuesta1;
//    @Size(max = 50)
//    @Column(name = "RESPUESTA2")
//    private byte[] respuesta2;
    @Column(name = "ULTIMACONEXION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimaconexion;
    @Column(name = "FECHADESDE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechadesde;
    @Column(name = "FECHAHASTA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechahasta;
    // @Size(max = 1) 200817
    @Column(name = "ENVIOCORREO")
    private String enviocorreo;
    // @Size(max = 200) 200817
    @Column(name = "OBSERVACIONES")
    private String observaciones;
    // @Size(max = 200) 200817
    @Column(name = "DIRIGIDOA")
    private String dirigidoa;
    @Column(name = "NITEMPRESA")
    private long nitEmpresa;
    @Column(name = "PERSONA")
    private BigDecimal persona;
//    @Transient
//    private String respuesta1UI;
//    @Transient
//    private String respuesta2UI;
//    @Transient
//    private boolean envioCorreo;

    public ConexionesKioskos() {
    }

    public ConexionesKioskos(BigDecimal secuencia) {
        this.secuencia = secuencia;
    }

    public BigDecimal getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(BigDecimal secuencia) {
        this.secuencia = secuencia;
    }

    public String getSeudonimo() {
        return seudonimo;
    }

    public void setSeudonimo(String seudonimo) {
        this.seudonimo = seudonimo;
    }

    /*
    public byte[] getPwd() {
        return pwd;
    }

    public void setPwd(byte[] pwd) {
        this.pwd = pwd;
    }
     */
    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    /*
    public byte[] getRespuesta1() {
        return respuesta1;
    }

    public void setRespuesta1(byte[] respuesta1) {
        this.respuesta1 = respuesta1;
    }
     */

 /*
    public byte[] getRespuesta2() {
        return respuesta2;
    }

    public void setRespuesta2(byte[] respuesta2) {
        this.respuesta2 = respuesta2;
    }
     */
    public Date getUltimaconexion() {
        return ultimaconexion;
    }

    public void setUltimaconexion(Date ultimaconexion) {
        this.ultimaconexion = ultimaconexion;
    }

    public Date getFechadesde() {
        return fechadesde;
    }

    public void setFechadesde(Date fechadesde) {
        this.fechadesde = fechadesde;
    }

    public Date getFechahasta() {
        return fechahasta;
    }

    public void setFechahasta(Date fechahasta) {
        this.fechahasta = fechahasta;
    }

    public String getEnviocorreo() {
        return enviocorreo;
    }

    public void setEnviocorreo(String enviocorreo) {
        this.enviocorreo = enviocorreo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDirigidoa() {
        return dirigidoa;
    }

    public void setDirigidoa(String dirigidoa) {
        this.dirigidoa = dirigidoa;
    }

    public long getNitEmpresa() {
        return nitEmpresa;
    }

    public void setNitEmpresa(long nitEmpresa) {
        this.nitEmpresa = nitEmpresa;
    }

    public BigDecimal getPersona() {
        return persona;
    }

    public void setPersona(BigDecimal persona) {
        this.persona = persona;
    }

    /*
    public String getRespuesta1UI() {
        return respuesta1UI;
    }

    public void setRespuesta1UI(String respuesta1UI) {
        this.respuesta1UI = respuesta1UI;
    }
     */
 /*
    public String getRespuesta2UI() {
        return respuesta2UI;
    }

    public void setRespuesta2UI(String respuesta2UI) {
        this.respuesta2UI = respuesta2UI;
    }
     */
 /*
    public boolean isEnvioCorreo() {
        return envioCorreo;
    }

    public void setEnvioCorreo(boolean envioCorreo) {
        this.envioCorreo = envioCorreo;
    }
     */

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (secuencia != null ? secuencia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConexionesKioskos)) {
            return false;
        }
        ConexionesKioskos other = (ConexionesKioskos) object;
        if ((this.secuencia == null && other.secuencia != null) || (this.secuencia != null && !this.secuencia.equals(other.secuencia))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.com.designer.kiosko.entidades.ConexionesKioskos[ id=" + secuencia + " ]";
    }

}
