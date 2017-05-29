/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.CarnetCommandeOf;
import entities.Ligne;
import entities.Site;
import entities.TypeLigne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import controller.util.CodeGanttCourbeUtil;
import controller.util.Data;
import controller.util.DateUtil;
import controller.util.Event;
import entities.ProblemePre;
import java.util.HashMap;

/**
 *
 * @author AIMAD
 */
@Stateless
public class LigneFacade extends AbstractFacade<Ligne> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private EngraisFacade engraisFacade;
    @EJB
    private CarnetCommandeOfFacade carnetCommandeOfFacade;
    @EJB
    private ProblemePreFacade problemePreFacade;
    @EJB
    private LigneFacade ligneFacade;

    public List<Ligne> getLignesWithCarnetCommandeOfTxt(Long nump, Date dateDebut, Date dateFin, HashMap<String, Event> eventMap, HashMap<String, Date[][]> timeLineDatesMap) {
        List<Ligne> lignes = new ArrayList<>();
        int nl = Data.getInstance().getNbreAteliers();
        for (int i = 1; i <= nl; i++) {
            Ligne ligne = prepareCarnetLigneTxt(Data.getInstance().getAteliersList().get(i - 1));
            lignes.add(ligne);
            if (!lignes.isEmpty()) {
                ligne.setCarnetCommandeOfs(getCarnetCommandeOfsFromTxt(nump, i, dateDebut, dateFin, eventMap, ligne, timeLineDatesMap));
            }
        }
        return lignes;
    }

    public List<Ligne> getWithCarnetCommandeOfTxtArrets(Long nump, Date dateDebut, Date dateFin, HashMap<String, Event> eventMap) {
        List<Ligne> lignes = new ArrayList<>();
        int nl = Data.getInstance().getNbreAteliers();
        for (int i = 1; i <= nl; i++) {
            Ligne ligne = prepareCarnetLigneTxt(Data.getInstance().getAteliersList().get(i - 1));
            lignes.add(ligne);
            if (!lignes.isEmpty()) {
                ligne.getCarnetCommandeOfs().addAll(getCarnetCommandeOfsArretFromTxt(nump, i, dateDebut, dateFin, eventMap, problemePreFacade.find(nump),ligne));
            }
        }
        return lignes;
    }

    private CarnetCommandeOf prepareCarnetCommandeOfTxt(int indexCommande, Long numOf, String qualite, Date dateDebut, Date dateFin, int debutEffectifAbs, int finAbs, HashMap<String, Event> eventMap, ProblemePre problemePre, Ligne ligne) {
        CarnetCommandeOf carnetCommandeOf = new CarnetCommandeOf();
        System.out.println("kooooooooooooooooooooo numero of: " + numOf);
        carnetCommandeOf.setNumeroOf(numOf);
        carnetCommandeOf.setEngrais(engraisFacade.findByExactLibelle(qualite));
        carnetCommandeOf.setDateLiveTot(DateUtil.addHourOfDayCalendarToDate(debutEffectifAbs, dateDebut));
        carnetCommandeOf.setDateLiveTard(DateUtil.addHourOfDayCalendarToDate(finAbs, dateFin));
        Event event = new Event();
        event.setId(carnetCommandeOf.getTitle());
        event.setProblemePre(problemePre);
        event.setCarnetCommandeOf(carnetCommandeOfFacade.findByProblemePreAndNumOf(problemePre, numOf));
        event.setDateStart(carnetCommandeOf.getDateLiveTot());
        event.setDateEnd(carnetCommandeOf.getDateLiveTard());
        event.setFinAbsolue(finAbs);
        event.setDebutEffectifaAbsolu(debutEffectifAbs);
        event.setIndiceCommande(indexCommande);
        event.setLigne(ligneFacade.findByExactLibelle(ligne.getNomLigne()));
        eventMap.put(carnetCommandeOf.getTitle(), event);
        return carnetCommandeOf;
    }

    private Ligne prepareCarnetLigneTxt(String nomLigne) {
        Ligne ligne = new Ligne();
        ligne.setNomLigne(nomLigne);
        return ligne;
    }

    List<CarnetCommandeOf> getCarnetCommandeOfsFromTxt(Long nump, int indexLigne, Date dateDebut, Date dateFin, HashMap<String, Event> eventMap, Ligne ligne, HashMap<String, Date[][]> timeLineDates) {
        List<CarnetCommandeOf> carnetCommandeOfs = new ArrayList<>();
        List<CarnetCommandeOf> loadedCarnetCommandeOfs = carnetCommandeOfFacade.findByNump(nump);
        int nc = Data.getInstance().getNbreCommandes();
        Date[][] dates = new Date[2][nc];
        System.out.println("############################################");
        System.out.println("#############  "+ligne.getNomLigne()+"   ###################");
        for (int i = 1; i <= nc; i++) {
            if (indexLigne == Data.getInstance().getLigneChoisi()[i - 1].intValue()) {
                CarnetCommandeOf carnetCommandeOf = prepareCarnetCommandeOfTxt(
                        i,
                        loadedCarnetCommandeOfs.get(i - 1).getNumeroOf(),
                        (String) Data.getInstance().getQualiteParCommande().get(i),
                        dateDebut,
                        dateDebut,
                        CodeGanttCourbeUtil.debutEffectifaAbsolu(i),
                        CodeGanttCourbeUtil.finAbsolue(i),
                        eventMap,
                        problemePreFacade.find(nump),
                        ligne);
                carnetCommandeOfs.add(carnetCommandeOf);
                dates[0][i - 1] = carnetCommandeOf.getDateLiveTot();
                dates[1][i - 1] = carnetCommandeOf.getDateLiveTard();
                System.out.println("date 0 : "+dates[0][i - 1]);
                System.out.println("date 1 : "+dates[1][i - 1]);
            }
        }
        timeLineDates.put(ligne.getNomLigne(), dates);
        System.out.println("############################################");
        return carnetCommandeOfs;
    }

    private List<CarnetCommandeOf> getCarnetCommandeOfsArretFromTxt(Long nump, int indexLigne, Date dateDebut, Date dateFin, HashMap<String, Event> eventMap, ProblemePre problemePre, Ligne ligne) {
        List<CarnetCommandeOf> carnetCommandeOfs = new ArrayList<>();
        List<String> listArrets = Data.getInstance().getArretParLigne().get(indexLigne);
        System.out.println("###################################");
        for (int i = 1; i <= listArrets.size(); i++) {
            carnetCommandeOfs.add(
                    prepareCarnetCommandeOfTxt(
                            i,
                            0l,
                            "",
                            dateDebut,
                            DateUtil.addHourOfDayCalendarToDate(Integer.valueOf(listArrets.get(i - 1)), dateDebut),
                            Integer.valueOf(listArrets.get(i - 1)),
                            1,
                            eventMap,
                            problemePre,
                            ligne)
            );
            System.out.println("begin " + carnetCommandeOfs.get(i - 1).getDateLiveTot());
            System.out.println("end " + carnetCommandeOfs.get(i - 1).getDateLiveTard());
        }
        System.out.println("###################################");
        return carnetCommandeOfs;
    }

    public Ligne findByExactLibelle(String libelle, Site site, TypeLigne typeLigne) {
        Ligne ligne = null;
        try {
            ligne = (Ligne) em.createQuery("select l from Ligne l where l.nomLigne ='" + libelle + "' and l.site.id=" + site.getId() + " and l.typeLigne.code=" + typeLigne.getCode()).getSingleResult();
        } catch (NoResultException e) {

        }
        return ligne;
    }

    /// A REFAIRE !!
    public Ligne findByExactLibelle(String libelle) {
        System.out.println("libelle => "+libelle);
        Ligne ligne = null;
        try {
            ligne = (Ligne) em.createQuery("select l from Ligne l where l.nomLigne like '" + libelle + "' ").getSingleResult();
            System.out.println("haaaaaaa "+ligne);
        } catch (NoResultException e) {

        }
        return ligne;
    }

    public List<Ligne> findBySite(Site site) {
        return em.createQuery("select l from Ligne l where l.site.id=" + site.getId()).getResultList();
    }

    public List<Ligne> findByCriteres(Long numeroLigne, String nomLigne, Site site, TypeLigne typeLigne) {
        String query = "select l from Ligne l where 1=1 ";
        if (numeroLigne != null) {
            query += " and l.numeroLigne=" + numeroLigne;
        }
        if (nomLigne != null) {
            query += " and l.nomLigne like '%" + nomLigne + "%'";
        }
        if (site != null) {
            query += " and l.site.id=" + site.getId();
        }
        if (typeLigne != null) {
            query += " and l.typeLigne.code=" + typeLigne.getCode();
        }
        return em.createQuery(query).getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LigneFacade() {
        super(Ligne.class);
    }

}
