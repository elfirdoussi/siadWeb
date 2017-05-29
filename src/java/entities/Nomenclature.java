/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Aimad.JAOUHAR
 */
@Entity
public class Nomenclature implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Engrais engrais;
    @ManyToOne
    private TypeLigne typeLigne;
    @OneToMany(fetch = FetchType.EAGER)
    private List<NomenclatureItem> nomenclatureItems;
    

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

    public Engrais getEngrais() {
        return engrais;
    }

    public void setEngrais(Engrais engrais) {
        this.engrais = engrais;
    }

    public List<NomenclatureItem> getNomenclatureItems() {
        if(nomenclatureItems == null){
            nomenclatureItems = new ArrayList<>();
        }
        return nomenclatureItems;
    }

    public void setNomenclatureItems(List<NomenclatureItem> nomenclatureItems) {
        this.nomenclatureItems = nomenclatureItems;
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
        if (!(object instanceof Nomenclature)) {
            return false;
        }
        Nomenclature other = (Nomenclature) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Nomenclature[ id=" + id + " ]";
    }
    
}
