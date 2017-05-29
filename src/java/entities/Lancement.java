/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Aimad.JAOUHAR
 */
@Entity
public class Lancement implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private TypeLigne typeLigne;
    @ManyToOne(fetch = FetchType.EAGER)
    private Engrais engraisp;
    @ManyToOne(fetch = FetchType.EAGER)
    private Engrais engraisc;
    private Float tempsl;
    private Float coutl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeLigne getTypeLigne() {
        return typeLigne;
    }

    public void setTypeLigne(TypeLigne typeLigne) {
        this.typeLigne = typeLigne;
    }

    public Engrais getEngraisp() {
        return engraisp;
    }

    public void setEngraisp(Engrais engraisp) {
        this.engraisp = engraisp;
    }

    public Engrais getEngraisc() {
        return engraisc;
    }

    public void setEngraisc(Engrais engraisc) {
        this.engraisc = engraisc;
    }

    public Float getTempsl() {
        return tempsl;
    }

    public void setTempsl(Float tempsl) {
        this.tempsl = tempsl;
    }

    public Float getCoutl() {
        return coutl;
    }

    public void setCoutl(Float coutl) {
        this.coutl = coutl;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lancement)) {
            return false;
        }
        Lancement other = (Lancement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Lancement[ id=" + id + " ]";
    }
    
}
