/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Engrais;
import entities.Input;
import entities.Nomenclature;
import entities.NomenclatureItem;
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
public class NomenclatureItemFacade extends AbstractFacade<NomenclatureItem> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    @EJB
    private InputFacade inputFacade;
    @EJB
    private NomenclatureFacade nomenclatureFacade;
    @EJB
    private NomenclatureItemFacade nomenclatureItemFacade;
    @EJB
    private EngraisFacade engraisFacade;
    @EJB
    private TypeLigneFacade typeLigneFacade;

    public void setup() {

        List<Engrais> engraiss = engraisFacade.findAll();
        List<TypeLigne> typeLignes = typeLigneFacade.findAll();
        List<Input> inputs = inputFacade.findAll();
        engraiss.forEach((e) -> {
            for (TypeLigne typeLigne : typeLignes) {
                Nomenclature nomenclature = new Nomenclature();
                nomenclature.setEngrais(e);
                nomenclature.setTypeLigne(typeLigne);
                for (Input input : inputs) {
                    NomenclatureItem nomenclatureItem = new NomenclatureItem();
                    nomenclatureItem.setInput(input);
                    nomenclatureItem.setNomenclature(nomenclature);
                    nomenclatureItem.setQtteInput(Float.parseFloat("0"));
                    nomenclatureItemFacade.create(nomenclatureItem);
                }
                nomenclatureFacade.create(nomenclature);
            }
        });

       

    }

    public void saveList(Nomenclature nomenclature) {
        nomenclature.getNomenclatureItems().forEach((n) -> {
            edit(n);
        });
    }

    public void prepareCreateNomenclatureItems(Nomenclature nomenclature) {
        inputFacade.findAll().forEach((i) -> {
            NomenclatureItem nomenclatureItem = new NomenclatureItem();
            nomenclatureItem.setNomenclature(nomenclature);
            nomenclatureItem.setInput(i);
            nomenclature.getNomenclatureItems().add(nomenclatureItem);
        });
    }

    public NomenclatureItem findByEngraisAndTypeLigneAndInput(Engrais engrais, TypeLigne typeLigne, Input input) {
        if (engrais == null || typeLigne == null || input == null) {
            return null;
        }
        NomenclatureItem nomenclatureItem = null;
        try {
            System.out.println("haaaaaaaa engrais " + engrais.getNumero() + "  typeLigne " + typeLigne.getCode() + " input " + input.getNumInput());
            nomenclatureItem = (NomenclatureItem) getEntityManager().createQuery("select n from NomenclatureItem n where n.input.numInput=" + input.getNumInput() + " and n.nomenclature.engrais.numero=" + engrais.getNumero() + " and n.nomenclature.typeLigne.code=" + typeLigne.getCode()).getSingleResult();
        } catch (NoResultException e) {

        }
        return nomenclatureItem;
    }
    
    public List<NomenclatureItem> findByEngraisAndTypeLigne(Engrais engrais, TypeLigne typeLigne){
        return em.createQuery("select n from NomenclatureItem n where n.nomenclature.engrais.numero="+engrais.getNumero()+" and n.nomenclature.typeLigne.code="+typeLigne.getCode()).getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NomenclatureItemFacade() {
        super(NomenclatureItem.class);
    }

}
