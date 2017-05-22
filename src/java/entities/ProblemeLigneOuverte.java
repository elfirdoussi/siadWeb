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
public class ProblemeLigneOuverte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProblemePre problemePre;
    @ManyToOne
    private Ligne ligne;
    @ManyToOne
    private Ligne ligne2;
    private Integer couplee;
    private Integer ordre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProblemePre getProblemePre() {
        return problemePre;
    }

    public void setProblemePre(ProblemePre problemePre) {
        this.problemePre = problemePre;
    }

    public Ligne getLigne() {
        return ligne;
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
    }

    public Integer getCouplee() {
        return couplee;
    }

    public void setCouplee(Integer couplee) {
        this.couplee = couplee;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public Ligne getLigne2() {
        return ligne2;
    }

    public void setLigne2(Ligne ligne2) {
        this.ligne2 = ligne2;
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
        if (!(object instanceof ProblemeLigneOuverte)) {
            return false;
        }
        ProblemeLigneOuverte other = (ProblemeLigneOuverte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ProblemeLigneOuverte[ id=" + id + " ]";
    }

}
