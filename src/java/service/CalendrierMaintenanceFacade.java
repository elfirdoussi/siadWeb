/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.util.DateUtil;
import controller.util.Item;
import entities.CalendrierMaintenance;
import entities.Ligne;
import entities.ProblemeLigneOuverte;
import entities.ProblemePre;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class CalendrierMaintenanceFacade extends AbstractFacade<CalendrierMaintenance> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private ProblemePreFacade problemePreFacade;
    @EJB
    private LigneFacade ligneFacade;

    public void setup() {
        List<Ligne> lignes = new ArrayList<>();
        lignes.add(ligneFacade.find(1l));
        lignes.add(ligneFacade.find(2l));
        lignes.add(ligneFacade.find(5l));
        lignes.add(ligneFacade.find(6l));
        for (Ligne ligne : lignes) {
            for (int i = 1; i < 383; i++) {
                CalendrierMaintenance calendrierMaintenance = new CalendrierMaintenance();
                calendrierMaintenance.setProblemePre(problemePreFacade.find(8000l));
                calendrierMaintenance.setLigne(ligne);
                calendrierMaintenance.setNumPeriod(i);
                calendrierMaintenance.setValeur(false);
                create(calendrierMaintenance);
            }
        }

    }

    public List<CalendrierMaintenance> findByProblemePreAndLigneAndPeriode(ProblemePre problemePre, Ligne ligne, int periode) {
        return getEntityManager().createQuery("select c from CalendrierMaintenance c where c.numPeriod = " + periode + " and c.ligne.numeroLigne=" + ligne.getNumeroLigne() + " and c.problemePre.nump=" + problemePre.getNump()).getResultList();
    }

    public int sumValeurPeriodeByProblemePreAndLigneAndPeriode(ProblemePre problemePre, Ligne ligne, int periode) {
        List<CalendrierMaintenance> calendrierMaintenances = findByProblemePreAndLigneAndPeriodeForSum(problemePre, ligne, periode);
        int sum = 0;
        if (calendrierMaintenances != null) {
            for (CalendrierMaintenance calendrierMaintenance : calendrierMaintenances) {
                sum += calendrierMaintenance.isValeur() ? 1 : 0;
            }
        }
        return sum;
    }

    public List<CalendrierMaintenance> findByProblemePreAndLigneAndPeriodeForSum(ProblemePre problemePre, Ligne ligne, int periode) {
        if (problemePre == null || ligne == null || periode == -1) {
            return null;
        }
        return getEntityManager().createQuery("select c from CalendrierMaintenance c where c.numPeriod < " + periode + " and c.ligne.numeroLigne=" + ligne.getNumeroLigne() + " and c.problemePre.nump=" + problemePre.getNump()).getResultList();
    }

    public void removeAll(List<CalendrierMaintenance> calendrierMaintenances) {
        calendrierMaintenances.forEach((c) -> {
            c.setProblemePre(null);
            remove(c);
        });
    }

    public void saveAllByLigne(HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        problemeLigneOuvertes.forEach((p) -> saveAll(calendrierMaintenanceMap.get(p.getLigne())));
    }

    public void saveAll(List<CalendrierMaintenance> calendrierMaintenances) {
        calendrierMaintenances.forEach((c) -> {
            edit(c);
        });
    }

    public List<CalendrierMaintenance> searchCalendrierMaintenance(Ligne ligne, int indexDate, HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap) {
        if (ligne != null && indexDate != -1) {
            return calendrierMaintenanceMap.get(ligne).subList(DateUtil.getPeriodeMinMax(indexDate, indexDate)[0], DateUtil.getPeriodeMinMax(indexDate, indexDate)[1]);
        }
        return new ArrayList<>();
    }

    public void prepareCalendrierMaintenance(ProblemePre problemePre, List<Item> dates, List<ProblemeLigneOuverte> problemeLigneOuvertes, HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap) {
        dates.clear();
        dates.addAll(prepareDates(DateUtil.getDatesBetween(problemePre.getDateDebut(), problemePre.getDateFin())));
        System.out.println("haaaaaaaaaaaaaaa dates " + dates.size());
        int[] periodeMinMax = DateUtil.getPeriodeMinMax(DateUtil.getNumberOfPeriod(problemePre.getDateDebut(), problemePre.getDateFin()));
        for (ProblemeLigneOuverte problemeLigneOuverte : problemeLigneOuvertes) {
            System.out.println("haaaaaa prob lihe " + problemeLigneOuverte);
            if (problemeLigneOuverte.getId() == -1 || problemeLigneOuverte.getId() == null) {
                System.out.println("haaaaaaaaaaaaaa jaaaaaa ");
                prepareCalendrierMaintenanceByLigne(problemeLigneOuverte.getLigne(), periodeMinMax[0], periodeMinMax[1], problemePre, calendrierMaintenanceMap);
            }
        }
    }

    private List<Item> prepareDates(List<Date> dates) {
        List<Item> items = new ArrayList<>();
        for (Date date : dates) {
            Item item = new Item();
            item.setKey(DateUtil.strDateDefaultPattern(date));
            item.setObject(date);
            items.add(item);
        }
        return items;
    }

    private void prepareCalendrierMaintenanceByLigne(Ligne ligne, int periodMin, int periodMax, ProblemePre problemePre, HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap) {
        List<CalendrierMaintenance> calendrierMaintenances = new ArrayList<>();
        for (int i = periodMin; i < periodMax; i++) {
            CalendrierMaintenance calendrierMaintenance = new CalendrierMaintenance();
            calendrierMaintenance.setNumPeriod(i);
            calendrierMaintenance.setValeur(false);
            calendrierMaintenance.setLigne(ligne);
            calendrierMaintenance.setProblemePre(problemePre);
            calendrierMaintenances.add(calendrierMaintenance);
        }
        if(calendrierMaintenanceMap.get(ligne) != null){
            System.out.println("haaaaaaaaaaaaa hakiii cccc");
            calendrierMaintenanceMap.get(ligne).addAll(calendrierMaintenances);
        }else{
            calendrierMaintenanceMap.put(ligne, calendrierMaintenances);
        }
    }

    public void prepareCalendrierMaintenanceByLigne(ProblemePre problemePre, HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap, List<ProblemeLigneOuverte> problemeLigneOuvertes, List<Item> dates) {
        dates.addAll(prepareDates(DateUtil.getDatesBetween(problemePre.getDateDebut(), problemePre.getDateFin())));
        problemeLigneOuvertes.forEach((p) -> {
            calendrierMaintenanceMap.put(p.getLigne(), findByProblemePreAndLigne(problemePre, p.getLigne()));
        });
    }

    public void correctionDates(ProblemePre problemePre, List<ProblemeLigneOuverte> problemeLigneOuvertes, HashMap<Ligne, List<CalendrierMaintenance>> calendrierMaintenanceMap) {
        int[] periodeMinMax = DateUtil.getPeriodeMinMax(DateUtil.getNumberOfPeriod(problemePre.getDateDebut(), problemePre.getDateFin()));
        for (ProblemeLigneOuverte problemeLigneOuverte : problemeLigneOuvertes) {
//            if (problemeLigneOuverte.getId() == -1 || problemeLigneOuverte.getId() == null) {
                System.out.println("le min "+getMaxPeriod(problemePre));
                System.out.println("le max "+periodeMinMax[1]);
                prepareCalendrierMaintenanceByLigne(problemeLigneOuverte.getLigne(), getMaxPeriod(problemePre), periodeMinMax[1], problemePre, calendrierMaintenanceMap);
//            }
        }
    }

    private int getMaxPeriod(ProblemePre problemePre) {
        Integer max = -1;
        try {
            max = (Integer) em.createQuery("select max(c.numPeriod) from CalendrierMaintenance c where c.problemePre.nump="+problemePre.getNump()).getSingleResult();
        } catch (NoResultException e) {

        }
        return max;
    }

    public List<CalendrierMaintenance> findByProblemePre(ProblemePre problemePre) {
        return em.createQuery("select c from CalendrierMaintenance c where c.problemePre.nump=" + problemePre.getNump()).getResultList();
    }

    public List<CalendrierMaintenance> findByProblemePreAndLigne(ProblemePre problemePre, Ligne ligne) {
        return em.createQuery("select c from CalendrierMaintenance c where c.ligne.numeroLigne=" + ligne.getNumeroLigne() + " and c.problemePre.nump=" + problemePre.getNump() + " order by c.numPeriod").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalendrierMaintenanceFacade() {
        super(CalendrierMaintenance.class);
    }

}
