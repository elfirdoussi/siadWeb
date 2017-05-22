/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.CarnetCommande;
import entities.CarnetCommandeOf;
import entities.DoodleGamme;
import entities.Engrais;
import entities.TypeLigne;
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
public class DoodleGammeFacade extends AbstractFacade<DoodleGamme> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private service.EngraisFacade engraisFacade;
    @EJB
    private service.TypeLigneFacade typeLigneFacade;

    public DoodleGamme findByEngraisAndTypeLigne(Engrais engrais, TypeLigne typeLigne) {
        DoodleGamme doodleGamme = null;
        try {
            doodleGamme = (DoodleGamme) getEntityManager().createQuery("select d from DoodleGamme d where d.typeLigne.code=" + typeLigne.getCode() + " and d.engrais.numero=" + engrais.getNumero()).getSingleResult();

        } catch (NoResultException e) {

        }
        return doodleGamme;
    }

    public List<DoodleGamme> findByCommandeOfAndTypeLigne(CarnetCommande carnetCommande, CarnetCommandeOf carnetCommandeOf, TypeLigne typeLigne) {
        return getEntityManager().createQuery("select d from DoodleGamme d where d.engrais.numero in (select c.engrais.numero from CarnetCommandeOf c, Engrais en  where c.engrais.numero = en.numero and c.carnetCommande.id=" + carnetCommande.getId() + " and c.numeroOf =" + carnetCommandeOf.getNumeroOf() + " ) and d.typeLigne.code=" + typeLigne.getCode()).getResultList();
    }

    public HashMap<TypeLigne, List<DoodleGamme>> findAllOrderByEngraisTypeLigne() {
        HashMap<TypeLigne, List<DoodleGamme>> map = new HashMap<>();
        List<TypeLigne> typeLignes = typeLigneFacade.findAllOrderByCode();
        int nbre = engraisFacade.count();
        List<DoodleGamme> doodleGammes = em.createQuery("select d from DoodleGamme d order by d.typeLigne.code, d.engrais.numero").getResultList();
        for (int i = 0; i < typeLignes.size(); i++) {
            map.put(typeLignes.get(i), doodleGammes.subList(i * nbre, (i * nbre) + nbre));
        }
        return map;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DoodleGammeFacade() {
        super(DoodleGamme.class);
    }

}
