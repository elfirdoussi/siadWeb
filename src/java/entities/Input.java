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

/**
 *
 * @author Aimad.JAOUHAR
 */
@Entity
public class Input implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long numInput;
    private String nomInput;

    public Long getNumInput() {
        return numInput;
    }

    public void setNumInput(Long numInput) {
        this.numInput = numInput;
    }

    public String getNomInput() {
        return nomInput;
    }

    public void setNomInput(String nomInput) {
        this.nomInput = nomInput;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numInput != null ? numInput.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the numInput fields are not set
        if (!(object instanceof Input)) {
            return false;
        }
        Input other = (Input) object;
        if ((this.numInput == null && other.numInput != null) || (this.numInput != null && !this.numInput.equals(other.numInput))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Input[ numInput=" + numInput + " ]";
    }
    
}
