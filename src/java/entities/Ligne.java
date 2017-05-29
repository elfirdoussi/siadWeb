package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Ligne
 *
 */
@Entity
public class Ligne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long numeroLigne;
    private String nomLigne;
    @ManyToOne(fetch = FetchType.EAGER)
    private Site site;
    @ManyToOne(fetch = FetchType.EAGER)
    private TypeLigne typeLigne;
    @OneToMany
    private List<CarnetCommandeOf> carnetCommandeOfs;

    public Ligne() {
        super();
    }

    public Long getNumeroLigne() {
        return numeroLigne;
    }

    public void setNumeroLigne(Long numeroLigne) {
        this.numeroLigne = numeroLigne;
    }

    public String getNomLigne() {
        return nomLigne;
    }
    
    public void setNomLigne(String nomLigne) {
        this.nomLigne = nomLigne;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public TypeLigne getTypeLigne() {
        return typeLigne;
    }

    public void setTypeLigne(TypeLigne typeLigne) {
        this.typeLigne = typeLigne;
    }

    public List<CarnetCommandeOf> getCarnetCommandeOfs() {
        if (carnetCommandeOfs == null) {
            carnetCommandeOfs = new ArrayList<>();
        }
        return carnetCommandeOfs;
    }

    public void setCarnetCommandeOfs(List<CarnetCommandeOf> carnetCommandeOfs) {
        this.carnetCommandeOfs = carnetCommandeOfs;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.numeroLigne);
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
        final Ligne other = (Ligne) obj;
        if (!Objects.equals(this.numeroLigne, other.numeroLigne)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Ligne{" + "numeroLigne=" + numeroLigne + ", nomLigne=" + nomLigne + ", site=" + site + ", typeLigne=" + typeLigne + ", carnetCommandeOfs=" + carnetCommandeOfs + '}';
    }

    
    
}
