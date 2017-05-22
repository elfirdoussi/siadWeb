/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Aimad.JAOUHAR
 */
@Entity
public class Gamme implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Engrais engrais;
    @ManyToOne
    private TypeLigne typeLigne;
    private Float qtteProduite;
    private Float coutVariable;
    private Float ratio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Engrais getEngrais() {
        return engrais;
    }

    public void setEngrais(Engrais engrais) {
        this.engrais = engrais;
    }

    public TypeLigne getTypeLigne() {
        return typeLigne;
    }

    public void setTypeLigne(TypeLigne typeLigne) {
        this.typeLigne = typeLigne;
    }

    public Float getQtteProduite() {
        return qtteProduite;
    }

    public void setQtteProduite(Float qtteProduite) {
        this.qtteProduite = qtteProduite;
    }

    public Float getCoutVariable() {
        return coutVariable;
    }

    public void setCoutVariable(Float coutVariable) {
        this.coutVariable = coutVariable;
    }

    public Float getRatio() {
        return ratio;
    }

    public void setRatio(Float ratio) {
        this.ratio = ratio;
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
        if (!(object instanceof Gamme)) {
            return false;
        }
        Gamme other = (Gamme) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Gamme[ id=" + id + " ]";
    }
    
}
