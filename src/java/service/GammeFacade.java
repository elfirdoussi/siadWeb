/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Engrais;
import entities.Gamme;
import entities.TypeLigne;
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
public class GammeFacade extends AbstractFacade<Gamme> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private TypeLigneFacade typeLigneFacade;
    @EJB
    private EngraisFacade engraisFacade;

    public Gamme findByEngraisAndTypeLigne(Engrais engrais, TypeLigne typeLigne) {
        Gamme gamme = null;
        try {
            gamme = (Gamme) getEntityManager().createQuery("select g from Gamme g where g.typeLigne.code=" + typeLigne.getCode() + " and g.engrais.numero=" + engrais.getNumero()).getSingleResult();

        } catch (NoResultException e) {

        }
        return gamme;
    }

    public Gamme findByLigneAndEngrais(Engrais engrais, TypeLigne typeLigne) {
        Gamme gamme = null;
        try {
            gamme = (Gamme) em.createQuery("select g from Gamme g where g.engrais.numero=" + engrais.getNumero() + " and g.typeLigne.code=" + typeLigne.getCode()).getSingleResult();
        } catch (NoResultException e) {

        }
        return gamme;
    }

    public void correctionTable() {
        List<TypeLigne> typrLignes = typeLigneFacade.findAll();
        List<Engrais> engraises = engraisFacade.findAll();

        engraises.forEach((e) -> {
            for (TypeLigne typrLigne : typrLignes) {
                Gamme gamme = new Gamme();
                gamme.setEngrais(e);
                create(gamme);
                gamme.setTypeLigne(typrLigne);

            }
        });
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GammeFacade() {
        super(Gamme.class);
    }

}
