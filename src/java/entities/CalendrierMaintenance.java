/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Aimad.JAOUHAR
 */
@Entity
public class CalendrierMaintenance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long numcm;
    @ManyToOne
    private ProblemePre problemePre;
    private String auteur;
    @Temporal(TemporalType.DATE)
    private Date dateCreation;
    @ManyToOne
    private Ligne ligne;
    private boolean valeur;
    private int numPeriod;

    public String getDateHours() {
        String a = calcHours(this.numPeriod);
        String b = calcHours(this.numPeriod + 1);
        return (a.length() == 1 ? "0" + a : a) + "h - " + (b.length() == 1 ? "0" + b : b) + "h";
    }

    public boolean getValeur() {
        return valeur;
    }

    public String getStrValeur() {
        return valeur ? "yes" : "no";
    }

    private String calcHours(int hours) {
        return (hours - ((int) hours / 24) * 24) + "";
    }

    public Long getNumcm() {
        return numcm;
    }

    public void setNumcm(Long numcm) {
        this.numcm = numcm;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Ligne getLigne() {
        return ligne;
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
    }

    public boolean isValeur() {
        return valeur;
    }

    public void setValeur(boolean valeur) {
        this.valeur = valeur;
    }

    public int getNumPeriod() {
        return numPeriod;
    }

    public void setNumPeriod(int numPeriod) {
        this.numPeriod = numPeriod;
    }

    public ProblemePre getProblemePre() {
        return problemePre;
    }

    public void setProblemePre(ProblemePre problemePre) {
        this.problemePre = problemePre;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numcm != null ? numcm.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the numcm fields are not set
        if (!(object instanceof CalendrierMaintenance)) {
            return false;
        }
        CalendrierMaintenance other = (CalendrierMaintenance) object;
        if ((this.numcm == null && other.numcm != null) || (this.numcm != null && !this.numcm.equals(other.numcm))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.CalendrierMaintenance[ numcm=" + numcm + " ]";
    }

}
