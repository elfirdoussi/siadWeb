/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.ProblemeLigneOuverte;
import entities.ProblemePre;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class ProblemeLigneOuverteFacade extends AbstractFacade<ProblemeLigneOuverte> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public int countProblemeLigneOuvert(ProblemePre problemePre) {
        Long count = 0l;
        try {
            count = (Long) getEntityManager().createQuery("select count(p.id) from ProblemeLigneOuverte p where p.problemePre.nump=" + problemePre.getNump()).getSingleResult();
        } catch (Exception e) {
        }
        return count.intValue();
    }

    public List<ProblemeLigneOuverte> findByProblemePreOrderByOrder(ProblemePre problemePre) {
        return getEntityManager().createQuery("select p from ProblemeLigneOuverte p where p.problemePre.nump=" + problemePre.getNump() + " order by p.ordre").getResultList();
    }

    public void removeAll(List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        problemeLigneOuvertes.forEach((p) -> {
            p.setProblemePre(null);
            remove(p);
        });
    }

    public void saveAll(List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        problemeLigneOuvertes.forEach((p) -> {
            if (p.getId() == -1) {
                p.setId(null);
            }
            edit(p);
        });
    }

    public int modifierProblemeLigneOuverte(ProblemeLigneOuverte problemeLigneOuverte, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        for (int i = 0; i < problemeLigneOuvertes.size(); i++) {
            if (problemeLigneOuvertes.get(i).getLigne().equals(problemeLigneOuverte.getLigne())) {
                problemeLigneOuvertes.set(i, problemeLigneOuverte);
                return 1;
            }
        }
        return -1;
    }

    public int supprimerProblemeLigneOuverte(ProblemeLigneOuverte problemeLigneOuverte, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        for (int i = 0; i < problemeLigneOuvertes.size(); i++) {
            if (problemeLigneOuvertes.get(i).getLigne().equals(problemeLigneOuverte.getLigne())) {
                problemeLigneOuvertes.remove(i);
                return 1;
            }
        }
        return -1;
    }

    public int addProblemeLigneOuvert(ProblemePre problemePre, ProblemeLigneOuverte problemeLigneOuverte, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        int code = verifyAddProblem(problemePre, problemeLigneOuverte, problemeLigneOuvertes);
        if (code > 0) {
            problemeLigneOuverte.setId(-1l);
            problemeLigneOuverte.setProblemePre(problemePre);
            problemeLigneOuvertes.add(problemeLigneOuverte);
        }
        return code;
    }

    private int verifyAddProblem(ProblemePre problemePre, ProblemeLigneOuverte problemeLigneOuverte, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        if (problemePre == null) {
            return -3;
        }
        if (problemeLigneOuverte.getLigne() == null) {
            return -1;
        }
        if (existe(problemeLigneOuverte, problemeLigneOuvertes)) {
            return -2;
        }
        return 1;
    }

    public int verifyAddTabProblemLigneOuverte(List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        return problemeLigneOuvertes.isEmpty() ? -1 : 1;
    }

    private boolean existe(ProblemeLigneOuverte problemeLigneOuverte, List<ProblemeLigneOuverte> problemeLigneOuvertes) {
        return problemeLigneOuvertes.stream().anyMatch((p) -> (problemeLigneOuverte.getLigne().equals(p.getLigne()) && problemeLigneOuverte.getLigne().equals(p.getLigne())));
    }

    public List<ProblemeLigneOuverte> findByProblemePre(ProblemePre problemePre) {
        return em.createQuery("select p from ProblemeLigneOuverte p where p.problemePre.nump=" + problemePre.getNump()).getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProblemeLigneOuverteFacade() {
        super(ProblemeLigneOuverte.class);
    }

}
