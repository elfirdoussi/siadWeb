package entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: TypeLigne
 *
 */
@Entity

public class TypeLigne implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private Long code;
    private String libelle;

    public TypeLigne() {
        super();
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.code);
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
        final TypeLigne other = (TypeLigne) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TypeLigne{" + "code=" + code + ", libelle=" + libelle + '}';
    }
   
}
