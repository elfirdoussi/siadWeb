package controller.util;

import entities.CarnetCommandeOf;
import entities.ProblemePre;
import java.util.Date;
import org.primefaces.model.timeline.TimelineEvent;

public class Event {

    private String id;
    private CarnetCommandeOf carnetCommandeOf;
    private ProblemePre problemePre;
    private Date dateStart;
    private Date dateEnd;
    private int debutEffectifaAbsolu;
    private int finAbsolue;
    private int indiceCommande;

    public void updateTempsAbsoluParAtelier(TimelineEvent timelineEvent) {
        double tmpAbs = DateUtil.hoursDifference(timelineEvent.getStartDate(), problemePre.getDateDebut());
        double finAbs = DateUtil.hoursDifference(timelineEvent.getEndDate(), problemePre.getDateDebut());
        
        int fin = Data.getInstance().getDateRelativeFin()[getIndiceCommande() - 1].intValue();
        int de = Data.getInstance().getDateRelativeDebutEffectifProd()[getIndiceCommande() - 1].intValue();
        int nligne = Data.getInstance().getLigneChoisi()[getIndiceCommande() - 1].intValue();
        Data.getInstance().getTempsAbsoluParAtelier().get(nligne)[de - 1] = tmpAbs;
        Data.getInstance().getTempsAbsoluParAtelier().get(nligne)[fin - 1] = finAbs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CarnetCommandeOf getCarnetCommandeOf() {
        return carnetCommandeOf;
    }

    public void setCarnetCommandeOf(CarnetCommandeOf carnetCommandeOf) {
        this.carnetCommandeOf = carnetCommandeOf;
    }

    public ProblemePre getProblemePre() {
        return problemePre;
    }

    public void setProblemePre(ProblemePre problemePre) {
        this.problemePre = problemePre;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getDebutEffectifaAbsolu() {
        return debutEffectifaAbsolu;
    }

    public void setDebutEffectifaAbsolu(int debutEffectifaAbsolu) {
        this.debutEffectifaAbsolu = debutEffectifaAbsolu;
    }

    public int getFinAbsolue() {
        return finAbsolue;
    }

    public void setFinAbsolue(int finAbsolue) {
        this.finAbsolue = finAbsolue;
    }

    public int getIndiceCommande() {
        return indiceCommande;
    }

    public void setIndiceCommande(int indiceCommande) {
        this.indiceCommande = indiceCommande;
    }

}
