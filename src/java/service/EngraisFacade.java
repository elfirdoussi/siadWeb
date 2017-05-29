/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entities.Engrais;
import entities.TypeLigne;
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
public class EngraisFacade extends AbstractFacade<Engrais> {

    @PersistenceContext(unitName = "siadWebPU")
    private EntityManager em;

    public List<Engrais> findByTypeLigne(TypeLigne typeLigne) {
        if (typeLigne != null) {
            return getEntityManager().createQuery("select e from Engrais e where e.numero in (select d.engrais.numero from DoodleGamme d where d.typeLigne.code=" + typeLigne.getCode() + " and d.valeur=true)").getResultList();
        }
        return new ArrayList<>();
    }

    public void deleteEngrais(Engrais engrais) {
        System.out.println("delete !!");
    }

    public int addEngrais(Engrais engrais) {
        int code = verifyEngraisCreation(engrais);
        if (code > 0) {
            create(engrais);
        }
        return code;
    }

    private int verifyEngraisCreation(Engrais engrais) {
        if (engrais.getNumero() == null || engrais.getNumero() == 0) {
            return -1;
        }
        if (engrais.getLibelle() == null || engrais.getLibelle().equals("")) {
            return -2;
        }
        if (engraisExist(engrais.getNumero())) {
            return -3;
        }
        if (findByExactLibelle(engrais.getLibelle()) != null) {
            return -4;
        }
        return 1;
    }

    public int verifyEngraisDelete(Engrais engrais) {
        if (engrais.getNumero() == null || engrais.getNumero() == 0) {
            return -1;
        }
        if (engrais.getLibelle() == null || engrais.getLibelle().equals("")) {
            return -2;
        }
        return 1;
    }

    private boolean engraisExist(Long numero) {
        Engrais engrais = find(numero);
        return engrais != null;
    }

    public Engrais findByExactLibelle(String libelle) {
        Engrais engrais = null;
        try {
            engrais = (Engrais) em.createQuery("select e from Engrais e where e.libelle='" + libelle + "'").getSingleResult();
        } catch (NoResultException e) {

        }
        return engrais;
    }

    public List<Engrais> findByCriteres(Long numero, String libelle) {
        String query = "select e from Engrais e where 1=1 ";
        if (numero != null && numero != 0) {
            query += " and e.numero=" + numero;
        }
        if (libelle != null && !libelle.equals("")) {
            query += " and e.libelle like '%" + libelle + "%'";
        }
        return getEntityManager().createQuery(query).getResultList();
    }
    
    public List<Engrais> findAllOrderByNumero(){
        return em.createQuery("select e from Engrais e order by e.numero").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EngraisFacade() {
        super(Engrais.class);
    }

}
