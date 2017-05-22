package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



@Entity
public class ProblemePre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long nump;
    private String auteur;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;
    private Integer numCm;
    @ManyToOne(fetch = FetchType.EAGER)
    private CarnetCommande carnetCommande;
    @OneToMany(mappedBy = "problemePre", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProblemeLigneOuverte> problemeLigneOuvertes;

    public ProblemePre() {
        super();
    }

    public Long getNump() {
        return nump;
    }

    public void setNump(Long nump) {
        this.nump = nump;
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

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getNumCm() {
        return numCm;
    }

    public void setNumCm(Integer numCm) {
        this.numCm = numCm;
    }

    public CarnetCommande getCarnetCommande() {
        return carnetCommande;
    }

    public void setCarnetCommande(CarnetCommande carnetCommande) {
        this.carnetCommande = carnetCommande;
    }

    public List<ProblemeLigneOuverte> getProblemeLigneOuvertes() {
        if(problemeLigneOuvertes == null){
            problemeLigneOuvertes = new ArrayList<>();
        }
        return problemeLigneOuvertes;
    }

    public void setProblemeLigneOuvertes(List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        this.problemeLigneOuvertes = problemeLigneOuvertes;
    }

    @Override
    public String toString() {
        return "ProblemePre{" + "nump=" + nump + ", auteur=" + auteur + ", dateCreation=" + dateCreation + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", numCm=" + numCm + ", carnetCommande=" + carnetCommande + ", problemeLigneOuvertes=" + problemeLigneOuvertes + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.nump);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProblemePre other = (ProblemePre) obj;
        if (!Objects.equals(this.nump, other.nump)) {
            return false;
        }
        return true;
    }
    
    
    
}
