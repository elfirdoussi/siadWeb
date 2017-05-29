/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.CarnetCommande;
import entities.CarnetCommandeOf;
import entities.Engrais;
import entities.ProblemePre;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class CarnetCommandeOfFacade extends AbstractFacade<CarnetCommandeOf> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public void deleteAll(CarnetCommande carnetCommande) {
        List<CarnetCommandeOf> carnetCommandeOfs = findByCarnetCommande(carnetCommande);
        carnetCommandeOfs.forEach((c) -> {
            remove(c);
        });
    }

    public void createCarnetCommandeOfs(CarnetCommande carnetCommande, List<CarnetCommandeOf> carnetCommandeOfs) {
        carnetCommandeOfs.forEach((c) -> {
            c.setCarnetCommande(carnetCommande);
            c.setId(null);
            create(c);
        });
    }

    public List<Engrais> findEngraisUtilise(CarnetCommande carnetCommande) {
        return getEntityManager().createQuery("select DISTINCT(e) from Engrais e where e.numero in (select c.engrais.numero from CarnetCommandeOf c where c.carnetCommande.id=" + carnetCommande.getId() + ")").getResultList();
    }

    public CarnetCommandeOf findByCarnetCommandeAndNumOf(CarnetCommande carnetCommande, Long numeroOf) {
        CarnetCommandeOf carnetCommandeOf = null;
        try {
            carnetCommandeOf = (CarnetCommandeOf) getEntityManager().createQuery("select c from CarnetCommandeOf c where c.numeroOf=" + numeroOf + " and c.carnetCommande.id=" + carnetCommande.getId()).getSingleResult();
        } catch (NoResultException e) {
        }
        return carnetCommandeOf;
    }

    public int countEngraisUtilise(CarnetCommande carnetCommande) {
        Long count = 0l;
        try {
            count = (Long) getEntityManager().createQuery("select count(DISTINCT (c.engrais.numero)) from CarnetCommandeOf c where c.carnetCommande.id=" + carnetCommande.getId()).getSingleResult();
        } catch (Exception e) {
        }
        return count.intValue();
    }

    public List<CarnetCommandeOf> findCarnetEnCours(CarnetCommande carnetCommande) {
        if (carnetCommande != null) {
            return getEntityManager().createQuery("select c from CarnetCommandeOf c where c.encours = true and c.carnetCommande.id=" + carnetCommande.getId() + " order by c.numeroOf").getResultList();
        }
        return new ArrayList<>();
    }

    public List<CarnetCommandeOf> findByAndOrderByCarnetCommande(CarnetCommande carnetCommande) {
        if (carnetCommande != null) {
            return getEntityManager().createQuery("select c from CarnetCommandeOf c where c.carnetCommande.id=" + carnetCommande.getId() + " order by c.numeroOf").getResultList();
        }
        return new ArrayList<>();
    }

    public List<CarnetCommandeOf> findByCarnetCommande(CarnetCommande carnetCommande) {
        return em.createQuery("select c from CarnetCommandeOf c where c.carnetCommande.id=" + carnetCommande.getId()).getResultList();
    }

    public List<CarnetCommandeOf> findByNump(Long nump) {
        return em.createQuery("select c from CarnetCommandeOf c, ProblemePre p where c.carnetCommande.id=p.carnetCommande.id and p.nump=" + nump).getResultList();
    }

    public CarnetCommandeOf findMaxDateLiveTardByNump(Long nump) {
        List<CarnetCommandeOf> carnetCommandeOfs = em.createQuery("SELECT c FROM CarnetCommandeOf c where c.dateLiveTard = (select max(ca.dateLiveTard) from CarnetCommandeOf ca, ProblemePre p where ca.carnetCommande.id = p.carnetCommande.id and p.nump=" + nump + ")").getResultList();
        if (!carnetCommandeOfs.isEmpty() && carnetCommandeOfs.get(0) != null) {
            return carnetCommandeOfs.get(0);
        }
        return null;
    }

    public CarnetCommandeOf findByProblemePreAndNumOf(ProblemePre problemePre, Long numOf) {
        try{
            return (CarnetCommandeOf) em.createQuery("select c from CarnetCommandeOf c where c.carnetCommande.id="+problemePre.getCarnetCommande().getId()+" and c.numeroOf="+numOf).getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }
    public List<CarnetCommandeOf> findByProblemePreDateLiveTarDesc(ProblemePre problemePre) {
        return em.createQuery("select c from CarnetCommandeOf c, ProblemePre p where c.carnetCommande.id=p.carnetCommande.id and p.nump=" + problemePre.getNump() + " order by c.dateLiveTard desc").getResultList();
    }

    public List<CarnetCommandeOf> findByProblemePreDateLiveTotAsc(ProblemePre problemePre) {
        return em.createQuery("select c from CarnetCommandeOf c, ProblemePre p where c.carnetCommande.id=p.carnetCommande.id and p.nump=" + problemePre.getNump() + " order by c.dateLiveTot asc").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CarnetCommandeOfFacade() {
        super(CarnetCommandeOf.class);
    }

}
