/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Ligne;
import entities.ProblemeLigneOuverte;
import entities.ProblemePre;
import entities.RegimeMarche;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class RegimeMarcheFacade extends AbstractFacade<RegimeMarche> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public RegimeMarche findByLigneAndProblemePre(ProblemePre problemePre, Ligne ligne) {
        if (problemePre == null || ligne == null) {
            return null;
        }
        List<RegimeMarche> regimeMarches = findByCrtitere(problemePre, ligne);
        return !regimeMarches.isEmpty() ? regimeMarches.get(0) : null;
    }

    public void removeAll(List<RegimeMarche> regimeMarches) {
        regimeMarches.forEach((r) -> {
            r.setProblemePre(null);
            remove(r);
        });
    }

    public void saveAll(List<RegimeMarche> regimeMarches) {
        regimeMarches.forEach((r) -> {
            edit(r);
        });
    }

    public void supprimerRegimeMarche(ProblemeLigneOuverte problemeLigneOuverte, List<RegimeMarche> regimeMarches) {
        for (int i = 0; i < regimeMarches.size(); i++) {
            if (regimeMarches.get(i).getLigne().equals(problemeLigneOuverte.getLigne())) {
                regimeMarches.remove(i);
            }
        }
    }

    public void ajouterRegimeMarche(ProblemeLigneOuverte problemeLigneOuverte, List<RegimeMarche> regimeMarches) {
        RegimeMarche regimeMarche = new RegimeMarche();
        regimeMarche.setLigne(problemeLigneOuverte.getLigne());
        regimeMarche.setProblemePre(problemeLigneOuverte.getProblemePre());
        regimeMarche.setValeur(1f);
        regimeMarches.add(regimeMarche);
    }

    public List<RegimeMarche> prepareRegimeMarches(List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        List<RegimeMarche> regimeMarches = new ArrayList<>();
        for (ProblemeLigneOuverte problemeLigneOuverte : problemeLigneOuvertes) {
            RegimeMarche regimeMarche = new RegimeMarche();
            regimeMarche.setLigne(problemeLigneOuverte.getLigne());
            regimeMarche.setValeur(0f);
            regimeMarches.add(regimeMarche);
        }
        return regimeMarches;
    }

    public List<RegimeMarche> findByProblemePre(ProblemePre problemePre) {
        return em.createQuery("select r from RegimeMarche r where r.problemePre.nump=" + problemePre.getNump()).getResultList();
    }

    public List<RegimeMarche> findByCrtitere(ProblemePre problemePre, Ligne ligne) {
        String query = "select r from RegimeMarche r where 1=1 ";
        if (problemePre != null) {
            query += " and r.problemePre.nump=" + problemePre.getNump();
        }
        if (ligne != null) {
            query += " and r.ligne.numeroLigne=" + ligne.getNumeroLigne();
        }
        return getEntityManager().createQuery(query).getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RegimeMarcheFacade() {
        super(RegimeMarche.class);
    }

}
