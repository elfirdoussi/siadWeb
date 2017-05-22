package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

/**
 *
 * @author Aimad.JAOUHAR
 */
@Entity
public class CarnetCommande implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dateCreation;
    @OneToMany(mappedBy = "carnetCommande")
    private List<CarnetCommandeOf> carnetCommandeOfs;

    public CarnetCommande() {
        super();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
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
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final CarnetCommande other = (CarnetCommande) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CarnetCommande{" + "id=" + id + ", dateCreation=" + dateCreation + ", carnetCommandeOfs=" + carnetCommandeOfs + '}';
    }

}
