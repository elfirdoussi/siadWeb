/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Engrais;
import entities.Lancement;
import entities.TypeLigne;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author AIMAD
 */
@Stateless
public class LancementFacade extends AbstractFacade<Lancement> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private TypeLigneFacade typeLigneFacade;
    @EJB
    private EngraisFacade engraisFacade;

    public HashMap<Engrais, List<Lancement>> findAllLancementByTypeLigne(TypeLigne typeLigne) {
        HashMap<Engrais, List<Lancement>> map = new HashMap<>();
        List<Engrais> engraises = engraisFacade.findAllOrderByNumero();
        List<Lancement> lancements = findByTypeLigneOrderByEngraisp(typeLigne);
        for (int i = 0; i < engraises.size(); i++) {
            map.put(engraises.get(i), lancements.subList(i * engraises.size(), (engraises.size() * (i + 1))));
            System.out.println("min " + (i * engraises.size()) + " ila " + ((engraises.size() * (i + 1))));
        }
        return map;
    }

    public List<Lancement> findByTypeLigneOrderByEngraisp(TypeLigne typeLigne) {
        return getEntityManager().createQuery("select l from Lancement l where l.typeLigne.code=" + typeLigne.getCode() + " order by l.engraisp.numero, l.engraisc.numero ").getResultList();
    }

    public List<Lancement> findByCritere(TypeLigne typeLigne, Engrais engraisp, Engrais engraisc) {
        String query = "select l from Lancement l where 1=1 ";
        if (typeLigne != null) {
            query += " and l.typeLigne.code=" + typeLigne.getCode();
        }
        if (engraisp != null) {
            query += " and l.engraisp.numero=" + engraisp.getNumero();
        }
        if (engraisc != null) {
            query += " and l.engraisc.numero=" + engraisc.getNumero();
        }
        return getEntityManager().createQuery(query).getResultList();
    }

    public void setupLancements() {
        List<Engrais> engraises = engraisFacade.findAll();
        engraises.forEach((e) -> {
            prepareEngraisC(typeLigneFacade.find(7l), e, engraisFacade.findAll());
            prepareEngraisC(typeLigneFacade.find(107l), e, engraisFacade.findAll());
        });

    }

    private void prepareEngraisC(TypeLigne typeLigne, Engrais engraisp, List<Engrais> engraises) {
        engraises.forEach((e) -> {
            if (findByCritere(typeLigne, engraisp, e).isEmpty()) {
                Lancement lancement = new Lancement();
                lancement.setEngraisp(engraisp);
                lancement.setEngraisc(e);
                lancement.setTypeLigne(typeLigne);
                create(lancement);
            }

        });
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancementFacade() {
        super(Lancement.class);
    }

}
