package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

/**
 *
 * @author Aimad.JAOUHAR
 */
@Entity
public class ProblemePost implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private ProblemePre problemePre;
    @ManyToOne(fetch = FetchType.EAGER)
    private CritereOptimisation critereOptimisation;

    public ProblemePost() {
        super();
    }

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

    public CritereOptimisation getCritereOptimisation() {
        return critereOptimisation;
    }

    public void setCritereOptimisation(CritereOptimisation critereOptimisation) {
        this.critereOptimisation = critereOptimisation;
    }
    
}
